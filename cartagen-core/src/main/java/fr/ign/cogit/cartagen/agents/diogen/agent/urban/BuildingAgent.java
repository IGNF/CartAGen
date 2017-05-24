package fr.ign.cogit.cartagen.agents.diogen.agent.urban;

import java.util.ArrayList;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.state.SmallCompactAgentState;
import fr.ign.cogit.cartagen.agents.core.agent.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ISmallCompactMicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.diogen.state.CartacomMicroAgentState;
import fr.ign.cogit.cartagen.agents.diogen.state.SmallCompactMicroAgentState;
import fr.ign.cogit.cartagen.agents.diogen.state.SmallCompactMicroAgentStateImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELLinkedFeatureState;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseParcelAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

public class BuildingAgent
    extends fr.ign.cogit.cartagen.agents.cartacom.agent.impl.BuildingAgent
    implements IBuildingAgent,
    fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IBuildingAgent,
    ISmallCompactMicroAgent {

  public BuildingAgent(IBuilding bat) {
    super(bat);
    agentAgent = new fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent(
        bat, 1);
  }

  public BuildingAgent(IBuilding bat, int importance) {
    super(bat);
    agentAgent = new fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent(
        bat, importance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.AgentImpl#computeSatisfaction()
   */
  @Override
  public void computeSatisfaction() {
    // the mean of the constraints satisfactions, weighted by the importance

    int n = this.getConstraints().size();

    // if the agent has no constraint or is deleted, so it is perfectly
    // satisfied
    if (n == 0 || this instanceof GeographicObjectAgent
        && ((GeographicObjectAgent) this).getFeature().isDeleted()) {
      this.setSatisfaction(100.0);
      return;
    }

    double sum = 0.0;
    double impSum = 0.0;
    for (Constraint cont : this.getConstraints()) {
      GeographicConstraint cont_ = (GeographicConstraint) cont;
      cont_.computeSatisfaction();
      // System.out.println(cont_ + " satisfaction: " +
      // cont_.getSatisfaction());
      double satisfaction = cont_.getSatisfaction();
      if (cont_ instanceof RelationalConstraint) {
        satisfaction *= 20;
      }
      sum += cont_.getImportance() * satisfaction;
      impSum += cont_.getImportance();
    }

    if (impSum == 0) {
      this.setSatisfaction(100.0);
    } else {
      this.setSatisfaction(sum / impSum);
    }
  }

  @Override
  public IBuilding getFeature() {
    return (IBuilding) super.getFeature();
  }

  private IBuildingAgent agentAgent;

  @Override
  public ArrayList<UrbanAlignmentAgent> getAlignments() {
    return agentAgent.getAlignments();
  }

  @Override
  public void setAlignments(ArrayList<UrbanAlignmentAgent> alignments) {
    agentAgent.setAlignments(alignments);
  }

  @Override
  public void addAlignment(UrbanAlignmentAgent alignment) {
    agentAgent.addAlignment(alignment);
  }

  @Override
  public double getGoalArea() {
    return agentAgent.getGoalArea();
  }

  @Override
  public IGeometry getOverlappingGeometry() {
    return agentAgent.getOverlappingGeometry();
  }

  @Override
  public double getOverlappingRate() {
    return agentAgent.getOverlappingRate();
  }

  @Override
  public IGeometry getOverlappingGeometryInAlignment(
      UrbanAlignmentAgent align) {
    return agentAgent.getOverlappingGeometryInAlignment(align);
  }

  @Override
  public double getOverlappingRateInAlignment(UrbanAlignmentAgent align) {
    return agentAgent.getOverlappingRateInAlignment(align);
  }

  @Override
  public IGeometry getOverlappingGeometryBetweenUrbanElements() {
    return agentAgent.getOverlappingGeometryBetweenUrbanElements();
  }

  @Override
  public double getOverlappingRateBetweenBuildings() {
    return agentAgent.getOverlappingRateBetweenBuildings();
  }

  @Override
  public boolean isOverlappingMeso() {
    return agentAgent.isOverlappingMeso();
  }

  @Override
  public IGeometry getDirectOverlapGeometryBetweenUrbanElements() {
    return agentAgent.getDirectOverlapGeometryBetweenUrbanElements();
  }

  @Override
  public double getDirectOverlapRateBetweenUrbanElements() {
    return agentAgent.getDirectOverlapRateBetweenUrbanElements();
  }

  @Override
  public double getSidesOrientation() {
    return agentAgent.getSidesOrientation();
  }

  @Override
  public IDirectPosition getPosition() {
    return agentAgent.getPosition();
  }

  @Override
  public double getElevation(ReliefFieldAgent reliefField) {
    return agentAgent.getElevation(reliefField);
  }

  @Override
  public double getSlopeVectorOrientation(ReliefFieldAgent reliefField) {
    return agentAgent.getSlopeVectorOrientation(reliefField);
  }

  @Override
  public LandUseParcelAgent getLandUseParcel() {
    return agentAgent.getLandUseParcel();
  }

  @Override
  public double getSmallestSideLength() {
    return agentAgent.getSmallestSideLength();
  }

  @Override
  public SmallCompactMicroAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new SmallCompactMicroAgentStateImpl(this,
        (SmallCompactMicroAgentState) previousState, action);
  }

  @Override
  public void instantiateConstraints() {
    agentAgent.instantiateConstraints();
  }

  @Override
  public double getGeneralOrientation() {
    return agentAgent.getGeneralOrientation();
  }

  @Override
  public double getInitialGeneralOrientation() {
    return agentAgent.getInitialGeneralOrientation();
  }

  @Override
  public void computeInitialGeneralOrientation() {
    agentAgent.computeInitialGeneralOrientation();

  }

  @Override
  public double getInitialSidesOrientation() {
    return agentAgent.getInitialSidesOrientation();
  }

  @Override
  public void computeInitialSidesOrientation() {
    agentAgent.computeInitialSidesOrientation();

  }

  @Override
  public double getSidesOrientationIndicator() {
    return agentAgent.getSidesOrientationIndicator();
  }

  @Override
  public double getConvexity() {
    return agentAgent.getConvexity();
  }

  @Override
  public double getInitialConvexity() {
    return agentAgent.getInitialConvexity();
  }

  @Override
  public void computeInitialConvexity() {
    agentAgent.computeInitialConvexity();

  }

  @Override
  public double getElongation() {
    return agentAgent.getElongation();
  }

  @Override
  public double getInitialElongation() {
    return agentAgent.getInitialElongation();
  }

  @Override
  public void computeInitialElongation() {
    agentAgent.computeInitialElongation();

  }

  @Override
  public double getGeneralOrientationDegree() {
    return agentAgent.getGeneralOrientationDegree();
  }

  @Override
  public double getSidesOrientationDegree() {
    return agentAgent.getSidesOrientationDegree();
  }

  @Override
  public IPointAgent getAgentPointReferant() {
    return agentAgent.getAgentPointReferant();
  }

  @Override
  public ArrayList<GAELSegment> getSegmentsProximite() {
    return agentAgent.getSegmentsProximite();
  }

  @Override
  public void goBackToState(GAELLinkedFeatureState linkedFeatureState) {
    agentAgent.goBackToState(linkedFeatureState);

  }

  @Override
  public void setAgentPointReferant(IPointAgent agentPointReferant) {
    agentAgent.setAgentPointReferant(agentPointReferant);

  }

  @Override
  public IGeometry getSymbolExtent() {
    return agentAgent.getSymbolExtent();
  }

  @Override
  public IGeometry getUsedSymbolExtent() {
    return agentAgent.getUsedSymbolExtent();
  }

  @Override
  public IFeatureCollection<IPointAgent> getPointAgents() {
    return agentAgent.getPointAgents();
  }

  @Override
  public IFeatureCollection<GAELPointSingleton> getPointSingletons() {
    return agentAgent.getPointSingletons();
  }

  @Override
  public IFeatureCollection<GAELSegment> getSegments() {
    return agentAgent.getSegments();
  }

  @Override
  public IFeatureCollection<GAELTriangle> getTriangles() {
    return agentAgent.getTriangles();
  }

  @Override
  public IFeatureCollection<GAELAngle> getAngles() {
    return agentAgent.getAngles();
  }

  @Override
  public ArrayList<IPointAgent> getListeAgentsPoints() {
    return agentAgent.getListeAgentsPoints();
  }

  @Override
  public void cleanDecomposition() {
    agentAgent.cleanDecomposition();

  }

  @Override
  public void activatePointAgents() throws InterruptedException {
    agentAgent.activatePointAgents();
  }

  @Override
  public void activatePointAgents(int nbLimite) throws InterruptedException {
    agentAgent.activatePointAgents(nbLimite);

  }

  @Override
  public IPointAgent getPlusInsatisfait() {
    return agentAgent.getPlusInsatisfait();
  }

  @Override
  public void chargerPointsNonEquilibres() {
    agentAgent.chargerPointsNonEquilibres();

  }

  @Override
  public void ajouterContPointPosition(double importance) {
    agentAgent.ajouterContPointPosition(importance);
  }

  @Override
  public void ajouterContPointGarderAltitude(double importance) {
    agentAgent.ajouterContPointGarderAltitude(importance);
  }

  @Override
  public void ajouterContSegmentLongueur(double importance) {
    agentAgent.ajouterContSegmentLongueur(importance);
  }

  @Override
  public void ajouterContSegmentLongueur(double importance,
      double longueurBut) {
    agentAgent.ajouterContSegmentLongueur(importance, longueurBut);

  }

  @Override
  public void ajouterContSegmentOrientation(double importance) {
    agentAgent.ajouterContSegmentOrientation(importance);
  }

  @Override
  public void ajouterContSegmentOrientation(double importance,
      double orientationBut) {
    agentAgent.ajouterContSegmentOrientation(importance, orientationBut);
  }

  @Override
  public void ajouterContSegmentCouler(double importance) {
    agentAgent.ajouterContSegmentCouler(importance);
  }

  @Override
  public void ajouterContSegmentEtrePlat(double importance) {
    agentAgent.ajouterContSegmentEtrePlat(importance);
  }

  @Override
  public void ajouterContAngleValeur(double importance, double valeurBut) {
    agentAgent.ajouterContAngleValeur(importance, valeurBut);
  }

  @Override
  public void ajouterContAngleValeur(double importance) {
    agentAgent.ajouterContAngleValeur(importance);
  }

  @Override
  public void ajouterContTriangleAire(double importance) {
    agentAgent.ajouterContTriangleAire(importance);
  }

  @Override
  public void ajouterContTriangleGarderG(double importance) {
    agentAgent.ajouterContTriangleGarderG(importance);
  }

  @Override
  public void ajouterContTriangleGarderOrientationAzimutalePente(
      double importance) {
    agentAgent.ajouterContTriangleGarderOrientationAzimutalePente(importance);

  }

  @Override
  public void ajouterContTriangleFaireCouler(double importance) {
    agentAgent.ajouterContTriangleFaireCouler(importance);

  }

  @Override
  public void ajouterContTriangleFaireGarderAltitude(double importance) {
    agentAgent.ajouterContTriangleFaireGarderAltitude(importance);

  }

  @Override
  public void supprimerContraintesSubmicro() {
    agentAgent.supprimerContraintesSubmicro();

  }

  @Override
  public GAELTriangle getTriangle(IDirectPosition pos) {
    return agentAgent.getTriangle(pos);
  }

  @Override
  public GAELSegment getSegment(IPointAgent p1, IPointAgent p2) {
    return agentAgent.getSegment(p1, p2);
  }

  @Override
  public GAELTriangle getTriangleInitial(IDirectPosition pos) {
    return agentAgent.getTriangleInitial(pos);
  }

  @Override
  public void creerPointsSingletonsAPartirDesPoints() {
    agentAgent.creerPointsSingletonsAPartirDesPoints();

  }

  @Override
  public void creerAnglesTriangles() {
    agentAgent.creerAnglesTriangles();

  }

  @Override
  public void triangule(boolean construireTriangles, IGeometry geom) {
    agentAgent.triangule(construireTriangles, geom);

  }

  @Override
  public void triangule() {
    agentAgent.triangule();

  }

  @Override
  public IPointAgent getPoint(double x, double y) {
    return agentAgent.getPoint(x, y);
  }

  @Override
  public void construireSegments(boolean ferme) {
    agentAgent.construireSegments(ferme);

  }

  @Override
  public void goBackToState(SmallCompactMicroAgentState state) {
    super.goBackToState(state);
  }

  @Override
  public void goBackToState(SmallCompactAgentState state) {
    super.goBackToState(state);
  }

  @Override
  public void goBackToState(CartacomMicroAgentState state) {
    super.goBackToState(state);
  }

  @Override
  public void registerDisplacement() {
    agentAgent.registerDisplacement();

  }

  @Override
  public Set<Environment> getBorderedEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderedEnvironments(Set<Environment> borderedEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public BorderStrategy getBorderStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderStrategy(BorderStrategy borderStrategy) {
    // TODO Auto-generated method stub

  }

  @Override
  public AgentSatisfactionState activate(Environment environment)
      throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Environment getEncapsulatedEnv() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEncapsulatedEnv(Environment encapsulatedEnv) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<Environment> getContainingEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setContainingEnvironments(
      Set<Environment> containingEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public EnvironmentStrategy getEnvironmentStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEnvironmentStrategy(EnvironmentStrategy environmentStrategy) {
    // TODO Auto-generated method stub

  }

}
