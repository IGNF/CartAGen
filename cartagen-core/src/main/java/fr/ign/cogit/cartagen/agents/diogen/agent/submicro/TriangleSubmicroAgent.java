package fr.ign.cogit.cartagen.agents.diogen.agent.submicro;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELTriangleGeneObj;

public class TriangleSubmicroAgent extends SubmicroAgent {

  public TriangleSubmicroAgent(GAELTriangleGeneObj submicro) {
    super(submicro);
    ((GeographicPointAgent) submicro.getSubMicro().getP3())
        .addSubmicroAgent(this);
    ((GeographicPointAgent) submicro.getSubMicro().getP1())
        .addSubmicroAgent(this);
    ((GeographicPointAgent) submicro.getSubMicro().getP2())
        .addSubmicroAgent(this);
  }

}
