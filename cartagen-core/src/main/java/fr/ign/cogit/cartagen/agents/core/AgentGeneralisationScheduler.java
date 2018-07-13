package fr.ign.cogit.cartagen.agents.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObservationSubject;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.Scheduler;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * @author julien Gaffuri 28 janv. 2009
 */
public class AgentGeneralisationScheduler extends Scheduler
    implements AgentObservationSubject, Callable<Integer> {
  private static Logger logger = Logger
      .getLogger(AgentGeneralisationScheduler.class.getName());

  /**
   * The scheduler
   */
  private static AgentGeneralisationScheduler scheduler = new AgentGeneralisationScheduler();
  private AtomicInteger processedAgents = new AtomicInteger(0);

  /**
   * @return The unique instance
   */
  public static AgentGeneralisationScheduler getInstance() {
    return AgentGeneralisationScheduler.scheduler;
  }

  protected AgentGeneralisationScheduler() {
    super();
  }

  private Collection<AgentObserver> observers = new HashSet<>();

  @Override
  public void add(IAgent ag) {
    this.getList().add(ag);
    notifyChange();
  }

  public void shuffle() {
    Collections.shuffle(this.getList());
  }

  @Override
  public void remove(IAgent ag) {
    this.getList().remove(ag);
    notifyChange();
  }

  public void initList() {
    this.getList().clear();
    notifyChange();
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
      // tant que la file n'est pas vide, activer les points de la pile a
      // tour
      // de role
      while (!this.getList().isEmpty()) {
        IAgent agent = this.getList().get(0);

        if (AgentGeneralisationScheduler.logger.isInfoEnabled()) {
          AgentGeneralisationScheduler.logger
              .info("Activation de " + agent + " (" + i++ + "/" + nb + ")");
        }
        agent.activate();

        this.remove(agent);
        processedAgents.incrementAndGet();
        this.testStop();

        // FIXME pause eventuelle
        if (this.getWaitTime() > 0
        /*
         * && GeneralisationRightPanelAgentComplement.getInstance().
         * cFairePauses .isSelected()
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

  @Override
  public void attach(AgentObserver observer) {
    observers.add(observer);
  }

  @Override
  public void detach(AgentObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyChange() {
    for (AgentObserver observer : observers) {
      observer.update();
    }
  }

  @Override
  public Integer call() throws Exception {
    run();
    return processedAgents.get();
  }

}
