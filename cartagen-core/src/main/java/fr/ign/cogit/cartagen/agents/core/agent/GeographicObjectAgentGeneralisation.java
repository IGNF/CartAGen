/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentStateImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author JGaffuri
 */
public abstract class GeographicObjectAgentGeneralisation
    extends GeographicAgentGeneralisation
    implements GeographicObjectAgent, IGeographicObjectAgentGeneralisation {

  private static Logger logger = Logger
      .getLogger(GeographicObjectAgentGeneralisation.class.getName());

  private IGeneObj geneObj = null;

  @Override
  public IGeneObj getFeature() {
    return this.geneObj;
  }

  @Override
  public void setFeature(IFeature geoObj) {
    if (geoObj instanceof IGeneObj) {
      this.geneObj = (IGeneObj) geoObj;
      this.geneObj.addToGeneArtifacts(this);
    }
  }

  /**
   * @return
   */
  @Override
  public IGeometry getInitialGeom() {
    return this.geneObj.getInitialGeom();
  }

  /**
   * @param geomInitiale
   */
  @Override
  public void setInitialGeom(IGeometry geomInitiale) {
    this.geneObj.setInitialGeom(geomInitiale);
  }

  /**
   * The meso agent the agent possibly belong to
   */
  private MesoAgent<? extends GeographicObjectAgent> mesoAgent = null;

  /**
   * @return
   */
  @Override
  public MesoAgent<? extends GeographicObjectAgent> getMesoAgent() {
    return this.mesoAgent;
  }

  /**
   * @param agentMesoControleur
   */
  @Override
  public void setMesoAgent(
      MesoAgent<? extends GeographicObjectAgent> agentMesoControleur) {
    this.mesoAgent = agentMesoControleur;
  }

  /**
   * The internal structures the agent possibly belong to
   */
  private List<InternStructureAgent> structureAgents = new ArrayList<InternStructureAgent>();

  @Override
  public List<InternStructureAgent> getStructureAgents() {
    return this.structureAgents;
  }

  @Override
  public void setStructureAgents(List<InternStructureAgent> structureAgents) {
    this.structureAgents = structureAgents;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicAgentImpl#buildCurrentState
   * (fr.ign.cogit.agentgeoxygene.state.AgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  public GeographicObjectAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new GeographicObjectAgentStateImpl(this,
        (GeographicObjectAgentState) previousState, action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicAgentImpl#goBackToState(fr.
   * ign.cogit.agentgeoxygene.state.AgentState)
   */
  @Override
  public void goBackToState(AgentState state) {
    super.goBackToState(state);
    GeographicObjectAgentState state_ = (GeographicObjectAgentState) state;
    this.geneObj.setGeom(state_.getGeometry());
    this.geneObj.setDeleted(state_.isDeleted());
  }

  @Override
  public void goBackToInitialState() {
    if (!(this.geneObj == null)) {
      this.geneObj.setDeleted(false);
      this.geneObj.setGeom(this.getInitialGeom());
    }
    if (this instanceof MesoAgent<?>) {
      MesoAgent<?> meso = (MesoAgent<?>) this;
      for (GeographicObjectAgent ag : meso.getComponents()) {
        ag.goBackToInitialState();
      }
      for (InternStructureAgent ag : meso.getInternStructures()) {
        ag.goBackToInitialState();
      }
    }
    if (this instanceof InternStructureAgent) {
      InternStructureAgent structure = (InternStructureAgent) this;
      for (GeographicObjectAgent ag : structure.getComponents()) {
        ag.goBackToInitialState();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgent#delete()
   */
  @Override
  public void deleteAndRegister() {
    // delete the features
    this.geneObj.eliminate();
    // delete its components, if it is a meso or a structure agent
    if (this instanceof MesoAgent<?>) {
      for (GeographicObjectAgent ag : ((MesoAgent<?>) this).getComponents()) {
        ag.deleteAndRegister();
      }
      for (InternStructureAgent ag : ((MesoAgent<?>) this)
          .getInternStructures()) {
        ag.deleteAndRegister();
      }
    }
    if (this instanceof InternStructureAgent) {
      for (GeographicObjectAgent ag : ((InternStructureAgent) this)
          .getComponents()) {
        ag.deleteAndRegister();
      }
    }
  }

  @Override
  public void displaceAndRegister(double dx, double dy) {

    // displace the agent's geometry
    this.geneObj
        .setGeom(CommonAlgorithms.translation(this.geneObj.getGeom(), dx, dy));
    // displace the components' geometries, if the agent is a meso
    if (this instanceof MesoAgent<?>) {
      for (GeographicObjectAgent ag : ((MesoAgent<?>) this).getComponents()) {
        ag.getFeature().setGeom(
            CommonAlgorithms.translation(ag.getFeature().getGeom(), dx, dy));
      }
    }

    this.registerDisplacement();

  }

  public void registerDisplacement() {
    this.geneObj.registerDisplacement();

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.AgentImpl#printInfosConsole()
   */
  @Override
  public void printInfosConsole() {
    super.printInfosConsole();
    System.out.println("Feature: " + this.geneObj.getClass().getSimpleName());
    System.out.println("Geometry: " + this.geneObj.getGeom());
    System.out.println("Initial geometry: " + this.getInitialGeom());
    System.out.println("Deletion: " + this.geneObj.isDeleted());
    if (this.getMesoAgent() != null) {
      System.out
          .println("Meso: " + this.getMesoAgent().getClass().getSimpleName()
              + " - id =" + this.getMesoAgent().getId());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  // trucs de gael

  public ArrayList<GAELTriangle> getTrianglesDessous(FieldAgent ac) {
    GeographicObjectAgentGeneralisation.logger
        .warn("Warning: revoir methode getTrianglesDessous de " + this);

    ArrayList<GAELTriangle> at = new ArrayList<GAELTriangle>();
    if (this.getGeom() == null || this.getGeom().isEmpty()) {
      return at;
    }
    at.add(ac.getTriangle(this.getGeom().centroid()));
    return at;
  }

  // fin trucs gael

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getGeom()
   */
  @Override
  public IGeometry getGeom() {
    return this.geneObj.getGeom();
  }

  @Override
  public IPolygon getSymbolGeom() {
    if (this.getGeom().isPolygon()) {
      return (IPolygon) this.getGeom();
    }
    return new GM_Polygon();
  }

  @Override
  public double getSymbolArea() {
    return this.getSymbolGeom().area();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#isDeleted()
   */
  @Override
  public boolean isDeleted() {
    return this.geneObj.isDeleted();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getId()
   */
  @Override
  public int getId() {
    return this.geneObj.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getPopulation()
   */
  @Override
  public IPopulation<? extends IFeature> getPopulation() {
    return this.geneObj.getPopulation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setEstSupprime(boolean)
   */
  @Override
  public void setDeleted(boolean deleted) {
    this.geneObj.setDeleted(deleted);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setGeom(fr.ign.cogit.geoxygene
   * .api.spatial.geomroot.IGeometry)
   */
  @Override
  public void setGeom(IGeometry g) {
    this.geneObj.setGeom(g);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setId(int)
   */
  @Override
  public void setId(int Id) {
    this.geneObj.setId(Id);
  }

}
