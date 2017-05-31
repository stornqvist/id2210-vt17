package se.kth.app.sim;

import se.kth.app.test.SetsScenarioType;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.Operation3;
import se.sics.kompics.simulator.adaptor.Operation4;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;
import java.util.Map;

//TODO: This class contains A LOT of duplicate code

public class ScenarioGenSets extends ScenarioGen {

  static Operation2<StartNodeEvent, Integer, Integer> startSimClientSetsOp =
          new Operation2<StartNodeEvent, Integer, Integer>() {

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

  static Operation3<StartNodeEvent, Integer, Integer, Integer> startSimClientSetsWithIdOp =
          new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

    @Override
    public StartNodeEvent generate(final Integer nodeId, final Integer scenarioType, final Integer elementId) {
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
          return new SimClientSets.Init(selfAdr, type, elementId);
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

  static Operation4<StartNodeEvent, Integer, Integer, Integer, Integer> startSimClientEdgeOp =
          new Operation4<StartNodeEvent, Integer, Integer, Integer, Integer>() {

            @Override
            public StartNodeEvent generate(final Integer nodeId, final Integer scenarioType, final Integer uId, final Integer vId) {
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
                  return new SimClientSets.Init(selfAdr, type, uId, vId);
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
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1),
                    new BasicIntSequentialDistribution(2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(10, startPeers);
        startAddClient.startAfterTerminationOf(2000, startPeers);
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1),
                    new ConstantDistribution(Integer.class, 2));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 2));
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1),
                    new ConstantDistribution(Integer.class, 2));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new ConstantDistribution(Integer.class, 1),
                new ConstantDistribution(Integer.class, 1), new BasicIntSequentialDistribution(27));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 2), new BasicIntSequentialDistribution(27));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(1000, startPeers);
        startRemoveClient.startAfterTerminationOf(2000, startAddClient);
        startAddClient.startAfterTerminationOf(2000, startRemoveClient);
        terminateAfterTerminationOf(10000, startRemoveClient);
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
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 2));
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
        StochasticProcess startAddClients = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(2, startSimClientSetsOp, new ConstantDistribution(Integer.class, 1), new ConstantDistribution(Integer.class, 1));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(5), new ConstantDistribution(Integer.class, 1));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsOp, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 2));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClients.startAfterTerminationOf(1000, startPeers);
        startRemoveClient.startAfterTerminationOf(3000, startAddClients);
        startAddClient.startAfterStartOf(0, startRemoveClient);
        terminateAfterTerminationOf(5000, startAddClient);
      }
    };

    return scen;
  }

  public static SimulationScenario simple2P2PGraphAddVertexScenario() {
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 4));
          }
        };
        StochasticProcess startAddVertexClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 101), new BasicIntSequentialDistribution(27));
          }
        };
        StochasticProcess startRemoveVertexClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 102), new BasicIntSequentialDistribution(27));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddVertexClient.startAfterTerminationOf(10, startPeers);
        terminateAfterTerminationOf(5000, startAddVertexClient);
      }
    };

    return scen;
  }

  public static SimulationScenario simple2P2PGraphAddRemoveVertexScenario() {
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
            raise(10, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 4));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 101), new BasicIntSequentialDistribution(27));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 102), new BasicIntSequentialDistribution(27));
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

  public static SimulationScenario intermediate2P2PGraphAddRemoveVertexScenario() {
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
            raise(5, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 4));
          }
        };
        StochasticProcess startAddClient = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(10));
            raise(3, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 101), new BasicIntSequentialDistribution(27));
          }
        };
        StochasticProcess startRemoveClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                new ConstantDistribution(Integer.class, 102), new BasicIntSequentialDistribution(27));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddClient.startAfterTerminationOf(10, startPeers);
        startRemoveClient.startAfterTerminationOf(100, startAddClient);
        terminateAfterTerminationOf(5000, startRemoveClient);
      }
    };

    return scen;
  }

  public static SimulationScenario intermediate2P2PGraphCompleteScenario() {
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
            raise(5, startNode2Op, new BasicIntSequentialDistribution(1), new ConstantDistribution(Integer.class, 4));
          }
        };
        StochasticProcess startAddVertexClient = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(10));
            raise(3, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                    new ConstantDistribution(Integer.class, 101), new BasicIntSequentialDistribution(27));
          }
        };
        StochasticProcess startRemoveVertexClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientSetsWithIdOp, new BasicIntSequentialDistribution(1),
                    new BasicIntSequentialDistribution(102), new BasicIntSequentialDistribution(27));
          }
        };
        StochasticProcess startAddEdgeClient = new StochasticProcess() {
          {
            eventInterArrivalTime(constant(10));
            raise(2, startSimClientEdgeOp, new BasicIntSequentialDistribution(1),
                    new ConstantDistribution(Integer.class, 11), new BasicIntSequentialDistribution(27),
                    new BasicIntSequentialDistribution(28));
          }
        };
        StochasticProcess startRemoveEdgeClient = new StochasticProcess() {
          {
            eventInterArrivalTime(uniform(1000, 1100));
            raise(1, startSimClientEdgeOp, new BasicIntSequentialDistribution(1),
                    new ConstantDistribution(Integer.class, 12), new BasicIntSequentialDistribution(27),
                    new BasicIntSequentialDistribution(28));
          }
        };

        systemSetup.start();
        startBootstrapServer.startAfterTerminationOf(1000, systemSetup);
        startPeers.startAfterTerminationOf(1000, startBootstrapServer);
        startAddVertexClient.startAfterTerminationOf(5000, startPeers);
        startAddEdgeClient.startAfterTerminationOf(5000, startAddVertexClient);
        startRemoveVertexClient.startAfterTerminationOf(8000, startAddEdgeClient);
        terminateAfterTerminationOf(10000, startRemoveVertexClient);
      }
    };

    return scen;
  }
}