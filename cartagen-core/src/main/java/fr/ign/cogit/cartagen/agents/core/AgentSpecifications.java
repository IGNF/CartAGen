/*
 * Créé le 19 juil. 2005
 */
package fr.ign.cogit.cartagen.agents.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;

/**
 * ensemble de parametres utilises pour la generalisation
 * 
 * @author julien Gaffuri
 */
public final class AgentSpecifications {
    @SuppressWarnings("unused")
    private static Logger logger = LogManager.getLogger(AgentSpecifications.class.getName());

    public static boolean STORE_STATES = false;

    private static boolean roadAgents = true;
    private static boolean hydroAgents = false;
    private static boolean railAgents = true;
    private static boolean elecAgents = false;
    private static boolean urbanAgents = true;

    // les parametres sont en mm carte, ou bien en m terrain
    // NB: distance_terrain_en_m = distance_carte_en_mm *
    // Legend.getSYMBOLISATI0N_SCALE()/1000.0
    // The parameters are in map mm, or in real world meters (see the comments
    // of each parameter).
    // NB: distance_in_m = distance_map_mm *
    // Legend.getSYMBOLISATI0N_SCALE()/1000.0

    // the life cycle used for geographic agents
    private static AgentLifeCycle lifeCycle = TreeExplorationLifeCycle.getInstance();

    public static AgentLifeCycle getLifeCycle() {
        return AgentSpecifications.lifeCycle;
    }

    public static void setLifeCycle(AgentLifeCycle lifeCycle) {
        AgentSpecifications.lifeCycle = lifeCycle;
    }

    // nb max d'etats a visiter dans un cycle de vie
    // maximum number of states visited in the life cycle of an agent.
    private static int MAX_NB_OF_STATES = 50;

    public static int getNB_MAX_ETATS_A_VISITES() {
        return AgentSpecifications.MAX_NB_OF_STATES;
    }

    public static void setNB_MAX_ETATS_A_VISITES(int nb) {
        AgentSpecifications.MAX_NB_OF_STATES = nb;
    }

    private static double validitySatisfactionTreshold = 0.5;

    public static double getValiditySatisfactionTreshold() {
        return AgentSpecifications.validitySatisfactionTreshold;
    }

    public static void setValiditySatisfactionTreshold(double validitySatisfactionTreshold) {
        AgentSpecifications.validitySatisfactionTreshold = validitySatisfactionTreshold;
    }

    public static boolean isRoadAgents() {
        return roadAgents;
    }

    public static void setRoadAgents(boolean roadAgents) {
        AgentSpecifications.roadAgents = roadAgents;
    }

    public static boolean isHydroAgents() {
        return hydroAgents;
    }

    public static void setHydroAgents(boolean hydroAgents) {
        AgentSpecifications.hydroAgents = hydroAgents;
    }

    public static boolean isRailAgents() {
        return railAgents;
    }

    public static void setRailAgents(boolean railAgents) {
        AgentSpecifications.railAgents = railAgents;
    }

    public static boolean isElecAgents() {
        return elecAgents;
    }

    public static void setElecAgents(boolean elecAgents) {
        AgentSpecifications.elecAgents = elecAgents;
    }

    public static boolean isUrbanAgents() {
        return urbanAgents;
    }

    public static void setUrbanAgents(boolean urbanAgents) {
        AgentSpecifications.urbanAgents = urbanAgents;
    }

    public static boolean STARTUP_INSTANCIATION = false;

    // /////////////////////////
    // Contraintes et importances
    // /////////////////////////

    // bati

    // taille
    // size
    public static boolean BUILDING_SIZE_CONSTRAINT = true;
    public static double BUILDING_SIZE_CONSTRAINT_IMP = 1.0;

    // granularite
    // granularity
    public static boolean BUILDING_GRANULARITY = true;
    public static double BULDING_GRANULARITY_IMP = 1.0;

    // equarrite
    // squareness
    public static boolean BUILDING_SQUARENESS = true;
    public static double BUILDING_SQUARENESS_IMP = 1.0;

    // largeur locale
    // local width
    public static boolean BUILDING_LOCAL_WIDTH = false;
    public static double BUILDING_LOCAL_WIDTH_IMP = 1.0;

    // convexite
    // convexity
    public static boolean BUILDING_CONVEXITY = false;
    public static double BUILDING_CONVEXITY_IMP = 1.0;
    // quantite de convexite correspondant a un point de satisfaction.
    public static double CONVEXITE_BUILDING_POINT_SATISFACTION = 0.005; // 0.5%

    // elongation
    public static boolean BUILDING_ELONGATION = false;
    public static double BUILDING_ELONGATION_IMP = 1.0;
    // quantite d'elongation correspondant a un point de satisfaction.
    public static double ELONGATION_BUILDING_POINT_SATISFACTION = 0.01; // 1%

