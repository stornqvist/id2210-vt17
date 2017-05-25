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

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class SimLauncher {
  // TODO: Add so that you can specify specific simulation scenarios
  public static void main(String[] args) {
    SimulationScenario.setSeed(ScenarioSetup.scenarioSeed);

    /************************GSET*************************************************************/
  //  SimulationScenario simpleBootScenario = ScenarioGen.simpleSim();
  //  simpleBootScenario.simulate(LauncherComp.class);

  //  SimulationScenario simpleKillScenario = ScenarioGen.simpleKillScenario();
  //  simpleKillScenario.simulate(LauncherComp.class);

  //  SimulationScenario simpleAddScenario = ScenarioGenSets.simpleGSetAddScenario();
  //  simpleAddScenario.simulate(LauncherComp.class);

    /************************TWOPSET*************************************************************/
  //  SimulationScenario simpleTwoPSetAddScenario = ScenarioGenSets.simple2PSetAddScenario();
  //  simpleTwoPSetAddScenario.simulate(LauncherComp.class);

  //  SimulationScenario simpleTwoPSetRemoveScenario = ScenarioGenSets.simple2PSetAddRemoveScenario();
  //  simpleTwoPSetRemoveScenario.simulate(LauncherComp.class);

    /************************ORSET*************************************************************/
  //  SimulationScenario simpleOrSetAddScenario = ScenarioGenSets.simpleOrSetAddScenario();
  //  simpleOrSetAddScenario.simulate(LauncherComp.class);

  //  SimulationScenario simpleOrSetAddRemoveScenario = ScenarioGenSets.simpleOrSetAddRemoveScenario();
  //  simpleOrSetAddRemoveScenario.simulate(LauncherComp.class);

    /************************TWOPTWOPSET*************************************************************/
  //  SimulationScenario simple2P2PGraphAddScenario = ScenarioGenSets.simple2P2PGraphAddVertexScenario();
  //  simple2P2PGraphAddScenario.simulate(LauncherComp.class);

  //  SimulationScenario simple2P2PGraphAddRemoveScenario = ScenarioGenSets.simple2P2PGraphAddRemoveVertexScenario();
  //  simple2P2PGraphAddRemoveScenario.simulate(LauncherComp.class);

  //  SimulationScenario intermediate2P2PGraphAddRemoveVertexScenario = ScenarioGenSets.intermediate2P2PGraphAddRemoveVertexScenario();
  //  intermediate2P2PGraphAddRemoveVertexScenario.simulate(LauncherComp.class);

    SimulationScenario intermediate2P2PGraphCompleteScenario = ScenarioGenSets.intermediate2P2PGraphCompleteScenario();
    intermediate2P2PGraphCompleteScenario.simulate(LauncherComp.class);
  }
}
