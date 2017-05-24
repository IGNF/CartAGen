package fr.ign.cogit.cartagen.agents.diogen.preprocessing;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IRouteSection;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

public class InconsistencesDetection {

  public Set<Inconsistence> compute(
      IFeatureCollection<? extends IRouteSection> routes,
      IFeatureCollection<? extends ICarryingRoadLine> roads) {

    Set<Inconsistence> inconsistences = new HashSet<Inconsistence>();

    for (ICarryingRoadLine road : roads) {
      for (ICarriedObject carried : road.getCarriedObjects()) {
        IRouteSection routeSection = (IRouteSection) carried;
        for (ICarriedObject carried2 : road.getCarriedObjects()) {
          IRouteSection routeSection2 = (IRouteSection) carried2;
          if (routeSection != routeSection2
              && routeSection.fromSameRoute(routeSection2)) {
            inconsistences
                .add(new Inconsistence(routeSection, "From same route"));
          }
        }
      }
    }

    for (IRouteSection routeSection : routes) {
      if (routeSection.getInitialNode().getOutSections().size()
          + routeSection.getInitialNode().getInSections().size() <= 1
          || routeSection.getFinalNode().getOutSections().size()
              + routeSection.getFinalNode().getInSections().size() <= 1) {
        inconsistences.add(new Inconsistence(routeSection, "End of route"));
      }
    }

    return inconsistences;
  }

  public class Inconsistence {

    private IRouteSection section;

    private String nature;

    public Inconsistence(IRouteSection section, String nature) {
      this.section = section;
      this.nature = nature;
    }

    public IRouteSection getSection() {
      return section;
    }

    public void setSection(IRouteSection section) {
      this.section = section;
    }

    public String getNature() {
      return nature;
    }

    public void setNature(String nature) {
      this.nature = nature;
    }
  }

}
