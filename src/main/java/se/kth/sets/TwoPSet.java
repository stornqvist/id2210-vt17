package se.kth.sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.Set;

public class TwoPSet extends SuperSet {

  Set set;
  Set tombstoneSet;

  private static final Logger LOG = LoggerFactory.getLogger(TwoPSet.class);
  private String logPrefix = " ";

  public TwoPSet(Init init) {
    super();
    set = new HashSet();
    tombstoneSet = new HashSet();

    logPrefix = "<nid:" + init.selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);
  }

  @Override
  public boolean add(Operation operation) {
    //TODO: This results in some uneccassary double checking
    if(!set.contains(operation.getElement()) && !tombstoneSet.contains(operation.getElement())){
      LOG.info("{} A new element {} was added", logPrefix, operation.getElement().toString());
      set.add(operation);
      broadcast(operation);
      return true;
    }
    return false;
  }

  @Override
  public boolean remove(Remove operation) {
    if (set.contains(operation)) {
      LOG.info("{} An element {} was removed", logPrefix, operation.getElement().toString());
      tombstoneSet.add(operation); //TODO: Adding here will already broadcast the operation (not any longer though)
      broadcast(operation);
      return true;
    }
    return false;
  }

  public static class Init extends se.sics.kompics.Init<TwoPSet> {

    public final KAddress selfAdr;

    public Init(KAddress selfAdr) {
      this.selfAdr = selfAdr;
    }
  }
}
