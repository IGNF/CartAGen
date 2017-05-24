package fr.ign.cogit.cartagen.agents.gael.deformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ContourLineAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.cartagen.graph.IGraphLinkableFeature;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.Agent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * The point fieldAgent class.
 * @author julien Gaffuri 26 juil. 2005
 */
public class PointAgentImpl extends Agent implements IPointAgent {
  private static Logger logger = Logger
      .getLogger(PointAgentImpl.class.getName());

  /**
   * All point agents
   */
  private static IFeatureCollection<IPointAgent> LISTE = new FT_FeatureCollection<IPointAgent>();

  /**
   * @return
   */
  public static IFeatureCollection<IPointAgent> getLISTE() {
    return PointAgentImpl.LISTE;
  }

  // compteur pour identifiant du point
  private static int COMPTEUR = 0;

  /**
   * Graph node on which the point agent is based
   */
  INode graphNode;

  public INode getGraphNode() {
    return this.graphNode;
  }

  /**
   * Les direct position lies a l'fieldAgent point
   */
  private IDirectPositionList DPList = new DirectPositionList();

  public IDirectPositionList getPositions() {
    return this.DPList;
  }

  @Override
  public IDirectPosition getPosition() {
    return this.DPList.get(0);
  }

  @Override
  public void updatePosition(IDirectPosition pos) {
    this.DPList.clear();
    this.DPList.add(pos);
    this.updateForces();
  }

  public double getX() {
    return this.getPositions().get(0).getX();
  }

  public void setX(double x) {
    for (IDirectPosition dp : this.getPositions()) {
      dp.setX(x);
    }
  }

  public double getY() {
    return this.getPositions().get(0).getY();
  }

  public void setY(double y) {
    for (IDirectPosition dp : this.getPositions()) {
      dp.setY(y);
    }
  }

  public double getZ() {
    return ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
        CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
            .getAltitude(this.getPosition());
  }

  public double getZIni() {
    return this.getPositions().get(0).getZ();
  }

  /**
   * coordonnées initiales: celles du point initial, sans aucune transformation.
   */
  private IDirectPosition positionIni;

  /**
   * @return
   */
  @Override
  public IDirectPosition getPositionIni() {
    return this.positionIni;
  }

  public double getXIni() {
    return this.getPositionIni().getX();
  }

  public double getYIni() {
    return this.getPositionIni().getY();
  }

  @Override
  public IPoint getGeom() {
    return (IPoint) this.getFeature().getGeom();
  }

  /**
   * les etats de l'fieldAgent point
   */
  private ArrayList<PointAgentState> etats;

  /**
   * @return
   */
  public ArrayList<PointAgentState> getEtats() {
    return this.etats;
  }

  public void effacerEtats() {
    if (this.etats == null) {
      return;
    }
    for (AgentState ea : this.etats) {
      ea.clean();
    }
    this.etats = null;
  }

  public boolean dansListe = false;

  public boolean isDansListe() {
    return this.dansListe;
  }

  public void setDansListe(boolean dansListe) {
    this.dansListe = dansListe;
  }

  /**
   * les submicros auxquels le point appartient
   */
  private ArrayList<ISubMicro> submicros = new ArrayList<ISubMicro>();

  /**
   * @return
   */
  public ArrayList<ISubMicro> getSubmicros() {
    return this.submicros;
  }

  /**
   * le point singleton associe eventuel
   */
  private GAELPointSingleton pointSingleton = null;

  /**
   * @return
   */
  public GAELPointSingleton getPointSingleton() {
    return this.pointSingleton;
  }

  /**
   * @param pointSingleton
   */
  public void setPointSingleton(GAELPointSingleton pointSingleton) {
    this.pointSingleton = pointSingleton;
  }

  /**
   * fixe si on n'autorise pas à bouger
   */
  private boolean fixe = false;

  /**
   * @return
   */
  public boolean isFixe() {
    return this.fixe;
  }

