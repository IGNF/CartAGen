package fr.ign.cogit.cartagen.appli.core.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;

public class GeneReliefGAELAgentAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(GeneReliefGAELAgentAction.class.getName());

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // initialisation
        AgentGeneralisationScheduler.getInstance().initList();
        TreeExplorationLifeCycle.getInstance().attach((AgentObserver) CartAGenPlugin.getInstance().getApplication());

        AgentGeneralisationScheduler.getInstance().initList();
        GeographicAgentGeneralisation ago = AgentUtil
                .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getReliefField());
        if (ago == null) {
            return;
        }
        logger.info("Chargement de " + ago);
        AgentGeneralisationScheduler.getInstance().add(ago);

        // run generalisation
        AgentGeneralisationScheduler.getInstance().activate();

    }

    public GeneReliefGAELAgentAction() {
        super();
        this.putValue(Action.NAME, "Run generalisation on selected agents");
    }

}
