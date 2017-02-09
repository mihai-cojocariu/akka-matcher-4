package mihai.messages;

import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.utils.TradeComment;
import mihai.utils.TradeState;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class AggregatorMessage implements Serializable {
    List<Operation<Trade>> tradesOperations;
    List<Operation<CcpTrade>> ccpTradesOperations;

    public AggregatorMessage(List<Operation<Trade>> tradesOperations, List<Operation<CcpTrade>> ccpTradesOperations) {
        this.tradesOperations = tradesOperations;
        this.ccpTradesOperations = ccpTradesOperations;
    }

    public List<Operation<Trade>> getTradesOperations() {
        return tradesOperations;
    }


    public List<Operation<CcpTrade>> getCcpTradesOperations() {
        return ccpTradesOperations;
    }

    public enum OperationType implements Serializable {
        ADD,
        REMOVE
    }

    public static class Operation<T> implements Serializable {
        T trade;
        TradeState tradeState;
        OperationType operationType;
        TradeComment tradeComment;

        public Operation(T trade, TradeState tradeState, OperationType operationType, TradeComment tradeComment) {
            this.trade = trade;
            this.tradeState = tradeState;
            this.operationType = operationType;
            this.tradeComment = tradeComment;
        }

        public T getTrade() {
            return trade;
        }

        public TradeState getTradeState() {
            return tradeState;
        }

        public OperationType getOperationType() {
            return operationType;
        }

        public TradeComment getTradeComment() {
            return tradeComment;
        }
    }
}
