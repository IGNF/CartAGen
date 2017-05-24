package fr.ign.cogit.cartagen.algorithms.typification;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

public class Placeholder {

  // *****************************************
  // PROPERTIES
  // *****************************************
  private ArrayList<IBuilding> buildings = new ArrayList<IBuilding>();
  private IBuilding obj;
  private String symbolisation = null;
  private IUrbanBlock block = null;
  private Boolean saliency = false;

  // *****************************************
  // CONSTRUCTOR
  // *****************************************
  public Placeholder() {
  }

  public Placeholder(IBuilding obj) {
    this.buildings.add(obj);
    this.obj = obj;
  }

  public Placeholder(IBuilding obj, String symbolisation, IUrbanBlock block) {
    this.buildings.add(obj);
    this.obj = obj;
    this.symbolisation = symbolisation;
    this.block = block;
    this.computeSaliency();
  }

  public Placeholder(ArrayList<IBuilding> objList) {
    this.buildings = objList;

  }

  // *****************************************
  // GETTERS
  // *****************************************
  public ArrayList<IBuilding> getBuildings() {
    return buildings;
  }

  public IFeature getObj() {
    return obj;
  }

  public String getSymbolisation() {
    return symbolisation;
  }

  public IUrbanBlock getBlock() {
    return block;
  }

  public Boolean getSaliency() {
    return this.saliency;
  }

  // *****************************************
  // SETTERS
  // *****************************************

  public void setBuildings(ArrayList<IBuilding> buildings) {
    this.buildings = buildings;
  }

  public void setObj(IBuilding obj) {
    this.obj = obj;
  }

  public void setSymbolisation(String symbolisation) {
    this.symbolisation = symbolisation;
  }

  public void setSaliency(Boolean saliency) {
    this.saliency = saliency;
  }

  public void setBlock(IUrbanBlock block) {
    this.block = block;
  }

  // *****************************************
  // METHODS
  // *****************************************

  public void mergePlaceholders(Placeholder ph1, Placeholder ph2) {
    // pour chaque obj de la liste, on ajoute les infos dans les listes
    this.buildings.addAll(ph1.getBuildings());
    this.buildings.addAll(ph2.getBuildings());
    this.setBlock(ph1.getBlock());
    // manage saliency & symbolisation
    // cas par défaut : ph1 plus saillant ou les deux de même saillance
    this.setSymbolisation(ph1.getSymbolisation());
    this.setSaliency(ph1.getSaliency());
    // autre cas : ph2 plus saillant
    if (ph2.getSaliency() == true) {
      this.setSymbolisation(ph2.getSymbolisation());
      this.setSaliency(ph2.getSaliency());
    }
    // PI: cas où les deux sont saillants déjà écartés
  }

  public IDirectPositionList getBuildingsPosition() {
    IDirectPositionList dpList = new DirectPositionList();
    for (int i = 0; i < this.buildings.size(); i++) {
      dpList.add(buildings.get(i).getGeom().centroid());
    }
    return dpList;
  }

  public Double getAverageArea() {
    Double avg = 0.0;
    for (int i = 0; i < this.buildings.size(); i++) {
      avg = avg + this.buildings.get(i).getGeom().area();
    }
    Double avgResult = avg / this.buildings.size();
    return avgResult;
  }

  public IFeature getLargestBuilding() {
    IFeature largestObj = this.buildings.get(0);
    for (int i = 0; i < this.buildings.size(); i++) {
      IFeature currentObj = this.buildings.get(i);
      if (currentObj.getGeom().area() > largestObj.getGeom().area()) {
        largestObj = currentObj;
      }
    }
    return largestObj;
  }

  public void computeSaliency() {
    // -----------------------Saillance par la symbo--------------------------
    // définition des valeurs spéciales
    List<String> valSpeciales = new ArrayList<String>();
    valSpeciales.add("Bâtiment commercial");
    valSpeciales.add("Bâtiment industriel");
    valSpeciales.add("Bâtiment sportif");
    valSpeciales.add("Bâtiment public");
    valSpeciales.add("Mairie");
    valSpeciales.add("Hangar");
    valSpeciales.add("Hangar commercial");
    valSpeciales.add("Hangar industriel");
    valSpeciales.add("Hangar public");
    valSpeciales.add("Bâtiment remarquable");
    valSpeciales.add("Bâtiment remarquable dense");
    valSpeciales.add("Gare");
    // valSpeciales.add("Bâtiment quelconque");
    // valSpeciales.add("Bâtiment quelconque dense");
    valSpeciales.add("Bâti chrétien plus 50 m");
    valSpeciales.add("Synagogue");
    valSpeciales.add("Mosquée");
    valSpeciales.add("Autre culte");
    valSpeciales.add("Bâti chrétien plus 50 m");
    valSpeciales.add("Arène ou théâtre antique");
    valSpeciales.add("Fort, blockhaus, casemate");
    valSpeciales.add("Château");
    valSpeciales.add("Monument");
    valSpeciales.add("Serre");
    valSpeciales.add("Silo");
    valSpeciales.add("Tour, donjon, moulin");
    valSpeciales.add("Arc de triomphe");
    // test si symbo speciale
    if (valSpeciales.contains(this.getSymbolisation()))
      this.saliency = true;
    // -----------------------Saillance par la taille--------------------------
    if (this.getObj().getGeom()
        .area() >= GeneralisationSpecifications.GRANDS_BATIMENTS_AIRE)
      this.saliency = true;
  }
}
