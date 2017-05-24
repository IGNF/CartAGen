package fr.ign.cogit.cartagen.agents.gael.field.relation;

import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

public abstract class ObjectFieldRelation extends RelationImpl {

  public ObjectFieldRelation(FieldAgent ac, GeographicObjectAgent ag) {
    super(ac, ag);
  }

  public FieldAgent getAgentChamp() {
    return (FieldAgent) this.getAgentGeo1();
  }

  public GeographicObjectAgent getAgentGeo() {
    return (GeographicObjectAgent) this.getAgentGeo2();
  }

}
