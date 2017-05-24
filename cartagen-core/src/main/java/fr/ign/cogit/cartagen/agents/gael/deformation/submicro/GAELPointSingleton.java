/*
 * Créé le 12 juin 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.submicro;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.singletonpoint.Elevation;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.singletonpoint.Position;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * A submicro composed of only one point agent
 * 
 * @author JGaffuri
 * 
 */
public class GAELPointSingleton extends SubMicro {

  private IPointAgent pointAgent;

  /**
   * @return The single point agent linked to the object
   */
  public IPointAgent getPointAgent() {
    return this.pointAgent;
  }

  /**
   * The constructor
   * 
   * @param def
   * @param p
   */
  public GAELPointSingleton(GAELDeformable def, IPointAgent p) {
    def.getPointSingletons().add(this);

    // links between the object and its point agent

    this.getPointAgents().add(p);
    p.getSubmicros().add(this);

    this.pointAgent = p;
    p.setPointSingleton(this);

    // build the geometry
    this.setGeom(p.getGeom());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.submicro.SubMicro#getX()
   */
  @Override
  public double getX() {
    return this.getPointAgent().getX();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.submicro.SubMicro#getY()
   */
  @Override
  public double getY() {
    return this.getPointAgent().getY();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.feature.AbstractFeature#getGeom()
   */
  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.submicro.SubMicro#clean()
   */
  @Override
  public void clean() {
    super.clean();
    this.pointAgent = null;
    this.setGeom(null);
  }

  /**
   * Add a constraint on the points position
   * 
   * @param importance
   */
  public void addPositionConstraint(double importance) {
    new Position(this, importance);
  }

  /**
   * Add a constraint on the points position
   * 
   * @param importance
   * @param xGoal
   * @param yGoal
   */
  public void addPositionConstraint(double importance, double xGoal,
      double yGoal) {
    new Position(this, importance, xGoal, yGoal);
  }

  /**
   * Add a constraint on the points elevation
   * 
   * @param importance
   */
  public void addElevationConstraint(double importance) {
    new Elevation(this, importance);
  }

  /**
   * Add a constraint on the points elevation
   * 
   * @param importance
   * @param zGoal
   */
  public void addElevationConstraint(double importance, double zGoal) {
    new Elevation(this, importance, zGoal);
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }
}
