package mihai;

/**
 * Created by clamba on 1/31/2017.
 */

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import mihai.actors.SupervisorActor;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.dto.TradeOutput;
import mihai.messages.*;
import mihai.utils.Constants;
import mihai.utils.TradeComment;
import mihai.utils.TradeDirection;
import mihai.utils.Utils;
import org.concordion.api.AfterExample;
import org.concordion.api.extension.Extensions;
import org.concordion.api.option.ConcordionOptions;
import org.concordion.ext.timing.TimerExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.logback.LogbackAdaptor;
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(ConcordionRunner.class)
@Extensions(value = TimerExtension.class)
//@Extensions(RunTotalsExtension.class)
@ConcordionOptions(copySourceHtmlToDir="C:/tmp")
//@FailFast
public class ConcordionTradesTest {
    private static ActorSystem system;
    private final static int RESULTS_DELAY_MS = 50;
    private final ReportLogger logger = ReportLoggerFactory.getReportLogger(this.getClass().getName());
    private final Logger tooltipLogger = LoggerFactory.getLogger("TOOLTIP_" + this.getClass().getName());
    private String tradeReference;
    private String exchangeReference;
    private ZonedDateTime tradeDate;
    private String isin;
    private TradeDirection tradeDirection;
    private Integer tradeQuantity;
    private String tradeCurrency;
    private Float tradeAmount;
    private String exchangeReferenceCcp;
    private ZonedDateTime tradeDateCcp;
    private String isinCcp;
    private TradeDirection tradeDirectionCcp;
    private Integer tradeQuantityCcp;
    private String tradeCurrencyCcp;
    private Float tradeAmountCcp;
    private static  int scenarioCounter;


    public ConcordionTradesTest() {
        this.tradeReference = tradeReference;
        this.exchangeReference = exchangeReference;
        this.tradeDate = tradeDate;
        this.isin = isin;
        this.tradeDirection = tradeDirection;
        this.tradeQuantity = tradeQuantity;
        this.tradeCurrency = tradeCurrency;
        this.tradeAmount = tradeAmount;

        this.exchangeReferenceCcp = exchangeReferenceCcp;
        this.tradeDateCcp = tradeDateCcp;
        this.isinCcp = isinCcp;
        this.tradeDirectionCcp = tradeDirectionCcp;
        this.tradeQuantityCcp = tradeQuantityCcp;
        this.tradeCurrencyCcp = tradeCurrencyCcp;
        this.tradeAmountCcp = tradeAmountCcp;
    }
    public String getTradeReference() {
        return tradeReference;
    }

    public void setTradeReference(String tradeReference) {
        this.tradeReference = tradeReference;
    }

    public String getExchangeReference() {
        return exchangeReference;
    }

    public void setExchangeReference(String exchangeReference) {
        this.exchangeReference = exchangeReference;
    }

