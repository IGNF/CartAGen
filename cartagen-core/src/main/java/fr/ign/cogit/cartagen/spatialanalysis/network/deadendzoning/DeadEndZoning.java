package fr.ign.cogit.cartagen.spatialanalysis.network.deadendzoning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

/**
 * This class is the algorithm of dead end zoning proposed by C. Duchêne in her
 * PhD (p.132-136).
 * @author GTouya
 * 
 */
public class DeadEndZoning {

  private static Logger logger = Logger
      .getLogger(DeadEndZoning.class.getName());
  /**
   * The network face geometry in which the zoning is computed (the zoning is
   * only meaningful when related to an object that is inside a zone and inside
   * a particular network face).
   */
  private IPolygon networkFace;
  /**
   * The network section that is an unusual network section (a dead end, a
   * bridge to a dead end or an isolated section).
   */
  private INetworkSection section;
  /**
   * The result of the zoning, i.e. a list of DeadEndZone, sorted according the
   * type index.
   */
  private ArrayList<DeadEndZone> zones;
  /**
   * The distance used on the buffer that helps to build the zoning.
   */
  private double zoningDistance;

  public DeadEndZoning(IPolygon networkFace, INetworkSection section,
      double zoningDistance) {
    super();
    this.networkFace = networkFace;
    this.section = section;
    this.zoningDistance = zoningDistance;
    this.zones = new ArrayList<DeadEndZone>();
  }

  public IPolygon getNetworkFace() {
    return this.networkFace;
  }

  public void setNetworkFace(IPolygon networkFace) {
    this.networkFace = networkFace;
  }

  public INetworkSection getSection() {
    return this.section;
  }

  public void setSection(INetworkSection section) {
    this.section = section;
  }

  public ArrayList<DeadEndZone> getZones() {
    return this.zones;
  }

  public void setZones(ArrayList<DeadEndZone> zones) {
    this.zones = zones;
  }

  public double getZoningDistance() {
    return this.zoningDistance;
  }

  public void setZoningDistance(double zoningDistance) {
    this.zoningDistance = zoningDistance;
  }

  public void buildDeadEndZoning() {
    NetworkSectionType type = this.section.getNetworkSectionType();
    // TODO
    if (type.equals(NetworkSectionType.DIRECT_DEAD_END)) {
      IDirectPosition initial = this.section.getGeom().coord().get(0);
      boolean geomWay = true;
      if (!this.networkFace.getExterior().touches(new GM_Point(initial))) {
        geomWay = false;
      }
      this.buildDirectDeadEndZoning(geomWay);
    } else {
      this.buildSimpleDeadEndZoning(true);
    }
  }

  /**
   * Compute the zone type index for a building in this dead end zone.
   * @param building
   * @return
   */
  public int computeZoneTypeIndex(IPolygon building) {
    // initialisation of the return value
    int zoneType = 0;
    // loop on the zone types
    int zoneIndex = 1;
    while (zoneIndex <= 100000) {
      // get the dead end zone corresponding to the index
      DeadEndZone zone = this.getZone(zoneIndex);
      // test if there is a zone for this index (i.e. there is not always a
      // RIGHT_START zone
      if (zone == null) {
        zoneIndex *= 10.0;
        continue;
      }
      // compute the intersection between this zone and the building
      IGeometry intersection = null;
      try {
        intersection = zone.getGeom().intersection(building);
      } catch (Exception e) {
        intersection = new GM_Polygon();
      }
      // if the intersection is empty, add the zoneIndex to the zoneType
      if (intersection == null) {
        zoneType += zoneIndex;
      } else if (intersection.isEmpty()) {
        zoneType += zoneIndex;
      }
      // increment the zoneIndex
      zoneIndex *= 10.0;
    }
    return zoneType;
  }

