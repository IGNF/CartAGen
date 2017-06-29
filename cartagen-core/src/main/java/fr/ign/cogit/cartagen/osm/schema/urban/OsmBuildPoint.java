package fr.ign.cogit.cartagen.osm.schema.urban;

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class OsmBuildPoint extends OsmGeneObjPoint implements IBuildPoint {

  public OsmBuildPoint(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmBuildPoint(IPoint point) {
    super(point);
  }

  @Override
  public String getNature() {
    // TODO Auto-generated method stub
    return null;
  }
}