    public ZonedDateTime getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(ZonedDateTime tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public TradeDirection getTradeDirection() {
        return tradeDirection;
    }

    public void setTradeDirection(TradeDirection tradeDirection) {
        this.tradeDirection = tradeDirection;
    }

    public Integer getTradeQuantity() {
        return tradeQuantity;
    }

    public void setTradeQuantity(Integer tradeQuantity) {
        this.tradeQuantity = tradeQuantity;
    }

    public String getTradeCurrency() {
        return tradeCurrency;
    }

    public void setTradeCurrency(String tradeCurrency) {
        this.tradeCurrency = tradeCurrency;
    }

    public Float getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(Float tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getExchangeReferenceCcp() {
        return exchangeReferenceCcp;
    }

    public void setExchangeReferenceCcp(String exchangeReferenceCcp) {
        this.exchangeReferenceCcp = exchangeReferenceCcp;
    }

    public ZonedDateTime getTradeDateCcp() {
        return tradeDateCcp;
    }

    public void setTradeDateCcp(ZonedDateTime tradeDateCcp) {
        this.tradeDateCcp = tradeDateCcp;
    }

    public String getIsinCcp() {
        return isinCcp;
    }

    public void setIsinCcp(String isinCcp) {
        this.isinCcp = isinCcp;
    }

    public TradeDirection getTradeDirectionCcp() {
        return tradeDirectionCcp;
    }

    public void setTradeDirectionCcp(TradeDirection tradeDirectionCcp) {
        this.tradeDirectionCcp = tradeDirectionCcp;
    }

    public Integer getTradeQuantityCcp() {
        return tradeQuantityCcp;
    }

    public void setTradeQuantityCcp(Integer tradeQuantityCcp) {
        this.tradeQuantityCcp = tradeQuantityCcp;
    }

    public String getTradeCurrencyCcp() {
        return tradeCurrencyCcp;
    }

    public void setTradeCurrencyCcp(String tradeCurrencyCcp) {
        this.tradeCurrencyCcp = tradeCurrencyCcp;
    }

    public Float getTradeAmountCcp() {
        return tradeAmountCcp;
    }

    public void setTradeAmountCcp(Float tradeAmountCcp) {
        this.tradeAmountCcp = tradeAmountCcp;
    }

    public void setTradeData(String tradeReference,String exchangeReference,String tradeDate,String isin,String tradeDirection,String tradeQuantity,String tradeCurrency,String tradeAmount){
        setTradeReference(tradeReference.trim());
        setExchangeReference(exchangeReference.trim());
        setTradeDate(Utils.getZonedDateTime(tradeDate.trim()));
        setIsin(isin.trim());
        setTradeDirection(TradeDirection.valueOf(tradeDirection.trim()));
        setTradeQuantity(Integer.parseInt(tradeQuantity.trim()));
        setTradeCurrency(tradeCurrency.trim());
        setTradeAmount(Float.valueOf(tradeAmount.trim()));
    }

    public void setCcpData(String exchangeReferenceCcp,String tradeDateCcp,String isinCcp,String tradeDirectionCcp,String tradeQuantityCcp,String tradeCurrencyCcp,String tradeAmountCcp){
        setExchangeReferenceCcp(exchangeReferenceCcp.trim());
        setTradeDateCcp(Utils.getZonedDateTime(tradeDateCcp.trim()));
        setIsinCcp(isinCcp.trim());
        setTradeDirectionCcp(TradeDirection.valueOf(tradeDirectionCcp.trim()));
        setTradeQuantityCcp(Integer.parseInt(tradeQuantityCcp.trim()));
        setTradeCurrencyCcp(tradeCurrencyCcp.trim());
        setTradeAmountCcp(Float.valueOf(tradeAmountCcp.trim()));
    }

    static {
        LogbackAdaptor.logInternalStatus();
    }

    public ReportLogger getLogger() {
        return logger;
    }

    public void addConcordionTooltip(final String message) {
        tooltipLogger.debug(message);
    }

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }


    @AfterClass
    public static void teardown() {
        system.shutdown();
        system.awaitTermination(Duration.create("10 seconds"));
    }
    @AfterExample
    private final void afterExample() {

        logger.trace(Marker.ANY_NON_NULL_MARKER);

    }


    private void stopActors2() {
        new JavaTestKit(system) {{
            ActorSelection supervisor = system.actorSelection("/user/" + Constants.SUPERVISOR_CLASS.getSimpleName());
            supervisor.tell(PoisonPill.getInstance(), null);
        }};
    }
    public Trade loadTradeData() {
        final Trade trade = new Trade.TradeBuilder(getExchangeReference())
                .withReference(getTradeReference())
                .withTradeDate(getTradeDate())
                .withIsin(getIsin())
                .withDirection(getTradeDirection())
                .withQuantity(getTradeQuantity())
                .withCurrency(getTradeCurrency())
                .withAmount(getTradeAmount())
                .build();
        return trade;
    }

    public CcpTrade loadCcpTradeData(){
        final CcpTrade ccpTrade = new CcpTrade.CcpTradeBuilder(getExchangeReferenceCcp())
                .withTradeDate(getTradeDateCcp())
                .withIsin(getIsinCcp())
                .withDirection(getTradeDirectionCcp())
                .withQuantity(getTradeQuantityCcp())
                .withCurrency(getTradeCurrencyCcp())
                .withAmount(getTradeAmountCcp())
                .build();
        return ccpTrade;
    }

    public Integer canAddATrade() {
        CompletableFuture<Integer> canAddATrade = new CompletableFuture<>();
        new JavaTestKit(system) {{
            logger.info("Starting canAddATrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
            final Trade trade = Trade.aTrade();
            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesListsRequest(), getTestActor());

            TradesListsResponse response = expectMsgClass(TradesListsResponse.class);
            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    canAddATrade.complete(response.getTradeList().size());
                    logger.info("Add a Trade {} on thread {}", this.getClass().getSimpleName(), Thread.currentThread().getName());
                    stopActors2();
                }
            };
        }};

