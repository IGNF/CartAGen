package fr.ign.cogit.cartagen.algorithms.urbanalignments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/*
 * ###### IGN / CartAGen ###### Title: StraightAlignmentsDetection Description:
 * Detection of building straight alingments based on [Christope & Ruas 2002]
 * Author: J. Renard Date: 03/12/2010
 */

public class StraightAlignmentsDetection {

  // PARAMETERS //

  // Discriminating distance between projections to split building packs
  private static double maxProjDistance = 3.0;

  public static double getMaxProjDistance() {
    return StraightAlignmentsDetection.maxProjDistance;
  }

  public static void setMaxProjDistance(double maxProjDistance) {
    StraightAlignmentsDetection.maxProjDistance = maxProjDistance;
  }

  // Discriminating distance between buildings to split building packs
  private static double maxBuildingDistance = 40.0;

  public static double getMaxBuildingDistance() {
    return StraightAlignmentsDetection.maxBuildingDistance;
  }

  public static void setMaxBuildingDistance(double maxBuildingDistance) {
    StraightAlignmentsDetection.maxBuildingDistance = maxBuildingDistance;
  }

  private static int nbPacksMax = 50;

  /**
   * Compute the Straight Alignments detection in a building block.
   * @param block
   * @param buildings
   * @return
   */
  public static ArrayList<IFeatureCollection<IUrbanElement>> compute(
      IUrbanBlock block, IFeatureCollection<IUrbanElement> buildings) {

    // Determination of anchor point (Xmin,Ymin)
    double Xmin = Double.MAX_VALUE;
    double Ymin = Double.MAX_VALUE;
    for (IDirectPosition point : block.getGeom().coord()) {
      if (point.getX() < Xmin) {
        Xmin = point.getX();
      }
      if (point.getY() < Ymin) {
        Ymin = point.getY();
      }
    }
    IDirectPosition anchor = new DirectPosition(Xmin, Ymin);

    // Creation of projection lines
    ArrayList<AlignmentsDetectionLine> projectionLines = new ArrayList<AlignmentsDetectionLine>();
    for (double angle = 0; angle < 180.0; angle++) {
      AlignmentsDetectionLine projLine = new AlignmentsDetectionLine(anchor,
          angle / 180.0 * Math.PI);
      StraightAlignmentsDetection.createBuildingsPacksOnProjectionLine(projLine,
          buildings);
      projectionLines.add(projLine);
    }

    // Selection of significant building packs, ordered with their size
    HashMap<Integer, ArrayList<IFeatureCollection<IUrbanElement>>> packs = new HashMap<Integer, ArrayList<IFeatureCollection<IUrbanElement>>>();
    for (int i = 3; i < StraightAlignmentsDetection.nbPacksMax; i++) {
      packs.put(i, new ArrayList<IFeatureCollection<IUrbanElement>>());
    }
    for (AlignmentsDetectionLine line : projectionLines) {
      for (IFeatureCollection<IUrbanElement> pack : line.getBuildingPacks()) {
        if (pack.size() > 2) {
          packs.get(pack.size()).add(pack);
        }
      }
    }

    // Elimination of packs based on homogeneity criteria
    for (int i = 3; i < StraightAlignmentsDetection.nbPacksMax; i++) {
      ArrayList<IFeatureCollection<IUrbanElement>> packsToRemove = new ArrayList<IFeatureCollection<IUrbanElement>>();

      for (IFeatureCollection<IUrbanElement> pack : packs.get(i)) {

        // Size criteria
        double[] sizes = new double[i];
        for (int k = 0; k < i; k++) {
          sizes[k] = pack.get(k).getGeom().area();
        }
        Arrays.sort(sizes);
        double median;
        if ((sizes.length % 2) == 0) { // even
          median = (sizes[sizes.length / 2] * sizes[sizes.length / 2 - 1])
              / 2.0;
        } else {
          median = sizes[sizes.length / 2];
        }
        double sommeEcartType = 0.0;
        for (int k = 0; k < i; k++) {
          sommeEcartType += Math.pow(sizes[k] - median, 2.0);
        }
        double ecartType = Math.sqrt(Math.abs(1.0 / i * sommeEcartType));
        if (ecartType > median) {
          packsToRemove.add(pack);
          continue;
        }

        // Distance criteria
        double XMax = pack.get(0).getGeom().centroid().getX();
        double XMin = pack.get(0).getGeom().centroid().getY();
        IUrbanElement buildMax = pack.get(0);
        IUrbanElement buildMin = pack.get(0);
        for (IUrbanElement building : pack) {
          IPolygon geom = building.getSymbolGeom();
          if (geom.centroid().getX() < XMin) {
            XMin = geom.centroid().getX();
            buildMin = building;
          }
          if (geom.centroid().getX() > XMax) {
            XMax = geom.centroid().getX();
            buildMax = building;
          }
        }
        ILineSegment seg = new GM_LineSegment(buildMin.getGeom().centroid(),
            buildMax.getGeom().centroid());
        boolean isToRemove = false;
        for (IUrbanElement building : pack) {
          double distMin1 = Double.MAX_VALUE;
          double distMin2 = Double.MAX_VALUE;
          for (IUrbanElement building2 : pack) {
            if (building2.equals(building)) {
              continue;
            }
            double dist = building.getGeom().distance(building2.getGeom());
            if (dist < distMin1) {
              distMin2 = distMin1;
              distMin1 = dist;
              continue;
            } else if (dist < distMin2) {
              distMin2 = dist;
            }
          }
          if (building.equals(buildMax) || building.equals(buildMin)) {
            if (distMin1 > 1.0 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {
              isToRemove = true;
              break;
            }
          } else {
            if (distMin2 > 1.0 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {
              isToRemove = true;
              break;
            }
          }
        }
        if (isToRemove) {
          packsToRemove.add(pack);
          continue;
        }

        // Overlap of dead ends
        for (INetworkSection section : block.getSurroundingNetwork()) {
          if (section.isDeleted()) {
            continue;
          }
          if (section.getGeom().intersects(seg)) {
            packsToRemove.add(pack);
            break;
          }
        }

      }
      packs.get(i).removeAll(packsToRemove);
    }

    // Elimination of redundant packs - biggest ones are preserved
    for (int i = StraightAlignmentsDetection.nbPacksMax - 1; i > 2; i--) {
      for (IFeatureCollection<IUrbanElement> biggestPack : packs.get(i)) {
        for (int j = 3; j < i; j++) {
          ArrayList<IFeatureCollection<IUrbanElement>> packsToRemove = new ArrayList<IFeatureCollection<IUrbanElement>>();
          for (IFeatureCollection<IUrbanElement> smallestPack : packs.get(j)) {
            if (biggestPack.equals(smallestPack)) {
              continue;
            }
            int compteur = 0;
            for (IUrbanElement building : smallestPack) {
              if (biggestPack.contains(building)) {
                compteur++;
                if (compteur > 1) {
                  break;
                }
              }
            }
            if (compteur > 1) {
              packsToRemove.add(smallestPack);
            }
          }
          packs.get(j).removeAll(packsToRemove);
        }
      }
    }

    // Elimination of redundant packs - packs of the same size
    for (int i = StraightAlignmentsDetection.nbPacksMax - 1; i > 2; i--) {
      int maxSize = packs.get(i).size();
      for (int j = 0; j < maxSize; j++) {
        if (j > packs.get(i).size() - 2) {
          break;
        }
        ArrayList<IFeatureCollection<IUrbanElement>> packsToRemove = new ArrayList<IFeatureCollection<IUrbanElement>>();
        IFeatureCollection<IUrbanElement> biggestPack = packs.get(i).get(j);
        for (int k = j + 1; k < packs.get(i).size(); k++) {
          IFeatureCollection<IUrbanElement> smallestPack = packs.get(i).get(k);
          int compteur = 0;
          for (IUrbanElement building : smallestPack) {
            if (biggestPack.contains(building)) {
              compteur++;
              if (compteur > 1) {
                break;
              }
            }
          }
          if (compteur > 1) {
            packsToRemove.add(smallestPack);
          }
        }
        packs.get(i).removeAll(packsToRemove);
      }
    }

    // // Display for control
    // for (int i=4;i<26;i++) {
    // ArrayList<IFeatureCollection<IBuilding>> packsToRemove = new
    // ArrayList<IFeatureCollection<IBuilding>>();
    // for (IFeatureCollection<IBuilding> pack: packs.get(i)) {
    // double XMax = pack.get(0).getGeom().centroid().getX();
    // double XMin = pack.get(0).getGeom().centroid().getY();
    // IBuilding buildMax = pack.get(0);
    // IBuilding buildMin = pack.get(0);
    // for (IBuilding building: pack) {
    // IPolygon geom = building.getGeom();
    // if (geom.centroid().getX()<XMin) {
    // XMin = geom.centroid().getX();
    // buildMin = building;
    // }
    // if (geom.centroid().getX()>XMax) {
    // XMax = geom.centroid().getX();
    // buildMax = building;
    // }
    // }
    // IDirectPosition pt1 = buildMin.getGeom().centroid();
    // IDirectPosition pt2 = buildMax.getGeom().centroid();
    // ILineSegment seg = new GM_LineSegment(pt1,pt2);
    // CartagenApplication.getInstance().getFrame().getLayerManager().addToGeometriesPool(seg);
    // }
    // packs.get(i).removeAll(packsToRemove);
    // }

    // Returns the arraylist of all alignments
    ArrayList<IFeatureCollection<IUrbanElement>> packsToReturn = new ArrayList<IFeatureCollection<IUrbanElement>>();
    for (int i = 4; i < StraightAlignmentsDetection.nbPacksMax; i++) {
      for (IFeatureCollection<IUrbanElement> pack : packs.get(i)) {
        packsToReturn.add(pack);
      }
    }
    return packsToReturn;

  }

  /**
   * Compute the Straight Alignments detection in a building block. The method
   * uses the Block properties to find the network elements surrounding the
   * block.
   * @param block
   * @param buildings
   * @return
   */
  public static ArrayList<IFeatureCollection<IUrbanElement>> computeNoAgent(
      IUrbanBlock block, IFeatureCollection<IUrbanElement> buildings) {

    // Determination of anchor point (Xmin,Ymin)
    double Xmin = Double.MAX_VALUE;
    double Ymin = Double.MAX_VALUE;
    for (IDirectPosition point : block.getGeom().coord()) {
      if (point.getX() < Xmin) {
        Xmin = point.getX();
      }
      if (point.getY() < Ymin) {
        Ymin = point.getY();
      }
    }
    IDirectPosition anchor = new DirectPosition(Xmin, Ymin);

    // Creation of projection lines
    ArrayList<AlignmentsDetectionLine> projectionLines = new ArrayList<AlignmentsDetectionLine>();
    for (double angle = 0; angle < 180.0; angle++) {
      AlignmentsDetectionLine projLine = new AlignmentsDetectionLine(anchor,
          angle / 180.0 * Math.PI);
      StraightAlignmentsDetection.createBuildingsPacksOnProjectionLine(projLine,
          buildings);
      projectionLines.add(projLine);
    }

    // Selection of significant building packs, ordered with their size
    HashMap<Integer, ArrayList<IFeatureCollection<IUrbanElement>>> packs = new HashMap<Integer, ArrayList<IFeatureCollection<IUrbanElement>>>();
    for (int i = 3; i < StraightAlignmentsDetection.nbPacksMax; i++) {
      packs.put(i, new ArrayList<IFeatureCollection<IUrbanElement>>());
    }
    for (AlignmentsDetectionLine line : projectionLines) {
      for (IFeatureCollection<IUrbanElement> pack : line.getBuildingPacks()) {
        if (pack.size() > 2) {
          packs.get(pack.size()).add(pack);
        }
      }
    }

    // Elimination of packs based on homogeneity criteria
    for (int i = 3; i < StraightAlignmentsDetection.nbPacksMax; i++) {
      ArrayList<IFeatureCollection<IUrbanElement>> packsToRemove = new ArrayList<IFeatureCollection<IUrbanElement>>();

      for (IFeatureCollection<IUrbanElement> pack : packs.get(i)) {

        // Size criteria
        double[] sizes = new double[i];
        for (int k = 0; k < i; k++) {
          sizes[k] = pack.get(k).getGeom().area();
        }
        Arrays.sort(sizes);
        double median;
        if ((sizes.length % 2) == 0) { // even
          median = (sizes[sizes.length / 2] * sizes[sizes.length / 2 - 1])
              / 2.0;
        } else {
          median = sizes[sizes.length / 2];
        }
        double sommeEcartType = 0.0;
        for (int k = 0; k < i; k++) {
          sommeEcartType += Math.pow(sizes[k] - median, 2.0);
        }
        double ecartType = Math.sqrt(Math.abs(1.0 / i * sommeEcartType));
        if (ecartType > median) {
          packsToRemove.add(pack);
          continue;
        }

        // Distance criteria
        double XMax = pack.get(0).getGeom().centroid().getX();
        double XMin = pack.get(0).getGeom().centroid().getY();
        IUrbanElement buildMax = pack.get(0);
        IUrbanElement buildMin = pack.get(0);
        for (IUrbanElement building : pack) {
          IPolygon geom = building.getSymbolGeom();
          if (geom.centroid().getX() < XMin) {
            XMin = geom.centroid().getX();
            buildMin = building;
          }
          if (geom.centroid().getX() > XMax) {
            XMax = geom.centroid().getX();
            buildMax = building;
          }
        }
        ILineSegment seg = new GM_LineSegment(buildMin.getGeom().centroid(),
            buildMax.getGeom().centroid());
        boolean isToRemove = false;
        for (IUrbanElement building : pack) {
          double distMin1 = Double.MAX_VALUE;
          double distMin2 = Double.MAX_VALUE;
          for (IUrbanElement building2 : pack) {
            if (building2.equals(building)) {
              continue;
            }
            double dist = building.getGeom().distance(building2.getGeom());
            if (dist < distMin1) {
              distMin2 = distMin1;
              distMin1 = dist;
              continue;
            } else if (dist < distMin2) {
              distMin2 = dist;
            }
          }
          if (building.equals(buildMax) || building.equals(buildMin)) {
            if (distMin1 > 1.0 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {
              isToRemove = true;
              break;
            }
          } else {
            if (distMin2 > 1.0 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {
              isToRemove = true;
              break;
            }
          }
        }
        if (isToRemove) {
          packsToRemove.add(pack);
          continue;
        }

        // Overlap of dead ends
        for (INetworkSection section : block.getSurroundingNetwork()) {
          if (section.isDeleted()) {
            continue;
          }
          if (section.getGeom().intersects(seg)) {
            packsToRemove.add(pack);
            break;
          }
        }

      }
      packs.get(i).removeAll(packsToRemove);
    }

    // Elimination of redundant packs - biggest ones are preserved
    for (int i = StraightAlignmentsDetection.nbPacksMax - 15; i > 2; i--) {
      for (IFeatureCollection<IUrbanElement> biggestPack : packs.get(i)) {
        for (int j = 3; j < i; j++) {
          ArrayList<IFeatureCollection<IUrbanElement>> packsToRemove = new ArrayList<IFeatureCollection<IUrbanElement>>();
          for (IFeatureCollection<IUrbanElement> smallestPack : packs.get(j)) {
            if (biggestPack.equals(smallestPack)) {
              continue;
            }
            int compteur = 0;
            for (IUrbanElement building : smallestPack) {
              if (biggestPack.contains(building)) {
                compteur++;
                if (compteur > 1) {
                  break;
                }
              }
            }
            if (compteur > 1) {
              packsToRemove.add(smallestPack);
            }
          }
          packs.get(j).removeAll(packsToRemove);
        }
      }
    }

    // Elimination of redundant packs - packs of the same size
    for (int i = StraightAlignmentsDetection.nbPacksMax - 1; i > 2; i--) {
      int maxSize = packs.get(i).size();
      for (int j = 0; j < maxSize; j++) {
        if (j > packs.get(i).size() - 2) {
          break;
        }
        ArrayList<IFeatureCollection<IUrbanElement>> packsToRemove = new ArrayList<IFeatureCollection<IUrbanElement>>();
        IFeatureCollection<IUrbanElement> biggestPack = packs.get(i).get(j);
        for (int k = j + 1; k < packs.get(i).size(); k++) {
          IFeatureCollection<IUrbanElement> smallestPack = packs.get(i).get(k);
          int compteur = 0;
          for (IUrbanElement building : smallestPack) {
            if (biggestPack.contains(building)) {
              compteur++;
              if (compteur > 1) {
                break;
              }
            }
          }
          if (compteur > 1) {
            packsToRemove.add(smallestPack);
          }
        }
        packs.get(i).removeAll(packsToRemove);
      }
    }

    // Returns the arraylist of all alignments
    ArrayList<IFeatureCollection<IUrbanElement>> packsToReturn = new ArrayList<IFeatureCollection<IUrbanElement>>();
    for (int i = 4; i < StraightAlignmentsDetection.nbPacksMax; i++) {
      for (IFeatureCollection<IUrbanElement> pack : packs.get(i)) {
        packsToReturn.add(pack);
      }
    }
    return packsToReturn;

  }

  /**
   * Given a projection line and a collection of buildings, projects the
   * buildings on the line and creates the buildings packs along the line
   * @param line : the line used to project buildings and create packs
   * @param buildings : the collection of buildings
   */

  private static void createBuildingsPacksOnProjectionLine(
      AlignmentsDetectionLine line,
      IFeatureCollection<IUrbanElement> buildings) {

    Hashtable<IDirectPosition, IUrbanElement> projectionsTable = new Hashtable<IDirectPosition, IUrbanElement>();

    // Graphic representation of the projection line
    IDirectPosition pt1 = new DirectPosition(
        line.getAnchor().getX() + 10000 * Math.cos(line.getAngle()),
        line.getAnchor().getY() + 10000 * Math.sin(line.getAngle()));
    IDirectPosition pt2 = new DirectPosition(
        line.getAnchor().getX() - 10000 * Math.cos(line.getAngle()),
        line.getAnchor().getY() - 10000 * Math.sin(line.getAngle()));
    IDirectPositionList ptList = new DirectPositionList();
    ptList.add(pt1);
    ptList.add(pt2);
    ILineString projLine = new GM_LineString(ptList);

    // Creation of the building projections along the line, through graphic
    // intersection
    IDirectPosition minProj = new DirectPosition(Double.MAX_VALUE,
        Double.MAX_VALUE);
    for (IUrbanElement building : buildings) {
      IDirectPosition pt3 = new DirectPosition(
          building.getGeom().centroid().getX()
              - 10000 * Math.sin(line.getAngle()),
          building.getGeom().centroid().getY()
              + 10000 * Math.cos(line.getAngle()));
      IDirectPosition pt4 = new DirectPosition(
          building.getGeom().centroid().getX()
              + 10000 * Math.sin(line.getAngle()),
          building.getGeom().centroid().getY()
              - 10000 * Math.cos(line.getAngle()));
      IDirectPositionList ptListBis = new DirectPositionList();
      ptListBis.add(pt3);
      ptListBis.add(pt4);
      ILineString projCentroid = new GM_LineString(ptListBis);
      if (!(projLine.intersects(projCentroid))) {
        continue;
      }
      IDirectPosition projection = projLine.intersection(projCentroid).coord()
          .get(0);
      projectionsTable.put(projection, building);
      if (projection.getX() < minProj.getX()) {
        minProj = projection;
      }
    }

    // //Different computation of projections (supposed to be faster but not
    // working)
    // double X0 = line.getAnchor().getX();
    // double Y0 = line.getAnchor().getY();
    // double a = line.getAngle();
    // // Creation of the building projections along the line, through graphic
    // intersection
    // IDirectPosition minProj = new
    // DirectPosition(Double.MAX_VALUE,Double.MAX_VALUE);
    // for (IBuilding building: buildings) {
    // double Xc = building.getGeom().centroid().getX();
    // double Yc = building.getGeom().centroid().getY();
    // double Xproj = ( Yc - Y0 + Math.tan(a)*X0 + Xc/Math.tan(a) ) / (
    // Math.tan(a) + 1/Math.tan(a) );
    // double Yproj = Math.tan(a)*Xproj + Y0 - Math.tan(a)*X0 ;
    // IDirectPosition projection = new DirectPosition(Xproj,Yproj);
    // projectionsTable.put(projection, building);
    // if (projection.getX()<minProj.getX()) minProj = projection;
    // CartagenApplication.getInstance().getFrame().getLayerManager().addToGeometriesPool(new
    // GM_Point(projection));
    // }
    // CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
    // try {Thread.sleep(500);} catch (InterruptedException e)
    // {e.printStackTrace();}

    // Creation of the packages according to distance between ordered
    // projections
    // First projected building
    IFeatureCollection<IUrbanElement> buildingPack = new FT_FeatureCollection<IUrbanElement>();
    buildingPack.add(projectionsTable.get(minProj));
    projectionsTable.remove(minProj);
    // Other projected buildings
    while (!projectionsTable.isEmpty()) {
      IDirectPosition nextMinProj = new DirectPosition(Double.MAX_VALUE,
          Double.MAX_VALUE);
      for (IDirectPosition proj : projectionsTable.keySet()) {
        if (proj.getX() < nextMinProj.getX()) {
          nextMinProj = proj;
        }
      }
      if (minProj.distance(
          nextMinProj) > StraightAlignmentsDetection.maxProjDistance) { // next
        // package
        line.addBuildingPack(
            new FT_FeatureCollection<IUrbanElement>(buildingPack));
        buildingPack.clear();
      }
      minProj = nextMinProj;
      buildingPack.add(projectionsTable.get(minProj));
      projectionsTable.remove(minProj);
    }

  }

}
