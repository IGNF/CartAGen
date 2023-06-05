/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.dataset.geompool;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Symbolizer;

/**
 * Extension of a GeOxygene default feature with additional information of
 * associated symbolisation - used for default symbolisation of a geometry in
 * the geometries pool
 * @see LayerManager#getGeometriesPoolLayer()
 * @author JRenard april 2012
 * @author CDuchene may 2012
 */

public class ColouredFeature extends DefaultFeature {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  @SuppressWarnings("unused")
  private static Logger logger = LogManager
      .getLogger(ColouredFeature.class.getName());
  private static AtomicInteger idCounter = new AtomicInteger();

  /**
   * Colour of the symbol the feature should be drawn with - used if
   * {@code this.symbolisation} is {@code null}
   */
  private Color symbolColour = null;
  private Color fillColour = null;

  /**
   * The name of the symbol used for point features, "cross" is the default
   * value.
   */
  private String symbolName = "cross";

  private int widthPixels = 1;

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Retrieves the colour of the symbol
   */
  public Color getSymbolColour() {
    return this.symbolColour;
  }

  public String getSymbolName() {
    return symbolName;
  }

  public void setSymbolName(String symbolName) {
    this.symbolName = symbolName;
  }

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * default constructor without colour : the colour will be red
   * @param geom
   */
  public ColouredFeature(IGeometry geom) {
    super(geom);
    this.setId(idCounter.getAndIncrement());
    this.symbolColour = Color.RED;
  }

  /**
   * Constructor with colour. Sets the colour, symbolisation left empty.
   * @param geom
   * @param colour
   */
  public ColouredFeature(IGeometry geom, Color colour) {
    super(geom);
    this.setId(idCounter.getAndIncrement());
    if (colour == null) {
      this.symbolColour = Color.RED;
    } else {
      this.symbolColour = colour;
    }
  }

  /**
   * Constructor with colour. Sets the colour, symbolisation left empty.
   * @param geom
   * @param colour
   */
  public ColouredFeature(IPolygon geom, Color fillColour, Color strokeColour,
      int widthPixels) {
    super(geom);
    this.setId(idCounter.getAndIncrement());
    this.fillColour = fillColour;
    this.symbolColour = strokeColour;
    this.widthPixels = widthPixels;
  }

  /**
   * Constructor with color and width (IN PIXELS). Sets the symbolisation so
   * that: if the geometry is a point, it is drawn as a "+" cross; if it is a
   * line, it is drawn with the colour and width in parameters; if it is a
   * polygon, its border is drawn with the colour and width in parameter and it
   * is filled with the same colour but semi-transparent.
   * 
   * @param geom
   * @param colour
   * @param widthPixels
   */
  public ColouredFeature(IGeometry geom, Color colour, int widthPixels) {
    super(geom);
    this.setId(idCounter.getAndIncrement());
    this.widthPixels = widthPixels;
    // Set colour to red if null
    Color actualColour = colour;
    if (colour == null) {
      actualColour = Color.RED;
    }
    this.symbolColour = actualColour;
  }

  public FeatureTypeStyle computeFeatureStyle() {
    FeatureTypeStyle style = new FeatureTypeStyle();
    style.setName(this.toString() + "-" + this.symbolColour);
    Rule rule = new Rule();
    rule.setFilter(new PropertyIsEqualTo(new PropertyName("id"),
        new Literal(String.valueOf(this.getId()))));
    rule.getSymbolizers().add(computeSymbolizer());
    style.getRules().add(rule);
    return style;
  }

  private Symbolizer computeSymbolizer() {
    if (this.getGeom() instanceof ILineString) {
      Symbolizer symbolizer = new LineSymbolizer();
      symbolizer.setGeometryPropertyName("geom");
      Stroke stroke = new Stroke();
      stroke.setColor(symbolColour);
      stroke.setStrokeWidth(widthPixels);
      symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
      symbolizer.setStroke(stroke);
      return symbolizer;
    } else if (this.getGeom() instanceof IPolygon) {
      PolygonSymbolizer symbolizer = new PolygonSymbolizer();
      symbolizer.setGeometryPropertyName("geom");
      Stroke stroke = new Stroke();
      stroke.setColor(symbolColour);
      stroke.setStrokeWidth(widthPixels);
      symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
      symbolizer.setStroke(stroke);
      Fill fill = new Fill();
      if (fillColour == null)
        fill.setColor(symbolColour);
      else
        fill.setColor(fillColour);
      fill.setFillOpacity((float) 0.5);
      symbolizer.setFill(fill);
      return symbolizer;
    } else if (this.getGeom() instanceof IPoint) {
      PointSymbolizer symbolizer = new PointSymbolizer();
      symbolizer.setGeometryPropertyName("geom");
      symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
      Graphic graphic = new Graphic();
      Mark mark = new Mark();
      mark.setWellKnownName(symbolName);
      Fill fill = new Fill();
      fill.setColor(symbolColour);
      mark.setFill(fill);
      Stroke stroke = new Stroke();
      stroke.setColor(symbolColour);
      stroke.setStrokeWidth(0);
      mark.setStroke(stroke);
      graphic.getMarks().add(mark);
      graphic.setSize(graphic.getSize() * widthPixels);
      symbolizer.setGraphic(graphic);
      return symbolizer;
    } else {
      Symbolizer symbolizer = new LineSymbolizer();
      symbolizer.setGeometryPropertyName("geom");
      Stroke stroke = new Stroke();
      stroke.setColor(symbolColour);
      stroke.setStrokeWidth(widthPixels);
      symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
      symbolizer.setStroke(stroke);
      return symbolizer;
    }
  }
}
