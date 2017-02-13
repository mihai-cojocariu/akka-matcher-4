package mihai.utils;

/**
 * Created by mcojocariu on 2/6/2017.
 */
public enum TradeComment {
    TRADE_UNMATCH("Missing trade"),
    CCP_TRADE_UNMATCH("Missing CCP trade"),
    FULL_MATCH("Full match"),
    MATCH_WITHIN_TOLERANCE_FOR_AMOUNT("Match within tolerance for amount"),
    UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT("Outside of tolerance for amount"),
    UNMATCH_ECONOMICS_MISMATCH("Economics mismatch");

    private String comment;

    TradeComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
