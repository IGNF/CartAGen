/*
 * Créé le 19 juil. 2005
 */
package fr.ign.cogit.cartagen.agents.core;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;

/**
 * ensemble de parametres utilises pour la generalisation
 * @author julien Gaffuri
 */
public final class AgentSpecifications {
  @SuppressWarnings("unused")
  private static Logger logger = Logger
      .getLogger(AgentSpecifications.class.getName());

  public static boolean STORE_STATES = false;

  private static boolean roadAgents = true;
  private static boolean hydroAgents = false;
  private static boolean railAgents = true;
  private static boolean elecAgents = false;
  private static boolean urbanAgents = true;

  // les parametres sont en mm carte, ou bien en m terrain
  // NB: distance_terrain_en_m = distance_carte_en_mm *
  // Legend.getSYMBOLISATI0N_SCALE()/1000.0

  // the life cycle used for geographic agents
  private static AgentLifeCycle lifeCycle = TreeExplorationLifeCycle
      .getInstance();

  public static AgentLifeCycle getLifeCycle() {
    return AgentSpecifications.lifeCycle;
  }

  public static void setLifeCycle(AgentLifeCycle lifeCycle) {
    AgentSpecifications.lifeCycle = lifeCycle;
  }

  // nb max d'etats a visiter dans un cycle de vie
  private static int NB_MAX_ETATS_A_VISITES = 50;

  public static int getNB_MAX_ETATS_A_VISITES() {
    return AgentSpecifications.NB_MAX_ETATS_A_VISITES;
  }

  public static void setNB_MAX_ETATS_A_VISITES(int nb) {
    AgentSpecifications.NB_MAX_ETATS_A_VISITES = nb;
  }

  private static double validitySatisfactionTreshold = 0.5;

  public static double getValiditySatisfactionTreshold() {
    return AgentSpecifications.validitySatisfactionTreshold;
  }

