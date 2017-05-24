package fr.ign.cogit.cartagen.spatialanalysis.network.deadendzoning;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class DeadEndZone implements Comparable<DeadEndZone>{
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  /**
   * The type of the dead end zone.
   */
  private DeadEndZoneType type;
  /**
   * The geometry of the dead end zone.
   */
  private IPolygon geom;
  /**
   * The geometry of the dead end that is zoned.
   */
  private ILineString deadEnd;

 

  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public DeadEndZone(DeadEndZoneType type, IPolygon geom, ILineString deadEnd) {
    super();
    this.type = type;
    this.geom = geom;
    this.deadEnd = deadEnd;
  }

  // Getters and setters //
  public DeadEndZoneType getType() {
    return type;
  }

  public void setType(DeadEndZoneType type) {
    this.type = type;
  }
  public IPolygon getGeom() {
    return geom;
  }
  public void setGeom(IPolygon geom) {
    this.geom = geom;
  }
  public ILineString getDeadEnd() {
    return deadEnd;
  }
  public void setDeadEnd(ILineString deadEnd) {
    this.deadEnd = deadEnd;
  }

  
  // Other public methods //
  @Override
  public int hashCode() {
    return type.getIndex();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DeadEndZone other = (DeadEndZone) obj;
    if (geom == null) {
      if (other.geom != null)
        return false;
    } else if (!geom.equals(other.geom))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }

  @Override
  public int compareTo(DeadEndZone arg0) {
    return this.hashCode() - arg0.hashCode();
  }

  ////////////////////////////////////////////
  //           Protected methods            //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //         Package visible methods        //
  ////////////////////////////////////////////

  //////////////////////////////////////////
  //           Private methods            //
  //////////////////////////////////////////

}

