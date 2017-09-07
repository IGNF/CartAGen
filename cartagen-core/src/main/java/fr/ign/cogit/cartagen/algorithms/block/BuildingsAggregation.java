/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.algorithms.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * Algorithms that aggregate/amalgamate buildings in a block.
 * 
 * @author JGaffuri
 * @author gtouya
 * 
 */
public class BuildingsAggregation {

  /**
   * Algorithms to amalgamate a collection of buildings into one (or several)
   * square amalgamated polygon, using morphological operators. The algorithm is
   * an implementation of the paper from Damen et al. (2008, ICA Workshop in
   * Montpellier). Here, the sequence of operators is closure, then opening,
   * then edge removal.
   * 
   * @param buildings
   * @param bufferSize the size of the buffer for the dilation/erosion
   *          operations.
   * @param edgeLength the minimum length for final edges of the polygon
   *          (smaller edges are removed).
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Collection<IBuilding> computeMorphologicalAmalgamation(
      Collection<IBuilding> buildings, double bufferSize, double edgeLength) {
    // initialise
    Collection<IBuilding> outCollection = new HashSet<>();
    Collection<IPolygon> clusters = new HashSet<>();
    IMultiSurface<IPolygon> multiPolygon = GeometryEngine.getFactory()
        .createMultiPolygon();
    for (IBuilding building : buildings)
      multiPolygon.add(building.getGeom());

    MorphologyTransform morph = new MorphologyTransform(bufferSize, 20);
    morph.setCapForm(BufferParameters.CAP_FLAT);
    IMultiSurface<IPolygon> closedGeom = morph
        .closingMultiPolygon(multiPolygon);
    IGeometry merged = morph.opening(closedGeom);

    if (merged instanceof IPolygon) {
      clusters.add((IPolygon) merged);
    } else if (merged instanceof IMultiSurface) {
      for (IPolygon simple : ((IMultiSurface<IPolygon>) merged).getList())
        clusters.add(simple);
    }

    // from the collection of output polygon, create a new building feature
    // from each of these polygons
    for (IPolygon clusterPolygon : clusters) {

      IPolygon simplified = edgeRemovalSimplification(clusterPolygon,
          edgeLength);

      // simplify the polygon by removing small edges
      IBuilding newBuilding = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB().getGeneObjImpl().getCreationFactory()
          .createBuilding(simplified);
      outCollection.add(newBuilding);
    }
    return outCollection;
  }

  private static IPolygon edgeRemovalSimplification(IPolygon polygon,
      double edgeLength) {

    // first filter unecessary vertices with a Douglas & Peucker filter with a
    // very small threshold.
    IPolygon filteredPol = Filtering.DouglasPeuckerPoly(polygon, 0.2);

    // then initialise the final list of vertices
    IDirectPositionList finalPtList = new DirectPositionList();
    finalPtList.add(filteredPol.coord().get(0));
    List<Segment> segmentList = Segment.getSegmentList(filteredPol,
        filteredPol.coord().get(0));
    Segment previousEdge = segmentList.get(segmentList.size() - 1);
    for (int i = 0; i < segmentList.size(); i++) {
      Segment edge = segmentList.get(i);
      finalPtList.add(edge.getEndPoint());

      // test if edge is too short
      if (edge.length() >= edgeLength)
        continue;

      // arrived here, edge is too short
      Segment nextEdge = null;
      if (i == segmentList.size() - 1)
        nextEdge = segmentList.get(0);
      else
        nextEdge = segmentList.get(i + 1);

      // we compute the angle between previousEdge and nextEdge
      double angle = Math
          .abs(previousEdge.orientation() - nextEdge.orientation());
      if (angle < Math.PI / 4) {
        // offset case
        // make edge a vector
        Vector2D vector = new Vector2D(edge.startPoint(), edge.endPoint());
        // keep last vertex of finalPtList in memory
        IDirectPosition lastVertex = finalPtList.get(finalPtList.size() - 1);
        // remove the last two vertices
        finalPtList.remove(finalPtList.size() - 1);
        finalPtList.remove(finalPtList.size() - 1);
        // get the antepenultimate vertex of finalPtList (which is now
        // the last)
        IDirectPosition antepenultimate = finalPtList
            .get(finalPtList.size() - 1);
        // translate this vertex by vector
        IDirectPosition translated = vector.translate(antepenultimate);
        // add translated and then lastVertex to the list of vertices
        finalPtList.add(translated);
        finalPtList.add(lastVertex);
      } else if (angle < 3 * Math.PI / 4) {
        // corner case
        // get the intersection point of previousEdge and nextEdge
        // considered as straight lines
        IDirectPosition intersection = previousEdge
            .straightLineIntersection(nextEdge);
        // insert the intersection before the last vertex
        finalPtList.add(finalPtList.size() - 2, intersection);
      } else {
        // intrusion or extrusion case
        // make edge a vector
        Vector2D vector = new Vector2D(edge.startPoint(), edge.endPoint());
        // remove the last two vertices
        finalPtList.remove(finalPtList.size() - 1);
        finalPtList.remove(finalPtList.size() - 1);
        IDirectPosition antepenultimate = finalPtList
            .get(finalPtList.size() - 1);
        // translate this vertex by vector
        IDirectPosition translated = vector.translate(antepenultimate);
        finalPtList.add(translated);
        // skip next edge
        finalPtList.add(nextEdge.getEndPoint());
        i++;
      }
      previousEdge = edge;
    }

    return GeometryEngine.getFactory().createIPolygon(finalPtList);
  }

  /**
   * Aggregate buildings that overlap in a given block.
   * 
   * @param ai
   * @return
   */
  public static IBuilding computeBlockAggregation(IUrbanBlock ai) {

    ArrayList<IBuilding> innerBuildings = new ArrayList<IBuilding>();

    for (IUrbanElement urbanElement : ai.getUrbanElements()) {
      if (urbanElement instanceof IBuilding) {
        innerBuildings.add((IBuilding) urbanElement);
      }
    }

    // s'il y a moins de deux batiment, sortir
    if (innerBuildings.size() < 2) {
      return null;
    }

    // recupere le meilleur couple de bâtiments de l'ilot a agreger
    // ce sont les deux batiments qui s'intersectent le plus
    IBuilding ab1_ = null, ab2_ = null;
    double aireIntersectionMax = 0.0;
    for (int i = 1; i < innerBuildings.size(); i++) {
      for (int j = 0; j < i; j++) {
        // recupere deux batiment distincts
        IBuilding ab1 = innerBuildings.get(i);
        IBuilding ab2 = innerBuildings.get(j);

        // si l'un des deux a ete supprime, passe
        if (ab1.isDeleted() || ab2.isDeleted()) {
          continue;
        }

        // calcul de l'aire de leur intersection
        double aireIntersection = ab1.getGeom().intersection(ab2.getGeom())
            .area();

        // si l'aire est superieure a l'aire maximum
        if (aireIntersection > aireIntersectionMax) {
          aireIntersectionMax = aireIntersection;
          ab1_ = ab1;
          ab2_ = ab2;
        }
      }
    }

    // si on n'a pas trouve de couple de batiments qui s'intersectent,
    // sortir
    if (ab1_ == null || ab2_ == null) {
      return null;
    }

    // agrege les batiments: le plus petit est supprime, le plus gros est
    // l'agregat des deux

    // intervertion des deux batiments si le deuxieme est le plus grand
    if (ab1_.getGeom().area() < ab2_.getGeom().area()) {
      IBuilding a = ab1_;
      ab1_ = ab2_;
      ab2_ = a;
    }

    // agregation
    ab1_.setGeom(ab1_.getGeom().union(ab2_.getGeom()));
    // ab1_.etats=null;
    // suppression du plus petit
    ab2_.eliminate();

    return ab1_;

  }

}