  public static void setValiditySatisfactionTreshold(
      double validitySatisfactionTreshold) {
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

  public static boolean INSTANCIER_AU_DEMARRAGE = false;

  // /////////////////////////
  // Contraintes et importances
  // /////////////////////////

  // bati

  // taille
  public static boolean TAILLE_BATIMENT = false;
  public static double TAILLE_BATIMENT_IMP = 1.0;

  // granularite
  public static boolean GRANULARITE_BATIMENT = false;
  public static double GRANULARITE_BATIMENT_IMP = 1.0;

  // equarrite
  public static boolean EQUARRITE_BATIMENT = false;
  public static double EQUARRITE_BATIMENT_IMP = 1.0;

  // largeur locale
  public static boolean LARGEUR_LOCALE_BATIMENT = false;
  public static double LARGEUR_LOCALE_BATIMENT_IMP = 1.0;

  // convexite
  public static boolean CONVEXITE_BATIMENT = false;
  public static double CONVEXITE_BATIMENT_IMP = 1.0;
  // quantite de convexite correspondant a un point de satisfaction.
  public static double CONVEXITE_BATIMENT_POINT_SATISFACTION = 0.005; // 0.5%

  // elongation
  public static boolean ELONGATION_BATIMENT = false;
  public static double ELONGATION_BATIMENT_IMP = 1.0;
  // quantite d'elongation correspondant a un point de satisfaction.
  public static double ELONGATION_BATIMENT_POINT_SATISFACTION = 0.01; // 1%

  // orientation
  public static boolean ORIENTATION_BATIMENT = false;
  public static double ORIENTATION_BATIMENT_IMP = 1.0;
  // deviation angulaire correspondant a un point de satisfaction.
  public static double ORIENTATION_BATIMENT_POINT_SATISFACTION = Math.PI / 200;

  // altitude
  public static boolean ALTITUDE_BATIMENT = false;
  public static double ALTITUDE_BATIMENT_IMP = 1.0;
  // valeur d'un point de satisfaction (en m)
  public static double DENIVELLEE_POINT_SATISFACTION = 0.3;

  // occ sol
  public static boolean OCCSOL_BATIMENT = false;
  public static double OCCSOL_BATIMENT_IMP = 1.0;

  // proximite
  public static boolean PROXIMITE_BATIMENT = false;
  public static double PROXIMITE_BATIMENT_IMP = 1.0;

  // densite
  public static boolean DENSITE_ILOT_BATIMENT = false;
  public static double DENSITE_ILOT_BATIMENT_IMP = 1.0;

  // repartition spatiale des batiments
  public static boolean REPARTITION_SPATIALE_BATIMENT = false;
  public static double REPARTITION_SPATIALE_BATIMENT_IMP = 1.0;

  // conservation des grands batiments
  public static boolean CONSERVATION_GRANDS_BATIMENTS = false;
  public static double CONSERVATION_GRANDS_BATIMENTS_IMP = 1.0;

  // satisfaction batiments d'un ilot
  public static boolean SATISFACTION_BATIMENTS_ILOT = false;
  public static double SATISFACTION_BATIMENTS_ILOT_IMP = 1.0;

  // satisfaction ilots d'une ville
  public static boolean SATISFACTION_ILOTS_VILLE = false;
  public static double SATISFACTION_ILOTS_VILLE_IMP = 1.0;

  // routier

  // satisfaction composants reseau routier
  public static boolean SATISFACTION_COMPOSANTS_RESEAU_ROUTIER = false;
  public static double SATISFACTION_COMPOSANTS_RESEAU_ROUTIER_IMP = 1.0;

  // empatement
  public static boolean ROUTIER_EMPATEMENT = false;
  public static double ROUTIER_EMPATEMENT_IMP = 5.0;

  // empatement
  public static boolean ROUTIER_CONTROLE_DEFORMATION = false;
  public static double ROUTIER_CONTROLE_DEFORMATION_IMP = 1.0;

  // impasse
  public static boolean ROUTIER_IMPASSES = false;
  public static double ROUTIER_IMPASSES_IMP = 1.0;

  // densite
  public static boolean ROUTIER_DENSITE = false;
  public static double ROUTIER_DENSITE_IMP = 1.0;

  // hydro

  // satisfaction composants reseau hydro
  public static boolean SATISFACTION_COMPOSANTS_RESEAU_HYDRO = false;
  public static double SATISFACTION_COMPOSANTS_RESEAU_HYDRO_IMP = 1.0;

  // empatement
  public static boolean HYDRO_EMPATEMENT = false;
  public static double HYDRO_EMPATEMENT_IMP = 1.0;

  // proximite routier
  public static boolean PROXIMITE_HYDRO_ROUTIER = false;
  public static double PROXIMITE_HYDRO_ROUTIER_IMP = 1.0;

  // ecoulement
  public static boolean ECOULEMENT_HYDRO = false;
  public static double ECOULEMENT_HYDRO_IMP = 1.0;

  // platitude lac
  public static boolean PLATITUDE_LAC = false;
  public static double PLATITUDE_LAC_IMP = 1.0;

  // relief

  // points
  public static boolean RELIEF_POSITION_POINT = false;
  public static double RELIEF_POSITION_POINT_IMP = 1.0;

  // segments CN
  public static boolean RELIEF_LONGUEUR_SEGMENT_CN = false;
  public static double RELIEF_LONGUEUR_SEGMENT_CN_IMP = 1.0;
  public static boolean RELIEF_ORIENTATION_SEGMENT_CN = false;
  public static double RELIEF_ORIENTATION_SEGMENT_CN_IMP = 1.0;

  // segments
  public static boolean RELIEF_LONGUEUR_SEGMENT = false;
  public static double RELIEF_LONGUEUR_SEGMENT_IMP = 1.0;
  public static boolean RELIEF_ORIENTATION_SEGMENT = false;
  public static double RELIEF_ORIENTATION_SEGMENT_IMP = 1.0;

  // triangles
  public static boolean RELIEF_AIRE_TRIANGLE = false;
  public static double RELIEF_AIRE_TRIANGLE_IMP = 1.0;
  public static boolean RELIEF_CENTREG_TRIANGLE = false;
  public static double RELIEF_CENTREG_TRIANGLE_IMP = 1.0;

  public static double RELIEF_ALTITUDE_BATIMENT_IMP = 1.0;
  public static double RELIEF_ECOULEMENT_HYDRO_IMP = 1.0;

}