  /**
   * @param fixe
   */
  public void setFixe(boolean fixe) {
    this.fixe = fixe;
  }

  private boolean selectionne = false;

  /**
   * @return
   */
  public boolean isSelectionne() {
    return this.selectionne;
  }

  /**
   * @param selectionne
   */
  public void setSelectionne(boolean selectionne) {
    this.selectionne = selectionne;
  }

  /**
   * la somme des importances des contraintes de l'Agent point
   */
  private double sommeImportances = 0.0;

  /**
   * @return
   */
  public double getSommeImportances() {
    return this.sommeImportances;
  }

  public void incrementerSommeImportances(double dimp) {
    this.sommeImportances += dimp;
  }

  /**
   * champ utilise dans la triangulation: pour connaitre la position du point
   * dans la liste des points
   */
  private int posTri;

  @Override
  public int getIndex() {
    return this.posTri;
  }

  @Override
  public void setIndex(int posTri) {
    this.posTri = posTri;
  }

  /**
   * les agents point accointants, c'est a dire ceux appartenant aux memes
   * objets submicros
   */
  private ArrayList<IPointAgent> agentPointAccointants = new ArrayList<IPointAgent>();

  /**
   * @return
   */
  public ArrayList<IPointAgent> getAgentPointAccointants() {
    return this.agentPointAccointants;
  }

  public void addAgentPointAccointants(IPointAgent ap) {
    this.agentPointAccointants.add(ap);
  }

  private ContourLineAgent cn = null;

  /**
   * @return la courbe de niveau eventuelle a laquelle le point appartient
   */
  public ContourLineAgent getCourbeDeNiveau() {
    return this.cn;
  }

  /**
   * Constructeur
   * @param point
   */

  public PointAgentImpl(IDirectPosition dp) {
    this.setFeature(new DefaultFeature());
    this.setLifeCycle(PointAgentLifeCycle.getInstance());

    PointAgentImpl.getLISTE().add(this);

    this.getPositions().add(dp);
    this.positionIni = new DirectPosition(dp.getX(), dp.getY(), dp.getZ());

    // cree geometrie du point liee a la position de l'fieldAgent point
    GM_Point pt = new GM_Point();
    pt.setPosition(dp);
    this.setGeom(pt);

    // donne son identifiant au point
    this.setId(PointAgentImpl.COMPTEUR++);
  }

  /**
   * un fieldAgent geographique objet utilisé par exemple pour la triangulation
   * dans ilot pour conserver le lien entre un batiment et l'fieldAgent point
   * representant son centre
   */
  private GAELLinkableFeature linkedFeature = null;

  /**
   * @return
   */
  public GAELLinkableFeature getLinkedFeature() {
    return this.linkedFeature;
  }

  /**
   * @param linkedFeature
   */
  public void setLinkedFeature(GAELLinkableFeature linkedFeature) {
    this.linkedFeature = linkedFeature;
  }

  private FieldAgent fieldAgent;

  public PointAgentImpl(GAELDeformable agentGeo, IDirectPosition dp) {
    this.setFeature(new DefaultFeature());
    this.setLifeCycle(PointAgentLifeCycle.getInstance());

    PointAgentImpl.getLISTE().add(this);

    this.getPositions().add(dp);
    this.positionIni = new DirectPosition(dp.getX(), dp.getY(), dp.getZ());

    // cree geometrie du point liee a la position de l'fieldAgent point
    GM_Point pt = new GM_Point();
    pt.setPosition(dp);
    this.setGeom(pt);

    // donne son identifiant au point
    this.setId(PointAgentImpl.COMPTEUR++);

    if (agentGeo instanceof FieldAgent) {
      this.setFieldAgent((FieldAgent) agentGeo);
      ((FieldAgent) agentGeo).getGAELDeformable().getPointAgents().add(this);
    } else {
      agentGeo.getPointAgents().add(this);
    }

  }

