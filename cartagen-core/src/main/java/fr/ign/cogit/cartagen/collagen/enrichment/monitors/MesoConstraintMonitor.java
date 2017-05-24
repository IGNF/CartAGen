package fr.ign.cogit.cartagen.collagen.enrichment.monitors;

import fr.ign.cogit.cartagen.collagen.enrichment.ConstraintMonitor;
import fr.ign.cogit.cartagen.collagen.enrichment.IMesoObject;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMesoConstraint;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public abstract class MesoConstraintMonitor extends ConstraintMonitor {

  FormalMesoConstraint constraint;

  public MesoConstraintMonitor(IGeneObj obj, FormalGenConstraint contr) {
    super(obj, contr);
    this.constraint = (FormalMesoConstraint) contr;
  }

  @Override
  public SpecificationElement getElementSpec() {
    return this.constraint;
  }

  public MesoConstraintMonitor() {
    super();
  }

  @Override
  public FormalMesoConstraint getConstraint() {
    return this.constraint;
  }

  public void setContrainte(FormalMesoConstraint contrainte) {
    this.constraint = contrainte;
  }

  @Override
  public int hashCode() {
    return this.getId();
  }

  @Override
  public String toString() {
    return this.constraint.toString() + " monitored on "
        + this.getSujet().toString();
  }

  @Override
  public int getImportance() {
    // TODO be careful the IMesoObject interface has not been added to core
    // schema yet
    if (!(this.getSujet() instanceof IMesoObject<?>)) {
      return 1;
    }
    int nb = ((IMesoObject<?>) this.getSujet()).getComponents().size();
    if (nb == 0) {
      return 1;
    }
    return nb;
  }

}
