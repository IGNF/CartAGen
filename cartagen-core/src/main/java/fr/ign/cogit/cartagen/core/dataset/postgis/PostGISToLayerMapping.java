/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.dataset.postgis;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import fr.ign.cogit.cartagen.core.dataset.GeneObjImplementation;

/**
 * Objects from this class are mappings between a PostGIS database data
 * structure and the CartAGen core data schema.
 * @author GTouya
 *
 */
public class PostGISToLayerMapping {

  // ******************
  // Properties (PostGISToLayerMapping)
  // ******************
  public GeneObjImplementation implementation;
  public Set<PostGISToLayerMatching> matchings;

  // ******************
  // Subclass PostGISToLayerMatching
  // ******************
  public class PostGISToLayerMatching {

    // ******************
    // Properties (PostGISToLayerMatching)
    // ******************
    private String postGISLayer;
    private Method creationMethod;
    private String scale;
    private String theme;
    private Hashtable<String, String> listAttr = new Hashtable<String, String>();

    // ******************
    // Constructor (PostGISToLayerMatching)
    // ******************
    public PostGISToLayerMatching(String postGISLayer, Method creationMethod,
        String scale, String theme, Hashtable<String, String> listAttr) {
      super();
      this.setPostGISLayer(postGISLayer);
      this.setCreationMethod(creationMethod);
      this.setScale(scale);
      this.setTheme(theme);
      this.setListAttr(listAttr);
    }

    // ******************
    // Getters (PostGISToLayerMatching)
    // ******************

    public String getPostGISLayer() {
      return postGISLayer;
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
    // Setters (PostGISToLayerMatching)
    // ******************

    public void setPostGISLayer(String postGISLayer) {
      this.postGISLayer = postGISLayer;
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
  }

  // ******************
  // Constructor (PostGISToLayerMapping)
  // ******************
  // Default constructor with default mapping

  public PostGISToLayerMapping(GeneObjImplementation implementation)
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

  public Method getCreationMethod(String postGISLayer) {
    Method creationMethod = null;
    for (PostGISToLayerMatching matching : this.matchings) {
      if (matching.postGISLayer.equals(postGISLayer)) {
        creationMethod = matching.creationMethod;
        break;
      }
    }
    return creationMethod;
  }

  public Hashtable<String, String> getListAttr(String postGISLayer) {
    Hashtable<String, String> listAttr = null;
    for (PostGISToLayerMatching matching : this.matchings) {
      if (matching.postGISLayer.equals(postGISLayer)) {
        listAttr = matching.listAttr;
        break;
      }
    }
    return listAttr;
  }

  public Integer getSize() {
    return matchings.size();
  }

  public void addMatching(String postGISLayer, Method creationMethod,
      String scale, String theme, Hashtable<String, String> listAttr) {
    this.matchings.add(new PostGISToLayerMatching(postGISLayer, creationMethod,
        scale, theme, listAttr));
  }

  public Set<PostGISToLayerMatching> getMatchings() {
    return this.matchings;
  }
}
