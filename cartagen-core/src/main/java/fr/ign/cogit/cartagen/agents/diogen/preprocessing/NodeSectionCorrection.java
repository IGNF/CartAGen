package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class NodeSectionCorrection {

  public static void execute(IPopulation<? extends INetworkNode> nodes,
      double tolerance, boolean correctInitialGeom) {

    for (INetworkNode node : nodes) {
      for (INetworkSection section : node.getInSections()) {
        ILineString geom = section.getGeom();
        geom.coord().remove(geom.coord().size() - 1);
        geom.coord()
            .add((IDirectPosition) node.getGeom().getPosition().clone());
        if (correctInitialGeom) {
          ILineString initialGeom = section.getGeom();
          initialGeom.coord().remove(initialGeom.coord().size() - 1);
          initialGeom.coord().add(
              (IDirectPosition) node.getGeom().getPosition().clone());
        }
      }
      for (INetworkSection section : node.getOutSections()) {
        ILineString geom = section.getGeom();
        geom.coord().remove(0);
        geom.coord().add(0,
            (IDirectPosition) node.getGeom().getPosition().clone());
        if (correctInitialGeom) {
          ILineString initialGeom = section.getGeom();
          initialGeom.coord().remove(0);
          initialGeom.coord().add(0,
              (IDirectPosition) node.getGeom().getPosition().clone());
        }
      }
    }

  }
}
