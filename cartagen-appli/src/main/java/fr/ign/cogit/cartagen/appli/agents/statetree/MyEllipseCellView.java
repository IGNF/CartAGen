/*
 * Créé le 30 mars 2006
 * 
 * Classe définissant la façon dont vont être dessiné les noeuds de type ellipse
 */

package fr.ign.cogit.cartagen.appli.agents.statetree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * @author Gaudenz Alder, modification Patrick Taillandier
 * 
 *         This class describes the cell view for the ellipse. it provides a
 *         renderer (JGraphEllipseRenderer) that paints the ellipse. The
 *         renderer is static, so that there is only one instance for each
 *         elipse graph cell type and not for each ellipse graph cell instance
 * 
 *         Ajout de la possibilité d'afficher le label du noeud sur plusieur
 *         ligne
 */
public class MyEllipseCellView extends VertexView {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  /**
	 */
  public static transient JGraphEllipseRenderer rendereur = new JGraphEllipseRenderer();

  /**
	 */
  public MyEllipseCellView() {
    super();
  }

  /**
	 */
  public MyEllipseCellView(Object cell) {
    super(cell);
  }

  /**
   * Returns the intersection of the bounding rectangle and the straight line
   * between the source and the specified point p. The specified point is
   * expected not to intersect the bounds.
   */
  @Override
  public Point2D getPerimeterPoint(Point2D source, Point2D p) {
    Rectangle2D r = this.getBounds();

    double x = r.getX();
    double y = r.getY();
    double a = (r.getWidth() + 1) / 2;
    double b = (r.getHeight() + 1) / 2;

    // x0,y0 - center of ellipse
    double x0 = x + a;
    double y0 = y + b;

    // x1, y1 - point
    double x1 = p.getX();
    double y1 = p.getY();

    // calculate straight line equation through point and ellipse center
    // y = d * x + h
    double dx = x1 - x0;
    double dy = y1 - y0;

    if (dx == 0) {
      return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));
    }

    double d = dy / dx;
    double h = y0 - d * x0;

    // calculate intersection
    double e = a * a * d * d + b * b;
    double f = -2 * x0 * e;
    double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;

    double det = Math.sqrt(f * f - 4 * e * g);

    // two solutions (perimeter points)
    double xout1 = (-f + det) / (2 * e);
    double xout2 = (-f - det) / (2 * e);
    double yout1 = d * xout1 + h;
    double yout2 = d * xout2 + h;

    double dist1Squared = Math.pow((xout1 - x1), 2) + Math.pow((yout1 - y1), 2);
    double dist2Squared = Math.pow((xout2 - x1), 2) + Math.pow((yout2 - y1), 2);

    // correct solution
    double xout, yout;

    if (dist1Squared < dist2Squared) {
      xout = xout1;
      yout = yout1;
    } else {
      xout = xout2;
      yout = yout2;
    }

    return this.getAttributes().createPoint(xout, yout);
  }

  /**
	 */
  @Override
  public CellViewRenderer getRenderer() {
    return MyEllipseCellView.rendereur;
  }

  /**
   * The Vertex Renderer inherits from JLabel. It is responsible for the look of
   * the shape, in this case an ellipse. There is no magic, simply a
   * paint-method that draws the ellipse with the Java Graphics methode
   * drawOval().
   */
  public static class JGraphEllipseRenderer extends JTextPane // pour pouvoir
                                                              // afficher le
                                                              // label sur
                                                              // plusieurs
                                                              // lignes
      implements CellViewRenderer {

    private static final long serialVersionUID = 1L;
    protected transient JGraph graph = null;
    private Color bordercolor;

    transient protected Color gradientColor = null;

    /** Cached hasFocus and selected value. */
    transient protected boolean hasFocus;
    /** Cached hasFocus and selected value. */
    transient protected boolean selected;
    /** Cached hasFocus and selected value. */
    transient protected boolean preview;

    public JGraphEllipseRenderer() {
      // définition du style du label qui sera affiché
      DefaultStyledDocument doc = (DefaultStyledDocument) this.getDocument();
      SimpleAttributeSet attributeSet = new SimpleAttributeSet();
      StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
      StyleConstants.setBold(attributeSet, true);

      doc.setParagraphAttributes(0, doc.getLength(), attributeSet, true);
      this.setDocument(doc);
    }

    @Override
	public Component getRendererComponent(JGraph graphe, CellView view,
        boolean sel, boolean focus, boolean estPreview) {
      this.setText(view.getCell().toString());
      this.graph = graphe;
      this.selected = sel;
      this.preview = estPreview;
      this.hasFocus = focus;
      Map<?, ?> attributes = view.getAllAttributes();
      this.installAttributes(graphe, attributes);
      return this;
    }

    protected void installAttributes(JGraph graphe, Map<?, ?> attributes) {
      this.setOpaque(GraphConstants.isOpaque(attributes));
      Color foreground = GraphConstants.getForeground(attributes);
      this.setForeground(foreground != null ? foreground : graphe
          .getForeground());
      Color background = GraphConstants.getBackground(attributes);
      this.setBackground(background != null ? background : graphe
          .getBackground());
      Font font = GraphConstants.getFont(attributes);
      this.setFont(font != null ? font : graphe.getFont());
      Border border = GraphConstants.getBorder(attributes);
      this.bordercolor = GraphConstants.getBorderColor(attributes);
      if (border != null) {
        this.setBorder(border);
      } else if (this.bordercolor != null) {
        int borderWidth = Math.max(1, Math.round(GraphConstants
            .getLineWidth(attributes)));
        this.setBorder(BorderFactory.createLineBorder(this.bordercolor,
            borderWidth));
      } else if (this.bordercolor == null) {
        this.setBorder(BorderFactory.createLineBorder(null, 0));
      }
      this.gradientColor = GraphConstants.getGradientColor(attributes);
    }

    /**
     * Return a slightly larger preferred size than for a rectangle.
     */
    @Override
    public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      d.width += d.width / 8;
      d.height += d.height / 2;
      return d;
    }

    /**
     * Paints the Ellipse.
     */
    @Override
    public void paint(Graphics g) {
      int b = 2;
      Graphics2D g2 = (Graphics2D) g;
      Dimension d = this.getSize();
      boolean tmp = this.selected;

      // if the GraphCell is set opaque (via GraphConstants.setOpaque(),
      // then paint a background. If a gradient color is set and it is not
      // the preview (during drag&drop of the cell) paint a gradient pane
      if (super.isOpaque()) {
        g.setColor(super.getBackground());
        if (this.gradientColor != null && !this.preview) {
          this.setOpaque(false);
          g2.setPaint(new GradientPaint(0, 0, this.getBackground(), this
              .getWidth(), this.getHeight(), this.gradientColor, true));
        }
        g.fillOval(b - 1, b - 1, d.width - b, d.height - b);
      }

      try {
        this.setBorder(null);
        this.setOpaque(false);
        this.selected = false;
        super.paint(g);
      } finally {
        this.selected = tmp;
      }

      // set linestyle and draw the ellipse with Java Graphics
      if (this.bordercolor != null) {
        g.setColor(this.bordercolor);
        g2.setStroke(new BasicStroke(b));
        g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
      }

      // if the cell is selected, set a stroke linestyle. then draw the
      // ellipse with Java Graphics
      if (this.selected) {
        // g2.setStroke(GraphConstants.SELECTION_STROKE);
        g2.setStroke(new BasicStroke(2 * b));
        g.setColor(Color.magenta);
        g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
        // g.setColor(graph.getLockedHandleColor());
        // g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
      }
    }
  }

}
