package fr.ign.cogit.cartagen.agents.diogen.interactionmodel;

/**
 * An assignation is an interaction included in an interaction matrix.
 * 
 * @author AMaudet
 * 
 */
public class AssignationImpl<InteractionClass extends Interaction> implements
    Assignation<InteractionClass> {

  /**
   * The interaction object
   */
  private InteractionClass interaction;

  /**
   * The priority of this interaction. 0 if no priority.
   */
  private double priority = 0;

  /**
   * The maximum distance for this interaction occurring.
   */
  private double distance;

  /**
   * Setter for interaction. {@inheritDoc}
   */
  @Override
  public void setInteraction(InteractionClass interaction) {
    this.interaction = interaction;
  }

  /**
   * Getter for interaction {@inheritDoc}
   */
  @Override
  public InteractionClass getInteraction() {
    return this.interaction;
  }

  /**
   * Setter for priority. {@inheritDoc}
   */
  @Override
  public void setPriority(double priority) {
    this.priority = priority;
  }

  /**
   * Getter for priority. {@inheritDoc}
   */
  @Override
  public double getPriority() {
    return this.priority;
  }

  /**
   * Setter for distance. {@inheritDoc}
   */
  @Override
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * Getter for distance. {@inheritDoc}
   */
  @Override
  public double getDistance() {
    return this.distance;
  }

  /**
   * Constructor
   * @param interaction
   * @param priority
   * @param distance
   */
  public AssignationImpl(InteractionClass interaction, double priority,
      double distance) {
    this.interaction = interaction;
    this.priority = priority;
    this.distance = distance;

  }

  /**
   * Constructor
   * @param interaction
   */
  public AssignationImpl(InteractionClass interaction) {
    this.interaction = interaction;
    this.priority = 0;
    this.distance = 0;

  }
}
