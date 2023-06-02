/**
 * 
 */
package fr.ign.cogit.cartagen.agents.cartacom.agent.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.INetworkSectionAgent;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ISmallCompactAgent;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;

/**
 * @author CDuchene
 * 
 */
public class NetworkFaceAgent extends CartAComAgentGeneralisation {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  /**
   * Logger for this class
   */
  @SuppressWarnings("unused")
  private static Logger logger = LogManager
      .getLogger(NetworkFaceAgent.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  /**
   * The containedSmallCompacts set (bidirectional reference, automatically
   * managed).
   */
  private Set<ISmallCompactAgent> containedSmallCompacts = new HashSet<ISmallCompactAgent>();

  /**
   * The clockwiseBorderingSections set (bidirectional reference, automatically
   * managed).
   */
  private Set<INetworkSectionAgent> clockwiseBorderingSections = new HashSet<INetworkSectionAgent>();

  /**
   * The antiClockwiseBorderingSections set (bidirectional reference,
   * automatically managed).
   */
  private Set<INetworkSectionAgent> antiClockwiseBorderingSections = new HashSet<INetworkSectionAgent>();

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * Constructs a network face agent from a Network face object, sets of
   * clockwise and anticlockwise bordering network sections, a set of contained
   * small compact agents, and an Id.
   * 
   * @param networkFace the network face object handled by this agent
   * @param containedSmallCompacts The small compact agents contained in this
   *          network face
   * @param clockwiseBorderingSections The network section agents bordering this
   *          network face in the clockwise direction
   * @param antiClockwiseBorderingSections The network section agents bordering
   *          this network face in the anticlockwise direction
   */
  public NetworkFaceAgent(INetworkFace networkFace,
      Set<ISmallCompactAgent> containedSmallCompacts,
      Set<INetworkSectionAgent> clockwiseBorderingSections,
      Set<INetworkSectionAgent> antiClockwiseBorderingSections) {
    super(networkFace);
    this.setContainedSmallCompacts(containedSmallCompacts);
    this.setClockwiseBorderingSections(clockwiseBorderingSections);
    this.setAntiClockwiseBorderingSections(antiClockwiseBorderingSections);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public INetworkFace getFeature() {
    return (INetworkFace) super.getFeature();
  }

  /**
   * Getter for containedSmallCompacts.
   * 
   * @return the containedSmallCompacts
   */
  public Set<ISmallCompactAgent> getContainedSmallCompacts() {
    return this.containedSmallCompacts;
  }

  /**
   * Setter for containedSmallCompacts. Also updates the reverse reference from
   * each element of containedSmallCompacts to {@code this}. To break the
   * reference use {@code this.setContainedSmallCompacts(new
   * HashSet<SmallCompactAgent>())}
   * 
   * @param containedSmallCompacts the set of containedSmallCompacts to set
   */
  public void setContainedSmallCompacts(
      Set<ISmallCompactAgent> containedSmallCompacts) {
    Set<ISmallCompactAgent> oldContainedSmallCompacts = new HashSet<ISmallCompactAgent>(
        this.containedSmallCompacts);
    for (ISmallCompactAgent containedSmallCompact : oldContainedSmallCompacts) {
      containedSmallCompact.setContainingFace(null);
    }
    for (ISmallCompactAgent containedSmallCompact : containedSmallCompacts) {
      if (containedSmallCompact == null)
        continue;
      containedSmallCompact.setContainingFace(this);
    }
  }

  /**
   * Adds a SmallCompactAgent to containedSmallCompacts, and updates the reverse
   * reference from the added SmallCompactAgent to {@code this}.
   */
  public void addContainedSmallCompact(
      ISmallCompactAgent containedSmallCompact) {
    if (containedSmallCompact == null) {
      return;
    }
    this.containedSmallCompacts.add(containedSmallCompact);
    containedSmallCompact.setContainingFace(this);
  }

  /**
   * Removes a SmallCompactAgent from containedSmallCompacts, and updates the
   * reverse reference from the removed SmallCompactAgent to {@code null}.
   */
  public void removeContainedSmallCompact(
      ISmallCompactAgent containedSmallCompact) {
    if (containedSmallCompact == null) {
      return;
    }
    this.containedSmallCompacts.remove(containedSmallCompact);
    containedSmallCompact.setContainingFace(null);
  }

  /**
   * Getter for clockwiseBorderingSections.
   * 
   * @return the clockwiseBorderingSections. It can be empty but not
   *         {@code null}.
   */
  public Set<INetworkSectionAgent> getClockwiseBorderingSections() {
    return this.clockwiseBorderingSections;
  }

  /**
   * Setter for clockwiseBorderingSections. Also updates the reverse reference
   * from each element of clockwiseBorderingSections to {@code this}. To break
   * the reference use {@code this.setClockwiseBorderingSections(new
   * HashSet<NetworkSectionAgent>())}
   * 
   * @param clockwiseBorderingSections the set of clockwiseBorderingSections to
   *          set
   */
  public void setClockwiseBorderingSections(
      Set<INetworkSectionAgent> clockwiseBorderingSections) {
    Set<INetworkSectionAgent> oldClockwiseBorderingSections = new HashSet<INetworkSectionAgent>(
        this.clockwiseBorderingSections);
    for (INetworkSectionAgent clockwiseBorderingSection : oldClockwiseBorderingSections) {
      this.clockwiseBorderingSections.remove(clockwiseBorderingSection);
      clockwiseBorderingSection.getRightBorderingFaces().remove(this);
    }
    for (INetworkSectionAgent clockwiseBorderingSection : clockwiseBorderingSections) {
      if (clockwiseBorderingSection == null)
        continue;
      this.clockwiseBorderingSections.add(clockwiseBorderingSection);
      clockwiseBorderingSection.getRightBorderingFaces().add(this);
    }
  }

  /**
   * Adds a NetworkSectionAgent to clockwiseBorderingSections, and updates the
   * reverse reference from the added NetworkSectionAgent to {@code this}.
   */
  public void addClockwiseBorderingSection(
      INetworkSectionAgent clockwiseBorderingSection) {
    if (clockwiseBorderingSection == null) {
      return;
    }
    this.clockwiseBorderingSections.add(clockwiseBorderingSection);
    clockwiseBorderingSection.getRightBorderingFaces().add(this);
  }

  /**
   * Removes a NetworkSectionAgent from clockwiseBorderingSections, and updates
   * the reverse reference from the removed NetworkSectionAgent by removing
   * {@code this}.
   */
  public void removeClockwiseBorderingSection(
      INetworkSectionAgent clockwiseBorderingSection) {
    if (clockwiseBorderingSection == null) {
      return;
    }
    this.clockwiseBorderingSections.remove(clockwiseBorderingSection);
    clockwiseBorderingSection.getRightBorderingFaces().remove(this);
  }

  /**
   * Getter for antiClockwiseBorderingSections.
   * 
   * @return the antiClockwiseBorderingSections. It can be empty but not
   *         {@code null}.
   */
  public Set<INetworkSectionAgent> getAntiClockwiseBorderingSections() {
    return this.antiClockwiseBorderingSections;
  }

  /**
   * Setter for antiClockwiseBorderingSections. Also updates the reverse
   * reference from each element of antiClockwiseBorderingSections to
   * {@code this}. To break the reference use
   * {@code this.setAntiClockwiseBorderingSections(new HashSet
   * <NetworkSectionAgent>())}
   * 
   * @param antiClockwiseBorderingSections the set of
   *          antiClockwiseBorderingSections to set
   */
  public void setAntiClockwiseBorderingSections(
      Set<INetworkSectionAgent> antiClockwiseBorderingSections) {
    Set<INetworkSectionAgent> oldAntiClockwiseBorderingSections = new HashSet<INetworkSectionAgent>(
        this.antiClockwiseBorderingSections);
    for (INetworkSectionAgent antiClockwiseBorderingSection : oldAntiClockwiseBorderingSections) {
      this.antiClockwiseBorderingSections.remove(antiClockwiseBorderingSection);
      antiClockwiseBorderingSection.getLeftBorderedFaces().remove(this);
    }
    for (INetworkSectionAgent antiClockwiseBorderingSection : antiClockwiseBorderingSections) {
      if (antiClockwiseBorderingSection == null)
        continue;
      this.antiClockwiseBorderingSections.add(antiClockwiseBorderingSection);
      antiClockwiseBorderingSection.getLeftBorderedFaces().add(this);
    }
  }

  /**
   * Adds a NetworkSectionAgent to antiClockwiseBorderingSections, and updates
   * the reverse reference from the added NetworkSectionAgent to {@code this}.
   */
  public void addAntiClockwiseBorderingSection(
      INetworkSectionAgent antiClockwiseBorderingSection) {
    if (antiClockwiseBorderingSection == null) {
      return;
    }
    this.antiClockwiseBorderingSections.add(antiClockwiseBorderingSection);
    antiClockwiseBorderingSection.getLeftBorderedFaces().add(this);
  }

  /**
   * Removes a NetworkSectionAgent from antiClockwiseBorderingSections, and
   * updates the reverse reference from the removed NetworkSectionAgent by
   * removing {@code this}.
   */
  public void removeAntiClockwiseBorderingSection(
      INetworkSectionAgent antiClockwiseBorderingSection) {
    if (antiClockwiseBorderingSection == null) {
      return;
    }
    this.antiClockwiseBorderingSections.remove(antiClockwiseBorderingSection);
    antiClockwiseBorderingSection.getLeftBorderedFaces().remove(this);
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void setId(int id) {
    super.setId(id);
    this.getFeature().setId(id);
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
