/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.gael.field.relation.hydrofield;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.relation.ObjectFieldRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;

/**
 * @author julien Gaffuri 27 janv. 07
 * 
 */
public class HydroSectionLandUseLimitRelation extends ObjectFieldRelation {
  /**
     */
  private double valeurCourante;

  public HydroSectionLandUseLimitRelation(ReliefFieldAgent ac,
      SectionAgent tr) {
    super(ac, tr);
  }

  public HydroSectionLandUseLimitRelation(SectionAgent tr) {
    super(
        ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())),
        tr);
  }

  @Override
  public void computeSatisfaction() {
    if (this.getAgentGeo().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }
    this.setSatisfaction(100);
  }

  @Override
  public void computeCurrentValue() {
    // la valeur courante traduite l'ecoulement du troncon
    // decomposition
    SectionAgent tr = (SectionAgent) this.getAgentGeo();
    tr.decompose();

  }

  @Override
  public void computeInitialValue() {
  }

  @Override
  public void computeGoalValue() {
  }

  /**
   * @return
   */
  public double getValeurCourante() {
    return this.valeurCourante;
  }

  public double getValeurInitiale() {
    return 0.0;
  }

}
