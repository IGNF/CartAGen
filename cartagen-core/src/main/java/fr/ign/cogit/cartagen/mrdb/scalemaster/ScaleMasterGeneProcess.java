/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement.ProcessPriority;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * Abstract class for the generalisation processes that can be triggered by a
 * ScaleMaster2.0. ScaleMaster2.0 processes can be simple algorithms
 * (e.g. @DouglasPeuckerProcess) or more complex processes
 * (e.g. @MixedRoadSelectionProcess).
 * 
 * @author GTouya
 *
 */
public abstract class ScaleMasterGeneProcess
        implements Comparable<ScaleMasterGeneProcess> {

    private Set<ProcessParameter> parameters;
    private int scale;
    private ScaleMaster scaleMaster;
    private ProcessPriority priority;

    protected ScaleMasterGeneProcess() {
        parameters = new HashSet<ProcessParameter>();
    }

    public Set<ProcessParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<ProcessParameter> parameters) {
        this.parameters = parameters;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }

    public ScaleMaster getScaleMaster() {
        return scaleMaster;
    }

    public void setScaleMaster(ScaleMaster scaleMaster) {
        this.scaleMaster = scaleMaster;
    }

    /**
     * Fills the process parameters from the generic set of parameters.
     */
    public abstract void parameterise();

    /**
     * Execute the process with the given parameters.
     */
    public abstract void execute(IFeatureCollection<IGeneObj> features,
            CartAGenDataSet currentDataset) throws Exception;

    public void execute(IFeatureCollection<IGeneObj> features,
            CartAGenDataSet currentDataset,
            Set<Class<? extends IGeneObj>> classes,
            AbstractCreationFactory factory) throws Exception {
        execute(features, currentDataset);
    }

    public abstract String getProcessName();

    /**
     * Get all the parameters of a process with their default value.
     * 
     * @return
     */
    public abstract Set<ProcessParameter> getDefaultParameters();

    /**
     * Get the parameter value from its name.
     * 
     * @param paramName
     * @return
     */
    public Object getParamValueFromName(String paramName) {
        for (ProcessParameter param : getParameters()) {
            if (param.getName().equals(paramName))
                return param.getValue();
        }
        return null;
    }

    public boolean hasParameter(String paramName) {
        for (ProcessParameter param : getParameters()) {
            if (param.getName().equals(paramName))
                return true;
        }
        return false;
    }

    public void addParameter(ProcessParameter parameter) {
        boolean contain = false;
        for (ProcessParameter param : this.parameters) {
            if (param.getName().equals(parameter.getName())) {
                param.setValue(parameter.getValue());
                contain = true;
            }
        }
        if (!contain)
            this.parameters.add(parameter);
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer(getProcessName());
        buff.append(" {");
        boolean first = true;
        for (ProcessParameter param : getParameters()) {
            if (!first)
                buff.append(", ");
            first = false;
            buff.append(param.getName());
            buff.append("(" + param.getValue() + ")");
        }
        buff.append("}");
        return buff.toString();
    }

    public Map<String, Object> getParametersMap() {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        for (ProcessParameter param : parameters) {
            paramsMap.put(param.getName(), param.getValue());
        }
        return paramsMap;
    }

    /**
     * Set the value of a parameter of the process.
     * 
     * @param paramName
     * @param value
     */
    public void setParameterValue(String paramName, Object value) {
        for (ProcessParameter param : parameters) {
            if (paramName.equals(param.getName()))
                param.setValue(value);
        }
    }

    @Override
    public int compareTo(ScaleMasterGeneProcess o) {
        return this.getPriority().compareTo(o.getPriority());
    }

    public ProcessPriority getPriority() {
        return priority;
    }

    public void setPriority(ProcessPriority priority) {
        this.priority = priority;
    }

}
