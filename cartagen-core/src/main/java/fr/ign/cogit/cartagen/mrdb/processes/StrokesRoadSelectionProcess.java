package fr.ign.cogit.cartagen.mrdb.processes;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.algorithms.network.roads.RoadNetworkStrokesBasedSelection;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

public class StrokesRoadSelectionProcess extends ScaleMasterGeneProcess {

    private double minLength;
    private int minTs;
    private boolean attribute;

    @Override
    public void parameterise() {
        this.setParameters(this.getDefaultParameters());
    }

    @Override
    public void execute(IFeatureCollection<IGeneObj> features,
            CartAGenDataSet currentDataset) throws Exception {
        for (ProcessParameter param : getParameters()) {
            if (param.getName().equals("min_length"))
                minLength = (Double) param.getValue();
            else if (param.getName().equals("min_T"))
                minTs = (Integer) param.getValue();
            else
                attribute = (Boolean) param.getValue();
        }

        String attributeName = "";
        if (attribute)
            attributeName = "importance";
        NetworkEnrichment.enrichNetwork(currentDataset,
                currentDataset.getRoadNetwork(), currentDataset.getCartAGenDB()
                        .getGeneObjImpl().getCreationFactory());

        RoadNetworkStrokesBasedSelection process = new RoadNetworkStrokesBasedSelection(
                currentDataset, currentDataset.getRoadNetwork(), attributeName);
        Set<INetworkSection> eliminated = process
                .strokesBasedSelection(minLength, minTs);
        for (INetworkSection feature : eliminated)
            feature.eliminate();

    }

    @Override
    public String getProcessName() {
        return "stroke_based_selection";
    }

    @Override
    public Set<ProcessParameter> getDefaultParameters() {
        Set<ProcessParameter> params = new HashSet<ProcessParameter>();
        params.add(new ProcessParameter("min_length", Double.class, 1000.0));
        params.add(new ProcessParameter("min_T", Integer.class, 3));
        params.add(new ProcessParameter("attribute", Boolean.class, false));
        return params;
    }

}