  @Override
  public void clean() {
    super.clean();
    PointAgentImpl.getLISTE().remove(this);
    this.getAgentPointAccointants().clear();
    this.getSubmicros().clear();
    this.setPointSingleton(null);
    this.cn = null;
    if (this.getLinkedFeature() != null) {
      this.getLinkedFeature().clean();
      this.setLinkedFeature(null);
    }
    this.effacerEtats();
    if (this.getEtats() != null) {
      this.getEtats().clear();
    }
  }

  @Override
  public void goBackToState(AgentState ea) {
    this.goBackToState((PointAgentState) ea);
  }

  public void goBackToState(PointAgentState eap) {
    super.goBackToState(eap);
    for (IDirectPosition dp : this.getPositions()) {
      dp.setX(eap.getX());
      dp.setY(eap.getY());
    }
    if (this.getLinkedFeature() != null) {
      this.getLinkedFeature().goBackToState(eap.getLinkedFeatureState());
    }
  }

  public double getDistance(GAELSegment s) {
    if ((s.getP2().getX() - s.getP1().getX()) * (this.getX() - s.getP1().getX())
        + (s.getP2().getY() - s.getP1().getY())
            * (this.getY() - s.getP1().getY()) <= 0.0) {
      return this.getDistance(s.getP1());
    } else if ((s.getP1().getX() - s.getP2().getX())
        * (this.getX() - s.getP2().getX())
        + (s.getP1().getY() - s.getP2().getY())
            * (this.getY() - s.getP2().getY()) <= 0.0) {
      return this.getDistance(s.getP2());
    } else {
      return Math.abs(((s.getP1().getX() - this.getX())
          * (s.getP1().getY() - s.getP2().getY())
          + (s.getP1().getY() - this.getY())
              * (s.getP2().getX() - s.getP1().getX()))
          / Math.sqrt(Math.pow(s.getP2().getX() - s.getP1().getX(), 2.0)
              + Math.pow(s.getP2().getY() - s.getP1().getY(), 2.0)));
    }
  }

  public double getDistance(double x_, double y_) {
    return Math.sqrt((this.getX() - x_) * (this.getX() - x_)
        + (this.getY() - y_) * (this.getY() - y_));
  }

  public double getDistance(IPointAgent p) {
    return this.getDistance(p.getX(), p.getY());
  }

  public double getDistanceCourante(GAELSegment s) {
    if ((s.getP2().getX() - s.getP1().getX()) * (this.getX() - s.getP1().getX())
        + (s.getP2().getY() - s.getP1().getY())
            * (this.getY() - s.getP1().getY()) <= 0.0) {
      return this.getDistanceCourante(s.getP1());
    } else if ((s.getP1().getX() - s.getP2().getX())
        * (this.getX() - s.getP2().getX())
        + (s.getP1().getY() - s.getP2().getY())
            * (this.getY() - s.getP2().getY()) <= 0.0) {
      return this.getDistanceCourante(s.getP2());
    } else {
      return Math.abs(((s.getP1().getX() - this.getX())
          * (s.getP1().getY() - s.getP2().getY())
          + (s.getP1().getY() - this.getY())
              * (s.getP2().getX() - s.getP1().getX()))
          / Math.sqrt(Math.pow(s.getP2().getX() - s.getP1().getX(), 2.0)
              + Math.pow(s.getP2().getY() - s.getP1().getY(), 2.0)));
    }
  }

  public IDirectPosition getProj(GAELSegment s) {
    double ps = (s.getP2().getX() - s.getP1().getX())
        * (this.getX() - s.getP1().getX())
        + (s.getP2().getY() - s.getP1().getY())
            * (this.getY() - s.getP1().getY());
    double dc = Math.pow(s.getP2().getX() - s.getP1().getX(), 2.0)
        + Math.pow(s.getP2().getY() - s.getP1().getY(), 2.0);
    return new DirectPosition(
        s.getP1().getX() + ps * (s.getP2().getX() - s.getP1().getX()) / dc,
        s.getP1().getY() + ps * (s.getP2().getY() - s.getP1().getY()) / dc);
  }

