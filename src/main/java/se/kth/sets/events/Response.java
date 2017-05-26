package se.kth.sets.events;

import se.sics.kompics.KompicsEvent;

public class Response implements KompicsEvent {

  final boolean bool;

  public Response(boolean bool) {
    this.bool = bool;
  }
}
