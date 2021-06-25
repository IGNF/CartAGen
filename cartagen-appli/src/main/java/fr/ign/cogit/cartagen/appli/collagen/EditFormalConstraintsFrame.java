package fr.ign.cogit.cartagen.appli.collagen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.appli.plugins.process.collagen.CollaGenComponent;
import fr.ign.cogit.cartagen.appli.utilities.I18N;
import fr.ign.cogit.cartagen.collagen.resources.ontology.Character;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeneralisationConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicConcept;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.collagen.resources.ontology.ProcessingConcept;
import fr.ign.cogit.cartagen.collagen.resources.specs.CharacterValueType;
import fr.ign.cogit.cartagen.collagen.resources.specs.SimpleOperator;
import fr.ign.cogit.cartagen.collagen.resources.specs.ValueUnit;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConfigExpressionType;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConstraintDatabase;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ConstraintOperator;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ControlExpressionType;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ExpressionType;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalGenConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMacroConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMesoConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalMicroConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.FormalRelationalConstraint;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.MarginExpressionType;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ReductionExpressionType;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.SelectionCriterion;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.SelectionCriterion.Request;
import fr.ign.cogit.cartagen.collagen.resources.specs.constraints.ThreshExpressionType;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.ORConclusion;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.ORPremise;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRule;
import fr.ign.cogit.cartagen.collagen.resources.specs.rules.OperationRulesDatabase;
import fr.ign.cogit.geoxygene.appli.panel.RealLimitator;
import fr.ign.cogit.geoxygene.appli.panel.XMLFileFilter;
import fr.ign.cogit.geoxygene.util.browser.ObjectBrowser;
import fr.ign.cogit.ontology.gui.OntologyBrowserFrame;

