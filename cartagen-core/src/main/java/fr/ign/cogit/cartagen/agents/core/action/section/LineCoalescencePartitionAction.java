package fr.ign.cogit.cartagen.agents.core.action.section;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.MesoComponentsActivation;
import fr.ign.cogit.cartagen.agents.core.agent.network.MesoSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.SectionAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * @author J. Renard 01/06/2010
 * 
 */
public class LineCoalescencePartitionAction
    extends MesoComponentsActivation<SectionAgent> {
  private static Logger logger = LogManager
      .getLogger(LineCoalescencePartitionAction.class.getName());

  /**
   */
  private ArrayList<ILineString> sectionsList;

  public LineCoalescencePartitionAction(MesoSectionAgent ag, Constraint cont,
      double poids, ArrayList<ILineString> sectionsList) {
    super(ag, cont, poids);
    this.sectionsList = sectionsList;
  }

  @Override
  public MesoSectionAgent getAgent() {
    if (super.getAgent() instanceof MesoSectionAgent) {
      return (MesoSectionAgent) super.getAgent();
    }
    return null;
  }

  @Override
  public ActionResult compute() throws InterruptedException {

    // Initial commit to perform later reconnection
    // try {
    // Cache.getCache().commit();
    // } catch (GothicException e1) {}

    // Decomposition of the meso agent based on coalescence
    /*
     * List<SectionAgent> sectionAgentList = new ArrayList<SectionAgent>(); for
     * (int i = 0; i < this.sectionsList.size(); i++) { ILineString ls =
     * this.sectionsList.get(i);
     * 
     * // Aggregation of the short sections (<10m) // TODO If we want to keep
     * it, should be // 1. improved: agregate with both previous and next
     * section, remove the // sections with which it has been agregated, manage
     * the case where the // road is composed of only short sections..., // 2.
     * put in the LineCoalescenceDetection instead of here. Otherwise it //
     * might generate infinite loops - according to coalescence
     * 
     * 
     * if (ls.length() < 10.0) { if (i < this.sectionsList.size() - 1) {
     * ls.coord().addAll(this.sectionsList.get(i + 1).coord());
     * this.sectionsList.set(i + 1, ls); // que se passe-t-il à i? } continue; }
     * if (i == this.sectionsList.size() - 2 && this.sectionsList.get(i +
     * 1).length() < 10.0) { ls.coord().addAll(this.sectionsList.get(i +
     * 1).coord()); }
     * 
     * // Creation of the micro agents if
     * (this.getAgent().getCorrespondingSectionMicroAgent() instanceof
     * RoadSectionAgent) { // RoadSectionAgent section = new
     * RoadSectionAgent(this.getAgent()
     * .getCorrespondingSectionMicroAgent().getNetwork(), new
     * GothicBasedRoadLine(ls, this.getAgent()
     * .getCorrespondingSectionMicroAgent().getFeature() .getImportance())); //
     * Essai Cecile juin 2012 // section.getConstraints().add(new
     * Coalescence(section, 1.0)); section.instantiateConstraints();
     * section.setTriggeredByMeso(0); sectionAgentList.add(section); } if
     * (this.getAgent().getCorrespondingSectionMicroAgent() instanceof
     * HydroSectionAgent) { HydroSectionAgent section = new
     * HydroSectionAgent(this.getAgent()
     * .getCorrespondingSectionMicroAgent().getNetwork(), new
     * GothicBasedWaterLine(ls, this.getAgent()
     * .getCorrespondingSectionMicroAgent().getFeature() .getImportance()));
     * section.getConstraints().add(new Coalescence(section, 1.0));
     * section.setTriggeredByMeso(0); sectionAgentList.add(section); }
     * 
     * }
     * 
     * // Correspondance between meso and its micros
     * this.getAgent().setComponents(sectionAgentList);
     * this.getAgent().getCorrespondingSectionMicroAgent().setDeleted(true);
     */
    MesoSectionAgent mesoSectionAgent = this.getAgent();
    mesoSectionAgent.decomposeIntoParts(this.sectionsList);

    if (LineCoalescencePartitionAction.logger.isTraceEnabled()) {
      LineCoalescencePartitionAction.logger
          .trace("Meso section decomposed into "
              + mesoSectionAgent.getComponents().size() + " micro sections");
    }

    // Usual meso life cycle to recursively generalise the micro agents
    super.compute();

    // Recomposition of the meso section after generalisation of its micro
    // sections
    mesoSectionAgent.recomposeMesoSection();

    // Tidy up
    // this.getAgent().setCorrespondingSectionMicroAgent(null);

    // We have to return a result
    return ActionResult.UNKNOWN;

  }

}
