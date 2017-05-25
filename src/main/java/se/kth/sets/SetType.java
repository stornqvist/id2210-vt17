package se.kth.sets;

import se.kth.app.test.SetsScenarioType;

public enum SetType {
  G_SET(1), TWOP_SET(2), OR_SET(3), TWOP_TWOP_GRAPH(4);
  public int intValue;

  private SetType(int intValue) {
    this.intValue = intValue;
  }

  public static SetType valueOf(int value) {
    for(SetType st : SetType.values()) {
      if (st.intValue == value) return st;
    }
    throw new IllegalArgumentException("Corresponding set not found. Please try again!");
  }

}
