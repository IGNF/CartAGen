/*
 * Créé le 9 août 2005
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent.relief;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.relational.pointsegment.MinimalDistance;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.ContoursMinimalDistance;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.spatialanalysis.fields.FieldEnrichment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * @author julien Gaffuri
 */
public final class ReliefFieldAgent extends FieldAgent {
  private static Logger logger = Logger.getLogger(ReliefFieldAgent.class);

  private IReliefField geneObj = null;

  public IReliefField getFeature() {
    return this.geneObj;
  }

  public void setFeature(IReliefField geoObj) {
    geoObj.addToGeneArtifacts(this);
    this.geneObj = geoObj;
  }

  public ReliefFieldAgent(IReliefField field) {
    super();
    this.setFeature(field);
  }

  /**
     */
  private Set<ContourLineAgent> contourLines = new HashSet<ContourLineAgent>();

  /**
   * @return les courbes de niveau du champ
   */
  public Set<ContourLineAgent> getContourLines() {
    return this.contourLines;
  }

  /**
     */
  private Set<SpotHeightAgent> spotHeights = new HashSet<SpotHeightAgent>();

  /**
   * @return les points cotes du champ
   */
  public Set<SpotHeightAgent> getSpotHeights() {
    return this.spotHeights;
  }

  /**
     */
  private Set<EmbankmentAgent> embankments = new HashSet<EmbankmentAgent>();

  /**
   * @return les talus du champ
   */
  public Set<EmbankmentAgent> getEmbankments() {
    return this.embankments;
  }

  /**
     */
  private Set<DEMPixelAgent> pixelsMNT = new HashSet<DEMPixelAgent>();

  /**
   * @return les pixels du champ
   */
  public Set<DEMPixelAgent> getPixelsMNT() {
    return this.pixelsMNT;
  }

  public void enrich() {

    if (ReliefFieldAgent.logger.isDebugEnabled()) {
      ReliefFieldAgent.logger.debug("recollement de CN de " + this);
    }
    FieldEnrichment.pickUpContourLines(this.getFeature());

    if (ReliefFieldAgent.logger.isDebugEnabled()) {
      ReliefFieldAgent.logger.debug("decomposition de " + this);
    }
    this.decompose();

    ReliefFieldAgent.logger
        .info("Construction de l'index spatial des triangles de " + this);
    this.getTriangles().initSpatialIndex(Tiling.class, true,
        (int) Math.sqrt(this.getTriangles().size()));
  }

  public void decompose() {
    ReliefFieldAgent.logger.info("Décomposition des "
        + this.getContourLines().size() + " courbes de niveau...");
    for (ContourLineAgent cn : this.getContourLines()) {
      cn.decompose();
    }

    ReliefFieldAgent.logger.info("Décomposition des "
        + this.getSpotHeights().size() + " points cotes...");
    for (SpotHeightAgent pt : this.getSpotHeights()) {
      pt.decompose();
    }

    ReliefFieldAgent.logger.info("Triangulation...");
    this.triangule();
  }

  @Override
  public void triangule() {
    GAELDeformable deformable = this.getGAELDeformable();
    for (ContourLineAgent agent : this.getContourLines()) {
      deformable.getSegments().addAll(agent.getSegments());
      deformable.getPointAgents().addAll(agent.getPointAgents());
    }
    for (SpotHeightAgent pt : this.getSpotHeights()) {
      deformable.getPointAgents().addAll(pt.getPointAgents());
    }
    super.triangule();
  }

  @Override
  public void cleanDecomposition() {
    super.cleanDecomposition();
    /*
     * for (IContourLine cn : this.getCourbesDeNiveau()) { ((ContourLineAgent)
     * AgentUtil.getAgentAgentFromGeneObj(cn)) .cleanDecomposition(); } for
     * (ISpotHeight pt : this.getSpotHeight()) { ((SpotHeightAgent)
     * AgentUtil.getAgentAgentFromGeneObj(pt)) .cleanDecomposition(); } for
     * (IReliefElementLine tal : this.getTalus()) {
     * AgentUtil.getAgentAgentFromGeneObj(tal).cleanDecomposition(); }
     */
  }

