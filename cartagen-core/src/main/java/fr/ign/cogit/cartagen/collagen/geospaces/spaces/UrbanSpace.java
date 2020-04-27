/*
 * Cr�� le 1 sept. 2008
 * 
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.cartagen.collagen.geospaces.spaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.relations.CollaGenRelation;
import fr.ign.cogit.cartagen.collagen.geospaces.model.ArealSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.IMesoObject;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * @author GTouya
 * 
 */
public class UrbanSpace extends ArealSpace {

  private GeoSpaceConcept geoConcept;

  public UrbanSpace(IPolygon geom) {
    super(geom);
    this.geoConcept = CollaGenEnvironment.getInstance()
        .getGeoSpaceConceptFromName("urban_area");
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
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * Récupère l'ensemble des contraintes des objets traités par le processus.
   * Méthode basique pour tester l'observation et évaluer le besoin en
   * échantillonnage.
   */
  public Set<SpecElementMonitor> getSimpleSample(GeneralisationProcess process) {
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();
    HashSet<IGeneObj> objsTraites = new HashSet<IGeneObj>();
    objsTraites.addAll(process.getObjetsTraites());
    // on prend toutes les contraintes des objets traités
    for (IGeneObj obj : objsTraites) {
      // on récupère ses contraintes instanciées
      contraintes.addAll(ConstraintMonitor.getFeatureConstraints(obj,
          CollaGenEnvironment.getInstance()));
      // on récupère les contraintes instanciées de ses relations
      Set<CollaGenRelation> rels = CollaGenEnvironment.getInstance()
          .getRelationsWithObj(obj);
      for (CollaGenRelation rel : rels) {
        contraintes.addAll(ConstraintMonitor.getRelationConstraints(rel,
            CollaGenEnvironment.getInstance()));
      }
    }

    return contraintes;
  }

  /**
   * Pour chaque partition de l'espace Urbain, prend au hasard 1 batiment sur 3.
   * Récupère l'ensemble des contraintes des batiments choisis. On fournit un
   * identifiant de la liste des objets traités pour éviter de choisir au hasard
   * d'autres batiments que ceux déjà choisis dans des appels précédents à la
   * méthode.
   */
  @SuppressWarnings("unchecked")
  public Set<SpecElementMonitor> getPartitionSample(int idPrev,
      GeneralisationProcess process) {
    // initialisation
    Set<SpecElementMonitor> contraintes = new HashSet<SpecElementMonitor>();
    ArrayList<IGeneObj> objsTraites = new ArrayList<IGeneObj>();
    objsTraites.addAll(process.getObjetsTraites());

    // on fait une boucle sur les objets traités
    for (int i = idPrev; i < objsTraites.size(); i++) {
      IGeneObj obj = objsTraites.get(i);
      // on teste si c'est bien une partition
      if (!IMesoObject.class.isAssignableFrom(obj.getClass()))
        continue;
      // arrivé ici, c'est une nouvelle partition
      // on récupère tous ses batiments
      @SuppressWarnings("rawtypes")
      Collection<IGeneObj> batis = ((IMesoObject) obj).getComponents();
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
  public GeoSpaceConcept getConcept() {
    return geoConcept;
  }

  @Override
  public GeographicConcept getThemeDominant() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SpecElementMonitor> getRandomSample(GeneralisationProcess process,
      double ratio) {
    // TODO Auto-generated method stub
    return null;
  }

}// class ZoneRurale
