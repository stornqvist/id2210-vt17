package se.kth.sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.sets.events.Add;
import se.kth.sets.events.Lookup;
import se.kth.sets.events.Operation;
import se.kth.sets.events.Remove;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OrSet extends SuperSet {

  private static final Logger LOG = LoggerFactory.getLogger(OrSet.class);
  private String logPrefix = " ";
  private Set<Pair> set;

  public OrSet(Init init) {
    super();
    logPrefix = "<nid:" + init.selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);
    this.set = new HashSet<>();
  }

  @Override
  public boolean add(Operation operation) {
    if (operation instanceof OR_Add) {
      LOG.info("{} {} was added", logPrefix, operation);
      OR_Add add = (OR_Add) operation;
      return set.add(new Pair(add.uuid, add.getElement()));
    } else {
      // TODO: Something went wrong, do something
      return false;
    }
  }

  @Override
  public boolean addAtSrc(Add operation) {
    LOG.info("{} {} was added at src", logPrefix, operation);
    Pair pair = new Pair(UUID.randomUUID(), operation.getElement());
    set.add(pair);
    broadcast(new OR_Add(pair.element, pair.uuid));
    return true;
  }

  @Override
  public boolean remove(Remove operation) {
    LOG.info("{} {} is maybe being removed", logPrefix, operation.getElement());
    if (operation instanceof OR_Remove) {
      LOG.info("{} {} is being removed", logPrefix, operation.getElement());
      OR_Remove remove = (OR_Remove) operation;
      for (UUID uuid : remove.uuid) {
        set.remove(new Pair(uuid, remove.getElement()));
      }
      return true;
    }
    else {
      // TODO: Something went wrong, fix it
      return false;
    }
  }

  @Override
  public boolean removeAtSrc(Remove remove) {
    LOG.info("{} was ordered to execute remove {}", logPrefix, remove);
    LOG.info("{} is the result of performing lookup on {}", lookup(new Lookup(remove.getElement())), remove.getElement());
    LOG.info("{} is the inälvor of OrSet", this.toString());
    if(lookup(new Lookup(remove.getElement()))){
      LOG.info("{} {} was removed at src", logPrefix, remove.getElement());
      Set<UUID> rmUUID = new HashSet();
      for (Pair pair : set) {
        if (pair.element.equals(remove.getElement())) {
          set.remove(pair);
          rmUUID.add(pair.uuid);
        }
      }
      StringBuilder sb = new StringBuilder();
      for(UUID id : rmUUID) {
        sb.append(id.toString());
        sb.append(", ");
      }

      LOG.info("{} {} are the UUID that are to be removed", logPrefix, sb.toString());
      broadcast(new OR_Remove(remove.getElement(), rmUUID));
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean lookup(Lookup lookup) {
    for (Pair pair : set) {
      if (pair.element.equals(lookup.getElement())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Pair pair : set) {
      sb.append(pair.toString());
      sb.append(", ");
    }
    return sb.toString();
  }

  public static class Init extends se.sics.kompics.Init<OrSet> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }
}

class OR_Add extends Add {
  UUID uuid;

  public OR_Add(Object element, UUID uuid) {
    super(element);
    this.uuid = uuid;
  }
}

class OR_Remove extends Remove {
  Set <UUID> uuid;

  public OR_Remove(Object element, Set uuid) {
    super(element);
    this.uuid = uuid;
  }
}

class Pair {

  UUID uuid;
  Object element;

  public Pair(UUID uuid, Object element) {
    this.uuid = uuid;
    this.element = element;
  }

  @Override
  public String toString() {
    return ("{" + uuid.toString() + ", " + element.toString() + "}");
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Pair) {
      return (((Pair) obj).element.equals(this.element) && ((Pair) obj).uuid.equals(this.uuid));
    } else {
      return false;
    }
  }
}
