package se.kth.sets.graphs;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.broadcast.Broadcast;
import se.kth.broadcast.CausalOrderReliableBroadcastPort;
import se.kth.broadcast.Deliver;
import se.kth.sets.events.Add;
import se.kth.sets.events.Lookup;
import se.kth.sets.events.Remove;
import se.kth.sets.SetPort;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.Set;

public class TwoPTwoPGraph extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(TwoPTwoPGraph.class);
  private String logPrefix = " ";

  Positive<CausalOrderReliableBroadcastPort> corbPort = requires(CausalOrderReliableBroadcastPort.class);
  Negative<SetPort> setPort = provides(SetPort.class);

  Set<Vertex> va, vr;
  Set<Edge> ea, er;

  public TwoPTwoPGraph(Init init) {
    logPrefix = "<nid:" + init.selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    va = new HashSet();
    vr = new HashSet();
    ea = new HashSet();
    er = new HashSet();

    subscribe(handleAddOperation, setPort);
    subscribe(handleRemoveOperation, setPort);
    subscribe(handleLookupOperation, setPort);
    subscribe(handleDeliver, corbPort);
  }

  void broadcast(KompicsEvent event) {
    trigger(new Broadcast(event), corbPort);
  }

  public boolean lookup(Vertex vertex) {
    return Sets.difference(va, vr).contains(vertex);
  }

  public boolean lookup(Edge edge) {
    return (lookup(edge.u) && lookup(edge.v) && Sets.difference(ea, er).contains(edge));
  }

  public void addVertexAtSource(Vertex vertex) {
    //TODO: Nothing is happening here in the algorithm...
    va.add(vertex);
    broadcast(new AddVertex(vertex));
  }

  public void addVertex(Vertex vertex) {
    va.add(vertex);
  }

  public void addEdgeAtSource(Edge edge) {
    if(lookup(edge.u) && lookup(edge.v)) {
      broadcast(new AddEdge(edge));
    }
    ea.add(edge);
  }

  public void addEdge(Edge edge) {
    ea.add(edge);
  }

  public void removeVertexAtSource(Vertex vertex) {
    if(lookup(vertex)){
      // In case there aren't any edges
      if (Sets.difference(ea, er).isEmpty()) {
        vr.add(vertex);
        broadcast(new RemoveVertex(vertex));
        return;
      }
      for(Edge edge : Sets.difference(ea, er)) {
        if(!edge.u.equals(vertex) && !edge.v.equals(vertex)) {
          vr.add(vertex);
          broadcast(new RemoveVertex(vertex));

        // Giving precedence to RemoveVertex over AddEdge
        } else {
          removeEdge(edge);
          vr.add(vertex);
          broadcast(new RemoveVertex(vertex));
        }
      }
    }
  }

  public void removeVertex(Vertex vertex) {
    if (va.contains(vertex) && !vr.contains(vertex)) {
      vr.add(vertex);

      // Giving precedence to RemoveVertex over AddEdge
      for (Edge edge : Sets.difference(ea, er)) {
        if (edge.u.equals(vertex) || edge.v.equals(vertex)) {
          removeEdge(edge);
        }
      }
    }
  }

  public void removeEdgeAtSource(Edge edge) {
    if (lookup(edge)) {
      er.add(edge);
      broadcast(new RemoveEdge(edge));
    }
  }

  public void removeEdge(Edge edge) {
    if (ea.contains(edge) && !er.contains(edge)) {
      er.add(edge);
    }
  }

  //TODO: This looks slightly messy, if there is time, find a way to streamline this.
  Handler handleAddOperation = new Handler<Add>() {
    @Override
    public void handle(Add add) {
      if (add instanceof AddVertex) {
        addVertexAtSource((Vertex) add.getElement());
      } else if (add instanceof AddEdge) {
        addEdgeAtSource((Edge)  add.getElement());
      } else {
        LOG.info("Ouch! This is not good...");
      }
    }
  };

  Handler handleRemoveOperation = new Handler<Remove>() {
    @Override
    public void handle(Remove remove) {
      if (remove instanceof RemoveVertex) {
        removeVertexAtSource((Vertex) remove.getElement());
      } else if (remove instanceof RemoveEdge) {
        removeEdgeAtSource((Edge) remove.getElement());
      } else {
        LOG.info("Oh no! Time to fail.");
      }
    }
  };

  Handler handleLookupOperation = new Handler<Lookup>() {
    @Override
    public void handle(Lookup lookup) {
      if (lookup instanceof LookupVertex) {
        lookup((Vertex) lookup.getElement());
      } else if (lookup instanceof LookupEdge) {
        lookup((Edge) lookup.getElement());
      } else {
        LOG.info("Infinite deathloop initiated.");
      }
    }
  };

  Handler handleDeliver = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      //This looks even more ridiculous at this point
      if(deliver.payload instanceof AddEdge) {
        addEdge((Edge) ((AddEdge) deliver.payload).getElement());
      } else if (deliver.payload instanceof AddVertex) {
        addVertex((Vertex) ((AddVertex) deliver.payload).getElement());
      } else if(deliver.payload instanceof RemoveEdge) {
        removeEdge((Edge) ((RemoveEdge) deliver.payload).getElement());
      } else if(deliver.payload instanceof RemoveVertex) {
        removeVertex((Vertex) ((RemoveVertex) deliver.payload).getElement());
      }else if(deliver.payload instanceof LookupEdge) {
        lookup((Edge) ((LookupEdge) deliver.payload).getElement());
      } else if(deliver.payload instanceof LookupVertex) {
        lookup((Vertex) ((LookupVertex) deliver.payload).getElement());
      } else {
        LOG.debug("No matching type (type is {}). Something must have went wrong.", deliver.payload);
      }
      LOG.debug("{} After {} the graph now looks something like the following: {}",logPrefix, deliver.payload, print());
    }
  };

  public static class Init extends se.sics.kompics.Init<TwoPTwoPGraph> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }

  // I wonder why overriding toString() did not work
  public String print() {
    StringBuilder sb = new StringBuilder();

    sb.append("\n Alive vertices: [");
    for (Vertex vertex : va) {
      sb.append(vertex.toString());
    }
    sb.append("]");

    sb.append("\n Removed vertices: [");
    for (Vertex vertex : vr) {
      sb.append(vertex.toString());
    }
    sb.append("]");

    sb.append("\n Alive edges: [");
    for (Edge edge : ea) {
      sb.append(edge.toString());
    }
    sb.append("]");

    sb.append("\n Removed edges: [");
    for (Edge edge : er) {
      sb.append(edge.toString());
    }
    sb.append("]");

    return sb.toString();
  }
}

