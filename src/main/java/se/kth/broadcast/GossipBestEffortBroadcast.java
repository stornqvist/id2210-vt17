package se.kth.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.croupier.util.CroupierHelper;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

class BEB_Broadcast extends Broadcast {

  public KAddress src;
  public BEB_Broadcast(KompicsEvent payload, KAddress src){
    super(payload);
    this.src = src;
  }
}

class BEB_Deliver extends Deliver {

  public BEB_Deliver(KAddress src, KompicsEvent payload) {
    super(src, payload);
  }
}

public class GossipBestEffortBroadcast extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(GossipBestEffortBroadcast.class);

  private Negative<BestEffortBroadcastPort> gbeb = provides(BestEffortBroadcastPort.class);
  private Positive<Network> net = requires(Network.class);
  private Positive<CroupierPort> croupier = requires(CroupierPort.class);

  private KAddress selfAdr;

  private Set<Deliver> past;

  public GossipBestEffortBroadcast(Init init) {
    past = new HashSet<>();
    selfAdr = init.selfAdr;

    subscribe(broadcastHandler, gbeb);
    subscribe(sampleHandler, croupier);
    subscribe(historyRequestHandler, net);
    subscribe(historyResponseHandler, net);
  }

  Handler<BEB_Broadcast> broadcastHandler = new Handler<BEB_Broadcast>() {
    @Override
    public void handle(BEB_Broadcast broadcast) {
      past.add(new BEB_Deliver(broadcast.src, broadcast.payload));
    }
  };

  Handler<CroupierSample> sampleHandler = new Handler<CroupierSample>() {
    @Override
    public void handle(CroupierSample croupierSample) {
      for (KAddress adr : (List<KAddress>) CroupierHelper.getSample(croupierSample)){
        KHeader header = new BasicHeader(selfAdr, adr, Transport.UDP);
        trigger(new BasicContentMsg(header, new HistoryRequest(selfAdr)), net);
      }
    }
  };

  ClassMatchedHandler historyRequestHandler = new ClassMatchedHandler<HistoryRequest, KContentMsg<?, ?, HistoryRequest>>() {
    @Override
    public void handle(HistoryRequest historyRequest, KContentMsg msg) {
      trigger(msg.answer(new HistoryResponse(past)), net);
    }
  };

  ClassMatchedHandler historyResponseHandler = new ClassMatchedHandler<HistoryResponse, KContentMsg<?, ?, HistoryResponse>>() {
    @Override
    public void handle(HistoryResponse historyResponse, KContentMsg kContentMsg) {
      Set<Deliver> unseen = Sets.difference(historyResponse.history, past);
      for (Deliver deliver : unseen) {
        trigger(deliver, gbeb);
      }
      past.addAll(unseen);
    }
  };

  public static class Init extends se.sics.kompics.Init<GossipBestEffortBroadcast> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }

  public class HistoryRequest implements KompicsEvent {
    KAddress src;

    public HistoryRequest(KAddress src) {
      this.src = src;
    }
  }

  public class HistoryResponse implements KompicsEvent {
    Set<Deliver> history;

    public HistoryResponse(Set history) {
      this.history = history;
    }
  }
}
