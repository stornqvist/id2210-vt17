package se.kth.app.test;

// Each type is associated with an integer, Kompics was complaining
public enum SetsScenarioType {
  SIMPLE_ADD(1), SIMPLE_REMOVE(2), ADD_THEN_REMOVE(3),
  SIMPLE_ADD_EDGE(11), SIMPLE_REMOVE_EDGE(12), ADD_THEN_REMOVE_EDGE(13),
  SIMPLE_ADD_VERTEX(101), SIMPLE_REMOVE_VERTEX(102), ADD_THEN_REMOVE_VERTEX(103);
  public int valueRepresentation;

  private SetsScenarioType(int valueRepresentation) {
    this.valueRepresentation = valueRepresentation;
  }

  public static SetsScenarioType valueOf(int valueRepresentation) {
    for(SetsScenarioType sst : SetsScenarioType.values()) {
      if (sst.valueRepresentation == valueRepresentation) return sst;
    }
    throw new IllegalArgumentException("Corresponding scenario not found. Please try again!");
  }
}
