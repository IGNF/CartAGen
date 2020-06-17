/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.dataset.shapefile;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import fr.ign.cogit.cartagen.core.dataset.GeneObjImplementation;

/**
 * Objects from this class are mappings between a shapefile data structure and
 * the CartAGen core data schema.
 * 
 * @author GTouya
 *
 */
public class ShapeToLayerMapping {

    // ******************
    // Properties (ShapeToLayerMapping)
    // ******************
    public GeneObjImplementation implementation;
    public Set<ShapeToLayerMatching> matchings;

    // ******************
    // Subclass ShapeToLayerMatching
    // ******************
    public class ShapeToLayerMatching {

        // ******************
        // Properties (PostGISToLayerMatching)
        // ******************
        private String shapeLayer;
        private Method creationMethod;
        private String scale;
        private String featureType;
        private String theme;
        private Hashtable<String, String> listAttr = new Hashtable<String, String>();

        // ******************
        // Constructor (PostGISToLayerMatching)
        // ******************
        public ShapeToLayerMatching(String shapeLayer, Method creationMethod,
                String scale, String theme, String featureType,
                Hashtable<String, String> listAttr) {
            super();
            this.setShapeLayer(shapeLayer);
            this.setCreationMethod(creationMethod);
            this.setScale(scale);
            this.setTheme(theme);
            this.setFeatureType(featureType);
            this.setListAttr(listAttr);
        }

        // ******************
        // Getters (PostGISToLayerMatching)
        // ******************

        public String getShapeLayer() {
            return shapeLayer;
        }

        public Method getCreationMethod() {
            return creationMethod;
        }

        public String getScale() {
            return scale;
        }

        public String getTheme() {
            return theme;
        }

        public Hashtable<String, String> getListAttr() {
            return this.listAttr;
        }

        public String getFeatureType() {
            return featureType;
        }

        public void setFeatureType(String featureType) {
            this.featureType = featureType;
        }

        // ******************
        // Setters (PostGISToLayerMatching)
        // ******************

        public void setShapeLayer(String shapeLayer) {
            this.shapeLayer = shapeLayer;
        }

        public void setCreationMethod(Method creationMethod) {
            this.creationMethod = creationMethod;
        }

        public void setScale(String scale) {
            this.scale = scale;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public void setListAttr(Hashtable<String, String> listAttr) {
            this.listAttr = listAttr;
        }

        @Override
        public String toString() {
            return "ShapeToLayerMatching [shapeLayer=" + shapeLayer
                    + ", creationMethod=" + creationMethod + ", scale=" + scale
                    + ", featureType=" + featureType + ", theme=" + theme
                    + ", listAttr=" + listAttr + "]";
        }

    }

    // ******************
    // Constructor (PostGISToLayerMapping)
    // ******************
    // Default constructor with default mapping

    public ShapeToLayerMapping(GeneObjImplementation implementation)
            throws NoSuchMethodException {
        this.implementation = implementation;
        this.matchings = new HashSet<>();
    }

    // ******************
    // Getters (PostGISToLayerMapping)
    // ******************

    public GeneObjImplementation getGeneObjImplementation() {
        return this.implementation;
    }

    public Method getCreationMethod(String shapeLayer) {
        Method creationMethod = null;
        for (ShapeToLayerMatching matching : this.matchings) {
            if (matching.shapeLayer.equals(shapeLayer)) {
                creationMethod = matching.creationMethod;
                break;
            }
        }
        return creationMethod;
    }

    public Hashtable<String, String> getListAttr(String shapeLayer) {
        Hashtable<String, String> listAttr = null;
        for (ShapeToLayerMatching matching : this.matchings) {
            if (matching.shapeLayer.equals(shapeLayer)) {
                listAttr = matching.listAttr;
                break;
            }
        }
        return listAttr;
    }

    public Integer getSize() {
        return matchings.size();
    }

    public Set<ShapeToLayerMatching> getMatchings() {
        return this.matchings;
    }

    public ShapeToLayerMatching getMatchingFromFeatureType(
            String featureTypeName) {
        for (ShapeToLayerMatching matching : matchings) {
            if (matching.getFeatureType().equals(featureTypeName))
                return matching;
        }
        return null;
    }

    public ShapeToLayerMatching getMatchingFromShapefile(String shapefile) {
        for (ShapeToLayerMatching matching : matchings) {
            if (matching.getShapeLayer().equals(shapefile))
                return matching;
        }
        return null;
    }

}
