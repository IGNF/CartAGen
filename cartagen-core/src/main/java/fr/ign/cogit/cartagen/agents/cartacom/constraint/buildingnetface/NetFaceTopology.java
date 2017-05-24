package fr.ign.cogit.cartagen.agents.cartacom.constraint.buildingnetface;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraint;
import fr.ign.cogit.cartagen.agents.cartacom.relation.MicroMicroRelation;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;

public class NetFaceTopology extends MicroMicroRelationalConstraint {

  public NetFaceTopology(ICartAComAgentGeneralisation ag,
      MicroMicroRelation rel, double importance) {
    super(ag, rel, importance);
  }

  @Override
  public void computePriority() {
    /*
     * Priorite 4: on garde la priorite 5 pour les pb de coherence topologique
     * avec les LineaireReseau entourant la FaceReseau
     */
    this.setPriority(CartacomSpecifications.CONSTRAINT_PRIORITY_4);
  }

  @Override
  public Set<ActionProposal> getActions() {
    // TODO Auto-generated method stub
    return null;
  }

}
