/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.appli.plugins.machinelearning;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * This plugin contains methods to generate different types of training datasets
 * for deep learning techniques.
 * @author gtouya
 *
 */
public class TrainingDatasetGenerator extends JMenu {

  private static int IMAGE_SIZE = 128;
  private static final long serialVersionUID = 1L;
  private static TrainingDatasetGenerator instance = null;
  private static Logger logger = Logger
      .getLogger(TrainingDatasetGenerator.class.getName());

  public TrainingDatasetGenerator() {
    // Exists only to defeat instantiation.
    super();
  }

  public static TrainingDatasetGenerator getInstance() {
    if (TrainingDatasetGenerator.instance == null) {
      TrainingDatasetGenerator.instance = new TrainingDatasetGenerator(
          "Training Data Generator");
    }
    return TrainingDatasetGenerator.instance;
  }

  public TrainingDatasetGenerator(String title) {
    super(title);
    TrainingDatasetGenerator.instance = this;

    JMenu buildingMenu = new JMenu("Building Generalisation");
    JMenu enrichMenu = new JMenu("Data Enrichment");
    this.add(buildingMenu);
    this.add(enrichMenu);
    enrichMenu.add(new BlockImagesAction());
    this.addSeparator();
    ;
    this.add(new ChangeSizeAction());
  }

  private class ChangeSizeAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          // TODO
        }
      });
      th.start();
    }

    public ChangeSizeAction() {
      super();
      this.putValue(Action.NAME, "Change image size");
    }

  }

  /**
   * This action gets all urban block features and generates an image per block,
   * centered on the block. Over an area threshold, the blocks are shrinked by
   * homethety to fit in the image.
   * @author GTouya
   *
   */
  private class BlockImagesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          // get the current dataset
          CartAGenDataSet dataset = CartAGenDoc.getInstance()
              .getCurrentDataset();

          // then loop on the buildings to create example images from each
          for (IUrbanBlock block : dataset.getBlocks()) {
            // get the geometry of the block
            IPolygon polygon = (IPolygon) block.getGeom();

            // get the transform for the geographic coordinates to the image
            // coordinates
            CoordinateTransformation transform = getCoordTransfoFromPolygon(
                polygon, 5, IMAGE_SIZE, 0.0);

            // Generate a blank RGB image
            BufferedImage bi = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE,
                BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setBackground(Color.LIGHT_GRAY);
            g2d.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

            IDirectPositionList viewDPList = transform
                .transform(block.getGeom().coord());
            Shape shapeBlock = toPolygonShape(viewDPList);
            // compute symbol width in pixels
            if (shapeBlock != null) {
              g2d.setColor(Color.DARK_GRAY);
              g2d.setStroke(new BasicStroke(2));
              g2d.draw(shapeBlock);
            }

            /*
             * // add the roads casing shape in the image for (INetworkSection
             * road : block.getSurroundingNetwork()) { IDirectPositionList
             * viewDirectPositionList = transform
             * .transform(road.getGeom().coord()); Shape shape =
             * toPolylineShape(viewDirectPositionList); // compute symbol casing
             * width in pixels double casingWidth =
             * SLDUtilCartagen.getSymbolMaxWidth(road); float pixelCasingWidth =
             * transform.getPixelWidth(casingWidth); //
             * System.out.println(viewDirectPositionList); //
             * System.out.println(pixelCasingWidth); // draw casing if (shape !=
             * null) { g2d.setColor(Color.DARK_GRAY); g2d.setStroke(new
             * BasicStroke(2)); g2d.setPaint(Color.DARK_GRAY); g2d.draw(shape);
             * } }
             * 
             * // add the roads inner shape in the image for (INetworkSection
             * road : block.getSurroundingNetwork()) { IDirectPositionList
             * viewDirectPositionList = transform
             * .transform(road.getGeom().coord()); Shape shape =
             * toPolylineShape(viewDirectPositionList); // compute symbol inner
             * width in pixels double innerWidth =
             * SLDUtilCartagen.getSymbolInnerWidth(road); float pixelInnerWidth
             * = transform.getPixelWidth(innerWidth); // draw inner symbol if
             * (shape != null) { g2d.setColor(Color.LIGHT_GRAY.brighter());
             * g2d.setStroke(new BasicStroke(1));
             * g2d.setPaint(Color.LIGHT_GRAY.brighter()); g2d.draw(shape); } }
             */

            // add the buildings shape in the image
            for (IUrbanElement building : block.getUrbanElements()) {
              IDirectPositionList viewDirectPositionList = transform
                  .transform(building.getGeom().coord());
              Shape shape = toPolygonShape(viewDirectPositionList);
              // compute symbol width in pixels
              if (shape != null) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke());
                g2d.fill(shape);
              }
            }

            File outputfile2 = new File(
                "F://tensorflow//block_" + block.getId() + ".png");
            try {
              ImageIO.write(bi, "png", outputfile2);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      });
      th.start();
    }

    public BlockImagesAction() {
      super();
      this.putValue(Action.NAME, "Generate images of urban blocks");
    }

  }

  /**
   * 
   * @param polygon the polygon to draw in the image
   * @param gap the pixel margin left between the geographic feature and the
   *          border of the image.
   * @param imageSize the size of the image
   * @param maxArea the maximum area over which the polygon is shrinked
   * @return
   */
  private CoordinateTransformation getCoordTransfoFromPolygon(IPolygon polygon,
      int gap, int imageSize, double maxArea) {
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

  class CoordinateTransformation {

    private double xMin, yMin;
    private double homotheticRatio;
    private int gap;
    private int imageSize;

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
  }

  /**
   * Transform a direct position list in view coordinates to an awt shape.
   * 
   * @param viewDirectPositionList a direct position list in view coordinates
   * @return A shape representing the polygon in view coordinates
   */
  private Shape toPolygonShape(
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

  /**
   * Transform a direct position list in view coordinates to an awt shape.
   * 
   * @param viewDirectPositionList a direct position list in view coordinates
   * @return A shape representing the polygon in view coordinates
   */
  private Shape toPolylineShape(
      final IDirectPositionList viewDirectPositionList) {
    Path2D.Double path = new Path2D.Double();
    for (int i = 0; i < viewDirectPositionList.size(); i++) {
      IDirectPosition p = viewDirectPositionList.get(i);
      path.moveTo(p.getX(), p.getY());
    }
    path.closePath();
    return path;
  }
}
