package fr.ign.cogit.cartagen.agents.gael.deformation;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionImpl;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionResult;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * The point agent displacement action
 * 
 * @author julien Gaffuri
 * 
 */
public class PointAgentDisplacementAction extends ActionImpl {
  static Logger logger = Logger
      .getLogger(PointAgentDisplacementAction.class.getName());

  /**
   * The displacement parameters
   */
  private double dx;

  /**
   * The displacement parameters
   */
  private double dy;

  public double getDx() {
    return this.dx;
  }

  public double getDy() {
    return this.dy;
  }

  /**
   * The constructor
   * 
   * @param pa
   * @param cont
   * @param dx
   * @param dy
   */
  public PointAgentDisplacementAction(IPointAgent pa, Constraint cont,
      double dx, double dy) {
    super(pa, cont, 0.0);
    this.dx = dx;
    this.dy = dy;
    pa.getActionProposals().add(new ActionProposal(cont, true, this, 0.0));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.ActionImpl#compute()
   */
  @Override
  public ActionResult compute() {
    PointAgentDisplacement.displace(this.getAgent(), this.getDx(),
        this.getDy());
    return ActionResult.MODIFIED;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.ActionImpl#getAgent()
   */
  @Override
  public IPointAgent getAgent() {
    return (IPointAgent) super.getAgent();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.agentgeneralisation.action.ActionCartagen#
   * toString ()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " (" + this.getDx() + ", "
        + this.getDy() + ")";
  }

}
