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
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;
import mihai.messages.TradesListsRequest;
import mihai.messages.TradesListsResponse;
import mihai.messages.TradesMatchRequest;
import mihai.messages.TradesMatchResponse;
import mihai.utils.Constants;
import mihai.utils.Utils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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

            Runnable task = () -> {
                supervisor.tell(new TradesListsRequest(), getTestActor());

                TradesListsResponse response = expectMsgClass(TradesListsResponse.class);

                new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                    protected void run() {
                        assertEquals(true, response.getTradeList().contains(trade));
                        assertEquals(1, response.getTradeList().size());
                        logUsedMemory();
                    }
                };
            };

            system.scheduler().scheduleOnce(Duration.create(RESULTS_DELAY_MS, TimeUnit.MILLISECONDS), task, system.dispatcher());
        }};
    }

    @Test
    public void canAddACcpTrade() {
        new JavaTestKit(system) {{
            log.debug("Starting canAddACcpTrade()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final CcpTrade ccpTrade = CcpTrade.aCcpTrade();
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());

            Runnable task = () -> {
                supervisor.tell(new TradesListsRequest(), getTestActor());

                TradesListsResponse response = expectMsgClass(TradesListsResponse.class);

                new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
                    protected void run() {
                        assertEquals(true, response.getCcpTradeList().contains(ccpTrade));
                        assertEquals(1, response.getCcpTradeList().size());
                        logUsedMemory();
                    }
                };
            };

            system.scheduler().scheduleOnce(Duration.create(RESULTS_DELAY_MS, TimeUnit.MILLISECONDS), task, system.dispatcher());
        }};
    }

//    @Test
//    public void canCancelATrade() {
//        new JavaTestKit(system) {{
//            log.debug("Starting canCancelATrade()...");
//
//            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
//
//            final Trade trade1 = Trade.aTrade();
//            final Trade trade2 = Trade.aTrade();
//            supervisor.tell(new NewTradeMessage(trade1), getTestActor());
//            supervisor.tell(new NewTradeMessage(trade2), getTestActor());
//            supervisor.tell(new CancelTradeMessage(trade1), getTestActor());
//            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_TRADES), getTestActor());
//
//            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);
//
//            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
//                protected void run() {
//                    assertEquals(trade2, response.getTrades().get(0));
//                    assertEquals(1, response.getTrades().size());
//                    logUsedMemory();
//                }
//            };
//        }};
//    }
//
//    @Test
//    public void canCancelACcpTrade() {
//        new JavaTestKit(system) {{
//            log.debug("Starting canCancelACcpTrade()...");
//
//            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
//
//            final CcpTrade ccpTrade1 = CcpTrade.aCcpTrade();
//            final CcpTrade ccpTrade2 = CcpTrade.aCcpTrade();
//            supervisor.tell(new NewCcpTradeMessage(ccpTrade1), getTestActor());
//            supervisor.tell(new NewCcpTradeMessage(ccpTrade2), getTestActor());
//            supervisor.tell(new CancelCcpTradeMessage(ccpTrade1), getTestActor());
//            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_CCP_TRADES), getTestActor());
//
//            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);
//
//            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
//                protected void run() {
//                    assertEquals(ccpTrade2, response.getCcpTrades().get(0));
//                    assertEquals(1, response.getCcpTrades().size());
//                    logUsedMemory();
//                }
//            };
//        }};
//    }

    @Test
    public void canPerformAMatch() {
        new JavaTestKit(system) {{
            log.debug("Starting canPerformAMatch()...");

            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();

            final Trade trade = Trade.aTrade();
            final CcpTrade ccpTrade = CcpTrade.aCcpTrade(trade.getExchangeReference());

            supervisor.tell(new NewTradeMessage(trade), getTestActor());
            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());

            Runnable task = () -> {
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
            };

            system.scheduler().scheduleOnce(Duration.create(RESULTS_DELAY_MS, TimeUnit.MILLISECONDS), task, system.dispatcher());
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

            Runnable task = () -> {
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
            };

            system.scheduler().scheduleOnce(Duration.create(RESULTS_DELAY_MS, TimeUnit.MILLISECONDS), task, system.dispatcher());
        }};
    }

//    @Test
//    public void canIdentifyAnUnmatchPostCancel(){
//        new JavaTestKit(system) {{
//            log.debug("Starting canIdentifyAnUnmatchPostCancel()...");
//
//            final TestActorRef<SupervisorActor> supervisor = getSupervisorActor();
//
//            final Trade trade = Trade.aTrade();
//            final CcpTrade ccpTrade = CcpTrade.aCcpTrade(trade.getExchangeReference());
//
//            supervisor.tell(new NewTradeMessage(trade), getTestActor());
//            supervisor.tell(new NewCcpTradeMessage(ccpTrade), getTestActor());
//            supervisor.tell(new CancelTradeMessage(trade), getTestActor());
//            supervisor.tell(new TradesRequest(UUID.randomUUID().toString(), RequestType.GET_UNMATCHED_TRADES), getTestActor());
//
//            final TradesResponseMessage response = expectMsgClass(TradesResponseMessage.class);
//
//            new Within(new FiniteDuration(10, TimeUnit.SECONDS)) {
//                protected void run() {
//                    assertEquals(0, response.getTrades().size());
//                    assertEquals(1, response.getCcpTrades().size());
//                    assertEquals(ccpTrade, response.getCcpTrades().get(0));
//                    logUsedMemory();
//                }
//            };
//        }};
//    }
//
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


            // match trades and ccp trades
            Runnable task = () -> {
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
            };

            system.scheduler().scheduleOnce(Duration.create(RESULTS_DELAY_MS, TimeUnit.MILLISECONDS), task, system.dispatcher());
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
