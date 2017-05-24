package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.network.deadendzoning.DeadEndZone;
import fr.ign.cogit.cartagen.spatialanalysis.network.deadendzoning.DeadEndZoning;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

public class BuildingDeadEndRelPositionRelation extends CollaGenRelation {

  private int zoneType;
  private DeadEndZoning zoning;

  /**
   * Constructeur � partir des composants de la relation. Construit la relation
   * Gothic en plus de la relation Java.
   * @param obj1
   * @param obj2
   * @param vac
   */
  public BuildingDeadEndRelPositionRelation(IGeneObj bati, IGeneObj route,
      GeographicRelation concept) {
    super(bati, route, concept);
    computeGeom();
    calculerTypeZone();
  }

  private void calculerZonage() {
    // on récupère la face réseau dans laquelle se trouve le bâtiment
    IPolygon networkFace = (IPolygon) CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.NETWORK_FACES_POP)
        .select(obj1.getGeom()).iterator().next().getGeom();
    zoning = new DeadEndZoning(networkFace, (INetworkSection) obj2, 60.0);
    zoning.buildDeadEndZoning();
  }

  private void calculerTypeZone() {
    calculerZonage();

    // on calcule le type de zone du bâtiment
    this.zoneType = zoning.computeZoneTypeIndex((IPolygon) obj1.getGeom());
  }

  public static boolean estValide(IBuilding bati, IRoadLine route) {
    if (!route.isDeadEnd())
      return false;
    if (route.getNetworkSectionType() != NetworkSectionType.DIRECT_DEAD_END)
      return false;
    // on teste maintenant si des bâtiments sont intercalés entre bati et route
    GeometryProximity proxi = new GeometryProximity(bati.getGeom(),
        route.getGeom());
    // on ne fait ce test qui si la distance est grande
    if (proxi.getDistance() > 40.0) {
      if (CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(CartAGenDataSet.BUILDINGS_POP)
          .select(proxi.toSegment()).size() >= 2)
        return false;
    }
    return true;
  }

  @Override
  public int qualiteRelation() {
    // TODO Auto-generated method stub
    return 0;
  }

  public void setZoneType(int zoneType) {
    this.zoneType = zoneType;
  }

  public int getZoneType() {
    return zoneType;
  }

  public void computeGeom() {
    GeometryProximity proxi = new GeometryProximity(obj1.getGeom(),
        obj2.getGeom());
    geom = proxi.toSegment();
  }

  /**
   * Construit la géométrie fusionn�e des zones interdites dans la relation de
   * positionnement relatif, en fonction d'un positionnement but passé en
   * entrée.
   * @param positionIndex
   * @return
   */
  public IGeometry getZoneInterdite(int positionIndex) {
    // si la valeur est nulle, on renvoie une géométrie vide
    if (positionIndex == 0)
      return new GM_Polygon();

    // convertit la valeur en liste de chiffres
    ArrayList<Integer> chiffres = valeurToListe(positionIndex);

    // on fait une liste des géométries des zones interdites
    List<IGeometry> listAreas = new ArrayList<IGeometry>();
    int indiceZone = 1;
    while (indiceZone <= 100000) {
      // si la liste des chiffres est vide c'est fini
      if (chiffres.size() == 0)
        break;
      // on récupère le dernier chiffre de la liste
      int chiffre = chiffres.get(chiffres.size() - 1);
      // on enlève le dernier de la liste
      chiffres.remove(chiffres.size() - 1);
      if (chiffre == 1) {
        // on teste s'il existe bien une zone correspondant à cet indice
        DeadEndZone zone = this.zoning.getZone(indiceZone);
        if (zone != null) {
          // on l'ajoute à la liste
          listAreas.add(zone.getGeom());
        }
      }
      indiceZone *= 10;
    }

    // on fusionne les géométries de la liste
    IGeometry geom = CommonAlgorithmsFromCartAGen.geomColnUnion(listAreas);
    return geom;
  }

  /**
   * Pour un entier, renvoie une liste de ses chiffres, dans l'ordre où ils
   * s'ecrivent (i.e. le dernier entier de la liste = le chiffre des unites du
   * nombre). Nombres negatifs: renvoie une liste vide.
   * 
   * @param valeur
   * @return
   */
  public static ArrayList<Integer> valeurToListe(int valeur) {
    ArrayList<Integer> liste = new ArrayList<Integer>();
    if (valeur < 0)
      return liste;

    // pour 0, renvoie une liste ne contenant que 0
    if (valeur == 0) {
      liste.add(0);
      return liste;
    }
    Integer valTemp = new Integer(valeur);
    long puiss10 = Math.round(Math.log10(valTemp.doubleValue()));
    // boucle sur le nombre de chiffres
    while (puiss10 >= 0) {
      // récupère le chiffre courant
      int chiffreCourant = (int) Math
          .round(valTemp.intValue() / Math.pow(10.0, puiss10));
      // on l'ajoute à la liste
      liste.add(new Integer(chiffreCourant));
      // on modifie la valeur temporaire
      valTemp = new Double(
          valTemp.intValue() - chiffreCourant * Math.pow(10, puiss10))
              .intValue();
      // on diminue la puissance
      puiss10--;
    }
    return liste;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

}
