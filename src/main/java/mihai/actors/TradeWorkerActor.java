package mihai.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.messages.AggregatorMessage;
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;
import mihai.utils.TradeComment;
import mihai.utils.TradeState;
import mihai.utils.TradeType;
import mihai.utils.TradeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class TradeWorkerActor extends UntypedActor {
    ActorRef aggregatorActor;
    Map<String, Trade> tradeMap = new HashMap<>();
    Map<String, CcpTrade> ccpTradeMap = new HashMap<>();

    List<AggregatorMessage.Operation<Trade>> tradesOperations;
    List<AggregatorMessage.Operation<CcpTrade>> ccpTradesOperations;

    public TradeWorkerActor(ActorRef aggregatorActor) {
        this.aggregatorActor = aggregatorActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        tradesOperations = new ArrayList<>();
        ccpTradesOperations = new ArrayList<>();

        if (message instanceof NewTradeMessage) {
            performNewTrade((NewTradeMessage) message);
        } else if (message instanceof NewCcpTradeMessage) {
            performNewCcpTrade((NewCcpTradeMessage) message);
        } else {
            unhandled(message);
        }

        AggregatorMessage aggregatorMessage = new AggregatorMessage(tradesOperations, ccpTradesOperations);
        aggregatorActor.tell(aggregatorMessage, getSelf());
    }

    private void performNewCcpTrade(NewCcpTradeMessage newCcpTradeMessage) {
        CcpTrade ccpTrade = newCcpTradeMessage.getCcpTrade();
        ccpTradeMap.put(ccpTrade.getExchangeReference(), ccpTrade);

        Trade trade = tradeMap.get(ccpTrade.getExchangeReference());

        if (trade == null) {
            // unmatch -> missing trade
            updateAggregatorOperations(TradeType.CCP_TRADE, trade, ccpTrade, TradeState.MISMATCH, TradeComment.TRADE_UNMATCH);
        } else if (TradeUtils.isFullMatch(trade, ccpTrade)) {
            // match -> full match
            updateAggregatorOperations(TradeType.CCP_TRADE, trade, ccpTrade, TradeState.MATCH, TradeComment.FULL_MATCH);
        } else if (TradeUtils.isMatchWithinToleranceForAmount(trade, ccpTrade)) {
            // match -> within tolerance for amount
            updateAggregatorOperations(TradeType.CCP_TRADE, trade, ccpTrade, TradeState.MATCH, TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT);
        } else if (TradeUtils.isUnmatchOutsideOfToleranceForAmount(trade, ccpTrade)) {
            // unmatch -> outside tolerance for amount
            updateAggregatorOperations(TradeType.CCP_TRADE, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT);
        } else {
            // unmatch -> economics mismatch
            updateAggregatorOperations(TradeType.CCP_TRADE, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_ECONOMICS_MISMATCH);
        }
    }

    private void performNewTrade(NewTradeMessage newTradeMessage) {
        Trade trade = newTradeMessage.getTrade();
        tradeMap.put(trade.getExchangeReference(), trade);

        CcpTrade ccpTrade = ccpTradeMap.get(trade.getExchangeReference());

        if (ccpTrade == null) {
            // unmatch -> missing CCP
            updateAggregatorOperations(TradeType.TRADE, trade, ccpTrade, TradeState.MISMATCH, TradeComment.CCP_TRADE_UNMATCH);
        } else if (TradeUtils.isFullMatch(trade, ccpTrade)) {
            // match -> full match
            updateAggregatorOperations(TradeType.TRADE, trade, ccpTrade, TradeState.MATCH, TradeComment.FULL_MATCH);
        } else if (TradeUtils.isMatchWithinToleranceForAmount(trade, ccpTrade)) {
            // match -> within tolerance for amount
            updateAggregatorOperations(TradeType.TRADE, trade, ccpTrade, TradeState.MATCH, TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT);
        } else if (TradeUtils.isUnmatchOutsideOfToleranceForAmount(trade, ccpTrade)) {
            // unmatch -> outside tolerance for amount
            updateAggregatorOperations(TradeType.TRADE, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT);
        } else {
            // unmatch -> economics mismatch
            updateAggregatorOperations(TradeType.TRADE, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_ECONOMICS_MISMATCH);
        }
    }

    private void updateAggregatorOperations(TradeType tradeType, Trade trade, CcpTrade ccpTrade, TradeState tradeState, TradeComment tradeComment) {
        if (TradeState.MATCH.equals(tradeState)) {
            // trades match
            if (TradeType.TRADE.equals(tradeType)) {
                // new trade added which is matched with an existing CCP trade
                tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MATCH, AggregatorMessage.OperationType.ADD, tradeComment));
                ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MISMATCH, AggregatorMessage.OperationType.REMOVE, null));
                ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MATCH, AggregatorMessage.OperationType.ADD, tradeComment));
            } else {
                // new CCP trade is added which is matched with an existing trade
                ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MATCH, AggregatorMessage.OperationType.ADD, tradeComment));
                tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MISMATCH, AggregatorMessage.OperationType.REMOVE, null));
                tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MATCH, AggregatorMessage.OperationType.ADD, tradeComment));
            }
        } else {
            // trades mismatch
            if (trade != null) {
                tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MISMATCH, AggregatorMessage.OperationType.ADD, tradeComment));
            }
            if (ccpTrade != null) {
                ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MISMATCH, AggregatorMessage.OperationType.ADD, tradeComment));
            }
        }
    }
}