  public DeadEndZone getZone(DeadEndZoneType type) {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().equals(type)) {
        return zone;
      }
    }
    return null;
  }

  public DeadEndZone getZone(int typeIndex) {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().getIndex() == typeIndex) {
        return zone;
      }
    }
    return null;
  }

  /**
   * Builds a simple dead end zoning with the six zones.
   * @param geomWay
   * @author GTouya
   */
  private void buildSimpleDeadEndZoning(boolean geomWay) {

    // first get the ending points
    ILineString sectionLine = this.section.getGeom();
    if (!geomWay) {
      sectionLine = sectionLine.reverse();
    }

    // compute the two half buffers of the line
    IPolygon halfBuffLeft = BufferComputing.buildLineHalfBuffer(sectionLine,
        this.zoningDistance, Side.LEFT);
    IPolygon halfBuffRight = BufferComputing.buildLineHalfBuffer(sectionLine,
        this.zoningDistance, Side.RIGHT);

    // now computes the geometries for the left and right typed zones
    // first left zone
    this.computeSideZone(Side.LEFT, sectionLine, false);
    // then right zone
    this.computeSideZone(Side.RIGHT, sectionLine, false);

    // now computes the left endings areas
    this.computeSideEndingZones(Side.LEFT, sectionLine, halfBuffLeft);
    // and the right ones
    this.computeSideEndingZones(Side.RIGHT, sectionLine, halfBuffRight);
  }

  /**
   * Builds a dead end zoning for direct deadEnds with four zones cut by the
   * networkFace.
   * @param geomWay
   * @author GTouya
   */
  @SuppressWarnings( { "unchecked" })
  private void buildDirectDeadEndZoning(boolean geomWay) {
    if (this.networkFace == null) {
      return;
    }

    // first get the ending points
    ILineString sectionLine = this.section.getGeom();
    if (!geomWay) {
      sectionLine = sectionLine.reverse();
    }

    // compute the two half buffers of the line
    IGeometry halfBuffLeft = this.networkFace.intersection(BufferComputing
        .buildLineHalfBuffer(sectionLine, this.zoningDistance, Side.LEFT));
    IGeometry halfBuffRight = this.networkFace.intersection(BufferComputing
        .buildLineHalfBuffer(sectionLine, this.zoningDistance, Side.RIGHT));

    // now computes the geometries for the left and right typed zones
    // first left zone
    this.computeSideZone(Side.LEFT, sectionLine, true);
    // then right zone
    this.computeSideZone(Side.RIGHT, sectionLine, true);

    // now computes the left ending area
    // first get the side zone geometry
    IPolygon leftZoneGeom = this.getSideArea(Side.LEFT);
    IGeometry diffLeft = null;
    if (halfBuffLeft != null) {
      diffLeft = halfBuffLeft.difference(leftZoneGeom);
    }
    if (diffLeft != null) {
      IPolygon zoneL = null;
      if (diffLeft instanceof IPolygon) {
        zoneL = (IPolygon) diffLeft;
      } else if (diffLeft instanceof IMultiSurface<?>) {
        zoneL = CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) diffLeft);
      } else {
        zoneL = CommonAlgorithmsFromCartAGen
            .getBiggerFromAggregate((IAggregate<IGeometry>) diffLeft);
      }

      DeadEndZoneType type = DeadEndZoneType.LEFT_END;
      this.zones.add(new DeadEndZone(type, zoneL, sectionLine));
    }
    // and the right ones
    IPolygon rightZoneGeom = this.getSideArea(Side.RIGHT);
    IGeometry diffRight = null;
    if (halfBuffRight != null) {
      diffRight = halfBuffRight.difference(rightZoneGeom);
    }
    // deal with the JTS bugs
    if (diffRight != null) {
      IPolygon zoneR = null;
      if (diffRight instanceof IPolygon) {
        zoneR = (IPolygon) diffRight;
      } else if (diffRight instanceof IMultiSurface<?>) {
        zoneR = CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) diffRight);
      } else {
        zoneR = CommonAlgorithmsFromCartAGen
            .getBiggerFromAggregate((IAggregate<IGeometry>) diffRight);
      }

      DeadEndZoneType type2 = DeadEndZoneType.RIGHT_END;
      this.zones.add(new DeadEndZone(type2, zoneR, sectionLine));
    }

    // now modifies the left and right zones to add small parts at the start of
    // the dead end
    if (this.getLeftEndZone() != null && halfBuffLeft != null) {
      DeadEndZone leftZone = this.getLeftZone();
      this.removeZone(DeadEndZoneType.LEFT);
      IGeometry diff = halfBuffLeft.difference(this.getLeftEndZone().getGeom());
      if (diff instanceof IPolygon) {
        leftZone.setGeom((IPolygon) diff);
      } else if (diff instanceof IMultiSurface<?>) {
        leftZone
            .setGeom(CommonAlgorithmsFromCartAGen
                .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) diff));
      }
      this.zones.add(leftZone);
    }
    if (this.getRightEndZone() != null && halfBuffRight != null) {
      DeadEndZone rightZone = this.getRightZone();
      this.removeZone(DeadEndZoneType.RIGHT);
      IGeometry diff = halfBuffRight.difference(this.getRightEndZone()
          .getGeom());
      if (diff instanceof IPolygon) {
        rightZone.setGeom((IPolygon) diff);
      } else if (diff instanceof IMultiSurface<?>) {
        rightZone
            .setGeom(CommonAlgorithmsFromCartAGen
                .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) diff));
      }
      this.zones.add(rightZone);
    }
  }

  /**
   * Computes the left or right zone geometry using an offset of the line
   * geometry.
   * 
   * @param side
   * @param sectionLine
   * @return
   * @author GTouya
   */
  @SuppressWarnings("unchecked")
  private void computeSideZone(Side side, ILineString sectionLine, boolean face) {

    IPolygon offset = BufferComputing.buildHalfOffsetBuffer(side, sectionLine,
        this.zoningDistance);
    DeadEndZoneType type = DeadEndZoneType.LEFT;
    if (side.equals(Side.RIGHT)) {
      offset = BufferComputing.buildHalfOffsetBuffer(side, sectionLine,
          this.zoningDistance);
      type = DeadEndZoneType.RIGHT;
    }
    if (face && this.networkFace != null) {
      IGeometry inter = offset.intersection(this.networkFace);
      if (inter instanceof IPolygon) {
        offset = (IPolygon) inter;
      } else if (inter instanceof IMultiSurface<?>) {
        offset = CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) inter);
      }
    }
    this.zones.add(new DeadEndZone(type, offset, sectionLine));
  }

  @SuppressWarnings("unchecked")
  private void computeSideEndingZones(Side side, ILineString sectionLine,
      IPolygon halfBuffer) {
    // first get the side zone geometry
    IPolygon sideZoneGeom = this.getSideArea(side);
    // compute the difference between the half buffer and the side zone
    IGeometry diff = halfBuffer.difference(sideZoneGeom);
    if (diff == null) {
      // JTS BUG, no ending zone is built
      return;
    }
    // definition of a threshold on zone area
    double threshold = 0.05 * Math.PI * this.zoningDistance
        * this.zoningDistance;
    // test if diff is a simple polygon geometry
    if (diff instanceof IPolygon) {
      // this case correspond to very twisted endings
      // we have to know if the polygon left is the start or end zone
      if (diff.area() > threshold) {
        IDirectPosition startPt = sectionLine.coord().get(0);
        IDirectPosition endPt = sectionLine.coord().get(
            sectionLine.coord().size() - 1);
        double distStart = startPt.distance(diff.centroid());
        double distEnd = endPt.distance(diff.centroid());
        DeadEndZoneType type = DeadEndZoneType.getExtremeType(side,
            (distStart < distEnd));
        this.zones.add(new DeadEndZone(type, (IPolygon) diff, sectionLine));
      }
    } else {
      // general case, with a multipolygon geometry
      HashSet<IPolygon> polygons = new HashSet<IPolygon>();
      if (diff instanceof IMultiSurface<?>) {
        IMultiSurface<IOrientableSurface> multi = (IMultiSurface<IOrientableSurface>) diff;
        for (IOrientableSurface surf : multi.getList()) {
          if (surf.area() > threshold) {
            polygons.add((IPolygon) surf);
          }
        }
      } else {
        IAggregate<IGeometry> aggr = (IAggregate<IGeometry>) diff;
        for (IGeometry surf : aggr.getList()) {
          if (surf.area() > threshold) {
            polygons.add((IPolygon) surf);
          }
        }
      }

      if (polygons.size() > 2 || polygons.size() == 0) {
        DeadEndZoning.logger
            .error("Problem in the dead end zoning for an ending zone");
        return;
      } else if (polygons.size() == 1) {
        // only one zone is left, find out which one it is
        IPolygon poly = polygons.iterator().next();
        IDirectPosition startPt = sectionLine.coord().get(0);
        IDirectPosition endPt = sectionLine.coord().get(
            sectionLine.coord().size() - 1);
        double distStart = startPt.distance(poly.centroid());
        double distEnd = endPt.distance(poly.centroid());
        DeadEndZoneType type = DeadEndZoneType.getExtremeType(side,
            (distStart < distEnd));
        this.zones.add(new DeadEndZone(type, poly, sectionLine));
        return;
      }

      // here, we have two polygons, get them
      Iterator<IPolygon> iter = polygons.iterator();
      IPolygon poly1 = iter.next();
      IPolygon poly2 = iter.next();
      // compute distances for first polygon
      IDirectPosition startPt = sectionLine.coord().get(0);
      IDirectPosition endPt = sectionLine.coord().get(
          sectionLine.coord().size() - 1);
      double distStart = startPt.distance(poly1.centroid());
      double distEnd = endPt.distance(poly1.centroid());
      DeadEndZoneType type = DeadEndZoneType.getExtremeType(side,
          (distStart < distEnd));
      this.zones.add(new DeadEndZone(type, poly1, sectionLine));
      DeadEndZoneType type2 = type.getOtherExtreme();
      this.zones.add(new DeadEndZone(type2, poly2, sectionLine));
    }

  }

  private IPolygon getSideArea(Side side) {
    if (side.equals(Side.LEFT)) {
      return this.getLeftArea();
    }
    if (side.equals(Side.RIGHT)) {
      return this.getRightArea();
    }
    return null;
  }

  private IPolygon getLeftArea() {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().equals(DeadEndZoneType.LEFT)) {
        return zone.getGeom();
      }
    }
    return null;
  }

  private IPolygon getRightArea() {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().equals(DeadEndZoneType.RIGHT)) {
        return zone.getGeom();
      }
    }
    return null;
  }

  private DeadEndZone getLeftZone() {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().equals(DeadEndZoneType.LEFT)) {
        return zone;
      }
    }
    return null;
  }

  private DeadEndZone getLeftEndZone() {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().equals(DeadEndZoneType.LEFT_END)) {
        return zone;
      }
    }
    return null;
  }

  private DeadEndZone getRightZone() {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().equals(DeadEndZoneType.RIGHT)) {
        return zone;
      }
    }
    return null;
  }

  private DeadEndZone getRightEndZone() {
    for (DeadEndZone zone : this.zones) {
      if (zone.getType().equals(DeadEndZoneType.RIGHT_END)) {
        return zone;
      }
    }
    return null;
  }

  private void removeZone(DeadEndZoneType type) {
    ArrayList<DeadEndZone> list = new ArrayList<DeadEndZone>(this.zones);
    for (DeadEndZone zone : list) {
      if (zone.getType().equals(type)) {
        this.zones.remove(zone);
      }
    }
  }
}
