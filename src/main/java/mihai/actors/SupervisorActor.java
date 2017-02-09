package mihai.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import mihai.messages.CancelCcpTradeMessage;
import mihai.messages.CancelTradeMessage;
import mihai.messages.NewCcpTradeMessage;
import mihai.messages.NewTradeMessage;
import mihai.messages.TradesListsRequest;
import mihai.messages.TradesMatchRequest;
import mihai.utils.Constants;

/**
 * Created by mcojocariu on 2/8/2017.
 */
public class SupervisorActor extends UntypedActor {
    private ActorRef aggregatorActor = null;

    public SupervisorActor() {
        aggregatorActor = getContext().actorOf(Props.create(Constants.AGGREGATOR_CLASS), Constants.AGGREGATOR_CLASS.getSimpleName());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof NewTradeMessage) {
            performNewTrade((NewTradeMessage) message);
        } else if (message instanceof NewCcpTradeMessage) {
            performNewCcpTrade((NewCcpTradeMessage) message);
        } else if (message instanceof CancelTradeMessage) {
            performCancelTrade((CancelTradeMessage) message);
        } else if (message instanceof CancelCcpTradeMessage) {
            performCancelCcpTrade((CancelCcpTradeMessage) message);
        } else if (message instanceof TradesMatchRequest || message instanceof TradesListsRequest) {
            performGetTrades(message);
        } else {
            unhandled(message);
        }
    }

    private void performGetTrades(Object message) {
        aggregatorActor.tell(message, getSender());
    }

    private void performCancelCcpTrade(CancelCcpTradeMessage cancelCcpTradeMessage) {
        ActorRef actor = getChildActor(cancelCcpTradeMessage.getCcpTrade().getExchangeReference());
        actor.tell(cancelCcpTradeMessage, getSelf());
    }

    private void performCancelTrade(CancelTradeMessage cancelTradeMessage) {
        ActorRef actor = getChildActor(cancelTradeMessage.getTrade().getExchangeReference());
        actor.tell(cancelTradeMessage, getSelf());
    }

    private void performNewCcpTrade(NewCcpTradeMessage newCcpTradeMessage) {
        ActorRef actor = getChildActor(newCcpTradeMessage.getCcpTrade().getExchangeReference());
        actor.tell(newCcpTradeMessage, getSelf());
    }

    private void performNewTrade(NewTradeMessage newTradeMessage) {
        ActorRef actor = getChildActor(newTradeMessage.getTrade().getExchangeReference());
        actor.tell(newTradeMessage, getSelf());
    }

    private ActorRef getChildActor(String exchangeReference) {
        String childActorName = getChildActorName(exchangeReference);
        ActorRef actor = getContext().getChild(childActorName);
        if (actor == null) {
            actor = getContext().actorOf(Props.create(Constants.WORKER_CLASS, aggregatorActor), childActorName);
        }
        return actor;
    }

    private String getChildActorName(String exchangeReference) {
        return Constants.WORKER_CLASS.getSimpleName() + "_" + String.valueOf(exchangeReference.charAt(0)).toUpperCase();
    }
}
