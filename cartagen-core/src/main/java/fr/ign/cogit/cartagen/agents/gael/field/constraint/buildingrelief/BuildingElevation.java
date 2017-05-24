package fr.ign.cogit.cartagen.agents.gael.field.constraint.buildingrelief;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.action.micro.DisplacementAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.IBuildingAgent;
import fr.ign.cogit.cartagen.agents.gael.field.action.FieldActivationAction;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.ObjectFieldRelationnalConstraint;
import fr.ign.cogit.cartagen.agents.gael.field.relation.buildingfield.BuildingElevationRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.relation.RelationImpl;

public class BuildingElevation extends ObjectFieldRelationnalConstraint {

  @Override
  public IBuildingAgent getAgent() {
    return (IBuildingAgent) super.getAgent();
  }

  public BuildingElevation(GeographicAgentGeneralisation ag, RelationImpl rel,
      double importance) {
    super(ag, rel, importance);
  }

  public double getValeurCourante() {
    return ((BuildingElevationRelation) this.getRelation()).getValeurCourante();
  }

  public double getValeurInitiale() {
    return ((BuildingElevationRelation) this.getRelation()).getValeurInitiale();
  }

  @Override
  public void computePriority() {
    this.setPriority(0);
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = null;

    // propose d'abord la deformation du champ
    actionToPropose = new FieldActivationAction(
        (GeographicObjectAgentGeneralisation) this.getAgent(), this, 1.0,
        ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
            CartAGenDoc.getInstance().getCurrentDataset().getReliefField())));
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));

    // propose ensuite un deplacement du batiment
    double dx, dy;
    // recupere le vecteur pente
    double[] p = ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
        CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
            .getVecteurPente(this.getAgent().getPosition());
    // si l'altitude n'est pas definie, sortir
    if (p == null) {
      return actionProposals;
    }
    // si la pente est horizontale (ou presque), sortir
    if (p[2] > 0.99) {
      return actionProposals;
    }
    double dz = this.getValeurCourante() - this.getValeurInitiale();
    double anglePente = ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
        CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
            .getZenitalOrientation(this.getAgent().getPosition());
    double tan = Math.tan(anglePente);
    double d = Math.sqrt(p[0] * p[0] + p[1] * p[1]);
    dx = -(dz / tan) * p[0] / d;
    dy = -(dz / tan) * p[1] / d;

    actionToPropose = new DisplacementAction(this.getAgent(), this, 2.0, dx,
        dy);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));
    actionToPropose = new DisplacementAction(this.getAgent(), this, 2.0, dx / 2,
        dy / 2);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));
    actionToPropose = new DisplacementAction(this.getAgent(), this, 2.0, dx / 4,
        dy / 4);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 2.0));

    return actionProposals;
  }

}
