/*
 * Créé le 18 avril 2006
 * 
 * Surcharge de la classe JGraphFacade permettant l'affichage des noeuds dans le
 * bonne ordre
 */

package fr.ign.cogit.cartagen.appli.agents.statetree;

import java.util.Comparator;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;

import com.jgraph.layout.JGraphFacade;

/**
 * @author ptaillandier
 * 
 *         Surcharge de la classe JGraphFacade permettant l'affichage des noeuds
 *         dans le bon ordre
 * 
 */
public class MyFacade extends JGraphFacade {

  /**
   * @param arg0
   */
  public MyFacade(JGraph arg0) {
    super(arg0);
    this.order = new MyComparator();
  }

  public class MyComparator implements Comparator<Object> {

    @Override
	public int compare(Object obj, Object obj1) {
      Object obj2 = this.extracted().getParent(obj);
      Object obj3 = this.extracted().getParent(obj1);
      int i = obj2 != null ? this.extracted().getIndexOfChild(obj2, obj) : this
          .extracted().getIndexOfRoot(obj);
      int j = obj3 != null ? this.extracted().getIndexOfChild(obj3, obj1)
          : this.extracted().getIndexOfRoot(obj1);
      return new Integer(j).compareTo(new Integer(i));
    }

    @SuppressWarnings("synthetic-access")
    private GraphModel extracted() {
      return MyFacade.this.model;
    }

    public MyComparator() {
    }
  }

}
