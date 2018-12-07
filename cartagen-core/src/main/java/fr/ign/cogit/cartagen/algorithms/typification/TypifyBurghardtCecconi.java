package fr.ign.cogit.cartagen.algorithms.typification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.NoeudDelaunay;
import fr.ign.cogit.geoxygene.contrib.delaunay.Triangulation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientation;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class TypifyBurghardtCecconi {

  // *****************************************
  // PROPERTIES
  // *****************************************
  private BidiMap<Placeholder, IDirectPosition> mapPhDp = new DualHashBidiMap<Placeholder, IDirectPosition>();
  private Triangulation triangulationInitiale;
  private Triangulation triangulationFinale;
  private double initialScale;
  private double targetScale;
  private IPopulation<IGeneObj> buildingsIni;
  private IPopulation<IGeneObj> roads;
  private IPopulation<IGeneObj> blocks;
  private double targetNumberOfObjects;
  private AbstractCreationFactory factory;

  // *****************************************
  // CONSTRUCTOR
  // *****************************************
  public TypifyBurghardtCecconi(double targetScale) {
    // **** Enrichments **** //
    // get useful elements
    CartAGenDoc doc = CartAGenDoc.getInstance();
    CartAGenDataSet dataset = doc.getCurrentDataset();
    INetwork net = CartAGenDoc.getInstance().getCurrentDataset()
        .getRoadNetwork();
    this.factory = dataset.getCartAGenDB().getGeneObjImpl()
        .getCreationFactory();
    // enrichment : build urban blocks
    if (dataset.getTowns().isEmpty()) {
      NetworkEnrichment.enrichNetwork(dataset, net, factory);
      UrbanEnrichment.buildTowns(dataset, false, factory);
    }
    // get useful populations
    this.buildingsIni = dataset.getCartagenPop("buildings");
    this.roads = dataset.getCartagenPop("roads");
    this.blocks = dataset.getCartagenPop("blocks");
    // scales initialisation
    System.out
        .println("initial scale : " + dataset.getCartAGenDB().getSymboScale());
    System.out.println("target scale : " + targetScale);
    this.setInitialScale(dataset.getCartAGenDB().getSymboScale());
    this.setTargetScale(targetScale);
    Legend.setSYMBOLISATI0N_SCALE(targetScale);

    // calcule et initialise le numbre d'objets cible
    this.setTargetNumberOfObjects(this.calculateTargetNumberOfObjects(
        initialScale, targetScale, buildingsIni));
    System.out.println("target nb: " + this.getTargetNumberOfObjects());

    // ******************* si besoin pour débug *******************
    // // crée la triangulation initiale à partir des centroïdes
    // this.setTriangulationInitiale(this.createTriangulation());
    // System.out.println("triangulation initiale ok");
    // // affichage à mettre en dernier !!
    // this.afficheTriangulation(this.triangulationInitiale);
    // ************************************************************
    //

    // généralisation du résultat
    this.generalize();
    System.out.println("généralisation effectuée");

    double debut = System.currentTimeMillis();
    // typification
    this.setTriangulationFinale(this.typify());
    System.out.println("triangulation finale ok");
    double temps = (System.currentTimeMillis() - debut) * 0.001;
    System.out.println("temps d'éxécution : " + temps + " sec");

    // ****************************TO DELETE********************
    // // en m terrain
    // System.out.println("symboScale : " + Legend.getSYMBOLISATI0N_SCALE());
    // double sizeMin = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
    // * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    // System.out.println("GeneSpec : " + sizeMin);
    // for (int i = 0; i < dataset.getBuildings().size(); i++) {
    // IBuilding build = dataset.getBuildings().get(i);
    // double area = build.getGeom().area();
    // if (area < sizeMin) {
    // System.out
    // .println("Aire trop petite : " + build.getId() + " = " + area);
    // }
    // }

    // ****************************TO DELETE********************
  }

  // ***************
  // GETTERS
  // ***************
  public BidiMap<Placeholder, IDirectPosition> getMapPhDp() {
    return mapPhDp;
  }

  public Triangulation getTriangulationInitiale() {
    return triangulationInitiale;
  }

  public Triangulation getTriangulationFinale() {
    return triangulationFinale;
  }

  public double getInitialScale() {
    return initialScale;
  }

  public double getTargetScale() {
    return targetScale;
  }

  public IPopulation<? extends IFeature> getBuildingsIni() {
    return buildingsIni;
  }

  public IPopulation<? extends IFeature> getRoads() {
    return roads;
  }

  public IPopulation<? extends IFeature> getBlocks() {
    return blocks;
  }

  public double getTargetNumberOfObjects() {
    return targetNumberOfObjects;
  }

  // ***************
  // SETTERS
  // ***************
  public void setMapPhDp(BidiMap<Placeholder, IDirectPosition> mapPhDp) {
    this.mapPhDp = mapPhDp;
  }

  public void setTriangulationInitiale(Triangulation triangulationInitiale) {
    this.triangulationInitiale = triangulationInitiale;
  }

  public void setTriangulationFinale(Triangulation triangulationFinale) {
    this.triangulationFinale = triangulationFinale;
  }

  public void setBuildingsIni(IPopulation<IGeneObj> buildingsIni) {
    this.buildingsIni = buildingsIni;
  }

  public void setRoads(IPopulation<IGeneObj> roads) {
    this.roads = roads;
  }

  public void setBlocks(IPopulation<IGeneObj> blocks) {
    this.blocks = blocks;
  }

  public void setInitialScale(double initialScale) {
    this.initialScale = initialScale;
  }

  public void setTargetScale(double targetScale) {
    this.targetScale = targetScale;
  }

  public void setTargetNumberOfObjects(double targetNumberOfObjects) {
    this.targetNumberOfObjects = targetNumberOfObjects;
  }

  // ***************
  // METHODS
  // ***************

  public double calculateTargetNumberOfObjects(double initialScale,
      double targetScale, IPopulation<? extends IFeature> population) {
    // ------------calcule le nombre d'objets cible--------------
    double initialObjectsNb = population.size();
    // ----avec loi de Töpfer
    double targetObjectsNb = Math
        .round(initialObjectsNb * (initialScale / targetScale)); // Math.sqrt
    return targetObjectsNb;
  }

  public Triangulation createTriangulation() {
    JtsAlgorithms jtsAlgo = new JtsAlgorithms();
    // ---------------------Création des noeuds---------------------------
    List<NoeudDelaunay> noeuds = new ArrayList<NoeudDelaunay>();
    // ---------------------Création des placeholders---------------------------
    for (int i = 0; i < this.getBuildingsIni().size(); i++) {
      // pour chaque bâtiment
      IBuilding obj = (IBuilding) this.getBuildingsIni().get(i);
      // récupère sa géométrie
      IDirectPosition dp = obj.getGeom().centroid();
      dp.setZ(0.0);
      IPoint geom = GeometryEngine.getFactory().createPoint(dp);
      // crée le noeud correspondant
      NoeudDelaunay noeud = new NoeudDelaunay(geom);
      // récupère l'identifiant du bâtiment
      noeud.setId(obj.getId());
      // noeud.setId(Integer.parseInt(obj.getAttribute("nom").toString()));
      noeuds.add(noeud);
      // crée le placeholder correspondant et affecte l'attribut symbo + block
      String symbolisation = (String) obj.getAttribute("symbolisation");
      IUrbanBlock block = (IUrbanBlock) obj.getAttribute("block");
      Placeholder ph;
      if (symbolisation != null && block != null) {
        ph = new Placeholder(obj, symbolisation, block);
      } else {
        ph = new Placeholder(obj);
      }
      // add to matching dico
      this.getMapPhDp().put(ph, dp);
    }

    // ---------------------Initialisation des options---------------------
    // c: construit l'enveloppe convexe (utile en cas de segments fermés)
    // z: numerotation de 0 a n-1 et pas de 1 a n
    // e: donne les edges
    // Q: quit
    String options = "czeBQ";

    // ----------Initialisation de la triangulation--------------------
    Triangulation tri = new Triangulation();
    tri.importAsNodes(noeuds);
    tri.setOptions(options);
    // Calcul de la trangulation
    try {
      tri.create();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // ************************************************************************
    // supprime les arcs qui intersectent un axe routier
    List<Arc> arcsToRemove = new ArrayList<Arc>();
    List<Noeud> noeudsToRemove = new ArrayList<Noeud>();
    // traitement par arc
    for (int i = 0; i < tri.getListeArcs().size(); i++) {
      Arc arc = tri.getListeArcs().get(i);
      // récupère les bâtiments correspondants aux noeuds de l'arc
      IBuilding b1 = this.getCorrespondingBuildings(arc.getNoeudIni()).get(0);
      IBuilding b2 = this.getCorrespondingBuildings(arc.getNoeudFin()).get(0);
      // vérifie que les bâtiments sont dans un ilot, sinon les élimine
      // (cas où ils sont à cheval sur la route et pas majoritairement dans un
      // ilot)
      if (b1.getBlock() == null) {
        b1.eliminate();
        noeudsToRemove.add(arc.getNoeudIni());
        arcsToRemove.add(arc);
        continue;
      }
      if (b2.getBlock() == null) {
        b2.eliminate();
        noeudsToRemove.add(arc.getNoeudFin());
        arcsToRemove.add(arc);
        continue;
      }
      // arc inter-ilots ou intra-ilot?
      if (b1.getBlock().equals(b2.getBlock())) {
        // intra-ilot : on teste l'intersection entre l'arc et les impasses
        // récupère les impasses de l'ilot
        HashSet<DeadEndGroup> insideRoads = b1.getBlock().getInsideDeadEnds();
        // vérifie qu'il y a des impasses dans l'ilot
        if (insideRoads != null) {
          Iterator<DeadEndGroup> itInsideRoads = insideRoads.iterator();
          // pour chaque impasse
          while (itInsideRoads.hasNext()) {
            boolean quit = false;
            // pour chaque partie de l'impasse
            HashSet<INetworkSection> currentDeadEnd = itInsideRoads.next()
                .getFeatures();
            Iterator<INetworkSection> itCurrentDeadEnd = currentDeadEnd
                .iterator();
            // teste l'intersection avec l'arc
            while (itCurrentDeadEnd.hasNext()) {
              INetworkSection currentPart = itCurrentDeadEnd.next();
              if (jtsAlgo.intersects(currentPart.getGeom(), arc.getGeom())) {
                // arc coupe la route : supprime l'arc
                arcsToRemove.add(arc);
                quit = true;
                break;
              }
            }
            if (quit == true)
              break;
          }
        }
      } else {
        // inter-ilots : on test l'intersection entre l'arc et les rues
        // entourant l'ilots
        IFeatureCollection<INetworkSection> surrondingRoads = new FT_FeatureCollection<INetworkSection>();
        surrondingRoads.addAll(b1.getBlock().getSurroundingNetwork());
        surrondingRoads.addAll(b2.getBlock().getSurroundingNetwork());
        for (int j = 0; j < surrondingRoads.size(); j++) {
          INetworkSection currentRoad = surrondingRoads.get(j);
          if (jtsAlgo.intersects((IGeometry) currentRoad.getGeom(),
              arc.getGeom())) {
            // arc coupe la route : supprime l'arc
            arcsToRemove.add(arc);
            break;
          }
        }
      }
      // si arc restant, on fixe son poids :
      double d = this.getDistanceWithDensity(b1, b2);
      arc.setPoids(d);
    }
    tri.getListeArcs().removeAll(arcsToRemove);
    tri.getListeNoeuds().removeAll(noeudsToRemove);
    // ************************************************************************
    return tri;
  }

  public ArrayList<IBuilding> getCorrespondingBuildings(Noeud noeud) {
    IDirectPosition dp = noeud.getCoord();
    Placeholder ph = this.getMapPhDp().getKey(dp);
    ArrayList<IBuilding> buildings = ph.getBuildings();
    return buildings;
  }

  public IGeometry getCorrespondingGeom(Noeud noeud) {
    IDirectPosition dp = noeud.getCoord();
    Placeholder ph = this.getMapPhDp().getKey(dp);
    return ph.getObj().getGeom();
  }

  public IDirectPosition calculBarycentre(ArrayList<IBuilding> buildingsList) {

    IDirectPosition barycentre;
    double moyenneX = 0;
    double moyenneY = 0;
    double sommeX = 0;
    double sommeY = 0;
    double sommeArea = 0;

    // calcul du barycentre
    // + pondération en fonction de l'importance du bâti (aire)
    for (int i = 0; i < buildingsList.size(); i++) {
      IPolygon bGeom = buildingsList.get(i).getGeom();
      sommeX = sommeX + bGeom.centroid().getX() * bGeom.area();
      sommeY = sommeY + bGeom.centroid().getY() * bGeom.area();
      sommeArea = sommeArea + bGeom.area();
    }
    moyenneX = sommeX / sommeArea;
    moyenneY = sommeY / sommeArea;
    barycentre = new DirectPosition(moyenneX, moyenneY);
    return barycentre;
  }

  public Triangulation typify() {
    double proxMin = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
        * (targetScale / 1000);
    Triangulation triFinale = this.createTriangulation();
    // this.afficheTriangulation(triFinale);
    // récupère la population des bâtiments
    IPopulation<IGeneObj> pop = this.buildingsIni;
    int newId = pop.size() + 1;
    // ---------itération jusqu'à atteindre le nb d'obj final----------
    double poidsMin = 0.0;
    while (pop.size() > this.getTargetNumberOfObjects() || poidsMin < proxMin) {
      // récupère l'arc le plus court
      Arc arc = this.getShortestEdge(triFinale.getListeArcs());
      poidsMin = arc.getPoids();
      // System.out.println(poidsMin);
      // récupérer les noeuds extrémités de l'arc
      Noeud noeud1 = arc.getNoeudIni();
      Noeud noeud2 = arc.getNoeudFin();
      // Récupère les PH correspondants aux noeuds
      Placeholder ph1 = this.getMapPhDp().getKey(noeud1.getCoord());
      Placeholder ph2 = this.getMapPhDp().getKey(noeud2.getCoord());
      // -------------------Traitement des bâtiments spéciaux ------------
      // s'ils sont saillants tous les deux : on ne typifie pas
      if (ph1.getSaliency() == true && ph2.getSaliency() == true
          && !ph1.getSymbolisation().equals(ph2.getSymbolisation())) {
        triFinale.enleveArc(arc);
        newId++;
        continue;
      }
      // récupérer les positions des objets correspondants
      ArrayList<IBuilding> correspondingObj = new ArrayList<IBuilding>();
      correspondingObj.addAll(ph1.getBuildings());
      correspondingObj.addAll(ph2.getBuildings());
      IDirectPosition barycentre = this.calculBarycentre(correspondingObj);
      // ajouter un nouveau noeud dans la triangulation
      NoeudDelaunay newNoeud = new NoeudDelaunay(barycentre);
      newNoeud.setId(newId);
      triFinale.addNoeud(newNoeud);
      // ----Mettre à jour les placeholder-------------------------
      // Crée le nouveau placeholder
      Placeholder ph = new Placeholder();
      // merge le contenu des placeholders précédents
      ph.mergePlaceholders(ph1, ph2);
      // ajouter le nouveau ph dans la bidimap
      this.getMapPhDp().put(ph, newNoeud.getCoord());
      // Retirer les anciens placeholders
      this.getMapPhDp().removeValue(noeud1.getCoord());
      this.getMapPhDp().removeValue(noeud2.getCoord());

      // créer le nouveau bâtiment
      IBuilding newBuilding = this.createNewGeometries(ph);
      // affecte les attributs symbo + block + antecedents
      newBuilding.setNature(ph.getSymbolisation());
      newBuilding.setBlock(ph1.getBlock());
      newBuilding.setAntecedents(new HashSet<IGeneObj>(ph.getBuildings()));
      // affectation de la nouvelle géométrie de ph
      ph.setObj(newBuilding);

      // ------actualisation de la population batiments---------
      // elimine les anciens bâtiments correspondants au ph
      for (IBuilding build : ph.getBuildings()) {
        build.eliminate();
      }
      // suppression des anciens ph
      pop.remove(ph1.getObj());
      pop.remove(ph2.getObj());
      // ajoute le nouveau bâti à la population
      pop.add(newBuilding);

      // connecte les arcs entrants des noeuds à supprimer au nouveau noeud
      List<Arc> arcsEntrants = new ArrayList<Arc>();
      arcsEntrants.addAll(noeud1.getEntrants());
      arcsEntrants.addAll(noeud2.getEntrants());
      for (int i = 0; i < arcsEntrants.size(); i++) {
        if (arcsEntrants.get(i).getId() != arc.getId()) {
          Arc arcCourant = arcsEntrants.get(i).copy();
          arcCourant.setId(arcsEntrants.get(i).getId());
          arcCourant.setNoeudFin(newNoeud);
          IGeometry arcGeom = arcCourant.getGeom();
          arcGeom.coord().set(1, newNoeud.getCoord());
          arcCourant.setGeom(arcGeom);
          // ---------calcule le nouveau poids de l'arc = distance entre les
          // deux objets------------
          // récupère l'obj (ph ou road) correspondant au noeud initial
          Placeholder phIni = this.getMapPhDp()
              .getKey(arcCourant.getNoeudIni().getCoord());
          IFeature objIni = phIni.getObj();
          // calcul de la distance pondérée par densité locale
          double d = this.getDistanceWithDensity(objIni, newBuilding);
          // affecte le poids à l'arc
          arcCourant.setPoids(d);
          triFinale.addArc(arcCourant);
        }
      }

      // connecte les arcs sortants des noeuds à supprimer au nouveau noeud
      List<Arc> arcsSortants = new ArrayList<Arc>();
      arcsSortants.addAll(noeud1.getSortants());
      arcsSortants.addAll(noeud2.getSortants());
      for (int i = 0; i < arcsSortants.size(); i++) {
        if (arcsSortants.get(i).getId() != arc.getId()) {
          Arc arcCourant = arcsSortants.get(i).copy();
          arcCourant.setId(arcsSortants.get(i).getId());
          arcCourant.setNoeudIni(newNoeud);
          IGeometry arcGeom = arcCourant.getGeom();
          arcGeom.coord().set(0, newNoeud.getCoord());
          arcCourant.setGeom(arcGeom);
          // ---------calcule le nouveau poids de l'arc = distance entre les
          // deux objets------------
          // récupère l'obj (ph ou road) correspondant au noeud final
          Placeholder phFin = this.getMapPhDp()
              .getKey(arcCourant.getNoeudFin().getCoord());
          IFeature objFin = phFin.getObj();
          // calcul de la distance pondérée par densité locale si option activée
          double d = this.getDistanceWithDensity(objFin, newBuilding);
          // affecte le poids à l'arc
          arcCourant.setPoids(d);
          triFinale.addArc(arcCourant);
        }
      }

      // màj des liens arcs<-noeuds
      IPopulation<Noeud> listNoeudsResult = triFinale.getPopNoeuds();
      for (int i = 0; i < listNoeudsResult.size(); i++) {
        Noeud noeudCourant = listNoeudsResult.get(i);
        noeudCourant.getEntrants().clear();
        noeudCourant.getSortants().clear();
      }

      IPopulation<Arc> listArcsResult = triFinale.getPopArcs();
      for (int i = 0; i < listArcsResult.size(); i++) {
        Arc arcCourant = listArcsResult.get(i);
        arcCourant.getNoeudIni().getSortants().add(arcCourant);
        arcCourant.getNoeudFin().getEntrants().add(arcCourant);
      }

      // nettoyage des arcs doublons
      triFinale.filtreArcsDoublons();

      // retirer les noeuds et arc initiaux de la triangulation
      triFinale.enleveNoeud(noeud1);
      triFinale.enleveNoeud(noeud2);
      triFinale.enleveArc(arc);
      newId++;
    }
    return triFinale;
  }

  public double getDistanceWithDensity(IFeature b1, IFeature b2) {
    // distance
    double d = 99999.0;
    // pour les bâtis
    if (b1 instanceof IBuilding && b1 instanceof IBuilding) {
      // récupère la densite simulée de l'ilot correspondant
      double densityFactor = ((IBuilding) b1).getBlock().getSimulatedDensity();
      d = Math.sqrt(densityFactor) * b1.getGeom().distance(b2.getGeom());
    } else {
      d = b1.getGeom().distance(b2.getGeom());
    }
    return d;
  }

  public Arc getShortestEdge(List<Arc> listArcs) {
    Arc minArc = new Arc();
    double minLength = 500;
    // récupère l'arc le plus court de la liste
    for (int i = 1; i < listArcs.size(); i++) {
      Arc arc = listArcs.get(i);
      // sinon, on prend le plus court
      if (arc.getPoids() < minLength) {
        minArc = arc;
        minLength = arc.getPoids();
      }
    }
    return minArc;
  }

  // Export from CartAGen classes to PostGIS tables

  public IBuilding createNewGeometries(Placeholder ph) {
    // --------créer la nouvelle géométrie-------------
    // ----aire moyenne
    Double averageArea = ph.getAverageArea();
    // ----facteur d'aire représentative : pour rester lisible
    // Double fArea = ((this.targetScale * this.targetScale) /
    // (this.initialScale * this.initialScale));

    // TODO prise en compte (upperScale = niveau connu supérieur)
    // Double upperScale = 100000.0;
    // Double aireMinIni = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
    // * this.initialScale * this.initialScale / 1000000.0;
    // Double aireMinUp = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
    // * upperScale * upperScale / 1000000.0;
    // Double phi = 1.0 - ((this.targetScale * this.targetScale) / (upperScale *
    // upperScale)); // *(/);
    // Double fArea = ((this.targetScale * this.targetScale) /
    // (this.initialScale * this.initialScale));
    // * (1.0 - phi* ((this.targetScale - this.initialScale) / (upperScale -
    // this.initialScale)));

    Double area = averageArea; // * fArea;
    // trouver l'objet le plus large
    IFeature largestObj = ph.getLargestBuilding();

    // calculer le MBR du largestObj
    IPolygon polySSR = SmallestSurroundingRectangleComputation
        .getSSR(largestObj.getGeom());
    // calculer longueur des deux côtés grand/petit
    Double long1 = polySSR.coord().get(0).distance(polySSR.coord().get(1));
    Double long2 = polySSR.coord().get(1).distance(polySSR.coord().get(2));
    // distinguer longueur = côté le plus long (de largeur)
    Double longueurIni = Math.max(long1, long2);
    Double largeurIni = Math.min(long1, long2);
    // ratio longueur / largeur
    Double ratioLonlar = longueurIni / largeurIni;
    // position
    IDirectPosition centroid = this.getMapPhDp().get(ph);
    // créer un bâtiment avec ces caractéristiques centré sur cette coordonnée
    // calculer les coordonnées
    Double longueur = Math.sqrt(ratioLonlar * area);
    Double largeur = area / longueur;
    Double xMin = centroid.getX() - longueur / 2;
    Double yMax = centroid.getY() + largeur / 2;
    IDirectPosition dp1 = new DirectPosition(xMin, yMax, 0.0);
    IPolygon poly = GeometryFactory.buildRectangle(dp1, longueur, largeur);
    // mesure de l'orientation du largest + rotation
    MesureOrientation mesOrientation = new MesureOrientation(
        largestObj.getGeom());
    Double orientation = mesOrientation.getOrientationGenerale();
    // orientation
    poly = CommonAlgorithms.rotation(poly, centroid, orientation);
    // vérifie que le bâtiment est assez gros
    double aireMinTerrain = ((GeneralisationSpecifications.BUILDING_MIN_AREA)
        * (targetScale / 1000) * (targetScale / 1000));
    if (poly.area() < aireMinTerrain) {
      // grossissement jusqu'à l'aire min
      poly = CommonAlgorithms.homothetie(poly,
          Math.sqrt(aireMinTerrain / poly.area()));
    }

    // créer un objet avec cette géométrie
    IBuilding newElement = this.factory.createBuilding(poly);
    return newElement;
  }

  public void generalize() {
    double aireMinTerrain = ((GeneralisationSpecifications.BUILDING_MIN_AREA)
        * (targetScale / 1000) * (targetScale / 1000));
    IPopulation<? extends IFeature> pop = this.getBuildingsIni();
    // suppression des bâtiments dont l'aire est inférieure au seuil
    for (int i = 1; i < pop.size(); i++) {
      IFeature build = pop.get(i);
      // vérifie que le batiment n'a pas été éliminé
      if (build.isDeleted() == false) {
        if (build.getGeom()
            .area() < GeneralisationSpecifications.BUILDING_ELIMINATION_AREA_THRESHOLD) {
          build.setDeleted(true);
        } else {
          if (build.getGeom().area() < aireMinTerrain) {
            // grossissement jusqu'à l'aire min
            IPolygon enlargeGeom = CommonAlgorithms.homothetie(
                (IPolygon) build.getGeom(),
                Math.sqrt(aireMinTerrain / build.getGeom().area()));
            build.setGeom(enlargeGeom);
          }
          // // suppression des angles négligeables
          // IGeometry poly = build.getGeom();
          // poly = SimplificationAlgorithm.simplification((IPolygon) poly, 2);
          // // simplification des bâtiments
          // SquarePolygonLS spLS = new SquarePolygonLS(10, 0.1, 7);
          // spLS.setPolygon((IPolygon) poly);
          // build.setGeom(spLS.square());
        }
      }
    }

  }

}
