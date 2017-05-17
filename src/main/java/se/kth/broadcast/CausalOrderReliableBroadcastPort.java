package se.kth.broadcast;

import se.sics.kompics.PortType;

public class CausalOrderReliableBroadcastPort extends PortType {
    {
        indication(Deliver.class);
        request(Broadcast.class);
    }
}