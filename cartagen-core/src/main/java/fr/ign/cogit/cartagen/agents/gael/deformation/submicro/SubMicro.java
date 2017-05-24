package fr.ign.cogit.cartagen.agents.gael.deformation.submicro;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.CenterPosition;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;

public abstract class SubMicro extends AbstractFeature implements ISubMicro {

  /**
   * les agents point composant le submicro
   */
  private ArrayList<IPointAgent> pointAgents = new ArrayList<IPointAgent>();

  /**
   * @return
   */
  public ArrayList<IPointAgent> getPointAgents() {
    return this.pointAgents;
  }

  /**
   * The constraints of the submicro
   */
  private ArrayList<SubmicroConstraint> submicroConstraints = new ArrayList<SubmicroConstraint>();

  /**
   * @return
   */
  public ArrayList<SubmicroConstraint> getSubmicroConstraints() {
    return this.submicroConstraints;
  }

  /**
   * Clean the object
   */
  public void clean() {
    this.getSubmicroConstraints().clear();
    this.getPointAgents().clear();
  }

  /**
   * @return The mean X of the points
   */
  public double getX() {
    double x = 0.0;
    for (IPointAgent p : this.getPointAgents()) {
      x += p.getX();
    }
    return x / this.getPointAgents().size();
  }

  /**
   * @return The mean Y of the points
   */
  public double getY() {
    double y = 0.0;
    for (IPointAgent p : this.getPointAgents()) {
      y += p.getY();
    }
    return y / this.getPointAgents().size();
  }

  private double xIni = -999.9;
  private double yIni = -999.9;

  /**
   * @return The mean X of the points in their initial state
   */
  public double getXIni() {
    if (this.xIni == -999.9) {
      this.xIni = 0.0;
      for (IPointAgent p : this.getPointAgents()) {
        this.xIni += p.getXIni();
      }
      this.xIni /= this.getPointAgents().size();
    }
    return this.xIni;
  }

  /**
   * @return The mean Y of the points in their initial state
   */
  public double getYIni() {
    if (this.yIni == -999.9) {
      this.yIni = 0.0;
      for (IPointAgent p : this.getPointAgents()) {
        this.yIni += p.getYIni();
      }
      this.yIni /= this.getPointAgents().size();
    }
    return this.yIni;
  }

  /**
   * Add a constraint on the center position of the submicro
   * 
   * @param importance
   */
  public void addCenterPositionConstraint(double importance) {
    new CenterPosition(this, importance);
  }

  /**
   * Add a constraint on the center position of the submicro
   * 
   * @param importance
   * @param xGoal
   * @param yGoal
   */
  public void addCenterPositionConstraint(double importance, double xGoal,
      double yGoal) {
    new CenterPosition(this, importance);
  }

}
