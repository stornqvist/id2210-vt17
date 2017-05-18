package se.kth.app.test;

import se.sics.kompics.KompicsEvent;

public class TestMsg implements KompicsEvent {

  private String msg;

  public TestMsg(String msg) {
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
}
