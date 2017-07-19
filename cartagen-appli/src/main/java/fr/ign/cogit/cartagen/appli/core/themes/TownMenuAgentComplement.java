package fr.ign.cogit.cartagen.appli.core.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.action.network.StreetSelectionRuas;
import fr.ign.cogit.cartagen.agents.core.agent.ITownAgent;
import fr.ign.cogit.cartagen.agents.core.constraint.town.StreetDensity;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.CrossRoadDetection;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStrokesNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetworkParameters;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.TownMenu;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.IlotImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.NoeudRoutierImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class TownMenuAgentComplement {

  private JLabel lblAgent = new JLabel("          AGENT");

  private JMenuItem mVilleChargerTous = new JMenuItem(new LoadAction());
  public JMenuItem mSuppressionRues = new JMenuItem(new DeleteStreetsAction());

  public TownMenuAgentComplement() {

    TownMenu menu = DataThemesGUIComponent.getInstance().getTownMenu();

    menu.addSeparator();
    menu.addSeparator();

    this.lblAgent.setForeground(Color.RED);
    menu.add(this.lblAgent);

    menu.addSeparator();
    menu.addSeparator();

    menu.add(this.mVilleChargerTous);

    menu.addSeparator();

    menu.add(this.mSuppressionRues);

  }

  private class LoadAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      AgentGeneralisationScheduler.getInstance().initList();
      for (ITown obj : CartAGenDoc.getInstance().getCurrentDataset()
          .getTowns()) {
        AgentGeneralisationScheduler.getInstance()
            .add(AgentUtil.getAgentFromGeneObj(obj));
      }
    }

    public LoadAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load all town agents into the scheduler");
      this.putValue(Action.NAME, "Load all towns");
    }
  }

  private class DeleteStreetsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      IFeatureCollection<IFeature> selection = new FT_FeatureCollection<IFeature>();
      selection.addAll(SelectionUtil
          .getSelectedObjects(CartAGenPlugin.getInstance().getApplication()));
      for (IFeature sel : selection) {
        if (!(sel instanceof ITown)) {
          continue;
        }
        ITown town = (ITown) sel;
        ITownAgent townAgent = (ITownAgent) AgentUtil.getAgentFromGeneObj(town);
        StreetDensity constraint = new StreetDensity(townAgent, 10.0);
        StreetSelectionRuas action = new StreetSelectionRuas(townAgent,
            constraint, 1.0, townAgent.getStreetNetwork(),
            GeneralisationSpecifications.ROADS_DEADEND_MIN_LENGTH
                * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
            townAgent.getDeadEnds());

        // compute roundabouts and strokes if it hasn't been done before
        if (townAgent.getStreetNetwork().getRoundabouts().size() == 0) {

          CartAGenDataSet dataset = CartAGenDoc.getInstance()
              .getCurrentDataset();
          IFeatureCollection<TronconDeRoute> roads = new FT_FeatureCollection<>();
          Reseau res = new ReseauImpl();
          for (IRoadLine feat : townAgent.getStreetNetwork().getRoads()) {
            TronconDeRoute road = (TronconDeRoute) (feat).getGeoxObj();
            roads.add(road);
          }
          // enrich the roads collection by building its topology

          // construction of the topological map based on roads
          CarteTopo carteTopo = new CarteTopo("roundabouts");
          carteTopo.setBuildInfiniteFace(false);
          carteTopo.importClasseGeo(roads, true);
          carteTopo.creeNoeudsManquants(1.0);
          carteTopo.fusionNoeuds(1.0);
          // create the node objects
          for (Noeud n : carteTopo.getPopNoeuds()) {
            NoeudRoutier noeud = new NoeudRoutierImpl(res, n.getGeometrie());
            for (Arc a : n.getEntrants()) {
              ((TronconDeRoute) a.getCorrespondant(0)).setNoeudFinal(noeud);
              noeud.getArcsEntrants()
                  .add((TronconDeRoute) a.getCorrespondant(0));
            }
            for (Arc a : n.getSortants()) {
              ((TronconDeRoute) a.getCorrespondant(0)).setNoeudInitial(noeud);
              noeud.getArcsSortants()
                  .add((TronconDeRoute) a.getCorrespondant(0));
            }
          }

          // create the blocks
          IFeatureCollection<Ilot> blocks = new FT_FeatureCollection<>();
          // use the same topology map
          carteTopo.filtreDoublons(1.0);
          carteTopo.rendPlanaire(1.0);
          carteTopo.fusionNoeuds(1.0);
          carteTopo.filtreArcsDoublons();
          carteTopo.creeTopologieFaces();
          for (Face face : carteTopo.getListeFaces()) {
            blocks.add(new IlotImpl(face.getGeom()));
          }

          CrossRoadDetection detect = new CrossRoadDetection();
          detect.detectRoundaboutsAndBranchingCartagen(dataset);
          townAgent.getStreetNetwork().setRoundabouts(
              dataset.getRoundabouts().select(townAgent.getGeom()));
        }
        if (townAgent.getStreetNetwork().getStrokes().size() == 0) {
          CartAGenDataSet dataset = CartAGenDoc.getInstance()
              .getCurrentDataset();
          AbstractCreationFactory factory = dataset.getCartAGenDB()
              .getGeneObjImpl().getCreationFactory();
          HashSet<ArcReseau> arcs = new HashSet<ArcReseau>();
          HashSet<NoeudReseau> noeuds = new HashSet<NoeudReseau>();
          for (IFeature feat : townAgent.getStreetNetwork().getRoads()) {
            if (feat instanceof IGeneObj) {
              arcs.add((ArcReseau) ((IGeneObj) feat).getGeoxObj());
              NoeudReseau noeudIni = ((ArcReseau) ((IGeneObj) feat)
                  .getGeoxObj()).getNoeudInitial();
              NoeudReseau noeudFin = ((ArcReseau) ((IGeneObj) feat)
                  .getGeoxObj()).getNoeudFinal();
              noeuds.add(noeudIni);
              noeuds.add(noeudFin);
            }
          }

          RoadStrokesNetwork network = new RoadStrokesNetwork(arcs);
          HashSet<String> attributeNames = new HashSet<String>();
          network.buildStrokes(attributeNames, 112.5, 45.0, true);

          // create IGeneObj strokes
          Collection<IRoadStroke> strokes = new HashSet<>();
          for (Stroke stroke : network.getStrokes()) {
            strokes.add(factory.createRoadStroke(stroke.getGeomStroke(),
                (RoadStroke) stroke));
          }
          townAgent.getStreetNetwork().setStrokes(strokes);
        }

        townAgent.getStreetNetwork().computeExternalAttributes(
            StreetNetworkParameters.costLarge,
            StreetNetworkParameters.costSmall, StreetNetworkParameters.costMed,
            StreetNetworkParameters.surfLarge,
            StreetNetworkParameters.surfSmall, StreetNetworkParameters.surfMed);
        try {
          action.compute();
        } catch (InterruptedException exc) {
        }
      }
    }

    public DeleteStreetsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger the Delete streets algorithm using density on the selected towns");
      this.putValue(Action.NAME, "Delete streets using density");
    }
  }

}
