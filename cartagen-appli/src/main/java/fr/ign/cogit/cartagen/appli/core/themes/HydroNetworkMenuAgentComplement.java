package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IHydroSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroNetworkAgent;
import fr.ign.cogit.cartagen.agents.gael.field.action.HydroSectionDeformationAction;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

public class HydroNetworkMenuAgentComplement {

    private Logger logger = Logger.getLogger(HydroNetworkMenuAgentComplement.class.getName());

    private JLabel lblAgent = new JLabel("          AGENT");

    private JMenuItem mResHydroCharger = new JMenuItem(new LoadHydroNetAction());
    private JMenuItem mResHydroChargerTroncons = new JMenuItem(new LoadHydroSectionsAction());
    private JMenuItem mVoirResHydroInfos = new JMenuItem(new DisplayInfoAction());
    private JMenuItem mResHydroRetourEtatInitial = new JMenuItem(new RestoreAction());
    public JCheckBoxMenuItem mSegmentsResHydroVoir = new JCheckBoxMenuItem("Display segments");
    public JCheckBoxMenuItem mPointsResHydroVoir = new JCheckBoxMenuItem("Display points");
    public JCheckBoxMenuItem mVoirSatisfactionProximiteRoutier = new JCheckBoxMenuItem(
            "Voir satisfaction proximite routier");
    public JCheckBoxMenuItem mVoirTronconHydroSatisfactionEcoulement = new JCheckBoxMenuItem(
            "Voir satisfaction troncons ecoulement");
    public JCheckBoxMenuItem mVoirResHydroEcoulement = new JCheckBoxMenuItem("Voir reseau hydro indicateur ecoulement");
    public JCheckBoxMenuItem mVoirResHydroIndicateurEcoulement = new JCheckBoxMenuItem(
            "Voir reseau hydro texte indicateur ecoulement");
    public JCheckBoxMenuItem mVoirIntersectionReseauHydrographique = new JCheckBoxMenuItem(
            "Voir triplets intersectant reseau hydrographique");
    public JCheckBoxMenuItem mVoirReliefEcoulement = new JCheckBoxMenuItem("Voir relief indicateur ecoulement");
    public JCheckBoxMenuItem mVoirReliefIndicateurEcoulement = new JCheckBoxMenuItem(
            "Voir relief texte indicateur ecoulement");
    public JCheckBoxMenuItem mHydroSegmentsVoirTexteOrientation = new JCheckBoxMenuItem(
            "Voir texte orientation (en rad entre -Pi et Pi)");
    public JCheckBoxMenuItem mHydroSegmentsVoirTexteEcartOrientationPente = new JCheckBoxMenuItem(
            "Voir texte ecart orientation pente (en rad entre -Pi et Pi)");
    public JCheckBoxMenuItem mHydroSegmentsVoirTexteEcartOrientationPentePourEtrePlat = new JCheckBoxMenuItem(
            "Voir texte ecart orientation pour etre plat (en rad entre -Pi/2 et Pi/2)");
    public JCheckBoxMenuItem mHydroSegmentsVoirTexteAnglePente = new JCheckBoxMenuItem(
            "Voir texte angle pente (en rad entre -Pi/2 et Pi/2)");
    private JMenuItem mResHydroDecomposition = new JMenuItem(new DecomposeAction());
    private JMenuItem mResHydroNettoyageDecomposition = new JMenuItem(new CleanDecompositionAction());
    private JMenuItem mTronconsHydroDeformation = new JMenuItem(new DeformSectionsAction());

    public HydroNetworkMenuAgentComplement() {

        HydroNetworkMenu menu = DataThemesGUIComponent.getInstance().getHydroNetMenu();

        menu.addSeparator();
        menu.addSeparator();

        this.lblAgent.setForeground(Color.RED);
        menu.add(this.lblAgent);

        menu.addSeparator();
        menu.addSeparator();

        menu.add(this.mResHydroCharger);
        menu.add(this.mResHydroChargerTroncons);
        menu.add(this.mVoirResHydroInfos);
        menu.add(this.mResHydroRetourEtatInitial);

        menu.addSeparator();

        menu.add(this.mSegmentsResHydroVoir);
        menu.add(this.mPointsResHydroVoir);

        menu.addSeparator();

        menu.add(this.mVoirSatisfactionProximiteRoutier);

        menu.addSeparator();

        menu.add(this.mVoirTronconHydroSatisfactionEcoulement);
        menu.add(this.mVoirResHydroEcoulement);
        menu.add(this.mVoirResHydroIndicateurEcoulement);

        menu.addSeparator();

        menu.add(this.mVoirIntersectionReseauHydrographique);
        menu.add(this.mVoirReliefEcoulement);
        menu.add(this.mVoirReliefIndicateurEcoulement);
        menu.add(this.mHydroSegmentsVoirTexteOrientation);
        menu.add(this.mHydroSegmentsVoirTexteEcartOrientationPente);
        menu.add(this.mHydroSegmentsVoirTexteEcartOrientationPentePourEtrePlat);
        menu.add(this.mHydroSegmentsVoirTexteAnglePente);

        menu.addSeparator();

        menu.add(this.mResHydroDecomposition);
        menu.add(this.mResHydroNettoyageDecomposition);
        menu.add(this.mTronconsHydroDeformation);

    }

