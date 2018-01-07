/**
 * 
 */
package fr.ign.cogit.cartagen.appli.agents;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.ign.cogit.cartagen.agents.cartacom.CartacomSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;

/**
 * @author JGaffuri
 * 
 */
public class CartAComConfigurationFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public static CartAComConfigurationFrame cartacomConfigFrame;

    public static CartAComConfigurationFrame getInstance() {
        if (CartAComConfigurationFrame.cartacomConfigFrame == null) {
            CartAComConfigurationFrame.cartacomConfigFrame = new CartAComConfigurationFrame();
            CartAComConfigurationFrame.cartacomConfigFrame.resetValues();
        }
        return CartAComConfigurationFrame.cartacomConfigFrame;
    }

    public static JTabbedPane tabbedPane = new JTabbedPane();

    // a panel for the relational constraints tab
    public static JPanel constraintsPanel = new JPanel(new GridBagLayout());
    public static Set<RelationalConstraintPanel> constraintPanels;
    // a panel for the advanced parameters tab
    public static JPanel advancedPanel = new JPanel(new GridBagLayout());
    private JSpinner spinLimitZones, spinDist, spinEnv;

    // boutons
    public final static JPanel panneauBoutons = new JPanel(new GridBagLayout());
    public final static JButton bValider = new JButton("Validate");
    public final static JButton bAnnuler = new JButton("Reset values");
    public final static JButton bEnregistrer = new JButton("Save in XML");

    public CartAComConfigurationFrame() {
        this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setResizable(false);
        this.setSize(new Dimension(400, 300));
        this.setLocation(100, 100);
        this.setTitle("CartAGen - CartACom configuration");
        this.setVisible(false);

        GridBagConstraints c;

        GridBagConstraints cont = new GridBagConstraints();
        cont.gridy = GridBagConstraints.RELATIVE;
        cont.gridx = 0;
        cont.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;

        // a panel for relational constraints
        RelationalConstraintPanel topologyConstrPanel = new RelationalConstraintPanel("BuildingNetFaceTopology", true,
                10.0);
        RelationalConstraintPanel roadProxyConstrPanel = new RelationalConstraintPanel("RoadBuildingProximity", true,
                9.0);
        RelationalConstraintPanel smallCompProxyConstrPanel = new RelationalConstraintPanel("SmallCompactsProximity",
                true, 8.0);
        RelationalConstraintPanel parallConstrPanel = new RelationalConstraintPanel("RoadBuildingParallelism", true,
                8.0);
        constraintPanels.add(topologyConstrPanel);
        constraintPanels.add(roadProxyConstrPanel);
        constraintPanels.add(smallCompProxyConstrPanel);
        constraintPanels.add(parallConstrPanel);

        constraintsPanel.add(topologyConstrPanel, cont);
        constraintsPanel.add(roadProxyConstrPanel, cont);
        constraintsPanel.add(smallCompProxyConstrPanel, cont);
        constraintsPanel.add(parallConstrPanel, cont);
        // TODO

        // a panel for advanced parameters

        // environment zone offset
        SpinnerModel modelEnv = new SpinnerNumberModel(CartacomSpecifications.ENVIRONMENT_ZONE_OFFSET, 0.0, 250.0, 1.0);
        this.spinEnv = new JSpinner(modelEnv);
        advancedPanel.add(new JLabel("Offset of the environment zone"), cont);
        advancedPanel.add(spinEnv, cont);

        // Number of limit zones
        SpinnerModel modelLimit = new SpinnerNumberModel(CartacomSpecifications.NB_LIMIT_ZONES, 1, 5, 1);
        this.spinLimitZones = new JSpinner(modelLimit);
        advancedPanel.add(new JLabel("Nb of limit zones in the environment"), cont);
        advancedPanel.add(spinLimitZones, cont);

        // Distance under which a small compact is considered 'close' enough to
        // a
        // network section
        SpinnerModel modelDist = new SpinnerNumberModel(
                CartacomSpecifications.DIST_SMALLCOMPACT_CLOSE_TO_NETWORK_SECTION, 0.0, 250.0, 1.0);
        this.spinDist = new JSpinner(modelDist);
        advancedPanel.add(new JLabel("Maximum distance to be considered close"), cont);
        advancedPanel.add(spinDist, cont);

        // panneau des boutons

        // bouton valider
        CartAComConfigurationFrame.bValider.setPreferredSize(new Dimension(110, 30));
        CartAComConfigurationFrame.bValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                CartAComConfigurationFrame.this.validateValues();
                AgentUtil.instanciateConstraints();
                CartAComConfigurationFrame.this.setVisible(false);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        CartAComConfigurationFrame.panneauBoutons.add(CartAComConfigurationFrame.bValider, c);

        // bouton annuler
        CartAComConfigurationFrame.bAnnuler.setPreferredSize(new Dimension(110, 30));
        CartAComConfigurationFrame.bAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                CartAComConfigurationFrame.this.resetValues();
                CartAComConfigurationFrame.this.setVisible(false);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        CartAComConfigurationFrame.panneauBoutons.add(CartAComConfigurationFrame.bAnnuler, c);

        // bouton enregistrer
        CartAComConfigurationFrame.bEnregistrer.setPreferredSize(new Dimension(110, 30));
        CartAComConfigurationFrame.bEnregistrer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser parcourir = new JFileChooser(
                        new File(CartAGenPlugin.getInstance().getCheminFichierConfigurationGene()));
                parcourir.setDialogType(JFileChooser.SAVE_DIALOG);
                parcourir.setApproveButtonText("Save");
                parcourir.setDialogTitle("Save");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML MiraGe", "xml");
                parcourir.setFileFilter(filter);
                /*
                 * parcourir.setSelectedFile( new
                 * File(CartAComConfigurationFrame.getInstance().
                 * getCheminFichierConfigurationCartACom()));
                 */
                // TODO save in the xml file
                int res = parcourir.showOpenDialog(CartAComConfigurationFrame.getInstance());
                if (res == JFileChooser.APPROVE_OPTION) {
                    GeneralisationSpecifications.saveToFile(parcourir.getSelectedFile());
                } else if (res == JFileChooser.ERROR_OPTION) {
                    JOptionPane.showMessageDialog(CartAComConfigurationFrame.getInstance(), "Error", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        CartAComConfigurationFrame.panneauBoutons.add(CartAComConfigurationFrame.bEnregistrer, c);

        this.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridheight = 5;
        this.add(CartAComConfigurationFrame.tabbedPane, c);

        c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        this.add(CartAComConfigurationFrame.panneauBoutons, c);

        this.pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CartAComConfigurationFrame.this.setVisible(false);
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        });

    }

    public void resetValues() {
        // restore the default values
        // TODO
    }

    public void validateValues() {
        // overwrite the xml file that contains the constraints for CartACom
        // TODO
    }

    class RelationalConstraintPanel extends JPanel {
        /****/
        private static final long serialVersionUID = 1L;
        private String name;
        private JCheckBox toConsider;
        private JSpinner importance;

        public RelationalConstraintPanel(String name, boolean toConsider, double importance) {
            super();
            this.name = name;
            this.toConsider = new JCheckBox("use constraint", toConsider);
            SpinnerModel model = new SpinnerNumberModel(importance, 1.0, 10.0, 1.0);
            this.importance = new JSpinner(model);

            // border of the panel
            Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createTitledBorder(name));
            this.setBorder(border);

            // layout of the panel
            GridBagConstraints cont = new GridBagConstraints();
            cont.gridy = GridBagConstraints.RELATIVE;
            cont.gridx = 0;
            cont.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;

            this.add(this.toConsider, cont);
            this.add(new JLabel("Importance"), cont);
            this.add(this.importance, cont);

            AgentConfigurationFrame.pBatiTaille.setBorder(BorderFactory.createTitledBorder("Size"));
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            CartAComConfigurationFrame.constraintsPanel.add(this, c);

            this.add(this.toConsider);
            this.add(this.importance);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isToConsider() {
            return toConsider.isSelected();
        }

        public void setToConsider(boolean toConsider) {
            this.toConsider.setSelected(toConsider);
        }

        public double getImportance() {
            return (double) importance.getValue();
        }

        public void setImportance(double importance) {
            this.importance.setValue(importance);
        }

    }
}
