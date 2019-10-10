package fr.ign.cogit.cartagen.core.dataset.json;

import fr.ign.cogit.cartagen.core.dataset.GeographicClass;
import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class JSONGeoClass implements GeographicClass {

    private String name;
    private String featureTypeName;
    private Class<? extends IGeometry> geometryType;

    public JSONGeoClass(String name, String featureTypeName,
            GeometryType geometry) {
        this.name = name;
        this.featureTypeName = featureTypeName;
        if (geometry != null)
            this.geometryType = geometry.toGeomClass();
    }

    public JSONGeoClass(String name, String featureTypeName,
            Class<? extends IGeometry> geometryType) {
        this.name = name;
        this.featureTypeName = featureTypeName;
        this.geometryType = geometryType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFeatureTypeName() {
        return featureTypeName;
    }

    @Override
    public Class<? extends IGeometry> getGeometryType() {
        return geometryType;
    }

    @Override
    public void addCartAGenId() {
        // TODO Auto-generated method stub

    }

}
