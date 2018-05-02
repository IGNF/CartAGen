/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.osm.schema.road;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.ICycleWay;
import fr.ign.cogit.cartagen.osm.schema.network.OsmNetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class OsmCycleWay extends OsmNetworkSection implements ICycleWay {

  private String surface = "";
  private double realWidth = 0;

  public OsmCycleWay(ILineString line) {
    super(line);
  }

  @Override
  public double getWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getInternWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public INetworkNode getInitialNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    // TODO Auto-generated method stub

  }

  @Override
  public INetworkNode getFinalNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getSurface() {
    return surface;
  }

  @Override
  public void setSurface(String surface) {
    this.surface = surface;
  }

  @Override
  public double getRealWidth() {
    return realWidth;
  }

  @Override
  public void setRealWidth(double width) {
    this.realWidth = width;
  }

}
