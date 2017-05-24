package fr.ign.cogit.cartagen.agents.diogen;

import java.util.Collection;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IGeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.relation.NonOverlappingHikingRoad;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierObject;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class PadawanAgentInitialisation {

  private static double DISTANCE = 6.0;

  public static void initialiseRelationsLineareFeaturesAndPointsOfFeatures(
      IFeatureCollection<? extends INetworkSection> linearCarrierFeatures,
      IFeatureCollection<? extends IGeneObj> deformableFeatures) {

    for (INetworkSection carrierSection : linearCarrierFeatures) {

      // System.out.println("Identify overlapping points for " +
      // carrierSection);

      Object a = PadawanUtil.getIODAAgentFromGeneObj(carrierSection);
      // System.out.println("Agent = " + a);
      if (!(a instanceof ICartAComAgentGeneralisation)) {
        continue;
      }
      ICartAComAgentGeneralisation sectionAgent = (ICartAComAgentGeneralisation) a;

      // Get the width of the road and the supported objects
      double width = carrierSection.getWidth();
      if (carrierSection instanceof ICarrierObject) {
        for (ICarriedObject carried : ((ICarrierObject) carrierSection)
            .getCarriedObjects()) {
          width += ((INetworkSection) carried).getWidth();
        }
      }
      width *= Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
      width += DISTANCE;

      // IGeometry buffer = Buffer ;
      IGeometry buffer = CommonAlgorithms.buffer(carrierSection.getGeom(),
          width);
      // IGeometry buffer2 = CommonAlgorithms
      // .buffer(carrierSection.getGeom(), 10, BufferParameters.CAP_ROUND,
      // BufferParameters.JOIN_MITRE, 100.0, width);

      Collection<? extends IGeneObj> neighborhoodFeatures = deformableFeatures
          .select(buffer);

      // System.out.println("neighborhoodFeatures " + neighborhoodFeatures);

      JtsAlgorithms jtsAlgorithms = new JtsAlgorithms();

      for (IGeneObj f : neighborhoodFeatures) {
        if (f == carrierSection) {
          continue;
        }
        // System.out.println("Test neig feature : " + f);

        for (int i = 0; i < f.getGeom().coord().size(); i++) {

          IDirectPosition point = f.getGeom().coord().get(i);
          // System.out.println("Point : " + point);

          // IDirectPosition previousPoint = null;
          // if (i > 0) {
          // previousPoint = f.getGeom().coord().get(i - 1);
          // }
          // IDirectPosition nextPoint = null;
          // if (i < f.getGeom().coord().size() - 1) {
          // nextPoint = f.getGeom().coord().get(i + 1);
          // }

          // System.out.println("Test if point " + point + " may be inside "
          // + deformableFeatures);
          if (jtsAlgorithms.contains(buffer, point.toGM_Point())) {
            // System.out.println("yes");
            Object o = PadawanUtil.getIODAAgentFromGeneObj(f);
            // System.out.println("Agent: " + o);
            if (!(o instanceof GAELDeformable)) {
              continue;
            }
            GAELDeformable deformable = (GAELDeformable) o;
            // System.out.println("Agent deformable: " + deformable);
            // create the relation between the point and the linear object
            IPointAgent pointAgent = PadawanUtil
                .getPointAgentFromDeformable(deformable, point);
            // System.out.println("Point agent : " + pointAgent);
            new NonOverlappingHikingRoad(sectionAgent,
                (IGeographicPointAgent) pointAgent, 3);
            // Environment env = sectionAgent.getContainingEnvironments()
            // .iterator().next();
            // @SuppressWarnings("unchecked")
            // Class<IAgent> sourceAgent = (Class<IAgent>)
            // sectionAgent.getClass();
            // @SuppressWarnings("unchecked")
            // Class<IAgent> targetAgent = (Class<IAgent>)
            // pointAgent.getClass();

          }
        }
      }
    }
  }
}
