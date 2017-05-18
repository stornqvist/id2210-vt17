/*
 * 2016 Royal Institute of Technology (KTH)
 *
 * LSelector is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.test.Ping;
import se.kth.app.test.Pong;
import se.kth.app.test.TestMsg;
import se.kth.broadcast.Broadcast;
import se.kth.broadcast.CausalOrderReliableBroadcastPort;
import se.kth.broadcast.Deliver;
import se.kth.sets.Add;
import se.kth.sets.GSet;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;

import java.util.Set;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class AppComp extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(AppComp.class);
  private String logPrefix = " ";
  private Set set;

  //*******************************CONNECTIONS********************************
  Positive<Timer> timer = requires(Timer.class);
  Positive<Network> net = requires(Network.class);
  Positive<CausalOrderReliableBroadcastPort> corb = requires(CausalOrderReliableBroadcastPort.class);
  //**************************************************************************
  private KAddress selfAdr;

  public AppComp(Init init) {
    selfAdr = init.selfAdr;
    set = new GSet();
    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    subscribe(handleStart, control);
    subscribe(handlePing, net);
    subscribe(handlePong, net);
    subscribe(handleCRBDeliver, corb);
    subscribe(handleTestMsg, net);
    //subscribe(handleAddOperation, net);
  }

  Handler handleStart = new Handler<Start>() {
    @Override
    public void handle(Start event) {
      LOG.info("{}starting...", logPrefix);
    }
  };

  ClassMatchedHandler handleTestMsg = new ClassMatchedHandler<TestMsg, KContentMsg<?, KHeader<?>, TestMsg>>() {

    @Override
    public void handle(TestMsg msg, KContentMsg<?, KHeader<?>, TestMsg> container) {
        LOG.debug("{}received TestMsg from {}, issuing broadcast", logPrefix, container.getHeader().getSource());
        trigger(new Broadcast(msg), corb);
    }
  };



  Handler handleCRBDeliver = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      LOG.debug("{} received {} from {}", logPrefix, deliver.payload, deliver.src);
    }
  };

  ClassMatchedHandler handlePing
    = new ClassMatchedHandler<Ping, KContentMsg<?, ?, Ping>>() {

      @Override
      public void handle(Ping content, KContentMsg<?, ?, Ping> container) {
        LOG.debug("{}received ping from:{}", logPrefix, container.getHeader().getSource());
        trigger(container.answer(new Pong()), net);
      }
    };

  ClassMatchedHandler handlePong
    = new ClassMatchedHandler<Pong, KContentMsg<?, KHeader<?>, Pong>>() {

      @Override
      public void handle(Pong content, KContentMsg<?, KHeader<?>, Pong> container) {
        LOG.debug("{}received pong from:{}", logPrefix, container.getHeader().getSource());
      }
    };

  public static class Init extends se.sics.kompics.Init<AppComp> {

    public final KAddress selfAdr;
    public final Identifier gradientOId;

    public Init(KAddress selfAdr, Identifier gradientOId) {
      this.selfAdr = selfAdr;
      this.gradientOId = gradientOId;
    }
  }
}
