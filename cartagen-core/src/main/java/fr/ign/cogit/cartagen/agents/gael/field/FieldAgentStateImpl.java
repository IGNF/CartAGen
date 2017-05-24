package fr.ign.cogit.cartagen.agents.gael.field;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentState;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentStateImpl;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicAgentStateImpl;

public class FieldAgentStateImpl extends GeographicAgentStateImpl
    implements FieldAgentState {
  /**
     */
  private ArrayList<PointAgentState> etatAgentPoints;

  public ArrayList<PointAgentState> getEtatAgentPoints() {
    return etatAgentPoints;
  }

  public FieldAgentStateImpl(FieldAgent ac, FieldAgentState etatPrecedent,
      Action action) {
    super(ac, etatPrecedent, action);

    // memorise l'etat de chacun des agents points
    this.etatAgentPoints = new ArrayList<PointAgentState>();
    for (IPointAgent ap : ac.getPointAgents()) {
      this.etatAgentPoints.add(new PointAgentStateImpl(ap, null, null));
    }
  }

  @Override
  public boolean isValid(double treshold) {
    // l'etat d'un agent champ est valide lorsque sa satisfaction est superieure
    // a celle de son etat precedent
    return this.getSatisfaction()
        - this.getPreviousState().getSatisfaction() > 0;
  }

  // public boolean isValid() { return isValid(0); }

  @Override
  public void clean() {
    super.clean();
    for (PointAgentState eap : this.etatAgentPoints) {
      eap.clean();
    }
  }
}
