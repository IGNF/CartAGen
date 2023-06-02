/**
 * COGIT generalisation
 */
package fr.ign.cogit.cartagen.agents.gael.field.relation.hydrofield;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.relation.ObjectFieldRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;

/**
 * @author julien Gaffuri 27 janv. 07
 * 
 */
public class HydroSectionOutflowRelation extends ObjectFieldRelation {
  static Logger logger = LogManager
      .getLogger(HydroSectionOutflowRelation.class.getName());

  // la moyenne des q des segments ponderee par longueur
  /**
   */
  private double valeurMoy;
  // la valeur minimum des q des segments
  /**
   */
  private double valeurMin;

  public HydroSectionOutflowRelation(ReliefFieldAgent ac,
      IHydroSectionAgent tr) {
    super(ac, tr);
  }

  public HydroSectionOutflowRelation(IHydroSectionAgent tr) {
    super(
        ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())),
        tr);
  }

  @Override
  public void computeSatisfaction() {
    if (HydroSectionOutflowRelation.logger.isDebugEnabled()) {
      HydroSectionOutflowRelation.logger
          .debug("calcul de statisfaction de " + this);
    }

    if (this.getAgentGeo().isDeleted()) {
      this.setSatisfaction(100);
      return;
    }
    this.computeCurrentValue();
    double d = 0.5 * this.valeurMin + 0.5 * this.valeurMoy;
    d = 1 - d;
    this.setSatisfaction(100 - (int) (d / 0.002));
    if (this.getSatisfaction() < 0) {
      this.setSatisfaction(0);
    }
  }

  @Override
  public void computeCurrentValue() {
    IHydroSectionAgent tr = (IHydroSectionAgent) this.getAgentGeo();
    double Slong = 0.0;
    double Sq = 0.0;
    this.valeurMin = 1.0;

    tr.decompose();

    for (GAELSegment s : tr.getSegments()) {
      double q = s.getOutflowIndicator();
      double lg = s.getLength();
      Sq += q * lg;
      Slong += lg;
      if (q < this.valeurMin) {
        this.valeurMin = q;
      }
    }
    if (Slong == 0) {
      this.valeurMoy = 1.0;
    } else {
      this.valeurMoy = Sq / Slong;
    }

    tr.cleanDecomposition();
  }

  @Override
  public void computeInitialValue() {
  }

  @Override
  public void computeGoalValue() {
  }

}
