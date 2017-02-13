package mihai;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import mihai.actors.SupervisorActor;
import mihai.dto.CcpTrade;
import mihai.dto.Trade;
import mihai.dto.TradeOutput;
import mihai.messages.CancelCcpTradeMessage;
import mihai.messages.CancelTradeMessage;
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;
import mihai.messages.TradesListsRequest;
import mihai.messages.TradesListsResponse;
import mihai.messages.TradesMatchRequest;
import mihai.messages.TradesMatchResponse;
import mihai.utils.Constants;
import mihai.utils.TradeComment;
import mihai.utils.Utils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by mcojocariu on 1/31/2017.
 */
public class TradesTest {
    private static ActorSystem system;
    private LoggingAdapter log = Logging.getLogger(system, this);
    private final static int RESULTS_DELAY_MS = 50;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        system.shutdown();
        system.awaitTermination(Duration.create("10 seconds"));
    }

    @After
    public void stopActors() {
        new JavaTestKit(system) {{
            ActorSelection supervisor = system.actorSelection("/user/" + Constants.SUPERVISOR_CLASS.getSimpleName());
            supervisor.tell(PoisonPill.getInstance(), null);
        }};
    }

    @Test
    public void canAddATrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canAddATrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesListsRequest(), getTestActor());

            TradesListsResponse response = expectMsgClass(TradesListsResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(true, response.getTradeList().contains(trade));
                    assertEquals(1, response.getTradeList().size());
                    logUsedMemory();
                }
            };

        }};
    }

    @Test
    public void canAddACcpTrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canAddACcpTrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final CcpTrade ccpTrade = CcpTrade.aCcpTrade();
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesListsRequest(), getTestActor());

            TradesListsResponse response = expectMsgClass(TradesListsResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(true, response.getCcpTradeList().contains(ccpTrade));
                    assertEquals(1, response.getCcpTradeList().size());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canCancelATrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canCancelATrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade1 = Trade.aTrade();
            final Trade trade2 = Trade.aTrade();
            supervisor.tell(new NewTradeMessage(trade1), getTestActor());
            supervisor.tell(new NewTradeMessage(trade2), getTestActor());
            supervisor.tell(new CancelTradeMessage(trade1), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesListsRequest(), getTestActor());

            final TradesListsResponse response = expectMsgClass(TradesListsResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(1, response.getTradeList().size());
                    assertEquals(trade2, response.getTradeList().get(0));

                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canCancelACcpTrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canCancelACcpTrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final CcpTrade ccpTrade1 = CcpTrade.aCcpTrade();
            final CcpTrade ccpTrade2 = CcpTrade.aCcpTrade();
            supervisor.tell(new NewCcpTradeMessage(ccpTrade1), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade2), getTestActor());
            supervisor.tell(new CancelCcpTradeMessage(ccpTrade1), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesListsRequest(), getTestActor());

            final TradesListsResponse response = expectMsgClass(TradesListsResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    assertEquals(1, response.getCcpTradeList().size());
                    assertEquals(ccpTrade2, response.getCcpTradeList().get(0));

                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canPerformAMatch() {
        new JavaTestKit(system) {{
            log.debug("Starting canPerformAMatch()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            final CcpTrade ccpTrade = CcpTrade.aCcpTrade(trade);

            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesMatchRequest(), getTestActor());

            TradesMatchResponse response = expectMsgClass(TradesMatchResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    Map<String, TradeOutput<Trade>> matchedTradesMap = response.getMatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap = response.getMatchedCcpTradesMap();

                    assertEquals(1, matchedTradesMap.size());
                    assertEquals(trade.getExchangeReference(), matchedTradesMap.keySet().iterator().next());
                    assertEquals(1, matchedCcpTradesMap.size());
                    assertEquals(ccpTrade.getExchangeReference(), matchedCcpTradesMap.keySet().iterator().next());
                    logUsedMemory();
                }
            };

        }};
    }

    @Test
    public void canIdentifyUnmatchedTrades(){
        new JavaTestKit(system) {{
            log.debug("Starting canIdentifyUnmatchedTrades()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            final CcpTrade ccpTrade = CcpTrade.aCcpTrade();

            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesMatchRequest(), getTestActor());

            TradesMatchResponse response = expectMsgClass(TradesMatchResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    Map<String, TradeOutput<Trade>> unmatchedTradesMap = response.getUnmatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap = response.getUnmatchedCcpTradesMap();

                    assertEquals(1, unmatchedTradesMap.size());
                    assertEquals(trade.getExchangeReference(), unmatchedTradesMap.keySet().iterator().next());
                    assertEquals(1, unmatchedCcpTradesMap.size());
                    assertEquals(ccpTrade.getExchangeReference(), unmatchedCcpTradesMap.keySet().iterator().next());
                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void canIdentifyAnUnmatchPostCancel(){
        new JavaTestKit(system) {{
            log.debug("Starting canIdentifyAnUnmatchPostCancel()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade1 = Trade.aTrade();
            final CcpTrade ccpTrade1 = CcpTrade.aCcpTrade(trade1);
            final Trade trade2 = Trade.aTrade();
            supervisor.tell(new NewTradeMessage(trade1), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade1), getTestActor());
            supervisor.tell(new NewTradeMessage(trade2), getTestActor());
            supervisor.tell(new CancelTradeMessage(trade1), getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesMatchRequest(), getTestActor());

            final TradesMatchResponse response = expectMsgClass(TradesMatchResponse.class);

            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                protected void run() {
                    Map<String, TradeOutput<Trade>> matchedTradesMap = response.getMatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap = response.getMatchedCcpTradesMap();
                    Map<String, TradeOutput<Trade>> unmatchedTradesMap = response.getUnmatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap = response.getUnmatchedCcpTradesMap();

                    assertEquals(0, matchedTradesMap.size());
                    assertEquals(1, unmatchedTradesMap.size());
                    assertEquals(trade2.getExchangeReference(), unmatchedTradesMap.keySet().iterator().next());
                    assertEquals(0, matchedCcpTradesMap.size());
                    assertEquals(1, unmatchedCcpTradesMap.size());
                    assertEquals(ccpTrade1.getExchangeReference(), unmatchedCcpTradesMap.keySet().iterator().next());

                    logUsedMemory();
                }
            };
        }};
    }

    @Test
    public void testMatchTradesHighVolume() {
        new JavaTestKit(system) {{
            log.debug("Starting testMatchTradesHighVolume()...");

            final int numberOfTrades = 30000;
            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            // load trades
            Long startLoadTimestamp = System.currentTimeMillis();

            Utils.loadRandomTrades(supervisor, getTestActor(), numberOfTrades);

            Long endLoadTimestamp = System.currentTimeMillis();
            Long diffLoad = endLoadTimestamp - startLoadTimestamp;
            log.debug("Trades loading duration (ms): " + diffLoad);

            Utils.delayExec(RESULTS_DELAY_MS);

            Long startMatchTimestamp = System.currentTimeMillis();
            supervisor.tell(new TradesMatchRequest(), getTestActor());

            TradesMatchResponse response = expectMsgClass(new FiniteDuration(20, TimeUnit.SECONDS) ,TradesMatchResponse.class);

            new Within(new FiniteDuration(20, TimeUnit.SECONDS)) {
                protected void run() {
                    Long endMatchTimestamp = System.currentTimeMillis();
                    Long diffMatch = endMatchTimestamp - startMatchTimestamp;

                    Map<String, TradeOutput<Trade>> matchedTradesMap = response.getMatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap = response.getMatchedCcpTradesMap();
                    Map<String, TradeOutput<Trade>> unmatchedTradesMap = response.getUnmatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap = response.getUnmatchedCcpTradesMap();

                    log.debug("Trades matching duration (ms): " + diffMatch);
                    log.debug("Matched trades : {}, Matched CCP trades: {}", matchedTradesMap.size(), matchedCcpTradesMap.size());
                    log.debug("Unmatched trades : {}, Unmatched CCP trades: {}", unmatchedTradesMap.size(), unmatchedCcpTradesMap.size());
                    logUsedMemory();

                    assertEquals(10000, matchedTradesMap.size());
                    assertEquals(10000, matchedCcpTradesMap.size());
                    assertEquals(20000, unmatchedTradesMap.size());
                    assertEquals(20000, unmatchedCcpTradesMap.size());
                }
            };

        }};
    }

    @Test
    public void matchingTest() {
        new JavaTestKit(system) {{
            log.debug("Starting matchingTest()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            Utils.loadTrades(supervisor, getTestActor());
            Utils.delayExec(RESULTS_DELAY_MS);
            supervisor.tell(new TradesMatchRequest(), getTestActor());

            TradesMatchResponse response = expectMsgClass(new FiniteDuration(20, TimeUnit.SECONDS) ,TradesMatchResponse.class);

            new Within(new FiniteDuration(20, TimeUnit.SECONDS)) {
                protected void run() {
                    Map<String, TradeOutput<Trade>> matchedTradesMap = response.getMatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> matchedCcpTradesMap = response.getMatchedCcpTradesMap();
                    Map<String, TradeOutput<Trade>> unmatchedTradesMap = response.getUnmatchedTradesMap();
                    Map<String, TradeOutput<CcpTrade>> unmatchedCcpTradesMap = response.getUnmatchedCcpTradesMap();

                    logUsedMemory();

                    assertEquals(2, matchedTradesMap.size());
                    assertEquals(2, matchedCcpTradesMap.size());
                    assertEquals(3, unmatchedTradesMap.size());
                    assertEquals(3, unmatchedCcpTradesMap.size());

                    for (TradeOutput<Trade> tradeOutput : matchedTradesMap.values()) {
                        Trade trade = tradeOutput.getTrade();
                        if (trade.getExchangeReference().equals("EX100")) {
                            assertEquals(TradeComment.FULL_MATCH.getComment(), tradeOutput.getComment());
                        }
                        if (trade.getExchangeReference().equals("EX102")) {
                            assertEquals(TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT.getComment(), tradeOutput.getComment());
                        }
                    }

                    for (TradeOutput<Trade> tradeOutput : unmatchedTradesMap.values()) {
                        Trade trade = tradeOutput.getTrade();
                        if (trade.getExchangeReference().equals("EX101")) {
                            assertEquals(TradeComment.UNMATCH_ECONOMICS_MISMATCH.getComment(), tradeOutput.getComment());
                        }
                        if (trade.getExchangeReference().equals("EX103")) {
                            assertEquals(TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT.getComment(), tradeOutput.getComment());
                        }
                        if (trade.getExchangeReference().equals("EX104")) {
                            assertEquals(TradeComment.CCP_TRADE_UNMATCH.getComment(), tradeOutput.getComment());
                        }
                    }

                    for (TradeOutput<CcpTrade> tradeOutput : matchedCcpTradesMap.values()) {
                        CcpTrade ccpTrade = tradeOutput.getTrade();
                        if (ccpTrade.getExchangeReference().equals("EX100")) {
                            assertEquals(TradeComment.FULL_MATCH.getComment(), tradeOutput.getComment());
                        }
                        if (ccpTrade.getExchangeReference().equals("EX102")) {
                            assertEquals(TradeComment.MATCH_WITHIN_TOLERANCE_FOR_AMOUNT.getComment(), tradeOutput.getComment());
                        }
                    }

                    for (TradeOutput<CcpTrade> tradeOutput : matchedCcpTradesMap.values()) {
                        CcpTrade ccpTrade = tradeOutput.getTrade();
                        if (ccpTrade.getExchangeReference().equals("EX101")) {
                            assertEquals(TradeComment.UNMATCH_ECONOMICS_MISMATCH.getComment(), tradeOutput.getComment());
                        }
                        if (ccpTrade.getExchangeReference().equals("EX103")) {
                            assertEquals(TradeComment.UNMATCH_OUTSIDE_OF_TOLERANCE_FOR_AMOUNT.getComment(), tradeOutput.getComment());
                        }
                        if (ccpTrade.getExchangeReference().equals("EX104")) {
                            assertEquals(TradeComment.TRADE_UNMATCH.getComment(), tradeOutput.getComment());
                        }
                    }
                }
            };
        }};
    }


    private TestActorRef getSupervisorActor() {
        String name = Constants.SUPERVISOR_CLASS.getSimpleName();
        return TestActorRef.create(system, Props.create(Constants.SUPERVISOR_CLASS), name);
    }

    private void logUsedMemory() {
        log.debug("Used memory {}", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }
}
