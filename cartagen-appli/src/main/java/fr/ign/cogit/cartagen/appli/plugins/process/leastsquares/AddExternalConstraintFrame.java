package fr.ign.cogit.cartagen.appli.plugins.process.leastsquares;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import fr.ign.cogit.cartagen.appli.utilities.ClassBrowserFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.renderer.ClassSimpleNameListRenderer;

public class AddExternalConstraintFrame extends JFrame
    implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;

  private LeastSquaresFrame parent;
  private JTextField txtClass1, txtClass2, txtThreshold;
  private JComboBox<Class<?>> cbConstraint;

  public AddExternalConstraintFrame(LeastSquaresFrame parent)
      throws HeadlessException {
    super();
    this.parent = parent;
    this.setTitle("Ajouter Contrainte Externe");
    this.setSize(800, 400);

    // --------------------------------
    // panneau Contrainte à ajouter
    JPanel panelContrainte = new JPanel();
    // panneau nom de la contrainte
    JPanel panelNom = new JPanel();
    JLabel lblContrainte = new JLabel("Nom de la contrainte externe");
    cbConstraint = new JComboBox<>(parent.getExternalConstraintsArray());
    cbConstraint.setRenderer(new ClassSimpleNameListRenderer());
    ImageIcon icone = new ImageIcon(
        this.getClass().getResource("/images/browse.jpeg"));
    panelNom.add(lblContrainte);
    panelNom.add(cbConstraint);
    panelNom.setLayout(new BoxLayout(panelNom, BoxLayout.X_AXIS));
    // panneau nom de la classe 1
    JPanel panelClasse1 = new JPanel();
    JLabel lblClasse1 = new JLabel("Nom de la première classe");
    txtClass1 = new JTextField();
    JButton btnAjouter1 = new JButton(icone);
    btnAjouter1.addActionListener(this);
    btnAjouter1.setActionCommand("add1");
    panelClasse1.add(lblClasse1);
    panelClasse1.add(txtClass1);
    panelClasse1.add(btnAjouter1);
    panelClasse1.setLayout(new BoxLayout(panelClasse1, BoxLayout.X_AXIS));
    // panneau nom de la classe 2
    JPanel panelClasse2 = new JPanel();
    JLabel lblClasse2 = new JLabel("Nom de la deuxième classe");
    txtClass2 = new JTextField();
    JButton btnAjouter2 = new JButton(icone);
    btnAjouter2.addActionListener(this);
    btnAjouter2.setActionCommand("add2");
    panelClasse2.add(lblClasse2);
    panelClasse2.add(txtClass2);
    panelClasse2.add(btnAjouter2);
    panelClasse2.setLayout(new BoxLayout(panelClasse2, BoxLayout.X_AXIS));
    // panneau seuil de séparabilité
    JPanel panelSeuil = new JPanel();
    JLabel lblSeuil = new JLabel("Seuil de séparabilité");
    txtThreshold = new JTextField("0.0");
    JLabel lblMesure = new JLabel("en mm carte");
    panelSeuil.add(lblSeuil);
    panelSeuil.add(txtThreshold);
    panelSeuil.add(lblMesure);
    panelSeuil.setLayout(new BoxLayout(panelSeuil, BoxLayout.X_AXIS));
    panelContrainte.add(panelNom);
    panelContrainte.add(panelClasse1);
    panelContrainte.add(panelClasse2);
    panelContrainte.add(panelSeuil);
    panelContrainte.setLayout(new BoxLayout(panelContrainte, BoxLayout.Y_AXIS));

    // --------------------
    // panneau OK / Annuler
    JPanel panelOK = new JPanel();
    JButton btnOK = new JButton("OK");
    btnOK.addActionListener(this);
    btnOK.setActionCommand("OK");
    btnOK.setPreferredSize(new Dimension(100, 50));
    JButton btnAnnuler = new JButton("Annuler");
    btnAnnuler.addActionListener(this);
    btnAnnuler.setActionCommand("annuler");
    btnAnnuler.setPreferredSize(new Dimension(100, 50));
    panelOK.add(btnOK);
    panelOK.add(btnAnnuler);
    panelOK.setLayout(new BoxLayout(panelOK, BoxLayout.X_AXIS));

    // *********************************
    // LA FRAME
    // *********************************
    this.getContentPane().add(panelContrainte);
    this.getContentPane().add(panelOK);
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.setVisible(true);
    this.pack();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("OK")) {
      DefaultTableModel model = (DefaultTableModel) parent.tableContrRel
          .getModel();
      model.addRow(new String[] {
          ((Class<?>) cbConstraint.getSelectedItem()).getName(),
          txtClass1.getText(), txtClass2.getText(), txtThreshold.getText() });
      parent.tableContrRel.setModel(model);
      if (!parent.contraintesActivees
          .contains(((Class<?>) cbConstraint.getSelectedItem()).getName())) {
        parent.contraintesActivees
            .add(((Class<?>) cbConstraint.getSelectedItem()).getName());
        parent.poidsContraintes.put(
            ((Class<?>) cbConstraint.getSelectedItem()).getName(),
            new Double(1.0));
      }
      this.setVisible(false);
      parent.miseAjourFrame();
    } else if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("add1")) {
      ClassBrowserFrame browser = new ClassBrowserFrame(txtClass1, true);
      browser.setVisible(true);
    } else if (e.getActionCommand().equals("add2")) {
      ClassBrowserFrame browser = new ClassBrowserFrame(txtClass2, true);
      browser.setVisible(true);
    }

  }

}
