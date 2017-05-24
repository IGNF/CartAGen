package fr.ign.cogit.cartagen.collagen.geospaces.spaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.relations.CollaGenRelation;
import fr.ign.cogit.cartagen.collagen.geospaces.model.ArealSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * @author GTouya
 * 
 */
public class RuralSpace extends ArealSpace {

  private GeoSpaceConcept geoConcept;

  public RuralSpace(IPolygon polygon) {
    super(polygon);
    this.geoConcept = CollaGenEnvironment.getInstance()
        .getGeoSpaceConceptFromName("rural_area");
  }

  @Override
  public double getAire() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRatioBati() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRatioNoirBlanc() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isHierarchique() {
    return false;
  }

  @Override
  public GeoSpaceConcept getConcept() {
    return geoConcept;
  }

  @Override
  public Set<SpecElementMonitor> getPartitionSample(int idLastStop,
      GeneralisationProcess process) {
    // initialisation
    CartAGenDataSet dataSet = CartAGenDoc.getInstance().getCurrentDataset();
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();
    ArrayList<IGeneObj> objsTraites = new ArrayList<IGeneObj>();
    objsTraites.addAll(dataSet.getNetworkFaces().select(getGeom()));

    // on fait une boucle sur les objets traités
    for (int i = idLastStop; i < objsTraites.size(); i++) {
      IGeneObj obj = objsTraites.get(i);
      // arrivé ici, c'est une nouvelle partition
      // on récupère tous ses batiments
      Collection<IBuilding> batis = dataSet.getBuildings()
          .select(obj.getGeom());
      int j = 0;
      for (IGeneObj bati : batis) {
        // on teste si le bâtiment a bien déjà été traité
        if (!objsTraites.contains(bati))
          continue;
        // on teste si j est un multiple de 3
        if (j % 3 == 0) {
          // on récupère ses contraintes instanciées
          contraintes.addAll(ConstraintMonitor.getFeatureConstraints(bati,
              CollaGenEnvironment.getInstance()));
        }
        j++;
      }
    }

    return contraintes;
  }

  @Override
  public java.util.Set<SpecElementMonitor> getRandomSample(
      GeneralisationProcess process, double ratio) {
    CollaGenEnvironment env = CollaGenEnvironment.getInstance();
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();
    List<IGeneObj> objsTraites = new ArrayList<IGeneObj>();
    objsTraites.addAll(process.getObjetsTraites());
    // on prend toutes les contraintes des objets trait�s
    for (IGeneObj obj : objsTraites) {
      // on récupère ses contraintes instanciées
      for (ConstraintMonitor m : ConstraintMonitor.getFeatureConstraints(obj,
          env)) {
        contraintes.add(m);
      }
      // on récupère les contraintes instanciées de ses relations
      for (CollaGenRelation rel : env.getRelationsWithObj(obj)) {
        contraintes.addAll(ConstraintMonitor.getRelationConstraints(rel, env));
      }
    }
    // on filtre maintenant contraintes pour respecter les proportions
    int nbSuppr = contraintes.size()
        - (int) Math.round(ratio * contraintes.size());
    for (int i = 0; i < nbSuppr; i++) {
      ArrayList<SpecElementMonitor> liste = new ArrayList<SpecElementMonitor>();
      liste.addAll(contraintes);
      Collections.shuffle(liste);
      SpecElementMonitor m = liste.get(0);
      contraintes.remove(m);
    }
    return contraintes;
  }

  @Override
  public java.util.Set<SpecElementMonitor> getSimpleSample(
      GeneralisationProcess process) {
    CollaGenEnvironment env = CollaGenEnvironment.getInstance();
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();
    List<IGeneObj> objsTraites = new ArrayList<IGeneObj>();
    objsTraites.addAll(process.getObjetsTraites());
    // on prend toutes les contraintes des objets trait�s
    for (IGeneObj obj : objsTraites) {
      // on récupère ses contraintes instanciées
      for (ConstraintMonitor m : ConstraintMonitor.getFeatureConstraints(obj,
          env)) {
        contraintes.add(m);
      }
      // on récupère les contraintes instanciées de ses relations
      for (CollaGenRelation rel : env.getRelationsWithObj(obj)) {
        contraintes.addAll(ConstraintMonitor.getRelationConstraints(rel, env));
      }
    }

    return contraintes;
  }

  @Override
  public GeographicConcept getThemeDominant() {
    // TODO Auto-generated method stub
    return null;
  }

}
