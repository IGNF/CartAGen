package fr.ign.cogit.cartagen.collagen.processes.implementation;

import fr.ign.cogit.cartagen.algorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;

public class ForestGeneralisationProcess extends GeneralisationProcess {

  /**
   * in map square millimeters
   */
  private double minArea = 0.5;
  /**
   * in map square meters
   */
  private double minHole = 2.0;
  private double minkowskiStep = 20.0;
  /**
   * in map square meters
   */
  private double visvalingamThreshold = 0.1;

  public ForestGeneralisationProcess(Conductor chefO) {
    super(chefO);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void updateEliminations() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void incrementStates() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadXMLDescription() {
    // TODO Auto-generated method stub

  }

  /**
   * Shortcut to run the process on a given {@link GeographicSpace} instance.
   * @param space
   */
  public void runOnGeoSpace(GeographicSpace space) {
    triggerGeneralisation(space);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void triggerGeneralisation(GeographicSpace space) {
    for (IGeneObj obj : space.getInsideFeatures()) {
      // First, remove small areas
      if (obj.getGeom().area() < this.minArea * Legend.getSYMBOLISATI0N_SCALE()
          / 1000.0 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {
        obj.eliminate();
        continue;
      }

      // then, remove small holes
      CommonAlgorithmsFromCartAGen.removeSmallHoles((IPolygon) obj.getGeom(),
          this.minHole * Legend.getSYMBOLISATI0N_SCALE() / 1000.0
              * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);

      // then, simplify the outline of the forests
      MorphologyTransform morpho = new MorphologyTransform(minkowskiStep, 10);
      IPolygon closed = morpho.closing((IPolygon) obj.getGeom());
      IGeometry opened = morpho.opening(closed);
      VisvalingamWhyatt visvalingam = new VisvalingamWhyatt(
          visvalingamThreshold);
      if (opened instanceof IPolygon) {
        IPolygon newGeom = visvalingam.simplify((IPolygon) opened);
        obj.setGeom(newGeom);
      } else if (closed instanceof IMultiSurface<?>) {
        IPolygon simple = CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface(
                (IMultiSurface<IOrientableSurface>) opened);
        IPolygon newGeom = visvalingam.simplify(simple);
        obj.setGeom(newGeom);
      }
    }
  }
}
