package fr.ign.cogit.cartagen.agents.core.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.action.CartacomAction;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.cartacom.constraint.MicroMicroRelationalConstraintWithZone;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.AskToDoAdHocArgument;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.Performative;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.relation.Relation;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationalConstraint;

public class LetToDoActionTask extends ProcessingTaskWithinConvImpl {

  private static Logger logger = LogManager
      .getLogger(LetToDoActionTask.class.getName());

  private CartacomAction action;

  public LetToDoActionTask(ICartacomAgent taskOwner, CartacomAction action) {
    this.setTaskOwner(taskOwner);
    this.setAction(action);
    this.setStatus(TaskStatus.NOT_STARTED);
    // this.setStage(TryActionTask.BEGINNING);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ICartacomAgent getTaskOwner() {
    return (ICartacomAgent) super.getTaskOwner();
  }

  @Override
  public void execute() {
    ICartacomAgent partner = (ICartacomAgent) ((RelationalConstraint) action
        .getConstraint()).getAgentSharingConstraint();
    Relation relation = ((RelationalConstraint) action.getConstraint())
        .getRelation();

    RelationalConstraint constraintForPartner = (RelationalConstraint) action
        .getConstraint();

    for (Constraint c : partner.getConstraints()) {
      if (c instanceof MicroMicroRelationalConstraintWithZone) {
        MicroMicroRelationalConstraintWithZone cc = ((MicroMicroRelationalConstraintWithZone) c);
        if (cc.getRelation().equals(relation)) {
          constraintForPartner = cc;
          break;
        }
      }
    }

    CartacomAction actionToLetDo = this.action;

    this.getTaskOwner().initiateConversation(partner, Performative.ASK_TO_DO,
        new AskToDoAdHocArgument(actionToLetDo, relation), this);

    logger.debug("initiateConversation with " + partner);

    this.setStatus(TaskStatus.WAITING);
    this.getTaskOwner().setWaiting(true);

  }

  public CartacomAction getAction() {
    return action;
  }

  public void setAction(CartacomAction action) {
    this.action = action;
  }

}
