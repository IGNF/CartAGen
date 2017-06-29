/*
 * Créé le 27 juil. 2005
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.submicro;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroNetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle.Area;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle.AzimutalSlopeOrientationPreservation;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle.BuildingsElevationPreservation;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle.CenterPreservation;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle.HydroSectionsOutflow;
import fr.ign.cogit.cartagen.agents.gael.deformation.constraint.simple.triangle.OrientationPreservation;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.cartagen.agents.gael.field.constraint.buildingrelief.BuildingElevation;
import fr.ign.cogit.cartagen.agents.gael.field.relation.buildingfield.BuildingElevationRelation;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.spatialanalysis.measures.TriangleFacesMeasures;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraph;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * The submicro triangle class.
 * 
 * @author JGaffuri
 * 
 */
public class GAELTriangle extends SubMicro implements TriangulationTriangle {

  private IPointAgent p1;
  private IPointAgent p2;
  private IPointAgent p3;

  /**
   * @return The first point of the triangle. NB: the points are ordered in the
   *         direct direction (p1->p2->p3)
   */
  public IPointAgent getP1() {
    return this.p1;
  }

  /**
   * @return The second point of the triangle. NB: the points are ordered in the
   *         direct direction (p1->p2->p3)
   */
  public IPointAgent getP2() {
    return this.p2;
  }

  /**
   * @return The third point of the triangle. NB: the points are ordered in the
   *         direct direction (p1->p2->p3)
   */
  public IPointAgent getP3() {
    return this.p3;
  }

  /**
   * The constructor
   * 
   * @param def
   * @param p1
   * @param p2
   * @param p3
   */
  public GAELTriangle(GAELDeformable def, IPointAgent p1, IPointAgent p2,
      IPointAgent p3) {
    def.getTriangles().add(this);

    // links between the object and its point agent
    p1.getSubmicros().add(this);
    p2.getSubmicros().add(this);
    p3.getSubmicros().add(this);

    this.getPointAgents().add(p1);
    this.getPointAgents().add(p2);
    this.getPointAgents().add(p3);

    // the angle (p1->p2->p3) has to be direct direct. computes the vectorial
    // product to build a direct angle
    if ((p2.getX() - p1.getX()) * (p3.getY() - p1.getY())
        - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX()) > 0) {
      this.p1 = p1;
      this.p2 = p2;
      this.p3 = p3;
    } else {
      this.p1 = p1;
      this.p2 = p3;
      this.p3 = p2;
    }

    p1.addAgentPointAccointants(p2);
    p1.addAgentPointAccointants(p3);
    p2.addAgentPointAccointants(p1);
    p2.addAgentPointAccointants(p3);
    p3.addAgentPointAccointants(p1);
    p3.addAgentPointAccointants(p2);

