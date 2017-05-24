package fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class GAELAngleGeneObj extends GeneObjLinDefault
    implements ISubmicroGeneObj {

  public GAELAngleGeneObj(GAELDeformable def, IPointAgent p1, IPointAgent p,
      IPointAgent p2) {
    this.submicro = new GAELAngle(def, p1, p, p2);
    p1.getSubmicros().remove(this.submicro);
    p.getSubmicros().remove(this.submicro);
    p2.getSubmicros().remove(this.submicro);
    p1.getSubmicros().add(this);
    p.getSubmicros().add(this);
    p2.getSubmicros().add(this);

  }

  private GAELAngle submicro;

  public GAELAngle getSubMicro() {
    return this.submicro;
  }

  private void computeGeom() {
    List<IDirectPosition> directPositions = new ArrayList<IDirectPosition>();
    directPositions.add(submicro.getP1().getPosition());
    directPositions.add(submicro.getP().getPosition());
    directPositions.add(submicro.getP2().getPosition());
    this.setGeom(new GM_LineString(directPositions));
  }

  public ILineString getGeom() {
    if (super.getGeom() == null) {
      this.computeGeom();
    }
    return super.getGeom();
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
