package fr.ign.cogit.cartagen.appli.core.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;

public class CreateDiscreteGAELAgentsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        AgentUtil.createAgentAgentsInDataset(CartAGenDoc.getInstance().getCurrentDataset());
    }

    public CreateDiscreteGAELAgentsAction() {
        super();
        this.putValue(Action.NAME, "Create the 'discrete' GAEL agents");
    }

}
