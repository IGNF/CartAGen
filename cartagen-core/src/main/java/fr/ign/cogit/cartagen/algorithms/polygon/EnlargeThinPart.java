package fr.ign.cogit.cartagen.algorithms.polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * Algorithm to enlarge a thin part of a linework from a polygon or a polyline.
 * The algorithm is based on a Delaunay triangulation to identify vertices that
 * are too close to each other. Then, a vector field is computed to distort the
 * line.
 * 
 * @author GTouya
 *
 */
public class EnlargeThinPart {

	private ILineString line;
	private double minimumWidth;
	private int vertexDistance = 15;
	private List<LineString> triangulationEdges;
	private List<LineString> enlargedEdges;
	private Map<IDirectPosition, Vector2D> enlargementVectors;
	private Map<IDirectPosition, Vector2D> vectorField;

	public Map<IDirectPosition, Vector2D> getVectorField() {
		return vectorField;
	}

	public void setVectorField(Map<IDirectPosition, Vector2D> vectorField) {
		this.vectorField = vectorField;
	}

	public EnlargeThinPart(ILineString line, double minimumWidth) {
		super();
		this.line = LineDensification.densification2(line, minimumWidth / 3);
		this.minimumWidth = minimumWidth;
	}

	public ILineString getLine() {
		return line;
	}

	public void setLine(ILineString line) {
		this.line = line;
	}

	public double getMinimumWidth() {
		return minimumWidth;
	}

	public void setMinimumWidth(double minimumWidth) {
		this.minimumWidth = minimumWidth;
	}

	public ILineString enlargeParts() {
		System.out.println("start");
		// first compute the triangulation of the inital geometry
		computeTriangulation();
		System.out.println("triangulation computed");

		// then, compute the triangulation edges that need to be enlarged
		this.enlargedEdges = computeConstrainedEdges();
		System.out.println(enlargedEdges.size() + " edges found");

		// then compute the enlargement vectors from the edges to be enlarged
		this.enlargementVectors = computeEnlargementVectors(enlargedEdges);
		System.out.println(enlargementVectors.size() + " vectors found");

		// then compute the vector field from the enlargement vectors
		this.vectorField = computeVectorField(enlargementVectors);

		// finally generate the enlarged geometry
		IDirectPositionList newGeom = new DirectPositionList();
		for (int i = 0; i < line.coord().size(); i++) {
			IDirectPosition point = line.coord().get(i);

			// if it is the first or last point, it remains unchanged
			if (i == 0) {
				newGeom.add(point);
				continue;
			}
			if (i == line.coord().size() - 1) {
				newGeom.add(point);
				continue;
			}

			// get the vector for this point from the vector field
			Vector2D vector = vectorField.get(point);
			// System.out.println("point " + i);
			// System.out.println(point);
			// System.out.println(vector.direction().getValeur());
			// System.out.println(vector.norme());

			// translate the point with this vector
			IDirectPosition translated = vector.translate(point);
			newGeom.add(translated);
		}

		return new GM_LineString(newGeom);
	}

