package fr.ign.cogit.cartagen.agents.gael.field.relation.buildingfield;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.relation.ObjectFieldRelation;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;

public class BuildingElevationRelation extends ObjectFieldRelation {
  /**
     */
  private double valeurCourante;
  /**
     */
  private double valeurInitiale;

  public BuildingElevationRelation(ReliefFieldAgent ac, IBuildingAgent ag) {
    super(ac, ag);
    this.computeInitialValue();
    this.valeurCourante = this.valeurInitiale;
  }

  public BuildingElevationRelation(IBuildingAgent ag) {
    super(
        ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())),
        ag);
    this.computeInitialValue();
    this.valeurCourante = this.valeurInitiale;
  }

  @Override
  public void computeSatisfaction() {
    if (this.getAgentGeo().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }

    // TODO a revoir

    this.computeCurrentValue();
    if (this.valeurInitiale == -999.9) {
      this.setSatisfaction(100);
      return;
    }
    double deniv = Math.abs(this.valeurCourante - this.valeurInitiale);
    if (deniv < GeneralisationSpecifications.DENIVELLEE_MINI) {
      this.setSatisfaction(100);
      return;
    }
    int nb = (int) ((deniv - GeneralisationSpecifications.DENIVELLEE_MINI)
        / AgentSpecifications.HEIGHT_DIFFERENCE_POINT_SATISFACTION);
    this.setSatisfaction(100 - nb);
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }
  }

  @Override
  public void computeCurrentValue() {
    double alt = ((IBuildingAgent) this.getAgentGeo())
        .getElevation(((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())));
    if (alt == -999.9) {
      return;
    }
    this.valeurCourante = alt;
  }

  @Override
  public void computeInitialValue() {
    this.valeurInitiale = ((IBuildingAgent) this.getAgentGeo())
        .getElevation(((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())));
  }

  @Override
  public void computeGoalValue() {
    this.computeInitialValue();
  }

  /**
   * @return
   */
  public double getValeurCourante() {
    return this.valeurCourante;
  }

  /**
   * @return
   */
  public double getValeurInitiale() {
    return this.valeurInitiale;
  }
}
