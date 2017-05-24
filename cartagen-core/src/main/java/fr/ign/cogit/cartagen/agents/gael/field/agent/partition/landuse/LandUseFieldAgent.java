package fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.PartitionFieldAgent;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public final class LandUseFieldAgent extends PartitionFieldAgent {
  static Logger logger = Logger.getLogger(LandUseFieldAgent.class.getName());

  private IGeneObj geneObj = null;

  public IGeneObj getFeature() {
    return this.geneObj;
  }

  public void setFeature(IGeneObj geoObj) {
    geoObj.addToGeneArtifacts(this);
    this.geneObj = geoObj;
  }

  public LandUseFieldAgent(IGeneObj field) {
    super();
    this.setFeature(field);
  }

  public void enrich() {
    if (LandUseFieldAgent.logger.isDebugEnabled()) {
      LandUseFieldAgent.logger.debug("decomposition de " + this);
    }
    this.decompose();
  }

  public void decompose() {
  }

  public void instanciateConstraints() {
  }

  /**
   * Dessine une couche Postgis.
   */
  /*
   * public static void dessinCouchePostGIS() { ResultSet
   * res=PostGISBD.requete(ParcelleOccSol.getNomClassePostGIS(), "the_geom",
   * PanelVisu.getInstance().getXMin(), PanelVisu.getInstance().getYMin(),
   * PanelVisu.getInstance().getXMax(), PanelVisu.getInstance().getYMax()); try
   * { while(res.next()) { GM_Polygon poly= (GM_Polygon)
   * AdapterFactory.toGM_Object
   * ((PostGISBD.getLecteurWKB().read(res.getBytes(1)))); if
   * (PanelGauche.get().cSymbole.isSelected()) {
   * PanelVisu.getInstance().dessiner(ParcelleOccSol.COULEUR_1, poly);
   * PanelVisu.getInstance().dessinerLimite(ParcelleOccSol.COULEUR_CONTOUR_1,
   * poly, ParcelleOccSol.LARGEUR_CONTOUR*SpecGene.getECHELLE_CIBLE()/1000.0,
   * BasicStroke.CAP_SQUARE , BasicStroke.JOIN_BEVEL); } else {
   * PanelVisu.getInstance().dessiner(ParcelleOccSol.COULEUR_1, poly); } }
   * res.close(); } catch (Exception e) {e.printStackTrace();} }
   */

}
