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
import java.awt.Shape;
import java.awt.image.BufferedImage;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * this class provides images of a block that can be used for deep learning
 * training or prediction.
 * 
 * @author GTouya
 *
 */
public class Block2Image {

    private IUrbanBlock block;
    private String imagePath;

    public Block2Image(IUrbanBlock block, String imagePath) {
        super();
        this.block = block;
        this.imagePath = imagePath;
    }

    public Block2Image(IUrbanBlock block) {
        super();
        this.block = block;
    }

    public BufferedImage createGrayBufferedImage(int imageSize, int gap) {
        // get the geometry of the block
        IPolygon polygon = (IPolygon) block.getGeom();

        // get the transform for the geographic coordinates to
        // the image
        // coordinates
        CoordinateTransformation transform = CoordinateTransformation
                .getCoordTransfoFromPolygon(polygon, gap, imageSize, 0.0);

        // Generate a blank RGB image
        BufferedImage bi = new BufferedImage(imageSize, imageSize,
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = bi.createGraphics();
        g2d.setBackground(Color.LIGHT_GRAY);
        g2d.clearRect(0, 0, imageSize, imageSize);

        IDirectPositionList viewDPList = transform
                .transform(block.getGeom().coord());
        Shape shapeBlock = CoordinateTransformation.toPolygonShape(viewDPList);
        // compute symbol width in pixels
        if (shapeBlock != null) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(shapeBlock);
        }

        // add the buildings shape in the image
        for (IUrbanElement building : block.getUrbanElements()) {
            IDirectPositionList viewDirectPositionList = transform
                    .transform(building.getGeom().coord());
            Shape shape = CoordinateTransformation
                    .toPolygonShape(viewDirectPositionList);
            // compute symbol width in pixels
            if (shape != null) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke());
                g2d.fill(shape);
            }
        }

        return bi;
    }
}
