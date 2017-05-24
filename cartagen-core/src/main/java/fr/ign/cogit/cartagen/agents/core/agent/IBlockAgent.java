package fr.ign.cogit.cartagen.agents.core.agent;

import java.util.List;

import fr.ign.cogit.cartagen.agents.core.agent.urban.IUrbanElementAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;

/**
 * This interface allows to publish the specific methods of a Block Agent in
 * order to allow other implementations of Block Agent (e.g. Clarity Block
 * Agents)
 * @author GTouya
 */
public interface IBlockAgent
    extends MesoAgent<IUrbanElementAgent>, GAELDeformable {

  /**
   * Gets the block feature attached to the agent
   */
  @Override
  public IUrbanBlock getFeature();

  /**
   * Gets the section agent (road mainly) of the block. Gets the surrounding
   * ones and the inside ones (dead ends).
   * 
   * @return the set of the section agents of the block
   * @author GTouya
   */
  public List<ISectionAgent> getSectionAgents();

  /**
   * Gets the section agent (road mainly) of the block. Gets the surrounding
   * ones and the inside ones (dead ends).
   * 
   * @return the set of the section agents of the block
   * @author GTouya
   */
  @Override
  public List<IUrbanElementAgent> getComponents();

  /**
   * Gets the initial density of the block (with the initial size of the
   * building).
   * 
   * @return the initial density of the block (before generalisation)
   * @author GTouya
   */
  public double getInitialDensity();

  /**
   * Computes the initial density of the block (with the initial size of the
   * building), before generalisation.
   * 
   * @author GTouya
   */
  public void computeInitialDensity();

  /**
   * Gets the simulated density of the block taking into account the road
   * symbols and the future size of the buildings once they are generalised.
   * deleted buildings are not taken into account
   * 
   * @return
   * @author GTouya
   */
  public double getSimulatedDensity();

  /**
   * Gets the simulated density of the block taking into account the road
   * symbols and the future size of the buildings once they are generalised. all
   * initial buildings are taken into account, even if deleted
   * 
   * @return
   * @author JRenard
   */
  public double getInitialSimulatedDensity();

  public boolean isColored();

  /**
   * Gets the block buildings overlapping rate mean.
   * @return the buildings overlapping rates mean, within [0,1]
   */
  public double getBuildingsOverlappingRateMean();

  /**
   * Among the block buildings, gets the one with the biggest overlapping rate,
   * i.e. the most conflicting building.
   * @return
   * @author GTouya
   */
  public IBuildingAgent getMaxOverlapBuilding();

  /**
   * Gets the smallest building among the block buildings.
   * @return the smallest building agent of the block
   * @author GTouya
   */
  public IBuildingAgent getSmallestBuilding();

  /**
   * Gets the best building to remove in the block if we want to eliminate
   * buildings. It's the one with the highest deletion cost. The Block
   * triangulation has to have been computed before.
   * 
   * @param distanceMax the maximum distance between 2 buildings to be
   *          considered as close
   * @return the next building agent to remove or null if none are to remove
   * @author GTouya
   */
  public IBuildingAgent getNextBuildingToRemove(double distanceMax);

  /**
   * @return The number of non deleted components of the meso.
   */
  @Override
  public abstract int getNonDeletedComponentsNumber();

  /**
   * Computes the ratio of overlapped alignments compared to the total unmber of
   * alignments
   * @return
   */
  public double getOverlappedAlignmentsRatio();

}
