package fr.ign.cogit.cartagen.agents.core;

import java.util.Collections;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.contrib.agents.Scheduler;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * @author julien Gaffuri 28 janv. 2009
 */
public class AgentGeneralisationScheduler extends Scheduler {
  private static Logger logger = Logger
      .getLogger(AgentGeneralisationScheduler.class.getName());

  /**
   * The scheduler
   */
  private static AgentGeneralisationScheduler scheduler = new AgentGeneralisationScheduler();

  /**
   * @return The unique instance
   */
  public static AgentGeneralisationScheduler getInstance() {
    return AgentGeneralisationScheduler.scheduler;
  }

  protected AgentGeneralisationScheduler() {
    super();
  }

  @Override
  public void add(IAgent ag) {
    this.getList().add(ag);
    // FIXME
    // GeneralisationRightPanelAgentComplement.getInstance().majAgentsListe();
  }

  public void shuffle() {
    Collections.shuffle(this.getList());
  }

  @Override
  public void remove(IAgent ag) {
    this.getList().remove(ag);
    // FIXME
    // GeneralisationRightPanelAgentComplement.getInstance().majAgentsListe();
  }

  public void initList() {
    this.getList().clear();
    // FIXME
    // GeneralisationRightPanelAgentComplement.getInstance().majAgentsListe();
  }

  @Override
  public synchronized void run() {
    AgentGeneralisationScheduler.logger.info("- Lancement du moteur");
    int nb = this.getList().size();
    int i = 1;

    if (AgentGeneralisationScheduler.logger.isDebugEnabled()) {
      AgentGeneralisationScheduler.logger
          .debug("  nombre d'agents a activer: " + nb);
    }
    if (AgentGeneralisationScheduler.logger.isDebugEnabled()) {
      AgentGeneralisationScheduler.logger
          .debug("  echelle cible: " + Legend.getSYMBOLISATI0N_SCALE());
    }

    try {
      // tant que la file n'est pas vide, activer les points de la pile a tour
      // de role
      while (!this.getList().isEmpty()) {
        IAgent agent = this.getList().get(0);

        if (AgentGeneralisationScheduler.logger.isInfoEnabled()) {
          AgentGeneralisationScheduler.logger
              .info("Activation de " + agent + " (" + i++ + "/" + nb + ")");
        }
        agent.activate();

        this.remove(agent);

        this.testStop();

        // FIXME pause eventuelle
        if (this.getWaitTime() > 0
        /*
         * && GeneralisationRightPanelAgentComplement.getInstance().cFairePauses
         * .isSelected()
         */) {
          try {
            Thread.sleep(this.getWaitTime());
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

        this.testStop();

      }
      this.thread = null;
      /*
       * GeneralisationRightPanelAgentComplement
       * .getInstance().bDemarrerArreterMoteur .setIcon(new
       * ImageIcon(AgentGeneralisationScheduler.class
       * .getResource("/images/start.gif").getPath() .replaceAll("%20", " ")));
       */
      AgentGeneralisationScheduler.logger.info("- Fin moteur");
    } catch (InterruptedException e) {
      this.stop = false;
      this.thread = null;
    }
  }

}
