package fr.ign.cogit.cartagen.collagen.processes.implementation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;

public class RiverAreasGeneralisationProcess extends GeneralisationProcess {

  /**
   * unit is map m², minimum area of whole to be kept
   */
  private double holeMinArea = 0.00008;
  private double minkowskiStep = 0.0002;
  private double minArea = 0.00002;

  public RiverAreasGeneralisationProcess(Conductor chefO) {
    super(chefO);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "River areas generalisation";
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

  @Override
  protected void triggerGeneralisation(GeographicSpace space) {
    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
    AbstractCreationFactory factory = dataset.getCartAGenDB().getGeneObjImpl()
        .getCreationFactory();

    // first step, merge all connected features
    List<IGeometry> geomList = new ArrayList<IGeometry>();
    for (IGeneObj obj : space.getInsideFeatures())
      geomList.add(obj.getGeom());
    IGeometry union = JtsAlgorithms.union(geomList);

    // eliminate all initial features
    for (IGeneObj obj : space.getInsideFeatures())
      obj.eliminate();

    // create new features
    double scale = Legend.getSYMBOLISATI0N_SCALE();
    if (union instanceof IPolygon) {
      IGeometry simplified = simplifyArea((IPolygon) union);
      if (simplified instanceof IPolygon) {
        if (simplified.area() > minArea * scale * scale) {
          IWaterArea newObj = factory.createWaterArea((IPolygon) simplified);
          dataset.getWaterAreas().add(newObj);
        }
      } else if (simplified instanceof IMultiSurface<?>) {
        for (IGeometry simple : ((IMultiSurface<?>) simplified).getList()) {
          if (simple.area() > minArea * scale * scale) {
            IWaterArea newObj = factory.createWaterArea((IPolygon) simple);
            dataset.getWaterAreas().add(newObj);
          }
        }
      }
    } else if (union instanceof IMultiSurface<?>) {
      for (IGeometry simple : ((IMultiSurface<?>) union).getList()) {
        if (simple instanceof IPolygon) {
          IGeometry simplified = simplifyArea((IPolygon) simple);
          if (simplified instanceof IPolygon) {
            if (simplified.area() > minArea * scale * scale) {
              IWaterArea newObj = factory
                  .createWaterArea((IPolygon) simplified);
              dataset.getWaterAreas().add(newObj);
            }
          } else if (simplified instanceof IMultiSurface<?>) {
            for (IGeometry simple2 : ((IMultiSurface<?>) simplified)
                .getList()) {
              if (simple2.area() > minArea * scale * scale) {
                IWaterArea newObj = factory.createWaterArea((IPolygon) simple2);
                dataset.getWaterAreas().add(newObj);
              }
            }
          }
        }
      }
    }
  }

  private IGeometry simplifyArea(IPolygon area) {
    double scale = Legend.getSYMBOLISATI0N_SCALE();
    // first remove small holes
    IPolygon noHole = CommonAlgorithmsFromCartAGen.removeSmallHoles(area,
        holeMinArea * scale * scale);
    // then trigger opening and closing on the polygon
    MorphologyTransform morpho = new MorphologyTransform(minkowskiStep * scale,
        10);
    IPolygon closed = morpho.closing(noHole);
    IGeometry opened = morpho.opening(closed);

    return opened;
  }

  /**
   * Shortcut to run the process on a given {@link GeographicSpace} instance.
   * @param space
   */
  public void runOnGeoSpace(GeographicSpace space) {
    triggerGeneralisation(space);
  }

  public double getHoleMinArea() {
    return holeMinArea;
  }

  public void setHoleMinArea(double holeMinArea) {
    this.holeMinArea = holeMinArea;
  }

  public double getMinkowskiStep() {
    return minkowskiStep;
  }

  public void setMinkowskiStep(double minkowskiStep) {
    this.minkowskiStep = minkowskiStep;
  }

}
