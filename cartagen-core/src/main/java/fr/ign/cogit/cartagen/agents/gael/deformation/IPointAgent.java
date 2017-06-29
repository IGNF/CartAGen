package fr.ign.cogit.cartagen.agents.gael.deformation;

import java.util.ArrayList;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ContourLineAgent;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;

/**
 * The point fieldAgent class.
 * @author julien Gaffuri 26 juil. 2005
 */
public interface IPointAgent extends IAgent, TriangulationPoint, IFeature {

  INode getGraphNode();

  IDirectPositionList getPositions();

  IDirectPosition getPosition();

  void updatePosition(IDirectPosition pos);

  double getX();

  void setX(double x);

  double getY();

  void setY(double y);

  double getZ();

  double getZIni();

  /**
   * @return
   */
  @Override
  IDirectPosition getPositionIni();

  double getXIni();

  double getYIni();

  @Override
  IPoint getGeom();

  ArrayList<PointAgentState> getEtats();

  void effacerEtats();

  boolean isDansListe();

  void setDansListe(boolean dansListe);

  /**
   * @return
   */
  ArrayList<ISubMicro> getSubmicros();

  /**
   * @return
   */
  GAELPointSingleton getPointSingleton();

  /**
   * @param pointSingleton
   */
  void setPointSingleton(GAELPointSingleton pointSingleton);

  /**
   * @return
   */
  boolean isFixe();

  /**
   * @param fixe
   */
  void setFixe(boolean fixe);

  /**
   * @return
   */
  boolean isSelectionne();

  /**
   * @param selectionne
   */
  void setSelectionne(boolean selectionne);

  /**
   * @return
   */
  double getSommeImportances();

  void incrementerSommeImportances(double dimp);

  @Override
  int getIndex();

  @Override
  void setIndex(int posTri);

  /**
   * @return
   */
  ArrayList<IPointAgent> getAgentPointAccointants();

  void addAgentPointAccointants(IPointAgent ap);

  /**
   * @return la courbe de niveau eventuelle a laquelle le point appartient
   */
  ContourLineAgent getCourbeDeNiveau();

  /**
   * @return
   */
  GAELLinkableFeature getLinkedFeature();

  /**
   * @param linkedFeature
   */
  void setLinkedFeature(GAELLinkableFeature linkedFeature);

  @Override
  void clean();

  @Override
  void goBackToState(AgentState ea);

  void goBackToState(PointAgentState eap);

  double getDistance(GAELSegment s);

  double getDistance(double x_, double y_);

  double getDistance(IPointAgent p);

  double getDistanceCourante(GAELSegment s);

  IDirectPosition getProj(GAELSegment s);

  double getDistanceInitiale(double x_, double y_);

  double getDistanceCourante(double x_, double y_);

  double getDistanceInitiale(IPointAgent p);

  double getDistanceCourante(IPointAgent p);

  double getDistanceAPositionInitiale();

  double getInitialOrientation(IPointAgent p);

  /**
   * orientation de this a p, en radians entre -PI et PI
   * @param p
   * @return
   */
  double getOrientation(IPointAgent p);

  double getOrientationEcart(IPointAgent p);

  @Override
  boolean isLinkedBySegment(TriangulationPoint point);

  @Override
  void run();

  @Override
  void updateActionProposals();

  void updateForces();

  void computeForces();

  Set<ActionProposal> getForces();

  double getDistancesFromBalance();

  boolean satisfactionParfaite();

  @Override
  String toString();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#getFeature()
   */
  /**
   * @return
   */
  IFeature getFeature();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.Agent#setFeature(fr.ign.cogit.geoxygene
   * .api.feature.IFeature)
   */
  /**
   * @param feature
   */
  void setFeature(IFeature feature);

  void setFieldAgent(FieldAgent agent);

  FieldAgent getFieldAgent();

}
