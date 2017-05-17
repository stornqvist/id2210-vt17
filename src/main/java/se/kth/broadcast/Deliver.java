package se.kth.broadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class Deliver implements KompicsEvent {
    public KAddress src;
    public KompicsEvent payload;

    public Deliver(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }
}
