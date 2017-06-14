/*
 * Créé le 30 mars 2006
 * 
 * Classe définissant les noeuds de type Ellipse
 */

package fr.ign.cogit.cartagen.appli.agents.statetree;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 * @author Taillandier
 * 
 */
public class MyEllipseGraphCell extends DefaultGraphCell {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  /**
	 */
  private int location;

  /**
   * Constructeur
   * 
   * @param userObject nom du noeud
   * @param bounds taille initiale du noeud
   * @param nonValide non validité de l'état
   */
  public MyEllipseGraphCell(Object userObject, Rectangle2D bounds,
      boolean nonValide) {
    super(userObject);
    this.initialize(bounds, nonValide);
  }

  /**
   * Initialise le noeud
   * 
   * @param bounds taille initiale du noeud
   * @param nonValide non Validité de l'état
   */
  private void initialize(Rectangle2D bounds, boolean nonValide) {

    // fixe la couleur du noeud en fonction de la validité de l'état
    if (nonValide) {
      GraphConstants.setBackground(this.getAttributes(), new Color(225, 86, 5));
    } else {
      GraphConstants.setBackground(this.getAttributes(), new Color(174, 241,
          148));
    }
    GraphConstants.setOpaque(this.getAttributes(), true);

    // fixe le style de la bordure
    GraphConstants.setBorder(this.getAttributes(), BorderFactory
        .createLineBorder(Color.BLACK));
    GraphConstants.setBorderColor(this.getAttributes(), Color.BLACK);

    GraphConstants.setSizeable(this.getAttributes(), true);
    GraphConstants.setBounds(this.getAttributes(), bounds);
    this.location = 0;
  }

  /**
   * @return location
   */
  public int getLocation() {
    return this.location;
  }

  /**
   * @param i
   */
  public void setLocation(int i) {
    this.location = i;
  }

}
