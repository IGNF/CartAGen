/**
 * @author julien Gaffuri 9 juil. 2009
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.decomposers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.MesoAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * 
 * algorithme de deplacement de composants d'un meso utilisant GAEL
 * 
 * @author julien Gaffuri 9 juil. 2009
 * 
 */
public class MesoComponentsDisplacementGAEL {
  private static Logger logger = LogManager
      .getLogger(MesoComponentsDisplacementGAEL.class.getName());

  // importance de la contrainte de proximite objet referents des segments
  private static double IMP_PROXIMITE = 10.0;
  // importance de la contrainte d'orientation des segments
  private static double IMP_ORIENTATION = 7.0;
  // importance de la contrainte de preservetion de position des points
  private static double IMP_POSITION_POINT = 1.0;

  /**
   * l'agent meso dont on souhaite deplacer les composants
   */
  private MesoAgentGeneralisation<? extends GeographicObjectAgent> meso = null;

  public MesoComponentsDisplacementGAEL(
      MesoAgentGeneralisation<? extends GeographicObjectAgent> meso) {
    this.meso = meso;
  }

  public void compute() throws InterruptedException {

    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("deplacement GAEL de composants de meso " + this.meso);
    }

    // decomposition du meso
    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("construction de triangulation du meso");
    }
    MesoDecomposer.buildTriangulation(this.meso,
        GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE);

    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace(" nbpts=" + this.meso.getPointAgents().size() + ", nbsegs="
              + this.meso.getSegments().size());
    }

    // fixation des agents point des troncons
    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("fixation des agents points de troncons");
    }
    for (IPointAgent ap : this.meso.getPointAgents()) {
      if (ap.getLinkedFeature().getFeature() instanceof INetworkSection) {
        if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
          MesoComponentsDisplacementGAEL.logger.trace(
              "   fixation de " + ap + " (" + ap.getLinkedFeature() + ")");
        }
        ap.setFixe(true);
      }
    }

    // instanciation des contraintes

    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("instanciation des contraintes de proximite des segments");
    }
    for (GAELSegment s : this.meso.getSegments()) {
      if (s.getP1().getLinkedFeature().getFeature() instanceof IRoadLine
          || s.getP2().getLinkedFeature().getFeature() instanceof IRoadLine) {
        s.addLinkedFeaturesProximityConstraint(
            MesoComponentsDisplacementGAEL.IMP_PROXIMITE,
            GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE);
      } else {
        s.addLinkedFeaturesProximityConstraint(
            MesoComponentsDisplacementGAEL.IMP_PROXIMITE,
            GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT);
      }
    }

    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("instanciation des contraintes d'orientation des segments");
    }
    this.meso.ajouterContSegmentOrientation(
        MesoComponentsDisplacementGAEL.IMP_ORIENTATION);

    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("instanciation des contraintes de position des points");
    }
    this.meso.creerPointsSingletonsAPartirDesPoints();
    this.meso.ajouterContPointPosition(
        MesoComponentsDisplacementGAEL.IMP_POSITION_POINT);

    // ajout des agents point a la liste d'activation
    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("initialisation de la liste des agents point");
    }
    for (IPointAgent ap : this.meso.getPointAgents()) {
      if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
        MesoComponentsDisplacementGAEL.logger.trace("  ajout de " + ap);
      }
      ap.setDansListe(true);
      this.meso.getListeAgentsPoints().add(ap);
    }

    // activation des agents point avec une faible valeur de resolution
    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("activation des agents point");
    }
    double res = GeneralisationSpecifications.getRESOLUTION();
    GeneralisationSpecifications.setRESOLUTION(res * 0.01);
    this.meso.activatePointAgents();
    GeneralisationSpecifications.setRESOLUTION(res);

    // efface decomposition
    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace("suppression de decomposition");
    }
    this.meso.supprimerContraintesSubmicro();
    this.meso.cleanDecomposition();
    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger
          .trace(" nbpts=" + this.meso.getPointAgents().size() + ", nbsegs="
              + this.meso.getSegments().size());
    }

    // par securite: replace les batiments etant sorti du meso a leur position
    // initiale
    for (GeographicObjectAgent ago : this.meso.getComponents()) {
      if (ago.isDeleted()) {
        continue;
      }

      // si le centre du composant est dans le meso, c'est bon
      if (this.meso.getGeom()
          .contains(new GM_Point(ago.getGeom().centroid()))) {
        continue;
      }

      // sinon, replacer le composant
      MesoComponentsDisplacementGAEL.logger.info(
          "DeplacementComposantsMesoGAEL: un agent est sorti de son meso ! "
              + ago.getGeom());
      IDirectPosition dp1 = ago.getGeom().centroid();
      IDirectPosition dp2 = ago.getInitialGeom().centroid();
      ((GeographicObjectAgentGeneralisation) ago).displaceAndRegister(
          dp2.getX() - dp1.getX(), dp2.getY() - dp1.getY());
    }

    if (MesoComponentsDisplacementGAEL.logger.isTraceEnabled()) {
      MesoComponentsDisplacementGAEL.logger.trace("fin");
    }

  }
}