	private Map<IDirectPosition, Vector2D> computeVectorField(Map<IDirectPosition, Vector2D> enlargementVectors) {
		Map<IDirectPosition, Vector2D> vectorField = new HashMap<>();

		// first loop on points with vectors only
		for (int i = 1; i < line.coord().size() - 1; i++) {
			IDirectPosition point = line.coord().get(i);

			// check if point already has an enlargement vector
			if (enlargementVectors.containsKey(point)) {
				Vector2D vector = enlargementVectors.get(point);

				// add the contributions of potential neigbouring vectors
				// i-1
				Vector2D beforeVector = enlargementVectors.get(line.coord().get(i - 1));
				if (beforeVector != null) {
					// cushion the vector
					double dist = point.distance2D(line.coord().get(i - 1));
					beforeVector.scalarMultiplication(Math.min(1, minimumWidth / dist));
					vector = vector.add(beforeVector);
					if (vector.norme() > minimumWidth / 2)
						vector = vector.changeNorm(minimumWidth / 2);
				}

				// i-2
				if (i - 2 >= 0) {
					Vector2D beforeBeforeVector = enlargementVectors.get(line.coord().get(i - 2));
					if (beforeBeforeVector != null) {
						// cushion the vector
						double dist = point.distance2D(line.coord().get(i - 2));
						beforeBeforeVector.scalarMultiplication(Math.min(1, minimumWidth / dist));
						vector = vector.add(beforeBeforeVector);
						if (vector.norme() > minimumWidth / 2)
							vector = vector.changeNorm(minimumWidth / 2);
					}
				}

				// i + 1
				Vector2D afterVector = enlargementVectors.get(line.coord().get(i + 1));
				if (afterVector != null) {
					// cushion the vector
					double dist = point.distance2D(line.coord().get(i + 1));
					afterVector.scalarMultiplication(Math.min(1, minimumWidth / dist));
					vector = vector.add(afterVector);
					if (vector.norme() > minimumWidth / 2)
						vector = vector.changeNorm(minimumWidth / 2);
				}

				// i + 2
				if (i + 2 < line.coord().size()) {
					Vector2D afterAfterVector = enlargementVectors.get(line.coord().get(i + 2));
					if (afterAfterVector != null) {
						// cushion the vector
						double dist = point.distance2D(line.coord().get(i + 2));
						afterAfterVector.scalarMultiplication(Math.min(1, minimumWidth / dist));
						vector = vector.add(afterAfterVector);
						if (vector.norme() > minimumWidth / 2)
							vector = vector.changeNorm(minimumWidth / 2);
					}
				}

				vectorField.put(point, vector);
			}
		}

		// second loop on points without vectors only
		for (int i = 1; i < line.coord().size() - 1; i++) {
			IDirectPosition point = line.coord().get(i);

			if (enlargementVectors.containsKey(point))
				continue;

			// search for the nearest enlargement vector before point
			Vector2D beforeVector = new Vector2D(new DirectPosition(0, 0));
			double dist = 0.0;
			for (int j = i - 1; j > 0; j--) {
				IDirectPosition before = line.coord().get(j);
				dist += before.distance2D(line.coord().get(j + 1));

				if (enlargementVectors.containsKey(before)) {
					// get the vector in the vector field, not in the initial enlargementVectors map
					beforeVector = vectorField.get(before);
					break;
				}
			}

			// then compute its cushioned contribution to point's displacement
			double newNorm = beforeVector.norme() * Math.min(1.0, minimumWidth / dist);
			Vector2D cushionedBefore = beforeVector.changeNorm(newNorm);

			// search for the nearest enlargement vector after point
			Vector2D afterVector = new Vector2D(new DirectPosition(0, 0));
			double dist2 = 0.0;
			for (int j = i + 1; j < line.coord().size() - 1; j++) {
				IDirectPosition after = line.coord().get(j);
				dist2 += after.distance2D(line.coord().get(j - 1));

				if (enlargementVectors.containsKey(after)) {
					// get the vector in the vector field, not in the initial enlargementVectors map
					afterVector = vectorField.get(after);
					break;
				}
			}

			// then compute its cushioned contribution to point's displacement
			newNorm = afterVector.norme() * Math.min(1.0, minimumWidth / dist2);
			Vector2D cushionedAfter = afterVector.changeNorm(newNorm);

			// finally sum vector contributions from before and after if they are not
			// opposite
			double angleDiff = Math
					.abs(cushionedAfter.direction().getValeur() - cushionedBefore.direction().getValeur());
			Vector2D sumVector = new Vector2D();
			if (angleDiff < Math.PI / 2)
				sumVector = cushionedBefore.add(cushionedAfter);
			else {
				// only use the nearest vector
				if (cushionedAfter.norme() > cushionedBefore.norme())
					sumVector = cushionedAfter;
				else
					sumVector = cushionedBefore;
			}

			// and cap the sum norm with the maximum of the neighbouring enlargement vectors
			sumVector = sumVector
					.changeNorm(Math.min(sumVector.norme(), Math.max(beforeVector.norme(), afterVector.norme())));

			vectorField.put(point, sumVector);
		}
		return vectorField;
	}

	private Map<IDirectPosition, Vector2D> computeEnlargementVectors(List<LineString> enlargedEdges) {
		Map<IDirectPosition, Vector2D> vectorMap = new HashMap<>();

		for (int i = 0; i < line.coord().size(); i++) {
			IDirectPosition point = line.coord().get(i);
			// search for the edges connected to this point
			Vector2D vector = null;
			for (LineString edge : enlargedEdges) {
				if (new DirectPosition(edge.getStartPoint().getX(), edge.getStartPoint().getY()).equals(point)) {
					// create the vector
					double norm = (minimumWidth - edge.getLength()) / 2;
					Angle angle = Angle.angleTroisPoints(
							new DirectPosition(edge.getEndPoint().getX() + 1.0, edge.getEndPoint().getY()),
							new DirectPosition(edge.getEndPoint().getX(), edge.getEndPoint().getY()),
							new DirectPosition(edge.getStartPoint().getX(), edge.getStartPoint().getY()));
					Vector2D newVector = new Vector2D(angle, norm);
					if (vector == null)
						vector = newVector;
					else {
						vector = vector.add(newVector);
						if (vector.norme() > minimumWidth / 2)
							vector = vector.changeNorm(minimumWidth / 2);
						// vector.scalarMultiplication(0.5);
					}
				} else if (new DirectPosition(edge.getEndPoint().getX(), edge.getEndPoint().getY()).equals(point)) {
					// create the vector
					double norm = (minimumWidth - edge.getLength()) / 2;
					Angle angle = Angle.angleTroisPoints(
							new DirectPosition(edge.getStartPoint().getX() + 1.0, edge.getStartPoint().getY()),
							new DirectPosition(edge.getStartPoint().getX(), edge.getStartPoint().getY()),
							new DirectPosition(edge.getEndPoint().getX(), edge.getEndPoint().getY()));
					Vector2D newVector = new Vector2D(angle, norm);
					if (vector == null)
						vector = newVector;
					else {
						vector = vector.add(newVector);
						if (vector.norme() > minimumWidth / 2)
							vector = vector.changeNorm(minimumWidth / 2);
						// vector.scalarMultiplication(0.5);
					}
				}
			}
			if (vector != null)
				vectorMap.put(point, vector);
		}

		return vectorMap;
	}

