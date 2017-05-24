package fr.ign.cogit.cartagen.collagen.processes.implementation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph.MinimumSeparation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSCurvatureConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementDirConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSProximityConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler.MatrixSolver;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.MapspecsLS;

/**
 * Process similar to Push software (from Monika Sester and Hanover University)
 * that pushes overlapping network features using a least squares adjustment.
 * @author GTouya
 * 
 */
public class PushNetworksProcess extends GeneralisationProcess {

  private Set<MinimumSeparation> minSeps = new HashSet<MinimumSeparation>();
  private Map<IFeature, Double> symbolWidth = new HashMap<>();

  public PushNetworksProcess(Conductor chefO) {
    super(chefO);
  }

  public Set<MinimumSeparation> getMinSeps() {
    return minSeps;
  }

  public void setMinSeps(Set<MinimumSeparation> minSeps) {
    this.minSeps = minSeps;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void updateEliminations() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void incrementStates() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadXMLDescription() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void triggerGeneralisation(GeographicSpace space) {

    // build LSA specifications
    Set<String> contraintesMalleables = new HashSet<String>();
    contraintesMalleables.add(LSMovementConstraint.class.getName());
    contraintesMalleables.add(LSCurvatureConstraint.class.getName());
    contraintesMalleables.add(LSMovementDirConstraint.class.getName());
    Set<String> classesMalleables = new HashSet<String>();
    Map<String[], Double> contraintesExternes = new HashMap<String[], Double>();

    for (MinimumSeparation minSep : this.minSeps) {
      classesMalleables.add(minSep.getClass1().getName());
      classesMalleables.add(minSep.getClass2().getName());
      contraintesExternes.put(
          new String[] { LSProximityConstraint.class.getName(),
              minSep.getClass1().getName(), minSep.getClass2().getName() },
          minSep.getMinSep());
    }
    Map<String, Double> poidsContraintes = new HashMap<String, Double>();
    poidsContraintes.put(LSMovementConstraint.class.getName(), 1.0);
    poidsContraintes.put(LSCurvatureConstraint.class.getName(), 6.0);
    poidsContraintes.put(LSMovementDirConstraint.class.getName(), 4.0);
    poidsContraintes.put(LSProximityConstraint.class.getName(), 15.0);

    MapspecsLS mapspecs = new MapspecsLS(Legend.getSYMBOLISATI0N_SCALE(), null,
        new HashSet<String>(), new HashSet<String>(), contraintesMalleables,
        contraintesExternes, new HashSet<String>(), new HashSet<String>(),
        classesMalleables, poidsContraintes);

    // puis on construit un scheduler
    LSScheduler sched = new LSScheduler(mapspecs);
    mapspecs.addSelectedObjects(space.getInsideFeatures());
    mapspecs.setMapSymbolWidth(symbolWidth);
    sched.setSolver(MatrixSolver.JAMA);
    // on lance la généralisation
    sched.triggerAdjustment(false, true);
  }

  /**
   * Shortcut to run the process on a given {@link GeographicSpace} instance.
   * @param space
   */
  public void runOnGeoSpace(GeographicSpace space) {
    triggerGeneralisation(space);
  }

  public void addMinimumSeparation(MinimumSeparation minSep) {
    this.minSeps.add(minSep);
  }

  public Map<IFeature, Double> getSymbolWidth() {
    return symbolWidth;
  }

  public void setSymbolWidth(Map<IFeature, Double> symbolWidth) {
    this.symbolWidth = symbolWidth;
  }
}
