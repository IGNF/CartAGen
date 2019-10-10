package fr.ign.cogit.cartagen.deeplearning.vector2image;

import java.awt.Polygon;
import java.awt.Shape;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

public class CoordinateTransformation {

    private double xMin, yMin;
    private double homotheticRatio;
    private int gap;
    private int imageSize;

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public double getHomotheticRatio() {
        return homotheticRatio;
    }

    public void setHomotheticRatio(double homotheticRatio) {
        this.homotheticRatio = homotheticRatio;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    CoordinateTransformation(double xMin, double yMin, double homotheticRatio,
            int gap, int imageSize) {
        this.gap = gap;
        this.homotheticRatio = homotheticRatio;
        this.xMin = xMin;
        this.yMin = yMin;
        this.imageSize = imageSize;
    }

    public float getPixelWidth(double groundWidth) {
        return (float) (groundWidth * homotheticRatio);
    }

    IDirectPositionList transform(IDirectPositionList coordinates) {
        IDirectPositionList newCoordinates = new DirectPositionList();
        for (IDirectPosition dp : coordinates) {
            double x = (dp.getX() - xMin) * homotheticRatio + gap;
            double y = (dp.getY() - yMin) * homotheticRatio + gap;
            if (y < imageSize / 2)
                y = y + 2 * (imageSize / 2 - y);
            else
                y = y - 2 * (y - imageSize / 2);
            newCoordinates.add(new DirectPosition(x, y));
        }
        return newCoordinates;
    }

    /**
     * 
     * @param polygon
     *            the polygon to draw in the image
     * @param gap
     *            the pixel margin left between the geographic feature and the
     *            border of the image.
     * @param imageSize
     *            the size of the image
     * @param maxArea
     *            the maximum area over which the polygon is shrinked
     * @return
     */
    static public CoordinateTransformation getCoordTransfoFromPolygon(
            IPolygon polygon, int gap, int imageSize, double maxArea) {
        double enlargementRatio = polygon.area() / maxArea;

        // xMin is 10, xMax is imageSize-10, same for y coordinate.
        // there is a need for a translation and a homothetic transformation
        IEnvelope env = polygon.getEnvelope();
        double xMin = env.minX();
        double yMin = env.minY();
        double ratio = 0.0;
        if (env.width() > env.length()) {
            ratio = (imageSize - 2 * gap) / env.width();
        } else
            ratio = (imageSize - 2 * gap) / env.length();

        if (ratio > 1.0)
            ratio = 1.0;

        if (enlargementRatio < 1.0)
            ratio = ratio * enlargementRatio;

        return new CoordinateTransformation(xMin, yMin, ratio, gap, imageSize);
    }

    /**
     * Computes a transformation between the geographic coordinates of a
     * polyline and the pixel coordinates of an image.
     * 
     * @param polygon
     *            the polygon to draw in the image
     * @param gap
     *            the pixel margin left between the geographic feature and the
     *            border of the image.
     * @param imageSize
     *            the size of the image
     * @return
     */
    static public CoordinateTransformation getCoordTransfoFromPolyline(
            ILineString polyline, int gap, int imageSize) {

        // xMin is 10, xMax is imageSize-10, same for y coordinate.
        // there is a need for a translation and a homothetic transformation
        IEnvelope env = polyline.getEnvelope();
        double xMin = env.minX();
        double yMin = env.minY();
        double ratio = 0.0;
        if (env.width() > env.length()) {
            ratio = (imageSize - 2 * gap) / env.width();
        } else
            ratio = (imageSize - 2 * gap) / env.length();

        if (ratio > 1.0)
            ratio = 1.0;

        return new CoordinateTransformation(xMin, yMin, ratio, gap, imageSize);
    }

    /**
     * Transform a direct position list in view coordinates to an awt shape.
     * 
     * @param viewDirectPositionList
     *            a direct position list in view coordinates
     * @return A shape representing the polygon in view coordinates
     */
    static public Shape toPolygonShape(
            final IDirectPositionList viewDirectPositionList) {
        int numPoints = viewDirectPositionList.size();
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];
        for (int i = 0; i < viewDirectPositionList.size(); i++) {
            IDirectPosition p = viewDirectPositionList.get(i);
            xpoints[i] = (int) p.getX();
            ypoints[i] = (int) p.getY();
        }
        return new Polygon(xpoints, ypoints, numPoints);
    }

}
