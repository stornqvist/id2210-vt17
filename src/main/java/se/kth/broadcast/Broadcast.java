package se.kth.broadcast;

import se.sics.kompics.KompicsEvent;

import java.util.List;

public class Broadcast implements KompicsEvent {
  final KompicsEvent payload;

  public Broadcast(KompicsEvent payload) {
        this.payload = payload;
    }
}
