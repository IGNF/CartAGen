package fr.ign.cogit.cartagen.agents.core.task;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.action.InternalGeneralisationAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

public class InternalGeneralisationTask extends TaskImpl {

  public InternalGeneralisationTask(ICartacomAgent cAgent) {
    super();
    this.setTaskOwner(cAgent);
    this.setStatus(TaskStatus.NOT_STARTED);
  }

  @Override
  public void execute() {
    CartacomAction action = new InternalGeneralisationAction(
        (IAgent) this.getTaskOwner());
    try {
      action.compute();
      this.setStatus(TaskStatus.FINISHED);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
