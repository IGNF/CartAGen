/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.cartagen.agents.gael.deformation.triangulation.TriangulationGAEL;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * an agent which is able to deform using GAEL
 * @author JGaffuri
 */
public class GAELDeformableImpl implements GAELDeformable {
  private static Logger logger = LogManager
      .getLogger(GAELDeformableImpl.class.getName());

  private IFeatureCollection<IPointAgent> pointAgents = new FT_FeatureCollection<IPointAgent>();

  private int id;
  private static AtomicInteger counter = new AtomicInteger();

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public GAELDeformableImpl() {
    this.id = counter.getAndIncrement();
  }

  /**
   * @return
   */
  @Override
  public IFeatureCollection<IPointAgent> getPointAgents() {
    return this.pointAgents;
  }

  /**
   * les points singleton
   */
  private IFeatureCollection<GAELPointSingleton> pointsSingleton = new FT_FeatureCollection<GAELPointSingleton>();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * getPointsSingleton ()
   */
  @Override
  public IFeatureCollection<GAELPointSingleton> getPointSingletons() {
    return this.pointsSingleton;
  }

  /**
   * les segments
   */
  private IFeatureCollection<GAELSegment> segments = new FT_FeatureCollection<GAELSegment>();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#getSegments()
   */
  /**
   * @return
   */
  @Override
  public IFeatureCollection<GAELSegment> getSegments() {
    return this.segments;
  }

  /**
   * les triangles
   */
  private IFeatureCollection<GAELTriangle> triangles = new FT_FeatureCollection<GAELTriangle>();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#getTriangles()
   */
  /**
   * @return
   */
  @Override
  public IFeatureCollection<GAELTriangle> getTriangles() {
    return this.triangles;
  }

  /**
   * les angles
   */
  private IFeatureCollection<GAELAngle> angles = new FT_FeatureCollection<GAELAngle>();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#getAngles()
   */
  /**
   * @return
   */
  @Override
  public IFeatureCollection<GAELAngle> getAngles() {
    return this.angles;
  }

