package fr.ign.cogit.cartagen.agents.diogen.constraint;

import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;

public class EnoughSpaceAtLeftRayTracing extends EnoughSpaceAtSideRayTracing {

  public EnoughSpaceAtLeftRayTracing(IDiogenAgent agent, double importance) {
    super(agent, importance);
  }

  @Override
  public boolean isLeftSide() {
    return true;
  }

}
