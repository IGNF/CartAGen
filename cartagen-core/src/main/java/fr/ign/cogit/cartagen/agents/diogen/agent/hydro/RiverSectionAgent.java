package fr.ign.cogit.cartagen.agents.diogen.agent.hydro;

import java.util.ArrayList;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.IRiverSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.NetworkNodeAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroSectionAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartAComAgentDeformableGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.ICartacomMicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.diogen.state.CartacomMicroAgentState;
import fr.ign.cogit.cartagen.agents.diogen.state.CartacomMicroAgentStateImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELLinkedFeatureState;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

public class RiverSectionAgent
    extends fr.ign.cogit.cartagen.agents.cartacom.agent.impl.RiverSectionAgent
    implements IHydroSectionAgent, IRiverSectionAgent, ICartacomMicroAgent,
    ICartAComAgentDeformableGeneralisation {

  public RiverSectionAgent(NetworkAgent network, IWaterLine river) {
    super(river);
    agentAgent = new HydroSectionAgent(network, river);
    if (this.getAgentAgent() == null) {
      this.setAgentAgent(new HydroSectionAgent(network, river));
    }
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  @Override
  public IWaterLine getFeature() {
    return (IWaterLine) super.getFeature();
  }

  private IHydroSectionAgent agentAgent;

  @Override
  public NetworkAgent getNetwork() {
    return agentAgent.getNetwork();
  }

  @Override
  public NetworkNodeAgent getInitialNode() {
    return agentAgent.getInitialNode();
  }

  @Override
  public void setInitialNode(NetworkNodeAgent node) {
    agentAgent.setInitialNode(node);

  }

  @Override
  public NetworkNodeAgent getFinalNode() {
    return agentAgent.getFinalNode();
  }

  @Override
  public void setFinalNode(NetworkNodeAgent node) {
    agentAgent.setFinalNode(node);

  }

  @Override
  public CartacomMicroAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new CartacomMicroAgentStateImpl(this,
        (CartacomMicroAgentState) previousState, action);
  }

  @Override
  public void goBackToState(CartacomMicroAgentState state) {
    super.goBackToState(state);

  }

  @Override
  public void decompose() {
    agentAgent.decompose();

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
  public void registerDisplacement() {
    agentAgent.registerDisplacement();
  }

  @Override
  public double getTauxSuperpositionRoutier() {
    return agentAgent.getTauxSuperpositionRoutier();
  }

  @Override
  public void ajouterContrainteEcoulement(double imp) {
    agentAgent.ajouterContrainteEcoulement(imp);
  }

  @Override
  public void ajouterContrainteProximiteRoutier(double imp) {
    agentAgent.ajouterContrainteProximiteRoutier(imp);
  }

  @Override
  public void ajouterContraintesSubmicrosProximiteRoutier(double imp) {
    agentAgent.ajouterContraintesSubmicrosProximiteRoutier(imp);
  }

  @Override
  public double getSidesOrientation() {
    return agentAgent.getSidesOrientation();
  }

  @Override
  public void instantiateConstraints() {
    agentAgent.instantiateConstraints();
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
