package mihai.messages;

import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.dto.TradeOutput;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class TradesMatchResponse implements Serializable {
    Map<String, TradeOutput<Trade>> matchedTradesMap;
    Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap;
    Map<String, TradeOutput<Trade>> unmatchedTradesMap;
    Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap;

    public TradesMatchResponse(Map<String, TradeOutput<Trade>> matchedTradesMap, Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap, Map<String, TradeOutput<Trade>> unmatchedTradesMap, Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap) {
        this.matchedTradesMap = matchedTradesMap;
        this.matchedCcpTradesMap = matchedCcpTradesMap;
        this.unmatchedTradesMap = unmatchedTradesMap;
        this.unmatchedCcpTradesMap = unmatchedCcpTradesMap;
    }

    public Map<String, TradeOutput<Trade>> getMatchedTradesMap() {
        return matchedTradesMap;
    }

    public Map<String, TradeOutput<CcpTrade>> getMatchedCcpTradesMap() {
        return matchedCcpTradesMap;
    }

    public Map<String, TradeOutput<Trade>> getUnmatchedTradesMap() {
        return unmatchedTradesMap;
    }

    public Map<String, TradeOutput<CcpTrade>> getUnmatchedCcpTradesMap() {
        return unmatchedCcpTradesMap;
    }
}
