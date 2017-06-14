/*
 * Créé le 30 mars 2006
 * 
 * Classe permettant la surchage de la "view" classique de l'arbre permettant
 * d'avoir des noeuds élliptiques ayant un label de plusieurs lignes
 */

package fr.ign.cogit.cartagen.appli.agents.statetree;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.VertexView;

/**
 * @author Taillandier
 * 
 */
public class MyCellViewFactory extends DefaultCellViewFactory {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  // sucharge de la fonction CreateVertexView de façon à obtenir des noeuds
  // élliptiques multilignes
  @Override
  protected VertexView createVertexView(Object vertex) {

    if (vertex instanceof MyEllipseGraphCell) {
      return new MyEllipseCellView(vertex);
    }

    return super.createVertexView(vertex);
  }

}
