/*
 * Créé le 29 juil. 2005
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.submicro;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.angle.Value;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * The submicro angle class.
 * 
 * @author julien Gaffuri
 * 
 */
public class GAELAngle extends SubMicro {

  private IPointAgent p;
  private IPointAgent p1;
  private IPointAgent p2;

  /**
   * The angle head
   */
  public IPointAgent getP() {
    return this.p;
  }

  /**
   * @return The first point of the angle
   */
  public IPointAgent getP1() {
    return this.p1;
  }

  /**
   * @return The second point of the angle
   */
  public IPointAgent getP2() {
    return this.p2;
  }

  /**
   * The constructor
   * 
   * @param def
   * @param p1
   * @param p
   * @param p2
   */
  public GAELAngle(GAELDeformable def, IPointAgent p1, IPointAgent p,
      IPointAgent p2) {
    def.getAngles().add(this);

    // links between the object and its point agent
    p1.getSubmicros().add(this);
    p.getSubmicros().add(this);
    p2.getSubmicros().add(this);

    this.getPointAgents().add(p1);
    this.getPointAgents().add(p);
    this.getPointAgents().add(p2);

    this.p = p;
    // the angle (p1->p->p2) has to be direct direct. computes the vectorial
    // product to build a direct angle
    if ((p1.getXIni() - p.getXIni()) * (p2.getYIni() - p.getYIni())
        - (p1.getYIni() - p.getYIni()) * (p2.getXIni() - p.getXIni()) > 0) {
      this.p1 = p1;
      this.p2 = p2;
    } else {
      this.p1 = p2;
      this.p2 = p1;
    }

    p1.addAgentPointAccointants(p2);
    p1.addAgentPointAccointants(p);
    p2.addAgentPointAccointants(p1);
    p2.addAgentPointAccointants(p);
    p.addAgentPointAccointants(p1);
    p.addAgentPointAccointants(p2);

    // build the geometry (a triangle)
    DirectPositionList dpl = new DirectPositionList();
    dpl.add(p.getPositions().get(0));
    dpl.add(p1.getPositions().get(0));
    dpl.add(p2.getPositions().get(0));
    dpl.add(p.getPositions().get(0));
    this.setGeom(new GM_Triangle(new GM_Ring(new GM_LineString(dpl))));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.submicro.SubMicro#clean()
   */
  @Override
  public void clean() {
    super.clean();
    this.p = null;
    this.p1 = null;
    this.p2 = null;
    this.setGeom(null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.submicro.SubMicro#getX()
   */
  @Override
  public double getX() {
    return this.getP().getX();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.submicro.SubMicro#getY()
   */
  @Override
  public double getY() {
    return this.getP().getY();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.feature.AbstractFeature#getGeom()
   */
  @Override
  public ITriangle getGeom() {
    return (ITriangle) super.getGeom();
  }

  /**
   * @return The angle initial value, between 0 and Pi.
   */
  public double getValue() {
    double value = Math.atan2(
        (this.getP1().getX() - this.getP().getX())
            * (this.getP2().getY() - this.getP().getY())
            - (this.getP1().getY() - this.getP().getY())
                * (this.getP2().getX() - this.getP().getX()),
        (this.getP1().getX() - this.getP().getX())
            * (this.getP2().getX() - this.getP().getX())
            + (this.getP1().getY() - this.getP().getY())
                * (this.getP2().getY() - this.getP().getY()));
    if (value < 0.0) {
      value += 2 * Math.PI;
    }
    return value;
  }

  /**
   * The angle initial value, between 0 and Pi.
   */
  private double initialValue = -999.9;

  /**
   * @return The angle initial value, between 0 and Pi.
   */
  public double getInitialValue() {
    if (this.initialValue == -999.9) {

      // compute the vectorial product
      double pv = (this.getP1().getXIni() - this.getP().getXIni())
          * (this.getP2().getYIni() - this.getP().getYIni())
          - (this.getP1().getYIni() - this.getP().getYIni())
              * (this.getP2().getXIni() - this.getP().getXIni());

      // compute the scalar product
      double ps = (this.getP1().getXIni() - this.getP().getXIni())
          * (this.getP2().getXIni() - this.getP().getXIni())
          + (this.getP1().getYIni() - this.getP().getYIni())
              * (this.getP2().getYIni() - this.getP().getYIni());

      this.initialValue = Math.atan2(pv, ps);
    }
    return this.initialValue;
  }

  /**
   * @param value
   * @return Angle difference between the given value and the current one,
   *         between -Pi and Pi
   */
  public double getValueDifference(double value) {
    // compute the difference value
    double diff = value - this.getValue();

    // guarantee the value is between -Pi and Pi
    if (diff < -Math.PI) {
      return diff + 2 * Math.PI;
    } else if (diff > Math.PI) {
      return diff - 2 * Math.PI;
    } else {
      return diff;
    }
  }

  /**
   * @return Angle difference between the initial and current values, between
   *         -Pi and Pi
   */
  public double getAngleEcart() {
    return this.getValueDifference(this.getInitialValue());
  }

  /**
   * Add a constraint on the angle value
   * 
   * @param importance
   */
  public void addValueConstraint(double importance) {
    new Value(this, importance);
  }

  /**
   * Add a constraint on the angle value
   * 
   * @param importance
   * @param goalValue
   */
  public void addValueConstraint(double importance, double goalValue) {
    new Value(this, importance, goalValue);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.feature.AbstractFeature#toString()
   */
  @Override
  public String toString() {
    return "Angle-" + this.getP1() + "-" + this.getP() + "-" + this.getP2();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

}
