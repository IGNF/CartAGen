package fr.ign.cogit.cartagen.agents.gael.field;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicAgentState;

public interface FieldAgentState extends GeographicAgentState {

  public ArrayList<PointAgentState> getEtatAgentPoints();
}
