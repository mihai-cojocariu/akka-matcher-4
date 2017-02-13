package mihai.messages;

import mihai.utils.TradeComment;
import mihai.utils.TradeState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class AggregatorMessage<T> implements Serializable {
    private List<Operation<T>> tradesOperations = new ArrayList<>();

    public AggregatorMessage() {
    }


    public List<Operation<T>> getTradesOperations() {
        return tradesOperations;
    }

    public void addTradesOperation(Operation<T> tradesOperation) {
        this.tradesOperations.add(tradesOperation);
    }


    public enum OperationType implements Serializable {
        ADD,
        REMOVE
    }

    public static class Operation<T> implements Serializable {
        private T trade;
        private TradeState tradeState;
        private OperationType operationType;
        private TradeComment tradeComment;

        private Operation(Builder<T> builder) {
            this.trade = builder.trade;
            this.tradeState = builder.tradeState;
            this.operationType = builder.operationType;
            this.tradeComment = builder.tradeComment;
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

        public static class Builder<T> {
            private T trade;
            private TradeState tradeState;
            private OperationType operationType;
            private TradeComment tradeComment;

            public Builder() {
            }

            public Builder withTrade(T trade) {
                this.trade = trade;
                return this;
            }

            public Builder withTradeState(TradeState tradeState) {
                this.tradeState = tradeState;
                return this;
            }

            public Builder withOperationType(OperationType operationType) {
                this.operationType = operationType;
                return this;
            }

            public Builder withTradeComment(TradeComment tradeComment) {
                this.tradeComment = tradeComment;
                return this;
            }

            public Operation build() {
                return new Operation(this);
            }
        }
    }

}
