package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.UrbanAlignmentMenu;

public class UrbanAlignmentMenuAgentComplement {

  private JLabel lblAgent = new JLabel("          AGENT");

  private JMenuItem mGeneraliseBuildings = new JMenuItem(
      new GeneraliseBuildingsAction());

  public UrbanAlignmentMenuAgentComplement() {

    UrbanAlignmentMenu menu = DataThemesGUIComponent.getInstance()
        .getUrbanAlignmentMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblAgent.setForeground(Color.RED);
    menu.add(this.lblAgent);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mGeneraliseBuildings);

  }

  private class GeneraliseBuildingsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (sel instanceof IUrbanAlignment) {
              IUrbanAlignment align = (IUrbanAlignment) sel;
              for (IUrbanElement build : align.getUrbanElements()) {
                GeographicAgentGeneralisation ab = AgentUtil
                    .getAgentFromGeneObj(build);
                if (ab != null) {
                  try {
                    ab.activate();
                  } catch (InterruptedException e) {
                  }
                }
              }
            }
          }
        }
      });
      th.start();
    }

    public GeneraliseBuildingsAction() {
      super();
      this.putValue(Action.NAME, "Generalisation of individual buildings");
    }

  }

}
