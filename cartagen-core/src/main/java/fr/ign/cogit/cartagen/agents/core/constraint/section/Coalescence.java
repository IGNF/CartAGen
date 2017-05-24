package fr.ign.cogit.cartagen.agents.core.constraint.section;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.section.CurvatureSmoothingAction;
import fr.ign.cogit.cartagen.agents.core.action.section.LineCoalescencePartitionAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.MesoSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.spatialanalysis.measures.coalescence.CoalescenceConflictType;
import fr.ign.cogit.cartagen.spatialanalysis.measures.coalescence.LineCoalescence;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

public class Coalescence extends GeographicObjectConstraintImpl {

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(Coalescence.class.getName());

  /**
   */
  double empatement;

  public Coalescence(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computePriority() {
    this.setPriority(1);
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    if (this.empatement >= 0.5) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(1 + (int) ((1.0 - 2 * this.empatement) * 100));
    }
    if (this.getSatisfaction() > 100.0) {
      this.setSatisfaction(100.0);
    }
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computeCurrentValue() {
    this.empatement = SectionSymbol
        .getCoalescence(((ISectionAgent) this.getAgent()).getFeature());
  }

  @Override
  // TODO Les actions proposées ne correspondent pas à la version initiale du
  // prototype AGENT. Voir si ça marcherait mieux en réintroduisant le
  // 'potential bulk' (emprise potentielle évaluée si on garde tous les
  // virages). Il faudrait aussi réintroduire une contrainte de densité de
  // l'environnement qui rétrograde les actions gourmandes en place (accordion
  // et max break).
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = null;

    // Partition of the micro agent based on coalescence
    LineCoalescence coalescenceSections = new LineCoalescence(
        ((ISectionAgent) this.getAgent()).getFeature());

    coalescenceSections.compute();

    ArrayList<ILineString> sectionsList = coalescenceSections.getSections();

    // Case where the micro section is partitioned in only one section ->
    // independant algorithms
    if (sectionsList.size() == 1) {
      ArrayList<CoalescenceConflictType> coalescencesList = coalescenceSections
          .getCoalescenceTypes();
      // Both sides conflicts => Actions relevant for bend series
      if (coalescencesList.get(0) == CoalescenceConflictType.BOTH) {
        // FIXME works only with Clarity
        /*
         * // Bend removal, weight 10.0 actionToPropose = new
         * GothicBendRemoveAction( (ISectionAgent) this.getAgent(), this, 10.0);
         * actionProposals .add(new ActionProposal(this, true, actionToPropose,
         * 10.0)); // Accordion, weight 5.0 actionToPropose = new
         * GothicAccordionAction( (ISectionAgent) this.getAgent(), this, 5.0);
         * actionProposals .add(new ActionProposal(this, true, actionToPropose,
         * 5.0)); // Plaster, weight 1.0 // actionToPropose = new
         * PlasterAction((SectionAgent) this.getAgent(), // this, 1.0); //
         * actionProposals // .add(new ActionProposal(this, true,
         * actionToPropose, 1.0));
         */
      }

      // One side conflict => Actions relevant for isolated bend
      else if (coalescencesList.get(0) == CoalescenceConflictType.RIGHT
          || coalescencesList.get(0) == CoalescenceConflictType.LEFT) {
        // FIXME works only with Clarity
        /*
         * // Max break, weight 10.0 actionToPropose = new GothicMaxBreakAction(
         * (ISectionAgent) this.getAgent(), this, 10.0); actionProposals
         * .add(new ActionProposal(this, true, actionToPropose, 10.0)); // Min
         * break, weight 5.0 actionToPropose = new GothicMinBreakAction(
         * (ISectionAgent) this.getAgent(), this, 5.0); actionProposals .add(new
         * ActionProposal(this, true, actionToPropose, 8.0)); // Plaster, weight
         * 1.0 // actionToPropose = new PlasterAction((SectionAgent)
         * this.getAgent(), // this, 1.0); // actionProposals // .add(new
         * ActionProposal(this, true, actionToPropose, 1.0));
         */
      }
      // No conflict => curvature smoothing
      else if (coalescencesList.get(0) == CoalescenceConflictType.NONE) {
        // curvature smoothing, weight 10.0
        actionToPropose = new CurvatureSmoothingAction(
            (ISectionAgent) this.getAgent(), this, 10.0);
        actionProposals
            .add(new ActionProposal(this, true, actionToPropose, 10.0));
      }
    }

    // Case where the micro section is partitioned in multiple sections ->
    // partition of
    // the line and recursivity
    else if (sectionsList.size() > 1) {
      MesoSectionAgent mesoController = new MesoSectionAgent(
          (SectionAgent) this.getAgent());
      ((IGeneObj) this.getAgent().getFeature())
          .removeFromGeneArtifacts(mesoController);
      // Partition of the line and recursivity, weight 10.0
      actionToPropose = new LineCoalescencePartitionAction(mesoController, this,
          10.0, sectionsList);
      actionProposals
          .add(new ActionProposal(this, true, actionToPropose, 10.0));
      // Plaster, weight 1.0
      // actionToPropose = new PlasterAction((SectionAgent) this.getAgent(),
      // this,
      // 1.0);
      // actionProposals.add(new ActionProposal(this, true, actionToPropose,
      // 1.0));
    }

    return actionProposals;
  }

}
