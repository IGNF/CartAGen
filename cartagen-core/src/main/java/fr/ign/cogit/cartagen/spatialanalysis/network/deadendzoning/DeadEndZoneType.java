package fr.ign.cogit.cartagen.spatialanalysis.network.deadendzoning;

import java.awt.Color;

import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

/**
 * The enumeration of the dead zone type in the zoning proposed by C. Duchêne in
 * her PhD (p.132-136)
 * @author GTouya
 * 
 */
public enum DeadEndZoneType {
  LEFT, LEFT_END, RIGHT_END, RIGHT, RIGHT_START, LEFT_START;

  /**
   * Get the index of the dead end zone type that allows to qualify position in
   * between two zones.
   * 
   * @return
   * @author GTouya
   */
  public int getIndex() {
    if (this.equals(LEFT))
      return 1;
    if (this.equals(LEFT_END))
      return 10;
    if (this.equals(RIGHT_END))
      return 100;
    if (this.equals(RIGHT))
      return 1000;
    if (this.equals(RIGHT_START))
      return 10000;
    if (this.equals(LEFT_START))
      return 100000;
    return 0;
  }

  public static DeadEndZoneType getExtremeType(Side side, boolean start) {
    if (side.equals(Side.LEFT)) {
      if (start)
        return LEFT_START;
      return LEFT_END;
    }
    if (start)
      return RIGHT_START;
    return RIGHT_END;
  }

  public DeadEndZoneType getOtherExtreme() {
    if (this.equals(LEFT_END))
      return LEFT_START;
    if (this.equals(LEFT_START))
      return LEFT_END;
    if (this.equals(RIGHT_END))
      return RIGHT_START;
    if (this.equals(RIGHT_START))
      return RIGHT_END;
    return this;
  }

  public Color getColor() {
    if (this.equals(LEFT))
      return Color.RED;
    if (this.equals(LEFT_END))
      return Color.ORANGE;
    if (this.equals(RIGHT_END))
      return Color.GRAY;
    if (this.equals(RIGHT))
      return Color.GREEN;
    if (this.equals(RIGHT_START))
      return Color.MAGENTA;
    if (this.equals(LEFT_START))
      return Color.BLUE;
    return Color.BLACK;
  }
}
