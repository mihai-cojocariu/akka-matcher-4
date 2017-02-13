package mihai;

/**
 * Created by mcojocariu on 1/31/2017.
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
import mihai.utils.TradeDirection;
import mihai.utils.Utils;
import org.concordion.api.extension.Extensions;
import org.concordion.ext.excel.ExcelExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.logback.LogbackAdaptor;
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(ConcordionRunner.class)
//@Extensions(value = TimerExtension.class)
@Extensions(ExcelExtension.class)
//@FailFast
public class ConcordionTradesTest {
    private static ActorSystem system;
    private final static int RESULTS_DELAY_MS = 50;
    private final ReportLogger logger = ReportLoggerFactory.getReportLogger(this.getClass().getName());
    private final Logger tooltipLogger = LoggerFactory.getLogger("TOOLTIP_" + this.getClass().getName());

    public ConcordionTradesTest() {

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

//    @After
//    public void stopActors() {
//        new JavaTestKit(system) {{
//            ActorSelection supervisor = system.actorSelection("/user/" + Constants.SUPERVISOR_CLASS.getSimpleName());
//            supervisor.tell(PoisonPill.getInstance(), null);
//        }};
//    }

    private void stopActors2() {
        new JavaTestKit(system) {{
            ActorSelection supervisor = system.actorSelection("/user/" + Constants.SUPERVISOR_CLASS.getSimpleName());
            supervisor.tell(PoisonPill.getInstance(), null);
        }};
    }

    public Integer canAddATrade() {
        CompletableFuture<Integer> canAddATrade = new CompletableFuture<>();
        new JavaTestKit(system) {{
            logger.info("Starting canAddATrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
            final Trade trade = Trade.aTrade();
            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS );
            supervisor.tell(new TradesListsRequest(), getTestActor());

            TradesListsResponse response = expectMsgClass(TradesListsResponse.class);
            new Within(new FiniteDuration(10, TimeUnit.SECONDS)){
                protected void run(){
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

            new Within(new FiniteDuration(10,TimeUnit.SECONDS)){
                protected void run(){
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



    public TradeValues canPerformAMatch(String externalReference, String externalReferenceCCP, String tradeReference,
                                        String tradeDate, String tradeDateCCP, String is2in, String is2inCCP,
                                        String direction, String directionCCP,
                                        String quantity, String quantityCCP, String currency, String currencyCCP,
                                        String amount, String amountCCP) throws ParseException, ExecutionException, InterruptedException {

        CompletableFuture<String> canPerformATradeMAtch = new CompletableFuture<>();
        CompletableFuture<String> canPerformACcpMAtch = new CompletableFuture<>();
        new JavaTestKit(system) {
            {
                logger.info("Perform a Match starting ...");

                final Trade trade = new Trade.TradeBuilder(externalReference)
                        .withReference(tradeReference)
                        .withTradeDate(Utils.getZonedDateTime(tradeDate))
                        .withIsin(is2in)
                        .withDirection(TradeDirection.valueOf(direction))
                        .withQuantity(Integer.parseInt(quantity))
                        .withCurrency(currency)
                        .withAmount(Float.parseFloat(amount))
                        .build();

                final CcpTrade ccpTrade = new CcpTrade.CcpTradeBuilder(externalReferenceCCP)
                        .withTradeDate(Utils.getZonedDateTime(tradeDateCCP))
                        .withIsin(is2inCCP)
                        .withDirection(TradeDirection.valueOf(directionCCP))
                        .withQuantity(Integer.parseInt(quantityCCP))
                        .withCurrency(currencyCCP)
                        .withAmount(Float.parseFloat(amountCCP))
                        .build();

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

                        canPerformATradeMAtch.complete(matchedTradesMap.keySet().iterator().next());
                        canPerformACcpMAtch.complete(matchedCcpTradesMap.keySet().iterator().next());

                        logger.info("Perform a Trade and Ccp Match - Trade {} on thread {}", this.getClass().getSimpleName(), Thread.currentThread().getName());
                        stopActors2();
                    }
                };
            }};
        TradeValues tradeV = new TradeValues();
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
class TradeValues{
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
    public void abc(ExcelTrade excelTrade) {
        //ExcelTrade excelTrade;// = new ExcelTrade();

        System.out.println(excelTrade.getExchangeReference());
        System.out.println("cucu");
    }


//    public String canIdentifyUnmatchedTrades(String externalReference, String externalReferenceCCP, String tradeReference,
//                                            String tradeDate, String tradeDateCCP, String is2in, String is2inCCP,
//                                            String direction, String directionCCP,
//                                            String quantity, String quantityCCP, String currency, String currencyCCP,
//                                            String amount, String amountCCP) throws ParseException {
//
//        CompletableFuture<Trade> canIdentifyUnmatchedTrades = new CompletableFuture<>();
//        CompletableFuture<CcpTrade> canIdentifyUnmatchedCcpTrades = new CompletableFuture<>();
//
//        new JavaTestKit(system) {{
//            logger.info("Starting canIdentifyUnmatchedTrades()...");
//            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
//
//            final Trade trade = new Trade.TradeBuilder(externalReference)
//                    .withReference(tradeReference)
//                    .withTradeDate(Utils.getZonedDateTime(tradeDate))
//                    .withIsin(is2in)
//                    .withDirection(TradeDirection.valueOf(direction))
//                    .withQuantity(Integer.parseInt(quantity))
//                    .withCurrency(currency)
//                    .withAmount(Float.parseFloat(amount))
//                    .build();
//
//            final CcpTrade ccpTrade = new CcpTrade.CcpTradeBuilder(externalReferenceCCP)
//                    .withTradeDate(Utils.getZonedDateTime(tradeDateCCP))
//                    .withIsin(is2inCCP)
//                    .withDirection(TradeDirection.valueOf(directionCCP))
//                    .withQuantity(Integer.parseInt(quantityCCP))
//                    .withCurrency(currencyCCP)
//                    .withAmount(Float.parseFloat(amountCCP))
//                    .build();
//
//            supervisor.tell(new NewTradeMessage(trade), getTestActor());
//            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
//            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_UNMATCHED_TRADES), getTestActor());
//
//            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);
//
//            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
//                protected void run() {
////                    assertEquals(1, response.getTrades().size());
////                    assertEquals(1, response.getCcpTrades().size());
////                    assertEquals(trade, response.getTrades().get(0));
////                    assertEquals(ccpTrade, response.getCcpTrades().get(0));
//                    canIdentifyUnmatchedTrades.complete(response.getTrades().get(0));
//                    canIdentifyUnmatchedCcpTrades.complete(response.getCcpTrades().get(0));
//                    logger.info("Identify the Unmatch trade ");
//                }
//            };
//
//        }};
//        try {
//            return canIdentifyUnmatchedTrades.get().getExchangeReference();
//        } catch (InterruptedException e) {
//            return "ERROR";
//        } catch (ExecutionException e) {
//            return "ERROR";
//        }
//    }

    private TestActorRef getSupervisorActor() {
        String name = Constants.SUPERVISOR_CLASS.getSimpleName();
        return TestActorRef.create(system, Props.create(Constants.SUPERVISOR_CLASS), name);
    }

    class ExcelTrade{
        String tradeReference;
        String exchangeReference;
       // ZonedDateTime tradeDate;
        String isin;
        TradeDirection tradeDirection;
        Integer quantity;
        String currency;
        Float amount;

        public ExcelTrade() {
            this.tradeReference = tradeReference;
            this.exchangeReference = exchangeReference;
         //   this.tradeDate = tradeDate;
            this.isin = isin;
            this.tradeDirection = tradeDirection;
            this.quantity = quantity;
            this.currency = currency;
            this.amount = amount;
        }

        public String getTradeReference() {
            return tradeReference;
        }

        public String getExchangeReference() {
            return exchangeReference;
        }

        //public ZonedDateTime getTradeDate() {
         //   return tradeDate;
        //}

        public String getIsin() {
            return isin;
        }

        public TradeDirection getTradeDirection() {
            return tradeDirection;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public String getCurrency() {
            return currency;
        }

        public Float getAmount() {
            return amount;
        }

        public void setTradeReference(String tradeReference) {
            this.tradeReference = tradeReference;
        }

        public void setExchangeReference(String exchangeReference) {
            this.exchangeReference = exchangeReference;
        }

//        public void setTradeDate(ZonedDateTime tradeDate) {
//            this.tradeDate = tradeDate;
//        }

        public void setIsin(String isin) {
            this.isin = isin;
        }

        public void setTradeDirection(TradeDirection tradeDirection) {
            this.tradeDirection = tradeDirection;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public void setAmount(Float amount) {
            this.amount = amount;
        }
    }

}


