package fr.ign.cogit.cartagen.mrdb.processes;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.algorithms.network.RiverNetworkSelection;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class RiverSelectionProcess extends ScaleMasterGeneProcess {

    private double minLength, minArea;
    private int minHorton;
    private boolean removeBraided;

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
            else if (param.getName().equals("horton_order"))
                minHorton = (Integer) param.getValue();
            else if (param.getName().equals("min_braided_area"))
                minArea = (Double) param.getValue();
            else
                removeBraided = (Boolean) param.getValue();
        }

        INetwork net = currentDataset.getHydroNetwork();
        if (net.getSections().size() == 0) {
            IFeatureCollection<INetworkSection> sections = new FT_FeatureCollection<INetworkSection>();
            for (IWaterLine w : currentDataset.getWaterLines()) {
                sections.add(w);
            }
            net.setSections(sections);
        }
        NetworkEnrichment.buildTopology(currentDataset, net, false);

        RiverNetworkSelection selection = new RiverNetworkSelection(minHorton,
                minLength, minArea, removeBraided);
        Set<IWaterLine> eliminated = selection.selection();
        for (IWaterLine feature : eliminated)
            feature.eliminate();

    }

    @Override
    public String getProcessName() {
        return "river_stroke_selection";
    }

    @Override
    public Set<ProcessParameter> getDefaultParameters() {
        Set<ProcessParameter> params = new HashSet<ProcessParameter>();
        params.add(new ProcessParameter("min_length", Double.class, 1000.0));
        params.add(new ProcessParameter("horton_order", Integer.class, 3));
        params.add(new ProcessParameter("min_braided_area", Double.class,
                25000.0));
        params.add(
                new ProcessParameter("remove_braided", Boolean.class, false));
        return params;
    }

}
