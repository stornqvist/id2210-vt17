package se.kth.sets;

import se.sics.kompics.KompicsEvent;

public class Response implements KompicsEvent {

  final boolean bool;

  public Response(boolean bool) {
    this.bool = bool;
  }
}