@SuppressWarnings("serial")
public class EditFormalConstraintsFrame extends JFrame
    implements ActionListener, ItemListener, CaretListener, MouseListener {

  private ConstraintDatabase cDb;
  private OperationRulesDatabase rDb;
  private OWLOntology ontology;

  private JComboBox<String> cbTypeExpr, cbOperateur, cbOpReq;
  private JComboBox<GeneralisationConcept> cbConcReq;
  private JComboBox<Character> cbCarac, cbCaracRo, cbCaracReq;
  private JRadioButton rdLisib, rdPreserv, rdMicro, rdMeso, rdRel, rdMacro;
  private JTextField txtValeurExpression, txtValReq, txtPourcReq;
  private JTextField txtConcept, txtNom, txtConcept1, txtConcept2;
  private JTextField txtNomRo, txtConceptCRo, txtConceptPRo, txtConceptRp,
      txtValeurRo;
  private JTextField txtEspaceCont, txtEspaceReg;
  private JCheckBox chkConseil, chkPositive;
  private JRadioButton rdProc;
  private JButton btnOnto1, btnOnto2, btnOnto4, btnOnto5;
  private JButton btnPlusRo, btnMoinsRo, btnOntoEspC, btnOntoEspR;
  private JComboBox<String> cbOpRegleOp, cbUniteValeur, cbUniteSelection,
      cbUniteRegle;
  private JSlider sldImp, sldRatioC, sldRatioR, sldImpRo;
  private JLabel lblValExpr, lblExemple, lblUnite, lblUniteSel;
  private JList<FormalGenConstraint> listeMi, listeMe, listeMa, listeRe;
  private JList<ORPremise> listeCond;
  private JList<Request> listeRequetes;
  private JList<OperationRule> listeRO;
  private JTable tableRestC, tableRestR;
  private ButtonGroup bg, bg2;
  private JTabbedPane onglets;
  private JPanel pBdc;
  private Map<String, String> mapTypeEx;
  private String[] typesL;
  private String[] typesP;
  private List<GeneralisationConcept> relatedConcepts = new ArrayList<GeneralisationConcept>();
  private List<Request> requests = new ArrayList<Request>();
  private SelectionCriterion currentCriterion;
  private HashSet<GeneralisationConcept> concepts;
  private Set<ORPremise> currentRulePremise;
  private List<SpaceRestriction> currentConstrRestriction,
      currentRuleRestriction;
  private List<FormalGenConstraint> microList, mesoList, macroList, relList;
  private ChargerBdcXmlAction loadXMLConstraintsAction;
  private ChargerBdrXmlAction loadXMLRulesAction;

  // Variables pour l'internationalisation des interfaces
  private String nomOk, nomAnnuler, nomEnreg, nomSeuil, nomLblSeuil,
      nomControle, nomLblControle;
  private String nomMarge, nomLblMarge, nomReduc, nomLblReduc, nomConfig,
      nomLblConfig;
  private String nomMicro, nomMeso, nomMacro, nomRel, nomMoyen, nomNom,
      nomValeur, nomBaseDonnees;
  private String nomLisib, nomPreserv, nomSimilaire, nomMaintenu, nomForce,
      nomEvite, nomInterdit;
  private String nomSsUnite, nomUniteTerr, nomUniteCarte, nomUniteAngle,
      nomUniteTerr2, nomUniteCarte2;
  private String titreCritSel, ttRatioC, nomEspaceGeo, titreMicro, titreMeso,
      titreMacro, titreRel;
  private String nomEspaceGeoCourt, nomRegle, titreConclusion, titrePremisse;
  private String nomOperConseil, ttOperConseil;
  private String nomOperRequise, ttRatioR, titreContrs, titreReglesOper,
      nomFichier;
  private String nomAide, nomPourcent, nomLblTerrain, nomLblCarte, nomLblAngle,
      nomLblTerrain1;
  private String nomLblCarte1, nomLblCarte2, nomLblTerrain2, popupAffich,
      popupModif, popupSuppr;
  private String nomExportContraintes, ttExportContraintes, nomExportRegles,
      ttExportRegles, nomStats;
  private String ttStatsContraintes, ttStatsReglesOper, ttStatsReglesProc,
      nomEffacer, nomCharger;
  private String titreCharger, ttEffacer, ttCharger, ttChargerR, ttEffacerR;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("annuler")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("ok")) {

      this.setVisible(false);
    } else if (e.getActionCommand().equals("plus")) {
      if (this.btnPlusRo.equals(e.getSource())) {
        GeneralisationConcept elem = GeneralisationConcept
            .getElemGeoFromName(this.txtConceptPRo.getText(), this.concepts);
        Character carac = (Character) this.cbCaracRo.getSelectedItem();
        String valeurS = this.txtValeurRo.getText();
        Object valeur = valeurS;
        if (carac.getDataType().equals(Integer.class)) {
          valeur = Integer.valueOf(valeurS);
        }
        if (carac.getDataType().equals(Double.class)) {
          valeur = Double.valueOf(valeurS);
        }
        if (carac.getDataType().equals(Boolean.class)) {
          valeur = Boolean.valueOf(valeurS);
        }
        SimpleOperator motCle = SimpleOperator.EQUAL;
        if (this.cbOpRegleOp.getSelectedItem().equals(">")) {
          motCle = SimpleOperator.SUP;
        } else if (this.cbOpRegleOp.getSelectedItem().equals(">=")) {
          motCle = SimpleOperator.EQ_SUP;
        } else if (this.cbOpRegleOp.getSelectedItem().equals("<")) {
          motCle = SimpleOperator.INF;
        } else if (this.cbOpRegleOp.getSelectedItem().equals("<=")) {
          motCle = SimpleOperator.EQ_INF;
        }
        ValueUnit unite = ValueUnit.values()[this.cbUniteRegle
            .getSelectedIndex()];
        ORPremise condition = new ORPremise(elem, carac, valeur,
            CharacterValueType.getType(valeur), unite, motCle);
        this.currentRulePremise.add(condition);
        DefaultListModel<ORPremise> dlm = new DefaultListModel<>();
        for (ORPremise r : this.currentRulePremise) {
          dlm.addElement(r);
        }
        this.listeCond.setModel(dlm);
      } else {
        GeneralisationConcept elem = (GeneralisationConcept) this.cbConcReq
            .getSelectedItem();
        Character carac = (Character) this.cbCaracReq.getSelectedItem();
        String valeurS = this.txtValReq.getText();
        Object valeur = valeurS;
        if (carac.getDataType().equals(Integer.class)) {
          valeur = Integer.valueOf(valeurS);
        }
        if (carac.getDataType().equals(Double.class)) {
          valeur = Double.valueOf(valeurS);
        }
        if (carac.getDataType().equals(Boolean.class)) {
          valeur = Boolean.valueOf(valeurS);
        }
        int marge = 0;
        if (!this.txtPourcReq.getText().equals("")) {
          marge = Integer.valueOf(this.txtPourcReq.getText()).intValue();
        }
        Request requete = this.currentCriterion.new Request(
            this.currentCriterion, elem, carac, valeur, marge,
            SimpleOperator.shortcut((String) this.cbOpReq.getSelectedItem()),
            ValueUnit.values()[this.cbUniteSelection.getSelectedIndex()]);
        this.requests.add(requete);
        this.currentCriterion.getRequests().add(requete);
        DefaultListModel<Request> dlm = new DefaultListModel<>();
        for (Request r : this.requests) {
          dlm.addElement(r);
        }
        this.listeRequetes.setModel(dlm);
      }
      this.pack();
    } else if (e.getActionCommand().equals("moins")) {
      if (this.btnMoinsRo.equals(e.getSource())) {
        ORPremise cond = (ORPremise) this.listeCond.getSelectedValue();
        if (cond != null) {
          this.currentRulePremise.remove(cond);
          DefaultListModel<ORPremise> dlm = new DefaultListModel<>();
          for (ORPremise r : this.currentRulePremise) {
            dlm.addElement(r);
          }
          this.listeCond.setModel(dlm);
        }
      } else {
        Request requete = this.listeRequetes.getSelectedValue();
        if (requete != null) {
          this.requests.remove(requete);
          DefaultListModel<Request> dlm = new DefaultListModel<>();
          for (Request r : this.requests) {
            dlm.addElement(r);
          }
          this.listeRequetes.setModel(dlm);
        }
      }
      this.pack();
    } else if (e.getActionCommand().equals("onto")) {
      String root = this.getRootFromConstr();
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology, root);
    } else if (e.getActionCommand().equals("onto1")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology,
          "entité_géographique");
    } else if (e.getActionCommand().equals("onto2")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology,
          "entité_géographique");
    } else if (e.getActionCommand().equals("enregistrer")) {
      // on cr�e l'objet java contrainte
      FormalGenConstraint contr = this.createConstraint();
      this.cDb.addConstraint(contr);
      this.addConstraintToLists(contr);
      this.updateConstraintLists();
      this.initialiserFormulaire();
    } else if (e.getActionCommand().equals("onto3")) {
      String root = null;
      if (this.rdProc.isSelected()) {
        root = "processus_de_généralisation";
      } else {
        root = "espace_géographique";
      }
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology, root);
    } else if (e.getActionCommand().equals("onto4")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology,
          "opération_de_généralisation");
    } else if (e.getActionCommand().equals("onto5")) {
      Set<String> rootsToRemove = new HashSet<String>();
      rootsToRemove.add("opération_de_généralisation");
      rootsToRemove.add("processus_de_généralisation");
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.txtConceptCRo,
          this.ontology, rootsToRemove);
    } else if (e.getActionCommand().equals("onto6")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology, "");
    } else if (e.getActionCommand().equals("ontoProc")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology,
          "processus_de_généralisation");
    } else if (e.getActionCommand().equals("ontoEsp")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology,
          "espace_géographique");
    } else if (e.getActionCommand().equals("enregistrerRo")) {
      // on cr�e l'objet java r�gle
      OperationRule regle = this.createOperationRule();
      this.rDb.getRules().add(regle);
      // mise � jour de la liste
      DefaultListModel<OperationRule> dlm = (DefaultListModel<OperationRule>) this.listeRO
          .getModel();
      dlm.addElement(regle);
      this.listeRO.setModel(dlm);
      this.initialiserFormulaire();
      this.pack();
    } else if (e.getActionCommand().equals("onto_espace_cont")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology,
          "espace_géographique");
    } else if (e.getActionCommand().equals("onto_espace_reg")) {
      @SuppressWarnings("unused")
      OntologyBrowserFrame f = new OntologyBrowserFrame(this.ontology,
          "espace_géographique");
    } else if (e.getActionCommand().equals("plus_cont")) {
      // on ajoute la restriction
      RestrictTableModel model = (RestrictTableModel) this.tableRestC
          .getModel();
      model.ajouterLigne();
      this.tableRestC.setModel(model);
      // on initialise le champ texte et le curseur
      this.txtEspaceCont.setText("");
      this.sldRatioC.setValue(100);
      this.pack();
    } else if (e.getActionCommand().equals("plus_reg")) {
      // on ajoute la restriction
      RestrictTableModel model = (RestrictTableModel) this.tableRestR
          .getModel();
      model.ajouterLigne();
      this.tableRestR.setModel(model);
      // on initialise le champ texte et le curseur
      this.txtEspaceReg.setText("");
      this.sldRatioR.setValue(100);
      this.pack();
    } else if (e.getActionCommand().equals("moins_cont")) {
      // on r�cup�re la ligne s�lectionn�e
      int ligne = this.tableRestC.getSelectedRow();
      RestrictTableModel model = (RestrictTableModel) this.tableRestC
          .getModel();
      model.enleverLigne(ligne);
      this.tableRestC.setModel(model);
      this.pack();
    } else if (e.getActionCommand().equals("moins_reg")) {
      // on r�cup�re la ligne s�lectionn�e
      int ligne = this.tableRestR.getSelectedRow();
      RestrictTableModel model = (RestrictTableModel) this.tableRestR
          .getModel();
      model.enleverLigne(ligne);
      this.tableRestR.setModel(model);
      this.pack();
    }
  }

  /**
   * Add a constraint object to the corresponding list.
   * @param contr
   */
  private void addConstraintToLists(FormalGenConstraint contr) {
    if (FormalMicroConstraint.class.isInstance(contr)) {
      this.microList.add(contr);
    }
    if (FormalMesoConstraint.class.isInstance(contr)) {
      this.mesoList.add(contr);
    }
    if (FormalMacroConstraint.class.isInstance(contr)) {
      this.macroList.add(contr);
    }
    if (FormalRelationalConstraint.class.isInstance(contr)) {
      this.relList.add(contr);
    }
  }

  /**
   * Cette m�hode initialise le formulaire de la frame.
   */
  private void initialiserFormulaire() {
    // ***********************************
    // ON VIDE L'ONGLET CONTRAINTE
    // ***********************************
    // on vide les champs texte
    this.txtNom.setText("");
    this.txtConcept.setEditable(true);
    this.txtConcept.setText("");
    this.txtConcept.setEditable(false);
    this.txtConcept1.setEditable(true);
    this.txtConcept1.setText("");
    this.txtConcept1.setEditable(false);
    this.txtConcept2.setEditable(true);
    this.txtConcept2.setText("");
    this.txtConcept2.setEditable(false);
    this.txtValeurExpression.setText("");
    this.txtPourcReq.setText("");
    this.txtValReq.setText("");

    // on vide la liste
    DefaultListModel<Request> dlm = new DefaultListModel<>();
    this.listeRequetes.setModel(dlm);

    // on vide les combo boxes
    this.cbConcReq.setModel(new DefaultComboBoxModel<GeneralisationConcept>());
    this.cbCaracReq.setModel(new DefaultComboBoxModel<Character>());

    // on vide les collections
    this.requests = new ArrayList<Request>();
    this.relatedConcepts = new ArrayList<GeneralisationConcept>();
    this.currentCriterion = new SelectionCriterion();

    this.sldImp.setValue(1);

    // on initialise les restrictions d'espace
    this.currentConstrRestriction = new ArrayList<SpaceRestriction>();
    this.currentRuleRestriction = new ArrayList<SpaceRestriction>();
    this.txtEspaceCont.setText("");
    this.txtEspaceReg.setText("");
    this.sldRatioC.setValue(100);
    this.sldRatioR.setValue(100);

    // ***********************************
    // ON VIDE L'ONGLET REGLE OPER
    // ***********************************
    this.currentRulePremise = new HashSet<ORPremise>();
    this.listeCond.setModel(new DefaultListModel<>());
    this.txtNomRo.setText("");
    this.txtConceptCRo.setEditable(true);
    this.txtConceptCRo.setText("");
    this.txtConceptCRo.setEditable(false);
    this.txtConceptPRo.setEditable(true);
    this.txtConceptPRo.setText("");
    this.txtConceptPRo.setEditable(false);
    this.txtValeurRo.setText("");
    this.cbCaracRo.setModel(new DefaultComboBoxModel<>());
    this.sldImpRo.setValue(1);
    this.pack();
  }

  /**
   * Update the constraints {@link JList} according to the lists of the frame.
   */
  private void updateConstraintLists() {
    DefaultListModel<FormalGenConstraint> dlm1 = new DefaultListModel<>();
    Collections.sort(this.microList);
    for (FormalGenConstraint c : this.microList) {
      dlm1.addElement(c);
    }
    DefaultListModel<FormalGenConstraint> dlm2 = new DefaultListModel<>();
    Collections.sort(this.mesoList);
    for (FormalGenConstraint c : this.mesoList) {
      dlm2.addElement(c);
    }
    DefaultListModel<FormalGenConstraint> dlm3 = new DefaultListModel<>();
    Collections.sort(this.macroList);
    for (FormalGenConstraint c : this.macroList) {
      dlm3.addElement(c);
    }
    DefaultListModel<FormalGenConstraint> dlm4 = new DefaultListModel<>();
    Collections.sort(this.relList);
    for (FormalGenConstraint c : this.relList) {
      dlm4.addElement(c);
    }
    this.listeMi.setModel(dlm1);
    this.listeMe.setModel(dlm2);
    this.listeMa.setModel(dlm3);
    this.listeRe.setModel(dlm4);
    this.pack();
  }

  /**
   * Update the rules {@link JList} according to the current content of the
   * {@link OperationRulesDatabase} feature of the frame.
   */
  private void updateRulesList() {
    DefaultListModel<OperationRule> dlm1 = new DefaultListModel<>();
    for (OperationRule r : this.rDb.getRules()) {
      dlm1.addElement(r);
    }

    this.listeRO.setModel(dlm1);
    this.pack();
  }

  /**
   * The method creates a {@link FormalGenConstraint} that can be micro or meso,
   * etc. according to the fields of the Swing frame.
   * 
   * @return a Feature instanciating {@link FormalGenConstraint}
   */
  private FormalGenConstraint createConstraint() {
    FormalGenConstraint contr = null;
    // on cr�e le type d'expression de la contrainte
    ExpressionType typeExpr = this.createExpressionType(contr);
    // on cr�e son critère de sélection
    // on commence par construire un Set de requetes
    if (this.currentCriterion == null) {
      this.currentCriterion = new SelectionCriterion();
    }
    this.currentCriterion.setConstraint(contr);
    // on récupère concept et caractère
    GeneralisationConcept concept = this
        .getElemGeoFromName(this.txtConcept.getText());
    Character caractere = (Character) this.cbCarac.getSelectedItem();
    // on récupère l'importance
    int importance = this.sldImp.getValue();
    // on récupère les restrictions
    Map<GeoSpaceConcept, Double> restriction = new HashMap<GeoSpaceConcept, Double>();
    for (SpaceRestriction rest : this.currentConstrRestriction) {
      restriction.put(rest.space, rest.ratio);
    }
    if (this.rdMicro.isSelected()) {
      contr = new FormalMicroConstraint(typeExpr, importance,
          this.currentCriterion, concept, caractere, this.txtNom.getText(),
          this.cDb, restriction);
    } else if (this.rdMeso.isSelected()) {
      contr = new FormalMesoConstraint(typeExpr, importance,
          this.currentCriterion, concept, caractere, this.txtNom.getText(),
          this.cDb, restriction);
    } else if (this.rdMacro.isSelected()) {
      contr = new FormalMacroConstraint(typeExpr, importance,
          this.currentCriterion, concept, caractere, this.txtNom.getText(),
          this.cDb, restriction);
    } else {
      // on récupère les deux concepts de la relation
      GeographicConcept concept1 = (GeographicConcept) this
          .getElemGeoFromName(this.txtConcept1.getText());
      GeographicConcept concept2 = (GeographicConcept) this
          .getElemGeoFromName(this.txtConcept2.getText());
      contr = new FormalRelationalConstraint(typeExpr, importance,
          this.currentCriterion, (GeographicRelation) concept, caractere,
          this.txtNom.getText(), this.cDb, concept1, concept2, restriction);
    }
    return contr;
  }

  /**
   * The method creates a {@link ExpressionType} that can be threshold or
   * margin, etc. according to the fields of the Swing frame, for the given
   * {@link FormalGenConstraint} feature.
   * 
   * @return a feature instanciating {@link ExpressionType}.
   */
  private ExpressionType createExpressionType(FormalGenConstraint contr) {
    ExpressionType type = null;
    // on détermine le motClé
    ConstraintOperator motCle = ConstraintOperator.EQUAL;
    if (this.cbOperateur.getSelectedItem().equals(">")) {
      motCle = ConstraintOperator.SUP;
    } else if (this.cbOperateur.getSelectedItem().equals(">=")) {
      motCle = ConstraintOperator.EQ_SUP;
    } else if (this.cbOperateur.getSelectedItem().equals("<")) {
      motCle = ConstraintOperator.INF;
    } else if (this.cbOperateur.getSelectedItem().equals("<=")) {
      motCle = ConstraintOperator.EQ_INF;
    } else if (this.cbOperateur.getSelectedItem().equals(this.nomInterdit)) {
      motCle = ConstraintOperator.FORBIDDEN;
    } else if (this.cbOperateur.getSelectedItem().equals(this.nomMaintenu)) {
      motCle = ConstraintOperator.MAINTAINED;
    } else if (this.cbOperateur.getSelectedItem().equals(this.nomEvite)) {
      motCle = ConstraintOperator.AVOID;
    } else if (this.cbOperateur.getSelectedItem().equals(this.nomForce)) {
      motCle = ConstraintOperator.FORCE;
    } else if (this.cbOperateur.getSelectedItem().equals(this.nomSimilaire)) {
      motCle = ConstraintOperator.SIMILAR;
    }
    // on r�cup�re le caract�re pour conna�tre son type de donn�es
    Character carac = (Character) this.cbCarac.getSelectedItem();
    String valeurS = this.txtValeurExpression.getText();
    if (this.cbTypeExpr.getSelectedItem().equals(this.nomSeuil)) {
      Object valeur = null;
      if (carac.getDataType().equals(Integer.class)) {
        valeur = Integer.valueOf(valeurS);
      }
      if (carac.getDataType().equals(Double.class)) {
        valeur = Double.valueOf(valeurS);
      }
      if (carac.getDataType().equals(String.class)) {
        valeur = valeurS;
      }
      if (carac.getDataType().equals(Boolean.class)) {
        valeur = Boolean.valueOf(valeurS);
      }
      // on r�cup�re l'unit�
      ValueUnit unite = ValueUnit.values()[this.cbUniteValeur
          .getSelectedIndex()];
      type = new ThreshExpressionType(contr, motCle, valeur, unite);
    } else if (this.cbTypeExpr.getSelectedItem().equals(this.nomControle)) {
      Object valeur = null;
      if (carac.getDataType().equals(Integer.class)) {
        valeur = Integer.valueOf(valeurS);
      }
      if (carac.getDataType().equals(Double.class)) {
        valeur = Double.valueOf(valeurS);
      }
      if (carac.getDataType().equals(String.class)) {
        valeur = valeurS;
      }
      if (carac.getDataType().equals(Boolean.class)) {
        valeur = Boolean.valueOf(valeurS);
      }
      // on r�cup�re l'unit�
      ValueUnit unite = ValueUnit.values()[this.cbUniteValeur
          .getSelectedIndex()];
      type = new ControlExpressionType(contr, motCle, valeur, unite);
    } else if (this.cbTypeExpr.getSelectedItem().equals(this.nomMarge)) {
      double valeur = 0.0;
      if (!valeurS.equals("")) {
        valeur = Double.valueOf(valeurS);
      }
      ValueUnit unite = ValueUnit.values()[this.cbUniteValeur
          .getSelectedIndex()];
      type = new MarginExpressionType(contr, motCle, valeur, unite);
    } else if (this.cbTypeExpr.getSelectedItem().equals(this.nomReduc)) {
      double valeur = 0.0;
      if (!valeurS.equals("")) {
        valeur = Double.valueOf(valeurS);
      }
      type = new ReductionExpressionType(contr, motCle, valeur);
    } else {
      type = new ConfigExpressionType(contr, motCle);
    }
    return type;
  }

  /**
   * The method creates a {@link OperationRule} according to the fields of the
   * Swing frame.
   * 
   * @return a {@link OperationRule} feature
   */
  private OperationRule createOperationRule() {
    // on construit la conclusion
    ProcessingConcept conclusionConcept = (ProcessingConcept) GeneralisationConcept
        .getElemGeoFromName(this.txtConceptCRo.getText(), this.concepts);
    String nom = this.txtNomRo.getText();
    boolean advice = this.chkConseil.isSelected();
    boolean positive = this.chkPositive.isSelected();
    int importance = this.sldImpRo.getValue();
    ORConclusion conclusion = new ORConclusion(positive, advice,
        conclusionConcept);
    // on r�cup�re les restictions
    Map<GeoSpaceConcept, Double> restriction = new HashMap<GeoSpaceConcept, Double>();
    for (SpaceRestriction rest : this.currentConstrRestriction) {
      restriction.put(rest.space, rest.ratio);
    }
    return new OperationRule(nom, importance, this.rDb, conclusion,
        this.currentRulePremise, restriction);
  }

  /**
   * Get the ontology upper concept for the current {@link FormalGenConstraint}
   * feature (i.e. the one being edited in the frame).
   * @return
   */
  private String getRootFromConstr() {
    String root = "entité_géographique";
    // on enl�ve des parties de l'arbre (inutiles) selon la nature de la
    // contrainte
    if (this.rdRel.isSelected()) {
      root = "relation_géographique";
    }
    return root;
  }

  /**
   * Standard constructor.
   * @param onto
   * @param nomBdc
   * @param nomBdr
   */
  public EditFormalConstraintsFrame(OWLOntology onto, String nomBdc,
      String nomBdr) {
    super();
    this.setAlwaysOnTop(true);
    this.concepts = GeneralisationConcept
        .ontologyToGeneralisationConcepts(onto);
    // on charge le nom des éléments d'interface dans la langue courante
    this.internationalisation();
    // on charge la bd de contraintes
    this.ontology = onto;
    this.cDb = new ConstraintDatabase(nomBdc, this.concepts,
        onto.getOntologyID().getOntologyIRI().toString());
    this.rDb = new OperationRulesDatabase(nomBdr, this.concepts,
        onto.getOntologyID().getOntologyIRI().toString());
    this.currentCriterion = new SelectionCriterion();
    this.microList = new ArrayList<FormalGenConstraint>();
    this.microList.addAll(this.cDb.getMicroConstraints());
    this.mesoList = new ArrayList<FormalGenConstraint>();
    this.mesoList.addAll(this.cDb.getMesoConstraints());
    this.macroList = new ArrayList<FormalGenConstraint>();
    this.macroList.addAll(this.cDb.getMacroConstraints());
    this.relList = new ArrayList<FormalGenConstraint>();
    this.relList.addAll(this.cDb.getRelationalConstraints());
    this.typesL = new String[] { this.nomControle, this.nomSeuil,
        this.nomReduc };
    this.typesP = new String[] { this.nomMarge, this.nomConfig };
    this.setSize(500, 400);

    // ***********************************
    // PANNEAU CONTENANT LES BOUTONS OK/ANNULER
    // ***********************************
    JPanel panelBoutons = new JPanel();
    // le bouton OK
    JButton btnOK = new JButton(this.nomOk);
    btnOK.addActionListener(this);
    btnOK.setActionCommand("ok");
    btnOK.setPreferredSize(new Dimension(100, 50));
    // le bouton annuler
    JButton btnAnnuler = new JButton(this.nomAnnuler);
    btnAnnuler.addActionListener(this);
    btnAnnuler.setActionCommand("annuler");
    btnAnnuler.setPreferredSize(new Dimension(100, 50));
    // le panneau
    panelBoutons.add(btnOK);
    panelBoutons.add(btnAnnuler);
    Border espacement = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    Border ligne = BorderFactory.createLineBorder(Color.BLACK);
    panelBoutons.setBorder(espacement);
    panelBoutons.setLayout(new BoxLayout(panelBoutons, BoxLayout.X_AXIS));

    // ***********************************
    // ONGLET AJOUT DE CONTRAINTE
    // ***********************************
    JPanel panelAjoutContr = new JPanel();
    this.mapTypeEx = new HashMap<String, String>();
    this.mapTypeEx.put(this.nomSeuil, this.nomLblSeuil);
    this.mapTypeEx.put(this.nomControle, this.nomLblControle);
    this.mapTypeEx.put(this.nomMarge, this.nomLblMarge);
    this.mapTypeEx.put(this.nomReduc, this.nomLblReduc);
    this.mapTypeEx.put(this.nomConfig, this.nomLblConfig);
    // on cr�e un panneau horiz pour les noms de la contrainte
    JPanel pNomsContr = new JPanel();
    this.rdMicro = new JRadioButton(this.nomMicro);
    this.rdMeso = new JRadioButton(this.nomMeso);
    this.rdMacro = new JRadioButton(this.nomMacro);
    this.rdRel = new JRadioButton(this.nomRel);
    this.bg2 = new ButtonGroup();
    this.bg2.add(this.rdMicro);
    this.bg2.add(this.rdMeso);
    this.bg2.add(this.rdMacro);
    this.bg2.add(this.rdRel);
    this.rdMicro.addItemListener(this);
    this.rdMeso.addItemListener(this);
    this.rdMacro.addItemListener(this);
    this.rdRel.addItemListener(this);
    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    p1.add(this.rdMicro);
    p1.add(this.rdMeso);
    p2.add(this.rdMacro);
    p2.add(this.rdRel);
    p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
    p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
    JPanel p3 = new JPanel();
    this.cbCarac = new JComboBox<>(new DefaultComboBoxModel<>());
    this.cbCarac.setMinimumSize(new Dimension(110, 20));
    this.cbCarac.setMaximumSize(new Dimension(110, 20));
    this.cbCarac.setPreferredSize(new Dimension(110, 20));
    this.txtConcept = new JTextField();
    this.txtConcept.setMinimumSize(new Dimension(80, 20));
    this.txtConcept.setMaximumSize(new Dimension(80, 20));
    this.txtConcept.setPreferredSize(new Dimension(80, 20));
    this.txtConcept.setEditable(false);
    this.txtConcept.addCaretListener(this);
    ImageIcon icoOnto = new ImageIcon(
        this.getClass().getResource("/images/icons/protege.jpg"));
    JButton btnOnto = new JButton(icoOnto);
    btnOnto.addActionListener(this);
    btnOnto.setActionCommand("onto");
    JPanel pConcept = new JPanel();
    pConcept.add(this.txtConcept);
    pConcept.add(btnOnto);
    pConcept.setLayout(new BoxLayout(pConcept, BoxLayout.X_AXIS));
    p3.add(pConcept);
    p3.add(this.cbCarac);
    p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
    this.sldImp = new JSlider(SwingConstants.HORIZONTAL, 1, 5, 1);
    this.sldImp.setMajorTickSpacing(1);
    this.sldImp.setMinorTickSpacing(5);
    this.sldImp.setPaintTicks(true);
    this.sldImp.setPaintLabels(true);
    this.sldImp.setPreferredSize(new Dimension(80, 50));
    Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
    labelTable.put(new Integer(1), new JLabel("Min"));
    labelTable.put(new Integer(3), new JLabel(this.nomMoyen));
    labelTable.put(new Integer(5), new JLabel("Max"));
    this.sldImp.setLabelTable(labelTable);
    this.txtNom = new JTextField();
    this.txtNom.setMinimumSize(new Dimension(120, 20));
    this.txtNom.setMaximumSize(new Dimension(120, 20));
    this.txtNom.setPreferredSize(new Dimension(120, 20));
    JPanel p4 = new JPanel();
    this.btnOnto1 = new JButton(icoOnto);
    this.btnOnto1.addActionListener(this);
    this.btnOnto1.setActionCommand("onto1");
    this.btnOnto2 = new JButton(icoOnto);
    this.btnOnto2.addActionListener(this);
    this.btnOnto2.setActionCommand("onto2");
    this.txtConcept1 = new JTextField();
    this.txtConcept1.setMinimumSize(new Dimension(70, 20));
    this.txtConcept1.setMaximumSize(new Dimension(70, 20));
    this.txtConcept1.setPreferredSize(new Dimension(70, 20));
    this.txtConcept1.setEditable(false);
    this.txtConcept2 = new JTextField();
    this.txtConcept2.setMinimumSize(new Dimension(70, 20));
    this.txtConcept2.setMaximumSize(new Dimension(70, 20));
    this.txtConcept2.setPreferredSize(new Dimension(70, 20));
    this.txtConcept2.setEditable(false);
    JPanel pConcept1 = new JPanel();
    pConcept1.add(this.txtConcept1);
    pConcept1.add(this.btnOnto1);
    pConcept1.setLayout(new BoxLayout(pConcept1, BoxLayout.X_AXIS));
    JPanel pConcept2 = new JPanel();
    pConcept2.add(this.txtConcept2);
    pConcept2.add(this.btnOnto2);
    pConcept2.setLayout(new BoxLayout(pConcept2, BoxLayout.X_AXIS));
    p4.add(pConcept1);
    p4.add(pConcept2);
    p4.setLayout(new BoxLayout(p4, BoxLayout.Y_AXIS));
    pNomsContr.add(p1);
    pNomsContr.add(p2);
    pNomsContr.add(new JLabel(this.nomNom + " : "));
    pNomsContr.add(this.txtNom);
    pNomsContr.add(p3);
    pNomsContr.add(this.sldImp);
    pNomsContr.add(p4);
    pNomsContr.setLayout(new BoxLayout(pNomsContr, BoxLayout.X_AXIS));
    // on cr�e un panneau horiz pour l'expression de la contrainte
    JPanel pExpresssion = new JPanel();
    this.rdLisib = new JRadioButton(this.nomLisib);
    this.rdPreserv = new JRadioButton(this.nomPreserv);
    this.bg = new ButtonGroup();
    this.bg.add(this.rdLisib);
    this.bg.add(this.rdPreserv);
    this.rdLisib.setSelected(true);
    this.cbTypeExpr = new JComboBox<>(this.typesL);
    this.cbTypeExpr.setMinimumSize(new Dimension(60, 20));
    this.cbTypeExpr.setMaximumSize(new Dimension(60, 20));
    this.cbTypeExpr.setPreferredSize(new Dimension(60, 20));
    this.rdLisib.addItemListener(this);
    this.rdPreserv.addItemListener(this);
    this.lblValExpr = new JLabel(this.nomValeur + " : ");
    this.lblExemple = new JLabel(this.mapTypeEx.get(this.nomSeuil));
    this.cbTypeExpr.addItemListener(this);
    this.cbOperateur = new JComboBox<>(
        new String[] { "=", "<", ">", "<=", ">=", this.nomSimilaire,
            this.nomMaintenu, this.nomForce, this.nomEvite, this.nomInterdit });
    this.cbOperateur.setMinimumSize(new Dimension(80, 20));
    this.cbOperateur.setMaximumSize(new Dimension(80, 20));
    this.cbOperateur.setPreferredSize(new Dimension(80, 20));
    this.txtValeurExpression = new JTextField("");
    this.txtValeurExpression.setMinimumSize(new Dimension(60, 20));
    this.txtValeurExpression.setMaximumSize(new Dimension(60, 20));
    this.txtValeurExpression.setPreferredSize(new Dimension(60, 20));
    this.cbOperateur.setSelectedItem("=");
    this.cbTypeExpr.setSelectedItem(this.nomSeuil);
    this.cbUniteValeur = new JComboBox<>(
        new String[] { this.nomSsUnite, this.nomUniteTerr, this.nomUniteCarte,
            this.nomUniteAngle, this.nomUniteTerr2, this.nomUniteCarte2 });
    this.cbUniteValeur.setMinimumSize(new Dimension(100, 20));
    this.cbUniteValeur.setMaximumSize(new Dimension(100, 20));
    this.cbUniteValeur.setPreferredSize(new Dimension(100, 20));
    this.cbUniteValeur.setSelectedItem(this.nomSsUnite);
    this.cbUniteValeur.addItemListener(this);
    this.lblUnite = new JLabel("");
    pExpresssion.add(this.rdLisib);
    pExpresssion.add(this.rdPreserv);
    pExpresssion.add(Box.createHorizontalStrut(5));
    pExpresssion.add(this.cbTypeExpr);
    pExpresssion.add(Box.createHorizontalStrut(5));
    pExpresssion.add(this.lblExemple);
    pExpresssion.add(Box.createHorizontalStrut(5));
    pExpresssion.add(this.cbOperateur);
    pExpresssion.add(Box.createHorizontalStrut(5));
    pExpresssion.add(this.lblValExpr);
    pExpresssion.add(Box.createHorizontalStrut(5));
    pExpresssion.add(this.txtValeurExpression);
    pExpresssion.add(Box.createHorizontalStrut(5));
    pExpresssion.add(this.cbUniteValeur);
    pExpresssion.add(Box.createHorizontalStrut(5));
    pExpresssion.add(this.lblUnite);
    pExpresssion.setLayout(new BoxLayout(pExpresssion, BoxLayout.X_AXIS));
    // on cr�e un panneau horiz pour les noms de la contrainte
    JPanel pSelection = new JPanel();
    this.listeRequetes = new JList<>();
    this.listeRequetes.setPreferredSize(new Dimension(80, 80));
    this.listeRequetes.setMaximumSize(new Dimension(80, 80));
    this.listeRequetes.setMinimumSize(new Dimension(80, 80));
    this.listeRequetes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JPanel pPlusMoins = new JPanel();
    JButton btnPlus = new JButton("+");
    btnPlus.addActionListener(this);
    btnPlus.setActionCommand("plus");
    btnPlus.setPreferredSize(new Dimension(40, 40));
    JButton btnMoins = new JButton("-");
    btnMoins.addActionListener(this);
    btnMoins.setActionCommand("moins");
    btnMoins.setPreferredSize(new Dimension(40, 40));
    pPlusMoins.add(btnPlus);
    pPlusMoins.add(btnMoins);
    pPlusMoins.setLayout(new BoxLayout(pPlusMoins, BoxLayout.Y_AXIS));
    JPanel pConceptReq = new JPanel();
    this.cbConcReq = new JComboBox<GeneralisationConcept>(
        new DefaultComboBoxModel<GeneralisationConcept>(
            (GeneralisationConcept[]) this.relatedConcepts.toArray()));
    this.cbConcReq.addItemListener(this);
    this.cbConcReq.setMinimumSize(new Dimension(100, 20));
    this.cbConcReq.setMaximumSize(new Dimension(100, 20));
    this.cbConcReq.setPreferredSize(new Dimension(100, 20));
    this.cbCaracReq = new JComboBox<>(new DefaultComboBoxModel<>());
    this.cbCaracReq.setMinimumSize(new Dimension(100, 20));
    this.cbCaracReq.setMaximumSize(new Dimension(100, 20));
    this.cbCaracReq.setPreferredSize(new Dimension(100, 20));
    pConceptReq.add(this.cbConcReq);
    pConceptReq.add(this.cbCaracReq);
    pConceptReq.setLayout(new BoxLayout(pConceptReq, BoxLayout.Y_AXIS));
    this.cbOpReq = new JComboBox<>(new String[] { "=", "<", ">", "<=", ">=" });
    this.cbOpReq.setMinimumSize(new Dimension(50, 20));
    this.cbOpReq.setMaximumSize(new Dimension(50, 20));
    this.cbOpReq.setPreferredSize(new Dimension(50, 20));
    this.txtValReq = new JTextField("");
    this.txtValReq.setMinimumSize(new Dimension(60, 20));
    this.txtValReq.setMaximumSize(new Dimension(60, 20));
    this.txtValReq.setPreferredSize(new Dimension(60, 20));
    this.txtPourcReq = new JTextField("");
    this.txtPourcReq.setMinimumSize(new Dimension(60, 20));
    this.txtPourcReq.setMaximumSize(new Dimension(60, 20));
    this.txtPourcReq.setPreferredSize(new Dimension(60, 20));
    this.txtPourcReq.setDocument(new RealLimitator());
    this.cbUniteSelection = new JComboBox<>(
        new String[] { this.nomSsUnite, this.nomUniteTerr, this.nomUniteCarte,
            this.nomUniteAngle, this.nomUniteTerr2, this.nomUniteCarte2 });
    this.cbUniteSelection.setMinimumSize(new Dimension(100, 20));
    this.cbUniteSelection.setMaximumSize(new Dimension(100, 20));
    this.cbUniteSelection.setPreferredSize(new Dimension(100, 20));
    this.cbUniteSelection.setSelectedItem(this.nomSsUnite);
    this.cbUniteSelection.addItemListener(this);
    this.lblUniteSel = new JLabel("");
    pSelection.add(new JLabel(this.titreCritSel + " : "));
    pSelection.add(this.listeRequetes);
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(pPlusMoins);
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(pConceptReq);
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(this.cbOpReq);
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(new JLabel(this.nomValeur + " : "));
    pSelection.add(this.txtValReq);
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(new JLabel(this.nomMarge + " : "));
    pSelection.add(this.txtPourcReq);
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(new JLabel(" %"));
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(this.cbUniteSelection);
    pSelection.add(Box.createHorizontalStrut(5));
    pSelection.add(this.lblUniteSel);
    pSelection.setLayout(new BoxLayout(pSelection, BoxLayout.X_AXIS));
    JButton btnEnregistrer = new JButton(this.nomEnreg);
    btnEnregistrer.addActionListener(this);
    btnEnregistrer.setActionCommand("enregistrer");
    // ********************************************************
    // UN PANNEAU POUR LES RESTRICTIONS D ESPACE
    JPanel pRestCont = new JPanel();
    // on initialise restrictCourante
    this.currentConstrRestriction = new ArrayList<SpaceRestriction>();
    // on cr�e la table
    RestrictTableModel tModel = new RestrictTableModel(true);
    this.tableRestC = new JTable(tModel);
    this.tableRestC.setMinimumSize(new Dimension(200, 100));
    this.tableRestC.setMaximumSize(new Dimension(200, 100));
    this.tableRestC.setPreferredSize(new Dimension(200, 100));
    // le curseur de ratio d'importance
    this.sldRatioC = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 100);
    this.sldRatioC.setMajorTickSpacing(10);
    this.sldRatioC.setMinorTickSpacing(20);
    this.sldRatioC.setPaintTicks(true);
    this.sldRatioC.setPaintLabels(true);
    this.sldRatioC.setPreferredSize(new Dimension(80, 50));
    this.sldRatioC.setToolTipText(this.ttRatioC);
    // un champ texte pour saisir le nom de l'espace (avec son bouton pour
    // acc�der � l'ontologie
    this.btnOntoEspC = new JButton(icoOnto);
    this.btnOntoEspC.addActionListener(this);
    this.btnOntoEspC.setActionCommand("onto_espace_cont");
    this.txtEspaceCont = new JTextField();
    this.txtEspaceCont.setMinimumSize(new Dimension(120, 20));
    this.txtEspaceCont.setMaximumSize(new Dimension(120, 20));
    this.txtEspaceCont.setPreferredSize(new Dimension(120, 20));
    // les deux boutons pour ajouter ou enlever une restriction
    JButton btnPlusC = new JButton("+");
    btnPlusC.addActionListener(this);
    btnPlusC.setActionCommand("plus_cont");
    btnPlusC.setPreferredSize(new Dimension(40, 40));
    JButton btnMoinsC = new JButton("-");
    btnMoinsC.addActionListener(this);
    btnMoinsC.setActionCommand("moins_cont");
    btnMoinsC.setPreferredSize(new Dimension(40, 40));
    JScrollPane spC = new JScrollPane(this.tableRestC);
    spC.setMinimumSize(new Dimension(200, 150));
    spC.setMaximumSize(new Dimension(200, 150));
    spC.setPreferredSize(new Dimension(200, 150));
    // mise en page du panneau
    JPanel pPlusMoinsC = new JPanel();
    pPlusMoinsC.add(btnPlusC);
    pPlusMoinsC.add(btnMoinsC);
    pPlusMoinsC.setLayout(new BoxLayout(pPlusMoinsC, BoxLayout.Y_AXIS));
    pRestCont.add(spC);
    pRestCont.add(pPlusMoinsC);
    pRestCont.add(new JLabel(this.nomEspaceGeoCourt + " : "));
    pRestCont.add(this.txtEspaceCont);
    pRestCont.add(this.btnOntoEspC);
    pRestCont.add(this.sldRatioC);
    pRestCont.setLayout(new BoxLayout(pRestCont, BoxLayout.X_AXIS));
    // ********************************************************
    // mise en page du panneau g�n�ral d'ajout de contrainte
    panelAjoutContr.add(pNomsContr);
    panelAjoutContr.add(Box.createVerticalGlue());
    panelAjoutContr.add(pExpresssion);
    panelAjoutContr.add(Box.createVerticalGlue());
    panelAjoutContr.add(pSelection);
    panelAjoutContr.add(Box.createVerticalGlue());
    panelAjoutContr.add(pRestCont);
    panelAjoutContr.add(Box.createVerticalGlue());
    panelAjoutContr.add(btnEnregistrer);
    panelAjoutContr
        .setBorder(BorderFactory.createCompoundBorder(ligne, espacement));
    panelAjoutContr.setLayout(new BoxLayout(panelAjoutContr, BoxLayout.Y_AXIS));

    // ********************************************************
    // UN PANNEAU LISTANT LES CONTRAINTES DE LA BASE DE DONNEES
    // *********************************************************
    this.pBdc = new JPanel();
    JPanel pListes = new JPanel();
    JPanel pMicros = new JPanel();
    this.listeMi = new JList<>(new DefaultListModel<>());
    this.listeMi.setPreferredSize(new Dimension(80, 580));
    this.listeMi.setMaximumSize(new Dimension(80, 580));
    this.listeMi.setMinimumSize(new Dimension(80, 580));
    this.listeMi.addMouseListener(this);
    pMicros.add(new JScrollPane(this.listeMi));
    pMicros.add(new JLabel(this.titreMicro));
    pMicros.setLayout(new BoxLayout(pMicros, BoxLayout.Y_AXIS));
    JPanel pMesos = new JPanel();
    this.listeMe = new JList<>(new DefaultListModel<>());
    this.listeMe.setPreferredSize(new Dimension(80, 380));
    this.listeMe.setMaximumSize(new Dimension(80, 380));
    this.listeMe.setMinimumSize(new Dimension(80, 380));
    this.listeMe.addMouseListener(this);
    pMesos.add(new JScrollPane(this.listeMe));
    pMesos.add(new JLabel(this.titreMeso));
    pMesos.setLayout(new BoxLayout(pMesos, BoxLayout.Y_AXIS));
    JPanel pMacros = new JPanel();
    this.listeMa = new JList<>(new DefaultListModel<>());
    this.listeMa.setPreferredSize(new Dimension(80, 280));
    this.listeMa.setMaximumSize(new Dimension(80, 280));
    this.listeMa.setMinimumSize(new Dimension(80, 280));
    this.listeMa.addMouseListener(this);
    pMacros.add(new JScrollPane(this.listeMa));
    pMacros.add(new JLabel(this.titreMacro));
    pMacros.setLayout(new BoxLayout(pMacros, BoxLayout.Y_AXIS));
    JPanel pRels = new JPanel();
    this.listeRe = new JList<>(new DefaultListModel<>());
    this.listeRe.setPreferredSize(new Dimension(80, 380));
    this.listeRe.setMaximumSize(new Dimension(80, 380));
    this.listeRe.setMinimumSize(new Dimension(80, 380));
    this.listeRe.addMouseListener(this);
    this.updateConstraintLists();
    pRels.add(new JScrollPane(this.listeRe));
    pRels.add(new JLabel(this.titreRel));
    pRels.setLayout(new BoxLayout(pRels, BoxLayout.Y_AXIS));
    pListes.add(pMicros);
    pListes.add(pMesos);
    pListes.add(pMacros);
    pListes.add(pRels);
    pListes.setLayout(new BoxLayout(pListes, BoxLayout.X_AXIS));
    this.pBdc.add(new JLabel(this.nomBaseDonnees + " : " + this.cDb.getName()));
    this.pBdc.add(pListes);
    this.pBdc.setBorder(BorderFactory.createCompoundBorder(ligne, espacement));
    this.pBdc.setLayout(new BoxLayout(this.pBdc, BoxLayout.Y_AXIS));

    // *****************************************
    // UN ONGLET POUR LES REGLES OPERATIONNELLES
    // *****************************************
    this.currentRulePremise = new HashSet<ORPremise>();
    JPanel pReglesOper = new JPanel();
    DefaultListModel<OperationRule> dlm6 = new DefaultListModel<>();
    for (OperationRule c : this.rDb.getRules()) {
      dlm6.addElement(c);
    }
    this.listeRO = new JList<>(dlm6);
    this.listeRO.setPreferredSize(new Dimension(80, 280));
    this.listeRO.setMaximumSize(new Dimension(80, 280));
    this.listeRO.setMinimumSize(new Dimension(80, 280));
    // on fait un panneau pour ajouter une r�gle
    JPanel pAjoutRO = new JPanel();
    // il est compos� d'un panneau g�n�ral
    JPanel pGeneralRo = new JPanel();
    this.txtNomRo = new JTextField();
    this.txtNomRo.setMinimumSize(new Dimension(90, 20));
    this.txtNomRo.setMaximumSize(new Dimension(90, 20));
    this.txtNomRo.setPreferredSize(new Dimension(90, 20));
    JButton btnEnregRo = new JButton(this.nomEnreg);
    btnEnregRo.addActionListener(this);
    btnEnregRo.setActionCommand("enregistrerRo");
    this.sldImpRo = new JSlider(SwingConstants.HORIZONTAL, 1, 5, 1);
    this.sldImpRo.setMajorTickSpacing(1);
    this.sldImpRo.setMinorTickSpacing(5);
    this.sldImpRo.setPaintTicks(true);
    this.sldImpRo.setPaintLabels(true);
    this.sldImpRo.setPreferredSize(new Dimension(150, 50));
    this.sldImpRo.setMaximumSize(new Dimension(150, 50));
    this.sldImpRo.setMinimumSize(new Dimension(150, 50));
    this.sldImpRo.setLabelTable(labelTable);
    pGeneralRo.add(new JLabel(this.nomRegle + " : "));
    pGeneralRo.add(this.txtNomRo);
    pGeneralRo.add(this.sldImpRo);
    pGeneralRo.add(btnEnregRo);
    pGeneralRo.setLayout(new BoxLayout(pGeneralRo, BoxLayout.X_AXIS));
    // un panneau pour la conclusion de la r�gle
    JPanel pConclusionRo = new JPanel();
    this.btnOnto4 = new JButton(icoOnto);
    this.btnOnto4.addActionListener(this);
    this.btnOnto4.setActionCommand("onto4");
    this.txtConceptCRo = new JTextField();
    this.txtConceptCRo.setMinimumSize(new Dimension(80, 20));
    this.txtConceptCRo.setMaximumSize(new Dimension(80, 20));
    this.txtConceptCRo.setPreferredSize(new Dimension(80, 20));
    this.txtConceptCRo.setEditable(false);
    this.chkConseil = new JCheckBox(this.nomOperConseil);
    this.chkConseil.setToolTipText(this.ttOperConseil);
    this.chkPositive = new JCheckBox(this.nomOperRequise);
    pConclusionRo.add(new JLabel(this.nomOperRequise + " : "));
    pConclusionRo.add(this.txtConceptCRo);
    pConclusionRo.add(this.btnOnto4);
    pConclusionRo.add(this.chkConseil);
    pConclusionRo.add(this.chkPositive);
    Border bordConcl = BorderFactory.createTitledBorder(ligne,
        this.titreConclusion);
    pConclusionRo
        .setBorder(BorderFactory.createCompoundBorder(bordConcl, espacement));
    pConclusionRo.setLayout(new BoxLayout(pConclusionRo, BoxLayout.X_AXIS));
    // et un panneau pour la pr�misse de la r�gle
    JPanel pPremisseRo = new JPanel();
    this.listeCond = new JList<>();
    this.listeCond.setPreferredSize(new Dimension(80, 80));
    this.listeCond.setMaximumSize(new Dimension(80, 80));
    this.listeCond.setMinimumSize(new Dimension(80, 80));
    this.listeCond.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.btnPlusRo = new JButton("+");
    this.btnPlusRo.addActionListener(this);
    this.btnPlusRo.setActionCommand("plus");
    this.btnMoinsRo = new JButton("-");
    this.btnMoinsRo.addActionListener(this);
    this.btnMoinsRo.setActionCommand("moins");
    JPanel pPlusMoinsRo = new JPanel();
    pPlusMoinsRo.add(this.btnPlusRo);
    pPlusMoinsRo.add(this.btnMoinsRo);
    pPlusMoinsRo.setLayout(new BoxLayout(pPlusMoinsRo, BoxLayout.Y_AXIS));
    this.cbOpRegleOp = new JComboBox<>(
        new String[] { "=", "<", ">", "<=", ">=" });
    this.cbOpRegleOp.setMinimumSize(new Dimension(50, 20));
    this.cbOpRegleOp.setMaximumSize(new Dimension(50, 20));
    this.cbOpRegleOp.setPreferredSize(new Dimension(50, 20));
    this.txtConceptPRo = new JTextField();
    this.txtConceptPRo.setMinimumSize(new Dimension(120, 20));
    this.txtConceptPRo.setMaximumSize(new Dimension(120, 20));
    this.txtConceptPRo.setPreferredSize(new Dimension(120, 20));
    this.txtConceptPRo.setEditable(false);
    this.txtConceptPRo.addCaretListener(this);
    this.btnOnto5 = new JButton(icoOnto);
    this.btnOnto5.addActionListener(this);
    this.btnOnto5.setActionCommand("onto6");
    this.cbCaracRo = new JComboBox<>();
    this.cbCaracRo.setMinimumSize(new Dimension(120, 20));
    this.cbCaracRo.setMaximumSize(new Dimension(120, 20));
    this.cbCaracRo.setPreferredSize(new Dimension(120, 20));
    this.txtValeurRo = new JTextField("");
    this.txtValeurRo.setMinimumSize(new Dimension(60, 20));
    this.txtValeurRo.setMaximumSize(new Dimension(60, 20));
    this.txtValeurRo.setPreferredSize(new Dimension(60, 20));
    this.cbUniteRegle = new JComboBox<>(
        new String[] { this.nomSsUnite, this.nomUniteTerr, this.nomUniteCarte,
            this.nomUniteAngle, this.nomUniteTerr2, this.nomUniteCarte2 });
    this.cbUniteRegle.setMinimumSize(new Dimension(100, 20));
    this.cbUniteRegle.setMaximumSize(new Dimension(100, 20));
    this.cbUniteRegle.setPreferredSize(new Dimension(100, 20));
    this.cbUniteRegle.setSelectedItem(this.nomSsUnite);
    pPremisseRo.add(this.listeCond);
    pPremisseRo.add(pPlusMoinsRo);
    pPremisseRo.add(this.txtConceptPRo);
    pPremisseRo.add(this.btnOnto5);
    pPremisseRo.add(this.cbCaracRo);
    pPremisseRo.add(this.cbOpRegleOp);
    pPremisseRo.add(this.txtValeurRo);
    pPremisseRo.add(this.cbUniteRegle);
    Border bordPrem = BorderFactory.createTitledBorder(ligne,
        this.titrePremisse);
    pPremisseRo
        .setBorder(BorderFactory.createCompoundBorder(bordPrem, espacement));
    pPremisseRo.setLayout(new BoxLayout(pPremisseRo, BoxLayout.X_AXIS));
    pAjoutRO.add(pPremisseRo);
    pAjoutRO.add(pConclusionRo);
    pAjoutRO.add(pGeneralRo);
    pAjoutRO.setLayout(new BoxLayout(pAjoutRO, BoxLayout.Y_AXIS));
    // ********************************************************
    // UN PANNEAU POUR LES RESTRICTIONS D ESPACE
    JPanel pRestReg = new JPanel();
    // on initialise restrictCourante
    this.currentRuleRestriction = new ArrayList<SpaceRestriction>();
    // on cr�e la table
    RestrictTableModel tModelR = new RestrictTableModel(false);
    this.tableRestR = new JTable(tModelR);
    // le curseur de ratio d'importance
    this.sldRatioR = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 100);
    this.sldRatioR.setMajorTickSpacing(10);
    this.sldRatioR.setMinorTickSpacing(20);
    this.sldRatioR.setPaintTicks(true);
    this.sldRatioR.setPaintLabels(true);
    this.sldRatioR.setPreferredSize(new Dimension(80, 50));
    this.sldRatioR.setToolTipText(this.ttRatioR);
    // un champ texte pour saisir le nom de l'espace (avec son bouton pour
    // acc�der � l'ontologie
    this.btnOntoEspR = new JButton(icoOnto);
    this.btnOntoEspR.addActionListener(this);
    this.btnOntoEspR.setActionCommand("onto_espace_reg");
    this.txtEspaceReg = new JTextField();
    this.txtEspaceReg.setMinimumSize(new Dimension(120, 20));
    this.txtEspaceReg.setMaximumSize(new Dimension(120, 20));
    this.txtEspaceReg.setPreferredSize(new Dimension(120, 20));
    // les deux boutons pour ajouter ou enlever une restriction
    JButton btnPlusR = new JButton("+");
    btnPlusR.addActionListener(this);
    btnPlusR.setActionCommand("plus_reg");
    btnPlusR.setPreferredSize(new Dimension(40, 40));
    JButton btnMoinsR = new JButton("-");
    btnMoinsR.addActionListener(this);
    btnMoinsR.setActionCommand("moins_reg");
    btnMoinsR.setPreferredSize(new Dimension(40, 40));
    JScrollPane spR = new JScrollPane(this.tableRestR);
    spR.setMinimumSize(new Dimension(200, 150));
    spR.setMaximumSize(new Dimension(200, 150));
    spR.setPreferredSize(new Dimension(200, 150));
    // mise en page du panneau
    JPanel pPlusMoinsR = new JPanel();
    pPlusMoinsR.add(btnPlusR);
    pPlusMoinsR.add(btnMoinsR);
    pPlusMoinsR.setLayout(new BoxLayout(pPlusMoinsR, BoxLayout.Y_AXIS));
    pRestReg.add(spR);
    pRestReg.add(pPlusMoinsR);
    pRestReg.add(new JLabel(this.nomEspaceGeoCourt + " : "));
    pRestReg.add(this.txtEspaceReg);
    pRestReg.add(this.btnOntoEspR);
    pRestReg.add(this.sldRatioR);
    pRestReg.setLayout(new BoxLayout(pRestReg, BoxLayout.X_AXIS));

    // mise en page finale
    pReglesOper.add(pAjoutRO);
    pReglesOper.add(pRestReg);
    pReglesOper.add(new JScrollPane(this.listeRO));
    pReglesOper.add(new JLabel(this.rDb.getName()));
    pReglesOper.setLayout(new BoxLayout(pReglesOper, BoxLayout.Y_AXIS));

    // *********************************
    // LES ONGLETS
    // *********************************
    JPanel pOngletC = new JPanel();
    pOngletC.add(panelAjoutContr);
    pOngletC.add(this.pBdc);
    pOngletC.setLayout(new BoxLayout(pOngletC, BoxLayout.Y_AXIS));
    this.onglets = new JTabbedPane(SwingConstants.TOP);
    this.onglets.addTab(this.titreContrs, pOngletC);
    this.onglets.addTab(this.titreReglesOper, pReglesOper);
    this.onglets.setSelectedIndex(0);

    // *********************************
    // MENU DE LA FENETRE
    // *********************************
    JMenuBar menuBar = new JMenuBar();
    // un menu Fichier
    JMenu menuFichier = new JMenu(this.nomFichier);
    ImageIcon iconeAide = new ImageIcon(
        "C:\\Program Files\\Laser-Scan\\clarity-v2.7\\images\\help.gif");
    JMenuItem aide = new JMenuItem(this.nomAide, iconeAide);
    aide.setAccelerator(KeyStroke.getKeyStroke('N',
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    aide.setActionCommand("aide");
    aide.addActionListener(this);
    ExportAction exportBdcAct = new ExportAction(this, true);
    ExportAction exportBdrAct = new ExportAction(this, false);
    menuFichier.add(exportBdcAct);
    menuFichier.add(exportBdrAct);
    menuFichier.addSeparator();
    menuFichier.add(aide);
    // un menu Contraintes
    JMenu menuContr = new JMenu(this.titreContrs);
    StatsAction statsBdcAct = new StatsAction(this, 0);
    EffacerBdcAction effacerBdcAct = new EffacerBdcAction(this);
    this.loadXMLConstraintsAction = new ChargerBdcXmlAction(this);
    menuContr.add(statsBdcAct);
    menuContr.add(effacerBdcAct);
    menuContr.add(this.loadXMLConstraintsAction);
    // un menu R�gles Op
    this.loadXMLRulesAction = new ChargerBdrXmlAction(this);
    EffacerBdrAction effacerBdrAct = new EffacerBdrAction(this);
    JMenu menuRegO = new JMenu(this.titreReglesOper);
    StatsAction statsBdroAct = new StatsAction(this, 1);
    menuRegO.add(this.loadXMLRulesAction);
    menuRegO.add(effacerBdrAct);
    menuRegO.add(statsBdroAct);

    menuBar.add(menuFichier);
    menuBar.add(menuContr);
    menuBar.add(menuRegO);
    menuBar.setAlignmentX(Component.LEFT_ALIGNMENT);

    // ***********************************
    // MISE EN PAGE FINALE
    // ***********************************
    this.setJMenuBar(menuBar);
    this.getContentPane().add(this.onglets);
    this.getContentPane().add(panelBoutons);
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
    this.setVisible(true);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (JRadioButton.class.isInstance(e.getSource())) {
      JRadioButton radio = (JRadioButton) e.getSource();
      if (radio.equals(this.rdLisib) || radio.equals(this.rdPreserv)) {
        DefaultComboBoxModel<String> cbm = null;
        if (this.rdLisib.isSelected()) {
          cbm = new DefaultComboBoxModel<>(this.typesL);
        } else {
          cbm = new DefaultComboBoxModel<>(this.typesP);
        }
        this.cbTypeExpr.setModel(cbm);
        this.cbTypeExpr
            .setSelectedIndex(Math.abs(this.cbTypeExpr.getSelectedIndex() - 1));
      } else if (this.rdRel.isSelected()) {
        this.txtConcept1.setEditable(true);
        this.txtConcept2.setEditable(true);
        this.btnOnto1.setEnabled(true);
        this.btnOnto2.setEnabled(true);
      } else {
        this.txtConcept1.setEditable(false);
        this.txtConcept2.setEditable(false);
        this.btnOnto1.setEnabled(false);
        this.btnOnto2.setEnabled(false);
      }
    } else if (e.getSource().equals(this.cbConcReq)) {
      GeneralisationConcept nouveau = (GeneralisationConcept) this.cbConcReq
          .getSelectedItem();
      if (nouveau != null) {
        this.cbCaracReq.setModel(new DefaultComboBoxModel<>(
            (Character[]) nouveau.getTousCaracteres().toArray()));
      }
    } else if (e.getSource().equals(this.cbTypeExpr)) {
      String nouveau = (String) this.cbTypeExpr.getSelectedItem();
      // on change lblExemple
      this.lblExemple.setText(this.mapTypeEx.get(nouveau));
      if (nouveau.equals(this.nomMarge)) {
        this.txtValeurExpression.setEnabled(true);
        this.lblValExpr.setText(this.nomPourcent + " : ");
      } else if (nouveau.equals(this.nomSeuil)) {
        this.txtValeurExpression.setEnabled(true);
        this.lblValExpr.setText(this.nomValeur + " : ");
      } else if (nouveau.equals(this.nomControle)) {
        this.txtValeurExpression.setEnabled(true);
        this.lblValExpr.setText(this.nomValeur + " : ");
      } else if (nouveau.equals(this.nomReduc)) {
        this.txtValeurExpression.setEnabled(true);
        this.lblValExpr.setText(this.nomValeur + " : ");
      } else {
        this.txtValeurExpression.setEnabled(false);
      }
    } else if (e.getSource().equals(this.cbUniteValeur)) {
      int index = this.cbUniteValeur.getSelectedIndex();
      if (index == 0) {
        this.lblUnite.setText("");
      }
      if (index == 1) {
        this.lblUnite.setText(this.nomLblTerrain);
      }
      if (index == 2) {
        this.lblUnite.setText(this.nomLblCarte);
      }
      if (index == 3) {
        this.lblUnite.setText(this.nomLblAngle);
      }
    } else if (e.getSource().equals(this.cbUniteSelection)) {
      int index = this.cbUniteSelection.getSelectedIndex();
      if (index == 0) {
        this.lblUniteSel.setText("");
      }
      if (index == 1) {
        this.lblUniteSel.setText(this.nomLblTerrain1);
      }
      if (index == 2) {
        this.lblUniteSel.setText(this.nomLblCarte1);
      }
      if (index == 3) {
        this.lblUniteSel.setText(this.nomLblAngle);
      }
      if (index == 4) {
        this.lblUniteSel.setText(this.nomLblTerrain2);
      }
      if (index == 5) {
        this.lblUniteSel.setText(this.nomLblCarte2);
      }
    }
    this.pack();
  }

  @Override
  public void caretUpdate(CaretEvent e) {
    JTextField txt = (JTextField) e.getSource();
    GeneralisationConcept elem = this.getElemGeoFromName(txt.getText());
    if (elem == null) {
      return;
    }
    if (txt.equals(this.txtConceptRp)) {
      if (this.rdProc.isSelected()) {
      } else {
      }
      return;
    }
    if (txt.equals(this.txtConceptPRo)) {
      DefaultComboBoxModel<Character> cbm = new DefaultComboBoxModel<>();
      for (Character c : elem.getTousCaracteres()) {
        cbm.addElement(c);
      }
      this.cbCaracRo.setModel(cbm);
      return;
    }
    DefaultComboBoxModel<Character> cbm = new DefaultComboBoxModel<>();
    this.relatedConcepts.add(elem);
    if (this.rdMacro.isSelected()) {
      for (Character c : elem.getCaracteresMacro()) {
        cbm.addElement(c);
      }
    } else {
      for (Character c : elem.getTousCaracteresSimples()) {
        cbm.addElement(c);
      }
    }
    this.cbCarac.setModel(cbm);
    DefaultComboBoxModel<GeneralisationConcept> cbm2 = new DefaultComboBoxModel<>(
        (GeneralisationConcept[]) this.relatedConcepts.toArray());
    for (GeneralisationConcept c : this.relatedConcepts) {
      for (GeneralisationConcept cc : c.getTousSousElements()) {
        cbm2.addElement(cc);
      }
    }
    this.cbConcReq.setModel(cbm2);
    // on remplit la combo box des caract�res
    DefaultComboBoxModel<Character> cbm3 = new DefaultComboBoxModel<>();
    GeographicConcept concSel = (GeographicConcept) this.cbConcReq
        .getSelectedItem();
    for (Character c : concSel.getTousCaracteres()) {
      cbm3.addElement(c);
    }
    this.cbCaracReq.setModel(cbm3);
  }

  private GeneralisationConcept getElemGeoFromName(String text) {
    if (text.equals("")) {
      return null;
    }
    for (GeneralisationConcept e : this.concepts) {
      if (e.getName().equals(text)) {
        return e;
      }
    }
    return null;
  }

  /**
   * Class to handle space restriction on formal constraints.
   * @author gtouya
   * 
   */
  class SpaceRestriction {
    GeoSpaceConcept space;
    Double ratio;

    public SpaceRestriction(GeoSpaceConcept espace, Double ratio) {
      this.space = espace;
      this.ratio = ratio;
    }

    @Override
    public String toString() {
      return this.space.toString() + " - " + this.ratio;
    }
  }

  class RestrictTableModel extends AbstractTableModel {
    boolean constraint;

    public RestrictTableModel(boolean contrainte) {
      super();
      this.constraint = contrainte;
    }

    public void enleverLigne(int ligne) {
      if (this.constraint) {
        EditFormalConstraintsFrame.this.currentConstrRestriction.remove(ligne);
      } else {
        EditFormalConstraintsFrame.this.currentRuleRestriction.remove(ligne);
      }
      this.fireTableChanged(null);
    }

    public void ajouterLigne() {
      if (this.constraint) {
        String nom = EditFormalConstraintsFrame.this.txtEspaceCont.getText();
        GeneralisationConcept elem = GeneralisationConcept
            .getElemGeoFromName(nom, EditFormalConstraintsFrame.this.concepts);
        Double ratio = new Double(
            ((double) EditFormalConstraintsFrame.this.sldRatioC.getValue())
                / 100);
        SpaceRestriction rest = new SpaceRestriction((GeoSpaceConcept) elem,
            ratio);
        EditFormalConstraintsFrame.this.currentConstrRestriction.add(rest);
      } else {
        String nom = EditFormalConstraintsFrame.this.txtEspaceReg.getText();
        GeneralisationConcept elem = GeneralisationConcept
            .getElemGeoFromName(nom, EditFormalConstraintsFrame.this.concepts);
        Double ratio = new Double(
            ((double) EditFormalConstraintsFrame.this.sldRatioR.getValue())
                / 100);
        SpaceRestriction rest = new SpaceRestriction((GeoSpaceConcept) elem,
            ratio);
        EditFormalConstraintsFrame.this.currentRuleRestriction.add(rest);
      }
      this.fireTableChanged(null);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0) {
        return GeneralisationConcept.class;
      }
      return Double.class;
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
      if (columnIndex == 0) {
        return EditFormalConstraintsFrame.this.nomEspaceGeo;
      }
      return "importance";
    }

    @Override
    public int getRowCount() {
      if (this.constraint) {
        return EditFormalConstraintsFrame.this.currentConstrRestriction.size();
      }
      return EditFormalConstraintsFrame.this.currentRuleRestriction.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      SpaceRestriction rest = null;
      if (this.constraint) {
        rest = EditFormalConstraintsFrame.this.currentConstrRestriction
            .get(rowIndex);
      } else {
        rest = EditFormalConstraintsFrame.this.currentRuleRestriction
            .get(rowIndex);
      }
      if (columnIndex == 0) {
        return rest.space;
      }
      return rest.ratio;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      if (columnIndex == 1) {
        return true;
      }
      return false;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
      SpaceRestriction rest = null;
      if (this.constraint) {
        rest = EditFormalConstraintsFrame.this.currentConstrRestriction
            .get(rowIndex);
      } else {
        rest = EditFormalConstraintsFrame.this.currentRuleRestriction
            .get(rowIndex);
      }
      rest.ratio = (Double) value;
      this.fireTableCellUpdated(rowIndex, columnIndex);

    }

  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // on n'agit qu'en cas de double clic
    if (e.getClickCount() == 2) {
      // on ouvre le browser sur l'objet s�lectionn� dans la liste
      // on commence par r�cup�rer la liste source de l'�v�nement
      @SuppressWarnings("unchecked")
      JList<FormalGenConstraint> liste = (JList<FormalGenConstraint>) e
          .getSource();
      // get the selected element
      FormalGenConstraint contr = (FormalGenConstraint) liste
          .getSelectedValue();
      // on lance le browser sur l'objet contrainte contr
      ObjectBrowser.browse(contr);

    }

    // s'il s'agit du clic droit, on propose l'édition de la contrainte
    if (e.getButton() == MouseEvent.BUTTON3) {
      // on fait appara�tre un menu contextuel avec 2 entr�es
      @SuppressWarnings("unchecked")
      JList<FormalGenConstraint> liste = (JList<FormalGenConstraint>) e
          .getSource();
      Point point = e.getLocationOnScreen();
      // int x = pBdc.getX();
      // int y = pBdc.getY();
      FormalGenConstraint contr = (FormalGenConstraint) liste
          .getSelectedValue();
      ModifPopupMenu modifMenu = new ModifPopupMenu(this, contr);
      modifMenu.show(this, point.x, point.y);
    }
  }

  // il ne se passe rien pour les autres �v�nements souris
  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  /**
   * Cette classe �tend les popup menus pour proposer de modifier ou supprimer
   * une contrainte d�j� stock�e dans gothic.
   * @author GTouya
   * 
   */
  public class ModifPopupMenu extends JPopupMenu implements ActionListener {
    JMenuItem miModif, miSuppr, miAffich;
    EditFormalConstraintsFrame parent;
    FormalGenConstraint contr;

    public ModifPopupMenu(EditFormalConstraintsFrame frame,
        FormalGenConstraint contr) {
      super();
      this.parent = frame;
      this.contr = contr;
      this.miAffich = new JMenuItem(
          EditFormalConstraintsFrame.this.popupAffich);
      this.miAffich.addActionListener(this);
      this.miAffich.setActionCommand("affich");
      this.miModif = new JMenuItem(EditFormalConstraintsFrame.this.popupModif);
      this.miModif.addActionListener(this);
      this.miModif.setActionCommand("modif");
      this.miSuppr = new JMenuItem(EditFormalConstraintsFrame.this.popupSuppr);
      this.miSuppr.addActionListener(this);
      this.miSuppr.setActionCommand("suppr");
      // ajout au menu
      this.add(this.miAffich);
      this.add(this.miModif);
      this.add(this.miSuppr);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("modif")) {
        // on remplit le formulaire par les champs de contr
        this.remplirFormulaire();
        // on supprime contr
        if (EditFormalConstraintsFrame.this.microList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.microList.remove(this.contr);
        }
        if (EditFormalConstraintsFrame.this.mesoList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.mesoList.remove(this.contr);
        }
        if (EditFormalConstraintsFrame.this.macroList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.macroList.remove(this.contr);
        }
        if (EditFormalConstraintsFrame.this.relList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.relList.remove(this.contr);
        }
        EditFormalConstraintsFrame.this.cDb.deleteConstraint(this.contr);

        this.setVisible(false);
        this.parent.updateConstraintLists();
        this.parent.pack();
      } else if (e.getActionCommand().equals("suppr")) {
        // on supprime contr
        if (EditFormalConstraintsFrame.this.microList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.microList.remove(this.contr);
        }
        if (EditFormalConstraintsFrame.this.mesoList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.mesoList.remove(this.contr);
        }
        if (EditFormalConstraintsFrame.this.macroList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.macroList.remove(this.contr);
        }
        if (EditFormalConstraintsFrame.this.relList.contains(this.contr)) {
          EditFormalConstraintsFrame.this.relList.remove(this.contr);
        }

        EditFormalConstraintsFrame.this.cDb.deleteConstraint(this.contr);

        this.setVisible(false);
        this.parent.updateConstraintLists();
        this.parent.pack();
      } else if (e.getActionCommand().equals("affich")) {
        this.remplirFormulaire();
        this.setVisible(false);
        this.parent.pack();
      }
    }

    private void remplirFormulaire() {
      Class<?> classe = this.contr.getClass();
      EditFormalConstraintsFrame.this.rdMicro
          .setSelected(classe.equals(FormalMicroConstraint.class));
      EditFormalConstraintsFrame.this.rdMeso
          .setSelected(classe.equals(FormalMesoConstraint.class));
      EditFormalConstraintsFrame.this.rdMacro
          .setSelected(classe.equals(FormalMacroConstraint.class));
      EditFormalConstraintsFrame.this.rdRel
          .setSelected(classe.equals(FormalRelationalConstraint.class));
      EditFormalConstraintsFrame.this.txtNom.setText(this.contr.getName());
      EditFormalConstraintsFrame.this.sldImp
          .setValue(this.contr.getImportance());
      EditFormalConstraintsFrame.this.txtConcept
          .setText(this.contr.getConcept().getName());
      EditFormalConstraintsFrame.this.cbCarac
          .setSelectedItem(this.contr.getCharacter());
      if (EditFormalConstraintsFrame.this.rdRel.isSelected()) {
        EditFormalConstraintsFrame.this.txtConcept1.setText(
            ((FormalRelationalConstraint) this.contr).getConcept1().getName());
        EditFormalConstraintsFrame.this.txtConcept2.setText(
            ((FormalRelationalConstraint) this.contr).getConcept2().getName());
      } else {
        EditFormalConstraintsFrame.this.txtConcept1.setText("");
        EditFormalConstraintsFrame.this.txtConcept2.setText("");
      }
      Class<?> classeType = this.contr.getExprType().getClass();
      if (classeType.equals(ThreshExpressionType.class)
          || classeType.equals(ControlExpressionType.class)) {
        EditFormalConstraintsFrame.this.rdLisib.setSelected(true);
        EditFormalConstraintsFrame.this.rdPreserv.setSelected(false);
      } else {
        EditFormalConstraintsFrame.this.rdLisib.setSelected(false);
        EditFormalConstraintsFrame.this.rdPreserv.setSelected(true);
      }
      EditFormalConstraintsFrame.this.cbOperateur
          .setSelectedItem(this.contr.getExprType().getKeyWord().toShortcut());
      if (classeType.equals(ThreshExpressionType.class)) {
        ThreshExpressionType type = (ThreshExpressionType) this.contr
            .getExprType();
        EditFormalConstraintsFrame.this.cbTypeExpr
            .setSelectedItem(EditFormalConstraintsFrame.this.nomSeuil);
        EditFormalConstraintsFrame.this.txtValeurExpression
            .setText(type.getValue().toString());
        EditFormalConstraintsFrame.this.cbUniteValeur
            .setSelectedIndex(type.getValueUnit().ordinal());
      } else if (classeType.equals(ControlExpressionType.class)) {
        ControlExpressionType type = (ControlExpressionType) this.contr
            .getExprType();
        EditFormalConstraintsFrame.this.cbTypeExpr
            .setSelectedItem(EditFormalConstraintsFrame.this.nomSeuil);
        EditFormalConstraintsFrame.this.txtValeurExpression
            .setText(type.getValue().toString());
        EditFormalConstraintsFrame.this.cbUniteValeur
            .setSelectedIndex(type.getValueUnit().ordinal());
      } else if (classeType.equals(MarginExpressionType.class)) {
        MarginExpressionType type = (MarginExpressionType) this.contr
            .getExprType();
        EditFormalConstraintsFrame.this.cbTypeExpr
            .setSelectedItem(EditFormalConstraintsFrame.this.nomMarge);
        EditFormalConstraintsFrame.this.txtValeurExpression
            .setText(String.valueOf(type.getMargin()));
        EditFormalConstraintsFrame.this.cbUniteValeur.setSelectedIndex(0);
      } else if (classeType.equals(ReductionExpressionType.class)) {
        ReductionExpressionType type = (ReductionExpressionType) this.contr
            .getExprType();
        EditFormalConstraintsFrame.this.cbTypeExpr
            .setSelectedItem(EditFormalConstraintsFrame.this.nomReduc);
        EditFormalConstraintsFrame.this.txtValeurExpression
            .setText(String.valueOf(type.getValue()));
        EditFormalConstraintsFrame.this.cbUniteValeur.setSelectedIndex(0);
      } else {
        EditFormalConstraintsFrame.this.cbTypeExpr
            .setSelectedItem(EditFormalConstraintsFrame.this.nomConfig);
        EditFormalConstraintsFrame.this.txtValeurExpression.setText("");
        EditFormalConstraintsFrame.this.cbUniteValeur.setSelectedIndex(0);
      }
      this.parent.currentCriterion = this.contr.getSelectionCrit();
      EditFormalConstraintsFrame.this.requests.clear();
      if (EditFormalConstraintsFrame.this.currentCriterion != null) {
        EditFormalConstraintsFrame.this.requests.addAll(
            EditFormalConstraintsFrame.this.currentCriterion.getRequests());
      }
      DefaultListModel<Request> dlm = new DefaultListModel<>();
      for (Request r : EditFormalConstraintsFrame.this.requests) {
        dlm.addElement(r);
      }
      EditFormalConstraintsFrame.this.listeRequetes.setModel(dlm);
      EditFormalConstraintsFrame.this.currentConstrRestriction = new ArrayList<SpaceRestriction>();
      for (GeoSpaceConcept c : this.contr.getRestriction().keySet()) {
        EditFormalConstraintsFrame.this.currentConstrRestriction
            .add(new SpaceRestriction(c, this.contr.getRestriction().get(c)));
      }
      RestrictTableModel tModel = new RestrictTableModel(true);
      EditFormalConstraintsFrame.this.tableRestC = new JTable(tModel);
    }
  }

  class ExportAction extends AbstractAction {

    final JFileChooser fc = new JFileChooser();
    EditFormalConstraintsFrame frame;
    boolean contr;
    String chemin = System.getProperty("goth.dataroot")
        + "/custom/xml/cogit/guillaume/these/orchestration/contraintes/";
    DateFormat dateFormat = new SimpleDateFormat("yy_MM_dd_HH'h'mm'm'ss's'");
    Date date;

    @Override
    public void actionPerformed(ActionEvent e) {
      this.date = new Date();
      String nom = "";
      try {
        nom = EditFormalConstraintsFrame.this.cDb.getName();
        if (!this.contr) {
          nom = EditFormalConstraintsFrame.this.rDb.getName();
        }
        String prefixe = "BDC_";
        if (!this.contr) {
          prefixe = "BDR_";
        }
        File fic = new File(this.chemin + prefixe + nom + "_"
            + this.dateFormat.format(this.date).toString() + ".xml");
        fic.createNewFile();
        if (this.contr) {
          EditFormalConstraintsFrame.this.cDb.saveToXml(fic);
        } else {
          EditFormalConstraintsFrame.this.rDb.saveToXml(fic);
        }
      } catch (IOException e2) {
        e2.printStackTrace();
      } catch (TransformerException e1) {
        e1.printStackTrace();
      }
    }

    public ExportAction(EditFormalConstraintsFrame parent, boolean contr) {
      this.frame = parent;
      this.contr = contr;
      String name = EditFormalConstraintsFrame.this.nomExportContraintes;
      String descr = EditFormalConstraintsFrame.this.ttExportContraintes;
      if (!contr) {
        name = EditFormalConstraintsFrame.this.nomExportRegles;
        descr = EditFormalConstraintsFrame.this.ttExportRegles;
      }
      this.putValue(Action.SHORT_DESCRIPTION, descr);
      this.putValue(Action.NAME, name);
      this.putValue(Action.MNEMONIC_KEY, new Integer('E'));
      this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('E',
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
  }// class ExportAction

  class StatsAction extends AbstractAction {

    EditFormalConstraintsFrame frame;
    int base;// 0 pour la BdC, 1 pour les r�gles Op et 2 pour les r�gles Proc

    @Override
    public void actionPerformed(ActionEvent e) {
      String message = "";
      String retour = System.getProperty("line.separator");
      if (this.base == 0) {
        String entete = "Statistiques de la base de contraintes :" + retour;
        String micro = "Nb de contraintes micro : "
            + this.frame.cDb.getMicroConstraints().size() + retour;
        String meso = "Nb de contraintes meso : "
            + this.frame.cDb.getMesoConstraints().size() + retour;
        String macro = "Nb de contraintes macro : "
            + this.frame.cDb.getMacroConstraints().size() + retour;
        String rel = "Nb de contraintes rel. : "
            + this.frame.cDb.getRelationalConstraints().size() + retour;
        message = entete + micro + meso + macro + rel;
      } else if (this.base == 1) {
        String entete = "Statistiques des règles opérationnelles :" + retour;
        message = entete + this.frame.rDb.getRules().size() + " rules";
      }
      JOptionPane.showMessageDialog(this.frame, message);
    }

    public StatsAction(EditFormalConstraintsFrame parent, int base) {
      this.frame = parent;
      this.base = base;
      String name = EditFormalConstraintsFrame.this.nomStats;
      String descr = EditFormalConstraintsFrame.this.ttStatsContraintes;
      if (base == 1) {
        descr = EditFormalConstraintsFrame.this.ttStatsReglesOper;
      }
      if (base == 2) {
        descr = EditFormalConstraintsFrame.this.ttStatsReglesProc;
      }
      this.putValue(Action.SHORT_DESCRIPTION, descr);
      this.putValue(Action.NAME, name);
      this.putValue(Action.MNEMONIC_KEY, new Integer('S'));
      this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('S',
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }
  }// class StatsAction

  class EffacerBdcAction extends AbstractAction {

    EditFormalConstraintsFrame frame;

    @Override
    public void actionPerformed(ActionEvent e) {
      this.frame.cDb.clear();

      // on met � jour l'onglet contraintes de la frame
      this.frame.macroList.clear();
      this.frame.microList.clear();
      this.frame.mesoList.clear();
      this.frame.relList.clear();
      this.frame.updateConstraintLists();
      this.frame.pack();
    }

    public EffacerBdcAction(EditFormalConstraintsFrame parent) {
      this.frame = parent;
      this.putValue(Action.SHORT_DESCRIPTION,
          EditFormalConstraintsFrame.this.ttEffacer);
      this.putValue(Action.NAME, EditFormalConstraintsFrame.this.nomEffacer);
    }
  }// class EffacerBdcAction

  class EffacerBdrAction extends AbstractAction {

    EditFormalConstraintsFrame frame;

    @Override
    public void actionPerformed(ActionEvent e) {
      this.frame.rDb.clear();
      // on met à jour les onglets règles de la frame
      this.frame.updateRulesList();
      this.frame.pack();
    }

    public EffacerBdrAction(EditFormalConstraintsFrame parent) {
      this.frame = parent;
      this.putValue(Action.SHORT_DESCRIPTION,
          EditFormalConstraintsFrame.this.ttEffacerR);
      this.putValue(Action.NAME, EditFormalConstraintsFrame.this.nomEffacer);
    }
  }// class EffacerBdcAction

  public class ChargerBdcXmlAction extends AbstractAction {

    EditFormalConstraintsFrame frame;
    String chemin = "D://Users//gtouya//workspace//libcogit-cartagen//src//main//resources//xml//collagen";

    @Override
    public void actionPerformed(ActionEvent e) {
      JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(new File(this.chemin));
      fc.setDialogTitle(EditFormalConstraintsFrame.this.titreCharger);
      int returnVal = fc.showOpenDialog(this.frame);
      fc.setFileFilter(new XMLFileFilter());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File fic = fc.getSelectedFile();

      try {
        this.frame.cDb = new ConstraintDatabase(fic);
        // CollaGenComponent.getInstance().getEnvironment()
        // .setConstraintDb(EditFormalConstraintsFrame.this.cDb);
      } catch (OWLOntologyCreationException e1) {
        e1.printStackTrace();
      } catch (ParserConfigurationException e1) {
        e1.printStackTrace();
      } catch (SAXException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      // on met à jour l'onglet contraintes de la frame
      EditFormalConstraintsFrame.this.microList.clear();
      EditFormalConstraintsFrame.this.microList
          .addAll(EditFormalConstraintsFrame.this.cDb.getMicroConstraints());
      EditFormalConstraintsFrame.this.mesoList.clear();
      EditFormalConstraintsFrame.this.mesoList
          .addAll(EditFormalConstraintsFrame.this.cDb.getMesoConstraints());
      EditFormalConstraintsFrame.this.macroList.clear();
      EditFormalConstraintsFrame.this.macroList
          .addAll(EditFormalConstraintsFrame.this.cDb.getMacroConstraints());
      EditFormalConstraintsFrame.this.relList.clear();
      EditFormalConstraintsFrame.this.relList.addAll(
          EditFormalConstraintsFrame.this.cDb.getRelationalConstraints());
      this.frame.updateConstraintLists();
      this.frame.pack();
    }

    public ChargerBdcXmlAction(EditFormalConstraintsFrame parent) {
      this.frame = parent;
      this.putValue(Action.SHORT_DESCRIPTION,
          EditFormalConstraintsFrame.this.ttCharger);
      this.putValue(Action.NAME, EditFormalConstraintsFrame.this.nomCharger);
    }
  }// class ChargerBdcXmlAction

  public class ChargerBdrXmlAction extends AbstractAction {

    EditFormalConstraintsFrame frame;
    String chemin = System.getProperty("goth.dataroot")
        + "/custom/xml/cogit/guillaume/these/orchestration/contraintes/";

    @Override
    public void actionPerformed(ActionEvent e) {
      JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(new File(this.chemin));
      fc.setDialogTitle(EditFormalConstraintsFrame.this.titreCharger);
      int returnVal = fc.showOpenDialog(this.frame);
      fc.setFileFilter(new XMLFileFilter());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File fic = fc.getSelectedFile();

      try {
        this.frame.rDb = new OperationRulesDatabase(fic);
        CollaGenComponent.getInstance().getEnvironment()
            .setRulesDb(EditFormalConstraintsFrame.this.rDb);
      } catch (OWLOntologyCreationException e1) {
        e1.printStackTrace();
      } catch (ParserConfigurationException e1) {
        e1.printStackTrace();
      } catch (SAXException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      // on met � jour l'onglet regles de la frame
      this.frame.updateRulesList();
      this.frame.pack();
    }

    public ChargerBdrXmlAction(EditFormalConstraintsFrame parent) {
      this.frame = parent;
      this.putValue(Action.SHORT_DESCRIPTION,
          EditFormalConstraintsFrame.this.ttChargerR);
      this.putValue(Action.NAME, EditFormalConstraintsFrame.this.nomCharger);
    }
  }// class ChargerBdrXmlAction

  private void internationalisation() {

    // on initialise les variables
    this.setTitle(I18N.getString("EditFormalConstraintsFrame.titre"));
    this.nomOk = I18N.getString("MainLabels.lblOk");
    this.nomAnnuler = I18N.getString("MainLabels.lblCancel");
    this.nomSeuil = I18N.getString("EditFormalConstraintsFrame.nom_seuil");
    this.nomLblSeuil = I18N.getString("EditFormalConstraintsFrame.lbl_seuil");
    this.nomControle = I18N
        .getString("EditFormalConstraintsFrame.nom_controle");
    this.nomLblControle = I18N
        .getString("EditFormalConstraintsFrame.lbl_controle");
    this.nomMarge = I18N.getString("EditFormalConstraintsFrame.nom_marge");
    this.nomLblMarge = I18N.getString("EditFormalConstraintsFrame.lbl_marge");
    this.nomReduc = I18N.getString("EditFormalConstraintsFrame.nom_reduc");
    this.nomLblReduc = I18N.getString("EditFormalConstraintsFrame.lbl_reduc");
    this.nomConfig = I18N.getString("EditFormalConstraintsFrame.nom_config");
    this.nomLblConfig = I18N.getString("EditFormalConstraintsFrame.lbl_config");
    this.nomMicro = I18N.getString("EditFormalConstraintsFrame.nom_micro");
    this.nomMeso = I18N.getString("EditFormalConstraintsFrame.nom_meso");
    this.nomMacro = I18N.getString("EditFormalConstraintsFrame.nom_macro");
    this.nomRel = I18N.getString("EditFormalConstraintsFrame.nom_rel");
    this.nomMoyen = I18N.getString("EditFormalConstraintsFrame.nom_moyen");
    this.nomNom = I18N.getString("MainLabels.name");
    this.nomValeur = I18N.getString("MainLabels.value");
    this.nomLisib = I18N.getString("EditFormalConstraintsFrame.nom_lisibilite");
    this.nomPreserv = I18N
        .getString("EditFormalConstraintsFrame.nom_preservation");
    this.nomSimilaire = I18N.getString("EditFormalConstraintsFrame.similaire");
    this.nomMaintenu = I18N.getString("EditFormalConstraintsFrame.maintenu");
    this.nomForce = I18N.getString("EditFormalConstraintsFrame.force");
    this.nomEvite = I18N.getString("EditFormalConstraintsFrame.evite");
    this.nomInterdit = I18N.getString("EditFormalConstraintsFrame.interdit");
    this.nomSsUnite = I18N.getString("EditFormalConstraintsFrame.sans_unite");
    this.nomUniteTerr = I18N
        .getString("EditFormalConstraintsFrame.unite_terrain");
    this.nomUniteTerr2 = I18N
        .getString("EditFormalConstraintsFrame.unite_terrain2");
    this.nomUniteCarte = I18N
        .getString("EditFormalConstraintsFrame.unite_carte");
    this.nomUniteCarte2 = I18N
        .getString("EditFormalConstraintsFrame.unite_carte2");
    this.nomUniteAngle = I18N.getString("EditFormalConstraintsFrame.angle");
    this.titreCritSel = I18N
        .getString("EditFormalConstraintsFrame.critere_selection");
    this.nomEnreg = I18N.getString("MainLabels.lblSave");
    this.ttRatioC = I18N.getString(
        "EditFormalConstraintsFrame.tool_tip_ratio_restrict_contrainte");
    this.nomEspaceGeoCourt = I18N
        .getString("EditFormalConstraintsFrame.nom_espace_geo_abrege");
    this.nomEspaceGeo = I18N
        .getString("EditFormalConstraintsFrame.nom_espace_geo");
    this.titreMicro = I18N
        .getString("EditFormalConstraintsFrame.contrainte_micro");
    this.titreMeso = I18N
        .getString("EditFormalConstraintsFrame.contrainte_meso");
    this.titreMacro = I18N
        .getString("EditFormalConstraintsFrame.contrainte_macro");
    this.titreRel = I18N.getString("EditFormalConstraintsFrame.contrainte_rel");
    this.nomBaseDonnees = I18N
        .getString("EditFormalConstraintsFrame.nom_base_donnees");
    this.nomRegle = I18N.getString("EditFormalConstraintsFrame.nom_regle");
    this.titreConclusion = I18N
        .getString("EditFormalConstraintsFrame.conclusion");
    this.titrePremisse = I18N.getString("EditFormalConstraintsFrame.premisse");
    this.nomOperConseil = I18N
        .getString("EditFormalConstraintsFrame.nom_oper_conseil");
    this.ttOperConseil = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_oper_conseillee");
    this.nomOperRequise = I18N
        .getString("EditFormalConstraintsFrame.nom_oper_requise");
    this.ttRatioR = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_ratio_restrict_regle");
    this.titreContrs = I18N.getString("EditFormalConstraintsFrame.contraintes");
    this.titreReglesOper = I18N
        .getString("EditFormalConstraintsFrame.regles_oper");
    this.nomFichier = I18N.getString("MainLabels.lblFile");
    this.nomAide = I18N.getString("MainLabels.lblHelp");
    this.nomPourcent = I18N.getString("EditFormalConstraintsFrame.pourcentage");
    this.nomLblTerrain = I18N
        .getString("EditFormalConstraintsFrame.lbl_unite_terrain");
    this.nomLblCarte = I18N
        .getString("EditFormalConstraintsFrame.lbl_unite_carte");
    this.nomLblAngle = I18N.getString("EditFormalConstraintsFrame.lbl_angle");
    this.nomLblTerrain1 = I18N
        .getString("EditFormalConstraintsFrame.lbl_unite_terrain1");
    this.nomLblCarte1 = I18N
        .getString("EditFormalConstraintsFrame.lbl_unite_carte1");
    this.nomLblTerrain2 = I18N
        .getString("EditFormalConstraintsFrame.lbl_unite_terrain2");
    this.nomLblCarte2 = I18N
        .getString("EditFormalConstraintsFrame.lbl_unite_carte2");
    this.popupAffich = I18N
        .getString("EditFormalConstraintsFrame.popup_afficher");
    this.popupModif = I18N
        .getString("EditFormalConstraintsFrame.popup_modifier");
    this.popupSuppr = I18N
        .getString("EditFormalConstraintsFrame.popup_supprimer");
    this.nomExportContraintes = I18N
        .getString("EditFormalConstraintsFrame.nom_export_contraintes");
    this.ttExportContraintes = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_export_contraintes");
    this.nomExportRegles = I18N
        .getString("EditFormalConstraintsFrame.nom_export_regles");
    this.ttExportRegles = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_export_regles");
    this.nomStats = I18N
        .getString("EditFormalConstraintsFrame.nom_statistiques");
    this.ttStatsContraintes = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_stats_contraintes");
    this.ttStatsReglesOper = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_stats_regles_oper");
    this.nomEffacer = I18N.getString("EditFormalConstraintsFrame.nom_effacer");
    this.ttEffacer = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_effacer_bdc");
    this.ttEffacerR = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_effacer_bdr");
    this.nomCharger = I18N.getString("EditFormalConstraintsFrame.nom_charger");
    this.ttCharger = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_charger_bdc");
    this.ttChargerR = I18N
        .getString("EditFormalConstraintsFrame.tool_tip_charger_bdr");
    this.titreCharger = I18N
        .getString("EditFormalConstraintsFrame.titre_choix_charger_xml");
  }

  public ChargerBdcXmlAction getLoadXMLConstraintsAction() {
    return this.loadXMLConstraintsAction;
  }

  public void setLoadXMLConstraintsAction(
      ChargerBdcXmlAction loadXMLConstraintsAction) {
    this.loadXMLConstraintsAction = loadXMLConstraintsAction;
  }

  public ChargerBdrXmlAction getLoadXMLRulesAction() {
    return this.loadXMLRulesAction;
  }

  public void setLoadXMLRulesAction(ChargerBdrXmlAction loadXMLRulesAction) {
    this.loadXMLRulesAction = loadXMLRulesAction;
  }
}
