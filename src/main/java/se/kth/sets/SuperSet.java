package se.kth.sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.broadcast.Broadcast;
import se.kth.broadcast.CausalOrderReliableBroadcastPort;
import se.kth.broadcast.Deliver;
import se.kth.sets.events.Add;
import se.kth.sets.events.Lookup;
import se.kth.sets.events.Operation;
import se.kth.sets.events.Remove;
import se.sics.kompics.*;

public abstract class SuperSet extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(SuperSet.class);

  Positive<CausalOrderReliableBroadcastPort> corbPort = requires(CausalOrderReliableBroadcastPort.class);
  Negative<SetPort> setPort = provides(SetPort.class);

  public SuperSet() {
    subscribe(handleAddOperation, setPort);
    subscribe(handleRemoveOperation, setPort);
    subscribe(handleLookupOperation, setPort);
    subscribe(handleDeliver, corbPort);
  }

  void broadcast(KompicsEvent event) {
    trigger(new Broadcast(event), corbPort);
  }

  public boolean add(Operation operation) {
    // TODO: Improve, please
    LOG.warn("A wild guess but I might have ended up here");
    return true;
  }

  public boolean addAtSrc(Add operation) {
    return add(operation);
  }

  public boolean removeAtSrc(Remove remove) {
    return remove(remove);
  }

  public boolean remove(Remove remove) {
    LOG.warn("Please I do not want to see this comment in the logs");
    // TODO: Improve, please. Reconsider an interface for the operations.
    return true;
  }

  public boolean lookup(Lookup lookup) {
    // TODO: Improve, please.
    return true;
  }

  //TODO: This will probably work but looks ugly af, maybe change to classMatchedHandler...
  Handler handleAddOperation = new Handler<Add>() {
    @Override
    public void handle(Add add) {
      addAtSrc(add);
    }
  };

  Handler handleRemoveOperation = new Handler<Remove>() {
    @Override
    public void handle(Remove remove) {
      removeAtSrc(remove);
    }
  };

  Handler handleLookupOperation = new Handler<Lookup>() {
    @Override
    public void handle(Lookup lookup) {
      lookup(lookup);
    }
  };

  Handler handleDeliver = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      if(deliver.payload instanceof Add) {
        add((Add) deliver.payload);
      } else if(deliver.payload instanceof Remove) {
        remove((Remove) deliver.payload);
      } else if(deliver.payload instanceof Lookup) {
        lookup((Lookup) deliver.payload);
      } else {
        LOG.debug("No matching type (type is {}). Something must have went wrong.", deliver.payload);
      }
    }
  };
}
