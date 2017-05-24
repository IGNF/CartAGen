package fr.ign.cogit.cartagen.agents.diogen.agent.submicro;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELAngleGeneObj;

public class AngleSubmicroAgent extends SubmicroAgent {

  public AngleSubmicroAgent(GAELAngleGeneObj submicro) {
    super(submicro);
    ((GeographicPointAgent) submicro.getSubMicro().getP())
        .addSubmicroAgent(this);
    ((GeographicPointAgent) submicro.getSubMicro().getP1())
        .addSubmicroAgent(this);
    ((GeographicPointAgent) submicro.getSubMicro().getP2())
        .addSubmicroAgent(this);
    ;
  }
}
