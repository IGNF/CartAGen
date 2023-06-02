/*
 * Créé le 10 août 2005
 */
package fr.ign.cogit.cartagen.agents.core.agent.network;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.MicroAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author julien Gaffuri
 */
public class NetworkNodeAgent extends MicroAgentGeneralisation {
  @SuppressWarnings("unused")
  private static Logger logger = LogManager
      .getLogger(NetworkNodeAgent.class.getName());

  /**
   * le reseau auquel le noeud est rattache
   */
  private NetworkAgent reseau;

  /**
   * @return
   */
  public NetworkAgent getReseau() {
    return this.reseau;
  }

  /**
   * @param res
   */
  public void setReseau(NetworkAgent res) {
    this.reseau = res;
  }

  @Override
  public INetworkNode getFeature() {
    return (INetworkNode) super.getFeature();
  }

  /**
   * construit un noeud de reseau au niveau d'un certain point
   * @param reseau
   * @param c
   */
  public NetworkNodeAgent(NetworkAgent reseau, INetworkNode noeud) {
    super();
    this.setFeature(noeud);
    noeud.addCorrespondant(this.getFeature());

    this.setInitialGeom((IGeometry) noeud.getGeom().clone());

    // lien entre le noeud et son reseau
    this.setReseau(reseau);
    this.getReseau().getNoeuds().add(this.getFeature());
  }

  /**
     */
  private Set<ISectionAgent> inSections = null;

  /**
   * @return les troncons entrants du noeud
   */
  public Set<ISectionAgent> getInSections() {
    return this.inSections;
  }

  public void setInSections(Set<ISectionAgent> sections) {
    this.inSections = sections;
  }

  /**
     */
  private Set<ISectionAgent> outSections = null;

  /**
   * @return les troncons sortants du noeud
   */
  public Set<ISectionAgent> getOutSections() {
    return this.outSections;
  }

  public void setOutSections(Set<ISectionAgent> sections) {
    this.outSections = sections;
  }

  @Override
  public IPoint getGeom() {
    return this.getFeature().getGeom();
  }

  @Override
  public IPoint getInitialGeom() {
    return (IPoint) super.getInitialGeom();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }

}
