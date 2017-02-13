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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class TradeWorkerActor extends UntypedActor {
    ActorRef aggregatorActor;
    Map<String, Trade> tradeMap = new HashMap<>();
    Map<String, CcpTrade> ccpTradeMap = new HashMap<>();
    private enum MessageType {
        NEW_MATCHED_TRADE,
        NEW_UNMATCHED_TRADE,
        CANCEL_MATCHED_TRADE,
        CANCEL_UNMATCHED_TRADE
    }

    public TradeWorkerActor(ActorRef aggregatorActor) {
        this.aggregatorActor = aggregatorActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        AggregatorMessage aggregatorMessage = null;

        if (message instanceof NewTradeMessage) {
            aggregatorMessage = performNewTrade((NewTradeMessage) message);
        } else if (message instanceof NewCcpTradeMessage) {
            aggregatorMessage = performNewCcpTrade((NewCcpTradeMessage) message);
        } else if (message instanceof CancelTradeMessage) {
            aggregatorMessage = performCancelTrade((CancelTradeMessage) message);
        } else if (message instanceof CancelCcpTradeMessage) {
            aggregatorMessage = performCancelCcpTrade((CancelCcpTradeMessage) message);
        } else {
            unhandled(message);
        }

        if (aggregatorMessage != null) {
            aggregatorActor.tell(aggregatorMessage, getSelf());
        }
    }

    private AggregatorMessage performCancelCcpTrade(CancelCcpTradeMessage cancelCcpTradeMessage) {
        AggregatorMessage aggregatorMessage = new AggregatorMessage();

        CcpTrade ccpTrade = cancelCcpTradeMessage.getCcpTrade();
        ccpTradeMap.remove(ccpTrade.getExchangeReference());

        Trade trade = tradeMap.get(ccpTrade.getExchangeReference());

        if (trade == null) {
            updateAggregatorOperations(MessageType.CANCEL_UNMATCHED_TRADE, aggregatorMessage, ccpTrade, null, null);
        } else {
            updateAggregatorOperations(MessageType.CANCEL_MATCHED_TRADE, aggregatorMessage, ccpTrade, trade, TradeComment.TRADE_UNMATCH);
        }

        return aggregatorMessage;
    }

    private AggregatorMessage performCancelTrade(CancelTradeMessage cancelTradeMessage) {
        AggregatorMessage aggregatorMessage = new AggregatorMessage();

        Trade trade = cancelTradeMessage.getTrade();
        tradeMap.remove(trade.getExchangeReference());

        CcpTrade ccpTrade = ccpTradeMap.get(trade.getExchangeReference());

        if (ccpTrade == null) {
            updateAggregatorOperations(MessageType.CANCEL_UNMATCHED_TRADE, aggregatorMessage, trade, null, null);
        } else {
            updateAggregatorOperations(MessageType.CANCEL_MATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.CCP_TRADE_UNMATCH);
        }

        return aggregatorMessage;
    }

    private AggregatorMessage performNewCcpTrade(NewCcpTradeMessage newCcpTradeMessage) {
        AggregatorMessage aggregatorMessage = new AggregatorMessage();

        CcpTrade ccpTrade = newCcpTradeMessage.getCcpTrade();
        ccpTradeMap.put(ccpTrade.getExchangeReference(), ccpTrade);

        Trade trade = tradeMap.get(ccpTrade.getExchangeReference());

        if (trade == null) {
            // unmatch -> missing trade
            updateAggregatorOperations(MessageType.NEW_UNMATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.TRADE_UNMATCH);
        } else if (TradeUtils.isFullMatch(trade, ccpTrade)) {
            // match -> full match
            updateAggregatorOperations(MessageType.NEW_MATCHED_TRADE, aggregatorMessage, ccpTrade, trade, TradeComment.FULL_MATCH);
        } else if (TradeUtils.isMatchWithinToleranceForAmount(trade, ccpTrade)) {
            // match -> within tolerance for amount
            updateAggregatorOperations(MessageType.NEW_MATCHED_TRADE, aggregatorMessage, ccpTrade, trade, TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT);
        } else if (TradeUtils.isUnmatchOutsideOfToleranceForAmount(trade, ccpTrade)) {
            // unmatch -> outside tolerance for amount
            updateAggregatorOperations(MessageType.NEW_UNMATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT);
        } else {
            // unmatch -> economics mismatch
            updateAggregatorOperations(MessageType.NEW_UNMATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.UNMATCH_ECONOMICS_MISMATCH);
        }

        return aggregatorMessage;
    }

    private AggregatorMessage performNewTrade(NewTradeMessage newTradeMessage) {
        AggregatorMessage aggregatorMessage = new AggregatorMessage();

        Trade trade = newTradeMessage.getTrade();
        tradeMap.put(trade.getExchangeReference(), trade);

        CcpTrade ccpTrade = ccpTradeMap.get(trade.getExchangeReference());

        if (ccpTrade == null) {
            // unmatch -> missing CCP
            updateAggregatorOperations(MessageType.NEW_UNMATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.CCP_TRADE_UNMATCH);
        } else if (TradeUtils.isFullMatch(trade, ccpTrade)) {
            // match -> full match
            updateAggregatorOperations(MessageType.NEW_MATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.FULL_MATCH);
        } else if (TradeUtils.isMatchWithinToleranceForAmount(trade, ccpTrade)) {
            // match -> within tolerance for amount
            updateAggregatorOperations(MessageType.NEW_MATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT);
        } else if (TradeUtils.isUnmatchOutsideOfToleranceForAmount(trade, ccpTrade)) {
            // unmatch -> outside tolerance for amount
            updateAggregatorOperations(MessageType.NEW_UNMATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT);
        } else {
            // unmatch -> economics mismatch
            updateAggregatorOperations(MessageType.NEW_UNMATCHED_TRADE, aggregatorMessage, trade, ccpTrade, TradeComment.UNMATCH_ECONOMICS_MISMATCH);
        }

        return aggregatorMessage;
    }

    private <T, V> void updateAggregatorOperations(MessageType messageType, AggregatorMessage aggregatorMessage, T trade1, V trade2, TradeComment tradeComment) {
        switch (messageType) {

            case NEW_MATCHED_TRADE:
                aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                        .withTrade(trade1)
                        .withTradeState(TradeState.MATCH)
                        .withOperationType(AggregatorMessage.OperationType.ADD)
                        .withTradeComment(tradeComment)
                        .build());
                aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                        .withTrade(trade2)
                        .withTradeState(TradeState.MISMATCH)
                        .withOperationType(AggregatorMessage.OperationType.REMOVE)
                        .build());
                aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                        .withTrade(trade2)
                        .withTradeState(TradeState.MATCH)
                        .withOperationType(AggregatorMessage.OperationType.ADD)
                        .withTradeComment(tradeComment)
                        .build());
                break;

            case NEW_UNMATCHED_TRADE:
                if (trade1 != null) {
                    aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                            .withTrade(trade1)
                            .withTradeState(TradeState.MISMATCH)
                            .withOperationType(AggregatorMessage.OperationType.ADD)
                            .withTradeComment(tradeComment)
                            .build());
                }
                if (trade2 != null) {
                    aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                            .withTrade(trade2)
                            .withTradeState(TradeState.MISMATCH)
                            .withOperationType(AggregatorMessage.OperationType.ADD)
                            .withTradeComment(tradeComment)
                            .build());
                }
                break;

            case CANCEL_MATCHED_TRADE:
                aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                        .withTrade(trade1)
                        .withTradeState(TradeState.MATCH)
                        .withOperationType(AggregatorMessage.OperationType.REMOVE)
                        .build());
                aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                        .withTrade(trade2)
                        .withTradeState(TradeState.MATCH)
                        .withOperationType(AggregatorMessage.OperationType.REMOVE)
                        .build());
                aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                        .withTrade(trade2)
                        .withTradeState(TradeState.MISMATCH)
                        .withOperationType(AggregatorMessage.OperationType.ADD)
                        .withTradeComment(tradeComment)
                        .build());
                break;

            case CANCEL_UNMATCHED_TRADE:
                aggregatorMessage.addTradesOperation(new AggregatorMessage.Operation.Builder()
                        .withTrade(trade1)
                        .withTradeState(TradeState.MISMATCH)
                        .withOperationType(AggregatorMessage.OperationType.REMOVE)
                        .build());
                break;
        }
    }
}