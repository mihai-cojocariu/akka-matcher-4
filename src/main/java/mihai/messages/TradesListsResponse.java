package mihai.messages;

import mihai.dto.CcpTrade;
import mihai.dto.Trade;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class TradesListsResponse implements Serializable {
    List<Trade> tradeList;
    List<CcpTrade> ccpTradeList;

    public TradesListsResponse(List<Trade> tradeList, List<CcpTrade> ccpTradeList) {
        this.tradeList = tradeList;
        this.ccpTradeList = ccpTradeList;
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public List<CcpTrade> getCcpTradeList() {
        return ccpTradeList;
    }
}
