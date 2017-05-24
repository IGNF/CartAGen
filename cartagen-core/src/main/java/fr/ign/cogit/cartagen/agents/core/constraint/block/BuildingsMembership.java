/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.constraint.block;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * contrainte qui incite un ilot a conserver ses batiments a l'interieur de
 * lui-meme
 * @author JGaffuri
 * 
 */
public class BuildingsMembership extends GeographicObjectConstraintImpl {
  /**
   */
  private boolean batiSorti;

  public BuildingsMembership(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeCurrentValue() {
    // on considere qu'un batiment est sorti de l'ilot quand plus de la moitié
    // de sa surface est a l'exterieur
    BlockAgent ai = (BlockAgent) this.getAgent();
    for (GeographicObjectAgent ago : ai.getComponents()) {
      if (ago.getGeom().intersection(ai.getGeom()).area()
          / ai.getGeom().area() >= 0.5) {
        this.batiSorti = true;
        return;
      }
    }
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    this.setSatisfaction(this.batiSorti ? 0 : 100);
  }

  @Override
  public Set<ActionProposal> getActions() {
    // proposer action qui remet les batiments dans l'ilot ?
    // proposer action qui supprime batiment a l'exterieur?
    return null;
  }

}
