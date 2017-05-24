/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.field;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;

/**
 * a geographic agent activates a field under it
 * 
 * @author JGaffuri
 * 
 */
public class FieldActivation {

  public static void compute(GeographicObjectAgentGeneralisation ag,
      FieldAgent agentChamp) throws InterruptedException {

    // recupere les triangles du champ qui sont sous l'agent
    ArrayList<GAELTriangle> ts = ag.getTrianglesDessous(agentChamp);

    // cas ou l'agent n'a pas de triangle dessous (ca peut arriver en limite de
    // carte): on sort.
    if (ts.size() == 0) {
      return;
    }

    // place les agents-point de chaque triangle dans la liste du champ (s'il
    // n'y est pas déjà).
    for (GAELTriangle t : ts) {
      if (!agentChamp.getListeAgentsPoints().contains(t.getP1())) {
        agentChamp.getListeAgentsPoints().add(t.getP1());
        t.getP1().setDansListe(true);
      }
      if (!agentChamp.getListeAgentsPoints().contains(t.getP2())) {
        agentChamp.getListeAgentsPoints().add(t.getP2());
        t.getP2().setDansListe(true);
      }
      if (!agentChamp.getListeAgentsPoints().contains(t.getP3())) {
        agentChamp.getListeAgentsPoints().add(t.getP3());
        t.getP3().setDansListe(true);
      }
    }

    // activation de l'agent champ
    agentChamp.activate();

  }

}
