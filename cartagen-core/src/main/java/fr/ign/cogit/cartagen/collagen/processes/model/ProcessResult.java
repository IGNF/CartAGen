package fr.ign.cogit.cartagen.collagen.processes.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import fr.ign.cogit.cartagen.collagen.components.registry.ProcessCapabDescription;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;

/**
 * Cette classe permet de stocker l'évaluation des résultats d'un traitement sur
 * des espaces géographiques d'un type donné. Ces résultats sont utilisés pour
 * pondérer les requêtes du registre de processus de généralisation.
 * 
 * @author GTouya
 * 
 */
public class ProcessResult {

  private ProcessCapabDescription service;
  private List<GeographicSpace> espaces;
  private DescriptiveStatistics evaluations;

  public void ajouterEval(GeographicSpace e, int eval) {
    this.espaces.add(e);
    this.evaluations.addValue(new Integer(eval));
  }

  public double evaluationMoyenne() {
    return this.evaluations.getMean();
  }

  public ProcessCapabDescription getService() {
    return this.service;
  }

  public void setService(ProcessCapabDescription service) {
    this.service = service;
  }

  public List<GeographicSpace> getEspaces() {
    return this.espaces;
  }

  public void setEspaces(List<GeographicSpace> espaces) {
    this.espaces = espaces;
  }

  public List<Integer> getEvaluationsList() {
    List<Integer> list = new ArrayList<Integer>();
    for (double eval : this.evaluations.getValues()) {
      list.add(new Double(eval).intValue());
    }
    return list;
  }

  // TODO ajouter des évaluations en fonction du type d'espace (concept et/ou
  // caracs)

  public double evaluationMoyenne(
      @SuppressWarnings("unused") GeographicSpace espace) {
    // TODO
    return 0.0;
  }
}