  /**
   * renvoit l'altitude ou -999.9 si elle n'est pas definie
   * @param x
   * @param y
   * @return
   */
  public double getAltitude(IDirectPosition pos) {
    GAELTriangle tri = this.getTriangle(pos);
    if (tri == null) {
      return -999.9;
    }
    return tri.getZ(pos);
  }

  /**
   * renvoit l'altitude initiale ou -999.9 si elle n'est pas definie
   * @param x
   * @param y
   * @return
   */
  public double getAltitudeInitiale(IDirectPosition pos) {
    GAELTriangle tri = this.getTriangleInitial(pos);
    if (tri == null) {
      return -999.9;
    }
    return tri.getZInitial(pos);
  }

  /**
   * renvoit le vecteur pente (le pv norme). si l'altitude n'est pas definie,
   * renvoit null
   * @param x
   * @param y
   * @return
   */
  public double[] getVecteurPente(IDirectPosition pos) {
    GAELTriangle tri = this.getTriangle(pos);
    if (tri == null) {
      return null;
    }
    return tri.getSlopeVector();
  }

  /**
   * renvoit l'angle en radian entre le plan horizontal et le triangle (diedre).
   * c'est pi/2 - zenithale ou angle entre le vecteur normal au triangle et la
   * verticale orientee vers le haut valeur entre 0 (plat) et Pi/2 (vertical).
   * si le mnt n'est pas defini, renvoit 999.9;
   * 
   * @param x
   * @param y
   * @return
   */
  public double getZenitalOrientation(IDirectPosition pos) {
    GAELTriangle tri = this.getTriangle(pos);
    if (tri == null) {
      return 999.9;
    }
    return tri.getSlopeAngle();
  }

  /**
   * renvoit la valeur de l'angle (en radian entre -Pi et Pi) entre le vecteur
   * de pente du triangle et l'axe des x si le mnt n'est pas defini, renvoit
   * 999.9; si le triangle est plat, renvoit -999.9;
   * 
   * @param x
   * @param y
   * @return
   */
  public double getSlopeAzimutalOrientation(IDirectPosition pos) {
    GAELTriangle tri = this.getTriangle(pos);
    if (tri == null) {
      return 999.9;
    }
    return tri.getSlopeAzimutalOrientation();
  }

  public void ajouterContraintesSubmicrosProximiteCN(double importance) {
    for (GAELTriangle t : this.getTriangles()) {
      // si au moins un des points n'est pas dans une courbe de niveau, passer
      // au suivant
      if (t.getP1().getCourbeDeNiveau() == null
          || t.getP2().getCourbeDeNiveau() == null
          || t.getP3().getCourbeDeNiveau() == null) {
        continue;
      }
      // on suppose que deux points appartiennent a la meme courbe de niveau
      // s'ils ont la meme altitude
      IPointAgent ap = null, ap1 = null, ap2 = null;
      if (t.getP1().getZ() == t.getP2().getZ()) {
        ap = t.getP3();
        ap1 = t.getP1();
        ap2 = t.getP2();
      } else if (t.getP2().getZ() == t.getP3().getZ()) {
        ap = t.getP1();
        ap1 = t.getP2();
        ap2 = t.getP3();
      } else if (t.getP3().getZ() == t.getP1().getZ()) {
        ap = t.getP2();
        ap1 = t.getP3();
        ap2 = t.getP1();
      } else {
        continue;
      }
      GAELSegment s = this.getSegment(ap1, ap2);
      if (ap.getPointSingleton() == null) {
        new GAELPointSingleton(this, ap);
      }
      double distance = (ap1.getCourbeDeNiveau().getFeature().getWidth()
          + GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_CN)
          * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
      new MinimalDistance(ap.getPointSingleton(), s, importance, distance);
    }
  }

