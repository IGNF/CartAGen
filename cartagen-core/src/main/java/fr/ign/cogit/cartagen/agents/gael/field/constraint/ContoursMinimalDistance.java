/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.field.constraint;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.action.DecomposedObjectDeformationAction;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ContourLineAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;

/**
 * @author JGaffuri
 * 
 */
public class ContoursMinimalDistance extends GeographicObjectConstraintImpl {
  @Override
  public void computeCurrentValue() {
  }

  @Override
  public void computeGoalValue() {
  }

  @Override
  public void computePriority() {
  }

  public ContoursMinimalDistance(GeographicAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computeSatisfaction() {
    // a verifier depuis passage a geoxygene

    GM_Aggregate<IPolygon> agg = new GM_Aggregate<IPolygon>();
    double sAire = 0.0;
    for (IContourLine obj : CartAGenDoc.getInstance().getCurrentDataset()
        .getReliefField().getContourLines()) {
      ContourLineAgent cn = (ContourLineAgent) AgentUtil
          .getAgentFromGeneObj(obj);
      IPolygon emprise = (IPolygon) cn.getSymbolExtent().buffer(
          0.5 * GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_CN
              * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
      sAire += emprise.area();
      agg.add(emprise);
    }
    IGeometry union = agg.buffer(0);
    double tauxSuperposition = union.area() / sAire;
    // ts vaut 1 lorsque c'est parfait et est plus petit sinon
    // System.out.println(tauxSuperposition);
    this.setSatisfaction(100 * tauxSuperposition * tauxSuperposition);
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();
    Action actionToPropose = new DecomposedObjectDeformationAction(
        this.getAgent(), this, 1.0);
    actionProposals.add(new ActionProposal(this, true, actionToPropose, 1.0));
    return actionProposals;
  }

}
