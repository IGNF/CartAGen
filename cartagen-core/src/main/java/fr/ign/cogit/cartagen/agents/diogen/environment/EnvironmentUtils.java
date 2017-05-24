package fr.ign.cogit.cartagen.agents.diogen.environment;

import java.util.List;

import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentTypesList;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

public class EnvironmentUtils {

  /**
   * 
   */
  public void changeEnvironmentTypeUsingClassification(Environment env) {
    IAgent agent = env.getHostAgent();
    // verify the environment type of this
    if (env.getEnvironmentType()
        .isA(EnvironmentTypesList.getBlockEnvironmentType())) {
      // cast the agent to block, if the agent is not a block, stop.
      if (!(agent instanceof BlockAgent)) {
        return;
      }
      BlockAgent blockAgent = (BlockAgent) agent;
      // test the size of the building

      // get the buildings
      List<IUrbanElementAgent> buildingAgents = blockAgent.getComponents();

      // count the number of small, medium and big building
      int bigBuilding = 0;
      int smallBuilding = 0;
      // under this area, a building is considered as small
      double lowerThreshold = 225;
      // if the building are bigger than upperThreshold, it is considered as big
      double upperThreshold = 275;
      // ratio of the difference of small and big building
      double ratioThreshold = 1 / 2;
      for (IUrbanElementAgent buildingAgent : buildingAgents) {
        double area = buildingAgent.getGeom().area();
        if (area < lowerThreshold)
          smallBuilding++;
        else if (area > upperThreshold)
          bigBuilding++;
      }

      // double standardDerivationThreshold = 250;

      // choose the type of block depending of the ratio of small and big
      // agents.
      if (((bigBuilding - smallBuilding)
          / blockAgent.getComponents().size()) > ratioThreshold) {
        // this is a big building zone. Use the standard derivation to choose
        // the environment type
        // env.environmentType = null;
      } else if (((bigBuilding - smallBuilding)
          / blockAgent.getComponents().size()) < -ratioThreshold) {
        // this is a small building zone, use the density to choose
        // env.environmentType = null;
      } else {
        // this is a medium area zone, use standard derivation of the building
        // size to choose.
        // env.environmentType = null;
      }
    }
  }

}
