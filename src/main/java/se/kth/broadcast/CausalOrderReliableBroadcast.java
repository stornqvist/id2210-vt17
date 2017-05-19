package se.kth.broadcast;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class CRB_Broadcast extends Broadcast {
    List past;

    public CRB_Broadcast(KompicsEvent payload, List past) {
        super(payload);
        this.past = past;
    }
}

class CRB_Deliver extends Deliver {

    public CRB_Deliver(KAddress src, KompicsEvent payload) {
        super(src, payload);
    }
}

public class CausalOrderReliableBroadcast extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(CausalOrderReliableBroadcast.class);

    private Negative<CausalOrderReliableBroadcastPort> crb = provides(CausalOrderReliableBroadcastPort.class);
    private Positive<ReliableBroadcastPort> rb = requires(ReliableBroadcastPort.class);

    private KAddress selfAdr;

    private Set delivered;
    private List<Deliver> past;

    public CausalOrderReliableBroadcast(Init init) {
        selfAdr = init.selfAdr;
        delivered = new HashSet();
        past = new LinkedList<>();

        subscribe(broadcastHandler, crb);
        subscribe(deliverHandler, rb);
    }

    Handler<Broadcast> broadcastHandler = new Handler<Broadcast>() {
        @Override
        public void handle(Broadcast msg) {
            CRB_Broadcast crbBroadcast = new CRB_Broadcast(msg.payload, new LinkedList());
            trigger(new RB_Broadcast(crbBroadcast, past), rb);
            past.add(new CRB_Deliver(selfAdr, msg.payload));
            //LOG.info("CRB at {} Received broadcast request", selfAdr);
        }
    };

    //TODO: Prevent duplicate sendning of events. There are two causes for this: There is a double trigger
    // which potentially overlap. Furthermore, there is an issue with encapsulation, since the RB_Deliver
    // contains CRB_Broadcast which in turn contain the desired KompicsEvent.
    Handler<RB_Deliver> deliverHandler = new Handler<RB_Deliver>() {
        @Override
        public void handle(RB_Deliver deliver) {
            //LOG.info("Received RB_Deliver containing {}", deliver.payload);
            CRB_Broadcast crbBroadcast = (CRB_Broadcast) deliver.payload;
            //Broadcast broadcast = (Broadcast) crbBroadcast.payload;
            if (!delivered.contains(crbBroadcast.payload)) {
                for (Deliver d : deliver.past){
                    if(!delivered.contains(d.payload)){
                        trigger(new CRB_Deliver(d.src, d.payload), crb);
                        //LOG.info("{} is delivering message upwards", selfAdr);
                        delivered.add(d.payload);
                        if (!past.contains(d)) {
                            past.add(d);
                        }
                    }
                }
                trigger(new CRB_Deliver(deliver.src ,crbBroadcast.payload), crb);
                //LOG.info("{} is delivering message upwards", selfAdr);
                delivered.add(deliver.payload);
                if (!past.contains(deliver)) {
                    past.add(deliver);
                }
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<CausalOrderReliableBroadcast> {

        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
        }
    }
}
