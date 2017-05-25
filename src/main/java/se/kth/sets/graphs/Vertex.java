package se.kth.sets.graphs;

public class Vertex {
  String id;

  public Vertex(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Vertex && obj != null) {
      return ((Vertex) obj).id.equals(this.id);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Integer.parseInt(id);
  }

  @Override
  public String toString() {
    return "{vertex, " + id + "}";
  }
}
