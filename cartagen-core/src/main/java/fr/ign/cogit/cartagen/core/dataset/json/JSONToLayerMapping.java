/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.dataset.json;

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
public class JSONToLayerMapping {

    // ******************
    // Properties (JSONToLayerMapping)
    // ******************
    public GeneObjImplementation implementation;
    public Set<JSONToLayerMatching> matchings;

    // ******************
    // Subclass JSONToLayerMapping
    // ******************
    public class JSONToLayerMatching {

        // ******************
        // Properties (JSONToLayerMatching)
        // ******************
        private String jsonLayer;
        private Method creationMethod;
        private String scale;
        private String theme;
        // the mapping of attribute (Java, JSON)
        private Hashtable<String, String> listAttr = new Hashtable<String, String>();

        // ******************
        // Constructor (JSONToLayerMatching)
        // ******************
        public JSONToLayerMatching(String shapeLayer, Method creationMethod,
                String scale, String theme,
                Hashtable<String, String> listAttr) {
            super();
            this.setJsonLayer(shapeLayer);
            this.setCreationMethod(creationMethod);
            this.setScale(scale);
            this.setTheme(theme);
            this.setListAttr(listAttr);
        }

        // ******************
        // Getters (JSONToLayerMatching)
        // ******************

        public String getJsonLayer() {
            return jsonLayer;
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

        // ******************
        // Setters (JSONToLayerMatching)
        // ******************

        public void setJsonLayer(String jsonLayer) {
            this.jsonLayer = jsonLayer;
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

        /**
         * Get the Java attribute matched with the given JSON attribute
         * 
         * @param JsonAttr
         * @return
         */
        public String getJavaAttribute(String JsonAttr) {
            for (String javaAttr : listAttr.keySet()) {
                if (JsonAttr.equals(listAttr.get(javaAttr)))
                    return javaAttr;
            }
            return null;
        }
    }

    // ******************
    // Constructor (JSONToLayerMapping)
    // ******************
    // Default constructor with default mapping

    public JSONToLayerMapping(GeneObjImplementation implementation)
            throws NoSuchMethodException {
        this.implementation = implementation;
        this.matchings = new HashSet<>();
    }

    // ******************
    // Getters (JSONToLayerMapping)
    // ******************

    public GeneObjImplementation getGeneObjImplementation() {
        return this.implementation;
    }

    public Method getCreationMethod(String shapeLayer) {
        Method creationMethod = null;
        for (JSONToLayerMatching matching : this.matchings) {
            if (matching.jsonLayer.equals(shapeLayer)) {
                creationMethod = matching.creationMethod;
                break;
            }
        }
        return creationMethod;
    }

    public Hashtable<String, String> getListAttr(String shapeLayer) {
        Hashtable<String, String> listAttr = null;
        for (JSONToLayerMatching matching : this.matchings) {
            if (matching.jsonLayer.equals(shapeLayer)) {
                listAttr = matching.listAttr;
                break;
            }
        }
        return listAttr;
    }

    public String getJSONAttribute(String shapeLayer, String javaAttr) {
        Hashtable<String, String> listAttr = null;
        for (JSONToLayerMatching matching : this.matchings) {
            if (matching.jsonLayer.equals(shapeLayer)) {
                listAttr = matching.listAttr;
                break;
            }
        }
        return listAttr.get(javaAttr);
    }

    public JSONToLayerMatching getMatchingFromName(String matchingName) {
        for (JSONToLayerMatching matching : this.matchings) {
            if (matching.jsonLayer.equals(matchingName)) {
                return matching;
            }
        }
        return null;
    }

    public Integer getSize() {
        return matchings.size();
    }

    public Set<JSONToLayerMatching> getMatchings() {
        return this.matchings;
    }
}
