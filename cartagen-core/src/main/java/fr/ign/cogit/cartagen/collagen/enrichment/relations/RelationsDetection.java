package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.LinearProximity;

public class RelationsDetection {

  private static final double maxDistProxi2Buildings = 100.0;

  /**
   * Algorithme pour détecter les relations de proximité bati/bati et bati/route
   * dans un set de partitions.
   * @param batiBati true si on veut créer des relations de proximité entre
   *          bâtiments
   * @param batiRoute true si on veut créer des relations de proximité entre
   *          bâtiment et route
   */
  public static void detectRelationsProxi(boolean batiBati, boolean batiRoute) {

    IPopulation<IGeneObj> partitions = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.NETWORK_FACES_POP);
    // on fait une boucle sur les partitions
    for (IGeneObj part : partitions) {
      // on construit une triangulation de cette partition
      // on récupère les points de la triangulation dans une map
      Map<IDirectPosition, IGeneObj> points = new HashMap<IDirectPosition, IGeneObj>();
      List<TriangulationPoint> triPts = new ArrayList<TriangulationPoint>();
      // on récupère les bâtiments de la partition
      Collection<IGeneObj> batis = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(CartAGenDataSet.BUILDINGS_POP).select(part.getGeom());
      for (IGeneObj bati : batis) {
        points.put(bati.getGeom().centroid(), bati);
        triPts.add(new TriangulationPointImpl(bati.getGeom().centroid()));
      }

      // on récupère les routes
      Collection<IGeneObj> routes = CartAGenDoc.getInstance()
          .getCurrentDataset().getCartagenPop(CartAGenDataSet.ROADS_POP)
          .select(part.getGeom());
      for (IGeneObj route : routes) {
        IGeometry newGeom = LineDensification.densification(route.getGeom(),
            20.0);
        // on met un point pour chaque vertex
        for (IDirectPosition pt : newGeom.coord()) {
          points.put(pt, route);
          triPts.add(new TriangulationPointImpl(pt));
        }
      }
      // on vérifie qu'il y a bien au moins 3 pts pour ne pas faire planter
      // la triangulation Shewchuk
      if (points.size() < 3)
        continue;

      // p: contrainte
      // c: construit l'enveloppe convexe (utile en cas de segments ferm�s)
      // z: numerotation de 0 a n-1 et pas de 1 a n
      // e: donne les edges
      // Q, V,VV,VVV: commentaires generes (Q pour quit, V pour verbose)
      String options = "pczeBQ";
      Triangulation tri = new Triangulation(triPts,
          new TriangulationSegmentFactoryImpl(),
          new TriangulationTriangleFactoryImpl());
      tri.compute(true, part.getGeom(), options);

      // on détermine les relations de proximité à partir des segments
      // construits
      Set<ObjectCouple> couplesTraites = new HashSet<ObjectCouple>();
      for (TriangulationSegment segment : tri.getSegments()) {
        IDirectPosition point1 = segment.getPoint1().getPosition();
        IGeneObj obj1 = points.get(point1);
        // steiner point
        if (obj1 == null)
          continue;

        IDirectPosition point2 = segment.getPoint2().getPosition();
        IGeneObj obj2 = points.get(point2);
        // steiner point
        if (obj2 == null)
          continue;

        // on teste qu'au moins un des deux est un bâtiment
        if (!batis.contains(obj1) && !batis.contains(obj2))
          continue;
        IGeneObj bati = obj1;
        IGeneObj autre = obj2;
        if (!batis.contains(obj1)) {
          bati = obj2;
          autre = obj1;
        }

        if (batis.contains(autre) && batiBati) {
          // cas d'une relation avec un batiment
          // on cr�e une nouvelle relation bati/bati
          CollaGenEnvironment.getInstance().getRelations()
              .add(new Building2ProximityRelation(bati, autre, null,
                  maxDistProxi2Buildings));
        } else {
          if (!batiRoute)
            continue;
          // cas d'une relation avec une route
          ObjectCouple couple = new RelationsDetection().new ObjectCouple(obj1,
              obj2);

          // on teste si cette relation a déjà été créée (avec un autre point de
          // la route)
          if (couplesTraites.contains(couple))
            continue;
          // on crée une nouvelle relation bati/route
          CollaGenEnvironment.getInstance().getRelations()
              .add(new BuildingRoadProximityRelation(bati, autre, null));
          // on ajoute le couple au set
          couplesTraites.add(couple);
        } // else de if(batis.contains(obj2))
      } // for(int j=0;j<jout.numberofedges;j+=2)
    } // for, boucle sur le set des partitions
  }

  /**
   * Algorithme pour détecter les relations d'orientation relative bati/bati et
   * bati/route dans un set de partitions.
   * @param partitions
   * @param classeBati
   * @param classeRoute
   */
  public static void detectRelationsOrientRel() {
    IPopulation<IGeneObj> routesPop = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.ROADS_POP);
    IPopulation<IGeneObj> batisPop = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.BUILDINGS_POP);
    IPopulation<IGeneObj> faces = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartagenPop(CartAGenDataSet.NETWORK_FACES_POP);
    for (IGeneObj face : faces) {
      Collection<IGeneObj> batis = batisPop.select(face.getGeom());
      Collection<IGeneObj> routes = routesPop.select(face.getGeom());
      for (IGeneObj bati : batis) {
        IRoadLine route = (IRoadLine) SpatialQuery
            .selectNearestWithDistance(bati.getGeom(),
                new FT_FeatureCollection<IGeneObj>(routes), 75.0)
            .get(0);
        if (route == null)
          continue;
        if (!BuildingRoadRelativeOrientRelation.estValide((IBuilding) bati,
            route))
          continue;
        CollaGenEnvironment.getInstance().getRelations()
            .add(new BuildingRoadRelativeOrientRelation(bati, route, null));
      }
    }

  }

  /**
   * Algorithme pour détecter les relations de positionnement relatif
   * bati/impasse dans un set de partitions.
   * @param partitions
   * @param classeBati
   * @param classeRoute
   */
  public static void detectRelationsPositRel() {
    IPopulation<IGeneObj> partitions = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.NETWORK_FACES_POP);
    // on fait une boucle sur les partitions
    for (IGeneObj part : partitions) {
      Collection<IGeneObj> batis = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(CartAGenDataSet.BUILDINGS_POP).select(part.getGeom());
      Collection<IGeneObj> routes = CartAGenDoc.getInstance()
          .getCurrentDataset().getCartagenPop(CartAGenDataSet.ROADS_POP)
          .select(part.getGeom());
      for (IGeneObj bati : batis) {
        IRoadLine route = (IRoadLine) SpatialQuery
            .selectNearestWithDistance(bati.getGeom(),
                new FT_FeatureCollection<IGeneObj>(routes), 75.0)
            .get(0);
        if (route == null)
          continue;
        if (!BuildingDeadEndRelPositionRelation.estValide((IBuilding) bati,
            route))
          continue;
        CollaGenEnvironment.getInstance().getRelations()
            .add(new BuildingDeadEndRelPositionRelation(bati, route, null));
      }
    }

  }

  /**
   * Algorithme pour détecter les relations d'alignement entre un batiment et un
   * carrefour en T.
   * 
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  public static void detectRelationsAlignBatiCroisement()
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException {

    // on récupère les carrefours routiers de la base
    IPopulation<IGeneObj> carrefs = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.ROAD_NODES_POP);
    for (IGeneObj carref : carrefs) {
      // cherche les bâtiments dans un rayon donné autour du carrefour
      Collection<IGeneObj> batis = SpatialQuery.selectInRadius(
          carref.getGeom().centroid(),
          BuildingCrossroadAlignRelation.SEARCH_DIST, IBuilding.class);
      for (IGeneObj bati : batis) {
        if (!BuildingCrossroadAlignRelation.estValide((IBuilding) bati,
            (IRoadNode) carref))
          continue;
        CollaGenEnvironment.getInstance().getRelations()
            .add(new BuildingCrossroadAlignRelation(bati, carref, null));
      }
    }
  }

  /**
   * Algorithme pour détecter les relations d'appartenance à une clairière entre
   * un batiment et une foret.
   * 
   */
  public static void detectRelationsBatiDansClairiere(int typeForest) {
    // on récupère les forets de la base
    IPopulation<IGeneObj> forests = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.LANDUSE_AREAS_POP);
    for (IGeneObj forest : forests) {
      if (((ISimpleLandUseArea) forest).getType() != typeForest)
        continue;
      IGeometry outer = new GM_Polygon(
          ((IPolygon) forest.getGeom()).getExterior());

      // cherche les bâtiments complètement inclus dans l'outer ring de la foret
      Collection<IGeneObj> batis = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(CartAGenDataSet.BUILDINGS_POP).select(outer);
      for (IGeneObj bati : batis) {
        if (!BuildingInClearingRelation.estValide((IBuilding) bati,
            (ISimpleLandUseArea) forest))
          continue;
        CollaGenEnvironment.getInstance().getRelations()
            .add(new BuildingInClearingRelation(bati, forest, null));
      }
    }

  }

  /**
   * Détecte les relations de proximité entre éléments de réseau, en prenant en
   * compte leur largeur de symbole. La recherche est faite localement par
   * buffer.
   * @param vac
   * @param proxiThresh la distance max de recherche des conflits (50.0 par
   *          défaut)
   * @param partitions
   * @param classesRes
   */
  public static void detectRelationsProxiReseau(double proxiThresh,
      Set<String> classesRes) {
    RelationsDetection a = new RelationsDetection();
    IPopulation<IGeneObj> partitions = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.NETWORK_FACES_POP);
    // on stocke les couples déjà traités
    HashSet<ObjectCouple> couplesTraites = new HashSet<ObjectCouple>();
    // on fait une boucle sur les partitions
    for (IGeneObj part : partitions) {
      // on récupère les éléments de réseau de la partition
      IFeatureCollection<IGeneObj> routes = new FT_FeatureCollection<IGeneObj>(
          CartAGenDoc.getInstance().getCurrentDataset()
              .getCartagenPop(CartAGenDataSet.ROADS_POP)
              .select(part.getGeom()));
      // on fait une boucle sur les objets de la partition
      for (IGeneObj obj : routes) {
        // on cherche les objets proches
        IGeometry buffer = obj.getGeom().buffer(proxiThresh);
        Collection<IGeneObj> voisins = routes.select(buffer);
        voisins.remove(obj);
        // on fait une boucle sur les voisins
        for (IGeneObj voisin : voisins) {
          // on crée le couple
          ObjectCouple couple = a.new ObjectCouple(obj, voisin);
          // on teste si cette relation a déjà été créée
          if (couplesTraites.contains(couple))
            continue;
          // il faut maintenant vérifier si c'est bien une relation de proximité
          // on commence par tester si les objets s'intersectent
          if (!obj.getGeom().intersects(voisin.getGeom())) {
            // on crée la nouvelle relation
            CollaGenEnvironment.getInstance().getRelations()
                .add(new Network2ProximityRelation(obj, voisin, null));
            // on ajoute le couple au set
            couplesTraites.add(couple);
            continue;
          }
          // deux objets s'intersectant peuvent aussi avoir une relation de
          // proximité
          // s'il sont proches loin de l'intersection
          // TODO utiliser les techniques de Nickerson (modifier aussi les
          // méthodes
          // pour obtenir la distance min et le symbol overlap)
          if (!obj.getGeom().touches(voisin.getGeom())) {
            ILineString line1 = (ILineString) obj.getGeom();
            ILineString line2 = (ILineString) voisin.getGeom();
            LinearProximity proxi = new LinearProximity(line1, line2);
            ILineSegment segment = proxi.getMinDistAwayIntersection();
            // cas avec de petits éléments de réseau qui se croisent
            if (segment == null)
              continue;
            if (segment.length() > proxiThresh)
              continue;
            // on crée la nouvelle relation
            CollaGenEnvironment.getInstance().getRelations()
                .add(new Network2ProximityRelation(obj, voisin, null));
            // on ajoute le couple au set
            couplesTraites.add(couple);
            continue;
          }
        }
      }
    }
  }

  class ObjectCouple {
    private IGeneObj obj1, obj2;

    ObjectCouple(IGeneObj obj1, IGeneObj obj2) {
      this.obj1 = obj1;
      this.obj2 = obj2;
    }

    @Override
    public int hashCode() {
      return obj1.hashCode() + obj2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ObjectCouple))
        return false;
      ObjectCouple other = (ObjectCouple) obj;
      if (this.obj1.equals(other.obj1) && this.obj2.equals(other.obj2))
        return true;
      if (this.obj1.equals(other.obj2) && this.obj2.equals(other.obj1))
        return true;
      return false;
    }

  }
}
