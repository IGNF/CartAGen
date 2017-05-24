package fr.ign.cogit.cartagen.agents.diogen.agent.submicro;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.ISubmicroGeneObj;

public interface ISubmicroAgent
    extends ICartAComAgentGeneralisation, IDiogenAgent {
  ISubmicroGeneObj getFeature();
}
