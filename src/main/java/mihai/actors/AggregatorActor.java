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
    Map<String, TradeOutput<CcpTrade>> unmatchedCcpTrades = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TradesMatchRequest) {
            TradesMatchResponse tradesMatchResponse = new TradesMatchResponse(matchedTradesMap, matchedCcpTradesMap, unmatchedTradesMap, unmatchedCcpTrades);
            getSender().tell(tradesMatchResponse, getSelf());
        } else if (message instanceof TradesListsRequest) {
            List<Trade> tradeList = matchedTradesMap.values().stream().map(tradeOutput -> tradeOutput.getTrade()).collect(Collectors.toList());
            List<CcpTrade> ccpTradeList = matchedCcpTradesMap.values().stream().map(tradeOutput -> tradeOutput.getTrade()).collect(Collectors.toList());
            TradesListsResponse tradesListsResponse = new TradesListsResponse(tradeList, ccpTradeList);
            getSender().tell(tradesListsResponse, getSelf());
        } else if (message instanceof AggregatorMessage) {
            processAggregatorMessage((AggregatorMessage) message);
        } else {
            unhandled(message);
        }
    }

    private void processAggregatorMessage(AggregatorMessage aggregatorMessage) {
        for (AggregatorMessage.Operation operation : aggregatorMessage.getTradesOperations()) {
            Trade trade = (Trade) operation.getTrade();

            if (TradeState.MATCH.equals(operation.getTradeState())) {
                if (AggregatorMessage.OperationType.ADD.equals(operation.getOperationType())) {
                    matchedTradesMap.put(trade.getExchangeReference(), new TradeOutput<>(trade, operation.getTradeComment().getComment()));
                } else {
                    matchedTradesMap.remove(trade.getExchangeReference());
                }
            } else {
                if (AggregatorMessage.OperationType.ADD.equals(operation.getOperationType())) {
                    unmatchedTradesMap.put(trade.getExchangeReference(), new TradeOutput<>(trade, operation.getTradeComment().getComment()));
                } else {
                    unmatchedTradesMap.remove(trade.getExchangeReference());
                }
            }
        }

        for (AggregatorMessage.Operation operation : aggregatorMessage.getCcpTradesOperations()) {
            CcpTrade ccpTrade = (CcpTrade) operation.getTrade();

            if (TradeState.MATCH.equals(operation.getTradeState())) {
                if (AggregatorMessage.OperationType.ADD.equals(operation.getOperationType())) {
                    matchedCcpTradesMap.put(ccpTrade.getExchangeReference(), new TradeOutput<>(ccpTrade, operation.getTradeComment().getComment()));
                } else {
                    matchedCcpTradesMap.remove(ccpTrade.getExchangeReference());
                }
            } else {
                if (AggregatorMessage.OperationType.ADD.equals(operation.getOperationType())) {
                    unmatchedCcpTrades.put(ccpTrade.getExchangeReference(), new TradeOutput<>(ccpTrade, operation.getTradeComment().getComment()));
                } else {
                    unmatchedCcpTrades.remove(ccpTrade.getExchangeReference());
                }
            }
        }
    }
}
