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
package se.kth.app.sim;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.sets.SetType;
import se.kth.sim.compatibility.SimNodeIdExtractor;
import se.kth.system.HostMngrComp;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.network.identifier.IdentifierExtractor;
import se.sics.ktoolbox.omngr.bootstrap.BootstrapServerComp;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class ScenarioGen {

  static Logger LOG = LoggerFactory.getLogger(ScenarioGen.class);

  static Operation<SetupEvent> systemSetupOp = new Operation<SetupEvent>() {
    @Override
    public SetupEvent generate() {
      return new SetupEvent() {
        @Override
        public IdentifierExtractor getIdentifierExtractor() {
          return new SimNodeIdExtractor();
        }
      };
    }
  };

  static Operation<StartNodeEvent> startBootstrapServerOp = new Operation<StartNodeEvent>() {

    @Override
    public StartNodeEvent generate() {
      return new StartNodeEvent() {
        KAddress selfAdr;

        {
          selfAdr = ScenarioSetup.bootstrapServer;
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        @Override
        public Class getComponentDefinition() {
          return BootstrapServerComp.class;
        }

        @Override
        public BootstrapServerComp.Init getComponentInit() {
          return new BootstrapServerComp.Init(selfAdr);
        }
      };
    }
  };

  static Operation1<StartNodeEvent, Integer> startNodeOp = new Operation1<StartNodeEvent, Integer>() {

    @Override
    public StartNodeEvent generate(final Integer nodeId) {
      return new StartNodeEvent() {
        KAddress selfAdr;

        {
          String nodeIp = "193.0.0." + nodeId;
          selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeId);
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        @Override
        public Class getComponentDefinition() {
          return HostMngrComp.class;
        }

        @Override
        public HostMngrComp.Init getComponentInit() {
          return new HostMngrComp.Init(selfAdr, ScenarioSetup.bootstrapServer, ScenarioSetup.croupierOId);
        }

        @Override
        public Map<String, Object> initConfigUpdate() {
          Map<String, Object> nodeConfig = new HashMap<>();
          nodeConfig.put("system.id", nodeId);
          nodeConfig.put("system.seed", ScenarioSetup.getNodeSeed(nodeId));
          nodeConfig.put("system.port", ScenarioSetup.appPort);
          return nodeConfig;
        }
      };
    }
  };

  static Operation2<StartNodeEvent, Integer, Integer> startNode2Op = new Operation2<StartNodeEvent, Integer, Integer>() {

    @Override
    public StartNodeEvent generate(final Integer nodeId, final Integer setId) {
      return new StartNodeEvent() {
        KAddress selfAdr;
        SetType setType;
        {
          String nodeIp = "193.0.0." + nodeId;
          setType = SetType.valueOf(setId);
          selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeId);
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        @Override
        public Class getComponentDefinition() {
          return HostMngrComp.class;
        }

        @Override
        public HostMngrComp.Init getComponentInit() {
          return new HostMngrComp.Init(selfAdr, ScenarioSetup.bootstrapServer, ScenarioSetup.croupierOId, setType);
        }

        @Override
        public Map<String, Object> initConfigUpdate() {
          Map<String, Object> nodeConfig = new HashMap<>();
          nodeConfig.put("system.id", nodeId);
          nodeConfig.put("system.seed", ScenarioSetup.getNodeSeed(nodeId));
          nodeConfig.put("system.port", ScenarioSetup.appPort);
          return nodeConfig;
        }
      };
    }
  };

  static Operation1<KillNodeEvent, Integer> killNodeOp = new Operation1<KillNodeEvent, Integer>() {

    @Override
    public KillNodeEvent generate(final Integer nodeId) {
      return new KillNodeEvent() {
        KAddress selfAdr;

        {
          String nodeIp = "193.0.0." + nodeId;
          selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeId);
          LOG.info("Node {} was killed", nodeId);
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }
      };
    }
  };

  static Operation1<StartNodeEvent, Integer> startSimClientOp = new Operation1<StartNodeEvent, Integer>() {

    @Override
    public StartNodeEvent generate(final Integer nodeId) {
      return new StartNodeEvent() {
        KAddress selfAdr;

        {
          String nodeIp = "193.0.1." + nodeId;
          selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeId);
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        @Override
        public Class getComponentDefinition() {
          return SimClientInfrastructure.class;
        }

        @Override
        public SimClientInfrastructure.Init getComponentInit() {
          return new SimClientInfrastructure.Init(selfAdr);
        }

        @Override
        public Map<String, Object> initConfigUpdate() {
          Map<String, Object> nodeConfig = new HashMap<>();
          nodeConfig.put("system.id", nodeId);
          nodeConfig.put("system.seed", ScenarioSetup.getNodeSeed(nodeId));
          nodeConfig.put("system.port", ScenarioSetup.appPort);
          return nodeConfig;
        }
      };
    }
  };

  public static SimulationScenario simpleSim() {
    SimulationScenario scen = new SimulationScenario() {
      {
        StochasticProcess systemSetup = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(1, systemSetupOp);
          }
        };
        StochasticProcess startBootstrapServer = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(1, startBootstrapServerOp);
          }
        };
        StochasticProcess startPeers = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(10, startNodeOp, new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startSimClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientOp, new BasicIntSequentialDistribution(1));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startSimClient.startAfterTerminationOf(10, startPeers);
        terminateAfterTerminationOf(5000, startSimClient);
      }
    };

    return scen;
  }

  public static SimulationScenario simpleKillScenario() {
    SimulationScenario scen = new SimulationScenario() {
      {
        StochasticProcess systemSetup = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(1, systemSetupOp);
          }
        };
        StochasticProcess startBootstrapServer = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(1, startBootstrapServerOp);
          }
        };
        StochasticProcess startPeers = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(10, startNodeOp, new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startSimClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientOp, new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess killPeers = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, killNodeOp, new ConstantDistribution<Integer>(Integer.class, 2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startSimClient.startAfterTerminationOf(10, startPeers);
        killPeers.startAfterTerminationOf(5000, startSimClient);
        terminateAfterTerminationOf(5000, killPeers);
      }
    };

    return scen;
  }

  public static SimulationScenario simpleResurrectScenario() {
    SimulationScenario scen = new SimulationScenario() {
      {
        StochasticProcess systemSetup = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(1, systemSetupOp);
          }
        };
        StochasticProcess startBootstrapServer = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(1, startBootstrapServerOp);
          }
        };
        StochasticProcess startPeers = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(10, startNodeOp, new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startSimClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientOp, new BasicIntSequentialDistribution(2));
          }
        };
        StochasticProcess killPeers = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, killNodeOp, new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess resurrectPeer = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startNodeOp, new BasicIntSequentialDistribution(1));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startSimClient.startAfterTerminationOf(10, startPeers);
        killPeers.startAfterTerminationOf(1000, startSimClient);
        startSimClient.startAfterTerminationOf(1000, killPeers);
        resurrectPeer.startAfterTerminationOf(1000, startSimClient);
        terminateAfterTerminationOf(5000, resurrectPeer);
      }
    };

    return scen;
  }
}
