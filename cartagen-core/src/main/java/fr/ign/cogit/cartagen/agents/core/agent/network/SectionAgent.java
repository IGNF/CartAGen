/*
 * Créé le 10 août 2005
 */
package fr.ign.cogit.cartagen.agents.core.agent.network;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.MicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.constraint.section.Coalescence;
import fr.ign.cogit.cartagen.agents.core.constraint.section.DeformationControl;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author julien Gaffuri
 * 
 */
public abstract class SectionAgent extends MicroAgentGeneralisation
    implements ISectionAgent {
  @SuppressWarnings("unused")
  private static Logger logger = LogManager.getLogger(SectionAgent.class.getName());

  @Override
  public INetworkSection getFeature() {
    return (INetworkSection) super.getFeature();
  }

  /**
   * Network controlling the section
   */
  private NetworkAgent reseau;

  public NetworkAgent getNetwork() {
    return this.reseau;
  }

  public void setNetwork(NetworkAgent res) {
    this.reseau = res;
  }

  /**
   * Controlling meso section agent in case of decomposition of the section in
   * multiple micro section agents
   */
  private MesoSectionAgent controllingMeso;

  /**
   * @return
   */
  public MesoSectionAgent getControllingMeso() {
    return this.controllingMeso;
  }

  /**
   * @param meso
   */
  public void setControllingMeso(MesoSectionAgent meso) {
    this.controllingMeso = meso;
  }

  /**
   * Number of times that a micro agent has been triggered during the meso life
   * cycle
   */
  private int triggeredByMeso;

  /**
   * @return
   */
  public int getTriggeredByMeso() {
    return this.triggeredByMeso;
  }

  /**
   * @param nb
   */
  public void setTriggeredByMeso(int nb) {
    this.triggeredByMeso = nb;
  }

  /**
   * Initial and final nodes of the section
   */
  private NetworkNodeAgent initialNode, finalNode;

  public NetworkNodeAgent getInitialNode() {
    return this.initialNode;
  }

  public void setInitialNode(NetworkNodeAgent node) {
    this.initialNode = node;
  }

  public NetworkNodeAgent getFinalNode() {
    return this.finalNode;
  }

  public void setFinalNode(NetworkNodeAgent node) {
    this.finalNode = node;
  }

  @Override
  public void deleteAndRegister() {
    super.deleteAndRegister();
    this.getNetwork().getTroncons().remove(this.getFeature());
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  @Override
  public IGeometry getSymbolExtent() {
    return SectionSymbol.getSymbolExtent(this.getFeature());
  }

  @Override
  public IGeometry getUsedSymbolExtent() {
    return SectionSymbol.getUsedSymbolExtent(this.getFeature());
  }

  public void decompose() {

    // traite la premiere coordonnee
    IDirectPosition c = this.getGeom().coord().get(0);

    // cherche si un agent point n'existe pas deja dans le reseau a cette
    // position
    IPointAgent p = this.getNetwork().getPoint(c.getX(), c.getY());

    // il n'y a pas encore d'agent point: on en cree un nouveau
    if (p == null) {
      c.setZ(((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
              .getAltitude(c));
      p = new PointAgentImpl(this, c);
    }
    // sinon, on se rattache a celui existant
    else {
      p.getPositions().add(c);
      this.getPointAgents().add(p);
    }

    // relie l'agent point au noeud initial eventuel
    if (this.getInitialNode() != null) {
      p.getPositions().add(this.getInitialNode().getGeom().coord().get(0));
      this.getInitialNode().getPointAgents().add(p);
    }

    // traite les coodonnees du milieu
    for (int i = 1; i < this.getGeom().coord().size() - 1; i++) {
      c = this.getGeom().coord().get(i);

      // cree l'agent point
      c.setZ(((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
              .getAltitude(c));
      new PointAgentImpl(this, c);
    }

    // traite la derniere coordonnee
    c = this.getGeom().coord().get(this.getGeom().coord().size() - 1);

    // cherche si un agent point n'existe pas deja dans le reseau a cette
    // position
    p = this.getNetwork().getPoint(c.getX(), c.getY());

    // il n'y a pas encore d'agent point: on en cree un nouveau
    if (p == null) {
      c.setZ(((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
              .getAltitude(c));
      p = new PointAgentImpl(this, c);
    }
    // sinon, on se rattache a celui existant
    else {
      p.getPositions().add(c);
      this.getPointAgents().add(p);
    }

    // relie l'agent point au noeud final eventuel
    if (this.getFinalNode() != null) {
      p.getPositions().add(this.getFinalNode().getGeom().coord().get(0));
      this.getFinalNode().getPointAgents().add(p);
    }

    // construction des segments
    this.construireSegments(this.getGeom().isClosed(0.001));
  }

  // recupere les triangles d'un champ sous le troncons
  @Override
  public ArrayList<GAELTriangle> getTrianglesDessous(FieldAgent ac) {
    ArrayList<GAELTriangle> at = new ArrayList<GAELTriangle>();
    IDirectPositionList coords = this.getGeom().coord();
    int nb = coords.size();
    for (int i = 0; i < nb; i++) {
      GAELTriangle t = ac.getTriangle(coords.get(i));
      if (t != null && !at.contains(t)) {
        at.add(t);
      }
    }
    // System.out.println(" "+at.size()+" triangles trouves sous "+this);
    return at;
  }

  public void ajouterContrainteEmpatement(double imp) {
    new Coalescence(this, imp);
  }

  public void ajouterContrainteControleDeformation(double imp) {
    new DeformationControl(this, imp);
  }

}
