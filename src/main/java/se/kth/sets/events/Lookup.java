package se.kth.sets.events;

public class Lookup extends Operation {

  public Lookup(Object element) {
    this.element = element;
  }

  @Override
  public String toString() {
    return "{lookup, " + element.toString() + "}";
  }
}
