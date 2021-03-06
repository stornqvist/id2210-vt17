package se.kth.sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.sets.events.Add;
import se.kth.sets.events.Lookup;
import se.kth.sets.events.Operation;
import se.kth.sets.events.Remove;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.Iterator;
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
      //LOG.debug("{} operation {} is to be executed on {}", logPrefix, operation, toString());
      OR_Add add = (OR_Add) operation;
      set.add(new Pair(add.uuid, add.getElement()));
      LOG.debug("{} {} operation resulted in {}", logPrefix, operation, toString());
      return set.contains(new Pair(add.uuid, add.getElement()));
    } else {
      // TODO: Something went wrong, do something
      return false;
    }
  }

  @Override
  public boolean addAtSrc(Add operation) {
    //LOG.debug("{} operation {} is to be executed on {} at src", logPrefix, operation, toString());
    Pair pair = new Pair(UUID.randomUUID(), operation.getElement());
    set.add(pair);
    broadcast(new OR_Add(pair.element, pair.uuid));
    LOG.debug("{} {} operation resulted in {}", logPrefix, operation, toString());
    return true;
  }

  @Override
  public boolean remove(Remove operation) {
    if (operation instanceof OR_Remove) {
      //LOG.debug("{} operation {} is to be executed on {}", logPrefix, operation, toString());
      OR_Remove remove = (OR_Remove) operation;
      for (UUID uuid : remove.uuid) {
        set.remove(new Pair(uuid, remove.getElement()));
      }
      LOG.debug("{} {} operation resulted in {}", logPrefix, remove, toString());
      return true;
    }
    else {
      // TODO: Something went wrong, fix it
      return false;
    }
  }

  @Override
  public boolean removeAtSrc(Remove remove) {
    //LOG.debug("{} was ordered to execute remove {} of {} on {}", logPrefix, remove, remove.getElement(), toString());
    if(lookup(new Lookup(remove.getElement()))){
      Set<UUID> rmUUID = new HashSet();

      Iterator<Pair> iterator = set.iterator();
      while (iterator.hasNext()) {
        Pair pair = iterator.next();
        if (pair.element.equals(remove.getElement())) {
          rmUUID.add(pair.uuid);
          iterator.remove();
        }
      }
      broadcast(new OR_Remove(remove.getElement(), rmUUID));
      LOG.debug("{} {} operation resulted in {}", logPrefix, remove, toString());
      return true;
    } else {
      LOG.debug("{} could not find the item {} that was to be removed", logPrefix, remove.getElement());
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
    sb.append("{");
    for (Pair pair : set) {
      sb.append(pair.toString());
      sb.append(", ");
    }
    if (!set.isEmpty()) sb.delete(sb.length() - 2, sb.length());
    sb.append("}");
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

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
