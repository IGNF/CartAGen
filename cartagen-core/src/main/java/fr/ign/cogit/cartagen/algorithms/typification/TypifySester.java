package fr.ign.cogit.cartagen.algorithms.typification;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

public class TypifySester {

  // *****************************************
  // PROPERTIES
  // *****************************************
  private double echelleInitiale;
  private double echelleFinale;
  private double nbObjetsEchIni;
  private double nbObjetsEchFin;
  private double constExageration;
  private double constForm;
  private IPopulation<? extends IFeature> population;

  // *****************************************
  // CONSTRUCTOR
  // *****************************************
  public TypifySester(IPopulation<? extends IFeature> population,
      double echelleInitiale, double echelleFinale, double constExageration,
      double constForm) {
    // set initial parameters
    this.echelleFinale = echelleFinale;
    this.echelleInitiale = echelleInitiale;
    this.constExageration = constExageration;
    this.constForm = constForm;
    this.population = population;

    // calculate the initial and final number of objects
    this.nbObjetsEchIni = this.population.getElements().size();
    this.nbObjetsEchFin = this.radicalLaw();

    // launch the random selection
    this.randomSelection();
  }

  // ***************
  // GETTERS
  // ***************
  public double getEchelleInitiale() {
    return this.echelleInitiale;
  }

  public double getEchelleFinale() {
    return this.echelleFinale;
  }

  public double getNbObjetsEchIni() {
    return this.nbObjetsEchIni;
  }

  public double getNbObjetsEchFin() {
    return this.nbObjetsEchFin;
  }

  public double getConstExageration() {
    return this.constExageration;
  }

  public double getConstForm() {
    return this.constForm;
  }

  public IPopulation<? extends IFeature> getPopulation() {
    return this.population;
  }

  // ***************
  // SETTERS
  // ***************

  // ***************
  // OTHER METHODS
  // ***************

  // Calcule le nombre d'objets nécessaires à l'échelle finale, selon Töpfer &
  // Pillewizer 1966
  private double radicalLaw() {
    double nbObjetsEchFin = Math
        .round(this.nbObjetsEchIni * this.constExageration * this.constForm
            * Math.sqrt(this.echelleInitiale / this.echelleFinale));
    System.out.println("radical law : " + nbObjetsEchFin);
    return nbObjetsEchFin;
  }

  // Sélectionne aléatoirement n objets (radicalLaw) dans la population
  private void randomSelection() {
    // cherche n index aléatoires
    ArrayList<Integer> indexRandom = new ArrayList<Integer>();
    while (indexRandom.size() != this.nbObjetsEchFin) {
      // calcule un nombre aléatoire entre 0 et le nombre d'objets initial -1
      // (car population.get(i) commence à 0)
      int j = (int) Math.round(Math.random() * (this.nbObjetsEchIni - 1));
      if (indexRandom.contains(j) == false) {
        indexRandom.add(j);
      }
    }
    System.out.println("indexRandom : " + indexRandom.size());
    // on va sélectionner les buildings
    // pour chaque index, sélectionne l'objet
    IPopulation<IBuilding> buildingsSelected = new Population<IBuilding>();
    for (int i = 0; i < indexRandom.size(); i++) {
      System.out.println("i : " + i);
      System.out.println("index : " + indexRandom.get(i));
      buildingsSelected
          .add((IBuilding) this.population.get(indexRandom.get(i)));
    }

    // TODO Self-Organized Map: use the old buildings as stimuli to attract the
    // new ones and readjust the dataset distribution
    // Load the new layer
    CartAGenDoc doc = CartAGenDoc.getInstance();
    CartAGenDataSet dataset = doc.getCurrentDataset();
    buildingsSelected.setNom("BuildingsSelected");
    dataset.addPopulation(buildingsSelected);
    // Set FT
    FeatureType ftGeom = new FeatureType();
    ftGeom.setGeometryType(IPolygon.class);
    dataset.getPopulation("BuildingsSelected").setFeatureType(ftGeom);

  }
}
