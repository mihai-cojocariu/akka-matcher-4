package mihai.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.messages.AggregatorMessage;
import mihai.messages.CancelCcpTradeMessage;
import mihai.messages.CancelTradeMessage;
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;
import mihai.utils.TradeComment;
import mihai.utils.TradeState;
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
        } else if (message instanceof CancelTradeMessage) {
            performCancelTrade((CancelTradeMessage) message);
        } else if (message instanceof CancelCcpTradeMessage) {
            performCancelCcpTrade((CancelCcpTradeMessage) message);
        } else {
            unhandled(message);
        }

        AggregatorMessage aggregatorMessage = new AggregatorMessage(tradesOperations, ccpTradesOperations);
        aggregatorActor.tell(aggregatorMessage, getSelf());
    }

    private void performCancelCcpTrade(CancelCcpTradeMessage cancelCcpTradeMessage) {
        CcpTrade ccpTrade = cancelCcpTradeMessage.getCcpTrade();
        ccpTradeMap.remove(ccpTrade.getExchangeReference());

        Trade trade = tradeMap.get(ccpTrade.getExchangeReference());

        if (trade == null) {
            updateAggregatorOperations(CancelCcpTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, null);
        } else {
            updateAggregatorOperations(CancelCcpTradeMessage.class, trade, ccpTrade, TradeState.MATCH, TradeComment.TRADE_UNMATCH);
        }
    }

    private void performCancelTrade(CancelTradeMessage cancelTradeMessage) {
        Trade trade = cancelTradeMessage.getTrade();
        tradeMap.remove(trade.getExchangeReference());

        CcpTrade ccpTrade = ccpTradeMap.get(trade.getExchangeReference());

        if (ccpTrade == null) {
            updateAggregatorOperations(CancelTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, null);
        } else {
            updateAggregatorOperations(CancelTradeMessage.class, trade, ccpTrade, TradeState.MATCH, TradeComment.CCP_TRADE_UNMATCH);
        }
    }

    private void performNewCcpTrade(NewCcpTradeMessage newCcpTradeMessage) {
        CcpTrade ccpTrade = newCcpTradeMessage.getCcpTrade();
        ccpTradeMap.put(ccpTrade.getExchangeReference(), ccpTrade);

        Trade trade = tradeMap.get(ccpTrade.getExchangeReference());

        if (trade == null) {
            // unmatch -> missing trade
            updateAggregatorOperations(NewCcpTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, TradeComment.TRADE_UNMATCH);
        } else if (TradeUtils.isFullMatch(trade, ccpTrade)) {
            // match -> full match
            updateAggregatorOperations(NewCcpTradeMessage.class, trade, ccpTrade, TradeState.MATCH, TradeComment.FULL_MATCH);
        } else if (TradeUtils.isMatchWithinToleranceForAmount(trade, ccpTrade)) {
            // match -> within tolerance for amount
            updateAggregatorOperations(NewCcpTradeMessage.class, trade, ccpTrade, TradeState.MATCH, TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT);
        } else if (TradeUtils.isUnmatchOutsideOfToleranceForAmount(trade, ccpTrade)) {
            // unmatch -> outside tolerance for amount
            updateAggregatorOperations(NewCcpTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT);
        } else {
            // unmatch -> economics mismatch
            updateAggregatorOperations(NewCcpTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_ECONOMICS_MISMATCH);
        }
    }

    private void performNewTrade(NewTradeMessage newTradeMessage) {
        Trade trade = newTradeMessage.getTrade();
        tradeMap.put(trade.getExchangeReference(), trade);

        CcpTrade ccpTrade = ccpTradeMap.get(trade.getExchangeReference());

        if (ccpTrade == null) {
            // unmatch -> missing CCP
            updateAggregatorOperations(NewTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, TradeComment.CCP_TRADE_UNMATCH);
        } else if (TradeUtils.isFullMatch(trade, ccpTrade)) {
            // match -> full match
            updateAggregatorOperations(NewTradeMessage.class, trade, ccpTrade, TradeState.MATCH, TradeComment.FULL_MATCH);
        } else if (TradeUtils.isMatchWithinToleranceForAmount(trade, ccpTrade)) {
            // match -> within tolerance for amount
            updateAggregatorOperations(NewTradeMessage.class, trade, ccpTrade, TradeState.MATCH, TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT);
        } else if (TradeUtils.isUnmatchOutsideOfToleranceForAmount(trade, ccpTrade)) {
            // unmatch -> outside tolerance for amount
            updateAggregatorOperations(NewTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT);
        } else {
            // unmatch -> economics mismatch
            updateAggregatorOperations(NewTradeMessage.class, trade, ccpTrade, TradeState.MISMATCH, TradeComment.UNMATCH_ECONOMICS_MISMATCH);
        }
    }

    private void updateAggregatorOperations(Class messageClass, Trade trade, CcpTrade ccpTrade, TradeState tradeState, TradeComment tradeComment) {
        if (NewTradeMessage.class.equals(messageClass) || NewCcpTradeMessage.class.equals(messageClass)) {
            // new trades / CCP trades
            if (TradeState.MATCH.equals(tradeState)) {
                // trades match
                if (NewTradeMessage.class.equals(messageClass)) {
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
        } else {
            // cancelling trades / CCP trades
            if (TradeState.MATCH.equals(tradeState)) {
                // cancel trades / CCP trades which have a matching CCP trade / trade
                if (CancelTradeMessage.class.equals(messageClass)) {
                    tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MATCH, AggregatorMessage.OperationType.REMOVE, null));
                    ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MATCH, AggregatorMessage.OperationType.REMOVE, null));
                    ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MISMATCH, AggregatorMessage.OperationType.ADD, tradeComment));
                } else {
                    ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MATCH, AggregatorMessage.OperationType.REMOVE, null));
                    tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MATCH, AggregatorMessage.OperationType.REMOVE, null));
                    tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MISMATCH, AggregatorMessage.OperationType.ADD, tradeComment));
                }
            } else {
                // cancel trades / CCP trades without a matching CCP trade / trade
                if (CancelTradeMessage.class.equals(messageClass)) {
                    tradesOperations.add(new AggregatorMessage.Operation<>(trade, TradeState.MISMATCH, AggregatorMessage.OperationType.REMOVE, null));
                } else {
                    ccpTradesOperations.add(new AggregatorMessage.Operation<>(ccpTrade, TradeState.MISMATCH, AggregatorMessage.OperationType.REMOVE, null));
                }
            }

        }

    }
}