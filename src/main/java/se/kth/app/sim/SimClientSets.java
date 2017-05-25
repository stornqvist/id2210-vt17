package se.kth.app.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.SetsScenarioType;
import se.kth.app.test.TestMsg;
import se.kth.broadcast.CausalOrderReliableBroadcastPort;
import se.kth.broadcast.Deliver;
import se.kth.sets.Add;
import se.kth.sets.Remove;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

public class SimClientSets extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(SimClientSets.class);
  private String logPrefix = " ";

  //*******************************CONNECTIONS********************************
  Positive<Timer> timer = requires(Timer.class);
  Positive<Network> net = requires(Network.class);
  //**************************************************************************
  private KAddress selfAdr;
  private SetsScenarioType type;

  public SimClientSets(SimClientSets.Init init) {
    selfAdr = init.selfAdr;
    type = init.type;
    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    subscribe(handleStart, control);
  }

  Handler handleStart = new Handler<Start>() {
    @Override
    public void handle(Start event) {
      LOG.info("{}starting...", logPrefix);
      //TODO: Do not hardcode this, the initIP could be a part of init construct
      KAddress initialPeer = ScenarioSetup.getNodeAdr("192.0.0.5", 5);
      String str = new String("Placeholder");
      // I wish I was using Scala...
      LOG.info("{} is the simulation type", type);
      switch (type) {
        case SIMPLE_ADD:
          sendAddOperation(str, initialPeer);
          break;
        case SIMPLE_REMOVE:
          sendRemoveOperation(str, initialPeer);
          break;
      }
    }
  };

  private void sendAddOperation(Object element, KAddress initialPeer) {
    Add addOperation = new Add(element);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, addOperation);
    LOG.info("{} operation is being triggered in about 0.01 ms, approximiatly", element);
    trigger(msg, net);
  }

  private void sendRemoveOperation(Object element, KAddress initialPeer) {
    Remove removeOperation = new Remove(element);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, removeOperation);
    LOG.info("{} operation is being triggered in about 0.01 ms, approximiatly", element);
    trigger(msg, net);
  }

  public static class Init extends se.sics.kompics.Init<SimClientInfrastructure> {

    public final KAddress selfAdr;
    public SetsScenarioType type;

    public Init(KAddress selfAdr, SetsScenarioType type) {
      this.selfAdr = selfAdr;
      this.type = type;
    }

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }
}
