package fr.ign.cogit.cartagen.appli.core.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.algorithms.block.BuildingsAggregation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;

public class BuildingAmalgamationAction extends AbstractAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void actionPerformed(ActionEvent e) {
    Collection<IBuilding> buildings = new HashSet<>();
    for (IFeature feat : SelectionUtil
        .getSelectedObjects(CartAGenPlugin.getInstance().getApplication()))
      if (feat instanceof IBuilding)
        buildings.add((IBuilding) feat);
    String bufferSize = JOptionPane
        .showInputDialog("Enter the buffer size in meters");
    String edgeLength = JOptionPane
        .showInputDialog("Enter the minimum edge length in meters");
    Collection<IBuilding> outputBuildings = BuildingsAggregation
        .computeMorphologicalAmalgamation(buildings, new Double(bufferSize),
            new Double(edgeLength));
    System.out
        .println(outputBuildings.size() + " buildings after amalgamation");

    // display the output in the geometry pool
    CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        .setSld(CartAGenPlugin.getInstance().getApplication().getMainFrame()
            .getSelectedProjectFrame().getSld());
    for (IBuilding building : outputBuildings)
      CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
          .addFeatureToGeometryPool(building.getGeom(), Color.RED, 2);
  }

  public BuildingAmalgamationAction() {
    this.putValue(Action.SHORT_DESCRIPTION,
        "Trigger the amalgamation of a set of close buildings with the algorithm from (Damen et al. 2008)");
    this.putValue(Action.NAME,
        "Trigger the amalgamation of a set of close buildings");
  }
}
