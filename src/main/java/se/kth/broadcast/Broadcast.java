package se.kth.broadcast;

import se.sics.kompics.KompicsEvent;

import java.util.List;

/**
 * Created by te27 on 2017-04-12.
 */
public class Broadcast implements KompicsEvent {
    KompicsEvent payload;

    public Broadcast(KompicsEvent payload) {
        this.payload = payload;
    }
}
