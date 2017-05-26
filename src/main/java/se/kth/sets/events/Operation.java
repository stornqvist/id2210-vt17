package se.kth.sets.events;

import se.sics.kompics.KompicsEvent;

public abstract class Operation implements KompicsEvent {

  Object element;

  public Object getElement() {
    return element;
  }
}
