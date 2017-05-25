package se.kth.sets;

import se.sics.kompics.PortType;

/**
 * Created by te27 on 2017-05-19.
 */
public class SetPort extends PortType {
  {
    //indication(); //TODO: Shouldn't it indicate something?
    request(Add.class);
    request(Remove.class);
    request(Lookup.class);
  }
}
