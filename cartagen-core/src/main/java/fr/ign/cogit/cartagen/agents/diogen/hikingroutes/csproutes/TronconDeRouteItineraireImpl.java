package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.ICarryingRoadLine;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema.IHikingRouteSection;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;

/**
 * This class extends ArcReseau. It's used to create RoadStrokeForRoutes stroke.
 * 
 * @author JTeulade-Denantes
 * 
 */
public class TronconDeRouteItineraireImpl extends TronconDeRouteImpl
        implements TronconDeRouteItineraire {

    protected static final Logger LOGGER = Logger
            .getLogger(TronconDeRouteItineraireImpl.class.getName());

    public TronconDeRouteItineraireImpl(Reseau res, boolean estFictif,
            ICarryingRoadLine road) {
        super(res, estFictif, road.getGeom(), 0);
        roadSection = road;
    }

    public String getSymbo() {
        String symbo = roadSection.getSymbo();
        if (symbo.equals("Sentier") || symbo.equals("Chemin")) {
            return "Chemin";
        }
        if (!symbo.equals("fictive road")) {
            return "Route";
        }
        return "fictive road";
    }

    public int getImportance() {
        return ((IRoadLine) roadSection).getImportance();
    }

    public Set<String> getRoutesName() {
        Set<String> routeSet = new HashSet<String>();
        for (ICarriedObject route : roadSection.getCarriedObjects())
            routeSet.add(((IHikingRouteSection) route).getName());
        return routeSet;
    }

    /**
     * This attribute allows to get the routes carried by the road
     */
    private ICarryingRoadLine roadSection;

    public ICarryingRoadLine getRoadSection() {
        return roadSection;
    }

    public void setRoadSection(ICarryingRoadLine roadSection) {
        this.roadSection = roadSection;
    }

    /**
     * override getAttribute method doesn't work
     */
    @Override
    public Object getAttribute(String nom) {
        if (nom.equals("symbo")) { //$NON-NLS-1$
            return this.getSymbo();
        }
        if (nom.equals("routesName")) { //$NON-NLS-1$
            return this.getRoutesName();
        }
        if (nom.equals("importance")) { //$NON-NLS-1$
            return this.getImportance();
        }
        logger.error("error in TronconDeRouteItineraireImpl.getAttribute");
        return null;

    }

    public void setRoadGeom(ILineString geom) {
        roadSection.setGeom(geom);
    }
}
