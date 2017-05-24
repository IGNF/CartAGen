package fr.ign.cogit.cartagen.spatialanalysis.geospace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.spatialanalysis.SpatialAnalysisUtil;
import fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification.GridCell;
import fr.ign.cogit.cartagen.spatialanalysis.geospace.gridclassification.UrbanGrid;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanAreaComputationJTS;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * This class contains static methods that identify the urban areas according to
 * different methods.
 * @author GTouya
 * 
 */
public class UrbanAreas {

  private static Logger logger = Logger.getLogger(UrbanAreas.class.getName());

  /**
   * MÃ©thode 1 de construction des zones urbaines : il s'agit de la reprise de
   * la mÃ©thode de URBA fonctionnant par buffers autour des bÃ¢timents. param
   * conseillÃ©s :le buffer est de 25 m et le seuil de sÃ©lection est de 1 000
   * 000mÂ². Pour le seuil de conservation des trous, on conseille 100 000 mÂ²
   * 
   * @param batis : la feature collection des bÃ¢timents
   * @param seuilBuff : le taille des buffers autour des bÃ¢timents
   * @param seuilSel : la surface Ã  partir de laquelle on garde l'objet
   * @param seuilTrous la surface minimum pour qu'un trou soit conservÃ©
   * @param seuilFermeture le pas de la fermeture appliquÃ©e au contour
   * 
   */
  @SuppressWarnings("unchecked")
  public static Set<IPolygon> buildUrbanAreasBoffet(
      IFeatureCollection<Batiment> batis, double seuilBuff, double seuilSel,
      double seuilTrous, double seuilFermeture) {

    Set<IPolygon> areas = new HashSet<IPolygon>();
    // on récupère les géomètries des bâtiments
    ArrayList<IGeometry> geoms = new ArrayList<IGeometry>();
    for (Batiment bati : batis)
      geoms.add(bati.getGeom());

    // on lance l'algorithme
    IGeometry totalGeom = UrbanAreaComputationJTS.calculTacheUrbaine(geoms,
        seuilBuff, seuilFermeture, 4, 2.0);

    // on teste si c'est un polygone ou une géométrie complexe
    if (totalGeom instanceof IPolygon) {
      if (totalGeom.area() > seuilSel) {
        // on enlève les petits trous
        IPolygon area = CommonAlgorithmsFromCartAGen
            .removeSmallHoles((IPolygon) totalGeom, seuilTrous);
        areas.add(area);
        return areas;
      }
    }

    if (totalGeom instanceof IMultiSurface<?>) {
      for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) totalGeom)
          .getList()) {
        // on vérifie que la surface est assez grande
        if (simple.area() <= seuilSel)
          continue;
        // on enlève les petits trous
        IPolygon area = CommonAlgorithmsFromCartAGen
            .removeSmallHoles((IPolygon) simple, seuilTrous);
        areas.add(area);
      }
    }

    logger.fine(areas.size() + " zones urbaines (Boffet) construites");
    return areas;
  }

  /**
   * Méthode 2 de construction des zones urbaines : il s'agit de la reprise de
   * la méthode d'Omair Chaudhry avec la notion de citiness. param conseillés :
   * le nb de voisins doit être de 50 selon (Chaudhry 2007).
   * 
   * @param buildings : la feature collection des bâtiments sources
   * @param partitions : les objets partitions à utiliser
   * @param nbVoisins : le nb de voisins à prendre en compte autour des
   *          bâtiments
   * @param constanteK : la constante utilisée pour calculer l'expansion d'un
   *          bâtiment.
   * @param seuilSel : la surface à partir de laquelle on garde l'objet
   * @param maxCity : la valeur maximum de citiness utilisée pour normalisation
   *          (valeur déterminée sur le 64 : 0.225)
   * 
   */
  @SuppressWarnings("unchecked")
  public static Set<IPolygon> buildUrbanAreasCitiness(
      IFeatureCollection<Batiment> buildings, Set<INetworkFace> partitions,
      int nbVoisins, double constanteK, double seuilSel, double maxCity) {

    // initialisation
    Set<IPolygon> areas = new HashSet<IPolygon>();
    Collection<IGeometry> areasPart = new HashSet<IGeometry>();
    Map<Batiment, Double> map = new HashMap<Batiment, Double>();
    // on fait une boucle sur les partitions sélectionnées
    for (INetworkFace face : partitions) {
      // on récupère les bâtiments de la partition
      Collection<Batiment> batiments = buildings.select(face.getGeom());
      if (batiments.size() == 0) {
        continue;
      }

      // on parcourt les batiments
      Set<IGeometry> buffers = new HashSet<IGeometry>();
      for (Batiment bati : batiments) {
        // on calcule sa valeur de citiness
        double citiness = computeCitiness(bati, nbVoisins, buildings);
        map.put(bati, new Double(citiness));

        // on récupère et normalise la citiness
        citiness = citiness / maxCity;
        // on en déduit la valeur d'expansion
        double expansion = constanteK * citiness;
        // si l'expansion est trop petite, on la fixe pour éviter des bugs
        if (expansion < 2.0) {
          expansion = 2.0;
        }
        // on construit le buffer autour du bâtiment
        buffers.add(bati.getGeom().buffer(expansion));
      }

      // on combine les buffers en surfaces disjointes
      IGeometry union = CommonAlgorithmsFromCartAGen.geomColnUnion(buffers);
      if (union instanceof IPolygon) {
        // on supprime les trous et on réalise une fermeture morphologique
        IPolygon areaNoHole = CommonAlgorithmsFromCartAGen
            .removeHoles((IPolygon) union);
        MorphologyTransform morph = new MorphologyTransform(20.0, 2);
        IPolygon areaClosed = morph.closing(areaNoHole);
        // on fait un petit filtrage D&P
        IPolygon area = Filtering.DouglasPeuckerPoly(areaClosed, 1.0);
        areasPart.add(area);
        continue;
      }
      if (union instanceof IMultiSurface<?>) {
        for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) union)
            .getList()) {
          if (simple.area() < 200.0)
            continue;
          // on supprime les trous et on réalise une fermeture morphologique
          IPolygon areaNoHole = CommonAlgorithmsFromCartAGen
              .removeHoles((IPolygon) simple);
          MorphologyTransform morph = new MorphologyTransform(20.0, 2);
          IPolygon areaClosed = morph.closing(areaNoHole);
          // on fait un petit filtrage D&P
          IPolygon area = Filtering.DouglasPeuckerPoly(areaClosed, 1.0);
          areasPart.add(area);
        }
      }
    }

    // il faut maintenant agréger tous les objets temporaires globalement
    IGeometry geomGlobale = CommonAlgorithmsFromCartAGen
        .geomColnUnion(areasPart);

    if (geomGlobale instanceof IPolygon) {
      if (geomGlobale.area() > seuilSel) {
        // on supprime les trous et on réalise une fermeture morphologique
        IPolygon areaNoHole = CommonAlgorithmsFromCartAGen
            .removeHoles((IPolygon) geomGlobale);
        MorphologyTransform morph = new MorphologyTransform(20.0, 2);
        IPolygon areaClosed = morph.closing(areaNoHole);
        // on fait un petit filtrage D&P
        IPolygon area = Filtering.DouglasPeuckerPoly(areaClosed, 1.0);
        areas.add(area);
      }
      return areas;
    }

    if (geomGlobale instanceof IMultiSurface<?>) {
      for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) geomGlobale)
          .getList()) {
        if (simple.area() < seuilSel)
          continue;
        // on supprime les trous et on réalise une fermeture morphologique
        IPolygon areaNoHole = CommonAlgorithmsFromCartAGen
            .removeHoles((IPolygon) simple);
        MorphologyTransform morph = new MorphologyTransform(25.0, 2);
        IPolygon areaClosed = morph.closing(areaNoHole);
        // on fait un petit filtrage D&P
        IPolygon area = Filtering.DouglasPeuckerPoly(areaClosed, 1.0);
        areas.add(area);
      }
    }

    return areas;
  }

  /**
   * Calcule l'indice de citiness pour un bÃ¢timent issu des travaux de Chaudhry
   * & Mackaness. Cet indice reprÃ©sente la vraisemblance que le bÃ¢timent soit
   * en zone urbaine, tenant compte de sa taille et de son nombre de voisins.
   * @param classIds
   * @param batiment
   * @param nbVoisins
   * @return la valeur de citiness
   */
  private static double computeCitiness(Batiment batiment, int nbVoisins,
      IFeatureCollection<Batiment> batis) {
    Collection<IFeature> plusProches = SpatialQuery
        .selectNearestN(batiment.getGeom(), batis, nbVoisins, 100.0);
    double aire = batiment.getGeom().area();
    // on parcourt les plusProches pour calculer la somme des aires et des
    // distances
    double sommeAires = 0.0;
    double sommeDist = 0.0;
    for (IFeature voisin : plusProches) {
      // on calcule la distance entre batiment et voisin
      double dist = new GeometryProximity(batiment.getGeom(), voisin.getGeom())
          .getDistance();
      sommeDist = sommeDist + (dist * dist);
      sommeAires += voisin.getGeom().area();
    }
    double citiness = Math.sqrt(sommeAires) * Math.sqrt(aire) / sommeDist;

    return citiness;
  }

  /**
   * MÃ©thode 3 de construction des zones urbaines : il s'agit de la reprise de
   * la mÃ©thode de (Walter, 2008) par grille raster. Les mesures sont
   * effectuÃ©es selon des critÃ¨res Ã  choisir (nb de noeuds routiers,
   * rectangularitÃ© des routes, nb de bÃ¢timents). param conseillÃ©s :le buffer
   * est de 25 m et le seuil de sÃ©lection est de 1 000 000mÂ². Pour Ã©viter de
   * surcharger la mÃ©moire, on travaille partition par partition puis on
   * recolle.
   * 
   * @param windowExtents la gÃ©omÃ©trie du rectangle dÃ©limitant la zone
   *          d'Ã©tude
   * @param critNoeuds true si on utilise le critÃ¨re de densitÃ© des noeuds.
   * @param poidsN poids du critÃ¨re de densitÃ© des noeuds (somme des poids =
   *          1)
   * @param seuilBasN seuil bas du critÃ¨re de densitÃ© des noeuds (conseil : 5
   *          * ((radiusCellule/150)Â²))
   * @param seuilHautN seuil haut du critÃ¨re de densitÃ© des noeuds (conseil :
   *          20 * ((radiusCellule/150)Â²))
   * @param critRect true si on utilise le critÃ¨re de rectangularitÃ©.
   * @param poidsR poids du critÃ¨re de rectangularitÃ© (somme des poids = 1)
   * @param seuilBasR seuil bas du critÃ¨re de rectangularitÃ© (Math.PI/4.0)
   * @param seuilHautR seuil haut du critÃ¨re de rectangularitÃ© (Math.PI/3.0)
   * @param critBati true si on utilise le critÃ¨re de densitÃ© des batiments.
   * @param poidsB poids du critÃ¨re de densitÃ© des batiments (somme des poids
   *          = 1)
   * @param seuilBasB seuil bas du critÃ¨re de densitÃ© des batiments (conseil :
   *          6 * ((radiusCellule/150)Â²))
   * @param seuilHautB seuil haut du critÃ¨re de densitÃ© des batiments (conseil
   *          : 20 (* (radiusCellule/150)Â²))
   * @param seuilClasse le seuil au-dessus duquel la classe du cluster est
   *          considÃ©rÃ©e comme de la ville
   * @throws Exception
   * 
   */
  public static Set<IPolygon> buildUrbanAreasGrid(IEnvelope windowExtents,
      IFeatureCollection<TronconDeRoute> roads,
      IFeatureCollection<NoeudRoutier> nodes, boolean critNoeuds, double poidsN,
      int seuilBasN, int seuilHautN, boolean critRect, double poidsR,
      int seuilBasR, int seuilHautR, boolean critBati, double poidsB,
      int seuilBasB, int seuilHautB, double seuilClasse, double seuilSimilarite)
      throws Exception {

    Set<IPolygon> areas = new HashSet<IPolygon>();

    // create the grid
    UrbanGrid grille = new UrbanGrid(100, 300, windowExtents.minX(),
        windowExtents.maxX(), windowExtents.minY(), windowExtents.maxY(),
        seuilSimilarite, roads, nodes);

    grille.setCriteres(critNoeuds, poidsN, seuilBasN, seuilHautN, critRect,
        poidsR, seuilBasR, seuilHautR);
    HashMap<HashSet<GridCell>, Double> clusters = grille.creerClusters("total");
    Iterator<HashSet<GridCell>> iter = clusters.keySet().iterator();
    while (iter.hasNext()) {
      HashSet<GridCell> cluster = iter.next();
      double classe = clusters.get(cluster);
      if (classe + grille.getSeuilSimilarite() >= seuilClasse) {
        // c'est une ville
        IPolygon geom = grille.creerGeomCluster(cluster);

        // on ajoute le polygone Ã  la collection
        areas.add(geom);
      }
    }
    return areas;
  }

  /**
   * This methods builds rurban areas, areas that are not completely urban nor
   * rural. It uses the Boffet (2000) method for urban areas, i.e. buffers
   * around buildings, but with different thresholds. The advice for buffer size
   * is 100 m, for selection threshold, it's 2 000 000 m² and for hole size,
   * it's 300 000 m². The advised values for urban areas can be found in
   * buildUrbanAreasBoffet comments.
   * 
   * @param buildings The feature collection of buildings on which the areas are
   *          built
   * @param seuilBuffUrban
   * @param seuilSelUrban
   * @param seuilTrousUrban
   * @param seuilFermeture
   * @param seuilBuffRurb
   * @param seuilSelRurb
   * @param seuilTrousRurb
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Set<IPolygon> buildRurbanAreas(
      IFeatureCollection<Batiment> buildings, double seuilBuffUrban,
      double seuilSelUrban, double seuilTrousUrban, double seuilFermeture,
      double seuilBuffRurb, double seuilSelRurb, double seuilTrousRurb) {

    // initialisation
    Set<IPolygon> areas = new HashSet<IPolygon>();

    // first build urban areas with the Boffet method
    Collection<IGeometry> urbanAreas = new HashSet<IGeometry>();
    urbanAreas.addAll(buildUrbanAreasBoffet(buildings, seuilBuffUrban,
        seuilSelUrban, seuilTrousUrban, seuilFermeture));

    // then build the Rurban areas with the same method but with different
    // thresholds (more distance between buildings is tolerated).
    Set<IPolygon> rurbanAreas = buildUrbanAreasBoffet(buildings, seuilBuffRurb,
        seuilSelRurb, seuilTrousRurb, seuilFermeture);

    // merge the urban areas into a single complex area
    IGeometry union = CommonAlgorithmsFromCartAGen.geomColnUnion(urbanAreas);
    // remove union from the rurban areas
    for (IPolygon rurban : rurbanAreas) {
      IGeometry diff = rurban.difference(union);
      if (diff instanceof IPolygon)
        areas.add((IPolygon) diff);
      if (diff instanceof IMultiSurface<?>) {
        for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) diff)
            .getList()) {
          if (simple.area() > seuilSelRurb)
            areas.add((IPolygon) simple);
        }
      }
    }

    return areas;
  }

  /**
   * Same as method one but carries out a morphological opening afterwards and
   * partitions areas with the roads given as parameters.
   * 
   * @param buildings The feature collection of buildings on which the areas are
   *          built
   * @param seuilBuffUrban
   * @param seuilSelUrban
   * @param seuilTrousUrban
   * @param seuilFermeture
   * @param seuilOuverture
   * @param seuilBuffRurb
   * @param seuilSelRurb
   * @param seuilTrousRurb
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Set<IPolygon> buildRurbanAreas2(
      IFeatureCollection<Batiment> buildings,
      IFeatureCollection<TronconDeRoute> roads, double seuilBuffUrban,
      double seuilSelUrban, double seuilTrousUrban, double seuilFermeture,
      double seuilOuverture, double seuilBuffRurb, double seuilSelRurb,
      double seuilTrousRurb) {

    // initialisation
    Set<IPolygon> areas = new HashSet<IPolygon>();

    // first build urban areas with the Boffet method
    Collection<IGeometry> urbanAreas = new HashSet<IGeometry>();
    urbanAreas.addAll(buildUrbanAreasBoffet(buildings, seuilBuffUrban,
        seuilSelUrban, seuilTrousUrban, seuilFermeture));

    // then build the Rurban areas with the same method but with different
    // thresholds (more distance between buildings is tolerated).
    Set<IPolygon> rurbanAreas = buildUrbanAreasBoffet(buildings, seuilBuffRurb,
        seuilSelRurb, seuilTrousRurb, seuilFermeture);

    // merge the urban areas into a single complex area
    IGeometry union = CommonAlgorithmsFromCartAGen.geomColnUnion(urbanAreas);
    // remove union from the rurban areas
    for (IPolygon rurban : rurbanAreas) {
      IGeometry diff = rurban.difference(union);
      if (diff instanceof IPolygon) {
        IGeometry open = new MorphologyTransform(seuilOuverture, 2)
            .opening((IPolygon) diff);
        if (open instanceof IPolygon)
          areas.add((IPolygon) open);
        if (open instanceof IMultiSurface<?>) {
          for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) open)
              .getList()) {
            if (simple.area() > seuilSelRurb)
              areas.add((IPolygon) simple);
          }
        }
      }
      if (diff instanceof IMultiSurface<?>) {
        for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) diff)
            .getList()) {
          IGeometry open = new MorphologyTransform(seuilOuverture, 2)
              .opening((IPolygon) simple);
          if (open instanceof IPolygon)
            if (open.area() > seuilSelRurb)
              areas.add((IPolygon) open);
          if (open instanceof IMultiSurface<?>) {
            for (IOrientableSurface simple1 : ((IMultiSurface<IOrientableSurface>) open)
                .getList()) {
              if (simple1.area() > seuilSelRurb)
                areas.add((IPolygon) simple1);
            }
          }
        }
      }
    }

    // then partition the areas with the roads
    // first build faces of the network composed of the roads
    if (!roads.isEmpty()) {
      CarteTopo carteTopo = new CarteTopo("cartetopo");
      carteTopo.importClasseGeo(roads, true);
      // set up the topo map
      carteTopo.creeNoeudsManquants(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      carteTopo.rendPlanaire(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      // build faces
      carteTopo.creeTopologieFaces();
      carteTopo.getPopFaces().initSpatialIndex(Tiling.class, false);

      // now loop on the rurban areas
      Set<IPolygon> copyToLoop = new HashSet<IPolygon>(areas);
      areas.clear();
      for (IPolygon rurban : copyToLoop) {
        Set<IPolygon> partitions = SpatialAnalysisUtil.partitionPolygon(rurban,
            carteTopo.getPopFaces());
        for (IPolygon partition : partitions) {
          if (partition.area() > 10000.0)
            areas.add(partition);
        }
      }
    }

    return areas;
  }
}
