/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.action;

import fr.ign.cogit.cartagen.agents.core.task.TryActionTask;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.FailureValidity;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * An action having an execute() method that returns a result indicating if the
 * acting agent has been modified, eliminated or is unchanged. It is also able
 * to compute an argument describing itself, that should be not null if this
 * action can be encapsulated in a {@link TryActionTask} associated to the
 * processing state of a conversation. Also, a constraint handled by a CartACom
 * action is necessarily a {@link GeographicConstraint}.
 * @author CDuchene
 * 
 */
public interface CartacomAction extends Action, Cloneable {

  /**
   * Computes an argument describing this action, which can be used by the agent
   * concerned with this action to warn another agent e.g. that it succeeded,
   * failed etc. to do this action.
   * @return an argument describing this action. Its concrete form depends on
   *         the actual language spoken by the agent
   */
  public Object computeDescribingArgument();

  /**
   * {@inheritDoc}
   */
  @Override
  public GeographicConstraint getConstraint();

  /**
   * Computes the validity of the failure, if this action has failed. Should not
   * return {@code null} as soon as this action can be encapsulated in a
   * {@code TryActionTask} task.
   * @return the validity of the failure encountered by this action.
   */
  public FailureValidity computeFailureValidity();

  /**
   * Return true if the action is a act itself action (cf. CartACom thesis,
   * p.66).
   * @return
   */
  public boolean isActItselfAction();

  public void setActItselfAction(boolean actItselfAction);

  /**
   * Return true if the action applies changes of the centroid related
   * constrained zones.
   * @return
   */
  public boolean modifieEnvironmentRepresentation();
}
