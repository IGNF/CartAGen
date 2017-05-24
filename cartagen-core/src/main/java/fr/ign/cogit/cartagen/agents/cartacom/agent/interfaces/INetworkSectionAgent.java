/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces;

import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.NetworkFaceAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.SectionType;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

/**
 * @author CDuchene
 * 
 */
public interface INetworkSectionAgent extends ICartAComAgentGeneralisation {

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  @Override
  public INetworkSection getFeature();

  /**
   * Getter for sectionType.
   * @return the sectionType
   */
  public SectionType getSectionType();

  /**
   * Setter for sectionType.
   * @param sectionType the sectionType to set
   */
  public void setSectionType(SectionType sectionType);

  /**
   * Getter for rightBorderingFaces.
   * @return the rightBorderingFaces. It can be empty but not {@code null}.
   */
  public Set<NetworkFaceAgent> getRightBorderingFaces();

  /**
   * Setter for rightBorderingFaces. Also updates the reverse reference from
   * each element of rightBorderingFaces to {@code this}. To break the reference
   * use {@code this.setRightBorderingFaces(new HashSet<NetworkFaceAgent>())}
   * @param rightBorderingFaces the set of rightBorderingFaces to set
   */
  public void setRightBorderingFaces(Set<NetworkFaceAgent> rightBorderingFaces);

  /**
   * Adds a NetworkFaceAgent to rightBorderingFaces, and updates the reverse
   * reference from the added NetworkFaceAgent to {@code this}.
   */
  public void addRightBorderingFace(NetworkFaceAgent rightBorderingFace);

  /**
   * Removes a NetworkFaceAgent from rightBorderingFaces, and updates the
   * reverse reference from the removed NetworkFaceAgent by removing
   * {@code this}.
   */
  public void removeRightBorderingFace(NetworkFaceAgent rightBorderingFace);

  /**
   * Getter for leftBorderedFaces.
   * @return the leftBorderedFaces. It can be empty but not {@code null}.
   */
  public Set<NetworkFaceAgent> getLeftBorderedFaces();

  /**
   * Setter for leftBorderedFaces. Also updates the reverse reference from each
   * element of leftBorderedFaces to {@code this}. To break the reference use
   * {@code this.setLeftBorderedFaces(new HashSet<NetworkFaceAgent>())}
   * @param leftBorderedFaces the set of leftBorderedFaces to set
   */
  public void setLeftBorderedFaces(Set<NetworkFaceAgent> leftBorderedFaces);

  /**
   * Adds a NetworkFaceAgent to leftBorderedFaces, and updates the reverse
   * reference from the added NetworkFaceAgent to {@code this}.
   */
  public void addLeftBorderedFace(NetworkFaceAgent leftBorderedFace);

  /**
   * Removes a NetworkFaceAgent from leftBorderedFaces, and updates the reverse
   * reference from the removed NetworkFaceAgent by removing {@code this}.
   */
  public void removeLeftBorderedFace(NetworkFaceAgent leftBorderedFace);

  @Override
  public ISectionAgent getAgentAgent();

}
