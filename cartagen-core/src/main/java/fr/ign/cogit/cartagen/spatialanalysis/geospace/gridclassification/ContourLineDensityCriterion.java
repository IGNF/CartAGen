package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class ContourLineDensityCriterion extends CellCriterion {

  public ContourLineDensityCriterion(GridCell cell, double poids,
      Number seuilBas, Number seuilHaut) {
    super(cell, poids, seuilBas, seuilHaut);
  }

  @Override
  public void setCategory() {
    // cas d'un critère entier
    double valeurD = this.getValeur().doubleValue();
    if (valeurD < this.getSeuilBas().doubleValue()) {
      this.setClassif(1);
    } else if (valeurD > this.getSeuilHaut().doubleValue()) {
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
    IFeatureCollection<CourbeDeNiveau> data = (IFeatureCollection<CourbeDeNiveau>) this
        .getCellule().getGrille().getData().get(MountainGrid.FC_CONTOURS);
    Collection<CourbeDeNiveau> cellContours = data.select(circle);
    if (cellContours.size() == 0) {
      this.setValeur(new Integer(0));
    } else {
      double value = 0.0;
      for (CourbeDeNiveau contour : cellContours) {
        IGeometry inter = circle.intersection(contour.getGeom());
        value += inter.length();
      }
      this.setValeur(new Integer((int) Math.round(value)));
    }
  }

}