  public void instanciateConstraints() {
    if (ReliefFieldAgent.logger.isDebugEnabled()) {
      ReliefFieldAgent.logger
          .debug("instanciation des contraintes du champ relief");
    }

    // effacer contraintes submicros
    if (ReliefFieldAgent.logger.isTraceEnabled()) {
      ReliefFieldAgent.logger
          .trace("suppression des contraintes submicro du champ relief");
    }
    this.supprimerContraintesSubmicro();
    if (ReliefFieldAgent.logger.isTraceEnabled()) {
      ReliefFieldAgent.logger
          .trace("suppression des contraintes submicro des courbes de niveau");
    }
    for (ContourLineAgent cn : this.getContourLines()) {
      cn.supprimerContraintesSubmicro();
    }

    // points
    if (AgentSpecifications.RELIEF_POSITION_POINT) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes position points");
      }
      this.ajouterContPointPosition(
          AgentSpecifications.RELIEF_POSITION_POINT_IMP);
    }

    // segments CN
    if (AgentSpecifications.CONTOUR_LINE_SEGMENT_LENGTH) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes longueur segments CN");
      }
      for (ContourLineAgent cn : this.getContourLines()) {
        cn.ajouterContSegmentLongueur(
            AgentSpecifications.CONTOUR_LINE_SEGMENT_LENGTH_IMP);
      }
    }
    if (AgentSpecifications.CONTOUR_LINE_SEGMENT_ORIENTATION) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes longueur segments CN");
      }
      for (ContourLineAgent cn : this.getContourLines()) {
        cn.ajouterContSegmentOrientation(
            AgentSpecifications.CONTOUR_LINE_SEGMENT_ORIENTATION_IMP);
      }
    }

    // segments
    if (AgentSpecifications.RELIEF_SEGMENT_LENGTH) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes longueur segments");
      }
      this.ajouterContSegmentLongueur(
          AgentSpecifications.RELIEF_SEGMENT_LENGTH_IMP);
    }
    if (AgentSpecifications.RELIEF_SEGMENT_ORIENTATION) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes longueur segments");
      }
      this.ajouterContSegmentOrientation(
          AgentSpecifications.RELIEF_SEGMENT_ORIENTATION_IMP);
    }

    // triangles
    if (AgentSpecifications.RELIEF_TRIANGLE_AREA) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes aire triangles CN");
      }
      this.ajouterContTriangleAire(
          AgentSpecifications.RELIEF_TRIANGLE_AREA_IMP);
    }
    if (AgentSpecifications.RELIEF_TRIANGLE_CENTROID) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes centreG triangles CN");
      }
      this.ajouterContTriangleGarderG(
          AgentSpecifications.RELIEF_TRIANGLE_CENTROID_IMP);
    }

    if (AgentSpecifications.BUILDING_ALTITUDE) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger
            .trace("ajout contraintes triangles altitude batiments");
      }
      this.ajouterContTriangleFaireGarderAltitude(
          AgentSpecifications.RELIEF_ALTITUDE_BUILDING_IMP);
    }
    if (AgentSpecifications.RIVER_FLOW_PRESERVATION) {
      if (ReliefFieldAgent.logger.isTraceEnabled()) {
        ReliefFieldAgent.logger.trace("ajout contraintes triangles ecoulement");
      }
      this.ajouterContTriangleFaireCouler(
          AgentSpecifications.RELIEF_RIVER_FLOW_IMP);
    }
  }

  public void ajouterContDistanceMinimaleCN(double importance) {
    new ContoursMinimalDistance(this, importance);
  }

  @Override
  public void activatePointAgents() throws InterruptedException {
    this.getGAELDeformable().getListeAgentsPoints()
        .addAll(this.getGAELDeformable().getPointAgents());
    super.activatePointAgents();
  }

}
