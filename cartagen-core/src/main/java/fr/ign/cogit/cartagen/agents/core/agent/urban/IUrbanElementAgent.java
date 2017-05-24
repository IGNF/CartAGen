package fr.ign.cogit.cartagen.agents.core.agent.urban;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.ISmallCompact;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author JRenard
 */

public interface IUrbanElementAgent
    extends IMicroAgentGeneralisation, ISmallCompact {

  /**
   * Gets the urban element feature attached to the object
   */
  @Override
  public IUrbanElement getFeature();

  /**
   * Getter and setter for the potential alignment the building agent takes part
   * @return the alignment if there is one, null either
   */
  public ArrayList<UrbanAlignmentAgent> getAlignments();

  public void setAlignments(ArrayList<UrbanAlignmentAgent> alignments);

  public void addAlignment(UrbanAlignmentAgent alignment);

  /**
   * Gets the goal area of the urban element after individual generalisation
   * taken into account for overlapping and displacement
   * @return
   */
  public double getGoalArea();

  /**
   * Gets the parts of the building that are overlapping with the other objects
   * buildings and roads symbols of the block - takes into account a separation
   * distance.
   * @return the part (polygons mostly) of the building which is overlapping
   */
  public IGeometry getOverlappingGeometry();

  /**
   * Gets the building overlapping rate: the area of the overlapping geometry
   * (see getOverlappingGeometry()) divided by the building area.
   * @return the building overlapping rates, within [0,1]
   */
  public double getOverlappingRate();

  /**
   * Gets the parts of the building that are overlapping with the other objects
   * buildings of a specified alignment - takes into account a separation
   * distance.
   * @return the part (polygons mostly) of the building which is overlapping
   */
  public IGeometry getOverlappingGeometryInAlignment(UrbanAlignmentAgent align);

  /**
   * Same as getOverlappingRate() but only taking into account building overlaps
   * in a specified alignment
   * 
   * @return the building overlapping rate, within [0,1]
   * @author JRenard
   */
  public double getOverlappingRateInAlignment(UrbanAlignmentAgent align);

  /**
   * Gets the parts of the building that are overlapping with the other objects
   * buildings of the block - takes into account a separation distance.
   * @return the part (polygons mostly) of the building which is overlapping
   */
  public IGeometry getOverlappingGeometryBetweenUrbanElements();

  /**
   * Same as getOverlappingRate() but only taking into account building
   * overlaps.
   * 
   * @return the building overlapping rate, within [0,1]
   * @author GTouya
   */
  public double getOverlappingRateBetweenBuildings();

  /**
   * Tests if the building overlaps the meso geometry (if it is fully inside the
   * meso).
   * 
   * @return true if the building is overlapping the meso limits
   * @author GTouya
   */
  public boolean isOverlappingMeso();

  /**
   * Gets the parts of the building that are overlapping with the other objects
   * buildings of the block without taking into account the separation distance.
   * 
   * @return the part (polygons mostly) of the building which is overlapping
   */
  public IGeometry getDirectOverlapGeometryBetweenUrbanElements();

  /**
   * Same as getOverlappingRateBetweenBuildings() but without the separation
   * distance.
   * 
   * @return the building overlapping rate, within [0,1]
   * @author GTouya
   */
  public double getDirectOverlapRateBetweenUrbanElements();

  /**
   * Gets the length of the smallest side of the building.
   * @return the length of the smallest side
   */
  public double getSmallestSideLength();

}
