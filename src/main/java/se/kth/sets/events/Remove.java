package se.kth.sets.events;

public class Remove extends Operation {

  public Remove(Object element) {
    this.element = element;
  }

  @Override
  public String toString() {
    return "{remove, " + element.toString() + "}";
  }
}
