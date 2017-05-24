package fr.ign.cogit.cartagen.agents.diogen.schema;

import java.lang.reflect.Method;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

public interface IBidirectionalNetworkNode extends INetworkNode {

  public INetworkSection getOtherSection(INetworkSection section, Method method);

}
