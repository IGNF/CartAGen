/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.deeplearning.vector2image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;

/**
 * this class provides images of a road that can be used for deep learning
 * training or prediction.
 * 
 * @author GTouya
 *
 */
public class Roads2Image {

    private IFeatureCollection<? extends IFeature> initialRoads,
            generalisedRoads;
    private String imagePath;
    private Color backgroundColor = Color.WHITE;
    private Color casingColor = Color.BLACK;
    private Color innerColor = Color.RED;
    private int casingWidth = 3, innerWidth = 2;

    public Roads2Image(IFeatureCollection<? extends IFeature> initialRoads,
            IFeatureCollection<? extends IFeature> generalisedRoads,
            String imagePath) {
        super();
        this.initialRoads = initialRoads;
        this.generalisedRoads = generalisedRoads;
        this.setImagePath(imagePath);
    }

    public Roads2Image(IFeatureCollection<? extends IFeature> initialRoads,
            IFeatureCollection<? extends IFeature> generalisedRoads) {
        super();
        this.initialRoads = initialRoads;
        this.generalisedRoads = generalisedRoads;
    }

    @SuppressWarnings("unchecked")
    public List<BufferedImage> createGeneralisedBufferedImages(int imageSize,
            int gap) {
        List<BufferedImage> listImages = new ArrayList<BufferedImage>();

        for (IFeature line : generalisedRoads) {
            ILineString polyline = (ILineString) ((IMultiCurve<IOrientableCurve>) line
                    .getGeom()).get(0);
            // get the transform for the geographic coordinates to the image
            // coordinates
            CoordinateTransformation transform = CoordinateTransformation
                    .getCoordTransfoFromPolyline(polyline, gap, imageSize);
            // System.out.println(transform);
            // Generate a blank RGB image
            BufferedImage bi = new BufferedImage(imageSize, imageSize,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setBackground(this.getBackgroundColor());
            g2d.clearRect(0, 0, imageSize, imageSize);

            IDirectPositionList viewDirectPositionList = transform
                    .transform(line.getGeom().coord());
            // System.out.println(viewDirectPositionList);
            // Shape shape = toPolylineShape(viewDirectPositionList);
            int numPoints = viewDirectPositionList.size();
            int[] xpoints = new int[numPoints];
            int[] ypoints = new int[numPoints];
            for (int i = 0; i < viewDirectPositionList.size(); i++) {
                IDirectPosition p = viewDirectPositionList.get(i);
                xpoints[i] = (int) p.getX();
                ypoints[i] = (int) p.getY();
            }
            g2d.setColor(this.getInnerColor());
            g2d.setStroke(new BasicStroke(this.getInnerWidth()));
            g2d.drawPolyline(xpoints, ypoints, numPoints);

            listImages.add(bi);
        }
        return listImages;
    }

    @SuppressWarnings("unchecked")
    public List<BufferedImage> createInitialBufferedImages(int imageSize,
            int gap) {
        List<BufferedImage> listImages = new ArrayList<BufferedImage>();

        for (IFeature line : generalisedRoads) {
            ILineString polyline = (ILineString) ((IMultiCurve<IOrientableCurve>) line
                    .getGeom()).get(0);
            CoordinateTransformation transform = CoordinateTransformation
                    .getCoordTransfoFromPolyline(polyline, gap, imageSize);
            Collection<IFeature> roads = (Collection<IFeature>) initialRoads
                    .select(polyline.envelope());
            // System.out.println(roads);
            // Generate a blank RGB image
            BufferedImage bi = new BufferedImage(imageSize, imageSize,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setBackground(this.getBackgroundColor());
            g2d.clearRect(0, 0, imageSize, imageSize);

            for (IFeature road : roads) {
                ILineString geom = (ILineString) ((IMultiCurve<IOrientableCurve>) road
                        .getGeom()).get(0);
                IDirectPositionList viewDirectPositionList1 = transform
                        .transform(geom.coord());
                // System.out.println(viewDirectPositionList);
                int numPoints1 = viewDirectPositionList1.size();
                int[] xpoints1 = new int[numPoints1];
                int[] ypoints1 = new int[numPoints1];
                for (int i = 0; i < viewDirectPositionList1.size(); i++) {
                    IDirectPosition p1 = viewDirectPositionList1.get(i);
                    xpoints1[i] = (int) p1.getX();
                    ypoints1[i] = (int) p1.getY();
                }

                g2d.setColor(this.getInnerColor());
                g2d.setStroke(new BasicStroke(this.getInnerWidth()));
                g2d.drawPolyline(xpoints1, ypoints1, numPoints1);
            }

            listImages.add(bi);
        }
        return listImages;
    }

    @SuppressWarnings("unchecked")
    public List<BufferedImage> createStickedBufferedImages(int imageSize,
            int gap) {
        List<BufferedImage> listImages = new ArrayList<BufferedImage>();

        for (IFeature line : generalisedRoads) {
            ILineString polyline = (ILineString) ((IMultiCurve<IOrientableCurve>) line
                    .getGeom()).get(0);
            CoordinateTransformation transform = CoordinateTransformation
                    .getCoordTransfoFromPolyline(polyline, gap, imageSize);
            Collection<IFeature> roads = (Collection<IFeature>) initialRoads
                    .select(polyline.envelope());
            // System.out.println(roads);
            // Generate a blank RGB image
            BufferedImage bi = new BufferedImage(2 * imageSize, imageSize,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setBackground(this.getBackgroundColor());
            g2d.clearRect(0, 0, 2 * imageSize, imageSize);

            // first generate the left part of the image
            IDirectPositionList viewDirectPositionList = transform
                    .transform(line.getGeom().coord());
            // System.out.println(viewDirectPositionList);
            // Shape shape = toPolylineShape(viewDirectPositionList);
            int numPoints = viewDirectPositionList.size();
            int[] xpoints = new int[numPoints];
            int[] ypoints = new int[numPoints];
            for (int i = 0; i < viewDirectPositionList.size(); i++) {
                IDirectPosition p = viewDirectPositionList.get(i);
                xpoints[i] = (int) p.getX();
                ypoints[i] = (int) p.getY();
            }
            g2d.setColor(this.getInnerColor());
            g2d.setStroke(new BasicStroke(this.getInnerWidth()));
            g2d.drawPolyline(xpoints, ypoints, numPoints);

            // then generate the right part of the image with the initial roads
            for (IFeature road : roads) {
                ILineString geom = (ILineString) ((IMultiCurve<IOrientableCurve>) road
                        .getGeom()).get(0);
                IDirectPositionList viewDirectPositionList1 = transform
                        .transform(geom.coord());
                // System.out.println(viewDirectPositionList);
                int numPoints1 = viewDirectPositionList1.size();
                int[] xpoints1 = new int[numPoints1];
                int[] ypoints1 = new int[numPoints1];
                for (int i = 0; i < viewDirectPositionList1.size(); i++) {
                    IDirectPosition p1 = viewDirectPositionList1.get(i);
                    xpoints1[i] = (int) p1.getX() + imageSize;
                    ypoints1[i] = (int) p1.getY();
                }

                g2d.setColor(this.getInnerColor());
                g2d.setStroke(new BasicStroke(this.getInnerWidth()));
                g2d.drawPolyline(xpoints1, ypoints1, numPoints1);
            }

            listImages.add(bi);
        }
        return listImages;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getCasingColor() {
        return casingColor;
    }

    public void setCasingColor(Color casingColor) {
        this.casingColor = casingColor;
    }

    public Color getInnerColor() {
        return innerColor;
    }

    public void setInnerColor(Color innerColor) {
        this.innerColor = innerColor;
    }

    public int getCasingWidth() {
        return casingWidth;
    }

    public void setCasingWidth(int casingWidth) {
        this.casingWidth = casingWidth;
    }

    public int getInnerWidth() {
        return innerWidth;
    }

    public void setInnerWidth(int innerWidth) {
        this.innerWidth = innerWidth;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
