package se.kth.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class RB_Broadcast extends Broadcast {
  List past;

  public RB_Broadcast(KompicsEvent payload, List past) {
    super(payload);
    this.past = past;
  }
}

class RB_Deliver extends Deliver {
  List<Deliver> past;

  public RB_Deliver(KAddress src, KompicsEvent payload, List past) {
    super(src, payload);
    this.past = past;
  }
}

public class EagerReliableBroadcast extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(EagerReliableBroadcast.class);

  private Negative<ReliableBroadcastPort> rb = provides(ReliableBroadcastPort.class);
  private Positive<BestEffortBroadcastPort> beb = requires(BestEffortBroadcastPort.class);

  private KAddress selfAdr;
  private Set delivered;

  public EagerReliableBroadcast(Init init) {
    selfAdr = init.selfAdr;
    delivered = new HashSet();

    subscribe(broadcastHandler, rb);
    subscribe(deliverHandler, beb);
  }

  Handler<RB_Broadcast> broadcastHandler = new Handler<RB_Broadcast>() {
    @Override
    public void handle(RB_Broadcast broadcast) {
      trigger(new BEB_Broadcast(broadcast, selfAdr), beb);
    }
  };

  Handler<BEB_Deliver> deliverHandler = new Handler<BEB_Deliver>() {
    @Override
    public void handle(BEB_Deliver deliver) {
      if(!delivered.contains(deliver.payload)){
        delivered.add(deliver.payload);
        //TODO: Following lines could cause troubles...
        if(!(deliver.payload instanceof RB_Broadcast)){
          LOG.warn("A ReliableBroadcast component has received a message which is not a ReliableBroadcast");
        }
        RB_Broadcast broadcast = (RB_Broadcast) deliver.payload;
        trigger(new RB_Deliver(deliver.src, broadcast.payload, broadcast.past), rb);
        trigger(new BEB_Broadcast(deliver.payload, deliver.src), beb);
      }
    }
  };

  public static class Init extends se.sics.kompics.Init<EagerReliableBroadcast> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }
}
