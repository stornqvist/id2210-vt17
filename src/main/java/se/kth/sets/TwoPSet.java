package se.kth.sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.Positive;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;
import java.util.Set;

public class TwoPSet extends SuperSet {

  Set set;
  Set tombstoneSet;

  private static final Logger LOG = LoggerFactory.getLogger(TwoPSet.class);
  private String logPrefix = " ";

  //Positive<SetPort> gSetPort = requires(SetPort.class);
  //Positive<SetPort> tombstoneSetPort = requires(SetPort.class);

  //private Component set;
  //private Component tombstoneSet;

  public TwoPSet(Init init) {
    super();
    set = new HashSet();
    tombstoneSet = new HashSet();

    logPrefix = "<nid:" + init.selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);
    //this.set = create(GSet.class, new GSet.Init(init.selfAdr));
    //this.tombstoneSet = create(GSet.class, new GSet.Init(init.selfAdr));

    //connect(set.getNegative(SetPort.class), gSetPort, Channel.TWO_WAY);
    //connect(tombstoneSet.getNegative(SetPort.class), tombstoneSetPort, Channel.TWO_WAY);
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
