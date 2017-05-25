package se.kth.sets;

import se.sics.kompics.KompicsEvent;

/**
 * Created by te27 on 2017-05-21.
 */
public abstract class Operation implements KompicsEvent {

  Object element;

  public Object getElement() {
    return element;
  }
}
