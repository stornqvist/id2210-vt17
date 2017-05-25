package se.kth.sets.graphs;

public class Edge {

  Vertex v;
  Vertex u;

  public Edge(Vertex v, Vertex u) {
    this.v = v;
    this.u = u;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Edge && obj != null) {
      Edge edge = (Edge) obj;
      return (edge.u.equals(this.u) && edge.v.equals(this.v)) || (edge.u.equals(this.v) && edge.v.equals(this.u));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (Integer.parseInt(v.id) + Integer.parseInt(u.id));
  }

  @Override
  public String toString() {
    return "{edge, " + v.toString() + ", " + u.toString() + "}";
  }
}
