package fr.ign.cogit.cartagen.appli.plugins.process;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.ign.cogit.cartagen.appli.collagen.EditFormalConstraintsFrame;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.BuildingGroupSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.FlexiGraphSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.RiverAreasSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.RoadNetworkSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.RuralSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.UrbanSpace;
import fr.ign.cogit.cartagen.collagen.geospaces.spaces.VegetationSpace;
import fr.ign.cogit.cartagen.collagen.processes.implementation.CartAComProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.CrossroadCollapseProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.ForestGeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.PushNetworksProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.RiverAreasGeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.RuralAGENTProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.RuralRoadSelectionProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.UrbanAGENTProcess;
import fr.ign.cogit.cartagen.collagen.processes.implementation.UrbanLeastSquaresProcess;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.core.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph.MinimumSeparation;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanAreaComputationJTS;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.ontology.owl.OwlUtil;

public class CollaGenComponent extends JMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static CollaGenComponent instance = null;

    private CollaGenEnvironment environment;
    public static String ONTOLOGY = "OntoGeneralisation";
    private static Logger LOGGER = Logger.getLogger(CollaGenComponent.class);

    public CollaGenComponent() {
        // Exists only to defeat instantiation.
        super();
        // this.setEnvironment(CollaGenEnvironment.getInstance());
    }

    public static CollaGenComponent getInstance() {
        if (CollaGenComponent.instance == null) {
            CollaGenComponent.instance = new CollaGenComponent("CollaGen");
        }
        return CollaGenComponent.instance;
    }

    public CollaGenComponent(String title) {
        super(title);
        CollaGenComponent.instance = this;
        // this.setEnvironment(CollaGenEnvironment.getInstance());

        JMenu resMenu = new JMenu("Resources");
        JMenu constrMenu = new JMenu("Formal Constraints");
        resMenu.add(constrMenu);
        constrMenu.add(new JMenuItem(new NewConstraintsAction()));
        constrMenu.add(new JMenuItem(new EditConstraintsAction()));
        JMenu descrMenu = new JMenu("Formal Descriptions");
        resMenu.add(descrMenu);

        JMenu compMenu = new JMenu("Component tests");

        JMenu spaceMenu = new JMenu("Geographic Spaces");
        spaceMenu.add(new JMenuItem(new CreateUrbanSpacesAction()));
        spaceMenu.add(new JMenuItem(new CreateRuralSpacesAction()));
        spaceMenu.add(new JMenuItem(new CreateRiverAreaSpaceAction()));
        spaceMenu.add(new JMenuItem(new CreateRoadsSpaceAction()));
        spaceMenu.add(new JMenuItem(new CreateVegetSpaceAction()));
        spaceMenu.add(new JMenuItem(new CreateFlexiGraphSpaceAction()));
        spaceMenu.add(new JMenuItem(new CreateBuildGroupSpaceAction()));

        JMenu geneMenu = new JMenu("Generalisation");
        geneMenu.add(new JMenuItem(new RunUrbanAgentAction()));
        geneMenu.add(new JMenuItem(new RunCartAComAction()));
        geneMenu.add(new JMenuItem(new RunRiverAreaGenAction()));
        geneMenu.add(new JMenuItem(new RunForestGenAction()));
        geneMenu.add(new JMenuItem(new RunRuralRoadSelAction()));
        geneMenu.add(new JMenuItem(new RunPushNetworksAction()));
        geneMenu.add(new JMenuItem(new RunUrbanLSAAction()));
        geneMenu.add(new JMenuItem(new RunCrossRoadCollapseAction()));
        geneMenu.add(new JMenuItem(new RunRuralAgentAction()));
        geneMenu.add(new JMenuItem(new RunUrbanAgentNoRoadAction()));
        geneMenu.add(new JMenuItem(new RunUrbanAgentNoGrayingAction()));
        this.add(resMenu);
        this.add(compMenu);
        this.add(spaceMenu);
        this.add(geneMenu);
    }

    public CollaGenEnvironment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(CollaGenEnvironment environment) {
        this.environment = environment;
    }

    class NewConstraintsAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            OWLOntology onto = null;
            try {
                onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
            }
            String dbName = JOptionPane.showInputDialog("Enter database name");
            EditFormalConstraintsFrame frame = new EditFormalConstraintsFrame(onto, dbName, dbName);
            frame.setVisible(true);
        }

        public NewConstraintsAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION, "Edit New Formal Constraints Database");
            this.putValue(Action.NAME, "New Constraints Database");
        }
    }

    class EditConstraintsAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            OWLOntology onto = null;
            try {
                onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
            }
            EditFormalConstraintsFrame frame = new EditFormalConstraintsFrame(onto, "", "");
            frame.getLoadXMLConstraintsAction().actionPerformed(null);
            frame.getLoadXMLRulesAction().actionPerformed(null);
            frame.setVisible(true);
        }

        public EditConstraintsAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION, "Edit Formal Constraints Database Stored in XML");
            this.putValue(Action.NAME, "Edit Constraints Database");
        }
    }

    class CreateUrbanSpacesAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<IGeometry> geoms = new ArrayList<>();
            for (IFeature building : CartAGenDoc.getInstance().getCurrentDataset().getBuildings()) {
                geoms.add(building.getGeom());
            }

            IGeometry complex = UrbanAreaComputationJTS.calculTacheUrbaine(geoms, 25.0, 10.0, 12, 5.0, 1000.0);
            Conductor conductor = Conductor.getInstance();

            if (complex instanceof IPolygon)
                conductor.addGeoSpace(new UrbanSpace((IPolygon) complex));
            else if (complex instanceof IMultiSurface<?>) {
                for (IPolygon simple : ((IMultiSurface<IPolygon>) complex)) {
                    if (simple == null)
                        continue;
                    if (simple.area() < 400000.0)
                        continue;
                    conductor.addGeoSpace(new UrbanSpace(simple));
                }
            }

            // put the urban areas in a new layer
            ProjectFrame project = CartAGenPlugin.getInstance().getApplication().getMainFrame()
                    .getSelectedProjectFrame();
            project.addUserLayer(conductor.getGeoSpaces(), Conductor.GEO_SPACE_LAYER, null);
        }

        public CreateUrbanSpacesAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Create urban spaces and add a geographic spaces layer if it does not exist");
            this.putValue(Action.NAME, "Create urban spaces");
        }
    }

    class RunUrbanAgentAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeature selected = SelectionUtil.getFirstSelectedObject(CartAGenPlugin.getInstance().getApplication());
            SelectionUtil.clearSelection(CartAGenPlugin.getInstance().getApplication());
            if (selected instanceof GeographicSpace) {
                GeographicSpace space = (GeographicSpace) selected;
                Conductor chefO = Conductor.getInstance();
                AgentObserver observer = (AgentObserver) CartAGenPlugin.getInstance().getApplication();
                observer.setSlowMotion(true);
                UrbanAGENTProcess process = new UrbanAGENTProcess(chefO, observer);
                process.runOnGeoSpace(space);
            }
        }

        public RunUrbanAgentAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION, "Run the Urban AGENT process on the selected UrbanSpace instances");
            this.putValue(Action.NAME, "Run the Urban AGENT process on selection");
        }
    }

    class RunUrbanAgentNoRoadAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeature selected = SelectionUtil.getFirstSelectedObject(CartAGenPlugin.getInstance().getApplication());
            SelectionUtil.clearSelection(CartAGenPlugin.getInstance().getApplication());
            if (selected instanceof GeographicSpace) {
                GeographicSpace space = (GeographicSpace) selected;
                Conductor chefO = Conductor.getInstance();
                AgentObserver observer = (AgentObserver) CartAGenPlugin.getInstance().getApplication();
                observer.setSlowMotion(true);
                UrbanAGENTProcess process = new UrbanAGENTProcess(chefO, observer);
                process.setGeneraliseRoads(false);
                process.runOnGeoSpace(space);
            }
        }

        public RunUrbanAgentNoRoadAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the Urban AGENT process on the selected UrbanSpace instances, without road selection algorithm");
            this.putValue(Action.NAME, "Run the Urban AGENT process without road selection");
        }
    }

    class RunUrbanAgentNoGrayingAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeature selected = SelectionUtil.getFirstSelectedObject(CartAGenPlugin.getInstance().getApplication());
            SelectionUtil.clearSelection(CartAGenPlugin.getInstance().getApplication());
            if (selected instanceof GeographicSpace) {
                GeographicSpace space = (GeographicSpace) selected;
                Conductor chefO = Conductor.getInstance();
                AgentObserver observer = (AgentObserver) CartAGenPlugin.getInstance().getApplication();
                observer.setSlowMotion(true);
                UrbanAGENTProcess process = new UrbanAGENTProcess(chefO, observer);
                process.setGeneraliseRoads(false);
                process.setBlockGrayingAllowed(false);
                process.runOnGeoSpace(space);
            }
        }

        public RunUrbanAgentNoGrayingAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the Urban AGENT process on the selected UrbanSpace instances, without road selection algorithm and without block graying");
            this.putValue(Action.NAME, "Run the Urban AGENT process without block graying");
        }
    }

    class RunCartAComAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            for (IFeature selected : SelectionUtil.getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
                if (selected instanceof GeographicSpace) {
                    GeographicSpace space = (GeographicSpace) selected;
                    Conductor chefO = Conductor.getInstance();
                    LOGGER.debug("centroide de l'espace rural: " + space.getGeom().centroid());
                    AgentObserver observer = (AgentObserver) CartAGenPlugin.getInstance().getApplication();
                    observer.setSlowMotion(true);
                    CartAComProcess process = new CartAComProcess(chefO, observer);
                    process.runOnGeoSpace(space);
                }
            }
        }

        public RunCartAComAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the default CartACom process on the selected RuralSpace instances");
            this.putValue(Action.NAME, "Run the default CartACom process on selected space");
        }
    }

    class RunRuralAgentAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeature selected = SelectionUtil.getFirstSelectedObject(CartAGenPlugin.getInstance().getApplication());
            SelectionUtil.clearSelection(CartAGenPlugin.getInstance().getApplication());
            if (selected instanceof GeographicSpace) {
                GeographicSpace space = (GeographicSpace) selected;
                Conductor chefO = Conductor.getInstance();
                AgentObserver observer = (AgentObserver) CartAGenPlugin.getInstance().getApplication();
                observer.setSlowMotion(true);
                RuralAGENTProcess process = new RuralAGENTProcess(chefO, observer);
                process.runOnGeoSpace(space);
            }
        }

        public RunRuralAgentAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the rural AGENT process (no town, only blocks) on the selected RuralSpace instances");
            this.putValue(Action.NAME, "Run the rural AGENT process on selected space");
        }
    }

    class RunRiverAreaGenAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor chefO = Conductor.getInstance();
            GeographicSpace space = chefO.getGeoSpacesFromConceptName("river_area_space").iterator().next();
            RiverAreasGeneralisationProcess process = new RiverAreasGeneralisationProcess(chefO);
            process.runOnGeoSpace(space);
        }

        public RunRiverAreaGenAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the River Area Generalisation process on the related thematic space instance");
            this.putValue(Action.NAME, "Run the River Area Generalisation process");
        }
    }

    class RunCrossRoadCollapseAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor chefO = Conductor.getInstance();
            GeographicSpace space = chefO.getGeoSpacesFromConceptName("réseau_routier").iterator().next();
            CrossroadCollapseProcess process = new CrossroadCollapseProcess(chefO);
            process.runOnGeoSpace(space);
        }

        public RunCrossRoadCollapseAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the process to collapse complex crossroads on the related thematic space instance");
            this.putValue(Action.NAME, "Run the process to collapse complex crossroads");
        }
    }

    class RunRuralRoadSelAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor chefO = Conductor.getInstance();
            GeographicSpace space = chefO.getGeoSpacesFromConceptName("réseau_routier").iterator().next();
            RuralRoadSelectionProcess process = new RuralRoadSelectionProcess(chefO);
            process.runOnGeoSpace(space);
        }

        public RunRuralRoadSelAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the process to select rural roads on the related thematic space instance");
            this.putValue(Action.NAME, "Run the process to select rural roads");
        }
    }

    class RunForestGenAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor chefO = Conductor.getInstance();
            GeographicSpace space = chefO.getGeoSpacesFromConceptName("couche_de_végétation").iterator().next();
            ForestGeneralisationProcess process = new ForestGeneralisationProcess(chefO);
            process.runOnGeoSpace(space);
        }

        public RunForestGenAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the process to generalise forests on the related thematic space instance");
            this.putValue(Action.NAME, "Run the process to generalise forests");
        }
    }

    class RunPushNetworksAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor chefO = Conductor.getInstance();
            GeographicSpace space = chefO.getGeoSpacesFromConceptName("graphe_de_flexibilité").iterator().next();
            PushNetworksProcess process = new PushNetworksProcess(chefO);
            Set<MinimumSeparation> minSeps = new HashSet<>();
            minSeps.add(new MinimumSeparation(IRoadLine.class, IRoadLine.class, 0.1));
            minSeps.add(new MinimumSeparation(IWaterLine.class, IRoadLine.class, 0.1));
            minSeps.add(new MinimumSeparation(IWaterLine.class, IWaterLine.class, 0.1));
            process.setMinSeps(minSeps);
            Map<IFeature, Double> symbolWidths = new HashMap<IFeature, Double>();
            for (IFeature feat : space.getInsideFeatures()) {
                double width = SLDUtilCartagen.getSymbolMaxWidthMapMm((IGeneObjLin) feat);
                symbolWidths.put(feat, width);
            }
            process.setSymbolWidth(symbolWidths);
            process.runOnGeoSpace(space);
        }

        public RunPushNetworksAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run the process to push network features by Least Squares on a FlexiGraph space instance");
            this.putValue(Action.NAME, "Run the process to push network features");
        }
    }

    class RunUrbanLSAAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor chefO = Conductor.getInstance();
            GeographicSpace space = chefO.getGeoSpacesFromConceptName("building_group_space").iterator().next();
            UrbanLeastSquaresProcess process = new UrbanLeastSquaresProcess(chefO);
            Set<MinimumSeparation> minSeps = new HashSet<>();
            minSeps.add(new MinimumSeparation(IRoadLine.class, IRoadLine.class, 0.1));
            minSeps.add(new MinimumSeparation(IBuilding.class, IRoadLine.class, 0.1));
            minSeps.add(new MinimumSeparation(IBuilding.class, IWaterLine.class, 0.1));
            minSeps.add(new MinimumSeparation(IBuilding.class, IBuilding.class, 0.1));
            process.setMinSeps(minSeps);

            Set<String> classesRigides = new HashSet<>();
            Set<String> classesMalleables = new HashSet<>();
            classesRigides.add(IBuilding.class.getName());
            classesMalleables.add(IRoadLine.class.getName());
            classesMalleables.add(IWaterLine.class.getName());
            process.setClassesRigides(classesRigides);
            process.setClassesMalleables(classesMalleables);
            Map<IFeature, Double> symbolWidths = new HashMap<IFeature, Double>();
            for (IFeature feat : space.getInsideFeatures()) {
                if (feat instanceof IGeneObjLin) {
                    double width = SLDUtilCartagen.getSymbolMaxWidthMapMm((IGeneObjLin) feat);
                    symbolWidths.put(feat, width);
                } else
                    symbolWidths.put(feat, 0.0);
            }
            process.setSymbolWidth(symbolWidths);
            process.runOnGeoSpace(space);
        }

        public RunUrbanLSAAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Run a Least Squares process tuned for urban features on a building group space instance");
            this.putValue(Action.NAME, "Run the urban least squares process");
        }
    }

    class CreateRiverAreaSpaceAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor conductor = Conductor.getInstance();
            OWLOntology onto = null;
            try {
                onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
            }
            CollaGenEnvironment.getInstance()
                    .setGeneralisationConcepts(GeneralisationConcept.ontologyToGeneralisationConcepts(onto));
            RiverAreasSpace space = new RiverAreasSpace();
            System.out.println(space.getInsideFeatures().size());
            System.out.println(space.getConcept());
            conductor.addGeoSpace(space);
        }

        public CreateRiverAreaSpaceAction() {
            super();
            this.putValue(Action.NAME, "Create a River Area thematic space");
        }
    }

    class CreateVegetSpaceAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor conductor = Conductor.getInstance();
            OWLOntology onto = null;
            try {
                onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
            }
            CollaGenEnvironment.getInstance()
                    .setGeneralisationConcepts(GeneralisationConcept.ontologyToGeneralisationConcepts(onto));
            VegetationSpace space = new VegetationSpace();
            System.out.println(space.getInsideFeatures().size());
            System.out.println(space.getConcept());
            conductor.addGeoSpace(space);
        }

        public CreateVegetSpaceAction() {
            super();
            this.putValue(Action.NAME, "Create a Vegetation thematic space");
        }
    }

    class CreateRoadsSpaceAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor conductor = Conductor.getInstance();
            OWLOntology onto = null;
            try {
                onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
            }
            CollaGenEnvironment.getInstance()
                    .setGeneralisationConcepts(GeneralisationConcept.ontologyToGeneralisationConcepts(onto));
            RoadNetworkSpace space = new RoadNetworkSpace();
            System.out.println(space.getInsideFeatures().size());
            System.out.println(space.getConcept());
            conductor.addGeoSpace(space);
        }

        public CreateRoadsSpaceAction() {
            super();
            this.putValue(Action.NAME, "Create a Road network thematic space");
        }
    }

    class CreateFlexiGraphSpaceAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor conductor = Conductor.getInstance();
            OWLOntology onto = null;
            try {
                onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
            }
            CollaGenEnvironment.getInstance()
                    .setGeneralisationConcepts(GeneralisationConcept.ontologyToGeneralisationConcepts(onto));
            IFeatureCollection<INetworkSection> sections = new FT_FeatureCollection<>();
            for (IFeature feat : SelectionUtil.getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
                if (feat instanceof INetworkSection)
                    sections.add((INetworkSection) feat);
            }
            FlexiGraphSpace space = new FlexiGraphSpace(sections.getEnvelope().getGeom(), sections);
            conductor.addGeoSpace(space);
        }

        public CreateFlexiGraphSpaceAction() {
            super();
            this.putValue(Action.NAME, "Create a FlexiGraph space from selection");
        }
    }

    class CreateBuildGroupSpaceAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Conductor conductor = Conductor.getInstance();
            OWLOntology onto = null;
            try {
                onto = OwlUtil.getOntologyFromName(CollaGenComponent.ONTOLOGY);
            } catch (OWLOntologyCreationException e1) {
                e1.printStackTrace();
            }
            CollaGenEnvironment.getInstance()
                    .setGeneralisationConcepts(GeneralisationConcept.ontologyToGeneralisationConcepts(onto));
            IFeatureCollection<IGeneObj> sections = new FT_FeatureCollection<>();
            for (IFeature feat : SelectionUtil.getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
                if (feat instanceof IGeneObj)
                    sections.add((IGeneObj) feat);
            }
            BuildingGroupSpace space = new BuildingGroupSpace(sections.getEnvelope().getGeom(), sections);
            conductor.addGeoSpace(space);
        }

        public CreateBuildGroupSpaceAction() {
            super();
            this.putValue(Action.NAME, "Create a building group space from selection");
        }
    }

    class CreateRuralSpacesAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent e) {
            // get the geographic space layer
            ProjectFrame project = CartAGenPlugin.getInstance().getApplication().getMainFrame()
                    .getSelectedProjectFrame();
            Layer layer = project.getLayer(Conductor.GEO_SPACE_LAYER);
            // build a multisurface geometry with all urban spaces
            IMultiSurface<IPolygon> urbanGeom = GeometryEngine.getFactory().createMultiPolygon();
            IFeatureCollection<IFeature> urbanSpaces = new FT_FeatureCollection<>();
            for (IFeature feat : layer.getFeatureCollection()) {
                if (feat instanceof UrbanSpace) {
                    urbanGeom.add((IPolygon) feat.getGeom());
                    urbanSpaces.add(feat);
                }
            }

            // get building geometries when buildings are outside urban areas
            ArrayList<IGeometry> geoms = new ArrayList<>();
            IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
            for (IFeature building : CartAGenDoc.getInstance().getCurrentDataset().getBuildings()) {
                if (urbanGeom.contains(building.getGeom()))
                    continue;
                geoms.add(building.getGeom());
                buildings.add(building);
            }

            IGeometry complex = UrbanAreaComputationJTS.calculTacheUrbaine(geoms, 50.0, 20.0, 12, 5.0, 10000.0);
            Conductor conductor = Conductor.getInstance();

            // cut the areas with the main road network
            CarteTopo carteTopo = new CarteTopo("cartetopo");
            IFeatureCollection<IFeature> mainSections = new FT_FeatureCollection<>();
            IFeatureCollection<IFeature> ruralExtents = new FT_FeatureCollection<>();
            IFeatureCollection<IFeature> masks = new FT_FeatureCollection<>();

            // fill the limits feature collection
            if (complex instanceof IPolygon) {
                IPolygon polygon = (IPolygon) complex;
                Collection<IFeature> intersecting = urbanSpaces.select(polygon);
                if (intersecting.size() > 0) {
                    for (IFeature urban : intersecting)
                        polygon = (IPolygon) polygon.difference(urban.getGeom());
                }

                IFeature defaultFeat = new DefaultFeature(polygon);
                ruralExtents.add(defaultFeat);
            } else if (complex instanceof IMultiSurface<?>) {
                for (IPolygon simple : ((IMultiSurface<IPolygon>) complex)) {
                    if (simple == null)
                        continue;
                    if (simple.area() < 2000.0)
                        continue;
                    IPolygon polygon = (IPolygon) simple.clone();
                    Collection<IFeature> intersecting = urbanSpaces.select(polygon);
                    if (intersecting.size() > 0) {
                        for (IFeature urban : intersecting) {
                            polygon = (IPolygon) polygon.difference(urban.getGeom());
                        }
                    }
                    IFeature defaultFeat = new DefaultFeature(polygon);
                    ruralExtents.add(defaultFeat);
                }
            }

            // get the main sections
            for (IRoadLine road : CartAGenDoc.getInstance().getCurrentDataset().getRoads()) {
                if (road.getImportance() < 1)
                    continue;
                mainSections.add(road);
            }
            for (IMask mask : CartAGenDoc.getInstance().getCurrentDataset().getMasks()) {
                masks.add(mask);
            }
            carteTopo.importClasseGeo(mainSections);
            carteTopo.importClasseGeo(masks);
            carteTopo.setBuildInfiniteFace(true);
            carteTopo.creeNoeudsManquants(1.0);
            carteTopo.fusionNoeuds(1.0);
            carteTopo.filtreArcsDoublons();
            carteTopo.rendPlanaire(1.0);
            carteTopo.fusionNoeuds(1.0);
            carteTopo.filtreArcsDoublons();
            carteTopo.creeTopologieFaces();
            carteTopo.getPopFaces().initSpatialIndex(Tiling.class, false);

            for (Face face : carteTopo.getListeFaces()) {

                Collection<IFeature> intersecting = ruralExtents.select(face.getGeom());
                if (intersecting.isEmpty())
                    continue;
                for (IFeature intersectingExtent : intersecting) {
                    IGeometry intersection = intersectingExtent.getGeom().intersection(face.getGeom());
                    if (intersection instanceof IPolygon) {
                        // check that the face contains buildings
                        Collection<IFeature> inside = buildings.select(intersection);
                        if (inside.size() == 0)
                            continue;
                        conductor.addGeoSpace(new RuralSpace((IPolygon) intersection));
                    } else if (intersection instanceof IMultiSurface<?>) {
                        for (IPolygon simple : ((IMultiSurface<IPolygon>) intersection)) {
                            // check that the face contains buildings
                            Collection<IFeature> inside = buildings.select(simple);
                            if (inside.size() == 0)
                                continue;
                            conductor.addGeoSpace(new RuralSpace(simple));
                        }
                    }

                }
            }
        }

        public CreateRuralSpacesAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Create rural spaces and add a geographic spaces layer if it does not exist");
            this.putValue(Action.NAME, "Create rural spaces");
        }
    }

}
