package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class RoadNodeDensityCriterion extends CellCriterion {

  public RoadNodeDensityCriterion(GridCell cell, double poids, Number seuilBas,
      Number seuilHaut) {
    super(cell, poids, seuilBas, seuilHaut);
  }

  @Override
  public void setCategory() {
    // cas d'un critère entier
    int valeurInt = this.getValeur().intValue();
    if (valeurInt < this.getSeuilBas().intValue()) {
      this.setClassif(1);
    } else if (valeurInt > this.getSeuilHaut().intValue()) {
      this.setClassif(3);
    } else {
      this.setClassif(2);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue() {
    // create un circle around this cell
    IDirectPosition centre = new DirectPosition(this.getCellule().getxCentre(),
        this.getCellule().getyCentre());
    IPolygon circle = GeometryFactory.buildCircle(centre, this.getCellule()
        .getGrille().getRadiusCellule(), 24);
    // get data
    IFeatureCollection<IFeature> data = (IFeatureCollection<IFeature>) this
        .getCellule().getGrille().getData().get(UrbanGrid.FC_NODES);
    Collection<IFeature> cellNodes = data.select(circle);
    this.setValeur(new Integer(cellNodes.size()));
  }

}