    private class LoadHydroNetAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            AgentGeneralisationScheduler.getInstance().initList();
            HydroNetworkMenuAgentComplement.this.logger.info("Chargement de "
                    + AgentUtil.getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()));
            AgentGeneralisationScheduler.getInstance().add(
                    AgentUtil.getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()));
        }

        public LoadHydroNetAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Load a hydro network agent");
            this.putValue(Action.NAME, "Load");
        }
    }

    private class LoadHydroSectionsAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            AgentGeneralisationScheduler.getInstance().initList();
            for (IWaterLine at : CartAGenDoc.getInstance().getCurrentDataset().getWaterLines()) {
                HydroNetworkMenuAgentComplement.this.logger.info("Chargement de " + at);
                AgentGeneralisationScheduler.getInstance().add(AgentUtil.getAgentFromGeneObj(at));
            }
        }

        public LoadHydroSectionsAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Load the sections of a hydro network agent");
            this.putValue(Action.NAME, "Load Hydro Sections");
        }
    }

    private class DisplayInfoAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((HydroNetworkAgent) AgentUtil
                    .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()))
                            .printInfosConsole();
        }

        public DisplayInfoAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Display information on the hydro network agent");
            this.putValue(Action.NAME, "Display Information");
        }
    }

    private class RestoreAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    ((HydroNetworkAgent) AgentUtil
                            .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()))
                                    .goBackToInitialState();
                }
            });
            th.start();
        }

        public RestoreAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Restore the hydro network agent to the previous state");
            this.putValue(Action.NAME, "Restore");
        }
    }

    private class DecomposeAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    HydroNetworkMenuAgentComplement.this.logger.info("Decomposition de reseau hydro");
                    ((HydroNetworkAgent) AgentUtil
                            .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()))
                                    .decompose();
                    HydroNetworkMenuAgentComplement.this.logger.info("Fin decomposition de reseau hydro");
                }
            });
            th.start();
        }

        public DecomposeAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Decompose the hydro network agent into sub-micro objects");
            this.putValue(Action.NAME, "Decompose Hydro network");
        }
    }

    private class CleanDecompositionAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    HydroNetworkMenuAgentComplement.this.logger.info("Nettoyage decomposition de reseau hydro");
                    ((HydroNetworkAgent) AgentUtil
                            .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()))
                                    .cleanDecomposition();
                    HydroNetworkMenuAgentComplement.this.logger.info("Fin nettoyage decomposition de reseau hydro");
                }
            });
            th.start();
        }

        public CleanDecompositionAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Clean the decomposition of the hydro network agent (destroy sub-micro objects)");
            this.putValue(Action.NAME, "Clean Decomposition");
        }
    }

    private class DeformSectionsAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    for (IFeature sel : SelectionUtil
                            .getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
                        if (sel.isDeleted()) {
                            continue;
                        }
                        if (!(sel instanceof IWaterLine)) {
                            continue;
                        }
                        IHydroSectionAgent tr = (IHydroSectionAgent) AgentUtil.getAgentFromGeneObj((IWaterLine) sel);
                        HydroNetworkMenuAgentComplement.this.logger.info("Deformation de " + tr);
                        if (HydroNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            HydroNetworkMenuAgentComplement.this.logger.config("Geometrie initiale: " + tr.getGeom());
                        }
                        try {
                            new HydroSectionDeformationAction(tr, null, 0.0, 20).compute();
                        } catch (InterruptedException exc) {
                        }
                        if (HydroNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            HydroNetworkMenuAgentComplement.this.logger.config("Geometrie finale: " + tr.getGeom());
                        }
                        HydroNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public DeformSectionsAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Deform the hydro sections with the GAEL model");
            this.putValue(Action.NAME, "Deform hydro sections");
        }
    }

}
