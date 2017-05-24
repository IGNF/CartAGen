package fr.ign.cogit.cartagen.agents.diogen.schema;

import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public interface IEmbeddedDeadEndArea extends IGeneObj {

  /**
   * Gets the urban elements composing the alignment
   * @return
   */
  public List<IUrbanElement> getUrbanElements();

  public void setUrbanElements(List<IUrbanElement> urbanElements);

  public void addUrbanElement(IUrbanElement urbanElement);

  public void removeUrbanElement(IUrbanElement urbanElement);

  public DeadEndGroup getDeadEnd();

  public void setDeadEnd(DeadEndGroup deadEnd);

  public IDirectPosition getRootDirectPosition();

  public Set<INetworkSection> getConnectedNetwork();

  public void goToLeft(double distance);

  public void goToRight(double distance);

  public IUrbanBlock getBlock();

  public void setBlock(IUrbanBlock block);

}
