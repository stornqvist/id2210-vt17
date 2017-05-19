package se.kth.broadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

public class Deliver implements KompicsEvent {
    public final KAddress src;
    public final KompicsEvent payload;

    public Deliver(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Deliver) {
            Deliver deliver = (Deliver) obj;
            return deliver.payload.equals(this.payload);
        } else {
            return super.equals(obj);
        }
    }
}
