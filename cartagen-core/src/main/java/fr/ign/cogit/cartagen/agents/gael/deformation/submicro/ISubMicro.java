/*
 * Créé le 7 juin 2006
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.submicro;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * The submicro objects superclass. A submicro object is a relatively small set
 * of points, whose relative position has to be constrained.
 * @author JGaffuri
 */
public interface ISubMicro extends IFeature {

  /**
   * @return
   */
  public ArrayList<IPointAgent> getPointAgents();

  /**
   * @return
   */
  public ArrayList<SubmicroConstraint> getSubmicroConstraints();

  /**
   * Clean the object
   */
  public void clean();

  /**
   * @return The mean X of the points
   */
  public double getX();

  /**
   * @return The mean Y of the points
   */
  public double getY();

  /**
   * @return The mean X of the points in their initial state
   */
  public double getXIni();

  /**
   * @return The mean Y of the points in their initial state
   */
  public double getYIni();

  /**
   * Add a constraint on the center position of the submicro
   * 
   * @param importance
   */
  public void addCenterPositionConstraint(double importance);

  /**
   * Add a constraint on the center position of the submicro
   * 
   * @param importance
   * @param xGoal
   * @param yGoal
   */
  public void addCenterPositionConstraint(double importance, double xGoal,
      double yGoal);

}