        try {
            return canAddATrade.get();
        } catch (InterruptedException e) {
            return -1;
        } catch (ExecutionException e) {
            return -1;
        }
    }

    public Integer canAddACcpTrade() {
        CompletableFuture<Integer> canAddACcpTrade = new CompletableFuture<>();
        new JavaTestKit(system) {{
            logger.info("Starting canAddCcpTrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
            final CcpTrade ccpTrade = CcpTrade.aCcpTrade();

            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesListsRequest(), getTestActor());

            TradesListsResponse response = expectMsgClass(TradesListsResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    canAddACcpTrade.complete(response.getCcpTradeList().size());
                    logger.info("Add a Ccp Trade {} on thread {}", this.getClass().getSimpleName(), Thread.currentThread().getName());
                    stopActors2();
                }
            };
        }};

        try {
            return canAddACcpTrade.get();
        } catch (InterruptedException e) {
            return -1;
        } catch (ExecutionException e) {
            return -1;
        }
    }


    public TradeValues canPerformAMatch() throws ParseException, ExecutionException, InterruptedException {

        CompletableFuture<String> canPerformATradeMAtch = new CompletableFuture<>();
        CompletableFuture<String> canPerformACcpMAtch = new CompletableFuture<>();
        final byte[] flag = {0};
        new JavaTestKit(system) {
            {
                logger.info("Perform Matching starting ...");

                Trade trade = loadTradeData();
                CcpTrade ccpTrade = loadCcpTradeData();

                final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
                supervisor.tell(new NewTradeMessage(trade), getTestActor());
                supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
                Utils.delayExec(RESULTS_DELAY_MS);
                supervisor.tell(new TradesMatchRequest(), getTestActor());
                final TradesMatchResponse response = expectMsgClass(TradesMatchResponse.class);

                new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                    protected void run() {
                        Map<String, TradeOutput<Trade>> matchedTradesMap = response.getMatchedTradesMap();
                        Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap = response.getMatchedCcpTradesMap();
                        if (matchedCcpTradesMap.size() != 0 && matchedTradesMap.size() != 0) {
                            canPerformATradeMAtch.complete(matchedTradesMap.keySet().iterator().next());
                            canPerformACcpMAtch.complete(matchedCcpTradesMap.keySet().iterator().next());
                            logger.info("Perform a Trade and Ccp Match - Trade {} on thread {}", this.getClass().getSimpleName(), Thread.currentThread().getName());
                        } else {
                            logger.error("Trade Map is empty no matching elements in the list of trades ");
                            flag[0] = 1;
                        }
                        stopActors2();
                    }
                };
            }
        };
        TradeValues tradeV = new TradeValues();
        if (flag[0] == 0) {
            try {
                tradeV.exchangeRef = canPerformATradeMAtch.get();
                tradeV.ccpExchangeRef = canPerformACcpMAtch.get();
                return tradeV;
            } catch (InterruptedException e) {
                tradeV.exchangeRef = "ERROR";
                tradeV.ccpExchangeRef = "ERROR";
                return tradeV;
            } catch (ExecutionException e) {
                tradeV.exchangeRef = "ERROR";
                tradeV.ccpExchangeRef = "ERROR";
                return tradeV;
            }
        }
        tradeV.exchangeRef = "ERROR";
        tradeV.ccpExchangeRef = "ERROR";
        return tradeV;
    }

    public String matchingTest() {
        CompletableFuture<String> matching = new CompletableFuture<>();
        new JavaTestKit(system) {{
            logger.info("Perform a Match Scenario " + String.valueOf(++scenarioCounter));

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            Trade trade = loadTradeData();
            CcpTrade ccpTrade = loadCcpTradeData();
            NewTradeMessage newTradeMessage = new NewTradeMessage(trade);
            supervisor.tell(newTradeMessage, getTestActor());
            NewCcpTradeMessage newCcpTradeMessage = new NewCcpTradeMessage(ccpTrade);
            supervisor.tell(newCcpTradeMessage, getTestActor());

            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesMatchRequest(), getTestActor());
            TradesMatchResponse response = expectMsgClass(new FiniteDuration(20, TimeUnit.SECONDS), TradesMatchResponse.class);

            new Within(new FiniteDuration(20, TimeUnit.SECONDS)) {
                protected void run() {
                      Map<String, TradeOutput<Trade>> matchedTradesMap = response.getMatchedTradesMap();
                      Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap = response.getMatchedCcpTradesMap();
                      Map<String, TradeOutput<Trade>> unmatchedTradesMap = response.getUnmatchedTradesMap();
                      Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap = response.getUnmatchedCcpTradesMap();


                    for (TradeOutput<Trade> tradeOutput : matchedTradesMap.values()) {
                        if (tradeOutput.getComment().equals(TradeComment.FULL_MATCH.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("Trade perform a Full Match ...");
                        }
                        if (tradeOutput.getComment().equals(TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("Trade match within tolerance for amount");
                        }
                    }

                    for (TradeOutput<Trade> tradeOutput : unmatchedTradesMap.values()) {
                        if (tradeOutput.getComment().equals(TradeComment.UNMATCH_ECONOMICS_MISMATCH.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("Unmatch trade, economics mismatch ...");
                        }
                        if (tradeOutput.getComment().equals(TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("Unmatch trade, outside of tolerance for amount ...");
                        }
                        if (tradeOutput.getComment().equals(TradeComment.CCP_TRADE_UNMATCH.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("CCP Trade unmatch ...");
                        }
                    }

                    for (TradeOutput<CcpTrade> tradeOutput : matchedCcpTradesMap.values()) {
                        if (tradeOutput.getComment().equals(TradeComment.FULL_MATCH.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("Ccp matching ...");
                        }
                        if (tradeOutput.getComment().equals(TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("CCP match within tolerance for amount ...");
                        }
                    }

                    for (TradeOutput<CcpTrade> tradeOutput : unmatchedCcpTradesMap.values()) {
                        if (tradeOutput.getComment().equals(TradeComment.UNMATCH_ECONOMICS_MISMATCH.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("CCP unmatch, economics mismatch ...");
                        }
                        if (tradeOutput.getComment().equals(TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("CCP unmatch, outside of tolerance for amount ...");
                        }

                        if (tradeOutput.getComment().equals(TradeComment.CCP_TRADE_UNMATCH.getComment())) {
                            matching.complete(tradeOutput.getComment());
                            logger.info("CCP unmatch ...");
                        }
                    }
                    stopActors2();
                }
            };
        }};
        try {
            return matching.get().toString();
        } catch (InterruptedException e) {
            return "ERROR";
        } catch (ExecutionException e) {
            return "ERROR";
        }
    }


    class TradeValues {
        public String exchangeRef;
        public String ccpExchangeRef;
        public Integer unmatchVal;

        public TradeValues() {
            this.exchangeRef = exchangeRef;
            this.ccpExchangeRef = ccpExchangeRef;
            this.unmatchVal = unmatchVal;
        }

        public String gettradeRef() {
            return exchangeRef;
        }

        public String getccpRef() {
            return ccpExchangeRef;
        }

        public Integer getunmatchVal() {
            return unmatchVal;
        }

    }


    private TestActorRef getSupervisorActor() {
        String name = Constants.SUPERVISOR_CLASS.getSimpleName();
        return TestActorRef.create(system, Props.create(Constants.SUPERVISOR_CLASS), name);
    }

}


