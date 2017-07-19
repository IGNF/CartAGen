package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.ITownAgent;
import fr.ign.cogit.cartagen.algorithms.network.roads.EliminateTownDeadEnds;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.defaultschema.urban.UrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.TownMenu;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class TownMenuCogitComplement extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private JLabel lblCogit = new JLabel("          COGIT");

  public JMenuItem mSuppressionImpasses = new JMenuItem(
      new DeleteDeadEndsAction());
  private JMenuItem mAggrBlocks = new JMenuItem(new AggregateBlocksAction());
  private JMenuItem mTownStreetSelection = new JMenuItem(
      new StreetSelectionAction());
  private TownMenu menu;

  public TownMenuCogitComplement() {

    menu = DataThemesGUIComponent.getInstance().getTownMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblCogit.setForeground(Color.RED);
    menu.add(this.lblCogit);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mSuppressionImpasses);
    menu.add(this.mAggrBlocks);
    menu.add(this.mTownStreetSelection);

  }

  private class DeleteDeadEndsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      IFeatureCollection<IFeature> selection = new FT_FeatureCollection<IFeature>();
      selection.addAll(SelectionUtil.getSelectedObjects(menu.getApplication()));
      for (IFeature sel : selection) {
        if (!(sel instanceof ITown)) {
          continue;
        }
        ITown town = (ITown) sel;
        // town.getStreetNetwork().limitedAggregationAlgorithm();
        EliminateTownDeadEnds algo = new EliminateTownDeadEnds(
            town.getDeadEnds(), town.getStreetNetwork(),
            GeneralisationSpecifications.ROADS_DEADEND_MIN_LENGTH);
        algo.execute();
      }
    }

    public DeleteDeadEndsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger the dead end selecrion algorithm using dead endsd min length");
      this.putValue(Action.NAME, "Delete short dead ends");
    }
  }

  private class AggregateBlocksAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      List<IFeature> sel = SelectionUtil
          .getListOfSelectedObjects(menu.getApplication());
      if (sel.size() != 2) {
        JOptionPane.showMessageDialog(
            menu.getApplication().getMainFrame().getGui(),
            "Please select two city blocks");
        return;
      }
      if (!(sel.get(0) instanceof UrbanBlock)
          || !(sel.get(1) instanceof UrbanBlock)) {
        JOptionPane.showMessageDialog(
            menu.getApplication().getMainFrame().getGui(),
            "These are not city blocks");
        return;
      }
      UrbanBlock block1 = (UrbanBlock) sel.get(0);
      UrbanBlock block2 = (UrbanBlock) sel.get(1);
      block1.aggregateWithBlock(block2);

      SelectionUtil.clearSelection(menu.getApplication());
      ;
    }

    public AggregateBlocksAction() {
      super();
      this.putValue(Action.NAME, "Aggregate two urban blocks");
    }
  }

  private class StreetSelectionAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Set<IFeature> sel = SelectionUtil
          .getSelectedObjects(menu.getApplication());
      for (IFeature feat : sel) {
        if (!(feat instanceof ITown)) {
          continue;
        }
        ITown city = (ITown) feat;
        ITownAgent cityAgent = (ITownAgent) AgentUtil.getAgentFromGeneObj(city);
        city.getStreetNetwork().limitedAggregationAlgorithm();
        cityAgent.updateBlocks();
      }
    }

    public StreetSelectionAction() {
      super();
      this.putValue(Action.NAME, "Eliminate streets in the town network");
    }
  }

}
