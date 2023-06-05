package fr.ign.cogit.cartagen.agents.core.agent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.constraint.InternStructuresSatisfaction;
import fr.ign.cogit.cartagen.agents.core.constraint.MesoComponentsSatisfaction;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.InternStructureAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.MesoAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.MesoAgentStateImpl;

/**
 * @author JGaffuri
 * 
 */
public abstract class MesoAgentGeneralisation<ComponentClass extends GeographicObjectAgent>
    extends GeographicObjectAgentGeneralisation
    implements MesoAgent<ComponentClass> {
  private static Logger logger = LogManager
      .getLogger(MesoAgentGeneralisation.class.getName());

  /**
   * The components of the meso.
   */
  private List<ComponentClass> components = new ArrayList<ComponentClass>();

  @Override
  public List<ComponentClass> getComponents() {
    return this.components;
  }

  @Override
  public void setComponents(List<ComponentClass> components) {
    this.components = components;
  }

  /**
   * The potential internal structures of the meso.
   */
  private List<InternStructureAgent> internStructures = new ArrayList<InternStructureAgent>();

  @Override
  public List<InternStructureAgent> getInternStructures() {
    return this.internStructures;
  }

  @Override
  public void setInternStructures(List<InternStructureAgent> internStructures) {
    this.internStructures = internStructures;
  }

  /**
   * The possible delignating sections
   */
  private List<ISectionAgent> sections = new ArrayList<ISectionAgent>();

  public List<ISectionAgent> getSectionAgents() {
    return this.sections;
  }

  public void addSectionAgent(ISectionAgent section) {
    this.sections.add(section);
  }

  public void setSectionAgents(List<ISectionAgent> sections) {
    this.sections = sections;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MesoAgent#getComponentsSatisfaction()
   */
  @Override
  public double getComponentsSatisfaction() {
    if (this.getComponents() == null || this.getComponents().size() == 0) {
      return 100.0;
    }

    // the mean of the non deleted components satisfactions.

    int nb = 0;
    double sum = 0.0;
    for (ComponentClass component : this.getComponents()) {
      if (component.isDeleted()) {
        continue;
      }
      component.computeSatisfaction();
      sum += component.getSatisfaction();
      nb++;
    }
    if (nb == 0) {
      return 100.0;
    }
    double s = sum / nb;
    if (s > 100) {
      MesoAgentGeneralisation.logger.warn(
          "problem during the computation of meso satisfaction: number greater than 100 :"
              + s);
    }
    return s;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MesoAgent#getComponentsSatisfaction()
   */
  @Override
  public double getInternStructuresSatisfaction() {
    if (this.getInternStructures() == null
        || this.getInternStructures().size() == 0) {
      return 100.0;
    }

    // the mean of the non deleted components satisfactions.

    int nb = 0;
    double sum = 0.0;
    for (InternStructureAgent structure : this.getInternStructures()) {
      if (structure.isDeleted()) {
        continue;
      }
      structure.computeSatisfaction();
      sum += structure.getSatisfaction();
      nb++;
    }
    if (nb == 0) {
      return 100.0;
    }
    double s = sum / nb;
    if (s > 100) {
      MesoAgentGeneralisation.logger.warn(
          "problem during the computation of meso satisfaction: number greater than 100 :"
              + s);
    }
    return s;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgentImpl#
   * buildCurrentState (fr.ign.cogit.agentgeoxygene.state.GeographicAgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  public MesoAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new MesoAgentStateImpl(this, (MesoAgentState) previousState, action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgentImpl#goBackToState
   * (fr.ign.cogit.agentgeoxygene.state.AgentState)
   */
  @Override
  public void goBackToState(AgentState state) {
    super.goBackToState(state);
    for (GeographicObjectAgentState componentState : ((MesoAgentState) state)
        .getComponentStates()) {
      componentState.getAgent().goBackToState(componentState);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MesoAgent#getBestComponentToActivate(
   * java.util.ArrayList)
   */
  @Override
  public ComponentClass getBestComponentToActivate(
      ArrayList<ComponentClass> componentsList) {
    // return the first element
    return componentsList.get(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MesoAgent#getBestComponentToActivate(
   * java.util.ArrayList)
   */
  @Override
  public InternStructureAgent getBestInternStructureToActivate(
      ArrayList<InternStructureAgent> structuresList) {
    // return the first element
    return structuresList.get(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.MesoAgent#getNonDeletedComponentsNumber()
   */
  @Override
  public int getNonDeletedComponentsNumber() {
    int nb = 0;
    for (ComponentClass component : this.getComponents()) {
      if (!component.isDeleted()) {
        nb++;
      }
    }
    return nb;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MesoAgent#getComponentsMaximumArea()
   */
  @Override
  public double getComponentsMaximumArea() {
    double maxArea = 0.0, area;
    for (ComponentClass ag : this.getComponents()) {
      if (ag.isDeleted()) {
        continue;
      }
      area = ag.getGeom().area();
      if (area > maxArea) {
        maxArea = area;
      }
    }
    return maxArea;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgentImpl#
   * printInfosConsole ()
   */
  @Override
  public void printInfosConsole() {
    super.printInfosConsole();
    System.out.println("Components: nb=" + this.getComponents().size());
    for (ComponentClass comp : this.getComponents()) {
      System.out.print("   ");
      comp.printInfosConsole();
    }
  }

  @Override
  public void manageInternalSideEffects(GeographicObjectAgent geoObj) {
    // Do nothing as default
  }

  public void ajouterContrainteSatisfactionComposants(double importance) {
    new MesoComponentsSatisfaction<ComponentClass>(this, importance);
  }

  public void ajouterContrainteSatisfactionStructuresInternes(
      double importance) {
    new InternStructuresSatisfaction<ComponentClass>(this, importance);
  }

  // ------- truc du package carto

  /**
   * la couleur du meso, s'il est colorie
   */
  Color couleur = null;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MesoAgent#getCouleur()
   */
  public Color getColor() {
    return this.couleur;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MesoAgent#setCouleur(java.awt.Color)
   */
  /**
   * @param couleur
   */
  public void setColor(Color couleur) {
    this.couleur = couleur;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.MesoAgent#isColored()
   */
  public boolean isColored() {
    return this.couleur != null;
  }

  // ------- fin truc du package carto

}
