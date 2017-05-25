package se.kth.sets;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.broadcast.Broadcast;
import se.kth.broadcast.CausalOrderReliableBroadcastPort;
import se.kth.broadcast.Deliver;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.Set;

public class TwoPTwoPGraph extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(TwoPTwoPGraph.class);

  Positive<CausalOrderReliableBroadcastPort> corbPort = requires(CausalOrderReliableBroadcastPort.class);
  Negative<SetPort> setPort = provides(SetPort.class);

  Set<Vertex> va, vr;
  Set<Edge> ea, er;

  public TwoPTwoPGraph() {
    va = new HashSet();
    vr = new HashSet();
    ea = new HashSet();
    er = new HashSet();

    subscribe(handleAddOperation, setPort);
    subscribe(handleRemoveOperation, setPort);
    subscribe(handleLookupOperation, setPort);
    subscribe(handleDeliver, corbPort);
    //TODO: Copy some stuff from SuperSet
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
      for(Edge edge : Sets.difference(ea, er)) {
        if(!edge.u.equals(vertex) && !edge.v.equals(vertex)) {
          //TODO: Remove edge
          vr.add(vertex);
        }
      }
    }
  }

  public void removeVertex(Vertex vertex) {
    if (va.contains(vertex) && !vr.contains(vertex)) {
      vr.add(vertex);
    }
  }

  public void removeEdgeAtSource(Edge edge) {
    if (lookup(edge)) {
      er.add(edge);
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
      //This looks ridiculous
      if(deliver.payload instanceof AddEdge) {
        addEdge((Edge) deliver.payload);
      } else if (deliver.payload instanceof AddVertex) {
        addVertex((Vertex) deliver.payload);
      } else if(deliver.payload instanceof RemoveEdge) {
        removeEdge((Edge) deliver.payload);
      } else if(deliver.payload instanceof RemoveVertex) {
        removeVertex((Vertex) deliver.payload);
      }else if(deliver.payload instanceof LookupEdge) {
        lookup((Edge) deliver.payload);
      } else if(deliver.payload instanceof LookupVertex) {
        lookup((Vertex) deliver.payload);
      } else {
        LOG.info("No matching type (type is {}). Something must have went wrong.", deliver.payload);
      }
    }
  };

  public static class Init extends se.sics.kompics.Init<TwoPTwoPGraph> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }
}

class Vertex {
  String id;

  public Vertex(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vertex && obj != null) {
      return ((Vertex) obj).id.equals(this.id);
    }
    return false;
  }
}

class Edge {

  Vertex v;
  Vertex u;

  public Edge(Vertex v, Vertex u) {
    this.v = v;
    this.u = u;
  }
}

class AddEdge extends Add {

  public AddEdge(Edge edge) {
    super(edge);
  }
}

class AddVertex extends Add {

  public AddVertex(Vertex vertex) {
    super(vertex);
  }
}

class RemoveEdge extends Remove {

  public RemoveEdge(Edge edge) {
    super(edge);
  }
}

class RemoveVertex extends Remove {

  public RemoveVertex(Vertex vertex) {
    super(vertex);
  }
}

class LookupEdge extends Lookup {
  public LookupEdge(Edge edge) {
    super(edge);
  }
}

class LookupVertex extends Lookup {
  public LookupVertex(Edge edge) {
    super(edge);
  }
}