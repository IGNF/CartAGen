package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

import java.util.Collection;
import java.util.Iterator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class RectangularRoadCriterion extends CellCriterion {

  public RectangularRoadCriterion(GridCell cell, double poids, Number seuilBas,
      Number seuilHaut) {
    super(cell, poids, seuilBas, seuilHaut);
  }

  @Override
  /**
   * Calcule le critère de rectangularité dans la cellule. Il s'agit d'une 
   * moyenne des angles incidents des noeuds routiers de degré > 2, ramenés
   * entre 0 et Pi/2. Plus cette valeur est proche de Pi/2, plus la
   * rectangularité est importante.
   * 
   */
  public void setCategory() {
    // cas d'un critère réel
    double valeurDbl = this.getValeur().doubleValue();
    if (valeurDbl < this.getSeuilBas().doubleValue()) {
      this.setClassif(1);
    } else if (valeurDbl > this.getSeuilHaut().doubleValue()) {
      this.setClassif(3);
    } else {
      this.setClassif(2);
    }
  }

  /**
   * Version non optimisée: le lien noeud/route se fait par la géométrie
   * {@inheritDoc} (This is the behaviour inherited from the super class).
   * <p>
   * 
   * @author GTouya
   */
  @SuppressWarnings("unchecked")
  @Override
  public void setValue() {
    double totalRect = 0.0;
    int nbNoeuds = 0;
    // create un circle around this cell
    IDirectPosition centre = new DirectPosition(this.getCellule().getxCentre(),
        this.getCellule().getyCentre());
    IPolygon circle = GeometryFactory.buildCircle(centre, this.getCellule()
        .getGrille().getRadiusCellule(), 24);
    IFeatureCollection<IFeature> nodes = (IFeatureCollection<IFeature>) this
        .getCellule().getGrille().getData().get(UrbanGrid.FC_NODES);
    IFeatureCollection<IFeature> roads = (IFeatureCollection<IFeature>) this
        .getCellule().getGrille().getData().get(UrbanGrid.FC_ROADS);
    Collection<IFeature> cellNodes = nodes.select(circle);
    // on parcourt cette collection
    for (IFeature node : cellNodes) {
      Collection<IFeature> nodeRoads = roads.select(node.getGeom());
      // on filtre selon son degré
      if (nodeRoads.size() < 3) {
        continue;
      }
      double totalAnglesNoeud = 0.0;
      int nbAnglesNoeud = 0;
      // on fait la moyenne des angles ramenés entre 0 et 90
      // entre les tronçons consécutifs
      Iterator<IFeature> iter = nodeRoads.iterator();
      IFeature first = iter.next();
      IFeature road1 = first;
      IFeature road2 = iter.next();
      for (int i = 0; i < nodeRoads.size() - 1; i++) {
        double angle = CommonAlgorithmsFromCartAGen.angleBetween2Lines(
            (ILineString) road1.getGeom(), (ILineString) road2.getGeom());
        // on ramène dans [0,Pi]
        if (angle < 0.0) {
          angle = -angle;
        }
        // on le ramène dans [0,Pi/2]
        if (angle > Math.PI / 2.0) {
          angle = Math.PI - angle;
        }
        // on incrémente le nb d'angles
        nbAnglesNoeud += 1;
        totalAnglesNoeud += angle;
        // on passe à l'angle suivant
        road1 = road2;
        if (iter.hasNext()) {
          road2 = iter.next();
        } else {
          road2 = first;
        }
      }
      // on calcule la rectangularité du noeud
      double rectangularite = totalAnglesNoeud / nbAnglesNoeud;

      // on met à jour les infos sur la cellule
      nbNoeuds += 1;
      totalRect += rectangularite;

    }// while boucle sur setNoeuds

    // on calcule la moyenne sur la cellule
    double rectCell = totalRect / nbNoeuds;
    this.setValeur(new Double(rectCell));
  }

}
