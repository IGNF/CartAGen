package fr.ign.cogit.cartagen.agents.diogen.agent.hydro;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.CartAComAgentDeformableGeneralisation;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;

public class CoastLineAgent extends CartAComAgentDeformableGeneralisation
    implements ICoastLineAgent {

  public CoastLineAgent(ICoastLine coastLine) {
    super(coastLine);
  }

}
