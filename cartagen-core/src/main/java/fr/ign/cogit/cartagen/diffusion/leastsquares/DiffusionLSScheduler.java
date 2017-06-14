/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.diffusion.leastsquares;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.leastsquares.conflation.ConflationScheduler;
import fr.ign.cogit.geoxygene.contrib.leastsquares.conflation.LSVectorDisplConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSPoint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.MapspecsLS;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;

public class DiffusionLSScheduler extends ConflationScheduler {

  private static Logger logger = Logger
      .getLogger(DiffusionLSScheduler.class.getName());

  private double minDispl = 0.0;

  public DiffusionLSScheduler(MapspecsLS ms, Set<DefaultFeature> vectors,
      Class<? extends LSVectorDisplConstraint> conflationConstraint,
      double minDispl) {
    super(ms, vectors, conflationConstraint);
    this.minDispl = minDispl;
  }

  public void triggerAdjustment() {
    // on commence par sélectionner les objets
    logger.fine("Moindres carres : on recupere les objets");
    try {
      this.setObjs();
    } catch (IllegalArgumentException e2) {
      e2.printStackTrace();
    } catch (SecurityException e2) {
      e2.printStackTrace();
    } catch (IllegalAccessException e2) {
      e2.printStackTrace();
    } catch (NoSuchFieldException e2) {
      e2.printStackTrace();
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
    }
    if (this.countObjs() == 0) {
      logger.fine("Moindres carres : pas d objet a traiter");
      return;
    }
    logger.fine("Moindres carres : " + this.countObjs() + " objets a traiter");

    // on crée les LSPoints de chaque objet
    logger.fine("Moindres carres : on initialise les points");
    try {
      this.initialiserLSPoints();
    } catch (SecurityException e1) {
      e1.printStackTrace();
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    } catch (NoSuchMethodException e1) {
      e1.printStackTrace();
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    } catch (InstantiationException e1) {
      e1.printStackTrace();
    }

    // puis on initialise les contraintes internes
    logger.fine("Moindres carres : on initialise les contraintes externes");
    try {
      this.initialiserContraintesExternes();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // puis on initialise les contraintes internes
    logger
        .fine("Moindres carres : on initialise les contraintes de conflation");
    try {
      this.initialiseConflationConstraints();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    // on assemble alors le système d'équations
    logger.fine("Moindres carres : on assemble les systemes d equation");
    this.assembleSystemesEquation();

    // puis on réalise l'ajustement du système par moindres carrés
    logger.fine("Moindres carres : on fait l ajustement");
    this.systemeGlobal
        .ajustementMoindresCarres(this.getMapspec().getPoidsContraintes());

    logger.finer("solutions : " + this.systemeGlobal.getSolutions().toString());

    // enfin, on met à jour les géométries
    logger.fine("Moindres carres : on met a jour les geometries");
    this.majGeometries(false);

  }

  protected IPoint construitNouveauPoint(IFeature obj, IPoint geomIni,
      Map<LSPoint, IDirectPosition> mapInconnues) {
    // on commence par récupèrer les coordonnées initiales du point
    IDirectPosition coord = geomIni.getPosition();
    // on récupère le LSPoint correspondant
    LSPoint pointLS = this.getPointFromCoord(coord, obj);
    IDirectPosition coordFinales = mapInconnues.get(pointLS);

    // on calcule les nouvelles coordonnées
    double newX = pointLS.getFinalPt().getX();
    double newY = pointLS.getFinalPt().getY();
    if (!pointLS.isFixed()
        && coordFinales.distance2D(pointLS.getFinalPt()) > minDispl) {
      newX = pointLS.getIniPt().getX() + coordFinales.getX();
      newY = pointLS.getIniPt().getY() + coordFinales.getY();
    }
    pointLS.setFinalPt(new DirectPosition(newX, newY));
    return pointLS.getFinalPt().toGM_Point();
  }

  protected ILineString construitNouvelleLigne(IFeature obj,
      Map<LSPoint, IDirectPosition> mapInconnues) {
    // on commence par construire la géométrie linéaire vide
    IDirectPositionList newPts = new DirectPositionList();

    for (LSPoint point : this.getMapObjPts().get(obj)) {
      // on récupère les nouvelles coordonnées
      IDirectPosition coordFinales = mapInconnues.get(point);
      // on calcule les nouvelles coordonnées
      double newX = point.getFinalPt().getX();
      double newY = point.getFinalPt().getY();
      if (!point.isFixed()
          && coordFinales.distance2D(point.getFinalPt()) > minDispl) {
        newX = point.getIniPt().getX() + coordFinales.getX();
        newY = point.getIniPt().getY() + coordFinales.getY();
      }
      point.setFinalPt(new DirectPosition(newX, newY));
      // on ajoute un vertex à ces coordonnées
      newPts.add(new DirectPosition(newX, newY));
    }

    return new GM_LineString(newPts);
  }

  protected IPolygon construitNouvelleSurface(IFeature obj, IPolygon geomIni,
      Map<LSPoint, IDirectPosition> mapInconnues) {
    // initialisation des variables
    IDirectPositionList ring = new DirectPositionList();
    ILineString ringIni = geomIni.exteriorLineString();
    HashSet<IRing> innerRings = new HashSet<IRing>();
    for (IRing inner : geomIni.getInterior()) {
      innerRings.add(inner);
    }
    if (this.getObjsMalleables().contains(obj)
        && ringIni.coord().size() < this.getMapObjPts().get(obj).size()) {
      ringIni = LineDensification.densification2(ringIni,
          this.getMapspec().getDensStep());
    }

    // loop on the vertices of initial geometry
    for (IDirectPosition vertex : ringIni.coord()) {
      // on récupère le LSPoint correspondant
      LSPoint point = this.getPointFromCoord(vertex, obj);
      // on récupère les nouvelles coordonnées
      IDirectPosition coordFinales = mapInconnues.get(point);
      // on teste si le point était bien une inconnue
      if (coordFinales == null) {
        // dans ce cas, on ne bouge pas le point
        coordFinales = new DirectPosition(0.0, 0.0);
      }
      // on calcule les nouvelles coordonnées
      double newX = point.getFinalPt().getX();
      double newY = point.getFinalPt().getY();
      if (!point.isFixed()
          && coordFinales.distance2D(point.getFinalPt()) > minDispl) {
        newX = point.getIniPt().getX() + coordFinales.getX();
        newY = point.getIniPt().getY() + coordFinales.getY();
      }
      point.setFinalPt(new DirectPosition(newX, newY));
      // on ajoute un vertex à ces coordonnées
      ring.add(new DirectPosition(newX, newY));
    }
    // on ferme le ring
    if (ring.size() == 0) {
      return geomIni;
    }
    IRing ringGeom = new GM_Ring(new GM_LineString(ring));
    if (!ringGeom.validate(0.0)) {
      ring.add(ring.get(0));
      ringGeom = new GM_Ring(new GM_LineString(ring));
    }
    // enfin on construit la surface à partir du ring
    IPolygon newPolygon = new GM_Polygon(ringGeom);

    // ajout des inners rings
    for (IRing innerRingIni : innerRings) {
      IDirectPositionList innerRingCoord = new DirectPositionList();
      // on marque la géométrie initiale
      for (IDirectPosition vertex : innerRingIni.coord()) {
        // on récupère le LSPoint correspondant
        LSPoint point = this.getPointFromCoord(vertex, obj);
        // on récupère les nouvelles coordonnées
        IDirectPosition coordFinales = mapInconnues.get(point);
        // on teste si le point était bien une inconnue
        if (coordFinales == null) {
          // dans ce cas, on ne bouge pas le point
          coordFinales = new DirectPosition(0.0, 0.0);
        }
        // on calcule les nouvelles coordonnées
        double newX = point.getIniPt().getX();
        double newY = point.getIniPt().getY();
        if (!point.isFixed()
            && coordFinales.distance2D(point.getFinalPt()) > minDispl) {
          newX = point.getIniPt().getX() + coordFinales.getX();
          newY = point.getIniPt().getY() + coordFinales.getY();
        }
        point.setFinalPt(new DirectPosition(newX, newY));
        // on ajoute un vertex à ces coordonnées
        innerRingCoord.add(new DirectPosition(newX, newY));
      }
      // on ferme le ring
      if (innerRingCoord.size() == 0) {
        newPolygon.addInterior(innerRingIni);
      }
      IRing innerRingGeom = new GM_Ring(new GM_LineString(innerRingCoord));
      if (!innerRingGeom.validate(0.0)) {
        innerRingCoord.add(innerRingCoord.get(0));
        innerRingGeom = new GM_Ring(new GM_LineString(innerRingCoord));
      }
      newPolygon.addInterior(innerRingGeom);
    }
    return newPolygon;
  }

}
