package fr.ign.cogit.cartagen.mrdb.processes;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.Filtering;

public class DouglasPeuckerProcess extends ScaleMasterGeneProcess {

    private double threshold;

    @Override
    public void parameterise() {
        this.setParameters(this.getDefaultParameters());
    }

    @Override
    public void execute(IFeatureCollection<IGeneObj> features,
            CartAGenDataSet currentDataset) throws Exception {
        ProcessParameter param = getParameters().iterator().next();
        threshold = (Double) param.getValue();
        for (IGeneObj feature : features) {
            IGeometry geom = feature.getGeom();
            IGeometry generalised = Filtering.DouglasPeucker(geom, threshold);
            if (generalised != null)
                feature.setGeom(generalised);
        }
    }

    @Override
    public String getProcessName() {
        return "douglas_peucker";
    }

    @Override
    public Set<ProcessParameter> getDefaultParameters() {
        Set<ProcessParameter> params = new HashSet<ProcessParameter>();
        params.add(new ProcessParameter("dp_filtering", Double.class, 10.0));
        return params;
    }

}
