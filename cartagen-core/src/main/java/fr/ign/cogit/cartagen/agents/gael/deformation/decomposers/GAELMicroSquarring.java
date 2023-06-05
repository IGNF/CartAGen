/**
 * @author julien Gaffuri 9 juil. 2009
 */
package fr.ign.cogit.cartagen.agents.gael.deformation.decomposers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.action.micro.SquarringAction;
import fr.ign.cogit.cartagen.agents.core.agent.IMicroAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * 
 * algorithme d'equarrissage d'un agent surfacique utilise GAEL
 * 
 * @author julien Gaffuri 3 févr. 07
 * 
 */
public class GAELMicroSquarring {
  private static Logger logger = LogManager
      .getLogger(SquarringAction.class.getName());

  /**
   */
  private IMicroAgentGeneralisation agent;
  /**
   */
  private double toleranceAngle = 0.0;
  /**
   */
  private int nbLimiteActivationsParPoint = 0;

  public GAELMicroSquarring(IMicroAgentGeneralisation agent,
      double toleranceAngle, int nbLimiteActivationsParPoint) {
    this.agent = agent;
    this.toleranceAngle = toleranceAngle;
    this.nbLimiteActivationsParPoint = nbLimiteActivationsParPoint;
  }

  public void declencher() throws InterruptedException {

    if (GAELMicroSquarring.logger.isDebugEnabled()) {
      GAELMicroSquarring.logger.debug("equarrissage de " + this.agent);
    }

    IPolygon poly = (IPolygon) this.agent.getGeom();

    if (poly.getInterior().size() != 0) {
      GAELMicroSquarring.logger
          .warn("Attention: impossible de lancer equarrissage sur " + this.agent
              + ". polygones a trou non pris en compte.");
      return;
    }

    // sauvegarde de la geometrie initiale
    IPolygon geom_ = (IPolygon) poly.clone();

    // decomposition en submicros: points, segments.
    Decomposers.decomposeLimitPolygon(this.agent, true);

    // calcul de l'orientation des murs
    double orientation = this.agent.getSidesOrientation();
    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger
          .trace("orientation des murs= " + orientation * 180 / Math.PI);
    }

