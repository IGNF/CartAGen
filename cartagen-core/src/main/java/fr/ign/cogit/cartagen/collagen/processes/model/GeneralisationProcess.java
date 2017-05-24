package fr.ign.cogit.cartagen.collagen.processes.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenAgent;
import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.components.registry.ProcessCapabDescription;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.Chronometer;

public abstract class GeneralisationProcess implements CollaGenAgent {

  protected ProcessCapabDescription description;
  protected Conductor chefO;
  protected CopyOnWriteArrayList<IGeneObj> objetsTraites;
  private boolean pasAPas = false;
  protected Chronometer chrono;

  @Override
  public void computeSatisfaction() {
    // TODO Auto-generated method stub

  }

  public GeneralisationProcess(Conductor chefO) {
    this.chefO = chefO;
    objetsTraites = new CopyOnWriteArrayList<IGeneObj>();
    loadXMLDescription();
    this.chrono = new Chronometer();
  }

  public GeneralisationProcess(Conductor chefO, ProcessCapabDescription descr) {
    this.chefO = chefO;
    objetsTraites = new CopyOnWriteArrayList<IGeneObj>();
    this.description = descr;
  }

  @Override
  public int lifeCycle() throws InterruptedException {

    // le processus se décrit
    // TODO
    // le processus s'enregistre auprès du registre du chefO
    // TODO
    // le processus communique avec le chefO et attend d'être sollicité
    // TODO
    // le processus se paramètre en fonction des caract�ristiques de l'espace
    // TODO
    // le processus se lance
    // TODO
    // il communique à l'espace qu'il a terminé
    // TODO

    return 0;
  }

  protected abstract void updateEliminations();

  protected abstract void incrementStates();

  protected abstract void loadXMLDescription();

  protected abstract void triggerGeneralisation(GeographicSpace space);

  public ProcessCapabDescription getDescription() {
    return description;
  }

  public void setDescription(ProcessCapabDescription description) {
    this.description = description;
  }

  public Conductor getChefO() {
    return chefO;
  }

  public void setChefO(Conductor chefO) {
    this.chefO = chefO;
  }

  public CopyOnWriteArrayList<IGeneObj> getObjetsTraites() {
    return objetsTraites;
  }

  public void setObjetsTraites(CopyOnWriteArrayList<IGeneObj> objetsTraites) {
    this.objetsTraites = objetsTraites;
  }

  public Chronometer getChrono() {
    return chrono;
  }

  public void setChrono(Chronometer chrono) {
    this.chrono = chrono;
  }

  /**
   * Construit le bon processus Java � partir de sa description par
   * introspection.
   * @param descr
   * @param chefO
   * @return
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  public static GeneralisationProcess getInstance(
      ProcessCapabDescription descr, Conductor chefO)
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    if (descr == null)
      return null;
    Class<? extends GeneralisationProcess> classe = (Class<? extends GeneralisationProcess>) Class
        .forName(descr.getPath());
    Constructor<? extends GeneralisationProcess> constr = classe
        .getConstructor(Conductor.class, ProcessCapabDescription.class);

    return constr.newInstance(chefO, descr);
  }

  public void setPasAPas(boolean pasAPas) {
    this.pasAPas = pasAPas;
  }

  public boolean isPasAPas() {
    return pasAPas;
  }
}
