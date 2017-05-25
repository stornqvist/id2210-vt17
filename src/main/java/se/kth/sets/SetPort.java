package se.kth.sets;

import se.sics.kompics.PortType;

public class SetPort extends PortType {
  {
    indication(Response.class);
    request(Add.class);
    request(Remove.class);
    request(Lookup.class);
  }
}
