package fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public class GAELPointGeneObj extends GeneObjPointDefault
    implements ISubmicroGeneObj {

  public GAELPointGeneObj(GAELDeformable def, IPointAgent p) {
    this.submicro = new GAELPointSingleton(def, p);
    p.getSubmicros().remove(this.submicro);
    p.getSubmicros().add(this);
  }

  private GAELPointSingleton submicro;

  public GAELPointSingleton getSubMicro() {
    return this.submicro;
  }

  public IPoint getGeom() {
    return submicro.getGeom();
  }

  @Override
  public ArrayList<IPointAgent> getPointAgents() {
    return this.submicro.getPointAgents();
  }

  @Override
  public ArrayList<SubmicroConstraint> getSubmicroConstraints() {
    return this.submicro.getSubmicroConstraints();
  }

  @Override
  public double getX() {
    return this.submicro.getX();
  }

  @Override
  public double getY() {
    return this.submicro.getY();
  }

  @Override
  public double getXIni() {
    return this.submicro.getXIni();
  }

  @Override
  public double getYIni() {
    return this.submicro.getYIni();
  }

  @Override
  public void addCenterPositionConstraint(double importance) {
    this.submicro.addCenterPositionConstraint(importance);

  }

  @Override
  public void addCenterPositionConstraint(double importance, double xGoal,
      double yGoal) {
    this.submicro.addCenterPositionConstraint(importance, xGoal, yGoal);

  }

}
