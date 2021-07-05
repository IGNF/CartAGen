/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core;

import java.awt.Color;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.Displacement;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Font;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RotationLabel;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.SLDUtil;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class SLDUtilCartagen extends SLDUtil {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SLDUtilCartagen.class.getName());

	/**
	 * Gets the symbol width of a linear object from the SLD value for this object.
	 * The width value is in terrain meters.
	 * 
	 * @param obj
	 * @return
	 */
	public static double getSymbolMaxWidth(IGeneObjLin obj) {
		String layerName = CartAGenDoc.getInstance().getCurrentDataset().getPopNameFromObj(obj);

		StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset().getSld();

		if (sld == null) {
			return 0.0;
		}
		Layer layer = sld.getLayer(layerName);
		// get the background style (max width is the bottom style width)
		Style style = layer.getStyles().get(0);
		double width = 0.0;
		for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
			Rule rule = ftStyle.getRules().get(0);
			if (rule.getFilter() != null)
				if (!rule.getFilter().evaluate(obj))
					continue;

			for (Symbolizer symbolizer : rule.getSymbolizers()) {
				if (symbolizer instanceof LineSymbolizer)
					width = Math.max(symbolizer.getStroke().getStrokeWidth(), width);
			}
		}
		return width;
	}

	/**
	 * Get the geometry of the symbol of a point feature.
	 * 
	 * @param point
	 * @return
	 */
	public static IPolygon getSymbolGeometry(IGeneObjPoint point) {
		String layerName = CartAGenDoc.getInstance().getCurrentDataset().getPopNameFromObj(point);

		StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset().getSld();

		if (sld == null) {
			return (IPolygon) point.getGeom().buffer(1.0);
		}

		Layer layer = sld.getLayer(layerName);
		Style style = layer.getStyles().get(0);
		IPolygon geom = null;
		for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
			Rule rule = ftStyle.getRules().get(0);
			if (rule.getFilter() != null)
				if (!rule.getFilter().evaluate(point))
					continue;

			for (Symbolizer symbolizer : rule.getSymbolizers()) {
				if (symbolizer instanceof PointSymbolizer) {
					for (Mark mark : ((PointSymbolizer) symbolizer).getGraphic().getMarks()) {

						float size = ((PointSymbolizer) symbolizer).getGraphic().getSize();
						if ((mark.getWellKnownName() == null) || (mark.getWellKnownName().equalsIgnoreCase("square"))) {//$NON-NLS-1$
							IDirectPosition corner = new DirectPosition(point.getGeom().getPosition().getX() - size / 2,
									point.getGeom().getPosition().getY() + size / 2);
							return GeometryFactory.buildRectangle(corner, size, size);
						} else if (mark.getWellKnownName().equalsIgnoreCase("circle")) {//$NON-NLS-1$
							return GeometryFactory.buildCircle(point.getGeom().getPosition(), size, 24);
						} else if (mark.getWellKnownName().equalsIgnoreCase("triangle")) {//$NON-NLS-1$
							IDirectPosition point1 = new DirectPosition(point.getGeom().getPosition().getX(),
									point.getGeom().getPosition().getY() + size);
							IDirectPosition point2 = new DirectPosition(
									point.getGeom().getPosition().getX() - (3 * size / (2 * Math.sqrt(3))),
									point.getGeom().getPosition().getY() - size / 2);
							IDirectPosition point3 = new DirectPosition(
									point.getGeom().getPosition().getX() + (3 * size / (2 * Math.sqrt(3))),
									point.getGeom().getPosition().getY() - size / 2);
							return GeometryFactory.buildTriangle(point1, point2, point3);
						} else if (mark.getWellKnownName().equalsIgnoreCase("star")) {//$NON-NLS-1$
							// TODO
							return GeometryFactory.buildCircle(point.getGeom().getPosition(), size, 24);
						} else if (mark.getWellKnownName().equalsIgnoreCase("cross")) {//$NON-NLS-1$
							// TODO
							return GeometryFactory.buildCircle(point.getGeom().getPosition(), size, 24);
						} else {
							return GeometryFactory.buildCircle(point.getGeom().getPosition(), size, 24);
						}
					}
				}
			}
		}
		return geom;
	}

	/**
	 * Gets the symbol width of a linear object from the SLD value for this object.
	 * The width value is in terrain meters.
	 * 
	 * @param obj
	 * @return
	 */
	public static double getSymbolMaxWidth(IFeature obj) {
		String layerName = CartAGenDoc.getInstance().getCurrentDataset().getPopNameFromObj(obj);

		StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset().getSld();

		if (sld == null) {
			return 0.0;
		}
		Layer layer = sld.getLayer(layerName);
		// get the background style (max width is the bottom style width)
		Style style = layer.getStyles().get(0);
		double width = 0.0;
		for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
			Rule rule = ftStyle.getRules().get(0);
			if (rule.getFilter() != null)
				if (!rule.getFilter().evaluate(obj))
					continue;

			for (Symbolizer symbolizer : rule.getSymbolizers()) {
				if (symbolizer instanceof LineSymbolizer)
					width = Math.max(symbolizer.getStroke().getStrokeWidth(), width);
			}
		}
		return width;
	}

	/**
	 * Gets the innner symbol width of a linear object from the SLD value for this
	 * object. The width value is in terrain meters.
	 * 
	 * @param obj
	 * @return
	 */
	public static double getSymbolInnerWidth(IGeneObjLin obj) {
		String layerName = CartAGenDoc.getInstance().getCurrentDataset().getPopNameFromObj(obj);
		StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset().getSld();
		Layer layer = sld.getLayer(layerName);
		// get the foreground style (inner width is the upper style width)
		Style style = layer.getStyles().get(layer.getStyles().size() - 1);
		double width = Double.MAX_VALUE;
		for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
			Rule rule = ftStyle.getRules().get(0);
			if (rule.getFilter() != null)
				if (!rule.getFilter().evaluate(obj))
					continue;

			for (Symbolizer symbolizer : rule.getSymbolizers()) {
				if (symbolizer instanceof LineSymbolizer)
					width = Math.min(symbolizer.getStroke().getStrokeWidth(), width);
			}
		}
		return width;
	}

	/**
	 * Gets the symbol inner width of a linear object from the SLD value for this
	 * object. The width value is in map millimeters.
	 * 
	 * @param obj
	 * @return
	 */
	public static double getSymbolInnerWidthMapMm(IGeneObjLin obj) {
		double width = getSymbolInnerWidth(obj);
		return width / Legend.getSYMBOLISATI0N_SCALE() * 1000.0;
	}

	/**
	 * Gets the symbol width of a linear object from the SLD value for this object.
	 * The width value is in map millimeters.
	 * 
	 * @param obj
	 * @return
	 */
	public static double getSymbolMaxWidthMapMm(IGeneObjLin obj) {
		double width = getSymbolMaxWidth(obj);
		return width / Legend.getSYMBOLISATI0N_SCALE() * 1000.0;
	}

	/**
	 * Add a user style to display the initial geometry of the features of the given
	 * layer, on top of the symbols.
	 * 
	 * @param obj
	 */
	public static void addInitialGeomDisplay(Layer layer, Color color, int width) {
		UserStyle style = new UserStyle();
		FeatureTypeStyle ftStyle = new FeatureTypeStyle();
		ftStyle.setName("initial geometry");
		style.getFeatureTypeStyles().add(ftStyle);
		Rule rule = new Rule();
		ftStyle.getRules().add(rule);
		IFeature feat = layer.getFeatureCollection().get(0);
		if (feat.getGeom() instanceof IPolygon) {
			Symbolizer symbolizer = new PolygonSymbolizer();
			symbolizer.setGeometryPropertyName("initialGeom");
			Stroke stroke = new Stroke();
			stroke.setColor(color);
			stroke.setStrokeWidth(width);
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			symbolizer.setStroke(stroke);
			rule.getSymbolizers().add(symbolizer);
		} else if (feat.getGeom() instanceof ILineString) {
			Symbolizer symbolizer = new LineSymbolizer();
			symbolizer.setGeometryPropertyName("initialGeom");
			Stroke stroke = new Stroke();
			stroke.setColor(color);
			stroke.setStrokeWidth(width);
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			symbolizer.setStroke(stroke);
			rule.getSymbolizers().add(symbolizer);
		} else if (feat.getGeom() instanceof IPoint) {
			PointSymbolizer symbolizer = new PointSymbolizer();
			symbolizer.setGeometryPropertyName("initialGeom");
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			Graphic graphic = new Graphic();
			Mark mark = new Mark();
			mark.setWellKnownName("cross");
			Fill fill = new Fill();
			fill.setColor(color);
			mark.setFill(fill);
			graphic.getMarks().add(mark);
			symbolizer.setGraphic(graphic);
			rule.getSymbolizers().add(symbolizer);
		}
		System.out.println(style);
		// add the new style to the layer
		layer.getStyles().add(style);
	}

	/**
	 * Removes, if exists, the style that displays the initial geometries of the
	 * layer.
	 * 
	 * @param layer
	 */
	public static void removeInitialGeomDisplay(Layer layer) {
		Style initialStyle = null;
		for (Style style : layer.getStyles()) {
			if ("initial geometry".equals(style.getFeatureTypeStyles().get(0).getName())) {
				initialStyle = style;
				break;
			}
		}
		if (initialStyle != null)
			layer.getStyles().remove(initialStyle);
	}

	/**
	 * Checks if a layer has a style for displaying initial geometries.
	 * 
	 * @param layer
	 * @return
	 */
	public static boolean layerHasInitialDisplay(Layer layer) {
		for (Style style : layer.getStyles()) {
			if ("initial geometry".equals(style.getFeatureTypeStyles().get(0).getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a layer has a style for displaying initial geometries.
	 * 
	 * @param layer
	 * @return
	 */
	public static FeatureTypeStyle getLayerInitialDisplay(Layer layer) {
		for (Style style : layer.getStyles()) {
			if ("initial geometry".equals(style.getFeatureTypeStyles().get(0).getName())) {
				return style.getFeatureTypeStyles().get(0);
			}
		}
		return null;
	}

	/**
	 * Compute a raw SLD (no fill, no complex symbol for roads, etc.) from a given
	 * sld.
	 * 
	 * @param initialSld
	 * @return
	 */
	public static StyledLayerDescriptor computeRawSld(StyledLayerDescriptor initialSld) {
		StyledLayerDescriptor rawSld = new StyledLayerDescriptor(initialSld.getDataSet());
		for (Layer layer : initialSld.getLayers()) {
			NamedLayer newLayer = new NamedLayer(rawSld, layer.getName());
			newLayer.setSld(rawSld);
			newLayer.getStyles().add(computeRawStyle(layer));
			rawSld.add(newLayer);
		}
		return rawSld;
	}

	/**
	 * Compute a SLD that only displays the eliminated features from the layers of a
	 * given sld.
	 * 
	 * @param initialSld
	 * @return
	 */
	public static StyledLayerDescriptor computeEliminatedSld(StyledLayerDescriptor initialSld) {
		StyledLayerDescriptor elimSld = new StyledLayerDescriptor(initialSld.getDataSet());
		for (Layer layer : initialSld.getLayers()) {
			NamedLayer newLayer = new NamedLayer(elimSld, layer.getName());
			newLayer.setSld(elimSld);
			newLayer.getStyles().add(computeEliminatedStyle(layer));
			elimSld.add(newLayer);
		}
		return elimSld;
	}

	private static Style computeRawStyle(Layer layer) {
		Style rawStyle = new UserStyle();
		FeatureTypeStyle ftStyle = new FeatureTypeStyle();
		rawStyle.getFeatureTypeStyles().add(ftStyle);
		Rule rule = new Rule();
		ftStyle.getRules().add(rule);
		Color color = getRawColor(layer);
		if (layer.getSymbolizer() instanceof PointSymbolizer) {
			PointSymbolizer symbolizer = new PointSymbolizer();
			symbolizer.setGeometryPropertyName("geom");
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			Graphic graphic = new Graphic();
			Mark mark = new Mark();
			mark.setWellKnownName("cross");
			Fill fill = new Fill();
			fill.setColor(color);
			mark.setFill(fill);
			graphic.getMarks().add(mark);
			symbolizer.setGraphic(graphic);
			rule.getSymbolizers().add(symbolizer);
		} else if (layer.getSymbolizer() instanceof PolygonSymbolizer) {
			PolygonSymbolizer symbolizer = new PolygonSymbolizer();
			symbolizer.setGeometryPropertyName("geom");
			Stroke stroke = new Stroke();
			stroke.setColor(color);
			stroke.setStrokeWidth(1);
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			symbolizer.setStroke(stroke);
			rule.getSymbolizers().add(symbolizer);
		} else if (layer.getSymbolizer() instanceof LineSymbolizer) {
			Symbolizer symbolizer = new LineSymbolizer();
			symbolizer.setGeometryPropertyName("geom");
			Stroke stroke = new Stroke();
			stroke.setColor(color);
			stroke.setStrokeWidth(2);
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			symbolizer.setStroke(stroke);
			rule.getSymbolizers().add(symbolizer);
		}
		return rawStyle;
	}

	private static Style computeEliminatedStyle(Layer layer) {
		Style rawStyle = new UserStyle();
		FeatureTypeStyle ftStyle = new FeatureTypeStyle();
		rawStyle.getFeatureTypeStyles().add(ftStyle);
		Rule rule = new Rule();
		Filter filter = new PropertyIsEqualTo(new PropertyName("eliminated"), new Literal("true"));
		rule.setFilter(filter);
		ftStyle.getRules().add(rule);
		Color color = getRawColor(layer);
		if (layer.getSymbolizer() instanceof PointSymbolizer) {
			PointSymbolizer symbolizer = new PointSymbolizer();
			symbolizer.setGeometryPropertyName("geom");
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			Graphic graphic = new Graphic();
			Mark mark = new Mark();
			mark.setWellKnownName("cross");
			Fill fill = new Fill();
			fill.setColor(color);
			mark.setFill(fill);
			graphic.getMarks().add(mark);
			symbolizer.setGraphic(graphic);
			rule.getSymbolizers().add(symbolizer);
		} else if (layer.getSymbolizer() instanceof PolygonSymbolizer) {
			PolygonSymbolizer symbolizer = new PolygonSymbolizer();
			symbolizer.setGeometryPropertyName("geom");
			Stroke stroke = new Stroke();
			stroke.setColor(color);
			stroke.setStrokeWidth(1);
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			symbolizer.setStroke(stroke);
			rule.getSymbolizers().add(symbolizer);
		} else if (layer.getSymbolizer() instanceof LineSymbolizer) {
			Symbolizer symbolizer = new LineSymbolizer();
			symbolizer.setGeometryPropertyName("geom");
			Stroke stroke = new Stroke();
			stroke.setColor(color);
			stroke.setStrokeWidth(2);
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			symbolizer.setStroke(stroke);
			rule.getSymbolizers().add(symbolizer);
		}
		return rawStyle;
	}

	private static Color getRawColor(Layer layer) {
		if (layer.getSymbolizer() instanceof PointSymbolizer) {
			if (((PointSymbolizer) layer.getSymbolizer()).getGraphic().getMarks().size() != 0)
				return ((PointSymbolizer) layer.getSymbolizer()).getGraphic().getMarks().get(0).getFill().getColor();
			else
				return Color.BLACK;
		} else if (layer.getSymbolizer() instanceof PolygonSymbolizer) {
			PolygonSymbolizer symb = (PolygonSymbolizer) layer.getSymbolizer();
			if (symb.getFill() != null) {
				return symb.getFill().getColor();
			} else {
				return symb.getStroke().getColor();
			}
		} else if (layer.getSymbolizer() instanceof LineSymbolizer) {
			Color strokeColor = ((LineSymbolizer) layer.getSymbolizer()).getStroke().getColor();
			if (strokeColor.equals(Color.WHITE))
				return Color.BLACK;
			else
				return strokeColor;
		}
		return Color.BLACK;
	}

	/**
	 * Change the symbolisation scale (i.e. also the target scale of generalisation
	 * in CartAGen) and update the SLD symbol size values to the new symbolisation
	 * scale.
	 * 
	 * @param newScale
	 * @param sld
	 */
	public static void changeSymbolisationScale(double newScale, StyledLayerDescriptor sld) {
		double oldScale = Legend.getSYMBOLISATI0N_SCALE();
		Legend.setSYMBOLISATI0N_SCALE(newScale);

		// update the SLD width values with the new scale value
		double scaleRatio = Legend.getSYMBOLISATI0N_SCALE() / oldScale;

		for (Layer layer : sld.getLayers()) {
			for (Style style : layer.getStyles()) {
				for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
					for (Rule rule : ftStyle.getRules()) {
						for (Symbolizer symbolizer : rule.getSymbolizers()) {
							if (symbolizer.getStroke() != null)
								symbolizer.getStroke()
										.setStrokeWidth((float) (symbolizer.getStroke().getStrokeWidth() * scaleRatio));
						}
					}
				}
			}
		}
	}

	/**
	 * Given a list of features, adds a text symbolizer on top of the feature that
	 * gives the order of the feature in a list (e.g. the first feature in the list
	 * has "1" displayed on top of it).
	 * 
	 * @param sld             the SLD instance in which the text symbolizer is added
	 * @param orderedFeatures the ordered list of features.
	 * @param layerName       the name of the layer of the features.
	 * @param color           the color of the text to display.
	 * @param size            the size of the text to display.
	 */
	public static void addOrderTextSymbolizer(StyledLayerDescriptor sld, List<IFeature> orderedFeatures,
			String layerName, Color color, int size) {

		for (Layer layer : sld.getLayers()) {
			if (layer.getName().equals(layerName)) {
				Style style = layer.getStyles().get(0);
				FeatureTypeStyle ftStyle = new FeatureTypeStyle();
				ftStyle.setName("text order");
				// create a rule for each feature
				for (int i = 0; i < orderedFeatures.size(); i++) {
					IFeature feat = orderedFeatures.get(i);
					Rule rule = new Rule();
					// create a Filter to apply this rule only to feat
					Filter filter = new PropertyIsEqualTo(new PropertyName("id"),
							new Literal(String.valueOf(feat.getId())));
					rule.setFilter(filter);

					// ************************************************************
					// create a text symbolizer
					TextSymbolizer symbolizer = new TextSymbolizer();

					// Name definition
					symbolizer.setLabel(String.valueOf(i + 1));

					// Font definition
					Font font = new Font();
					font.setFontSize(size);
					symbolizer.setFont(font);

					// build the label placement
					LabelPlacement placement = new LabelPlacement();
					PointPlacement ptPlacement = new PointPlacement();
					Displacement displ = new Displacement();
					displ.setDisplacementX(0f);
					displ.setDisplacementY(0f);
					ptPlacement.setDisplacement(displ);
					placement.setPlacement(ptPlacement);
					RotationLabel rotationLabel = new RotationLabel();
					rotationLabel.setRotationValue(0f);
					ptPlacement.setRotation(rotationLabel);
					symbolizer.setLabelPlacement(placement);

					// Fill the label
					Fill t_fill = new Fill();
					t_fill.setColor(color);
					symbolizer.setFill(t_fill);

					rule.getSymbolizers().add(symbolizer);
					ftStyle.addRule(rule);
				}
				style.addFeatureTypeStyle(ftStyle);
			}
		}
	}
}
