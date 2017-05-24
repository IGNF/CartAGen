package fr.ign.cogit.cartagen.collagen.enrichment.monitors;

import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.relations.CollaGenRelation;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalRelationalConstraint;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public abstract class RelConstraintMonitor extends ConstraintMonitor {

  FormalRelationalConstraint contrainte;

  public RelConstraintMonitor(IGeneObj obj, FormalGenConstraint contr) {
    super(obj, contr);
    this.contrainte = (FormalRelationalConstraint) contr;
  }

  @Override
  public SpecificationElement getElementSpec() {
    return this.contrainte;
  }

  @Override
  public FormalRelationalConstraint getConstraint() {
    return this.contrainte;
  }

  public void setContrainte(FormalRelationalConstraint contrainte) {
    this.contrainte = contrainte;
  }

  public RelConstraintMonitor() {
    super();
  }

  @Override
  public int hashCode() {
    return this.getId();
  }

  @Override
  public String toString() {
    return this.contrainte.toString() + " instanci�e sur "
        + this.getSujet().toString();
  }

  @Override
  public IGeometry getExtent(double facteur) {
    // il s'agit ici d'un buffer autour de la géométrie de la relation liée à
    // la contrainte instanciée, buffer de la taille du facteur
    // on commence donc par récupérer la géométrie du sujet
    IGeometry geom = this.getSujet().getGeom();
    IGeometry emprise = geom.buffer(facteur, 10);
    return emprise;
  }

  @Override
  public IDirectPosition toPoint() {
    return this.getSujet().getGeom().centroid();
  }

  @Override
  public int getImportance() {
    return 2;
  }

  @Override
  public CollaGenRelation getSujet() {
    return (CollaGenRelation) super.getSujet();
  }

}
