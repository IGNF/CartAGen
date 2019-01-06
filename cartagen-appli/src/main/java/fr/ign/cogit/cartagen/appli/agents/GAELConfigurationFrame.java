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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.utilities.I18N;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;

/**
 * @author JGaffuri
 * 
 */
public class GAELConfigurationFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public static GAELConfigurationFrame agentConfigFrame;

    public static GAELConfigurationFrame getInstance() {
        if (GAELConfigurationFrame.agentConfigFrame == null) {
            GAELConfigurationFrame.agentConfigFrame = new GAELConfigurationFrame();
            GAELConfigurationFrame.agentConfigFrame.resetValues();
        }
        return GAELConfigurationFrame.agentConfigFrame;
    }

    private String cheminFichierConfigurationAgent = "configurationAgent.xml";

    public String getCheminFichierConfigurationAgent() {
        return this.cheminFichierConfigurationAgent;
    }

    public static JTabbedPane panneauOnglets = new JTabbedPane();

    // bati
    public static JPanel pFieldObjectRels = new JPanel(new GridBagLayout());

    // altitude
    public final static JPanel pBatiAltitude = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiAltitude = new JCheckBox("Constrain", false);
    public final static JTextField tBatiAltitudeImp = new JTextField("" + AgentSpecifications.BUILDING_ALTITUDE_IMP, 5);

    // occ sol
    public final static JPanel pBatiOccSol = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiOccSol = new JCheckBox("Constrain", false);
    public final static JTextField tBatiOccSolImp = new JTextField("" + AgentSpecifications.BUILDING_LANDUSE_IMP, 5);

    // hydrographie
    public final static JPanel pHydro = new JPanel(new GridBagLayout());

    // proximite routier
    public final static JPanel pHydroProximiteRoutier = new JPanel(new GridBagLayout());
    public final static JCheckBox cHydroProximiteRoutier = new JCheckBox("Constrain", false);
    public final static JTextField tHydroProximiteRoutierImp = new JTextField(
            "" + AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP, 5);

    // ecoulement
    public final static JPanel pHydroEcoulement = new JPanel(new GridBagLayout());
    public final static JCheckBox cHydroEcoulement = new JCheckBox("Constrain", false);
    public final static JTextField tHydroEcoulementImp = new JTextField(
            "" + AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP, 5);

    // platitude lac
    public final static JPanel pHydroPlatitudeLac = new JPanel(new GridBagLayout());
    public final static JCheckBox cHydroPlatitudeLac = new JCheckBox("Constrain", false);
    public final static JTextField tHydroPlatitudeLacImp = new JTextField(
            "" + AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP, 5);

    // champ relief
    public final static JPanel pRelief = new JPanel(new GridBagLayout());

    // position points
    public final static JPanel pReliefPositionPoint = new JPanel(new GridBagLayout());
    public final static JCheckBox cReliefPositionPoint = new JCheckBox("Constrain", false);
    public final static JTextField tReliefPositionPointImp = new JTextField("", 5);

    // occ sol
    public final static JPanel pOccSol = new JPanel(new GridBagLayout());

    // position points
    public final static JPanel pOccSolPositionPoint = new JPanel(new GridBagLayout());
    public final static JCheckBox cOccSolPositionPoint = new JCheckBox("Constrain", false);
    public final static JTextField tOccSolPositionPointImp = new JTextField("", 5);

    // boutons
    public final static JPanel panneauBoutons = new JPanel(new GridBagLayout());
    public final static JButton bValider = new JButton("Validate");
    public final static JButton bAnnuler = new JButton("Reset values");
    public final static JButton bEnregistrer = new JButton("Save in XML");

    public GAELConfigurationFrame() {
        this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setResizable(false);
        this.setSize(new Dimension(400, 300));
        this.setLocation(100, 100);
        this.setTitle("CartAGen - Agent configuration");
        this.setVisible(false);

        GridBagConstraints c;

        GridBagConstraints cont = new GridBagConstraints();
        cont.gridy = GridBagConstraints.RELATIVE;
        cont.gridx = 0;
        cont.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;

        // bati

        // altitude
        GAELConfigurationFrame.pBatiAltitude.add(GAELConfigurationFrame.cBatiAltitude, cont);
        GAELConfigurationFrame.pBatiAltitude.add(new JLabel("Importance"), cont);
        GAELConfigurationFrame.pBatiAltitude.add(GAELConfigurationFrame.tBatiAltitudeImp, cont);

        GAELConfigurationFrame.pBatiAltitude.setBorder(BorderFactory.createTitledBorder("Altitude"));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        GAELConfigurationFrame.pFieldObjectRels.add(GAELConfigurationFrame.pBatiAltitude, c);

        GAELConfigurationFrame.panneauOnglets.addTab(I18N.getString("CartAComConfigurationFrame.relConstraints"),
                new ImageIcon(
                        GAELConfigurationFrame.class.getResource("/images/bati.gif").getPath().replaceAll("%20", " ")),
                GAELConfigurationFrame.pFieldObjectRels, "Configure constraints on object-field relations");

        // hydro

        // proximite routier
        GAELConfigurationFrame.pHydroProximiteRoutier.add(GAELConfigurationFrame.cHydroProximiteRoutier, cont);
        GAELConfigurationFrame.pHydroProximiteRoutier.add(new JLabel("Importance"), cont);
        GAELConfigurationFrame.pHydroProximiteRoutier.add(GAELConfigurationFrame.tHydroProximiteRoutierImp, cont);

        GAELConfigurationFrame.pHydroProximiteRoutier
                .setBorder(BorderFactory.createTitledBorder("Proximity of road network"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        GAELConfigurationFrame.pHydro.add(GAELConfigurationFrame.pHydroProximiteRoutier, c);

        // ecoulement
        GAELConfigurationFrame.pHydroEcoulement.add(GAELConfigurationFrame.cHydroEcoulement, cont);
        GAELConfigurationFrame.pHydroEcoulement.add(new JLabel("Importance"), cont);
        GAELConfigurationFrame.pHydroEcoulement.add(GAELConfigurationFrame.tHydroEcoulementImp, cont);

        GAELConfigurationFrame.pHydroEcoulement.setBorder(BorderFactory.createTitledBorder("Flow"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        GAELConfigurationFrame.pHydro.add(GAELConfigurationFrame.pHydroEcoulement, c);

        // platitude lac
        GAELConfigurationFrame.pHydroPlatitudeLac.add(GAELConfigurationFrame.cHydroPlatitudeLac, cont);
        GAELConfigurationFrame.pHydroPlatitudeLac.add(new JLabel("Importance"), cont);
        GAELConfigurationFrame.pHydroPlatitudeLac.add(GAELConfigurationFrame.tHydroPlatitudeLacImp, cont);

        GAELConfigurationFrame.pHydroPlatitudeLac.setBorder(BorderFactory.createTitledBorder("Lake flatness"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        GAELConfigurationFrame.pHydro.add(GAELConfigurationFrame.pHydroPlatitudeLac, c);

        GAELConfigurationFrame.panneauOnglets.addTab("Hydro",
                new ImageIcon(
                        GAELConfigurationFrame.class.getResource("/images/hydro.gif").getPath().replaceAll("%20", " ")),
                GAELConfigurationFrame.pHydro, "Configure constraints on hydro network");

        // champ relief

        // postion points
        GAELConfigurationFrame.pReliefPositionPoint.add(GAELConfigurationFrame.cReliefPositionPoint, cont);
        GAELConfigurationFrame.pReliefPositionPoint.add(new JLabel("Importance"), cont);
        GAELConfigurationFrame.pReliefPositionPoint.add(GAELConfigurationFrame.tReliefPositionPointImp, cont);

        GAELConfigurationFrame.pReliefPositionPoint.setBorder(BorderFactory.createTitledBorder("Points position"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        GAELConfigurationFrame.pRelief.add(GAELConfigurationFrame.pReliefPositionPoint, c);

        GAELConfigurationFrame.panneauOnglets
                .addTab("Relief",
                        new ImageIcon(GAELConfigurationFrame.class.getResource("/images/relief.gif").getPath()
                                .replaceAll("%20", " ")),
                        GAELConfigurationFrame.pRelief, "Configure constraints on relief");

        // champ occ sol

        // postion points
        GAELConfigurationFrame.pOccSolPositionPoint.add(GAELConfigurationFrame.cOccSolPositionPoint, cont);
        GAELConfigurationFrame.pOccSolPositionPoint.add(new JLabel("Importance"), cont);
        GAELConfigurationFrame.pOccSolPositionPoint.add(GAELConfigurationFrame.tOccSolPositionPointImp, cont);

        GAELConfigurationFrame.pOccSolPositionPoint.setBorder(BorderFactory.createTitledBorder("Points position"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        GAELConfigurationFrame.pOccSol.add(GAELConfigurationFrame.pOccSolPositionPoint, c);

        GAELConfigurationFrame.panneauOnglets
                .addTab("Occ. sol",
                        new ImageIcon(GAELConfigurationFrame.class.getResource("/images/occsol.gif").getPath()
                                .replaceAll("%20", " ")),
                        GAELConfigurationFrame.pOccSol, "Configure constraints on landscape");

        // panneau des boutons

        // bouton valider
        GAELConfigurationFrame.bValider.setPreferredSize(new Dimension(110, 30));
        GAELConfigurationFrame.bValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GAELConfigurationFrame.this.validateValues();
                AgentUtil.instanciateConstraints();
                GAELConfigurationFrame.this.setVisible(false);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        GAELConfigurationFrame.panneauBoutons.add(GAELConfigurationFrame.bValider, c);

        // bouton annuler
        GAELConfigurationFrame.bAnnuler.setPreferredSize(new Dimension(110, 30));
        GAELConfigurationFrame.bAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                GAELConfigurationFrame.this.resetValues();
                GAELConfigurationFrame.this.setVisible(false);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        GAELConfigurationFrame.panneauBoutons.add(GAELConfigurationFrame.bAnnuler, c);

        // bouton enregistrer
        GAELConfigurationFrame.bEnregistrer.setPreferredSize(new Dimension(110, 30));
        GAELConfigurationFrame.bEnregistrer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser parcourir = new JFileChooser(
                        new File(CartAGenPlugin.getInstance().getCheminFichierConfigurationGene()));
                parcourir.setDialogType(JFileChooser.SAVE_DIALOG);
                parcourir.setApproveButtonText("Save");
                parcourir.setDialogTitle("Save");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML MiraGe", "xml");
                parcourir.setFileFilter(filter);
                parcourir.setSelectedFile(
                        new File(GAELConfigurationFrame.getInstance().getCheminFichierConfigurationAgent()));
                int res = parcourir.showOpenDialog(GAELConfigurationFrame.getInstance());
                if (res == JFileChooser.APPROVE_OPTION) {
                    GeneralisationSpecifications.saveToFile(parcourir.getSelectedFile());
                } else if (res == JFileChooser.ERROR_OPTION) {
                    JOptionPane.showMessageDialog(GAELConfigurationFrame.getInstance(), "Error", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        GAELConfigurationFrame.panneauBoutons.add(GAELConfigurationFrame.bEnregistrer, c);

        this.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridheight = 5;
        this.add(GAELConfigurationFrame.panneauOnglets, c);

        c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        this.add(GAELConfigurationFrame.panneauBoutons, c);

        this.pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GAELConfigurationFrame.this.setVisible(false);
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        });

    }

    public void resetValues() {

        // bati
        // altitude
        GAELConfigurationFrame.tBatiAltitudeImp.setText("" + AgentSpecifications.BUILDING_ALTITUDE_IMP);
        GAELConfigurationFrame.cBatiAltitude.setSelected(AgentSpecifications.BUILDING_ALTITUDE);
        // occ sol
        GAELConfigurationFrame.tBatiOccSolImp.setText("" + AgentSpecifications.BUILDING_LANDUSE_IMP);
        GAELConfigurationFrame.cBatiOccSol.setSelected(AgentSpecifications.BUILDING_LANDUSE);

        // hydro

        // proximite routier
        GAELConfigurationFrame.tHydroProximiteRoutierImp.setText("" + AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP);
        GAELConfigurationFrame.cHydroProximiteRoutier.setSelected(AgentSpecifications.RIVER_ROAD_PROXIMITY);
        // ecoulement
        GAELConfigurationFrame.tHydroEcoulementImp.setText("" + AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP);
        GAELConfigurationFrame.cHydroProximiteRoutier.setSelected(AgentSpecifications.RIVER_ROAD_PROXIMITY);
        // platitude lac
        GAELConfigurationFrame.tHydroPlatitudeLacImp.setText("" + AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP);
        GAELConfigurationFrame.cHydroPlatitudeLac.setSelected(AgentSpecifications.LAKE_FLATNESS_PRESERVATION);

    }

    public void validateValues() {

        // bati
        // altitude
        AgentSpecifications.BUILDING_ALTITUDE_IMP = Double
                .parseDouble(GAELConfigurationFrame.tBatiAltitudeImp.getText());
        AgentSpecifications.BUILDING_ALTITUDE = GAELConfigurationFrame.cBatiAltitude.isSelected();
        // occupation du sol
        AgentSpecifications.BUILDING_LANDUSE_IMP = Double.parseDouble(GAELConfigurationFrame.tBatiOccSolImp.getText());
        AgentSpecifications.BUILDING_LANDUSE = GAELConfigurationFrame.cBatiOccSol.isSelected();

        // hydro

        // proximite routier
        AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP = Double
                .parseDouble(GAELConfigurationFrame.tHydroProximiteRoutierImp.getText());
        AgentSpecifications.RIVER_ROAD_PROXIMITY = GAELConfigurationFrame.cHydroProximiteRoutier.isSelected();
        // ecoulement
        AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP = Double
                .parseDouble(GAELConfigurationFrame.tHydroEcoulementImp.getText());
        AgentSpecifications.RIVER_FLOW_PRESERVATION = GAELConfigurationFrame.cHydroEcoulement.isSelected();
        // platitude lac
        AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP = Double
                .parseDouble(GAELConfigurationFrame.tHydroPlatitudeLacImp.getText());
        AgentSpecifications.LAKE_FLATNESS_PRESERVATION = GAELConfigurationFrame.cHydroPlatitudeLac.isSelected();

    }

}
