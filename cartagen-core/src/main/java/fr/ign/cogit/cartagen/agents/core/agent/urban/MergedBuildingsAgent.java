package fr.ign.cogit.cartagen.agents.core.agent.urban;

import java.util.List;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.agent.InternStructureAgentGeneralisation;

/*
 * ###### IGN / CartAGen ###### Title: AlignmentAgent Description: Agent
 * representing an alignment of buidings into a urban block Author: J. Renard
 * Date: 02/05/2011
 */

public class MergedBuildingsAgent extends InternStructureAgentGeneralisation {

  /**
   * cree un alignement a partir de batiments
   * @param buildings les batiments constituant l'alignement
   * @param id l'identifiant de l'alignement
   */
  public MergedBuildingsAgent(List<IUrbanElementAgent> buildings) {
    super();

    // liaison avec les micros
    this.setComponents(buildings);
    for (IUrbanElementAgent build : buildings) {
      build.getStructureAgents().add(this);
    }

    // liaison avec le meso
    buildings.get(0).getMesoAgent().getInternStructures().add(this);
    this.setMesoAgent(buildings.get(0).getMesoAgent());

    // Instanciation des contraintes
    this.instantiateConstraints();

  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    this.ajouterContrainteSatisfactionComposants(
        AgentSpecifications.SATISFACTION_BATIMENTS_ILOT_IMP);
  }

}
