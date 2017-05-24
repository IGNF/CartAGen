package fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class HeightDifferenceCriterion extends CellCriterion {

  public HeightDifferenceCriterion(GridCell cell, double poids,
      Number seuilBas, Number seuilHaut) {
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
      int heightMin = Integer.MAX_VALUE;
      int heightMax = 0;
      for (CourbeDeNiveau contour : cellContours) {
        int height = new Double(contour.getValeur()).intValue();
        if (height < heightMin) {
          heightMin = height;
        }
        if (height > heightMax) {
          heightMax = height;
        }
      }
      this.setValeur(new Integer(heightMax - heightMin));
    }
  }

}
