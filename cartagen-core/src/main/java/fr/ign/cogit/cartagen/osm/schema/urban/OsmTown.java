/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.osm.schema.urban;

import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class OsmTown extends OsmGeneObjSurf implements ITown {

  private IFeatureCollection<IUrbanBlock> townBlocks;
  private IFeatureCollection<DeadEndGroup> deadEnds;

  public OsmTown() {
    super();
    townBlocks = new FT_FeatureCollection<>();
    deadEnds = new FT_FeatureCollection<>();
  }

  @Override
  public IFeatureCollection<IUrbanBlock> getTownBlocks() {
    return townBlocks;
  }

  @Override
  public void setTownBlocks(IFeatureCollection<IUrbanBlock> townBlocks) {
    this.townBlocks = townBlocks;
  }

  @Override
  public StreetNetwork getStreetNetwork() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setStreetNetwork(StreetNetwork net) {
    // TODO Auto-generated method stub

  }

  @Override
  public IFeatureCollection<DeadEndGroup> getDeadEnds() {
    return deadEnds;
  }

  @Override
  public void setDeadEnds(IFeatureCollection<DeadEndGroup> deadEnds) {
    this.deadEnds = deadEnds;
  }

  @Override
  public boolean isTownCentre(IUrbanBlock block) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void initComponents() {
    // TODO Auto-generated method stub

  }

}
