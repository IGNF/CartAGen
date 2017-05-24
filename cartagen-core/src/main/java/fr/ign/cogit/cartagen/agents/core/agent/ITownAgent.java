package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;

public interface ITownAgent extends MesoAgent<IBlockAgent> {

  /**
   * Gets the town feature attached to the object
   */
  @Override
  public ITown getFeature();

  /**
   * Update the blocks mesos components of the agent according to the current
   * eliminated roads. This method avoids to put agent code in the street
   * selection algorithm.
   * 
   * @author GTouya
   */
  public void updateBlocks();

  public StreetNetwork getStreetNetwork();

  public IFeatureCollection<DeadEndGroup> getDeadEnds();
}