    // orientation
    public static boolean BUILDING_ORIENTATION = false;
    public static double BUILDING_ORIENTATION_IMP = 1.0;
    // deviation angulaire correspondant a un point de satisfaction.
    public static double ORIENTATION_BUILDING_POINT_SATISFACTION = Math.PI / 200;

    // altitude
    public static boolean BUILDING_ALTITUDE = false;
    public static double BUILDING_ALTITUDE_IMP = 1.0;
    // valeur d'un point de satisfaction (en m)
    public static double HEIGHT_DIFFERENCE_POINT_SATISFACTION = 0.3;

    // occ sol
    // land use
    public static boolean BUILDING_LANDUSE = false;
    public static double BUILDING_LANDUSE_IMP = 1.0;

    // proximite
    // building proximity inside a block
    public static boolean BLOCK_BUILDING_PROXIMITY = false;
    public static double BLOCK_BUILDING_PROXIMITY_IMP = 1.0;

    // densite
    // building density inside a block
    public static boolean BLOCK_BUILDING_DENSITY = false;
    public static double BLOCK_BUILDING_DENSITY_IMP = 1.0;

    // repartition spatiale des batiments
    public static boolean BUILDING_SPATIAL_DISTRIBUTION = false;
    public static double BUILDING_SPATIAL_DISTRIBUTION_IMP = 1.0;

    // conservation des grands batiments
    public static boolean LARGE_BUILDING_PRESERVATION = false;
    public static double LARGE_BUILDING_PRESERVATION_IMP = 1.0;

    // satisfaction batiments d'un ilot
    public static boolean BLOCK_MICRO_SATISFACTION = true;
    public static double BLOCK_MICRO_SATISFACTION_IMP = 1.0;

    // satisfaction ilots d'une ville
    public static boolean TOWN_BLOCK_SATISFACTION = true;
    public static double TOWN_BLOCK_SATISFACTION_IMP = 1.0;

    // routier

    // satisfaction composants reseau routier
    public static boolean ROAD_NETWORK_MICRO_SATISFACTION = false;
    public static double ROAD_NETWORK_MICRO_SATISFACTION_IMP = 1.0;

    // empatement
    // coalescence
    public static boolean ROAD_COALESCENCE = false;
    public static double ROAD_COALESCENCE_IMP = 5.0;

    // empatement
    public static boolean ROAD_CONTROL_DISTORTION = false;
    public static double ROAD_CONTROL_DISTORTION_IMP = 1.0;

    // impasse
    public static boolean DEAD_END_ROADS = false;
    public static double DEAD_END_ROADS_IMP = 1.0;

    // densite
    public static boolean ROAD_DENSITY = false;
    public static double ROAD_DENSITY_IMP = 1.0;

    // hydro

    // satisfaction composants reseau hydro
    public static boolean RIVER_NETWORK_MICRO_SATISFACTION = false;
    public static double RIVER_NETWORK_MICRO_SATISFACTION_IMP = 1.0;

    // empatement
    public static boolean RIVER_COALESCENCE = false;
    public static double RIVER_COALESCENCE_IMP = 1.0;

    // proximite routier
    public static boolean RIVER_ROAD_PROXIMITY = false;
    public static double RIVER_ROAD_PROXIMITY_IMP = 1.0;

    // ecoulement
    // flow preservation
    public static boolean RIVER_FLOW_PRESERVATION = false;
    public static double RIVER_FLOW_PRESERVATION_IMP = 1.0;

    // platitude lac
    // lake flatness preservation
    public static boolean LAKE_FLATNESS_PRESERVATION = false;
    public static double LAKE_FLATNESS_PRESERVATION_IMP = 1.0;

    // relief

    // points
    public static boolean RELIEF_POSITION_POINT = false;
    public static double RELIEF_POSITION_POINT_IMP = 1.0;

    // contour line segments
    public static boolean CONTOUR_LINE_SEGMENT_LENGTH = false;
    public static double CONTOUR_LINE_SEGMENT_LENGTH_IMP = 1.0;
    public static boolean CONTOUR_LINE_SEGMENT_ORIENTATION = false;
    public static double CONTOUR_LINE_SEGMENT_ORIENTATION_IMP = 1.0;

    // field (i.e. triangulation) segments
    public static boolean RELIEF_SEGMENT_LENGTH = false;
    public static double RELIEF_SEGMENT_LENGTH_IMP = 1.0;
    public static boolean RELIEF_SEGMENT_ORIENTATION = false;
    public static double RELIEF_SEGMENT_ORIENTATION_IMP = 1.0;

    // triangles
    public static boolean RELIEF_TRIANGLE_AREA = false;
    public static double RELIEF_TRIANGLE_AREA_IMP = 1.0;
    public static boolean RELIEF_TRIANGLE_CENTROID = false;
    public static double RELIEF_TRIANGLE_CENTROID_IMP = 1.0;

    // importance of the relational constraints of relief field agent regarding
    // the field agent
    public static double RELIEF_ALTITUDE_BUILDING_IMP = 1.0;
    public static double RELIEF_RIVER_FLOW_IMP = 1.0;

}
