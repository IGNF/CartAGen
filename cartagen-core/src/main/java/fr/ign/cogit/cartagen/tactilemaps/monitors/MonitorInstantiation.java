package fr.ign.cogit.cartagen.tactilemaps.monitors;

import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public class MonitorInstantiation {

	private String monitorName, parameter;
	private Class<? extends ConstraintMonitor> javaClass;
	private String layerName;
	private String constraintType;

	@SuppressWarnings("unchecked")
	public MonitorInstantiation(String monitorName, String javaClass, String layerName, String parameter,
			String constraintType) throws ClassNotFoundException {
		this.monitorName = monitorName;
		this.parameter = parameter;
		this.layerName = layerName;
		this.setConstraintType(constraintType);
		this.javaClass = (Class<? extends ConstraintMonitor>) Class.forName(javaClass);
	}

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public Class<? extends ConstraintMonitor> getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(Class<? extends ConstraintMonitor> javaClass) {
		this.javaClass = javaClass;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	/**
	 * Instantiate the constraint monitor described by this on a given
	 * {@link IGeneObj}.
	 * 
	 * @param obj
	 * @return
	 */
	public ConstraintMonitor instantiateOnObject(IGeneObj obj) {
		ConstraintMonitor monitor = null;

		if (this.javaClass.equals(MinimumLineLengthMonitor.class)) {
			monitor = new MinimumLineLengthMonitor(obj, Double.valueOf(parameter));
		}

		return monitor;
	}

	public String getName() {
		return getMonitorName() + " - " + getLayerName();
	}

	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}
}
