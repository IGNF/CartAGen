package fr.ign.cogit.cartagen.agents.diogen.relation;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.Relation;

public interface IMultiplePartitesRelation extends Relation {

  /**
   * The agents involved in the relation
   */
  IGeographicAgent[] getAgentsGeo();

  /**
   * @param ag1
   */
  void setAgentsGeo(IGeographicAgent... agents);

  /**
   * The agents involved in the relation
   */
  IGeographicAgent getAgentGeo(int i);

}
