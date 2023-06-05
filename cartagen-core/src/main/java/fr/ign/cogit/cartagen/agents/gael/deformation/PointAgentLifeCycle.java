/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;

/**
 * Point agent lifecycle: the point moves towards its balance
 * @author jgaffuri
 */
public class PointAgentLifeCycle implements AgentLifeCycle {
  private static Logger logger = LogManager
      .getLogger(PointAgentLifeCycle.class.getName());

  private int statesMaxNumber = -1;

  public int getStatesMaxNumber() {
    return this.statesMaxNumber;
  }

  public void setStatesMaxNumber(int statesMaxNumber) {
    this.statesMaxNumber = statesMaxNumber;
  }

  private boolean storeStates = false;

  @Override
  public boolean isStoreStates() {
    return this.storeStates;
  }

  @Override
  public void setStoreStates(boolean storeStates) {
    this.storeStates = storeStates;
  }

  /**
     */
  private static PointAgentLifeCycle instance = null;

  public static PointAgentLifeCycle getInstance() {
    if (PointAgentLifeCycle.instance == null) {
      PointAgentLifeCycle.instance = new PointAgentLifeCycle();
    }
    return PointAgentLifeCycle.instance;
  }

  private PointAgentLifeCycle() {
  }

  @Override
  public AgentSatisfactionState compute(IAgent agent)
      throws InterruptedException {
    if (PointAgentLifeCycle.logger.isDebugEnabled()) {
      PointAgentLifeCycle.logger.debug("activation de " + agent);
    }

    IPointAgent pt = (IPointAgent) agent;

    // si le point est fixe ou selectionne ou qu'il n'a pas de contrainte, il
    // est satisfait
    if (pt.isFixe() || pt.isSelectionne() || pt.getConstraints().size() == 0) {
      pt.setSatisfaction(0.0);
      pt.setDansListe(false);
      if (PointAgentLifeCycle.logger.isDebugEnabled()) {
        PointAgentLifeCycle.logger.debug("			" + this
            + " -> etat parfait atteint (point fixe, selectionne ou non contraint)");
      }
      return AgentSatisfactionState.PERFECTLY_SATISFIED_INITIALY;
    }

    // calcule la satisfaction

    // recupere les deplacements
    if (PointAgentLifeCycle.logger.isTraceEnabled()) {
      PointAgentLifeCycle.logger
          .trace("recuperation des actions des contraintes de " + this);
    }
    pt.updateActionProposals();

    // calcule somme
    double dx = 0.0, dy = 0.0;
    for (ActionProposal actionProposal : pt.getActionProposals()) {
      PointAgentDisplacementAction dep = (PointAgentDisplacementAction) actionProposal
          .getAction();
      dx += dep.getDx();
      dy += dep.getDy();
    }
    // calcule satisfaction
    pt.setSatisfaction(Math.sqrt(dx * dx + dy * dy));

    if (pt.satisfactionParfaite()) {

      // satisfaction parfaite
      if (PointAgentLifeCycle.logger.isDebugEnabled()) {
        PointAgentLifeCycle.logger.debug("			" + this
            + " -> etat parfait (satis.=" + pt.getSatisfaction() + ")");
      }

      // vide liste des deplacements
      pt.cleanActionsToTry();

      return AgentSatisfactionState.PERFECTLY_SATISFIED_INITIALY;
    }

    // memorise l'etat avant deplacement
    PointAgentState etatPrecedent = new PointAgentStateImpl(pt, null, null);

    // applique le deplacement somme
    (new PointAgentDisplacementAction(pt, null, dx, dy)).compute();

    // calcule la nouvelle satisfaction

    // recupere les deplacements
    pt.updateActionProposals();

    // calcule somme
    dx = 0.0;
    dy = 0.0;
    for (ActionProposal actionProposal : pt.getActionProposals()) {
      PointAgentDisplacementAction dep = (PointAgentDisplacementAction) actionProposal
          .getAction();
      dx += dep.getDx();
      dy += dep.getDy();
    }

    // calcule satisfaction
    pt.setSatisfaction(Math.sqrt(dx * dx + dy * dy));

    if (pt.satisfactionParfaite()) {
      // satisfaction parfaite
      if (PointAgentLifeCycle.logger.isDebugEnabled()) {
        PointAgentLifeCycle.logger
            .debug("			" + this + " -> etat parfait atteint");
      }

      // enregistre l'etat
      pt.getEtats().add(new PointAgentStateImpl(pt, null, null));

      // vide liste des deplacements
      pt.cleanActionsToTry();

      return AgentSatisfactionState.PERFECTLY_SATISFIED_AFTER_TRANSFORMATION;
    }

    else if (pt.getSatisfaction() < etatPrecedent.getSatisfaction()) {
      // satisfaction a ete amelioree
      if (PointAgentLifeCycle.logger.isDebugEnabled()) {
        PointAgentLifeCycle.logger
            .debug("			" + this + " -> etat ameliore");
      }

      // enregistre l'etat
      pt.getEtats().add(new PointAgentStateImpl(pt, null, null));

      // vide liste des deplacements
      pt.cleanActionsToTry();

      return AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT;
    }

    else {
      // satisfaction n'a pas progresse
      if (PointAgentLifeCycle.logger.isDebugEnabled()) {
        PointAgentLifeCycle.logger.debug(
            "			" + this + " -> *** etat deteriore ou inchange");
      }

      // retrouve son etat precedent
      pt.goBackToState(etatPrecedent);

      // vide liste des deplacements
      pt.cleanActionsToTry();

      return AgentSatisfactionState.SATISFACTION_UNCHANGED;
    }
  }

}
