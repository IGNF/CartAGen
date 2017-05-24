package fr.ign.cogit.cartagen.agents.diogen.padawan;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.AngleSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.SegmentSubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.interaction.point.PointAutoDisplacementInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interaction.point.PointDisplacementAggregableInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.AssignationImpl;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedInteraction;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * Static class storing all environment types.
 * @author AMaudet
 * 
 */
public class EnvironmentTypesList {

  /**
   * Getter for globalEnviromentType.
   * @return the globalEnviromentType
   */
  public static EnvironmentType getGlobalEnvironmentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();

    return environmentMap.get("Global");
  }

  /**
   * Getter for basicEnviromentType.
   * @return the basicEnviromentType
   */
  public static EnvironmentType getBasicEnviromentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();

    return environmentMap.get("Basic");
  }

  /**
   * Getter for areaEnviromentType.
   * @return the areaEnviromentType
   */
  public static EnvironmentType getAreaEnviromentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();

    return environmentMap.get("Area");
  }

  /**
   * Getter for blockEnvironmentType.
   * @return the blockEnvironmentType
   */
  public static EnvironmentType getBlockEnvironmentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();

    return environmentMap.get("Block");
  }

  private static EnvironmentType deformable = null;

  /**
   * 
   * @return
   */
  public static EnvironmentType getDeformableEnvironmentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();
    if (deformable == null) {
      deformable = environmentMap.get("Deformable");
      if (deformable == null) {
        deformable = new EnvironmentType();
        deformable.setEnvironmentTypeName("Deformable");
      }

      Class<? extends IAgent> sourceAgent = GeographicPointAgent.class;
      Class<? extends IAgent> segmentAgent = SegmentSubmicroAgent.class;

      Class<? extends IAgent> angleAgent = AngleSubmicroAgent.class;

      deformable.getInteractionMatrix().addDegenerateAssignation(sourceAgent,
          new AssignationImpl<ConstrainedInteraction>(
              PointAutoDisplacementInteraction.getInstance()));

      deformable.getInteractionMatrix().addSingleTargetAssignation(sourceAgent,
          angleAgent, new AssignationImpl<ConstrainedInteraction>(
              PointDisplacementAggregableInteraction.getInstance()));

      deformable.getInteractionMatrix().addSingleTargetAssignation(sourceAgent,
          segmentAgent, new AssignationImpl<ConstrainedInteraction>(
              PointDisplacementAggregableInteraction.getInstance()));
    }

    return deformable;
  }

  private static EnvironmentType roadNeighbourhood = null;

  /**
   * 
   * @return
   */
  public static EnvironmentType getRoadNeighbourhoodEnvironmentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();
    if (roadNeighbourhood == null) {
      roadNeighbourhood = environmentMap.get("Road Neighbourhood");
      if (roadNeighbourhood == null) {
        roadNeighbourhood = new EnvironmentType();
        roadNeighbourhood.setEnvironmentTypeName("Road Neighbourhood");
      }
    }

    return roadNeighbourhood;
  }

  public static EnvironmentType getTownEnvironmentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();

    return environmentMap.get("Town");
  }

  public static EnvironmentType getNetworkEnvironmentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();

    return environmentMap.get("Network");
  }

  public static EnvironmentType getDeadEndEnvironmentType() {
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();

    return environmentMap.get("DeadEndNeihbourhood");
  }

  public static boolean isBlockEnvironmentType(EnvironmentType type) {
    return type.isA(getBlockEnvironmentType());
  }

  private static Map<String, EnvironmentType> environmentMap;

  public static Set<EnvironmentType> getEnvironmentTypeList() {
    // EnvironmentType[] envTypes = { getBasicEnviromentType(),
    // getGlobalEnvironmentType(), getAreaEnviromentType(),
    // getBlockEnvironmentType(), getTownEnvironmentType(),
    // getNetworkEnvironmentType(), getDeadEndEnvironmentType() };
    if (environmentMap == null)
      environmentMap = MatrixParser.parseEnvironmentsXML();
    return new HashSet<EnvironmentType>(environmentMap.values());

  }
}