  public double getDistanceInitiale(double x_, double y_) {
    return Math.sqrt((this.getXIni() - x_) * (this.getXIni() - x_)
        + (this.getYIni() - y_) * (this.getYIni() - y_));
  }

  public double getDistanceCourante(double x_, double y_) {
    return this.getDistance(x_, y_);
  }

  public double getDistanceInitiale(IPointAgent p) {
    return this.getDistanceInitiale(p.getXIni(), p.getYIni());
  }

  public double getDistanceCourante(IPointAgent p) {
    return this.getDistance(p);
  }

  public double getDistanceAPositionInitiale() {
    return Math
        .sqrt((this.getXIni() - this.getX()) * (this.getXIni() - this.getX())
            + (this.getYIni() - this.getY()) * (this.getYIni() - this.getY()));
  }

  public double getInitialOrientation(IPointAgent p) {
    return Math.atan2(p.getYIni() - this.getYIni(),
        p.getXIni() - this.getXIni());
  }

  /**
   * orientation de this a p, en radians entre -PI et PI
   * @param p
   * @return
   */
  public double getOrientation(IPointAgent p) {
    return Math.atan2(p.getY() - this.getY(), p.getX() - this.getX());
  }

  public double getOrientationEcart(IPointAgent p) {
    double ecart = this.getOrientation(p) - this.getInitialOrientation(p);
    if (ecart < -Math.PI) {
      return ecart + 2.0 * Math.PI;
    } else if (ecart > Math.PI) {
      return ecart - 2.0 * Math.PI;
    } else {
      return ecart;
    }
  }

  @Override
  public boolean isLinkedBySegment(TriangulationPoint point) {

    // parcours les submicros du point pour trouver les segments
    for (ISubMicro sm : this.getSubmicros()) {

      // si sm n'est pas un segment, continuer
      if (!(sm instanceof GAELSegment)) {
        continue;
      }

      // sm est un segment. renvoie true si les deux points sont lies par le
      // segment
      GAELSegment s = (GAELSegment) sm;
      IPointAgent p = (IPointAgent) point;
      if (s.getP1() == this && s.getP2() == p
          || s.getP2() == this && s.getP1() == p) {
        return true;
      }
    }
    return false;

    // autre possibilite: faire intersection des submicros des deux point et
    // voir s'il n'y a pas un segment parmi eux.
  }