  /**
   * les agents points en cours d'activation pour deformation
   */
  private ArrayList<IPointAgent> listeAgentsPoints = new ArrayList<IPointAgent>();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * getListeAgentsPoints ()
   */
  /**
   * @return
   */
  @Override
  public ArrayList<IPointAgent> getListeAgentsPoints() {
    return this.listeAgentsPoints;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * cleanDecomposition ()
   */
  @Override
  public void cleanDecomposition() {
    this.supprimerContraintesSubmicro();

    for (IPointAgent ap : this.getPointAgents()) {
      for (ISubMicro sm : ap.getSubmicros()) {
        sm.clean();
      }
      ap.clean();
    }
    if (this.getListeAgentsPoints() != null) {
      this.getListeAgentsPoints().clear();
    }
    if (this.getPointAgents() != null) {
      this.getPointAgents().clear();
    }
    if (this.getPointSingletons() != null) {
      this.getPointSingletons().clear();
    }
    if (this.getSegments() != null) {
      this.getSegments().clear();
    }
    if (this.getAngles() != null) {
      this.getAngles().clear();
    }
    if (this.getTriangles() != null) {
      this.getTriangles().clear();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * activatePointAgents ()
   */
  @Override
  public void activatePointAgents() throws InterruptedException {
    this.activatePointAgents(-1);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * activatePointAgents (int)
   */
  @Override
  public void activatePointAgents(int nbLimite) throws InterruptedException {
    if (GAELDeformableImpl.logger.isDebugEnabled()) {
      GAELDeformableImpl.logger.debug("activation des agents point de " + this
          + ". " + this.getListeAgentsPoints().size()
          + " agents points a activer.");
    }

    // active les agents point de la liste tant qu'elle n'est pas vide
    IPointAgent ap;
    int i = 0;
    while (true) {
      if (this.getListeAgentsPoints().isEmpty()) {
        return;
      }
      if (nbLimite > 0 && i >= nbLimite) {
        return;
      }

      i++;

      // recupere un agent point au hasard
      ap = this.getListeAgentsPoints()
          .get((int) (this.getListeAgentsPoints().size() * Math.random()));

      // pause
      // try {Thread.sleep(500);} catch (InterruptedException e)
      // {e.printStackTrace();}

      // active l'agent point
      if (GAELDeformableImpl.logger.isTraceEnabled()) {
        GAELDeformableImpl.logger.trace(" activation de agent point " + ap);
      }
      AgentSatisfactionState res = ap.activate();

      // traite resultat

      if (res == AgentSatisfactionState.PERFECTLY_SATISFIED_INITIALY) {
        if (GAELDeformableImpl.logger.isTraceEnabled()) {
          GAELDeformableImpl.logger
              .trace("   parfaitement satisfait initialement");
        }
        // retirer agent point de liste
        this.getListeAgentsPoints().remove(ap);
        ap.setDansListe(false);
      }

      else if (res == AgentSatisfactionState.PERFECTLY_SATISFIED_AFTER_TRANSFORMATION) {
        if (GAELDeformableImpl.logger.isTraceEnabled()) {
          GAELDeformableImpl.logger
              .trace("   parfaitement satisfait apres deplacement");
        }
        // retirer agent point de liste
        this.getListeAgentsPoints().remove(ap);
        ap.setDansListe(false);
        // ajouter agents point accointants
        this.reveillerPointsAccointants(ap);
      }

      else if (res == AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT) {
        if (GAELDeformableImpl.logger.isTraceEnabled()) {
          GAELDeformableImpl.logger.trace("   mieux mais pas parfait");
        }
        // ajouter agents point accointants
        this.reveillerPointsAccointants(ap);
      }

      else if (res == AgentSatisfactionState.SATISFACTION_UNCHANGED) {
        if (GAELDeformableImpl.logger.isTraceEnabled()) {
          GAELDeformableImpl.logger.trace("   pareil");
        }
        // retirer agent point de liste
        this.getListeAgentsPoints().remove(ap);
        ap.setDansListe(false);
      } else {
        GAELDeformableImpl.logger.error(
            "erreur lors de l'activation de " + ap + " de l'agent " + this
                + ". resultat d'activation d'agent point non traite: " + res);
      }

    }

  }

  /**
   * place les agents point accointant de l'agent point ap dans la liste des
   * agents points à activer de l'agent.
   * @param ap l'agent point dont les agents point accointants doivent etre
   *          activer
   */
  protected void reveillerPointsAccointants(IPointAgent ap) {

    for (IPointAgent ap_ : ap.getAgentPointAccointants()) {
      // place chaque point dans la file du moteur, s'il n'y est pas déjà.
      if (!ap_.isDansListe() && !ap_.isFixe() && !ap_.isSelectionne()) {
        if (GAELDeformableImpl.logger.isTraceEnabled()) {
          GAELDeformableImpl.logger.trace("			 ajout " + ap_);
        }
        this.getListeAgentsPoints().add(ap_);
        ap_.setDansListe(true);
      }
    }

  }

  // parmi les agents-point de l'agent geo, retourne la plus insatisfait
  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * getPlusInsatisfait ()
   */
  @Override
  public IPointAgent getPlusInsatisfait() {
    IPointAgent apMax = null;
    double sMax = 0.0;
    // cet agent-point est forcement dans listeAgentsPoints
    for (IPointAgent ap : this.getListeAgentsPoints()) {
      ap.computeSatisfaction();
      if (ap.getSatisfaction() > sMax) {
        apMax = ap;
        sMax = ap.getSatisfaction();
      }
    }
    return apMax;
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * chargerPointsNonEquilibres()
   */
  @Override
  public void chargerPointsNonEquilibres() {
    if (GAELDeformableImpl.logger.isDebugEnabled()) {
      GAELDeformableImpl.logger
          .debug("chargement des agents-point non en equilibre de " + this);
    }

    this.getListeAgentsPoints().clear();

    for (IPointAgent ap : this.getPointAgents()) {
      if (ap.isSelectionne() || ap.isFixe()) {
        continue;
      }
      ap.computeSatisfaction();
      if (GAELDeformableImpl.logger.isDebugEnabled()) {
        GAELDeformableImpl.logger.debug("S=" + ap.getSatisfaction());
      }
      if (!ap.satisfactionParfaite()) {
        if (GAELDeformableImpl.logger.isDebugEnabled()) {
          GAELDeformableImpl.logger.debug("ajout de " + ap + ". (satisfaction="
              + ap.getSatisfaction() + ")");
        }
        this.getListeAgentsPoints().add(ap);
        ap.setDansListe(true);
      }
    }
    // LoggerMirage.get().info("********* Fin de chargement des agents-point non
    // en equilibre de "+this);
  }

  // contraintes sur les points
  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContPointPosition(double)
   */
  @Override
  public void ajouterContPointPosition(double importance) {
    for (GAELPointSingleton ps : this.getPointSingletons()) {
      ps.addPositionConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContPointGarderAltitude(double)
   */
  @Override
  public void ajouterContPointGarderAltitude(double importance) {
    for (GAELPointSingleton ps : this.getPointSingletons()) {
      ps.addElevationConstraint(importance);
    }
  }

  // contraintes sur les segments
  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContSegmentLongueur(double)
   */
  @Override
  public void ajouterContSegmentLongueur(double importance) {
    for (GAELSegment s : this.getSegments()) {
      s.addLengthConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContSegmentLongueur(double, double)
   */
  @Override
  public void ajouterContSegmentLongueur(double importance,
      double longueurBut) {
    for (GAELSegment s : this.getSegments()) {
      s.addLengthConstraint(importance, longueurBut);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContSegmentOrientation(double)
   */
  @Override
  public void ajouterContSegmentOrientation(double importance) {
    for (GAELSegment s : this.getSegments()) {
      s.addOrientationConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContSegmentOrientation(double, double)
   */
  @Override
  public void ajouterContSegmentOrientation(double importance,
      double orientationBut) {
    for (GAELSegment s : this.getSegments()) {
      s.addOrientationConstraint(importance, orientationBut);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContSegmentCouler(double)
   */
  @Override
  public void ajouterContSegmentCouler(double importance) {
    for (GAELSegment s : this.getSegments()) {
      s.addOutflowConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContSegmentEtrePlat(double)
   */
  @Override
  public void ajouterContSegmentEtrePlat(double importance) {
    for (GAELSegment s : this.getSegments()) {
      s.addHorizontalityConstraint(importance);
    }
  }

  // contraintes sur les angles
  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContAngleValeur(double, double)
   */
  @Override
  public void ajouterContAngleValeur(double importance, double valeurBut) {
    for (GAELAngle a : this.getAngles()) {
      a.addValueConstraint(importance, valeurBut);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContAngleValeur(double)
   */
  @Override
  public void ajouterContAngleValeur(double importance) {
    for (GAELAngle a : this.getAngles()) {
      a.addValueConstraint(importance);
    }
  }

  // contraintes sur les triangles
  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContTriangleAire(double)
   */
  @Override
  public void ajouterContTriangleAire(double importance) {
    for (GAELTriangle t : this.getTriangles()) {
      t.addAreaConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContTriangleGarderG(double)
   */
  @Override
  public void ajouterContTriangleGarderG(double importance) {
    for (GAELTriangle t : this.getTriangles()) {
      t.addCenterPreservationConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContTriangleGarderOrientationAzimutalePente(double)
   */
  @Override
  public void ajouterContTriangleGarderOrientationAzimutalePente(
      double importance) {
    for (GAELTriangle t : this.getTriangles()) {
      t.addAzimutalSlopeOrientationPreservationConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContTriangleFaireCouler(double)
   */
  @Override
  public void ajouterContTriangleFaireCouler(double importance) {
    for (GAELTriangle t : this.getTriangles()) {
      t.addHydroSectionsOutflowConstraint(importance);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * ajouterContTriangleFaireGarderAltitude(double)
   */
  @Override
  public void ajouterContTriangleFaireGarderAltitude(double importance) {
    for (GAELTriangle t : this.getTriangles()) {
      t.addBuildingsElevationPreservationConstraint(importance);
    }
  }

  // supprime toutes les contraintes submicro
  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * supprimerContraintesSubmicro()
   */
  @Override
  public void supprimerContraintesSubmicro() {
    for (GAELPointSingleton ps : this.getPointSingletons()) {
      ps.getSubmicroConstraints().clear();
    }
    for (GAELSegment s : this.getSegments()) {
      s.getSubmicroConstraints().clear();
    }
    for (GAELTriangle t : this.getTriangles()) {
      t.getSubmicroConstraints().clear();
    }
    for (GAELAngle a : this.getAngles()) {
      a.getSubmicroConstraints().clear();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#getTriangle(
   * fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition)
   */
  @Override
  public GAELTriangle getTriangle(IDirectPosition pos) {

    // choisi l'ensemble de triangles parmi lesquels chercher:s'il y a un index
    // spatial, on l'utilise
    Collection<GAELTriangle> col = null;
    if (this.getTriangles().hasSpatialIndex()) {
      col = this.getTriangles().select(pos, 10.0);
      GAELDeformableImpl.logger
          .trace("OK ! " + col.size() + " < " + this.getTriangles().size());
    } else {
      col = this.getTriangles();
    }

    // renvoit le premier triangle trouve qui contient le point x,y
    for (GAELTriangle t : col) {
      if (t.contains(pos)) {
        return t;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#getSegment(fr
   * .ign.cogit.generalisation.gaeldeformation.PointAgent,
   * fr.ign.cogit.generalisation.gaeldeformation.PointAgent)
   */
  @Override
  public GAELSegment getSegment(IPointAgent p1, IPointAgent p2) {
    for (GAELSegment s : this.getSegments()) {
      if (p1.equals(s.getP1()) && p2.equals(s.getP2())
          || p2.equals(s.getP1()) && p1.equals(s.getP2())) {
        return s;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * getTriangleInitial
   * (fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition)
   */
  @Override
  public GAELTriangle getTriangleInitial(IDirectPosition pos) {
    for (GAELTriangle t : this.getTriangles()) {
      if (t.containsInitial(pos)) {
        return t;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * creerPointsSingletonsAPartirDesPoints()
   */
  @Override
  public void creerPointsSingletonsAPartirDesPoints() {
    for (IPointAgent ap : this.getPointAgents()) {
      if (ap.getPointSingleton() == null) {
        new GAELPointSingleton(this, ap);
      } else if (!this.getPointSingletons().contains(ap.getPointSingleton())) {
        this.getPointSingletons().add(ap.getPointSingleton());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * creerAnglesTriangles ()
   */
  @Override
  public void creerAnglesTriangles() {
    for (GAELTriangle t : this.getTriangles()) {
      t.buildAngles(this);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#triangule(
   * boolean )
   */
  @Override
  public void triangule(boolean construireTriangles, IGeometry geom) {
    TriangulationGAEL.compute(this, construireTriangles, geom);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#triangule()
   */
  @Override
  public void triangule() {
    this.triangule(true, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#getPoint(double,
   * double)
   */
  @Override
  public IPointAgent getPoint(double x, double y) {
    for (IPointAgent p : this.getPointAgents()) {
      if (p.getX() == x && p.getY() == y) {
        return p;
      }
    }
    return null;
  }

  // public void triangule(Vector<Point> pts) {
  // Point pi,pj,pk,pm;
  // double xn,yn,zn;
  // int i,j,k,m;
  // int n=pts.size();
  // for (i=0;i<n;i++){
  // pi=(Point)pts.elementAt(i);
  // for (j=i+1;j<n;j++){
  // pj=(Point)pts.elementAt(j);
  // for (k=i+1;k<n;k++){
  // pk=(Point)pts.elementAt(k);
  // if (j==k) continue;
  // //pi, pj et pk sont 3 noeuds distincts
  // //produit vectoriel de ij et ik
  // zn=(pj.getX()-pi.getX())*(pk.getY()-pi.getY())-(pj.getY()-pi.getY())*(pk.getX()-pi.getX());
  // if (zn>0) continue;
  // //pi,pj,pk sont distincts; l'angle (ij,ik) est direct, entre 0 et Pi
  // xn=(pj.getY()-pi.getY())*(pk.getCalc()-pi.getCalc())-(pk.getY()-pi.getY())*(pj.getCalc()-pi.getCalc());
  // yn=(pk.getX()-pi.getX())*(pj.getCalc()-pi.getCalc())-(pj.getX()-pi.getX())*(pk.getCalc()-pi.getCalc());
  // m=0;
  // for (m=0; m<n; m++){
  // pm=(Point) pts.elementAt(m);
  // //si pm est différent de pi,pj,pk et est à l'intérieur du cercle
  // circonscrit au triangle ijk, alors on sort.
  // if (m!=i && m!=j && m!=k &&
  // ((pm.getX()-pi.getX())*xn+(pm.getY()-pi.getY())*yn+(pm.getCalc()-pi.getCalc())*zn)
  // > 0) break;
  // }
  // if (m==n){
  // //on n'a pas trouvé de point pm à l'intérieur du triangle ijk: pi,pj et pk
  // forment un triangle de delaunay.
  // //creer de nouveaux segments uniquement s'ils n'existent pas déjà
  // if (getSegment(pi,pj)==null) new Segment(this,pi,pj);
  // if (getSegment(pj,pk)==null) new Segment(this,pj,pk);
  // if (getSegment(pk,pi)==null) new Segment(this,pk,pi);
  // new Triangle(this,pi,pj,pk);
  // }
  // }
  // }
  // }
  // }
  //
  // //
  // public void contraindreTriangulationDelaunay(Segment s) {
  // //chercher tous les segments de la triangulation coupant le segment s
  // Vector<Segment> sv=new Vector<Segment>();
  // for(Segment s0:segments) {
  // if (s0==s) continue;
  // if (s.coupe(s0)) sv.add(s0);
  // }
  // //récupérer les points situés à droite, puis ceux situés à gauche de s
  // Vector<Point> points1=new Vector<Point>();
  // Vector<Point> points2=new Vector<Point>();
  // for(Segment s0:sv) {
  // if (s.getPVCourant(s0.p1)<=0) {
  // if (!points1.contains(s0.p1)) points1.add(s0.p1);
  // if (!points2.contains(s0.p2)) points2.add(s0.p2);
  // }
  // else {
  // if (!points1.contains(s0.p2)) points1.add(s0.p2);
  // if (!points2.contains(s0.p1)) points2.add(s0.p1);
  // }
  // }
  //
  // //détruire tous les segments, tous les triangles associés
  // Triangle tri;
  // for(Segment s0:sv) {
  // //supprime les triangles eventuels
  // tri=s0.getTriangle(triangles);
  // if (tri!=null) {
  // triangles.remove(tri);
  // Triangle.LISTE.remove(tri);
  // }
  // tri=s0.getTriangle(triangles);
  // if (tri!=null) {
  // triangles.remove(tri);
  // Triangle.LISTE.remove(tri);
  // }
  // //supprime le segment
  // segments.removeElement(s0);
  // Segment.LISTE.remove(s0);
  // }
  //
  // //effectuer la triangulation des points de droite plus s.p1 et s.p2; idem
  // pour les points à gauche
  // points1.add(s.p1); points1.add(s.p2);
  // triangule(points1);
  // points2.add(s.p1); points2.add(s.p2);
  // triangule(points2);
  // }

  /*
   * //a verifier public void densifier(double pas){ //copie des données utiles
   * Vector<Point> pt2=(Vector<Point>) points.clone(); Vector<Segment>
   * s2=(Vector<Segment>) segments.clone(); //nettoyage des données du troncon
   * points.clear(); segments.clear();
   * 
   * //l'index du coté courant int i=0; //le lien courant Segment
   * l=(Segment)s2.elementAt(i); //le noeud courant Point
   * n=(Point)pt2.elementAt(0); points.add(n); //la distance parcourue sur le
   * lien courant. double dist=0.0; //la longeur du lien courant. double
   * dist_l=l.getLongueur(); while (true){ //on avance du pas dist+=pas*2; //si
   * on a trop avancé, on sort du lien while (dist>dist_l) { //on doit changer
   * de lien i++; //il n'y a plus de liens if (i==s2.size()) { //ajouter le
   * dernier noeud et le dernier lien Point
   * n2=(Point)pt2.elementAt(pt2.size()-1); points.add(n2); new
   * Segment(this,n,n2); return; } //on passe au lien suivant dist-=dist_l;
   * l=(Segment)s2.elementAt(i); dist_l=l.getLongueur(); } //on ajoute le
   * nouveau noeud Point p2=new Point(this, n.getX() +
   * pas*(l.p2.getX()-l.p1.getX())/dist_l, n.getY() +
   * pas*(l.p2.getY()-l.p1.getY())/dist_l,0); //on ajoute le nouveau segment new
   * Segment(this,n,p2); n=p2; } }
   */

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELDeformable#
   * construireSegments (boolean)
   */
  @Override
  public void construireSegments(boolean ferme) {
    Iterator<IPointAgent> it = this.getPointAgents().iterator();
    IPointAgent p_ = null, p1 = null, p2 = null;
    if (it.hasNext()) {
      p1 = it.next();
      p_ = p1;
    } else {
      return;
    }
    while (it.hasNext()) {
      p2 = it.next();
      new GAELSegment(this, p1, p2);
      p1 = p2;
    }
    if (ferme && p2 != p_) {
      this.getSegments().add(new GAELSegment(this, p2, p_));
    }
  }

  @Override
  public void registerDisplacement() {
    // TODO Auto-generated method stub
  }

}
