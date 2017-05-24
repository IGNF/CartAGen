package fr.ign.cogit.cartagen.spatialanalysis.geospace;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconLaisse;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class CostalAreas {

  /**
   * A partir d'un set d'objets traits de côte, cr�e une zone littorale pour
   * chaque trait de côte. Fonctionne par parties connexes topologiquement et
   * agr�ge les traits de côte voisins tant que le total est inf�rieur au seuil.
   * 
   * @param vac_id : la version courante
   * @param setCotes : le set des objets traits de côte.
   * @param seuil : Longueur max d'une zone littorale (3000.0 m)
   * @param sigmaLiss : Seuil de lissage du trait de côte (15.0)
   * @param seuilBuff : Taille du buffer autour du trait de côte (2000.0)
   * @param seuilClust : Seuil de surface du clustering des zones
   *          créées(75000000.0)
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws IllegalArgumentException
   * @throws SecurityException
   */
  public static Set<IPolygon> buildCostalAreas(
      Collection<TronconLaisse> setCotes, double seuil, double sigmaLiss,
      double seuilBuff, double seuilClust) {

    Set<IPolygon> areas = new HashSet<IPolygon>();

    // on commence par séparer le réseau de laisses en parties connexes
    Set<HashSet<INode>> clusters = new Graph("coast", false,
        new HashSet<ArcReseau>(setCotes)).cutInConnexParts();
    // on parcourt maintenant ce set de clusters
    for (HashSet<INode> cluster : clusters) {
      // on ne continue pas si c'est un cluster d'un trait (une petite île)
      if (cluster.size() < 2) {
        continue;
      }
      // on convertit ce set de noeuds en set d'arcs réseau
      Set<ArcReseau> laisses = new HashSet<ArcReseau>();
      for (INode node : cluster) {
        for (IEdge edge : node.getEdges()) {
          laisses.add((ArcReseau) edge.getGeoObjects().iterator().next());
        }
      }

      // on construit pour l'instant une zone par laisse
      for (ArcReseau laisse : laisses) {
        IGeometry newGeom = GaussianFilter.gaussianFilter(
            (ILineString) laisse.getGeom(), sigmaLiss, 1);
        areas.add((IPolygon) newGeom.buffer(seuilBuff));
      }

    }

    // on réalise maintenant le clustering des zones créées
    return CostalAreas.clusteringZones(areas, seuilClust);
  }

  /**
   * A partir d'un set de zones littorales qui intersectent this, détermine en
   * fonction de la distance surfacique, le meilleur candidat à l'union.
   */
  private static IPolygon groupZones(IPolygon geom,
      Collection<DefaultFeature> voisins) {
    // initialisations
    IPolygon union = null;
    double distMin = 1.0;
    for (IFeature voisin : voisins) {
      // calcul de la distance surfacique
      double distSurf = Distances.distanceSurfacique(geom, voisin.getGeom());
      if (distSurf < distMin) {
        distMin = distSurf;
        union = (IPolygon) voisin.getGeom();
      }
    }

    return union;
  }

  /**
   * A partir d'un set de zones littorales r�alise un clustering pour regrouper
   * les petites zones qui s'intersectent. Les regroupement se de mani�re
   * it�rative dans le style de l'algorithme t-Gap selon la distance surfacique.
   * Le choix du candidat à traiter se fait en fonction de la taille des zones
   * (on prend la plus petite). Deux zones se regroupent par union des
   * géométries et élimination d'une des deux. L'algorithme s'arrête quand il
   * n'y a plus de zones en dessous du seuil ou plus de regroupement possible.
   */
  private static Set<IPolygon> clusteringZones(Set<IPolygon> zones,
      double seuilCluster) {
    Set<IPolygon> finalAreas = new HashSet<IPolygon>();
    // on commence par filtrer le set des zones pour ne garder que celles
    // qui nécessitent un regroupement: on en fait un stack avec le min en haut
    double surfMin = seuilCluster;
    Stack<IPolygon> pile = new Stack<IPolygon>();
    for (IPolygon zone : zones) {
      double surf = zone.area();
      // si la surface est supérieure au seuil, on passe
      if (surf >= seuilCluster) {
        finalAreas.add(zone);
        continue;
      }

      // on l'ajoute au stack
      pile.add(zone);
      // si la surface est inférieure au min, on met la zone en haut
      if (surf < surfMin) {
        pile.remove(zone);
        pile.push(zone);
        surfMin = surf;
      }
    }

    // on fait maintenant la boucle principale de la méthode :
    // on continue tant qu'il y a des zones dans la pile
    while (!pile.empty()) {
      // on prend la zone en haut
      IPolygon geom = pile.pop();
      // on cherche les zones qui l'intersectent par requête spatiale
      IFeatureCollection<DefaultFeature> coln = new FT_FeatureCollection<DefaultFeature>();
      for (IPolygon poly : pile) {
        coln.add(new DefaultFeature(poly));
      }

      Collection<DefaultFeature> voisins = coln.select(geom);
      if (voisins == null) {
        continue;
      }
      if (voisins.isEmpty()) {
        continue;
      }

      // on cherche le meilleur candidat au regroupement
      IPolygon candidat = CostalAreas.groupZones(geom, voisins);
      // on fusionne le candidat et zone
      IPolygon fusion = (IPolygon) candidat.union(geom);
      // on enlève candidat de la pile s'il y est
      pile.remove(candidat);

      // on teste si on remet zone dans la pile
      if (fusion.area() < seuilCluster) {
        pile.add(fusion);
      } else {
        finalAreas.add(fusion);
      }

      // si la pile n'est pas vide, on met la nouvelle plus petite zone en haut
      if (!pile.empty()) {
        double surfMini = seuilCluster;
        for (int i = 0; i < pile.size(); i++) {
          IPolygon zoneL = pile.get(i);
          double surf = zoneL.area();
          if (surf < surfMini) {
            pile.remove(zoneL);
            pile.push(zoneL);
            surfMini = surf;
          }
        }
      }
    }

    return finalAreas;
  }

}
