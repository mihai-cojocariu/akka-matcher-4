package mihai.utils;

import mihai.dto.CcpTrade;
import mihai.dto.Trade;

import java.util.Objects;

/**
 * Created by mcojocariu on 2/9/2017.
 */
public class TradeUtils {

    public static boolean isFullMatch(Trade trade, CcpTrade ccpTrade) {
        boolean result = false;

        if (Objects.equals(trade.getExchangeReference(), ccpTrade.getExchangeReference())
                && Objects.equals(trade.getTradeDate(), ccpTrade.getTradeDate())
                && Objects.equals(trade.getIsin(), ccpTrade.getIsin())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getQuantity(), ccpTrade.getQuantity())
                && Objects.equals(trade.getCurrency(), ccpTrade.getCurrency())
                && Objects.equals(trade.getAmount(), ccpTrade.getAmount())){
            result = true;
        }

        return result;
    }

    public static boolean isMatchWithinToleranceForAmount(Trade trade, CcpTrade ccpTrade) {
        boolean result = false;

        if (Objects.equals(trade.getExchangeReference(), ccpTrade.getExchangeReference())
                && Objects.equals(trade.getTradeDate(), ccpTrade.getTradeDate())
                && Objects.equals(trade.getIsin(), ccpTrade.getIsin())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getQuantity(), ccpTrade.getQuantity())
                && Objects.equals(trade.getCurrency(), ccpTrade.getCurrency())
                && (Math.abs(trade.getAmount() -  ccpTrade.getAmount()) <= Constants.TRADE_AMOUNT_TOLERANCE)){
            result = true;
        }

        return result;
    }

    public static boolean isUnmatchOutsideOfToleranceForAmount(Trade trade, CcpTrade ccpTrade) {
        boolean result = false;

        if (Objects.equals(trade.getExchangeReference(), ccpTrade.getExchangeReference())
                && Objects.equals(trade.getTradeDate(), ccpTrade.getTradeDate())
                && Objects.equals(trade.getIsin(), ccpTrade.getIsin())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getDirection(), ccpTrade.getDirection())
                && Objects.equals(trade.getQuantity(), ccpTrade.getQuantity())
                && Objects.equals(trade.getCurrency(), ccpTrade.getCurrency())
                && (Math.abs(trade.getAmount() -  ccpTrade.getAmount()) > Constants.TRADE_AMOUNT_TOLERANCE)){
            result = true;
        }

        return result;
    }
}
