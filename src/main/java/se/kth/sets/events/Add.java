package se.kth.sets.events;

public class Add extends Operation {

  public Add(Object element) {
    this.element = element;
  }

  @Override
  public String toString() {
    return "{add, " + element.toString() + "}";
  }
}
