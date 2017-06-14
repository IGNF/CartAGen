/*
 * Créé le 30 mars 2006
 * 
 * Classe dédiée à l'affichage de l'arbre d'états d'un agent
 */

package fr.ign.cogit.cartagen.appli.agents.statetree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.graph.DefaultEdge;

import com.jgraph.layout.tree.JGraphCompactTreeLayout;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.appli.agents.FrameInfoAgent;
import fr.ign.cogit.cartagen.appli.core.GeneralisationRightPanelAgentComplement;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * @author ptaillandier
 * 
 *         classe qui permet de visualiser les arbres d'états obtenus après
 *         généralisation se base sur ma classe Agent, mais peut facilement être
 *         adaptée à d'autre classe d'agent géo Utilise les lib jgraphlayout.jar
 *         et jgraph.jar
 * 
 *         Should be updated with new versions of libraries and Agent modelling.
 * 
 */
@Deprecated
public class StateTree extends JPanel implements GraphSelectionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   */
  JGraph arbre; // arbre des etats

  /**
   */
  private FrameInfoAgent frameInfoAgent;

  // le dictionnaire des états de l'agent
  // Clef : String : nom de l'état -> Valeur : EtatAgent : etat correspondant
  /**
   */
  private Map<String, AgentState> etatsDict = null;

  // nombre de noeuds de l'arbre d'états
  /**
   */
  private int nbNoeud;

  // agent géo pour lequel on trace l'arbre d'états
  /**
   */
  private IGeographicAgent agentLeger;

  /**
   */
  private AgentState etatSelectionne;
  /**
   */
  private String nomEtatSelectionne;

  /**
   */
  JTextPane agentInfotext = new JTextPane();

  // Chemin du dossier images (pour faire des captures d'écran)
  static final public String IMAGES_PATH = System.getProperty("user.dir")
      + "/images/";

  /**
   * Fonction de création des arcs
   * 
   * @param name label de l'arc
   */
  public static DefaultEdge createEdge(String name) {

    // Créer un arc avec pour label le nom donné en paramétre
    DefaultEdge edge = new DefaultEdge();

    // définition des caractéristiques de l'arc
    /*
     * GraphConstants.setLabelAlongEdge(edge.getAttributes(), true);
     * GraphConstants.setLineColor(edge.getAttributes(), new Color(99, 177,
     * 255)); GraphConstants.setLineWidth(edge.getAttributes(), 3); Font theFont
     * = new Font("Arial", Font.PLAIN, 11);
     * GraphConstants.setFont(edge.getAttributes(), theFont);
     * GraphConstants.setForeground(edge.getAttributes(), Color.BLACK);
     * GraphConstants.setSelectable(edge.getAttributes(), false);
     */
    // FIXME

    return edge;
  }

  /**
   * Fonction de création des noeuds
   * 
   * @param name label du noeud 'état est il valide ou non
   */
  public static DefaultGraphCell createVertex(String name, boolean nonValide) {

    // Créer un noeud avec pour label le nom donné en paramétre et
    // une couleur dépandante de la validité de l'état
    MyEllipseGraphCell cell = new MyEllipseGraphCell(name,
        new Rectangle2D.Double(10.0, 10.0, 120.0, 60.0), nonValide);
    /*
     * // Définition du style de l'écriture Font theFont = new Font("Arial",
     * Font.BOLD, 12); GraphConstants.setFont(cell.getAttributes(), theFont);
     * 
     * // définition des caractéristiques des noeuds
     * GraphConstants.setBendable(cell.getAttributes(), true);
     * GraphConstants.setAutoSize(cell.getAttributes(), true);
     * 
     * // Ajout d'un port (pour pouvoir relier des arcs à ce noeud) DefaultPort
     * port = new DefaultPort(); cell.add(port); port.setParent(cell);
     */
    // FIXME
    return cell;
  }

  /**
   * Fonction de paramétrage de l'arbre
   * 
   */
  private void paramTree() {
    this.arbre.setEditable(false);
    this.arbre.setAlignmentX(Component.CENTER_ALIGNMENT);
    this.arbre.setMoveable(false);
    this.arbre.setJumpToDefaultPort(true);
    this.arbre.getSelectionModel().addGraphSelectionListener(this);
  }

  /**
   * Fonction de paramétrage du layout de l'arbre l'arbre (comment vont être
   * disposer les noeuds et les arcs)
   * 
   * @param cells tableau contenant tous les noeuds et les arcs de l'arbre
   */
  private void paramTreeLayout(DefaultGraphCell[] cells) {
    // On cherche la racine de l'arbre (le noeud 1*1)

    int theIndexRoot = 0;
    for (theIndexRoot = 0; theIndexRoot < this.nbNoeud; theIndexRoot++) {
      // on ne récupére que la première partie du nom (11*11) de la
      // cellule que l'on parcours
      String stateName = cells[theIndexRoot].toString().split("\n   S=")[0];
      if (stateName.equals("\n1")) {
        break;
      }
    }
    DefaultGraphCell[] the_root = new DefaultGraphCell[1];
    the_root[0] = cells[theIndexRoot];

    // Définition du layout (comment seront disposés les noeuds et les arcs)
    this.arbre.getGraphLayoutCache().edit(new Hashtable<Object, Object>(), null,
        null, null);
    this.arbre.getGraphLayoutCache().insert(cells);
    MyFacade facade = new MyFacade(this.arbre);
    facade.setOrdered(true);
    JGraphCompactTreeLayout layout = new JGraphCompactTreeLayout();
    layout.setOrientation(SwingConstants.NORTH);
    layout.setNodeDistance(20);
    layout.setLevelDistance(170);
    layout.run(facade);
    Map<?, ?> graphAttrib = facade.createNestedMap(true, true);
    this.arbre.getGraphLayoutCache().edit(graphAttrib);

    this.arbre.setSelectionCell(the_root[0]);
    this.arbre.setSelectionEnabled(true);

  }

  /**
   * Fonction de création et de paramétrage de la fenêtre dans laquelle va
   * s'afficher l'arbre
   * 
   */
  private void creationPanel() {
    JScrollPane jPane = new JScrollPane(this.arbre);

    jPane.setPreferredSize(new Dimension(500, 200));
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.add(this.createToolBar());
    this.add(jPane);
    // this.add(this.creerInfotextPane());
    // this.setSize(500, 700);
    this.setPreferredSize(new Dimension(500, 300));
    this.setSize(new Dimension(500, 300));
    // zoom initiale de visualisation (dépend du nombre de noeuds de l'arbre)
    double zoomInit = Math.min(5.00 / this.nbNoeud, 1);
    this.arbre.setScale(this.arbre.getScale() * zoomInit);

    /*
     * this.setPreferredSize(new Dimension(2000, 1000)); JLabel nomAgent; //
     * contient la classe et l'ID de l'agent nomAgent = new JLabel(description);
     * Font theFont = new Font("Times New Roman", Font.BOLD, 20);
     * nomAgent.setFont(theFont); this.setLayout( new BoxLayout(this,
     * BoxLayout.Y_AXIS));
     * 
     * nomAgent.setBorder(BorderFactory.createLineBorder(Color.BLACK));
     * this.add(nomAgent); this.add(createToolBar()); this.add(arbre);
     * this.setSize(new Dimension(2000, 1000));
     * 
     * // zoom initiale de visualisation (dépend du nombre de noeuds de l'arbre)
     * double zoomInit = Math.min(5.00 / nbNoeud, 1);
     * arbre.setScale(arbre.getScale() * zoomInit);
     */

  }

  /**
   * Fonction de tri permettant de classer les arcs dans le bon ordre
   * 
   */
  public DefaultGraphCell[] triArcs(DefaultGraphCell[] theCells) {
    List<String> cells = new ArrayList<String>();
    /*
     * for (DefaultGraphCell theCell : theCells) { cells.add(((DefaultPort)
     * ((DefaultEdge) theCell).getTarget()).getParent() .toString()); }
     */
    Collections.sort(cells);
    DefaultGraphCell[] theCellsTrie = new DefaultGraphCell[theCells.length];
    /*
     * for (int i = 0; i < theCells.length; i++) { for (DefaultGraphCell theCell
     * : theCells) { if (cells.get(i) .equals(((DefaultPort) ((DefaultEdge)
     * theCell).getTarget()) .getParent().toString())) { theCellsTrie[i] =
     * theCell; break; } } }
     */
    // FIXME
    return theCellsTrie;
  }

  /**
   * Fonction de tri permettant de classer les noeuds dans le bon ordre
   * 
   */
  public DefaultGraphCell[] triNoeuds(DefaultGraphCell[] theCells) {
    List<String> cells = new ArrayList<String>();
    for (DefaultGraphCell theCell : theCells) {
      cells.add(theCell.toString());
    }
    Collections.sort(cells);
    DefaultGraphCell[] theCellsTrie = new DefaultGraphCell[theCells.length];
    for (int i = 0; i < theCells.length; i++) {
      for (DefaultGraphCell theCell : theCells) {
        if (cells.get(i).equals(theCell.toString())) {
          theCellsTrie[i] = theCell;
          break;
        }
      }
    }
    return theCellsTrie;
  }

  /**
   * Fonction de création de la Toolbar (va contenir nos actions de zoom)
   * 
   */
  public JToolBar createToolBar() {
    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    // effectue un zoom de 1.5
    toolbar
        .add(
            new AbstractAction("zoomIn",
                new ImageIcon(StateTree.class
                    .getResource(StateTree.IMAGES_PATH + "zoom.gif").getPath()
                    .replaceAll("%20", " "))) {
              /**
              	 * 
              	 */
              private static final long serialVersionUID = 1L;

              @Override
              public void actionPerformed(ActionEvent e) {
                StateTree.this.arbre
                    .setScale(1.5 * StateTree.this.arbre.getScale());
                if (StateTree.this.arbre.getSelectionCell() != null) {
                  StateTree.this.arbre.scrollCellToVisible(
                      StateTree.this.arbre.getSelectionCell());
                }
              }
            });

    // effectue un dézoomage de 1/1.5
    toolbar
        .add(new AbstractAction("zoomOut",
            new ImageIcon(StateTree.class
                .getResource(StateTree.IMAGES_PATH + "dezoom.gif").getPath()
                .replaceAll("%20", " "))) {
          /**
          	 * 
          	 */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e) {
            StateTree.this.arbre
                .setScale(StateTree.this.arbre.getScale() / 1.5);
            if (StateTree.this.arbre.getSelectionCell() != null) {
              StateTree.this.arbre
                  .scrollCellToVisible(StateTree.this.arbre.getSelectionCell());
            }
          }
        });

    return toolbar;
  }

  /**
   * Renvoie un int représentant la position (l'index) dans le tableau des
   * cellules du père de l'état passé en paramétre
   * 
   * @param cells tableau contenant tous les noeuds et les arcs de l'arbre
   * @param name nom de l'état (du type 1-1)dont on recherche le père
   */
  private int getIndexPreviousState(DefaultGraphCell[] cells, String name) {
    // on parcours le tableau des cellules
    String[] nomEtatSplit = name.split("-");
    String nomEtat = "\n" + nomEtatSplit[0];
    for (int i = 1; i < nomEtatSplit.length - 1; i++) {
      nomEtat += "-" + nomEtatSplit[i];
    }

    for (int j = 1; j <= this.nbNoeud; j++) {
      // on ne récupére que la première partie du nom (1-1) de la
      // cellule que l'on parcours
      String stateName = cells[j - 1].toString().split("\n   S=")[0];
      // on recherche l'index de l'état qui est le père de l'état passé en
      // paramétre
      if (stateName.equals(nomEtat)) {
        return j - 1;
      }
    }
    return -1;
  }

  /**
   * Focntion gérant les événements lors de la sélection d'un noeud
   * 
   * @param event évenement
   */
  @Override
  public void valueChanged(GraphSelectionEvent event) {

    // On ne prend en compte les événements que lorsque le graphe est
    // affiché
    for (int i = 0; i < event.getCells().length; i++) {
      // On ne prend en compte que les événement de selection de
      // noeuds (et pas de désélection)
      if (event.isAddedCell(i)) {
        // on récupére l'état de ce noeud
        StringTokenizer tokenizer = new StringTokenizer(
            event.getCells()[i].toString());
        this.nomEtatSelectionne = tokenizer.nextToken();
        this.etatSelectionne = this.etatsDict.get(this.nomEtatSelectionne);
        if (this.etatSelectionne != null) {
          if (this.frameInfoAgent.getJApercuBase() != null) {
            this.frameInfoAgent
                .getJApercuBase().etatAAfficher = this.etatSelectionne;
            // this.frameInfoAgent.getJApercuBase().activate();
          }
          this.maJInfotextPane();
        }
      }
    }

  }

  /**
   * Constructeur
   * @param agent : Agent géo généralisé pour lequel on trace l'arbre d'états
   */
  public StateTree(GeographicObjectAgentGeneralisation agent,
      FrameInfoAgent frameInfoAgent) {
    super();

    this.etatsDict = new Hashtable<String, AgentState>();
    this.etatSelectionne = agent.getRootState();
    this.nomEtatSelectionne = "1";
    if (GeneralisationRightPanelAgentComplement.getInstance().cStockerEtats
        .isSelected()) {
      this.construireArbreRecurssif(this.etatSelectionne,
          this.nomEtatSelectionne);
      this.agentLeger = agent;
      this.frameInfoAgent = frameInfoAgent;
      this.afficheArbre();
    }
  }

  /**
   * Construit recursivement un arbre d'etats a partir d'un etat donne
   */
  private void construireArbreRecurssif(AgentState etat, String nomEtat) {
    this.etatsDict.put(nomEtat, etat);
    if (etat.getChildStates() == null) {
      return;
    }
    // cas ou l'etat a des successeurs: renvoit de partie d'arbre avec appel
    // recursif
    for (int i = 1; i <= etat.getChildStates().size(); i++) {
      this.construireArbreRecurssif(etat.getChildStates().get(i - 1),
          nomEtat + "-" + i);
    }
  }

  /**
   * Fonction principale d'affichage de l'arbre
   * 
   */
  public void afficheArbre() {
    /*
     * // Création du model de graphe GraphModel model = new
     * DefaultGraphModel(); // le "cell view factory" renvoie la vue par défauts
     * pour les noeuds, // les arcs et les ports // Cette vue a été surcharger
     * (voir class MyCellViewFactory) CellViewFactory cvf = new
     * MyCellViewFactory(); GraphLayoutCache glc = new GraphLayoutCache(model,
     * cvf);
     * 
     * // Création de l'arbre et définition de ses caractéristiques this.arbre =
     * new JGraph(model, glc); this.paramTree();
     * 
     * if (this.etatsDict != null) { this.nbNoeud = this.etatsDict.size();
     * AgentState preState; int indexBestState = 0;
     * 
     * // Tableau des noeuds de l'arbre DefaultGraphCell[] noeuds = new
     * DefaultGraphCell[this.nbNoeud];
     * 
     * // Pour chaque état on va créer un noeud int cpt = 1; for (String
     * preStateName : this.etatsDict.keySet()) { preState =
     * this.etatsDict.get(preStateName);
     * 
     * // Creation du noeud double satisfaction = preState.getSatisfaction(); //
     * Arrondissement de la satisfaction a 10^-3 satisfaction *= 1000;
     * satisfaction = (int) (satisfaction + .5); satisfaction /= 1000;
     * 
     * noeuds[this.nbNoeud - cpt] = StateTree.createVertex( "\n" + preStateName
     * + "\n   S=" + satisfaction + "   ",
     * this.agentLeger.getEtatsMap().get(preStateName).isInvalide()); // on
     * cherche à déterminer quel sera le noeud correspondant // au meilleur état
     * 
     * if (this.agentLeger.getMeilleurEtat().equals(preStateName)) {
     * indexBestState = this.nbNoeud - cpt; } cpt++; } // On change la couleur
     * du noeud correspondant au meilleur état
     * GraphConstants.setBackground(noeuds[indexBestState].getAttributes(), new
     * Color(63, 152, 254));
     * 
     * // On va de nouveau parcourir le dictionnaire des états pour // créer les
     * arcs // entier qui va s'incrémenter à chaque création d'arc int k = 0;
     * noeuds = this.triNoeuds(noeuds); // Tableau des arcs de l'arbre
     * DefaultGraphCell arcs[] = null; if (this.nbNoeud > 1) { arcs = new
     * DefaultGraphCell[this.nbNoeud - 1]; } else { return; } for (String
     * stateName : this.etatsDict.keySet()) { AgentState stateCourant =
     * this.etatsDict.get(stateName);
     * 
     * // On ne créer un arc que si l'état courant n'est pas 1 // (qui n'a pas
     * de père) if (stateName != "1") { // On recherche le nom du père int
     * indexPrec = this.getIndexPreviousState(noeuds, stateName); if (indexPrec
     * != -1) { // S'il existe on va le chercher dans le tableau des // cellules
     * int indexCourant = 0; for (int i = 0; i < this.nbNoeud; i++) { String
     * theState = noeuds[i].toString().split("\n   S=")[0]; if (("\n" +
     * stateName).compareTo(theState) == 0) { indexCourant = i; } } DefaultEdge
     * edge; // On va ensuite déterminer le nom du dernier // traitement
     * effectué if (stateCourant.getAction() != null) { String nomAlgo =
     * ((ActionCartagen) stateCourant.getAction()) .getNom();
     * 
     * // création de l'arc edge = StateTree.createEdge(nomAlgo); } else if
     * (stateName.equals("1")) { edge = StateTree.createEdge(""); // cas d'un
     * changement de sous phase (pas de nom // d'algo et état différent de 1) }
     * else { edge = StateTree.createEdge("Changement de sous-phase"); } //
     * Connection de l'arc le noeud courant et son père
     * edge.setSource(noeuds[indexPrec].getChildAt(0));
     * edge.setTarget(noeuds[indexCourant].getChildAt(0)); arcs[k] = edge; k++;
     * } } } // tri des cellules de façon à ce que les noeuds soient dans le //
     * bon ordre arcs = this.triArcs(arcs); // Tableau de cellules qui va
     * contenir tous les noeuds et tous // les arcs de l'arbre
     * DefaultGraphCell[] cells = new DefaultGraphCell[2 * this.nbNoeud - 1];
     * for (int i = 0; i < this.nbNoeud - 1; i++) { cells[i] = arcs[i]; } for
     * (int i = 0; i < this.nbNoeud; i++) { cells[i + this.nbNoeud - 1] =
     * noeuds[i]; }
     * 
     * // définition du Layout this.paramTreeLayout(cells);
     * 
     * // création et paramétrage de la fenêtre d'affichage de l'arbre
     * this.creationPanel(); }
     */
  }

  private void maJInfotextPane() {
    /*
     * String traitements = ""; // info sur le dernier traitement effectué
     * EtatAgentGeographiqueLeger etatLeger = this.agentLeger.getEtatsMap()
     * .get(this.nomEtatSelectionne); String etatStr = "Etat : " +
     * this.nomEtatSelectionne + " -> "; if (!etatLeger.isInvalide()) { etatStr
     * += "VALIDE"; } else { etatStr += "INVALIDE"; } // (contrainte le
     * proposant, nom algos, // poids) Action act =
     * this.etatSelectionne.getAction(); if (act != null) { String nomAlgo =
     * ((ActionCartagen) act).getNom(); String contrainteProp =
     * etatLeger.getProposeur();
     * 
     * traitements = "Dernière action appliquée : " + nomAlgo +
     * ":\nContrainte proposant l'action : " + contrainteProp; } String
     * mesuresString = "Valeurs des mesures : \n"; for (String mesure :
     * etatLeger.getMesures().keySet()) { mesuresString += mesure + " : " +
     * etatLeger.getMesures().get(mesure) + "\n"; }
     * 
     * if (traitements.equals("")) { this.agentInfotext.setText(etatStr + "\n\n"
     * + mesuresString); } else { this.agentInfotext .setText(etatStr + "\n" +
     * traitements + "\n\n" + mesuresString); }
     * this.agentInfotext.setCaretPosition(0); this.agentInfotext.repaint(); }
     * 
     * private JScrollPane creerInfotextPane() { // Création et paramétrage du
     * JTextPane qui va contenir toutes ces infos this.agentInfotext = new
     * JTextPane(); DefaultStyledDocument doc = (DefaultStyledDocument)
     * this.agentInfotext .getDocument(); SimpleAttributeSet attributeSet = new
     * SimpleAttributeSet(); StyleConstants.setAlignment(attributeSet,
     * StyleConstants.ALIGN_LEFT); StyleConstants.setFontSize(attributeSet, 14);
     * StyleConstants.setBold(attributeSet, true); doc.setParagraphAttributes(0,
     * doc.getLength(), attributeSet, true);
     * this.agentInfotext.setDocument(doc);
     * this.agentInfotext.setEditable(false);
     * this.agentInfotext.setBorder(BorderFactory.createLineBorder(Color.BLACK))
     * ;
     * 
     * // on place le JtextPane dans un JScrollPane JScrollPane
     * theagentConstraintstextPane = new JScrollPane( this.agentInfotext);
     * theagentConstraintstextPane.setViewportView(this.agentInfotext);
     * theagentConstraintstextPane.setPreferredSize(new Dimension(500, 100));
     * 
     * this.maJInfotextPane(); return theagentConstraintstextPane;
     */
  }

}
