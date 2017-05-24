package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.ISubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.diogen.schema.GeneObjPointPatch;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELLinkableFeature;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentState;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELPointGeneObj;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ContourLineAgent;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.cartagen.graph.IGraphLinkableFeature;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

public class GeographicPointAgent extends CartAComAgentGeneralisation
    implements IGeographicPointAgent {

  private IPointAgent pointAgent;

  private GAELPointGeneObj submicroPoint;

  // public GeographicPointAgent(IGeneObj feature, GAELDeformable agentGeo,
  // IDirectPosition dp) {
  // super(feature);
  //
  // this.pointAgent = new PointAgentImpl(agentGeo, dp);
  // agentGeo.getPointAgents().add(this);
  // }
  //
  // public GeographicPointAgent(IGeneObj feature, IDirectPosition dp) {
  // super(feature);
  // this.pointAgent = new PointAgentImpl(dp);
  // }
  //
  // public GeographicPointAgent(IGeneObj feature, IPointAgent pointAgent) {
  // super(feature);
  // this.pointAgent = pointAgent;
  // }

  public GeographicPointAgent(GAELDeformable def, IDirectPosition dp) {
    super(new GeneObjPointPatch(dp.toGM_Point()));
    this.pointAgent = new PointAgentImpl(def, dp);
    def.getPointAgents().remove(this.pointAgent);
    def.getPointAgents().add(this);
    this.submicroPoint = new GAELPointGeneObj(def, this);
    this.setFeature(this.submicroPoint);
  }

  public GeographicPointAgent(IDirectPosition dp) {
    super(new GeneObjPointPatch(dp.toGM_Point()));
    this.pointAgent = new PointAgentImpl(dp);
  }

  public GeographicPointAgent(IPointAgent pointAgent) {
    super(new GeneObjPointPatch(pointAgent.getPosition().toGM_Point()));
    this.pointAgent = pointAgent;
    this.submicroPoint = new GAELPointGeneObj(pointAgent.getFieldAgent(), this);
    this.setFeature(this.submicroPoint);
  }

  /**
   * @Override public IGeneObj getFeature() { if (this.submicroPoint == null) {
   *           return super.getFeature(); } return this.submicroPoint; }
   */

  private Set<ISubmicroAgent> submicroAgents = new HashSet<>();

  public void addSubmicroAgent(ISubmicroAgent subMicroAgent) {
    this.submicroAgents.add(subMicroAgent);
  }

  public Set<ISubmicroAgent> getSubmicroAgents() {
    return submicroAgents;
  }

  // public GeographicPointAgent(GAELDeformable agentGeo, IPointAgent
  // pointAgent) {
  // super(new GeneObjPointPatch(pointAgent.getPosition().toGM_Point()));
  // this.pointAgent = pointAgent;
  // agentGeo.getPointAgents().remove(this.pointAgent);
  // agentGeo.getPointAgents().add(this);
  // }

  public GAELPointGeneObj getSubmicroPoint() {
    return submicroPoint;
  }

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  @Override
  public Set<IFeature> getGeoObjects() {
    return pointAgent.getGeoObjects();
  }

  @Override
  public void setGeoObjects(Set<IFeature> geoObjects) {
    pointAgent.setGeoObjects(geoObjects);

  }

  @Override
  public IGraphLinkableFeature getGraphLinkableFeature() {
    return pointAgent.getGraphLinkableFeature();
  }

  @Override
  public void setGraphLinkableFeature(IGraphLinkableFeature feature) {
    pointAgent.setGraphLinkableFeature(feature);
  }

  @Override
  public Set<IEdge> getEdgesIn() {
    return pointAgent.getEdgesIn();
  }

  @Override
  public void setEdgesIn(Set<IEdge> edgesIn) {
    pointAgent.setEdgesIn(edgesIn);

  }

  @Override
  public void addEdgeIn(IEdge edgeIn) {
    pointAgent.addEdgeIn(edgeIn);

  }

  @Override
  public Set<IEdge> getEdgesOut() {
    return pointAgent.getEdgesOut();
  }

  @Override
  public void setEdgesOut(Set<IEdge> edgesOut) {
    pointAgent.setEdgesOut(edgesOut);

  }

  @Override
  public void addEdgeOut(IEdge edgeOut) {
    pointAgent.addEdgeOut(edgeOut);

  }

  @Override
  public Set<IEdge> getEdges() {
    return pointAgent.getEdges();
  }

  @Override
  public int getDegree() {
    return pointAgent.getDegree();
  }

  @Override
  public void setGeom(IPoint geom) {
    pointAgent.setGeom(geom);
  }

  @Override
  public IGraph getGraph() {
    return pointAgent.getGraph();
  }

  @Override
  public void setGraph(IGraph graph) {
    pointAgent.setGraph(graph);
  }

  @Override
  public double getProximityCentrality() {
    return pointAgent.getProximityCentrality();
  }

  @Override
  public double getBetweenCentrality() {
    return pointAgent.getBetweenCentrality();
  }

  @Override
  public Set<INode> getNextNodes() {
    return pointAgent.getNextNodes();
  }

  @Override
  public Map<INode, IEdge> getNeighbourEdgeNode() {
    return pointAgent.getNeighbourEdgeNode();
  }

  @Override
  public boolean hasGeom() {
    return pointAgent.hasGeom();
  }

  @Override
  public ITopology getTopo() {
    return pointAgent.getTopo();
  }

  @Override
  public void setTopo(ITopology t) {
    pointAgent.setTopo(t);
  }

  @Override
  public boolean hasTopo() {
    return pointAgent.hasTopo();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return pointAgent.cloneGeom();
  }

  @Override
  public List<IFeatureCollection<IFeature>> getFeatureCollections() {
    return pointAgent.getFeatureCollections();
  }

  @Override
  public IFeatureCollection<IFeature> getFeatureCollection(int i) {
    return pointAgent.getFeatureCollection(i);
  }

  @Override
  public List<IFeature> getCorrespondants() {
    return pointAgent.getCorrespondants();
  }

  @Override
  public void setCorrespondants(List<IFeature> L) {
    pointAgent.setCorrespondants(L);

  }

  @Override
  public IFeature getCorrespondant(int i) {
    return pointAgent.getCorrespondant(i);
  }

  @Override
  public void addCorrespondant(IFeature O) {
    pointAgent.addCorrespondant(O);

  }

  @Override
  public void removeCorrespondant(IFeature O) {
    pointAgent.removeCorrespondant(O);

  }

  @Override
  public void clearCorrespondants() {
    pointAgent.clearCorrespondants();

  }

  @Override
  public void addAllCorrespondants(Collection<IFeature> c) {
    pointAgent.addAllCorrespondants(c);

  }

  @Override
  public Collection<IFeature> getCorrespondants(
      IFeatureCollection<? extends IFeature> pop) {
    return pointAgent.getCorrespondants(pop);
  }

  @Override
  public void setPopulation(IPopulation<? extends IFeature> population) {
    pointAgent.setPopulation(population);
  }

  @Override
  public void setFeatureType(GF_FeatureType featureType) {
    pointAgent.setFeatureType(featureType);
  }

  @Override
  public GF_FeatureType getFeatureType() {
    return pointAgent.getFeatureType();
  }

  @Override
  public Object getAttribute(GF_AttributeType attribute) {
    return pointAgent.getAttribute(attribute);
  }

  @Override
  public void setAttribute(GF_AttributeType attribute, Object valeur) {
    pointAgent.setAttribute(attribute, valeur);
  }

  @Override
  public List<? extends IFeature> getRelatedFeatures(GF_FeatureType ftt,
      GF_AssociationRole role) {
    return pointAgent.getRelatedFeatures(ftt, role);
  }

  @Override
  public Object getAttribute(String nomAttribut) {
    return pointAgent.getAttribute(nomAttribut);
  }

  @Override
  public List<? extends IFeature> getRelatedFeatures(String nomFeatureType,
      String nomRole) {
    return pointAgent.getRelatedFeatures(nomFeatureType, nomRole);
  }

  @Override
  public Representation getRepresentation() {
    return pointAgent.getRepresentation();
  }

  @Override
  public void setRepresentation(Representation rep) {
    pointAgent.setRepresentation(rep);
  }

  @Override
  public INode getGraphNode() {
    return pointAgent.getGraphNode();
  }

  @Override
  public IDirectPositionList getPositions() {
    return pointAgent.getPositions();
  }

  @Override
  public IDirectPosition getPosition() {
    return pointAgent.getPosition();
  }

  @Override
  public void updatePosition(IDirectPosition pos) {
    pointAgent.updatePosition(pos);
  }

  @Override
  public double getX() {
    return pointAgent.getX();
  }

  @Override
  public void setX(double x) {
    pointAgent.setX(x);
  }

  @Override
  public double getY() {
    return pointAgent.getY();
  }

  @Override
  public void setY(double y) {
    pointAgent.setY(y);
  }

  @Override
  public double getZ() {
    return pointAgent.getZ();
  }

  @Override
  public double getZIni() {
    return pointAgent.getZIni();
  }

  @Override
  public IDirectPosition getPositionIni() {
    return pointAgent.getPositionIni();
  }

  @Override
  public double getXIni() {
    return pointAgent.getXIni();
  }

  @Override
  public double getYIni() {
    return pointAgent.getYIni();
  }

  @Override
  public ArrayList<PointAgentState> getEtats() {
    return pointAgent.getEtats();
  }

  @Override
  public void effacerEtats() {
    pointAgent.effacerEtats();

  }

  @Override
  public boolean isDansListe() {
    return pointAgent.isDansListe();
  }

  @Override
  public void setDansListe(boolean dansListe) {
    pointAgent.setDansListe(dansListe);

  }

  @Override
  public ArrayList<ISubMicro> getSubmicros() {
    return pointAgent.getSubmicros();
  }

  @Override
  public GAELPointSingleton getPointSingleton() {
    return pointAgent.getPointSingleton();
  }

  @Override
  public void setPointSingleton(GAELPointSingleton pointSingleton) {
    pointAgent.setPointSingleton(pointSingleton);
  }

  @Override
  public boolean isFixe() {
    return pointAgent.isFixe();
  }

  @Override
  public void setFixe(boolean fixe) {
    pointAgent.setFixe(fixe);
  }

  @Override
  public boolean isSelectionne() {
    return pointAgent.isSelectionne();
  }

  @Override
  public void setSelectionne(boolean selectionne) {
    pointAgent.setSelectionne(selectionne);
  }

  @Override
  public double getSommeImportances() {
    return pointAgent.getSommeImportances();
  }

  @Override
  public void incrementerSommeImportances(double dimp) {
    pointAgent.incrementerSommeImportances(dimp);
  }

  @Override
  public int getIndex() {
    return pointAgent.getIndex();
  }

  @Override
  public void setIndex(int posTri) {
    pointAgent.setIndex(posTri);

  }

  @Override
  public ArrayList<IPointAgent> getAgentPointAccointants() {
    return pointAgent.getAgentPointAccointants();
  }

  @Override
  public void addAgentPointAccointants(IPointAgent ap) {
    pointAgent.addAgentPointAccointants(ap);
  }

  @Override
  public ContourLineAgent getCourbeDeNiveau() {
    return pointAgent.getCourbeDeNiveau();
  }

  @Override
  public GAELLinkableFeature getLinkedFeature() {
    return pointAgent.getLinkedFeature();
  }

  @Override
  public void setLinkedFeature(GAELLinkableFeature linkedFeature) {
    pointAgent.setLinkedFeature(linkedFeature);
  }

  @Override
  public void goBackToState(PointAgentState eap) {
    pointAgent.goBackToState(eap);
  }

  @Override
  public double getDistance(GAELSegment s) {
    return pointAgent.getDistance(s);
  }

  @Override
  public double getDistance(double x_, double y_) {
    return pointAgent.getDistance(x_, y_);
  }

  @Override
  public double getDistance(IPointAgent p) {
    return pointAgent.getDistance(p);
  }

  @Override
  public double getDistanceCourante(GAELSegment s) {
    return pointAgent.getDistanceCourante(s);
  }

  @Override
  public IDirectPosition getProj(GAELSegment s) {
    return pointAgent.getProj(s);
  }

  @Override
  public double getDistanceInitiale(double x_, double y_) {
    return pointAgent.getDistanceInitiale(x_, y_);
  }

  @Override
  public double getDistanceCourante(double x_, double y_) {
    return pointAgent.getDistanceCourante(x_, y_);
  }

  @Override
  public double getDistanceInitiale(IPointAgent p) {
    return pointAgent.getDistanceInitiale(p);
  }

  @Override
  public double getDistanceCourante(IPointAgent p) {
    return pointAgent.getDistanceCourante(p);
  }

  @Override
  public double getDistanceAPositionInitiale() {
    return pointAgent.getDistanceAPositionInitiale();
  }

  @Override
  public double getInitialOrientation(IPointAgent p) {
    return pointAgent.getInitialOrientation(p);
  }

  @Override
  public double getOrientation(IPointAgent p) {
    return pointAgent.getOrientation(p);
  }

  @Override
  public double getOrientationEcart(IPointAgent p) {
    return pointAgent.getOrientation(p);
  }

  @Override
  public boolean isLinkedBySegment(TriangulationPoint point) {
    return pointAgent.isLinkedBySegment(point);
  }

  @Override
  public boolean satisfactionParfaite() {
    return pointAgent.satisfactionParfaite();
  }

  @Override
  public boolean intersecte(IEnvelope env) {
    return pointAgent.intersecte(env);
  }

  @Override
  public void setFieldAgent(FieldAgent agent) {
    pointAgent.setFieldAgent(agent);
  }

  @Override
  public FieldAgent getFieldAgent() {
    return pointAgent.getFieldAgent();
  }

  @Override
  public void updateForces() {
    pointAgent.setActionsToTry(new HashSet<ActionProposal>());
    for (Constraint cont : this.getConstraints()) {
      if (cont instanceof SubmicroConstraint) {
        ((SubmicroConstraint) cont).proposeDisplacement(this);
      }
    }
  }

  @Override
  public Set<ActionProposal> getForces() {
    return this.getActionProposals();
  }

  @Override
  public void computeForces() {
    pointAgent.computeForces();
  }

  @Override
  public double getDistancesFromBalance() {
    return pointAgent.getDistancesFromBalance();
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