    // build the geometry
    DirectPositionList dpl = new DirectPositionList();
    dpl.add(p1.getPositions().get(0));
    dpl.add(p2.getPositions().get(0));
    dpl.add(p3.getPositions().get(0));
    dpl.add(p1.getPositions().get(0));
    this.setGeom(new GM_Triangle(new GM_Ring(new GM_LineString(dpl))));
  }

  /**
   * Build the three submicro angles of the triangle
   * 
   * @param def
   */
  public void buildAngles(GAELDeformable def) {
    new GAELAngle(def, this.getP1(), this.getP2(), this.getP3());
    new GAELAngle(def, this.getP2(), this.getP3(), this.getP1());
    new GAELAngle(def, this.getP3(), this.getP1(), this.getP2());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.gaeldeformation.submicro.SubMicro#clean()
   */
  @Override
  public void clean() {
    super.clean();
    this.p1 = null;
    this.p2 = null;
    this.p3 = null;
    this.setGeom(null);
  }

  @Override
  public ITriangle getGeom() {
    return (ITriangle) super.getGeom();
  }

  @Override
  public double getX() {
    return TriangleFacesMeasures.getX(this);
  }

  @Override
  public double getY() {
    return TriangleFacesMeasures.getY(this);
  }

  /**
   * @param pos
   * @return True if the input position is in the triangle, false otherwise
   */
  public boolean contains(IDirectPosition pos) {
    return TriangleFacesMeasures.contains(this, pos);
  }

  /**
   * @param pos
   * @return True if the input position is in the triangle in its initial state,
   *         false otherwise
   */
  public boolean containsInitial(IDirectPosition pos) {
    return TriangleFacesMeasures.containsInitial(this, pos);
  }

  /**
   * @return The triangle area
   */
  public double getArea() {
    return TriangleFacesMeasures.getArea(this);
  }

  private double initialArea = -1.0;

  /**
   * @return The triangle area in its initial state
   */
  public double getInitialArea() {
    if (this.initialArea == -1.0) {
      this.initialArea = TriangleFacesMeasures.getInitialArea(this);
    }
    return this.initialArea;
  }

  /**
   * @return The triangle direction: "d" for direct, "i" for indirect, "n" if
   *         neither. (NB: in its initial state, the 3 points are ordered to be
   *         direct)
   */
  public String getDirection() {
    return TriangleFacesMeasures.getDirection(this);
  }

  /**
   * @return Return if the triangle is reverted or not. (NB: in its initial
   *         state, the triangle is not reverted)
   */
  public boolean istReverted() {
    return TriangleFacesMeasures.istReverted(this);
  }

  /**
   * Return the Z value of the plan defined by the triangle
   * 
   * @param pos
   * @return
   */
  public double getZ(IDirectPosition pos) {
    return TriangleFacesMeasures.getZ(this, pos);
  }

  /**
   * Return the Z value of the plan defined by the triangle in its initial state
   * 
   * @param pos
   * @return
   */
  public double getZInitial(IDirectPosition pos) {
    return TriangleFacesMeasures.getZInitial(this, pos);
  }

  /**
   * @return The angle between the horizontal plan and the triangle, between 0
   *         and Pi, in radians. It is Pi/2 - zenital angle, or too the angle
   *         between the normal vector to the triangle and the vertical oriented
   *         up. 0 means the triangle is horizontal, Pi/2 it is vertical.
   *         Between Pi/2 and Pi, the triangle is reversed.
   */
  public double getSlopeAngle() {
    return TriangleFacesMeasures.getSlopeAngle(this);
  }

  /**
   * @return The normed vector product of the triangle. That is the vector
   *         normal to the oriented triangle.
   */
  public double[] getSlopeVector() {
    return TriangleFacesMeasures.getSlopeVector(this);
  }

  /**
   * @return The azimutal orientation of the slope vector, between -Pi and Pi,
   *         in radian, from the (O,x) axis. Returns -999.9 if the slope is not
   *         defined (horizontal triangle)
   */
  public double getSlopeAzimutalOrientation() {
    return TriangleFacesMeasures.getSlopeAzimutalOrientation(this);
  }

  private double orientationAzimutalePenteIni = -9999.9;

  /**
   * @return The azimutal orientation of the slope vector in the triangle
   *         initial state, between -Pi and Pi, in radian, from the (O,x) axis.
   *         Returns -999.9 if the slope is not defined (horizontal triangle)
   */
  public double getInitialSlopeAzimutalOrientation() {
    if (this.orientationAzimutalePenteIni == -9999.9) {
      this.orientationAzimutalePenteIni = TriangleFacesMeasures
          .getInitialSlopeAzimutalOrientation(this);
    }
    return this.orientationAzimutalePenteIni;
  }

  /**
   * @param orientation
   * @return The difference between -Pi and Pi, in radian, between the slope
   *         azimutal orientation and a given one. Returns -999.9 if the slope
   *         is not defined (horizontal triangle)
   */
  public double getSlopeAzimutalOrientationDifference(double orientation) {
    return TriangleFacesMeasures.getSlopeAzimutalOrientationDifference(this,
        orientation);
  }

  /**
   * @return The difference between -Pi and Pi, in radian, between the slope
   *         azimutal orientation and the one in its initial state. Returns
   *         -999.9 if the slope is not defined (horizontal triangle)
   */
  public double getSlopeAzimutalOrientationDifference() {
    return TriangleFacesMeasures.getSlopeAzimutalOrientationDifference(this);
  }

  /**
   * Mean of the differences between: - the azimutal orientations of the parts
   * of hydro segments on the triangle - and the triangle azimutal orientation
   * in radian, between -Pi and Pi
   * @param hydroNet
   * @return
   */
  public double getSlopeAzimutalOrientationDifferenceToFlowDown(
      HydroNetworkAgent hydroNet) {
    double step = 0.1; // (in meters)

    // get the orientation
    double or = this.getSlopeAzimutalOrientation();

    // if it is horizontal, return 0.
    if (or == -999.9) {
      return 0.0;
    }

    int nb = 0;
    double diffsSum = 0.0;

    // go through the hydrographic segments
    for (GAELSegment seg : hydroNet.getSegments()) {
      // compute the orientation difference between the segment and the triangle
      // slope
      double diff = or - seg.getOrientation();

      // guarantee the difference is between -Pi and Pi
      if (diff < -Math.PI) {
        diff += 2.0 * Math.PI;
      } else if (diff > Math.PI) {
        diff -= 2.0 * Math.PI;
      }

      // go through the points of the segment
      for (double t = 0; t <= 1; t += step / seg.getLength()) {
        // if the point is on the triangle, count it
        if (!this.contains(new DirectPosition(
            (1 - t) * seg.getP1().getX() + t * seg.getP2().getX(),
            (1 - t) * seg.getP1().getY() + t * seg.getP2().getY()))) {
          continue;
        }
        diffsSum += diff;
        nb++;
      }
    }
    // no hydrographic segment is on the triangle: return 0.
    if (nb == 0) {
      return 0.0;
    }

    // return the mean
    return diffsSum / nb;
  }

  /**
   * @param hydroNet
   * @return The length of the hydrographic network on the triangle
   */
  public double getHydroNetworkLengthUp(HydroNetworkAgent hydroNet) {
    double step = 0.1; // (in meters)
    int nb = 0;

    // go through the hydrographic segments
    for (GAELSegment seg : hydroNet.getSegments()) {
      for (double t = 0; t <= 1; t += step / seg.getLength()) {
        // if the point is on the triangle, count it
        if (this.contains(new DirectPosition(
            (1 - t) * seg.getP1().getX() + t * seg.getP2().getX(),
            (1 - t) * seg.getP1().getY() + t * seg.getP2().getY()))) {
          nb++;
        }
      }
    }
    return nb * step;
  }

  /**
   * @param hydroNet
   * @return True if the triangle intersects the hydro network
   */
  public boolean intersectsHydroNetwork(HydroNetworkAgent hydroNet) {
    double step = 3.0;

    // go through the hydrographic segments
    for (GAELSegment seg : hydroNet.getSegments()) {
      for (double t = 0; t <= 1; t += step / seg.getLength()) {
        if (this.contains(new DirectPosition(
            (1 - t) * seg.getP1().getX() + t * seg.getP2().getX(),
            (1 - t) * seg.getP1().getY() + t * seg.getP2().getY()))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return Outflow indicator, between 0 (awful) and 1 (great)
   */
  public double getIndicateurEcoulement(HydroNetworkAgent hydroNet) {

    double alpha = this
        .getSlopeAzimutalOrientationDifferenceToFlowDown(hydroNet); // between
    // -Pi and
    // Pi

    // case where there is no hydrography on the triangle or the hydro perfectly
    // flows down
    if (alpha == 0.0) {
      return 1.0;
    }
    alpha = Math.abs(alpha) / Math.PI;

    // this factor alows to make the indicator worst where the slope is high,
    // and better where it is horizontal
    double slopeFactor = this.getSlopeAngle(); // between 0 and Pi

    // the triangle is horizontal
    if (slopeFactor == 0.0) {
      return 1.0;
    }

    // the triangle is reversed
    if (slopeFactor >= Math.PI / 2) {
      slopeFactor = 0.0;
    } else {
      slopeFactor /= Math.PI / 2.0;
    }

    // a size factor for the indicator: the more the hydro network is present on
    // the triangle, the higher this factor is.
    // this factor depand on the size of the triangle too, of course.
    if (this.getArea() == 0) {
      return 1.0;
    }
    double sizeFactor = this.getHydroNetworkLengthUp(hydroNet)
        / Math.sqrt(Math.abs(this.getArea()));

    // return the indicator
    return 1.0 - alpha * slopeFactor * sizeFactor;
  }

  /**
   * @return True if a building is on the triangle, else false
   */
  public boolean contientBatiment() {
    return TriangleFacesMeasures.contientBatiment(this);
  }

  /**
   * @return Mean of the elevation differences of the building on the triangle,
   *         which elevation is constrained
   */
  public double getEcartAltitudeBatiments(ReliefFieldAgent reliefFieldAgent) {
    double Sdz = 0.0;
    int nb = 0;
    for (IBuilding bat : CartAGenDoc.getInstance().getCurrentDataset()
        .getBuildings()) {

      // if the building is deleted, continue
      if (bat.isDeleted() || bat.getGeom() == null || bat.getGeom().isEmpty()) {
        continue;
      }

      // if the building is not on the triangle, continue
      if (!this.contains(bat.getGeom().centroid())) {
        continue;
      }

      // consider the building only if it is constrained
      for (Constraint c : AgentUtil.getAgentFromGeneObj(bat).getConstraints()) {
        if (!(c instanceof BuildingElevation)) {
          continue;
        }
        System.out.println("valeur initiale: "
            + ((BuildingElevationRelation) ((BuildingElevation) c)
                .getRelation()).getValeurInitiale());
        System.out.println("valeur courante: "
            + ((BuildingAgent) AgentUtil.getAgentFromGeneObj(bat))
                .getElevation(reliefFieldAgent));
        System.out.println(this.getPoint1());
        System.out.println(this.getPoint2());
        System.out.println(this.getPoint3());
        Sdz += ((BuildingElevationRelation) ((BuildingElevation) c)
            .getRelation()).getValeurInitiale()
            - ((BuildingAgent) AgentUtil.getAgentFromGeneObj(bat))
                .getElevation(reliefFieldAgent);
        nb++;
      }
    }
    // return the mean
    if (nb == 0) {
      return 0.0;
    }
    return -Sdz / nb;
  }

  /**
   * Add a contraint on the area
   * 
   * @param importance
   */
  public void addAreaConstraint(double importance) {
    new Area(this, importance);
  }

  /**
   * Add a constraint on the center position
   * 
   * @param importance
   */
  public void addCenterPreservationConstraint(double importance) {
    new CenterPreservation(this, importance);
  }

  /**
   * Add a constraint on the center position
   * 
   * @param importance
   */
  public void addOrientationPreservationConstraint(double importance) {
    new OrientationPreservation(this, importance);
  }

  /**
   * Add a constraint on the azimutal orientation
   * 
   * @param importance
   */
  public void addAzimutalSlopeOrientationPreservationConstraint(
      double importance) {
    new AzimutalSlopeOrientationPreservation(this, importance);
  }

  /**
   * Add a constraint on the hydro sections outflow
   * 
   * @param importance
   */
  public void addHydroSectionsOutflowConstraint(double importance) {
    new HydroSectionsOutflow(this, importance);
  }

  /**
   * Add a constraint on the buildings elevation
   * 
   * @param importance
   */
  public void addBuildingsElevationPreservationConstraint(double importance) {
    new BuildingsElevationPreservation(this, importance);
  }

  @Override
  public String toString() {
    return this.p1 + " " + this.p2 + " " + this.p3;
  }

  @Override
  public TriangulationPoint getPoint1() {
    return this.p1;
  }

  @Override
  public TriangulationPoint getPoint2() {
    return this.p2;
  }

  @Override
  public TriangulationPoint getPoint3() {
    return this.p3;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  @Override
  public void setGeom(IPolygon geom) {
    super.setGeom(geom);
  }

  @Override
  public INode getNode1() {
    return this.p1;
  }

  @Override
  public INode getNode2() {
    return this.p2;
  }

  @Override
  public INode getNode3() {
    return this.p3;
  }

  @Override
  public IGraph getGraph() {
    return null;
  }

  @Override
  public void setGraph(IGraph graph) {
  }

}
