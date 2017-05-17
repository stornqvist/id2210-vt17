package se.kth.broadcast;

import se.sics.kompics.PortType;

public class BestEffortBroadcastPort extends PortType {
  {
    indication(BEB_Deliver.class);
    request(BEB_Broadcast.class);
  }
}
