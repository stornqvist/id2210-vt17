package se.kth.app.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.SetsScenarioType;
import se.kth.sets.events.Add;
import se.kth.sets.events.Remove;
import se.kth.sets.graphs.*;
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
  private int id;

  private int uId;
  private int vId;

  public SimClientSets(SimClientSets.Init init) {
    selfAdr = init.selfAdr;
    type = init.type;

    // Graph simulation => retrieve the edge's vertices id
    if(10 < type.valueRepresentation && type.valueRepresentation < 100) {
      uId = init.uId;
      vId = init.vId;
    }

    // Graph simulation => retrieve vertex id
    if(type.valueRepresentation > 100) {
      id = init.id;
    }

    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    subscribe(handleStart, control);
  }

  Handler handleStart = new Handler<Start>() {
    @Override
    public void handle(Start event) {
      LOG.info("{}starting...", logPrefix);
      //TODO: Do not hardcode this, the initIP could be a part of init construct
      KAddress initialPeer = ScenarioSetup.getNodeAdr("192.0.0." + selfAdr.getId(), Integer.parseInt(selfAdr.getId().toString()));
      String str = new String("Placeholder");// + selfAdr.getId());
      // I wish I was using Scala...
      switch (type) {
        case SIMPLE_ADD:
          sendAddOperation(str, initialPeer);
          break;
        case SIMPLE_REMOVE:
          sendRemoveOperation(str, initialPeer);
          break;
        case SIMPLE_ADD_EDGE:
          Edge edge = new Edge(new Vertex(String.valueOf(vId)), new Vertex(String.valueOf(uId)));
          sendAddEdgeOperation(edge, initialPeer);
          break;
        case SIMPLE_REMOVE_EDGE:
          Edge edge1 = new Edge(new Vertex(String.valueOf(vId)), new Vertex(String.valueOf(uId)));
          sendRemoveEdgeOperation(edge1, initialPeer);
          break;
        case SIMPLE_ADD_VERTEX:
          Vertex v = new Vertex(String.valueOf(id));
          sendAddVertexOperation(v, initialPeer);
          break;
        case SIMPLE_REMOVE_VERTEX:
          Vertex u = new Vertex(String.valueOf(id));
          sendRemoveVertexOperation(u, initialPeer);
          break;
        default:
          LOG.info("The desired ScenarioType {} is not yet supported. Do submit a complaint if it bothers you.", type);
      }
    }
  };

  //TODO: A gentle warning to anyone sensitive to non-DRY code (this includes myself)
  private void sendAddOperation(Object element, KAddress initialPeer) {
    Add addOperation = new Add(element);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, addOperation);
    LOG.info("Operation {} will be triggered on {}", addOperation, initialPeer);
    trigger(msg, net);
  }

  private void sendRemoveOperation(Object element, KAddress initialPeer) {
    Remove removeOperation = new Remove(element);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, removeOperation);
    LOG.info("Operation {} will be triggered on {}", removeOperation, initialPeer);
    trigger(msg, net);
  }

  private void sendAddEdgeOperation(Edge edge, KAddress initialPeer) {
    AddEdge addOperation = new AddEdge(edge);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, addOperation);
    LOG.info("Operation {} will be triggered on {}", addOperation, initialPeer);
    trigger(msg, net);
  }

  private void sendRemoveEdgeOperation(Edge edge, KAddress initialPeer) {
    RemoveEdge removeOperation = new RemoveEdge(edge);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, removeOperation);
    LOG.info("Operation {} will be triggered on {}", removeOperation, initialPeer);
    trigger(msg, net);
  }

  private void sendAddVertexOperation(Vertex vertex, KAddress initialPeer) {
    AddVertex addOperation = new AddVertex(vertex);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, addOperation);
    LOG.info("Operation {} will be triggered on {}", addOperation, initialPeer);
    trigger(msg, net);
  }

  private void sendRemoveVertexOperation(Vertex vertex, KAddress initialPeer) {
    RemoveVertex removeOperation = new RemoveVertex(vertex);
    KHeader header = new BasicHeader(selfAdr, initialPeer, Transport.UDP);
    KContentMsg msg = new BasicContentMsg(header, removeOperation);
    LOG.info("Operation {} will be triggered on {}", removeOperation, initialPeer);
    trigger(msg, net);
  }

  public static class Init extends se.sics.kompics.Init<SimClientInfrastructure> {

    public final KAddress selfAdr;
    public SetsScenarioType type;
    public int id;

    public int vId;
    public int uId;

    public Init(KAddress selfAdr, SetsScenarioType type) {
      this.selfAdr = selfAdr;
      this.type = type;
    }

    public Init(KAddress selfAdr, SetsScenarioType type, int id) {
      this.selfAdr = selfAdr;
      this.type = type;
      this.id = id;
    }

    public Init(KAddress selfAdr, SetsScenarioType type, int vId, int uId) {
      this.selfAdr = selfAdr;
      this.type = type;
      this.vId = vId;
      this.uId = uId;
    }

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }
}
