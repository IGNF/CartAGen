package fr.ign.cogit.cartagen.collagen.geospaces.model;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.SchemaAnnotation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public abstract class ArealSpace extends GeographicSpace implements
    ArealProperties {

  public ArealSpace(IPolygon polygon) {
    super(polygon);
  }

  @Override
  protected void buildAdjacency() {
    // TODO
  }

  @Override
  protected void buildEdges() {
    // TODO
  }

  @Override
  protected void findConnates() {
    // TODO Auto-generated method stub
  }

  @Override
  protected void buildIntersection() {
    // TODO
  }

  @Override
  public boolean contains(IGeneObj obj) {
    if (this.getGeom().contains(obj.getGeom())) {
      return true;
    }
    return false;
  }

  @Override
  /**
   * Cette implémentation regarde si le lien de l'ontologie est restreint. Si c'est
   * le cas, on récupère par requête spatiale les objets correspondant à la 
   * restriction. Sinon, on récupère tous les objets CollaGen contenus dans
   * l'espace géométriquement.
   */
  public Set<IGeneObj> getInsideFeatures() {
    GeoSpaceConcept concept = this.getConcept();
    HashSet<String> nomsClasses = new HashSet<String>();
    SchemaAnnotation app = CollaGenEnvironment.getInstance().getAnnotation();
    for (GeographicConcept c : concept.getContainsRestriction()) {
      // on récupère la classe relative à ce concept
      String classe = app.getClassAnnotation().get(c.getName());
      // s'il existe, on l'ajoute
      if (classe != null) {
        nomsClasses.add(classe);
      }
    }
    Set<IGeneObj> objs = new HashSet<IGeneObj>();
    try {
      // on teste s'il y a des noms de classes
      if (nomsClasses.size() == 0) {
        // on fait une requête sur tous les objets contenus dans l'espace
        objs.addAll(SpatialQuery.selectInAreaAll((IPolygon) this.getGeom()));
      } else {
        objs.addAll(SpatialQuery.selectInAreaNames((IPolygon) this.getGeom(),
            nomsClasses));
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return objs;
  }

  @Override
  public Set<SpecElementMonitor> getMonitors() {
    // on cr�e le set des contraintes
    HashSet<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();

    // on fait une boucle sur les objets intérieurs
    for (IGeneObj obj : this.getInsideFeatures()) {
      // on récupère les contraintes sur cet objet
      try {
        contraintes.addAll(ConstraintMonitor.getFeatureConstraints(obj,
            CollaGenEnvironment.getInstance()));
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
    System.out.println(contraintes.size() + " moniteurs trouves");
    return contraintes;
  }
}
