package se.kth.app.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.TestMsg;
import se.kth.broadcast.Deliver;
import se.kth.sets.events.Add;
import se.kth.sets.events.Remove;
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

import java.util.UUID;

public class SimClientInfrastructure extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(SimClientInfrastructure.class);
  private String logPrefix = " ";

  //*******************************CONNECTIONS********************************
  Positive<Timer> timer = requires(Timer.class);
  Positive<Network> net = requires(Network.class);

  Positive<CausalOrderReliableBroadcastPort> crb = requires(CausalOrderReliableBroadcastPort.class);
  //**************************************************************************
  private KAddress selfAdr;

  public SimClientInfrastructure(Init init) {
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
      TestMsg testMsg = new TestMsg(getRandomMessage());
      KAddress initialPeer = ScenarioSetup.getNodeAdr("192.0.0.5", 5);
      KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
      KContentMsg msg = new BasicContentMsg(header, testMsg);
      LOG.info("Sending msg {}", testMsg.getMsg());
      trigger(msg, net);
    }
  };

  Handler handleCRBDeliver = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      LOG.info("{} received {} from {}", logPrefix, deliver.payload, deliver.src);
    }
  };

  private String getRandomMessage(){
    return UUID.randomUUID().toString();
  }

  public static class Init extends se.sics.kompics.Init<SimClientInfrastructure> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }

}
