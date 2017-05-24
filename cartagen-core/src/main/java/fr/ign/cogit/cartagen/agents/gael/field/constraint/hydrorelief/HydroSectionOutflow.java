package fr.ign.cogit.cartagen.agents.gael.field.constraint.hydrorelief;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.gael.field.action.FieldActivationAction;
import fr.ign.cogit.cartagen.agents.gael.field.action.HydroSectionDeformationAction;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.ObjectFieldRelationnalConstraint;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

public class HydroSectionOutflow extends ObjectFieldRelationnalConstraint {

  public HydroSectionOutflow(GeographicAgentGeneralisation agent,
      RelationImpl rel, double importance) {
    super(agent, rel, importance);
  }

  @Override
  public void computePriority() {
    this.setPriority(0);
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = null;

    // propose d'abord une deformation du champ relief
    actionToPropose = new FieldActivationAction(
        (IHydroSectionAgent) this.getAgent(), this, 1.0,
        ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())));
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));

    // propose ensuite une deformation du troncon
    actionToPropose = new HydroSectionDeformationAction(
        (IHydroSectionAgent) this.getAgent(), this, 2.0, 10);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));

    return actionProposals;
  }

}
