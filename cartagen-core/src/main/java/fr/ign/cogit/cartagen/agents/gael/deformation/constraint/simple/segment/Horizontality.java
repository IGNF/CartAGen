package fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.segment;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentDisplacementAction;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.SubmicroSimpleConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;

/**
 * Constraint that forces a segment to be horizontal (to have a null slope). It
 * can be used for channel sections for example.
 * 
 * @author JGaffuri
 * 
 */
public class Horizontality extends SubmicroSimpleConstraint {

  public Horizontality(GAELSegment pp, double importance) {
    super(pp, importance);
  }

  @Override
  public void proposeDisplacement(IPointAgent p, double alpha) {
    GAELSegment s = (GAELSegment) this.getSubmicro();
    double angle = -alpha * s.getOrientationDifferenceToBeHorizontal();
    double cos = Math.cos(angle), sin = Math.sin(angle);
    double dx1 = 0.5 * (s.getP2().getX() - s.getP1().getX()
        + cos * (s.getP1().getX() - s.getP2().getX())
        - sin * (s.getP1().getY() - s.getP2().getY()));
    double dy1 = 0.5 * (s.getP2().getY() - s.getP1().getY()
        + sin * (s.getP1().getX() - s.getP2().getX())
        + cos * (s.getP1().getY() - s.getP2().getY()));
    double dx2 = 0.5 * (s.getP1().getX() - s.getP2().getX()
        + cos * (s.getP2().getX() - s.getP1().getX())
        - sin * (s.getP2().getY() - s.getP1().getY()));
    double dy2 = 0.5 * (s.getP1().getY() - s.getP2().getY()
        + sin * (s.getP2().getX() - s.getP1().getX())
        + cos * (s.getP2().getY() - s.getP1().getY()));
    if (p == s.getP1()) {
      new PointAgentDisplacementAction(p, this, dx1, dy1);
    } else if (p == s.getP2()) {
      new PointAgentDisplacementAction(p, this, dx2, dy2);
    }
  }

  @Override
  public void computeCurrentValue() {
    this.setCurrentValue(((GAELSegment) this.getSubmicro())
        .getOrientationDifferenceToBeHorizontal());
  }

  @Override
  public void computeGoalValue() {
    this.setGoalValue(0.);
  }

  private double threshold = Math.PI / 10.;

  @Override
  public void computeSatisfaction() {
    this.computeGoalValue();
    this.computeCurrentValue();

    // compute the difference value
    double diff = this.getCurrentValue() - this.getGoalValue();

    // guarantee the value is between -Pi and Pi
    if (diff < -Math.PI) {
      diff += 2 * Math.PI;
    } else if (diff > Math.PI) {
      diff -= 2 * Math.PI;
    }

    double result;
    if (Math.abs(diff) <= this.threshold) {
      result = CartacomSpecifications.SATISFACTION_5;
    } else {
      result = CartacomSpecifications.SATISFACTION_1;
    }

    this.setSatisfaction(result);
  }

  @Override
  public void computePriority() {
  }

}
