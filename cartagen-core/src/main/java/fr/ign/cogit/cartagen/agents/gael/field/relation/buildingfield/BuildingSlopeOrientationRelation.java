package fr.ign.cogit.cartagen.agents.gael.field.relation.buildingfield;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.relation.ObjectFieldRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;

public class BuildingSlopeOrientationRelation extends ObjectFieldRelation {
  /**
   */
  private double valeurCouranteOrientationPente;
  /**
   */
  private double valeurCouranteOrientationBatiment;
  /**
   */
  private double valeurInitialeOrientationPente;
  /**
   */
  private double valeurInitialeOrientationBatiment;

  // private double dAngle=2*Math.PI/20;

  public BuildingSlopeOrientationRelation(ReliefFieldAgent ac,
      BuildingAgent ag) {
    super(ac, ag);
    this.computeInitialValue();
    this.valeurCouranteOrientationPente = this.valeurInitialeOrientationPente;
    this.valeurCouranteOrientationBatiment = this.valeurInitialeOrientationBatiment;
  }

  public BuildingSlopeOrientationRelation(BuildingAgent ag) {
    super(
        ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())),
        ag);
    this.computeInitialValue();
    this.valeurCouranteOrientationPente = this.valeurInitialeOrientationPente;
    this.valeurCouranteOrientationBatiment = this.valeurInitialeOrientationBatiment;
  }

  @Override
  public void computeSatisfaction() {
    if (this.getAgentGeo().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }

    this.computeCurrentValue();
    if (this.valeurInitialeOrientationPente == -999.9
        || this.valeurInitialeOrientationPente == 999.9
        || this.valeurInitialeOrientationBatiment == 999.9
        || this.valeurCouranteOrientationPente == -999.9
        || this.valeurCouranteOrientationPente == 999.9
        || this.valeurCouranteOrientationBatiment == 999.9) {
      this.setSatisfaction(100);
      return;
    }

    /*
     * double deniv=Math.abs(valeurCourante-valeurInitiale); if
     * (deniv<denivMin){ satisfaction=10; return;} int
     * nb=(int)((deniv-denivMin)/dDeniv); satisfaction=9-nb; if (satisfaction<0)
     * satisfaction=0;
     */
  }

  @Override
  public void computeCurrentValue() {
    double orPente = ((BuildingAgent) this.getAgentGeo())
        .getSlopeVectorOrientation(
            ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(CartAGenDoc
                .getInstance().getCurrentDataset().getReliefField())));
    if (orPente == -999.9 || orPente == 999.9) {
      return;
    }
    double orBati = new OrientationMeasure(
        this.getAgentGeo().getFeature().getGeom()).getGeneralOrientation();
    this.valeurCouranteOrientationPente = orPente;
    this.valeurCouranteOrientationBatiment = orBati;
  }

  @Override
  public void computeInitialValue() {
    this.valeurInitialeOrientationPente = ((BuildingAgent) this.getAgentGeo())
        .getSlopeVectorOrientation(
            ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(CartAGenDoc
                .getInstance().getCurrentDataset().getReliefField())));
    this.valeurInitialeOrientationBatiment = new OrientationMeasure(
        this.getAgentGeo().getGeom()).getGeneralOrientation();
    // Algos.orientationGenerale();
  }

  @Override
  public void computeGoalValue() {
    this.computeInitialValue();
  }

  public double getValeurCourante() {
    return 0.0;
  }

  public double getValeurInitiale() {
    return 0.0;
  }

}
