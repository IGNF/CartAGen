package fr.ign.cogit.cartagen.agents.diogen.interactionmodel;

/**
 * An assignation is an interaction assigned in an interaction matrix. The
 * interaction is associated with other information.
 * @author AMaudet
 * 
 * @param <InteractionType>
 */
public interface Assignation<InteractionType extends Interaction> {

  public InteractionType getInteraction();

  public void setInteraction(InteractionType interaction);

  public double getPriority();

  public void setPriority(double priority);

  public double getDistance();

  public void setDistance(double distance);
}
