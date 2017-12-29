package fr.ign.cogit.cartagen.appli.core.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;

public class CreateReliefGAELAgentAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // load specifications
        AgentUtil.loadAgentSpecifications(AgentUtil.getConfigAgentFile());

        // Relief elements
        AgentUtil.createReliefGaelAgentsInDataset(CartAGenDoc.getInstance().getCurrentDataset());
        AgentUtil.enrichGaelAgents(CartAGenDoc.getInstance().getCurrentDataset());

    }

    public CreateReliefGAELAgentAction() {
        super();
        this.putValue(Action.NAME, "Create the relief GAEL agents");
    }

}