    // instanciation des contraintes

    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger
          .trace("contrainte des segments de " + this.agent);
    }
    for (GAELSegment s : this.agent.getSegments()) {
      if (GAELMicroSquarring.logger.isTraceEnabled()) {
        GAELMicroSquarring.logger.trace("Segment " + s);
      }

      // ajout de contrainte de preservation de longueur
      s.addLengthConstraint(1.0);

      // recupere l'orientation entre -PI et PI du segment oriente
      double orientationSeg = s.getOrientation();
      if (GAELMicroSquarring.logger.isTraceEnabled()) {
        GAELMicroSquarring.logger.trace(
            "   orientation (-180, 180): " + orientationSeg * 180 / Math.PI);
      }

      // orientation a Pi/2 pres du segment
      double orientationSegPiSurDeux = 0.0;
      if (orientationSeg < -Math.PI / 2) {
        orientationSegPiSurDeux = orientationSeg + Math.PI;
      } else if (orientationSeg >= -Math.PI / 2 && orientationSeg < 0) {
        orientationSegPiSurDeux = orientationSeg + Math.PI / 2;
      } else if (orientationSeg >= 0 && orientationSeg < Math.PI / 2) {
        orientationSegPiSurDeux = orientationSeg;
      } else if (orientationSeg > Math.PI / 2) {
        orientationSegPiSurDeux = orientationSeg - Math.PI / 2;
      }
      if (orientationSeg == Math.PI) {
        orientationSegPiSurDeux = 0;
      }

      if (GAELMicroSquarring.logger.isTraceEnabled()) {
        GAELMicroSquarring.logger.trace("   orientation (0, 90): "
            + orientationSegPiSurDeux * 180 / Math.PI);
      }

      // contraint le segment uniquement s'il a son orientation proche de celle
      // de l'orientation moyenne des murs
      if (Math.abs(orientationSegPiSurDeux - orientation) * 180
          / Math.PI <= this.toleranceAngle) {
        if (GAELMicroSquarring.logger.isTraceEnabled()) {
          GAELMicroSquarring.logger
              .trace("   orientation contrainte a changer!");
        }

        double valeurButAngle = 0.0;

        if (orientationSeg < -Math.PI / 2) {
          valeurButAngle = orientation - Math.PI;
        } else if (orientationSeg >= -Math.PI / 2 && orientationSeg < 0) {
          valeurButAngle = orientation - Math.PI / 2;
        } else if (orientationSeg >= 0 && orientationSeg < Math.PI / 2) {
          valeurButAngle = orientation;
        } else if (orientationSeg > Math.PI / 2) {
          valeurButAngle = orientation + Math.PI / 2;
        }
        if (orientationSeg == Math.PI) {
          valeurButAngle = 0.0;
        }

        if (GAELMicroSquarring.logger.isTraceEnabled()) {
          GAELMicroSquarring.logger
              .trace("   (valeur but=" + valeurButAngle * 180 / Math.PI + ")");
        }

        // ajout contrainte orientation an segment
        s.addOrientationConstraint(10.0, valeurButAngle);
      } else {
        if (GAELMicroSquarring.logger.isTraceEnabled()) {
          GAELMicroSquarring.logger
              .trace("   orientation contrainte a etre preservee");
        }

        // ajout contrainte orientation an segment
        s.addOrientationConstraint(2.0);
      }

    }

    // Add constraint on almost right angles.
    // @author (AMaudet)
    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger
          .trace("Initialization of the angle constraints.");
    }
    for (GAELAngle angle : this.agent.getAngles()) {
      logger.trace("Angle " + angle + " with value =" + angle.getValue());
      double difference = Math.abs(angle.getValue() - Math.PI / 2);
      if (GAELMicroSquarring.logger.isTraceEnabled()) {
        logger.trace("    Difference from the right angle " + difference);
      }
      if ((difference * 180 / Math.PI <= this.toleranceAngle)) {
        if (GAELMicroSquarring.logger.isTraceEnabled()) {
          logger.trace("    Add angle value constraint on angle " + angle);
        }
        angle.addValueConstraint(5.0, Math.PI / 2);
      }
    }

    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger
          .trace("initialisation de la liste des agents point");
    }
    for (IPointAgent ap : this.agent.getPointAgents()) {
      if (GAELMicroSquarring.logger.isTraceEnabled()) {
        GAELMicroSquarring.logger.trace("  ajout de " + ap);
      }
      ap.setDansListe(true);
      this.agent.getListeAgentsPoints().add(ap);
    }

    // activation des agents point avec une faible valeur de resolution
    double res = GeneralisationSpecifications.getRESOLUTION();
    GeneralisationSpecifications.setRESOLUTION(0.001);

    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger.trace("activation des agents point");
    }
    this.agent.activatePointAgents(
        this.nbLimiteActivationsParPoint * this.agent.getPointAgents().size());

    GeneralisationSpecifications.setRESOLUTION(res);

    /*
     * if (logger.isTraceEnabled()) logger.trace("recomposition de la geometrie"
     * );
     * 
     * DirectPositionList coords=new DirectPositionList(); for(AgentPoint ap :
     * ab.agentPoints) coords.add( new DirectPosition(ap.getX(), ap.getY()) );
     * coords.add( new DirectPosition(ab.agentPoints.get(0).getX(),
     * ab.agentPoints.get(0).getY()) );
     * 
     * GM_Ring lr=new GM_Ring(new GM_LineString(coords)); GM_Polygon p=new
     * GM_Polygon(lr);
     */

    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger.trace("avant equarrissage: " + geom_);
    }
    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger
          .trace("coord0=" + geom_.getExterior().coord().get(0));
    }
    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger.trace("coordN=" + geom_.getExterior().coord()
          .get(geom_.getExterior().coord().size() - 1));
    }
    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger
          .trace("apres equarrissage: " + this.agent.getGeom());
    }
    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger.trace("coord0="
          + ((IPolygon) this.agent.getGeom()).getExterior().coord().get(0));
    }
    if (GAELMicroSquarring.logger.isTraceEnabled()) {
      GAELMicroSquarring.logger.trace("coordN="
          + ((IPolygon) this.agent.getGeom()).getExterior().coord().get(
              ((IPolygon) this.agent.getGeom()).getExterior().coord().size()
                  - 1));
    }

    if (!this.agent.getGeom().isValid()) {
      GAELMicroSquarring.logger
          .warn("Echec de l'application d'algorithme d'equarrissage  pour "
              + this.agent + ". geometrie renvoyee non valide: "
              + this.agent.getGeom());
      this.agent.setGeom(geom_);
    } else if (this.agent.getGeom().area() < 0.0001) {
      GAELMicroSquarring.logger
          .warn("Echec de l'application d'algorithme d'equarrissage  pour "
              + this.agent + ". geometrie renvoyee de surface presque nulle: "
              + this.agent.getGeom());
      this.agent.setGeom(geom_);
    } else if (this.agent.getGeom().area() < geom_.area() / 2.0) {
      GAELMicroSquarring.logger
          .warn("Echec de l'application d'algorithme d'equarrissage  pour "
              + this.agent + ". geometrie renvoyee de surface presque nulle: "
              + this.agent.getGeom());
      this.agent.setGeom(geom_);
    } else if (this.agent.getGeom().area() > geom_.area() * 2.0) {
      GAELMicroSquarring.logger
          .warn("Echec de l'application d'algorithme d'equarrissage  pour "
              + this.agent + ". geometrie renvoyee de surface presque nulle: "
              + this.agent.getGeom());
      this.agent.setGeom(geom_);
    } else if (this.agent.getGeom().isEmpty()) {
      GAELMicroSquarring.logger
          .warn("Echec de l'application d'algorithme d'equarrissage pour "
              + this.agent + ". geometrie vide: " + this.agent.getGeom());
      this.agent.setGeom(geom_);
    }

    // effacer decomposition
    this.agent.cleanDecomposition();

    /*
     * if (this.agent.getFeature() instanceof GothicBasedGeneObj) {
     * Cache.getCache().addToModifiedObjects( (GothicBasedGeneObj)
     * this.agent.getFeature()); }
     */
  }

}
