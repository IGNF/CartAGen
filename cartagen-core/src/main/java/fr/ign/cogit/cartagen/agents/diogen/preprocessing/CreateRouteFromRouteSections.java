package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IRoute;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IRouteSection;

public class CreateRouteFromRouteSections {

  private static final Logger LOGGER = Logger
      .getLogger(CreateRouteFromRouteSections.class.getName());

  public void createRouteFromOneRouteSection(IRoute route,
      IRouteSection section) {

    LOGGER.debug("route " + route);
    LOGGER.debug("section " + section);

    // add the first section
    route.add(section);

    IRouteSection last = section;
    // add the next section
    IRouteSection next = section.getNext();
    LOGGER.debug("next " + next);
    if (next != null) {
      route.add(next);
      while (true) {
        // get the next section of the next section
        IRouteSection test = next.getNext();
        LOGGER.debug("test " + test);
        if (test == last) {
          last = next;
          next = next.getPrevious();
        } else {
          last = next;
          next = test;
        }

        LOGGER.debug("last " + last);
        LOGGER.debug("next " + next);
        if (next == null) {
          break;
        }
        if (route.contains(next)) {
          break;
        }
        route.add(next);
      }
    }

    last = section;
    IRouteSection previous = section.getPrevious();

    LOGGER.debug("previous " + previous);
    if (previous != null) {
      route.add(0, previous);
      while (true) {
        IRouteSection test = previous.getPrevious();
        LOGGER.debug("test " + test);
        if (test == last) {
          last = previous;
          previous = previous.getNext();
        } else {
          last = previous;
          previous = test;
        }
        LOGGER.debug("previous " + previous);
        LOGGER.debug("last " + last);

        if (previous == null) {
          break;
        }
        if (route.contains(previous)) {
          break;
        }
        route.add(previous);
      }
    }

  }

}
