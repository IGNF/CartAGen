package fr.ign.cogit.cartagen.appli.plugins.process.leastsquares;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;

import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.core.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IEmbankmentLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph.MinimumSeparation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSCurvatureConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSMovementDirConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSProximityConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler.MatrixSolver;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSSpatialConflict;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.MapspecsLS;

public class LSEmbankmentAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private GeOxygeneApplication appli;

    @Override
    public void actionPerformed(ActionEvent e) {
        // first get the selected features
        Set<IFeature> selected = SelectionUtil.getSelectedObjects(appli);
        System.out.println(selected.size() + " objects to adjust");

        // first create the specifications of the adjustment
        MapspecsLS mapspecs = this.buildMapspecs(selected);
        System.out.println("specifications defined");

        // then create a scheduler instance with these specifications
        LSScheduler sched = new LSScheduler(mapspecs);
        sched.setSolver(MatrixSolver.JAMA);
        System.out.println("scheduler created");

        // trigger the generalisation
        sched.triggerAdjustment(true, true);
        System.out.println("adjusment achieved");
        System.out.println("solutions: ");
        System.out.println(sched.getSolutions());
        for (LSSpatialConflict conflict : sched.getConflits()) {
            System.out.println(conflict.distance());
        }

        // finally display the other geometry

        for (IGeometry geom : sched.getMapObjGeom().values()) {
            CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                    .addFeatureToGeometryPool(geom, Color.GREEN, 2);
        }
        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
                .addTriangulationToGeometryPool(sched.getTriangulation(),
                        Color.ORANGE, Color.ORANGE);
    }

    private MapspecsLS buildMapspecs(Set<IFeature> selected) {
        MapspecsLS mapspecs = new MapspecsLS();

        mapspecs.getClassesFixes().add(IRoadLine.class.getName());
        mapspecs.getClassesFixes().add(IWaterLine.class.getName());
        mapspecs.getClassesFixes().add(IRailwayLine.class.getName());

        mapspecs.getClassesMalleables().add(IEmbankmentLine.class.getName());

        Set<MinimumSeparation> minSeps = new HashSet<>();
        minSeps.add(
                new MinimumSeparation(IRoadLine.class, IRoadLine.class, 0.1));
        minSeps.add(
                new MinimumSeparation(IBuilding.class, IRoadLine.class, 0.1));
        minSeps.add(
                new MinimumSeparation(IBuilding.class, IWaterLine.class, 0.1));
        minSeps.add(
                new MinimumSeparation(IBuilding.class, IBuilding.class, 0.1));

        Map<IFeature, Double> symbolWidths = new HashMap<IFeature, Double>();
        for (IFeature feat : selected) {
            if (feat instanceof IGeneObjLin) {
                double width = SLDUtilCartagen
                        .getSymbolMaxWidthMapMm((IGeneObjLin) feat);
                symbolWidths.put(feat, width);
            } else
                symbolWidths.put(feat, 0.0);
        }

        // set the constraints
        Set<String> malleableConstraints = new HashSet<String>();
        malleableConstraints.add(LSMovementConstraint.class.getName());
        malleableConstraints.add(LSCurvatureConstraint.class.getName());
        malleableConstraints.add(LSMovementDirConstraint.class.getName());

        Map<String[], Double> externalConstraints = new HashMap<String[], Double>();
        for (MinimumSeparation minSep : minSeps) {
            externalConstraints
                    .put(new String[] { LSProximityConstraint.class.getName(),
                            minSep.getClass1().getName(),
                            minSep.getClass2().getName() }, minSep.getMinSep());
        }
        Map<String, Double> constraintWeights = new HashMap<String, Double>();
        constraintWeights.put(LSMovementConstraint.class.getName(), 1.0);
        constraintWeights.put(LSCurvatureConstraint.class.getName(), 10.0);
        constraintWeights.put(LSMovementDirConstraint.class.getName(), 10.0);
        constraintWeights.put(LSProximityConstraint.class.getName(), 15.0);

        mapspecs.setConstraintWeights(constraintWeights);
        mapspecs.setContraintesExternes(externalConstraints);
        mapspecs.setContraintesMalleables(malleableConstraints);

        mapspecs.setSelectedObjects(selected);

        return mapspecs;
    }

    public LSEmbankmentAction(GeOxygeneApplication appli) {
        this.appli = appli;
        this.putValue(Action.NAME, "indidvidual displacement");
        this.putValue(Action.SHORT_DESCRIPTION,
                "indidvidual displacement of selected embankment lines");
    }
}
