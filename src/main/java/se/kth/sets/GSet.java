package se.kth.sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.Set;

public class GSet extends SuperSet {

  private static final Logger LOG = LoggerFactory.getLogger(GSet.class);
  private String logPrefix = " ";
  Set<Object> set;

  public GSet(Init init) {
    super();
    this.set = new HashSet();
    logPrefix = "<nid:" + init.selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);
  }

  // Sets prevent duplicate elements
  @Override
  public boolean add(Operation operation) {
    if(set.add(operation.getElement())){
      LOG.info("{} A new element {} was added", logPrefix, operation.getElement().toString());
      broadcast(operation);
      return true;
    }
    return false;
  }

  @Override
  public boolean remove(Remove remove) {
    throw new UnsupportedOperationException("This operation is not allowed for Grow-Only sets");
  }

  public boolean contains(Object element) {
    return set.contains(element);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Object obj : set) {
      sb.append(obj.toString());
    }
    return sb.toString();
  }

  public static class Init extends se.sics.kompics.Init<GSet> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }
}
