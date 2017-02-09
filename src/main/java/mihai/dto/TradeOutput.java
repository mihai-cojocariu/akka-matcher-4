package mihai.dto;

import java.io.Serializable;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class TradeOutput<T> implements Serializable {
    T trade;
    String comment;

    public TradeOutput(T trade, String comment) {
        this.trade = trade;
        this.comment = comment;
    }

    public T getTrade() {
        return trade;
    }

    public String getComment() {
        return comment;
    }
}
