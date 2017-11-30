package fr.ign.cogit.cartagen.appli.core.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;

public class RunOnSelectionAction extends AbstractAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger logger = Logger
      .getLogger(RunOnSelectionAction.class.getName());

  @Override
  public void actionPerformed(ActionEvent arg0) {
    // initialisation
    AgentGeneralisationScheduler.getInstance().initList();

    // load the agents related to the selected features
    Collection<IFeature> features = new HashSet<>();
    features.addAll(SelectionUtil
        .getSelectedObjects(CartAGenPlugin.getInstance().getApplication()));
    SelectionUtil.clearSelection(CartAGenPlugin.getInstance().getApplication());

    TreeExplorationLifeCycle.getInstance()
        .attach((AgentObserver) CartAGenPlugin.getInstance().getApplication());

    for (IFeature obj : features) {
      if (!(obj instanceof IGeneObj)) {
        continue;
      }
      AgentGeneralisationScheduler.getInstance().initList();
      GeographicAgentGeneralisation ago = AgentUtil
          .getAgentFromGeneObj((IGeneObj) obj);
      if (ago == null) {
        continue;
      }
      logger.info("Chargement de " + ago);
      AgentGeneralisationScheduler.getInstance().add(ago);

      // run generalisation
      AgentGeneralisationScheduler.getInstance().activate();
    }

  }

  public RunOnSelectionAction() {
    super();
    this.putValue(Action.NAME, "Run generalisation on selected agents");
  }

}
