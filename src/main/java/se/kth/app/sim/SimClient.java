package se.kth.app.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.Ping;
import se.kth.app.test.TestMsg;
import se.kth.broadcast.Broadcast;
import se.kth.broadcast.Deliver;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;

import se.kth.broadcast.CausalOrderReliableBroadcastPort;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

//TODO: Separate this client from the broadcasting procedures. Instead utiilise some network operations such as add, rm etc.

public class SimClient extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(SimClient.class);
  private String logPrefix = " ";

  //*******************************CONNECTIONS********************************
  Positive<Timer> timer = requires(Timer.class);
  Positive<Network> net = requires(Network.class);

  Positive<CausalOrderReliableBroadcastPort> crb = requires(CausalOrderReliableBroadcastPort.class);
  //**************************************************************************
  private KAddress selfAdr;

  public SimClient(Init init) {
    selfAdr = init.selfAdr;
    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    subscribe(handleStart, control);
    subscribe(handleCRBDeliver, crb);
  }

  Handler handleStart = new Handler<Start>() {
    @Override
    public void handle(Start event) {
      LOG.info("{}starting...", logPrefix);
      KAddress initialPeer = ScenarioSetup.getNodeAdr("192.0.0.5", 5);
      KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
      KContentMsg msg = new BasicContentMsg(header, new TestMsg("Hello World"));
      LOG.info("Sending msg {}", "Hello World");
      trigger(msg, net);
      //trigger(new Broadcast(new TestMsg("Hello World")), crb);
    }
  };

  Handler handleCRBDeliver = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      LOG.info("{} received {} from {}", logPrefix, deliver.payload, deliver.src);
    }
  };

  public static class Init extends se.sics.kompics.Init<SimClient> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }

}
