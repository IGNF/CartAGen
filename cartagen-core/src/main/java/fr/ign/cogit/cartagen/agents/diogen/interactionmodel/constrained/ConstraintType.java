package fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConstraintType implements Comparable<ConstraintType> {

  public final static int STRONGHLY_FAVORABLE = Integer.MAX_VALUE;
  public final static int OPPOSITE = Integer.MIN_VALUE;
  public final static int INDIFFERENT = 0;
  public final static int FAVORABLE = 1;
  public final static int UNFAVORABLE = -1;

  private String name;

  /**
   * Setter for name.
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for name.
   * @return the name
   */
  public String getName() {
    return name;
  }

  public String getSimplifiedName() {
    String[] res = this.name.split("\\.");
    return res[res.length - 1];
  }

  private int position;

  /**
   * Setter for position.
   * @param position the position to set
   */
  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * Getter for position.
   * @return the position
   */
  public int getPosition() {
    return position;
  }

  private int defaultInfluence = 0;

  /**
   * Setter for defaultInfluence.
   * @param defaultInfluence the defaultInfluence to set
   */
  public void setDefaultInfluence(int defaultInfluence) {
    this.defaultInfluence = defaultInfluence;
  }

  /**
   * Getter for defaultInfluence.
   * @return the defaultInfluence
   */
  public int getDefaultInfluence() {
    return defaultInfluence;
  }

  private Map<Integer, Method> influencesMap = new HashMap<Integer, Method>();

  /**
   * Setter for influencesMap.
   * @param influencesMap the influencesMap to set
   */
  public void setInfluencesMap(Map<Integer, Method> influencesMap) {
    this.influencesMap = influencesMap;
  }

  /**
   * Getter for influencesMap.
   * @return the influencesMap
   */
  public Map<Integer, Method> getInfluencesMap() {
    return influencesMap;
  }

  private Map<Integer, String> influencesDescriptionMap = new HashMap<Integer, String>();

  /**
   * Getter for influencesDescriptionMap.
   * @return the influencesDescriptionMap
   */
  public Map<Integer, String> getInfluencesDescriptionMap() {
    return influencesDescriptionMap;
  }

  /**
   * Method adding a new method for the given influence.
   * 
   * @param influence
   * @param method
   * @param influenceDescription
   */
  public void addInfluence(int influence, Method method,
      String influenceDescription) {
    influencesMap.put(influence, method);
    influencesDescriptionMap.put(influence, influenceDescription);
  }

  /**
   * Constructor for ConstraintType
   * @param name
   * @param position
   * @param defaultInfluence
   */
  public ConstraintType(String name, int position, int defaultInfluence) {
    super();
    this.name = name;
    this.position = position;
    this.defaultInfluence = defaultInfluence;
  }

  /**
   * 
   * {@inheritDoc}
   */
  @Override
  public int compareTo(ConstraintType arg) {
    return this.getPosition() - arg.getPosition();
  }

}
