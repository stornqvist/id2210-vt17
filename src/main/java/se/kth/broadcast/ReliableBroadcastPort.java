package se.kth.broadcast;

import se.sics.kompics.PortType;

public class ReliableBroadcastPort extends PortType {
  {
    indication(RB_Deliver.class);
    request(RB_Broadcast.class);
  }
}
