package fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.SubmicroConstraint;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class GAELSegmentGeneObj extends GeneObjLinDefault
    implements ISubmicroGeneObj {

  public GAELSegmentGeneObj(IPointAgent p1, IPointAgent p2) {
    this.submicro = new GAELSegment(p1, p2);
    p1.getSubmicros().remove(this.submicro);
    p2.getSubmicros().remove(this.submicro);
    p1.getSubmicros().add(this);
    p2.getSubmicros().add(this);
  }

  public GAELSegmentGeneObj(GAELDeformable def, IPointAgent p1,
      IPointAgent p2) {
    this.submicro = new GAELSegment(def, p1, p2);
    p1.getSubmicros().remove(this.submicro);
    p2.getSubmicros().remove(this.submicro);
    p1.getSubmicros().add(this);
    p2.getSubmicros().add(this);
  }

  private GAELSegment submicro;

  private GAELDeformable def;

  public GAELDeformable getDef() {
    return def;
  }

  public GAELSegment getSubMicro() {
    return this.submicro;
  }

  private void computeGeom() {
    List<IDirectPosition> directPositions = new ArrayList<IDirectPosition>();
    directPositions.add(submicro.getP1().getPosition());
    directPositions.add(submicro.getP2().getPosition());
    this.setGeom(new GM_LineString(directPositions));
  }

  public ILineString getGeom() {
    // if (super.getGeom() == null) {
    this.computeGeom();
    // }
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

  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (!(o instanceof GAELSegmentGeneObj)) {
      return false;
    } else {
      GAELSegmentGeneObj seg = (GAELSegmentGeneObj) o;

      boolean res = this.submicro.getP1().equals(seg.getSubMicro().getP1())
          && this.submicro.getP2().equals(seg.getSubMicro().getP2());
      res = res || this.submicro.getP1().equals(seg.getSubMicro().getP2())
          && this.submicro.getP2().equals(seg.getSubMicro().getP1());
      if (this.getDef() == null && seg.getDef() == null) {
        return res;
      } else if (this.getDef() != null && seg.def != null) {
        return res && this.getDef().equals(seg.getDef());
      } else {
        return false;
      }
    }
  }

  @Override
  public String toString() {
    String toReturn = "GAELSegmentGeneObj, id=" + this.getId()
        + ", begin point agent : " + this.submicro.getP1()
        + ", end point agent : " + this.submicro.getP2();
    return toReturn;
  }

}
