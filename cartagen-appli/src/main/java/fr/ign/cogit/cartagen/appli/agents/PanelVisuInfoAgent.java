/**
 * @author julien Gaffuri 15 sept. 2008
 */
package fr.ign.cogit.cartagen.appli.agents;

import java.awt.Color;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.state.MicroAgentState;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewAwtPanel;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.InternStructureAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.MesoAgentState;

/**
 * @author julien Gaffuri 15 sept. 2008
 * 
 */
public class PanelVisuInfoAgent extends LayerViewAwtPanel {
  private static final long serialVersionUID = -5037001262393524360L;
  private final static Logger logger = Logger
      .getLogger(PanelVisuInfoAgent.class.getName());

  /**
   */
  public AgentState etatAAfficher = null;

  public PanelVisuInfoAgent() {
    super();

    // TODO

  }

  /**
   * Displays the satisfaction color of the micro agent considered
   * @param state : the current state of the agent
   * @param col : the computed satisfaction color
   */
  public void paint(MicroAgentState state, Color col) {
    // FIXME this.draw(col, state.getGeometry());
  }

  /**
   * Computes the satisfaction color of the micro agent considered
   * @param state : the current state of the agent
   */
  public void paintSatisfaction(MicroAgentState state) {

    double s = state.getSatisfaction() / 100;
    if (s < 0.75) {
      s = 0.0;
    } else {
      s = (s - 0.75) * 4;
    }
    Color col = new Color((int) (255.0 * (1 - s)), (int) (255.0 * s), 0);
    this.paint(state, col);

  }

  /**
   * Displays the satisfaction color of the meso agent considered
   * @param state : the current state of the agent
   * @param col : the computed satisfaction color
   */
  public void paint(MesoAgentState state, Color col) {
    if (state.getGeometry() != null) {
      // FIXME this.draw(col, state.getGeometry());
    }
    for (GeographicObjectAgentState eago : state.getComponentStates()) {
      if (!eago.isDeleted()) {
        this.paint((MicroAgentState) eago, col);
      }
    }
  }

  /**
   * Computes the satisfaction color of the meso agent considered
   * @param state : the current state of the agent
   */
  public void paintSatisfaction(MesoAgentState state) {
    if (state.getGeometry() != null) {
      double s = state.getSatisfaction() / 100;
      if (s < 0.75) {
        s = 0.0;
      } else {
        s = (s - 0.75) * 4;
      }
      Color col = new Color((int) (255.0 * (1 - s)), (int) (255.0 * s), 0);
      this.paint(state, col);
    }
    for (GeographicObjectAgentState eago : state.getComponentStates()) {
      if (!eago.isDeleted()) {
        this.paint((MicroAgentState) eago, Color.CYAN);
      }
    }
  }

  /**
   * Displays the satisfaction color of the intern structure agent considered
   * @param state : the current state of the agent
   * @param col : the computed satisfaction color
   */
  public void paint(InternStructureAgentState state, Color col) {
    if (state.getGeometry() != null) {
      // FIXME this.draw(col, state.getGeometry());
    }
    for (GeographicObjectAgentState eago : state.getComponentStates()) {
      if (!eago.isDeleted()) {
        this.paint((MicroAgentState) eago, col);
      }
    }
  }

  /**
   * Computes the satisfaction color of the intern structure agent considered
   * @param state : the current state of the agent
   */
  public void paintSatisfaction(InternStructureAgentState state) {
    if (state.getGeometry() != null) {
      double s = state.getSatisfaction() / 100;
      if (s < 0.75) {
        s = 0.0;
      } else {
        s = (s - 0.75) * 4;
      }
      Color col = new Color((int) (255.0 * (1 - s)), (int) (255.0 * s), 0);
      this.paint(state, col);
    }
    for (GeographicObjectAgentState eago : state.getComponentStates()) {
      if (!eago.isDeleted()) {
        this.paint((MicroAgentState) eago, Color.CYAN);
      }
    }
  }

}
