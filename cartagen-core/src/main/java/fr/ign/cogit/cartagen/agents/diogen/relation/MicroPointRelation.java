package fr.ign.cogit.cartagen.agents.diogen.relation;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IGeographicPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

public abstract class MicroPointRelation extends RelationImpl {

  public MicroPointRelation(ICartAComAgentGeneralisation microAgent,
      IGeographicPointAgent pointAgent, double importance) {
    super(microAgent, pointAgent, importance);
  }

  public IPointAgent getPointAgent() {
    return (IPointAgent) super.getAgentGeo2();
  }

  public void setPointAgent(IGeographicPointAgent pointAgent) {
    super.setAgentGeo2(pointAgent);
  }

  @Override
  public ICartAComAgentGeneralisation getAgentGeo1() {
    return (ICartAComAgentGeneralisation) super.getAgentGeo1();
  }

}