	private void computeTriangulation() {
		/*
		 * String options = "pczeBQ";
		 * 
		 * List<TriangulationPoint> points = new ArrayList<TriangulationPoint>();
		 * List<TriangulationSegment> segments = new ArrayList<TriangulationSegment>();
		 * List<Segment> objSegments = new ArrayList<Segment>();
		 * objSegments.addAll(Segment.getSegmentList((ILineString) line));
		 * 
		 * // process first point IDirectPosition first = line.startPoint();
		 * TriangulationPoint previous = new TriangulationPointImpl(first);
		 * points.add(previous);
		 * 
		 * for (Segment seg : objSegments) { IDirectPosition pt2 = seg.getEndPoint();
		 * 
		 * TriangulationPoint triPt2 = new TriangulationPointImpl(pt2);
		 * points.add(triPt2);
		 * 
		 * segments.add(new TriangulationSegmentImpl(previous, triPt2)); previous =
		 * triPt2; }
		 * 
		 * triangulation = new Triangulation(points, segments, new
		 * TriangulationSegmentFactoryImpl(), new TriangulationTriangleFactoryImpl());
		 * triangulation.compute(true, null, options);
		 */

		List<Coordinate> coords = new ArrayList<>();
		for (int i = 0; i < line.numPoints(); i++) {
			IDirectPosition point = line.coord().get(i);
			coords.add(new Coordinate(point.getX(), point.getY()));
		}

		DelaunayTriangulationBuilder triangleBuilder = new DelaunayTriangulationBuilder();
		triangleBuilder.setSites(coords);
		GeometryFactory geometryFactory = new GeometryFactory();
		Geometry edges = triangleBuilder.getEdges(geometryFactory);

		triangulationEdges = new ArrayList<>();
		if (edges instanceof GeometryCollection) {
			GeometryCollection geometryCollection = (GeometryCollection) edges;
			for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
				LineString edge = (LineString) geometryCollection.getGeometryN(i);
				triangulationEdges.add(edge);
			}
		}
	}

	/**
	 * Searches the edges of the triangulation that are connected to this point
	 * 
	 * @return
	 */
	private List<LineString> computeConstrainedEdges() {
		List<LineString> constrainedEdges = new ArrayList<>();

		// check if these edges should be constrained, i.e. connect both sides of a
		// thin portion of the geometry.
		for (LineString edge : triangulationEdges) {
			Point point1 = edge.getStartPoint();
			Point point2 = edge.getEndPoint();

			// then remove the long edges
			if (edge.getLength() > this.minimumWidth)
				continue;

			// then remove the short edges between vertices that are too close to each other
			// in the line order.
			int distance = computeVertexDistance(new DirectPosition(point1.getX(), point1.getY()),
					new DirectPosition(point2.getX(), point2.getY()), line);
			if (distance < vertexDistance)
				continue;

			// arrived here, the edge should be constrained
			constrainedEdges.add(edge);
		}

		return constrainedEdges;
	}

	private int computeVertexDistance(IDirectPosition point1, IDirectPosition point2, IGeometry geom) {
		int distance = 0;
		boolean first = false;
		for (int i = 0; i < geom.numPoints(); i++) {
			if (first)
				distance++;
			IDirectPosition point = geom.coord().get(i);

			if (first && (point.equals(point2) || point.equals(point1)))
				break;

			if (!first && (point.equals(point1) || point.equals(point2)))
				first = true;

		}

		return distance;
	}

	public List<LineString> getEnlargedEdges() {
		return enlargedEdges;
	}

	public void setEnlargedEdges(List<LineString> enlargedEdges) {
		this.enlargedEdges = enlargedEdges;
	}

	public Map<IDirectPosition, Vector2D> getEnlargementVectors() {
		return enlargementVectors;
	}

	public void setEnlargementVectors(Map<IDirectPosition, Vector2D> enlargementVectors) {
		this.enlargementVectors = enlargementVectors;
	}
}
