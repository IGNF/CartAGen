package fr.ign.cogit.cartagen.agents.diogen.relation;

import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

public abstract class MultiplePartitesRelation extends RelationImpl
    implements IMultiplePartitesRelation {

  private IGeographicAgent[] agents;

  public MultiplePartitesRelation(Set<IGeographicAgent> agents) {
    super();
    this.setAgentsGeo(agents.toArray(new IGeographicAgent[0]));
  }

  public MultiplePartitesRelation(IGeographicAgent... agents) {
    super();
    this.setAgentsGeo(agents);
  }

  public IGeographicAgent[] getAgentsGeo() {
    return agents;
  }

  public void setAgentsGeo(IGeographicAgent... agents) {
    this.agents = agents;
  }

}
