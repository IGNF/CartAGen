package fr.ign.cogit.cartagen.appli.plugins.vgi;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.gui.HarmonisationFrame;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class OSMCartAGenPlugin extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public OSMCartAGenPlugin(String title) {
    super(title);

    JMenu harmoniseMenu = new JMenu("LoD Harmonisation");
    harmoniseMenu.add(new JMenuItem(new HarmonisationFrameAction()));
    this.add(harmoniseMenu);
  }

  /**
   * Launch the frame that triggers LoD harmonisation processes.
   * 
   * @author GTouya
   * 
   */
  class HarmonisationFrameAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      StyledLayerDescriptor sld = CartAGenPlugin.getInstance().getApplication()
          .getMainFrame().getSelectedProjectFrame().getSld();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(sld);
      CartAGenDoc.getInstance().getCurrentDataset().setSld(sld);
      HarmonisationFrame frame = new HarmonisationFrame(SelectionUtil
          .getAllWindowObjects(CartAGenPlugin.getInstance().getApplication()),
          pool);
      frame.setVisible(true);
    }

    public HarmonisationFrameAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Launch the harmonisation frame");
      this.putValue(Action.NAME, "Launch harmonisation");
    }
  }

}
