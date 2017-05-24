package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.INetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

public class NetworkSectionAgent extends CartAComAgentGeneralisation
    implements INetworkSectionAgent {

  // /////////////////////////////////////////////////////////////////////////////

  // Reference (m-n) d'une classe A (cette classe) vers une classe B
  // Definir ci-dessous les noms de classes et d'attributs à utiliser
  // puis virer ces lignes
  // Classe A : NetworkSectionAgent
  // Un A, vu de B : clockwiseBorderingSection
  // Le même avec une majuscule au début : ClockwiseBorderingSection
  // Classe B : NetworkFaceAgent
  // Un B, vu de A : rightBorderingFace
  // Le même avec une majuscule au début : RightBorderingFace

  // ////////////// Code à mettre dans la classe NetworkSectionAgent //

  // ////////////// Code à mettre dans la classe NetworkFaceAgent //

  // ///// Fin
  // //////////////////////////////////////////////////////////////////

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * The type of this network section
   */
  private SectionType sectionType;

  /**
   * The rightBorderingFaces set (bidirectional reference, automatically
   * managed).
   */
  private Set<NetworkFaceAgent> rightBorderingFaces = new HashSet<NetworkFaceAgent>();

  /**
   * The leftBorderedFaces set (bidirectional reference, automatically managed).
   */
  private Set<NetworkFaceAgent> leftBorderedFaces = new HashSet<NetworkFaceAgent>();

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Builds a Network section agent from a geographic object
   * @param feature the geographic object
   */
  public NetworkSectionAgent(IGeneObj feature) {
    super(feature);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  @Override
  public INetworkSection getFeature() {
    return (INetworkSection) super.getFeature();
  }

  /**
   * Getter for sectionType.
   * @return the sectionType
   */
  public SectionType getSectionType() {
    return this.sectionType;
  }

  /**
   * Setter for sectionType.
   * @param sectionType the sectionType to set
   */
  public void setSectionType(SectionType sectionType) {
    this.sectionType = sectionType;
  }

  /**
   * Getter for rightBorderingFaces.
   * @return the rightBorderingFaces. It can be empty but not {@code null}.
   */
  public Set<NetworkFaceAgent> getRightBorderingFaces() {
    return this.rightBorderingFaces;
  }

  /**
   * Setter for rightBorderingFaces. Also updates the reverse reference from
   * each element of rightBorderingFaces to {@code this}. To break the reference
   * use {@code this.setRightBorderingFaces(new HashSet<NetworkFaceAgent>())}
   * @param rightBorderingFaces the set of rightBorderingFaces to set
   */
  public void setRightBorderingFaces(
      Set<NetworkFaceAgent> rightBorderingFaces) {
    Set<NetworkFaceAgent> oldRightBorderingFaces = new HashSet<NetworkFaceAgent>(
        this.rightBorderingFaces);
    for (NetworkFaceAgent rightBorderingFace : oldRightBorderingFaces) {
      this.rightBorderingFaces.remove(rightBorderingFace);
      rightBorderingFace.getClockwiseBorderingSections().remove(this);
    }
    for (NetworkFaceAgent rightBorderingFace : rightBorderingFaces) {
      this.rightBorderingFaces.add(rightBorderingFace);
      rightBorderingFace.getClockwiseBorderingSections().add(this);
    }
  }

  /**
   * Adds a NetworkFaceAgent to rightBorderingFaces, and updates the reverse
   * reference from the added NetworkFaceAgent to {@code this}.
   */
  public void addRightBorderingFace(NetworkFaceAgent rightBorderingFace) {
    if (rightBorderingFace == null) {
      return;
    }
    this.rightBorderingFaces.add(rightBorderingFace);
    rightBorderingFace.getClockwiseBorderingSections().add(this);
  }

  /**
   * Removes a NetworkFaceAgent from rightBorderingFaces, and updates the
   * reverse reference from the removed NetworkFaceAgent by removing
   * {@code this}.
   */
  public void removeRightBorderingFace(NetworkFaceAgent rightBorderingFace) {
    if (rightBorderingFace == null) {
      return;
    }
    this.rightBorderingFaces.remove(rightBorderingFace);
    rightBorderingFace.getClockwiseBorderingSections().remove(this);
  }

  /**
   * Getter for leftBorderedFaces.
   * @return the leftBorderedFaces. It can be empty but not {@code null}.
   */
  public Set<NetworkFaceAgent> getLeftBorderedFaces() {
    return this.leftBorderedFaces;
  }

  /**
   * Setter for leftBorderedFaces. Also updates the reverse reference from each
   * element of leftBorderedFaces to {@code this}. To break the reference use
   * {@code this.setLeftBorderedFaces(new HashSet<NetworkFaceAgent>())}
   * @param leftBorderedFaces the set of leftBorderedFaces to set
   */
  public void setLeftBorderedFaces(Set<NetworkFaceAgent> leftBorderedFaces) {
    Set<NetworkFaceAgent> oldLeftBorderedFaces = new HashSet<NetworkFaceAgent>(
        this.leftBorderedFaces);
    for (NetworkFaceAgent leftBorderedFace : oldLeftBorderedFaces) {
      this.leftBorderedFaces.remove(leftBorderedFace);
      leftBorderedFace.getAntiClockwiseBorderingSections().remove(this);
    }
    for (NetworkFaceAgent leftBorderedFace : leftBorderedFaces) {
      this.leftBorderedFaces.add(leftBorderedFace);
      leftBorderedFace.getAntiClockwiseBorderingSections().add(this);
    }
  }

  /**
   * Adds a NetworkFaceAgent to leftBorderedFaces, and updates the reverse
   * reference from the added NetworkFaceAgent to {@code this}.
   */
  public void addLeftBorderedFace(NetworkFaceAgent leftBorderedFace) {
    if (leftBorderedFace == null) {
      return;
    }
    this.leftBorderedFaces.add(leftBorderedFace);
    leftBorderedFace.getAntiClockwiseBorderingSections().add(this);
  }

  /**
   * Removes a NetworkFaceAgent from leftBorderedFaces, and updates the reverse
   * reference from the removed NetworkFaceAgent by removing {@code this}.
   */
  public void removeLeftBorderedFace(NetworkFaceAgent leftBorderedFace) {
    if (leftBorderedFace == null) {
      return;
    }
    this.leftBorderedFaces.remove(leftBorderedFace);
    leftBorderedFace.getAntiClockwiseBorderingSections().remove(this);
  }

  @Override
  public ISectionAgent getAgentAgent() {
    return (ISectionAgent) super.getAgentAgent();
  }

  // /////////////////////////////////////////////
  // Public methods - Others //
  // /////////////////////////////////////////////

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
