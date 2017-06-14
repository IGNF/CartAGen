/**
 * 
 */

/*
 * Créé le 18 dec 2008
 */
package fr.ign.cogit.cartagen.appli.agents;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicAgentState;

/**
 * @author JGaffuri
 * 
 */
public class FrameInfoAgent extends JFrame {
  private static final long serialVersionUID = 1L;
  static final Logger logger = Logger.getLogger(FrameInfoAgent.class.getName());

  private GeographicObjectAgentGeneralisation agentGeo = null;

  private GeographicObjectAgentGeneralisation getAgentGeo() {
    return this.agentGeo;
  }

  public FrameInfoAgent(final GeographicObjectAgentGeneralisation agentGeo) {
    // logger.setLevel(Level.DEBUG);

    this.agentGeo = agentGeo;

    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setResizable(false);

    this.setSize(new Dimension(400, 300));

    this.setLocation(100, 100);
    this.setTitle("CartAGen " + " - infos selection");

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        FrameInfoAgent.this.setVisible(false);
        FrameInfoAgent.this.dispose();
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }
    });

    this.setLayout(new GridBagLayout());

    GridBagConstraints c;
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.insets = new Insets(5, 5, 5, 5);

    if (FrameInfoAgent.logger.isDebugEnabled()) {
      FrameInfoAgent.logger.debug("ajout panneau arbre");
    }
    this.add(this.getScroll(), c);
    if (FrameInfoAgent.logger.isDebugEnabled()) {
      FrameInfoAgent.logger.debug("ajout panneau apercu");
    }
    this.add(this.getJApercu(), c);
    // FIXME this.getJApercu().activate();

    this.pack();

  }

  private JScrollPane scroll = null;

  public JScrollPane getScroll() {
    if (this.scroll == null) {
      /*
       * if (this.agentGeo.getArbreEtats() != null) { // si on dispose des infos
       * nécessaires, on utilise visualisateur // d'arbres d'états de patrick if
       * (FrameInfoAgent.logger.isDebugEnabled()) { FrameInfoAgent.logger.debug(
       * "construction panneau arbre patrick"); } this.scroll = new
       * JScrollPane(new StateTree(this.agentGeo, this),
       * ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
       * ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); } else { if
       * (FrameInfoAgent.logger.isDebugEnabled()) { FrameInfoAgent.logger.debug(
       * "construction panneau arbre"); } this.scroll = new
       * JScrollPane(this.getArbre(),
       * ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
       * ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); }
       * this.scroll.setPreferredSize(new Dimension(500, 350));
       */
      // FIXME
    }
    return this.scroll;
  }

  private JTree arbre;

  public JTree getArbre() {
    if (this.arbre == null) {
      if (FrameInfoAgent.logger.isDebugEnabled()) {
        FrameInfoAgent.logger.debug(
            "construction de l'arbre (appel a fonction recursive su le premier etat qui est l'etat initial)");
      }
      DefaultMutableTreeNode racine = null;
      if (this.getAgentGeo().getRootState() != null) {
        racine = this
            .construireArbreRecurssif(this.getAgentGeo().getRootState());
      }
      this.arbre = new JTree(racine);
      this.arbre.setFont(new Font("Arial", Font.PLAIN, 10));
      this.arbre.getSelectionModel()
          .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      this.arbre.setSelectionRow(0);
      this.arbre.setToggleClickCount(2);

      this.arbre.addTreeSelectionListener(new TreeSelectionListener() {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
          if (FrameInfoAgent.logger.isDebugEnabled()) {
            FrameInfoAgent.logger.debug("affichage de l'etat "
                + ((DefaultMutableTreeNode) FrameInfoAgent.this.getArbre()
                    .getLastSelectedPathComponent()).getUserObject());
          }
          FrameInfoAgent.this
              .getJApercu().etatAAfficher = (GeographicAgentState) ((DefaultMutableTreeNode) FrameInfoAgent.this
                  .getArbre().getLastSelectedPathComponent()).getUserObject();
          if (FrameInfoAgent.logger.isDebugEnabled()) {
            FrameInfoAgent.logger.debug("rafraichissement...");
          }
          // FIXME FrameInfoAgent.this.getJApercu().activate();
          if (FrameInfoAgent.logger.isDebugEnabled()) {
            FrameInfoAgent.logger.debug("fin rafraichissement");
          }
        }
      });

      // deroule l'arbre
      int row = 0;
      while (row < this.getArbre().getRowCount()) {
        this.getArbre().expandRow(row++);
      }
    }
    return this.arbre;
  }

  private PanelVisuInfoAgent jApercu = null;

  public PanelVisuInfoAgent getJApercu() {
    if (this.jApercu == null) {
      if (FrameInfoAgent.logger.isDebugEnabled()) {
        FrameInfoAgent.logger.debug("construction de la fenetre apercu");
      }
      this.jApercu = new PanelVisuInfoAgent();
      this.jApercu.setSize(new Dimension(500, 300));
      this.jApercu.setPreferredSize(new Dimension(500, 300));
      try {
        this.jApercu.getViewport().center(this.getAgentGeo().getFeature());
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
      if (this.getAgentGeo().getRootState() != null) {
        this.jApercu.etatAAfficher = (GeographicAgentState) ((DefaultMutableTreeNode) this
            .getArbre().getLastSelectedPathComponent()).getUserObject();
      }
    }
    return this.jApercu;
  }

  /* DEBUT AJOUT PATRICK */
  public PanelVisuInfoAgent getJApercuBase() {
    return this.jApercu;
  }

  /* FIN AJOUT PATRICK */

  /**
   * Construit recursivement un arbre d'etats a partir d'un etat donne
   * @param eag
   * @return
   */
  private DefaultMutableTreeNode construireArbreRecurssif(AgentState eag) {

    // cas ou l'etat n'a pas de successeurs: renvoi du noeud seul
    if (eag.getChildStates() == null || eag.getChildStates().size() == 0) {
      return new DefaultMutableTreeNode(eag);
    }

    // cas ou l'etat a des successeurs: renvoit de partie d'arbre avec appel
    // recursif
    DefaultMutableTreeNode pere = new DefaultMutableTreeNode(eag);
    if (FrameInfoAgent.logger.isTraceEnabled()) {
      FrameInfoAgent.logger
          .trace("appel recurssif pour construction de l'arbre d'états");
    }
    for (AgentState eagFils : eag.getChildStates()) {
      pere.add(this.construireArbreRecurssif(eagFils));
    }
    return pere;

  }

}
