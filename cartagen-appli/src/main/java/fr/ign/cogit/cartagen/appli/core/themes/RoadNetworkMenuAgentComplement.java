package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.IRoadSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.MesoSectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.road.RoadNetworkAgent;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.spatialanalysis.measures.coalescence.CoalescenceConflictType;
import fr.ign.cogit.cartagen.spatialanalysis.measures.coalescence.LineCoalescence;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class RoadNetworkMenuAgentComplement {

    private Logger logger = Logger.getLogger(RoadNetworkMenuAgentComplement.class.getName());

    private JLabel lblAgent = new JLabel("          AGENT");

    private JMenuItem mResRoutierCharger = new JMenuItem(new LoadRoadNetAction());
    private JMenuItem mResRoutierChargerTroncons = new JMenuItem(new LoadRoadSectionsAction());
    private JMenuItem mVoirResRoutierInfos = new JMenuItem(new DisplayInfoAction());
    private JMenuItem mResRoutierRetourEtatInitial = new JMenuItem(new RestoreAction());
    public JMenuItem mResRoutierDecomposer = new JMenuItem(new DecomposeAction());
    public JMenuItem mResRoutierNettoyerDecomposition = new JMenuItem(new CleanDecompositionAction());
    public JCheckBoxMenuItem mPointsResRoutierVoir = new JCheckBoxMenuItem("Display points");
    public JCheckBoxMenuItem mSegmentsResRoutierVoir = new JCheckBoxMenuItem("Display segments");
    public JCheckBoxMenuItem mRoutierVoirSatisfactionEmpatementTroncons = new JCheckBoxMenuItem(
            "Display sections coalescence satisfaction value");
    private JMenuItem mRoutierMesoDecomposition = new JMenuItem(new MesoDecompositionAction());
    private JMenuItem mRoutierMesoRecomposition = new JMenuItem(new MesoRecompositionAction());
    private JMenuItem mRoutierDecomposition = new JMenuItem(new CoalDecompositionAction());
    private JMenuItem mRoutierMinBreak = new JMenuItem(new MinBreakAction());
    private JMenuItem mRoutierMaxBreak = new JMenuItem(new MaxBreakAction());
    private JMenuItem mRoutierGALBE = new JMenuItem(new GalbeAction());
    private JMenuItem mRoutierPlatre = new JMenuItem(new PlasterAction());
    private JMenuItem mRoutierAccordeon = new JMenuItem(new AccordionAction());
    private JMenuItem mRoutierBendRemove = new JMenuItem(new BendRemovalAction());

    public RoadNetworkMenuAgentComplement() {

        RoadNetworkMenu menu = DataThemesGUIComponent.getInstance().getRoadNetMenu();

        menu.addSeparator();
        menu.addSeparator();

        this.lblAgent.setForeground(Color.RED);
        menu.add(this.lblAgent);

        menu.addSeparator();
        menu.addSeparator();

        menu.add(this.mResRoutierCharger);
        menu.add(this.mResRoutierChargerTroncons);
        menu.add(this.mVoirResRoutierInfos);
        menu.add(this.mResRoutierRetourEtatInitial);

        menu.addSeparator();

        menu.add(this.mResRoutierDecomposer);
        menu.add(this.mResRoutierNettoyerDecomposition);

        menu.addSeparator();

        menu.add(this.mSegmentsResRoutierVoir);
        menu.add(this.mPointsResRoutierVoir);

        menu.addSeparator();

        menu.add(this.mRoutierVoirSatisfactionEmpatementTroncons);

        menu.addSeparator();

        menu.add(this.mRoutierMesoDecomposition);
        menu.add(this.mRoutierMesoRecomposition);

        menu.addSeparator();

        menu.add(this.mRoutierDecomposition);
        menu.add(this.mRoutierMinBreak);
        menu.add(this.mRoutierMaxBreak);

        menu.addSeparator();

        menu.add(this.mRoutierGALBE);
        menu.add(this.mRoutierPlatre);
        menu.add(this.mRoutierAccordeon);
        menu.add(this.mRoutierBendRemove);

    }

    private class LoadRoadNetAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            AgentGeneralisationScheduler.getInstance().initList();
            RoadNetworkMenuAgentComplement.this.logger.info("Chargement de "
                    + AgentUtil.getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()));
            AgentGeneralisationScheduler.getInstance()
                    .add(AgentUtil.getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()));
        }

        public LoadRoadNetAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Load a road network agent");
            this.putValue(Action.NAME, "Load");
        }
    }

    private class LoadRoadSectionsAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            AgentGeneralisationScheduler.getInstance().initList();
            for (IRoadLine at : CartAGenDoc.getInstance().getCurrentDataset().getRoads()) {
                RoadNetworkMenuAgentComplement.this.logger.info("Chargement de " + at);
                AgentGeneralisationScheduler.getInstance().add(AgentUtil.getAgentFromGeneObj(at));
            }
        }

        public LoadRoadSectionsAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Load the sections of a road network agent");
            this.putValue(Action.NAME, "Load Road Sections");
        }
    }

    private class DisplayInfoAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((RoadNetworkAgent) AgentUtil
                    .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()))
                            .printInfosConsole();
        }

        public DisplayInfoAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Display information on the road network agent");
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
                    ((RoadNetworkAgent) AgentUtil
                            .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()))
                                    .goBackToInitialState();
                }
            });
            th.start();
        }

        public RestoreAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Restore the road network agent to the previous state");
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
                    RoadNetworkMenuAgentComplement.this.logger.info("Decomposition de reseau routier");
                    ((RoadNetworkAgent) AgentUtil
                            .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()))
                                    .decompose();
                    RoadNetworkMenuAgentComplement.this.logger.info("Fin decomposition de reseau routier");
                }
            });
            th.start();
        }

        public DecomposeAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Decompose the road network agent into sub-micros");
            this.putValue(Action.NAME, "Decompose");
        }
    }

    private class CleanDecompositionAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    RoadNetworkMenuAgentComplement.this.logger.info("Nettoyage decomposition de reseau routier");
                    ((RoadNetworkAgent) AgentUtil
                            .getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()))
                                    .cleanDecomposition();
                    RoadNetworkMenuAgentComplement.this.logger.info("Fin nettoyage decomposition de reseau routier");
                }
            });
            th.start();
        }

        public CleanDecompositionAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Clean the decomposition of the road network agent (destroy sub-micros)");
            this.putValue(Action.NAME, "Clean Decomposition");
        }
    }

    private class MesoDecompositionAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    // recuperation de la route selectionnee
                    Set<IFeature> feats = SelectionUtil
                            .getSelectedObjects(CartAGenPlugin.getInstance().getApplication());
                    for (IFeature feat : feats) {
                        if (!(feat instanceof IRoadLine)) {
                            continue;
                        }
                        IRoadLine road = (IRoadLine) feat;

                        // Construction de l'agent meso route
                        IRoadSectionAgent roadAgent = (IRoadSectionAgent) AgentUtil.getAgentFromGeneObj(road);
                        MesoSectionAgent mesoController = new MesoSectionAgent(roadAgent);
                        road.removeFromGeneArtifacts(mesoController);

                        // dectection des parties sinueuses de la route
                        LineCoalescence coalescenceSections = new LineCoalescence(road);

                        coalescenceSections.compute();

                        ArrayList<ILineString> sectionsList = coalescenceSections.getSections();

                        // decomposition de la route meso
                        mesoController.decomposeIntoParts(sectionsList);

                        for (CoalescenceConflictType type : coalescenceSections.getCoalescenceTypes()) {
                            if (type.equals(CoalescenceConflictType.NONE)) {
                                System.out.println("none");
                            } else if (type.equals(CoalescenceConflictType.BOTH)) {
                                System.out.println("both");
                            } else if (type.equals(CoalescenceConflictType.LEFT)) {
                                System.out.println("left");
                            } else if (type.equals(CoalescenceConflictType.RIGHT)) {
                                System.out.println("right");
                            } else if (type.equals(CoalescenceConflictType.HETEROG)) {
                                System.out.println("heterog");
                            }
                        }

                    }
                }
            });
            th.start();

        }

        public MesoDecompositionAction() {
            super();
            this.putValue(Action.NAME, "Decompose meso raod in micro sections");
        }
    }

    private class MesoRecompositionAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    // recuperation de la route selectionnee
                    Set<IFeature> feats = SelectionUtil
                            .getSelectedObjects(CartAGenPlugin.getInstance().getApplication());
                    for (IFeature feat : feats) {
                        if (!(feat instanceof IRoadLine)) {
                            continue;
                        }
                        IRoadLine road = (IRoadLine) feat;

                        // Recuperation et recomposition de l'agent meso route
                        IRoadSectionAgent roadAgent = (IRoadSectionAgent) AgentUtil.getAgentFromGeneObj(road);
                        if (roadAgent.getMesoAgent() == null) {
                            continue;
                        }
                        ((MesoSectionAgent) roadAgent.getMesoAgent()).recomposeMesoSection();

                    }
                }
            });
            th.start();

        }

        public MesoRecompositionAction() {
            super();
            this.putValue(Action.NAME, "Recompose meso raod from micro sections");
        }
    }

    private class CoalDecompositionAction extends AbstractAction {

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
                        if (!(sel instanceof INetworkSection)) {
                            continue;
                        }

                        RoadNetworkMenuAgentComplement.this.logger
                                .info("Segmentation par empatement sur le troncon routier " + sel);
                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie initiale: " + ((INetworkSection) sel).getGeom());
                        }

                        LineCoalescence algo = new LineCoalescence((INetworkSection) sel);

                        algo.compute();

                        for (ILineString ls : algo.getSections()) {
                            IRoadLine road = CartAGenPlugin.getInstance().getGeneObjImpl().getCreationFactory()
                                    .createRoadLine(ls, ((INetworkSection) sel).getImportance());
                            CartAGenDoc.getInstance().getCurrentDataset().getRoads().add(road);
                        }
                        ((INetworkSection) sel).setDeleted(true);
                        RoadNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public CoalDecompositionAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Trigger coalescence decomposition on selected roads for tests");
            this.putValue(Action.NAME, "Decompose selected roads with coalescence");
        }
    }

    private class MinBreakAction extends AbstractAction {

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
                        if (!(sel instanceof INetworkSection)) {
                            continue;
                        }

                        RoadNetworkMenuAgentComplement.this.logger
                                .info("Application de faille min au troncon routier " + sel);
                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie initiale: " + ((INetworkSection) sel).getGeom());
                        }

                        // TODO

                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie finale: " + ((INetworkSection) sel).getGeom());
                        }
                        RoadNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public MinBreakAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Trigger Min Break algorithm on selected roads for tests");
            this.putValue(Action.NAME, "Trigger Min Break");
        }
    }

    private class MaxBreakAction extends AbstractAction {

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
                        if (!(sel instanceof INetworkSection)) {
                            continue;
                        }

                        RoadNetworkMenuAgentComplement.this.logger
                                .info("Application de faille max au troncon routier " + sel);
                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie initiale: " + ((INetworkSection) sel).getGeom());
                        }

                        // TODO

                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie finale: " + ((INetworkSection) sel).getGeom());
                        }
                        RoadNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public MaxBreakAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Trigger Max Break algorithm on selected roads for tests");
            this.putValue(Action.NAME, "Trigger Max Break");
        }
    }

    private class GalbeAction extends AbstractAction {

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
                        if (!(sel instanceof INetworkSection)) {
                            continue;
                        }

                        RoadNetworkMenuAgentComplement.this.logger
                                .info("Application de GALBE au troncon routier " + sel);
                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie initiale: " + ((INetworkSection) sel).getGeom());
                        }

                        // TODO

                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie finale: " + ((INetworkSection) sel).getGeom());
                        }
                        RoadNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public GalbeAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Trigger GALBE process on selected roads for tests");
            this.putValue(Action.NAME, "Trigger GALBE");
        }
    }

    private class PlasterAction extends AbstractAction {

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
                        if (!(sel instanceof INetworkSection)) {
                            continue;
                        }

                        RoadNetworkMenuAgentComplement.this.logger
                                .info("Application de Platre au troncon routier " + sel);
                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie initiale: " + ((INetworkSection) sel).getGeom());
                        }

                        // TODO

                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie finale: " + ((INetworkSection) sel).getGeom());
                        }
                        RoadNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public PlasterAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Trigger Plaster algorithm on selected roads for tests");
            this.putValue(Action.NAME, "Trigger Plaster");
        }
    }

    private class AccordionAction extends AbstractAction {

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
                        if (!(sel instanceof INetworkSection)) {
                            continue;
                        }

                        RoadNetworkMenuAgentComplement.this.logger
                                .info("Application de Accordeon au troncon routier " + sel);
                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie initiale: " + ((INetworkSection) sel).getGeom());
                        }

                        // TODO

                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie finale: " + ((INetworkSection) sel).getGeom());
                        }
                        RoadNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public AccordionAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Trigger Accordion algorithm on selected roads for tests");
            this.putValue(Action.NAME, "Trigger Accordion");
        }
    }

    private class BendRemovalAction extends AbstractAction {

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
                        if (!(sel instanceof INetworkSection)) {
                            continue;
                        }

                        RoadNetworkMenuAgentComplement.this.logger
                                .info("Application de suppression de virage au troncon routier " + sel);
                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie initiale: " + ((INetworkSection) sel).getGeom());
                        }

                        // TODO

                        if (RoadNetworkMenuAgentComplement.this.logger.isLoggable(Level.CONFIG)) {
                            RoadNetworkMenuAgentComplement.this.logger
                                    .config("Geometrie finale: " + ((INetworkSection) sel).getGeom());
                        }
                        RoadNetworkMenuAgentComplement.this.logger.info(" fin");
                    }
                }
            });
            th.start();
        }

        public BendRemovalAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Trigger Bend removal algorithm on selected roads for tests");
            this.putValue(Action.NAME, "Trigger Bend removal");
        }
    }

}
