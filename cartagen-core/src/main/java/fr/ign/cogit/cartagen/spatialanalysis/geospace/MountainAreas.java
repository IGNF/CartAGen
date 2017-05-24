package fr.ign.cogit.cartagen.spatialanalysis.geospace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification.GridCell;
import fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification.MountainGrid;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * This class contains static methods that identify the mountain areas according
 * to different methods.
 * @author GTouya
 * 
 */
public class MountainAreas {

  /**
   * Cette méthode reprend le principe de la catégorisation par grille de
   * (Walter 2008), appliquée à la détection des zones de montagne en analysant
   * les courbes de niveau dans chaque cellule de la grille. Cette méthode est
   * issue de la thèse de G. Touya (2011) et réutilisée dans la thèse de JF.
   * Girres (2012).
   * 
   * @param windowExtents l'enveloppe de la zone traitée
   * @param tailleCellule la taille des cellules de la grille (en m)
   * @param radius le rayon de recherche d'une cellule
   * @param seuilMontagne le seuil de classe globale au-dessus duquel une
   *          cellule est dans la montagne
   * @param simil le seuil de similarité entre deux clusters (si leur différence
   *          de classe est inférieure à ce seuil, ils sont fusionnés).
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static Set<IPolygon> buildMountainAreasGrid(IEnvelope windowExtents,
      int tailleCellule, int radius, double seuilMontagne, double simil,
      IFeatureCollection<CourbeDeNiveau> courbes, boolean critCourbes,
      double poidsC, double seuilBasC, double seuilHautC, boolean critDeniv,
      double poidsD, int seuilBasD, int seuilHautD, boolean critIndicePente,
      double poidsP, double seuilBasP, double seuilHautP) throws Exception {

    Set<IPolygon> areas = new HashSet<IPolygon>();
    Set<IGeometry> temp = new HashSet<IGeometry>();
    MountainGrid grille = new MountainGrid(tailleCellule, radius,
        windowExtents.minX(), windowExtents.maxX(), windowExtents.minY(),
        windowExtents.maxY(), simil, courbes);
    grille.setCriteres(critCourbes, poidsC, seuilBasC, seuilHautC, critDeniv,
        poidsD, seuilBasD, seuilHautD, critIndicePente, poidsP, seuilBasP,
        seuilHautP);
    HashMap<HashSet<GridCell>, Double> clusters = grille.creerClusters("total");
    for (HashSet<GridCell> cluster : clusters.keySet()) {
      if (clusters.get(cluster) >= seuilMontagne) {
        IPolygon geom = grille.creerGeomCluster(cluster);
        temp.add(geom);
      }
    }

    // on agrège les zones qui se touchent
    IGeometry geomTotal = CommonAlgorithmsFromCartAGen.geomColnUnion(temp);
    if (geomTotal instanceof IPolygon) {
      areas.add((IPolygon) geomTotal);
      return areas;
    }
    if (geomTotal instanceof IMultiSurface<?>) {
      for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) geomTotal)
          .getList()) {
        areas.add((IPolygon) simple);
      }
    }
    return areas;
  }

}
