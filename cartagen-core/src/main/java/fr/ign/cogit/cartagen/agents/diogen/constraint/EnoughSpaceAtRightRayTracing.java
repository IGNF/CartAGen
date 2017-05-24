package fr.ign.cogit.cartagen.agents.diogen.constraint;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;

public class EnoughSpaceAtRightRayTracing extends EnoughSpaceAtSideRayTracing {

  public EnoughSpaceAtRightRayTracing(IDiogenAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public boolean isLeftSide() {
    return false;
  }

}
