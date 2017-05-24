package fr.ign.cogit.cartagen.agents.diogen.agent.submicro;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj.GAELSegmentGeneObj;

public class SegmentSubmicroAgent extends SubmicroAgent {

  public SegmentSubmicroAgent(GAELSegmentGeneObj submicro) {
    super(submicro);
    ((GeographicPointAgent) submicro.getSubMicro().getP1())
        .addSubmicroAgent(this);
    ((GeographicPointAgent) submicro.getSubMicro().getP2())
        .addSubmicroAgent(this);
  }

  public IPointAgent getP1() {
    return ((GAELSegmentGeneObj) this.getFeature()).getSubMicro().getP1();
  }

  public IPointAgent getP2() {
    return ((GAELSegmentGeneObj) this.getFeature()).getSubMicro().getP2();
  }
  //
  // @Override
  // public LinearEnvironment getEncapsulatedEnv() {
  // return (LinearEnvironment) super.getEncapsulatedEnv();
  // }
  //
  // @Override
  // public void setEncapsulatedEnv(Environment encapsulatedEnv) {
  // // TODO manage the case of an environment nonlinear
  // super.setEncapsulatedEnv((LinearEnvironment) encapsulatedEnv);
  // }
}
