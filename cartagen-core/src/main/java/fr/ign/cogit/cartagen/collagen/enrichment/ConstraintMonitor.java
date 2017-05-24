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

  private Object valeurIni, valeurCourante, valeurBut;
  protected IFeature sujet;
  private FormalGenConstraint constraint;

  public Object getValeurIni() {
    return this.valeurIni;
  }

  public void setValeurIni(Object valeurIni) {
    this.valeurIni = valeurIni;
  }

  public Object getValeurBut() {
    return this.valeurBut;
  }

  public void setValeurBut(Object valeurBut) {
    this.valeurBut = valeurBut;
  }

  public Object getValeurCourante() {
    return this.valeurCourante;
  }

  public void setValeurCourante(Object valeurCourante) {
    this.valeurCourante = valeurCourante;
  }

  public IFeature getSujet() {
    return this.sujet;
  }

  public void setSujet(IGeneObj sujet) {
    this.sujet = sujet;
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
   * Constructeur vide qui permet entre autres d'utiliser la méthode
   * getGothicClassName() comme une méthode statique.
   */
  public ConstraintMonitor() {
  }

  public ConstraintMonitor(IGeneObj sujet, FormalGenConstraint constraint) {
    this.sujet = sujet;
    this.etatsSatisf = new CopyOnWriteArrayList<ConstraintSatisfaction>();
    this.setConstraint(constraint);
  }

  /**
   * Get the monitors related to a given {@link IGeneObj} feature, looping on
   * the constraint monitors population of the {@link CollaGenEnvironment}.
   * Indeed, the link is not bi-directional to avoid code dependencies.
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
    IFeatureCollection<ConstraintMonitor> pop = env.getConstraintsMonitors();
    for (ConstraintMonitor monitor : pop) {
      if (monitor.getSujet().equals(obj)) {
        constraints.add(monitor);
      }
    }
    return constraints;
  }

  /**
   * Get the monitors related to a given {@link IGeneObj} feature, looping on
   * the constraint monitors population of the {@link CollaGenEnvironment}.
   * Indeed, the link is not bi-directional to avoid code dependencies.
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
    IFeatureCollection<ConstraintMonitor> pop = env.getConstraintsMonitors();
    for (ConstraintMonitor monitor : pop) {
      if (monitor.getSujet().equals(rel)) {
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
    if (this.sujet == null) {
      if (other.sujet != null) {
        return false;
      }
    } else if (!this.sujet.equals(other.sujet)) {
      return false;
    }
    return true;
  }

  /**
   * Shorcut to get the point representation of the monitor as a geometry.
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
