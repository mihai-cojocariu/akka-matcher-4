package mihai.actors;

import akka.actor.UntypedActor;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.dto.TradeOutput;
import mihai.messages.AggregatorMessage;
import mihai.messages.TradesListsRequest;
import mihai.messages.TradesListsResponse;
import mihai.messages.TradesMatchRequest;
import mihai.messages.TradesMatchResponse;
import mihai.utils.TradeComment;
import mihai.utils.TradeState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class AggregatorActor extends UntypedActor {
    Map<String, TradeOutput<Trade>> matchedTradesMap = new HashMap<>();
    Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap = new HashMap<>();
    Map<String, TradeOutput<Trade>> unmatchedTradesMap = new HashMap<>();
    Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TradesMatchRequest) {
            TradesMatchResponse tradesMatchResponse = new TradesMatchResponse(matchedTradesMap, matchedCcpTradesMap, unmatchedTradesMap, unmatchedCcpTradesMap);
            getSender().tell(tradesMatchResponse, getSelf());
        } else if (message instanceof TradesListsRequest) {
            List<Trade> tradeList = matchedTradesMap.values().stream().map(tradeOutput -> tradeOutput.getTrade()).collect(Collectors.toList());
            tradeList.addAll(unmatchedTradesMap.values().stream().map(tradeOutput -> tradeOutput.getTrade()).collect(Collectors.toList()));

            List<CcpTrade> ccpTradeList = matchedCcpTradesMap.values().stream().map(tradeOutput -> tradeOutput.getTrade()).collect(Collectors.toList());
            ccpTradeList.addAll(unmatchedCcpTradesMap.values().stream().map(tradeOutput -> tradeOutput.getTrade()).collect(Collectors.toList()));

            TradesListsResponse tradesListsResponse = new TradesListsResponse(tradeList, ccpTradeList);
            getSender().tell(tradesListsResponse, getSelf());
        } else if (message instanceof AggregatorMessage) {
            processAggregatorMessage((AggregatorMessage) message);
        } else {
            unhandled(message);
        }
    }

    private void processAggregatorMessage(AggregatorMessage aggregatorMessage) {
        for (Object operation : aggregatorMessage.getTradesOperations()) {
            Trade trade = null;
            CcpTrade ccpTrade = null;
            TradeComment tradeComment = null;
            TradeState tradeState = null;
            AggregatorMessage.OperationType operationType = null;

            try {
                AggregatorMessage.Operation<Trade> tradeOperation = (AggregatorMessage.Operation<Trade>) operation;
                trade = tradeOperation.getTrade();
                tradeComment = tradeOperation.getTradeComment();
                tradeState = tradeOperation.getTradeState();
                operationType = tradeOperation.getOperationType();
            } catch (ClassCastException ex) {
                AggregatorMessage.Operation<CcpTrade> tradeOperation = (AggregatorMessage.Operation<CcpTrade>) operation;
                ccpTrade = tradeOperation.getTrade();
                tradeComment = tradeOperation.getTradeComment();
                tradeState = tradeOperation.getTradeState();
                operationType = tradeOperation.getOperationType();
            }


            if (TradeState.MATCH.equals(tradeState)) {
                if (AggregatorMessage.OperationType.ADD.equals(operationType)) {
                    if (trade != null) {
                        matchedTradesMap.put(trade.getExchangeReference(), new TradeOutput<>(trade, tradeComment.getComment()));
                    } else {
                        matchedCcpTradesMap.put(ccpTrade.getExchangeReference(), new TradeOutput<>(ccpTrade, tradeComment.getComment()));
                    }
                } else {
                    if (trade != null) {
                        matchedTradesMap.remove(trade.getExchangeReference());
                    } else {
                        matchedCcpTradesMap.remove(ccpTrade.getExchangeReference());
                    }
                }
            } else {
                if (AggregatorMessage.OperationType.ADD.equals(operationType)) {
                    if (trade != null) {
                        unmatchedTradesMap.put(trade.getExchangeReference(), new TradeOutput<>(trade, tradeComment.getComment()));
                    } else {
                        unmatchedCcpTradesMap.put(ccpTrade.getExchangeReference(), new TradeOutput<>(ccpTrade, tradeComment.getComment()));
                    }
                } else {
                    if (trade != null) {
                        unmatchedTradesMap.remove(trade.getExchangeReference());
                    } else {
                        unmatchedCcpTradesMap.remove(ccpTrade.getExchangeReference());
                    }
                }
            }
        }
    }
}
