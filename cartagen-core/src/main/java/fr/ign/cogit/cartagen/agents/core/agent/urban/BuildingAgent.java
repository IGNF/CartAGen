package fr.ign.cogit.cartagen.agents.core.agent.urban;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Convexity;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Elongation;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Granularity;
import fr.ign.cogit.cartagen.agents.core.constraint.building.LocalWidth;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Orientation;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Size;
import fr.ign.cogit.cartagen.agents.core.constraint.building.Squareness;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.buildinglanduse.BuildingLandUseMembership;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.buildingrelief.BuildingElevation;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.buildingrelief.ReliefBuildingElevation;
import fr.ign.cogit.cartagen.agents.gael.field.relation.buildingfield.BuildingElevationRelation;
import fr.ign.cogit.cartagen.agents.gael.field.relation.buildingfield.BuildingLandUseMembershipRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.generalisation.simplification.PolygonSegment;

/**
 * @author julien Gaffuri 29 juin 2006
 * 
 */
public class BuildingAgent extends UrbanElementAgent implements IBuildingAgent {
  private static Logger logger = Logger
      .getLogger(BuildingAgent.class.getName());

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public IPolygon getInitialGeom() {
    return (IPolygon) super.getInitialGeom();
  }

  @Override
  public IBuilding getFeature() {
    return (IBuilding) super.getFeature();
  }

  @Override
  public IPolygon getSymbolGeom() {
    return this.getGeom();
  }

  /**
     */
  private int importance;

  /**
   * @return
   */
  public int getImportance() {
    return this.importance;
  }

  /**
   * @param importance
   */
  public void setImportance(int importance) {
    this.importance = importance;
  }

  /**
   * Constructor for BuildingAgent
   * 
   * Modification by @author AMaudet : test if they are a geometry before
   * cloning it.
   * @param bat
   * @param importance
   * 
   */
  public BuildingAgent(IBuilding bat, int importance) {
    super();
    this.setFeature(bat);
    this.setImportance(importance);
    if (bat.getGeom() != null)
      this.setInitialGeom((IGeometry) bat.getGeom().clone());
  }

  /**
   * @return la longueur du plus petit cote du batiment
   */
  @Override
  public double getSmallestSideLength() {
    return PolygonSegment.getSmallest(this.getGeom()).segment.length;
  }

  @Override
  public void instantiateConstraints() {
    this.getConstraints().clear();
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte de taille");
    }
    if (AgentSpecifications.BUILDING_SIZE_CONSTRAINT) {
      this.ajouterContrainteTaille(AgentSpecifications.BUILDING_SIZE_CONSTRAINT_IMP);
    }
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte de granularite");
    }
    if (AgentSpecifications.BUILDING_GRANULARITY) {
      this.ajouterContrainteGranularite(
          AgentSpecifications.BULDING_GRANULARITY_IMP);
    }
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte d'equarrite");
    }
    if (AgentSpecifications.BUILDING_SQUARENESS) {
      this.ajouterContrainteEquarrite(
          AgentSpecifications.BUILDING_SQUARENESS_IMP);
    }
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte de largeur locale");
    }
    if (AgentSpecifications.BUILDING_LOCAL_WIDTH) {
      this.ajouterContrainteLargeurLocale(
          AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP);
    }
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte de convexite");
    }
    if (AgentSpecifications.BUILDING_CONVEXITY) {
      this.ajouterContrainteConvexite(
          AgentSpecifications.BUILDING_CONVEXITY_IMP);
    }
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte d'elongation");
    }
    if (AgentSpecifications.BUILDING_ELONGATION) {
      this.ajouterContrainteElongation(
          AgentSpecifications.BUILDING_ELONGATION_IMP);
    }
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte de d'orientation");
    }
    if (AgentSpecifications.BUILDING_ORIENTATION) {
      this.ajouterContrainteOrientation(
          AgentSpecifications.BUILDING_ORIENTATION_IMP);
    }
    if (BuildingAgent.logger.isTraceEnabled()) {
      BuildingAgent.logger.trace("Ajout contrainte d'altitude");
    }
    if (AgentSpecifications.BUILDING_ALTITUDE) {
      this.ajouterContrainteAltitude(AgentSpecifications.BUILDING_ALTITUDE_IMP);
    }
  }

  public void ajouterContrainteTaille(double imp) {
    new Size(this, imp);
  }

  public void enleverContrainteTaille() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof Size) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteGranularite(double imp) {
    new Granularity(this, imp);
  }

  public void enleverContrainteGranularite() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof Granularity) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteEquarrite(double imp) {
    new Squareness(this, imp);
  }

  public void enleverContrainteEquarrite() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof Squareness) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteLargeurLocale(double imp) {
    new LocalWidth(this, imp);
  }

  public void enleverContrainteLargeurLocale() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof LocalWidth) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteConvexite(double imp) {
    new Convexity(this, imp);
  }

  public void enleverContrainteConvexite() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof Convexity) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteElongation(double imp) {
    new Elongation(this, imp);
  }

  public void enleverContrainteElongation() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof Elongation) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteOrientation(double imp) {
    new Orientation(this, imp);
  }

  public void enleverContrainteOrientation() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof Orientation) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteAltitude(double imp) {
    // construit la relation
    BuildingElevationRelation rel = new BuildingElevationRelation(this);
    // contruit la contrainte du batiment
    new BuildingElevation(this, rel, imp);
    // contruit la contrainte du champ
    new ReliefBuildingElevation(
        (AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())),
        rel, imp);
  }

  public void enleverContrainteAltitude() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof BuildingElevation) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  public void ajouterContrainteDomaineOccSol(double imp) {
    // construit la relation
    BuildingLandUseMembershipRelation rel = new BuildingLandUseMembershipRelation(
        this);
    // contruit la contrainte du batiment
    new BuildingLandUseMembership(this, rel, imp);
    // contruit la contrainte du champ
    // new ContrainteChampOccSolBatimentZone(ChampRelief.get(),rel,imp);
  }

  public void enleverContrainteDomaineOccSol() {
    for (Constraint contrainte : this.getConstraints()) {
      if (contrainte instanceof BuildingLandUseMembership) {
        this.getConstraints().remove(contrainte);
      }
    }
  }

  /*
   * public static void dessinCouchePostGIS(PanelVisu pv) { ResultSet
   * res=PostGISBD.requete(nomclassePG, "the_geom", pv.getXMin(), pv.getYMin(),
   * pv.getXMax(), pv.getYMax()); try { while(res.next()) { GM_Polygon poly=
   * (GM_Polygon)
   * AdapterFactory.toGM_Object((PostGISBD.getLecteurWKB().read(res.
   * getBytes(1)))); if (PanelGauche.get().cSymbole.isSelected()) {
   * pv.dessiner(COULEUR, poly); pv.dessinerLimite(COULEUR_CONTOUR, poly,
   * LARGEUR_CONTOUR*SpecGene.getECHELLE_CIBLE()/1000.0, BasicStroke.CAP_SQUARE
   * , BasicStroke.JOIN_MITER); } else { pv.dessiner(COULEUR, poly); } }
   * res.close(); } catch (Exception e) {e.printStackTrace();} }
   */

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new BuildingAgent(this.getFeature(), this.importance);
  }

}
