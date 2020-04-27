package fr.ign.cogit.cartagen.collagen.enrichment;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.ign.cogit.cartagen.collagen.agents.CollaGenEnvironment;
import fr.ign.cogit.cartagen.collagen.enrichment.relations.CollaGenRelation;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public abstract class ConstraintMonitor extends SpecElementMonitor {

    private Object initialValue, currentValue, goalValue;
    protected IFeature subject;
    private FormalGenConstraint constraint;

    public Object getInitialValue() {
        return this.initialValue;
    }

    public void setInitialValue(Object iniValue) {
        this.initialValue = iniValue;
    }

    public Object getGoalValue() {
        return this.goalValue;
    }

    public void setGoalValue(Object goalValue) {
        this.goalValue = goalValue;
    }

    public Object getCurrentValue() {
        return this.currentValue;
    }

    public void setCurrentValue(Object valeurCourante) {
        this.currentValue = valeurCourante;
    }

    public IFeature getSubject() {
        return this.subject;
    }

    public void setSubject(IGeneObj sujet) {
        this.subject = sujet;
    }

    public void setConstraint(FormalGenConstraint constraint) {
        this.constraint = constraint;
    }

    public FormalGenConstraint getConstraint() {
        return this.constraint;
    }

    public abstract IGeometry getExtent(double facteur);

    public abstract IDirectPosition toPoint();

    /**
     * Default empty Constructor.
     */
    public ConstraintMonitor() {
    }

    public ConstraintMonitor(IGeneObj subject, FormalGenConstraint constraint) {
        this.subject = subject;
        this.etatsSatisf = new CopyOnWriteArrayList<ConstraintSatisfaction>();
        this.setConstraint(constraint);
    }

    /**
     * Get the monitors related to a given {@link IGeneObj} feature, looping on
     * the constraint monitors population of the {@link CollaGenEnvironment}.
     * Indeed, the link is not bi-directional to avoid code dependencies.
     * 
     * @param obj
     * @param env
     * @return
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    public static Set<ConstraintMonitor> getFeatureConstraints(IGeneObj obj,
            CollaGenEnvironment env)
            throws SecurityException, IllegalArgumentException {
        Set<ConstraintMonitor> constraints = new HashSet<ConstraintMonitor>();
        IFeatureCollection<ConstraintMonitor> pop = env
                .getConstraintsMonitors();
        for (ConstraintMonitor monitor : pop) {
            if (monitor.getSubject().equals(obj)) {
                constraints.add(monitor);
            }
        }
        return constraints;
    }

    /**
     * Get the monitors related to a given {@link IGeneObj} feature, looping on
     * the constraint monitors population of the {@link CollaGenEnvironment}.
     * Indeed, the link is not bi-directional to avoid code dependencies.
     * 
     * @param obj
     * @param env
     * @return
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    public static Set<ConstraintMonitor> getRelationConstraints(
            CollaGenRelation rel, CollaGenEnvironment env)
            throws SecurityException, IllegalArgumentException {
        Set<ConstraintMonitor> constraints = new HashSet<ConstraintMonitor>();
        IFeatureCollection<ConstraintMonitor> pop = env
                .getConstraintsMonitors();
        for (ConstraintMonitor monitor : pop) {
            if (monitor.getSubject().equals(rel)) {
                constraints.add(monitor);
            }
        }
        return constraints;
    }

    @Override
    public SpecificationElement getElementSpec() {
        return this.constraint;
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ConstraintMonitor other = (ConstraintMonitor) obj;
        if (this.constraint == null) {
            if (other.constraint != null) {
                return false;
            }
        } else if (!this.constraint.equals(other.constraint)) {
            return false;
        }
        if (this.subject == null) {
            if (other.subject != null) {
                return false;
            }
        } else if (!this.subject.equals(other.subject)) {
            return false;
        }
        return true;
    }

    /**
     * Shorcut to get the point representation of the monitor as a geometry.
     * 
     * @return
     */
    public IPoint getPointGeom() {
        return toPoint().toGM_Point();
    }

    public IGeometry getGeom() {
        return toPoint().toGM_Point();
    }

    @Override
    public Object getAttribute(String nomAttribut) {
        if ("pointGeom".equals(nomAttribut))
            return this.getPointGeom();
        else if ("satisfactionString".equals(nomAttribut))
            return getSatisfactionString();
        return super.getAttribute(nomAttribut);
    }

}
