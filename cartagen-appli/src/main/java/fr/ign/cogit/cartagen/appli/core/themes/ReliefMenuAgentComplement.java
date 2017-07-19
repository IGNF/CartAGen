package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.ReliefMenu;

public class ReliefMenuAgentComplement {

  private static Logger logger = Logger
      .getLogger(ReliefMenuAgentComplement.class.getName());

  private JLabel lblAgent = new JLabel("          AGENT");

  private JMenuItem mReliefEnrichissement = new JMenuItem(
      new EnrichReliefAction());
  private JMenuItem mReliefCharger = new JMenuItem(new LoadReliefAction());
  private JMenuItem mReliefChargerPts = new JMenuItem(new LoadPtsAction());
  private JMenuItem mVoirReliefInfos = new JMenuItem(new DisplayInfoAction());
  private JMenuItem mReliefActiverPoints = new JMenuItem(
      new PtAgentsActivateAction());
  public JCheckBoxMenuItem mSegmentsReliefVoir = new JCheckBoxMenuItem(
      "Display segments");
  public JCheckBoxMenuItem mPointsReliefVoir = new JCheckBoxMenuItem(
      "Display points");
  public JCheckBoxMenuItem mReliefSegmentsVoirTexteDistance = new JCheckBoxMenuItem(
      "Display distance text");
  public JCheckBoxMenuItem mReliefSegmentsVoirTexteEcartDistanceIni = new JCheckBoxMenuItem(
      "Display text gap between distance and initial distance");
  public JCheckBoxMenuItem mReliefTrianglesVoirTexteOrientationAzimutalePente = new JCheckBoxMenuItem(
      "Display text slope azimutale orientation (in rad within -Pi and Pi)");
  public JCheckBoxMenuItem mReliefTrianglesVoirTexteEcartOrientationAzimutalePente = new JCheckBoxMenuItem(
      "Display text gap slope azimutale orientation (in rad within -Pi and Pi)");
  public JCheckBoxMenuItem mReliefTrianglesVoirTexteEcartOrientationPourFaireCouler = new JCheckBoxMenuItem(
      "Display text ouflow orientation gap (in rad within -Pi and Pi)");
  public JCheckBoxMenuItem mReliefTrianglesVoirTexteEcartAltitudeBatiments = new JCheckBoxMenuItem(
      "Display text buidlings elevation gap");

  public ReliefMenuAgentComplement() {

    ReliefMenu menu = DataThemesGUIComponent.getInstance().getReliefMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblAgent.setForeground(Color.RED);
    menu.add(this.lblAgent);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mReliefEnrichissement);

    menu.addSeparator();

    menu.add(this.mReliefCharger);
    menu.add(this.mReliefChargerPts);
    menu.add(this.mVoirReliefInfos);

    menu.addSeparator();

    menu.add(this.mReliefActiverPoints);

    menu.addSeparator();

    menu.add(this.mSegmentsReliefVoir);
    menu.add(this.mPointsReliefVoir);

    menu.addSeparator();

    menu.add(this.mReliefSegmentsVoirTexteDistance);
    menu.add(this.mReliefSegmentsVoirTexteEcartDistanceIni);

    menu.addSeparator();

    menu.add(this.mReliefTrianglesVoirTexteOrientationAzimutalePente);
    menu.add(this.mReliefTrianglesVoirTexteEcartOrientationAzimutalePente);
    menu.add(this.mReliefTrianglesVoirTexteEcartOrientationPourFaireCouler);
    menu.add(this.mReliefTrianglesVoirTexteEcartAltitudeBatiments);

  }

  private class EnrichReliefAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      ReliefMenuAgentComplement.logger.info("enrichissement relief");
      ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
              .enrich();
    }

    public EnrichReliefAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Relief enrichment with the sub-micro object from J. Gaffuri's phd");
      this.putValue(Action.NAME, "Enrich - Create GAEL triangles");
    }
  }

  private class LoadReliefAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      AgentGeneralisationScheduler.getInstance().initList();
      ReliefMenuAgentComplement.logger
          .info("Chargement de " + AgentUtil.getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getReliefField()));
      AgentGeneralisationScheduler.getInstance()
          .add(AgentUtil.getAgentFromGeneObj(
              CartAGenDoc.getInstance().getCurrentDataset().getReliefField()));
    }

    public LoadReliefAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Load a relief agent");
      this.putValue(Action.NAME, "Load");
    }
  }

  private class LoadPtsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
              .chargerPointsNonEquilibres();
    }

    public LoadPtsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load unbalanced points in the relief agent");
      this.putValue(Action.NAME, "Load unbalanced points");
    }
  }

  private class DisplayInfoAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      System.out.println("----field info-----");
      ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
              .printInfosConsole();
      System.out.println("----end field info-----");
    }

    public DisplayInfoAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Display information on the relief agent");
      this.putValue(Action.NAME, "Display Information");
    }
  }

  private class PtAgentsActivateAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      ReliefMenuAgentComplement.logger
          .severe("activation des agents point du champ relief");
      for (IPointAgent ap : ((ReliefFieldAgent) AgentUtil.getAgentFromGeneObj(
          CartAGenDoc.getInstance().getCurrentDataset().getReliefField()))
              .getPointAgents()) {
        new Thread(ap).start();
      }
      ReliefMenuAgentComplement.logger
          .severe("fin activation des agents point du champ relief");
    }

    public PtAgentsActivateAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Activate the point agents of the relief agent");
      this.putValue(Action.NAME, "Point Agents Activation");
    }
  }

}