  @Override
  public void run() {
    if (PointAgentImpl.logger.isDebugEnabled()) {
      PointAgentImpl.logger.debug("Lancement de thread de " + this);
    }

    while (true) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (this.fixe || this.selectionne || this.getConstraints().size() == 0) {
        continue;
      }

      if (PointAgentImpl.logger.isTraceEnabled()) {
        PointAgentImpl.logger.trace("recupere les deplacements de " + this);
      }
      this.updateActionProposals();

      if (PointAgentImpl.logger.isTraceEnabled()) {
        PointAgentImpl.logger.trace("calcule somme");
      }
      double dx = 0.0, dy = 0.0;
      PointAgentDisplacementAction dep = null;
      for (ActionProposal actionProposal : this.getActionProposals()) {
        dep = (PointAgentDisplacementAction) actionProposal.getAction();
        dx += dep.getDx();
        dy += dep.getDy();
        if (PointAgentImpl.logger.isTraceEnabled()) {
          PointAgentImpl.logger.trace(dep);
        }
      }

      if (PointAgentImpl.logger.isTraceEnabled()) {
        PointAgentImpl.logger
            .trace("effectue deplacement: dx=" + dx + ", dy=" + dy);
      }
      new PointAgentDisplacementAction(this, null, dx, dy).compute();
    }
  }

  @Override
  public void updateActionProposals() {
    this.updateForces();
  }

  @Override
  public void updateForces() {
    this.setActionsToTry(new HashSet<ActionProposal>());
    // System.out.println(this.getConstraints());
    for (Constraint cont : this.getConstraints()) {
      // System.out.println("recupere les deplacements de " + cont);
      if (PointAgentImpl.logger.isTraceEnabled()) {
        PointAgentImpl.logger.trace("recupere les deplacements de " + cont);
      }
      ((SubmicroConstraint) cont).proposeDisplacement(this);
    }
  }

  @Override
  public void computeSatisfaction() {
    this.computeForces();
  }

  public double getDistancesFromBalance() {
    return this.getSatisfaction();
  }

  @Override
  public Set<ActionProposal> getForces() {
    return this.getActionProposals();
  }

  @Override
  public void computeForces() {
    // si le point est fixe ou selectionne, il est satisfait
    if (this.fixe || this.selectionne || this.getConstraints().size() == 0) {
      // logger.info("point fixe ou selectionne");
      this.setSatisfaction(0.0);
      return;
    }

    this.updateForces();
    double dx = 0.0, dy = 0.0;
    for (ActionProposal actionProposal : this.getActionProposals()) {
      PointAgentDisplacementAction dep = (PointAgentDisplacementAction) actionProposal
          .getAction();
      dx += dep.getDx();
      dy += dep.getDy();
    }

    // Important: effacer la liste des actions
    this.setActionsToTry(new HashSet<ActionProposal>());
    this.setSatisfaction(Math.sqrt(dx * dx + dy * dy));

  }

  public boolean satisfactionParfaite() {
    return this.getSatisfaction() < GeneralisationSpecifications
        .getRESOLUTION();
  }

  @Override
  public boolean intersecte(IEnvelope env) {
    return env.contains(this.getX(), this.getY());
  }

  @Override
  public String toString() {
    return "AgentPoint" + Integer.toString(this.getId());
  }

  /*
   * public void actionApresSatisfactionParfaite(){ //quitte la file
   * estDansListe=false;
   * //AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.remove(this);
   * 
   * //l'fieldAgent s'est déplace: il reveille ses voisins for (AgentPoint
   * ap_:agentPointAccointants) { //place chaque point dans la liste de
   * l'fieldAgent, s'il n'y est pas déjà. if (!ap_.estDansListe && !ap_.fixe &&
   * ap_!=PanelVisu.pointSelectionne) {
   * //AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.add(ap_);
   * ap_.estDansListe=true; } }
   * 
   * //enregistre l'etat if
   * (MenuMirage.get().mAgentsPointEnregistrerTrajectoire.getState())
   * etats.add(new EtatAgentPoint(this));
   * 
   * //interface if (PanelControleMoteur.cSuivreGraphique.isSelected()) {
   * //PanelControleMoteur
   * .donneesGraphique.add(PanelControleMoteur.indexGraphique,
   * AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.size());
   * PanelControleMoteur.indexGraphique++; } }
   * 
   * //action effectuee par l'fieldAgent apres qu'il a ameliore sa satisfaction;
   * sa satisfaction n'est pas parfaite public void
   * actionApresSatisfactionAmelioree(){ //il reste dans la file
   * //AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.add(this);
   * //estDansListe=true;
   * 
   * //l'fieldAgent s'est déplace: il reveille ses voisins for (AgentPoint
   * ap_:agentPointAccointants) { //place chaque point dans la file du moteur,
   * s'il n'y est pas déjà. if (!ap_.estDansListe && !ap_.fixe &&
   * ap_!=PanelVisu.pointSelectionne) {
   * //AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.add(ap_);
   * ap_.estDansListe=true; } }
   * 
   * //enregistre l'etat if
   * (MenuMirage.get().mAgentsPointEnregistrerTrajectoire.getState())
   * etats.add(new EtatAgentPoint(this));
   * 
   * //interface if (PanelControleMoteur.cSuivreGraphique.isSelected()) {
   * //PanelControleMoteur
   * .donneesGraphique.add(PanelControleMoteur.indexGraphique,
   * AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.size());
   * PanelControleMoteur.indexGraphique++; }
   * 
   * //vide la liste des plans pour etre sur de quitter son cycle de vie
   * plans=new ArrayList<Plan>(); }
   * 
   * public void actionApresSatisfactionDeterioree(){ //s'il n'a plus de plan a
   * essayer, il quitte la file if (plans.size()==0) {
   * //AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.remove(this);
   * estDansListe=false; }
   * 
   * //retrouve son etat precedent retrouverEtatPrecedent();
   * 
   * //interface if (PanelControleMoteur.cSuivreGraphique.isSelected()) {
   * //PanelControleMoteur
   * .donneesGraphique.add(PanelControleMoteur.indexGraphique,
   * AGENT_GEO_EN_DEFORMATION.listeAgentsPoints.size());
   * PanelControleMoteur.indexGraphique++; } }
   */

  /**
   * the feature the fieldAgent is on
   */
  private IFeature feature = null;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#getFeature()
   */
  /**
   * @return
   */
  public IFeature getFeature() {
    return this.feature;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.Agent#setFeature(fr.ign.cogit.geoxygene
   * .api.feature.IFeature)
   */
  /**
   * @param feature
   */
  public void setFeature(IFeature feature) {
    this.feature = feature;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#addAllCorrespondants(java.util
   * .Collection)
   */
  @Override
  public void addAllCorrespondants(Collection<IFeature> c) {
    this.getFeature().addAllCorrespondants(c);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#addCorrespondant(fr.ign.cogit
   * .geoxygene.api.feature.IFeature)
   */
  @Override
  public void addCorrespondant(IFeature O) {
    this.getFeature().addCorrespondant(O);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#clearCorrespondants()
   */
  @Override
  public void clearCorrespondants() {
    this.getFeature().clearCorrespondants();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#cloneGeom()
   */
  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return this.getFeature().cloneGeom();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#isDeleted()
   */
  @Override
  public boolean isDeleted() {
    return this.getFeature().isDeleted();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getAttribute(fr.ign.cogit.
   * geoxygene .schema.schemaConceptuelISOJeu.AttributeType)
   */
  @Override
  public Object getAttribute(GF_AttributeType attribute) {
    return this.getFeature().getAttribute(attribute);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#getAttribute(java.lang.String)
   */
  @Override
  public Object getAttribute(String nomAttribut) {
    return this.getFeature().getAttribute(nomAttribut);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getCorrespondant(int)
   */
  @Override
  public IFeature getCorrespondant(int i) {
    return this.getFeature().getCorrespondant(i);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getCorrespondants()
   */
  @Override
  public List<IFeature> getCorrespondants() {
    return this.getFeature().getCorrespondants();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#getCorrespondants(fr.ign.cogit
   * .geoxygene.api.feature.IFeatureCollection)
   */
  @Override
  public Collection<IFeature> getCorrespondants(
      IFeatureCollection<? extends IFeature> pop) {
    return this.getFeature().getCorrespondants(pop);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getFeatureCollection(int)
   */
  @Override
  public IFeatureCollection<IFeature> getFeatureCollection(int i) {
    return this.getFeature().getFeatureCollection(i);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getFeatureCollections()
   */
  @Override
  public List<IFeatureCollection<IFeature>> getFeatureCollections() {
    return this.getFeature().getFeatureCollections();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getFeatureType()
   */
  @Override
  public GF_FeatureType getFeatureType() {
    return this.getFeature().getFeatureType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getId()
   */
  @Override
  public int getId() {
    return this.getFeature().getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getPopulation()
   */
  @Override
  public IPopulation<? extends IFeature> getPopulation() {
    return this.getFeature().getPopulation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#getRelatedFeatures(fr.ign.cogit
   * .geoxygene.api.feature.type.GF_FeatureType,
   * fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationRole)
   */
  @Override
  public List<? extends IFeature> getRelatedFeatures(GF_FeatureType ftt,
      GF_AssociationRole role) {
    return this.getFeature().getRelatedFeatures(ftt, role);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#getRelatedFeatures(java.lang
   * .String, java.lang.String)
   */
  @Override
  public List<? extends IFeature> getRelatedFeatures(String nomFeatureType,
      String nomRole) {
    return this.getFeature().getRelatedFeatures(nomFeatureType, nomRole);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getRepresentation()
   */
  @Override
  public Representation getRepresentation() {
    return this.getFeature().getRepresentation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getTopo()
   */
  @Override
  public ITopology getTopo() {
    return this.getFeature().getTopo();
  }

  @Override
  public boolean hasGeom() {
    return this.getFeature().hasGeom();
  }

  @Override
  public boolean hasTopo() {
    return this.getFeature().hasTopo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#removeCorrespondant(fr.ign.
   * cogit.geoxygene.api.feature.IFeature)
   */
  @Override
  public void removeCorrespondant(IFeature O) {
    this.getFeature().removeCorrespondant(O);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setAttribute(fr.ign.cogit.
   * geoxygene .schema.schemaConceptuelISOJeu.AttributeType, java.lang.Object)
   */
  @Override
  public void setAttribute(GF_AttributeType attribute, Object valeur) {
    this.getFeature().setAttribute(attribute, valeur);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setCorrespondants(java.util
   * .List)
   */
  @Override
  public void setCorrespondants(List<IFeature> L) {
    this.getFeature().setCorrespondants(L);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setEstSupprime(boolean)
   */
  @Override
  public void setDeleted(boolean deleted) {
    this.getFeature().setDeleted(deleted);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setFeatureType(fr.ign.cogit
   * .geoxygene.api.feature.type.GF_FeatureType)
   */
  @Override
  public void setFeatureType(GF_FeatureType featureType) {
    this.getFeature().setFeatureType(featureType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setGeom(fr.ign.cogit.geoxygene
   * .api.spatial.geomroot.IGeometry)
   */
  @Override
  public void setGeom(IGeometry g) {
    this.getFeature().setGeom(g);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setId(int)
   */
  @Override
  public void setId(int Id) {
    this.getFeature().setId(Id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setPopulation(fr.ign.cogit.
   * geoxygene.api.feature.IPopulation)
   */
  @Override
  public void setPopulation(IPopulation<? extends IFeature> population) {
    this.getFeature().setPopulation(population);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setRepresentation(fr.ign.cogit
   * .geoxygene.api.feature.Representation)
   */
  @Override
  public void setRepresentation(Representation rep) {
    this.getFeature().setRepresentation(rep);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setTopo(fr.ign.cogit.geoxygene
   * .spatial.toporoot.TP_Object)
   */
  @Override
  public void setTopo(ITopology t) {
    this.getFeature().setTopo(t);
  }

  public void setFieldAgent(FieldAgent agent) {
    this.fieldAgent = agent;
  }

  public FieldAgent getFieldAgent() {
    return this.fieldAgent;
  }

  @Override
  public Set<IFeature> getGeoObjects() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setGeoObjects(Set<IFeature> geoObjects) {
    // TODO Auto-generated method stub

  }

  @Override
  public IGraphLinkableFeature getGraphLinkableFeature() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IEdge> getEdgesIn() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEdgesIn(Set<IEdge> edgesIn) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addEdgeIn(IEdge edgeIn) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<IEdge> getEdgesOut() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEdgesOut(Set<IEdge> edgesOut) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addEdgeOut(IEdge edgeOut) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<IEdge> getEdges() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getDegree() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setGeom(IPoint geom) {
    // TODO Auto-generated method stub

  }

  @Override
  public IGraph getGraph() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setGraph(IGraph graph) {
    // TODO Auto-generated method stub

  }

  @Override
  public double getProximityCentrality() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getBetweenCentrality() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Set<INode> getNextNodes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<INode, IEdge> getNeighbourEdgeNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setGraphLinkableFeature(IGraphLinkableFeature feature) {
    // TODO Auto-generated method stub

  }

}
