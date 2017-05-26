package se.kth.sets;

import se.kth.sets.events.Add;
import se.kth.sets.events.Lookup;
import se.kth.sets.events.Remove;
import se.kth.sets.events.Response;
import se.sics.kompics.PortType;

public class SetPort extends PortType {
  {
    indication(Response.class);
    request(Add.class);
    request(Remove.class);
    request(Lookup.class);
  }
}
