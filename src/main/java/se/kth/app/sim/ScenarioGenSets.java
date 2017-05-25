package se.kth.app.sim;

import se.kth.app.test.SetsScenarioType;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.ktoolbox.util.network.KAddress;


import java.util.HashMap;
import java.util.Map;

public class ScenarioGenSets extends ScenarioGen {


  static Operation2<StartNodeEvent, Integer, Integer> startSimClientSetsOp = new Operation2<StartNodeEvent, Integer, Integer>() {

    @Override
    public StartNodeEvent generate(final Integer nodeId, final Integer scenarioType) {
      return new StartNodeEvent() {
        KAddress selfAdr;
        SetsScenarioType type;

        {
          String nodeIp = "193.0.1." + nodeId;
          type = SetsScenarioType.valueOf(scenarioType);
          selfAdr = ScenarioSetup.getNodeAdr(nodeIp, nodeId);
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        @Override
        public Class getComponentDefinition() {
          return SimClientSets.class;
        }

        @Override
        public SimClientSets.Init getComponentInit() {
          return new SimClientSets.Init(selfAdr, type);
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

  public static SimulationScenario simpleGSetAddScenario() {
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
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(10, startPeers);
        terminateAfterTerminationOf(5000, startAddClient);
      }
    };

    return scen;
  }

  public static SimulationScenario simple2PSetAddScenario() {
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 2));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(10, startPeers);
        terminateAfterTerminationOf(5000, startAddClient);
      }
    };

    return scen;
  }

  public static SimulationScenario simple2PSetAddRemoveScenario() {
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 2));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(10, startPeers);
        startRemoveClient.startAfterTerminationOf(100, startAddClient);
        terminateAfterTerminationOf(5000, startAddClient);
      }
    };

    return scen;
  }

  public static SimulationScenario simpleOrSetAddScenario() {
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 3));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(10, startPeers);
        terminateAfterTerminationOf(5000, startAddClient);
      }
    };

    return scen;
  }

  public static SimulationScenario simpleOrSetAddRemoveScenario() {
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 3));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(10, startPeers);
        startRemoveClient.startAfterTerminationOf(100, startAddClient);
        terminateAfterTerminationOf(5000, startAddClient);
      }
    };

    return scen;
  }
}