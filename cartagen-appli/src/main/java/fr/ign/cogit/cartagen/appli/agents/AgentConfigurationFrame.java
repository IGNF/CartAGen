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
import fr.ign.cogit.cartagen.appli.utilities.I18N;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;

/**
 * @author JGaffuri
 * 
 */
public class AgentConfigurationFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public static AgentConfigurationFrame agentConfigFrame;

    public static AgentConfigurationFrame getInstance() {
        if (AgentConfigurationFrame.agentConfigFrame == null) {
            AgentConfigurationFrame.agentConfigFrame = new AgentConfigurationFrame();
            AgentConfigurationFrame.agentConfigFrame.resetValues();
        }
        return AgentConfigurationFrame.agentConfigFrame;
    }

    private String cheminFichierConfigurationAgent = "configurationAgent.xml";

    public String getCheminFichierConfigurationAgent() {
        return this.cheminFichierConfigurationAgent;
    }

    public static JTabbedPane tabbedPane = new JTabbedPane();

    // bati
    public static JPanel pBati = new JPanel(new GridBagLayout());

    // taille
    public final static JPanel pBatiTaille = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiTaille = new JCheckBox("Constrain", false);
    public final static JTextField tBatiTailleImp = new JTextField(
            "" + AgentSpecifications.BUILDING_SIZE_CONSTRAINT_IMP, 5);
    public final static JTextField tBatiTailleAireMini = new JTextField(
            "" + GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT, 5);
    public final static JTextField tBatiTailleSeuilSuppression = new JTextField(
            "" + GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT, 5);

    // granularite
    public final static JPanel pBatiGranularite = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiGranularite = new JCheckBox("Constrain", false);
    public final static JTextField tBatiGranulariteImp = new JTextField(
            "" + AgentSpecifications.BULDING_GRANULARITY_IMP, 5);
    public final static JTextField tBatiGranulariteLongueurMini = new JTextField(
            "" + GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE, 5);

    // equarrite
    public final static JPanel pBatiEquarrite = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiEquarrite = new JCheckBox("Constrain", false);
    public final static JTextField tBatiEquarriteImp = new JTextField("" + AgentSpecifications.BUILDING_SQUARENESS_IMP,
            5);

    // largeur locale
    public final static JPanel pBatiLargeurLocale = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiLargeurLocale = new JCheckBox("Constrain", false);
    public final static JTextField tBatiLargeurLocaleImp = new JTextField(
            "" + AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP, 5);

    // convexite
    public final static JPanel pBatiConvexite = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiConvexite = new JCheckBox("Constrain", false);
    public final static JTextField tBatiConvexiteImp = new JTextField("" + AgentSpecifications.BUILDING_CONVEXITY_IMP,
            5);

    // elongation
    public final static JPanel pBatiElongation = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiElongation = new JCheckBox("Constrain", false);
    public final static JTextField tBatiElongationImp = new JTextField("" + AgentSpecifications.BUILDING_ELONGATION_IMP,
            5);

    // orientation
    public final static JPanel pBatiOrientation = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiOrientation = new JCheckBox("Constrain", false);
    public final static JTextField tBatiOrientationImp = new JTextField(
            "" + AgentSpecifications.BUILDING_ORIENTATION_IMP, 5);

    // altitude
    public final static JPanel pBatiAltitude = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiAltitude = new JCheckBox("Constrain", false);
    public final static JTextField tBatiAltitudeImp = new JTextField("" + AgentSpecifications.BUILDING_ALTITUDE_IMP, 5);

    // occ sol
    public final static JPanel pBatiOccSol = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiOccSol = new JCheckBox("Constrain", false);
    public final static JTextField tBatiOccSolImp = new JTextField("" + AgentSpecifications.BUILDING_LANDUSE_IMP, 5);

    // routier
    public final static JPanel pRoutier = new JPanel(new GridBagLayout());

    // empatement
    public final static JPanel pRoutierEmpatement = new JPanel(new GridBagLayout());
    public final static JCheckBox cRoutierEmpatement = new JCheckBox("Constrain", false);
    public final static JTextField tRoutierEmpatementImp = new JTextField("" + AgentSpecifications.ROAD_COALESCENCE_IMP,
            5);
    public final static JTextField tRoutierCoeffPropagationEmpatement = new JTextField(
            "" + GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT, 5);

    // controle deformation des troncons
    public final static JPanel pRoutierControleDeformation = new JPanel(new GridBagLayout());
    public final static JCheckBox cRoutierControleDeformation = new JCheckBox("Constrain", false);
    public final static JTextField tRoutierControleDeformationImp = new JTextField(
            "" + AgentSpecifications.ROAD_CONTROL_DISTORTION_IMP, 5);

    // impasses
    public final static JPanel pRoutierImpasses = new JPanel(new GridBagLayout());
    public final static JCheckBox cRoutierImpasses = new JCheckBox("Constrain", false);
    public final static JTextField tRoutierImpassesImp = new JTextField("" + AgentSpecifications.DEAD_END_ROADS_IMP, 5);

    // densite
    public final static JPanel pRoutierDensite = new JPanel(new GridBagLayout());
    public final static JCheckBox cRoutierDensite = new JCheckBox("Constrain", false);
    public final static JTextField tRoutierDensiteImp = new JTextField("" + AgentSpecifications.ROAD_DENSITY_IMP, 5);

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

    // block contraints
    public final static JPanel pBlock = new JPanel(new GridBagLayout());

    // proximite
    public final static JPanel pBatiProximite = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiProximite = new JCheckBox("Constrain", false);
    public final static JTextField tBatiProximiteImp = new JTextField(
            "" + AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP, 5);

    // densite ilot
    public final static JPanel pBatiDensiteIlot = new JPanel(new GridBagLayout());
    public final static JCheckBox cBatiDensiteIlot = new JCheckBox("Constrain", false);
    public final static JTextField tBatiDensiteIlotImp = new JTextField(
            "" + AgentSpecifications.BLOCK_BUILDING_DENSITY_IMP, 5);

    // spatial distribution
    public final static JPanel pSpatialDistribution = new JPanel(new GridBagLayout());
    public final static JCheckBox cSpatialDistribution = new JCheckBox("Constrain", false);
    public final static JTextField tSpatialDistribution = new JTextField(
            "" + AgentSpecifications.BUILDING_SPATIAL_DISTRIBUTION_IMP, 5);

    // big building preservation
    public final static JPanel pBigBuildings = new JPanel(new GridBagLayout());
    public final static JCheckBox cBigBuildings = new JCheckBox("Constrain", false);
    public final static JTextField tBigBuildings = new JTextField(
            "" + AgentSpecifications.LARGE_BUILDING_PRESERVATION_IMP, 5);

    // town constraints
    public final static JPanel pTown = new JPanel(new GridBagLayout());

    // street density
    public final static JPanel pStreetDensity = new JPanel(new GridBagLayout());
    public final static JCheckBox cStreetDensity = new JCheckBox("Constrain", false);
    public final static JTextField tStreetDensity = new JTextField("", 5);

    // boutons
    public final static JPanel panneauBoutons = new JPanel(new GridBagLayout());
    public final static JButton bValider = new JButton("Validate");
    public final static JButton bAnnuler = new JButton("Reset values");
    public final static JButton bEnregistrer = new JButton("Save in XML");

    public AgentConfigurationFrame() {
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

        // taille
        AgentConfigurationFrame.pBatiTaille.add(AgentConfigurationFrame.cBatiTaille, cont);
        AgentConfigurationFrame.pBatiTaille.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiTaille.add(AgentConfigurationFrame.tBatiTailleImp, cont);
        AgentConfigurationFrame.pBatiTaille.add(new JLabel("Minimal area"), cont);
        AgentConfigurationFrame.pBatiTaille.add(AgentConfigurationFrame.tBatiTailleAireMini, cont);
        AgentConfigurationFrame.pBatiTaille.add(new JLabel("Suppression area"), cont);
        AgentConfigurationFrame.pBatiTaille.add(AgentConfigurationFrame.tBatiTailleSeuilSuppression, cont);

        AgentConfigurationFrame.pBatiTaille.setBorder(BorderFactory.createTitledBorder("Size"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiTaille, c);

        // granularite
        AgentConfigurationFrame.pBatiGranularite.add(AgentConfigurationFrame.cBatiGranularite, cont);
        AgentConfigurationFrame.pBatiGranularite.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiGranularite.add(AgentConfigurationFrame.tBatiGranulariteImp, cont);
        AgentConfigurationFrame.pBatiGranularite.add(new JLabel("Minimal length"), cont);
        AgentConfigurationFrame.pBatiGranularite.add(AgentConfigurationFrame.tBatiGranulariteLongueurMini, cont);

        AgentConfigurationFrame.pBatiGranularite.setBorder(BorderFactory.createTitledBorder("Granularity"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiGranularite, c);

        // equarrite
        AgentConfigurationFrame.pBatiEquarrite.add(AgentConfigurationFrame.cBatiEquarrite, cont);
        AgentConfigurationFrame.pBatiEquarrite.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiEquarrite.add(AgentConfigurationFrame.tBatiEquarriteImp, cont);

        AgentConfigurationFrame.pBatiEquarrite.setBorder(BorderFactory.createTitledBorder("Squareness"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiEquarrite, c);

        // largeur locale
        AgentConfigurationFrame.pBatiLargeurLocale.add(AgentConfigurationFrame.cBatiLargeurLocale, cont);
        AgentConfigurationFrame.pBatiLargeurLocale.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiLargeurLocale.add(AgentConfigurationFrame.tBatiLargeurLocaleImp, cont);

        AgentConfigurationFrame.pBatiLargeurLocale.setBorder(BorderFactory.createTitledBorder("Local width"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiLargeurLocale, c);

        // convexite
        AgentConfigurationFrame.pBatiConvexite.add(AgentConfigurationFrame.cBatiConvexite, cont);
        AgentConfigurationFrame.pBatiConvexite.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiConvexite.add(AgentConfigurationFrame.tBatiConvexiteImp, cont);

        AgentConfigurationFrame.pBatiConvexite.setBorder(BorderFactory.createTitledBorder("Convexity"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiConvexite, c);

        // elongation
        AgentConfigurationFrame.pBatiElongation.add(AgentConfigurationFrame.cBatiElongation, cont);
        AgentConfigurationFrame.pBatiElongation.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiElongation.add(AgentConfigurationFrame.tBatiElongationImp, cont);

        AgentConfigurationFrame.pBatiElongation.setBorder(BorderFactory.createTitledBorder("Elongation"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiElongation, c);

        // orientation
        AgentConfigurationFrame.pBatiOrientation.add(AgentConfigurationFrame.cBatiOrientation, cont);
        AgentConfigurationFrame.pBatiOrientation.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiOrientation.add(AgentConfigurationFrame.tBatiOrientationImp, cont);

        AgentConfigurationFrame.pBatiOrientation.setBorder(BorderFactory.createTitledBorder("Orientation"));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiOrientation, c);

        // altitude
        AgentConfigurationFrame.pBatiAltitude.add(AgentConfigurationFrame.cBatiAltitude, cont);
        AgentConfigurationFrame.pBatiAltitude.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiAltitude.add(AgentConfigurationFrame.tBatiAltitudeImp, cont);

        AgentConfigurationFrame.pBatiAltitude.setBorder(BorderFactory.createTitledBorder("Altitude"));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiAltitude, c);

        AgentConfigurationFrame.tabbedPane.addTab(I18N.getString("ThemeLabel.buildingShort"),
                new ImageIcon(
                        AgentConfigurationFrame.class.getResource("/images/bati.gif").getPath().replaceAll("%20", " ")),
                AgentConfigurationFrame.pBati, "Configure constraints on urban data");

        // routier

        // empatement
        AgentConfigurationFrame.pRoutierEmpatement.add(AgentConfigurationFrame.cRoutierEmpatement, cont);
        AgentConfigurationFrame.pRoutierEmpatement.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pRoutierEmpatement.add(AgentConfigurationFrame.tRoutierEmpatementImp, cont);
        AgentConfigurationFrame.pRoutierEmpatement.add(new JLabel("Propagation coeff"), cont);
        AgentConfigurationFrame.pRoutierEmpatement.add(AgentConfigurationFrame.tRoutierCoeffPropagationEmpatement,
                cont);

        AgentConfigurationFrame.pRoutierEmpatement.setBorder(BorderFactory.createTitledBorder("Coalescence"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pRoutier.add(AgentConfigurationFrame.pRoutierEmpatement, c);

        // controle deformation des troncons
        AgentConfigurationFrame.pRoutierControleDeformation.add(AgentConfigurationFrame.cRoutierControleDeformation,
                cont);
        AgentConfigurationFrame.pRoutierControleDeformation.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pRoutierControleDeformation.add(AgentConfigurationFrame.tRoutierControleDeformationImp,
                cont);

        AgentConfigurationFrame.pRoutierControleDeformation
                .setBorder(BorderFactory.createTitledBorder("Deformation control"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pRoutier.add(AgentConfigurationFrame.pRoutierControleDeformation, c);

        // impasses
        AgentConfigurationFrame.pRoutierImpasses.add(AgentConfigurationFrame.cRoutierImpasses, cont);
        AgentConfigurationFrame.pRoutierImpasses.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pRoutierImpasses.add(AgentConfigurationFrame.tRoutierImpassesImp, cont);

        AgentConfigurationFrame.pRoutierImpasses.setBorder(BorderFactory.createTitledBorder("Dead ends presence"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pRoutier.add(AgentConfigurationFrame.pRoutierImpasses, c);

        // densite
        AgentConfigurationFrame.pRoutierDensite.add(AgentConfigurationFrame.cRoutierDensite, cont);
        AgentConfigurationFrame.pRoutierDensite.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pRoutierDensite.add(AgentConfigurationFrame.tRoutierDensiteImp, cont);

        AgentConfigurationFrame.pRoutierDensite.setBorder(BorderFactory.createTitledBorder("Density"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pRoutier.add(AgentConfigurationFrame.pRoutierDensite, c);

        AgentConfigurationFrame.tabbedPane.addTab(I18N.getString("ThemeLabel.roadsShort"),
                new ImageIcon(AgentConfigurationFrame.class.getResource("/images/routier.gif").getPath()
                        .replaceAll("%20", " ")),
                AgentConfigurationFrame.pRoutier, "Configure constraints on road network");

        // hydro

        // proximite routier
        AgentConfigurationFrame.pHydroProximiteRoutier.add(AgentConfigurationFrame.cHydroProximiteRoutier, cont);
        AgentConfigurationFrame.pHydroProximiteRoutier.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pHydroProximiteRoutier.add(AgentConfigurationFrame.tHydroProximiteRoutierImp, cont);

        AgentConfigurationFrame.pHydroProximiteRoutier
                .setBorder(BorderFactory.createTitledBorder("Proximity of road network"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pHydro.add(AgentConfigurationFrame.pHydroProximiteRoutier, c);

        // ecoulement
        AgentConfigurationFrame.pHydroEcoulement.add(AgentConfigurationFrame.cHydroEcoulement, cont);
        AgentConfigurationFrame.pHydroEcoulement.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pHydroEcoulement.add(AgentConfigurationFrame.tHydroEcoulementImp, cont);

        AgentConfigurationFrame.pHydroEcoulement.setBorder(BorderFactory.createTitledBorder("Flow"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pHydro.add(AgentConfigurationFrame.pHydroEcoulement, c);

        // platitude lac
        AgentConfigurationFrame.pHydroPlatitudeLac.add(AgentConfigurationFrame.cHydroPlatitudeLac, cont);
        AgentConfigurationFrame.pHydroPlatitudeLac.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pHydroPlatitudeLac.add(AgentConfigurationFrame.tHydroPlatitudeLacImp, cont);

        AgentConfigurationFrame.pHydroPlatitudeLac.setBorder(BorderFactory.createTitledBorder("Lake flatness"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pHydro.add(AgentConfigurationFrame.pHydroPlatitudeLac, c);

        AgentConfigurationFrame.tabbedPane.addTab(
                I18N.getString("ThemeLabel.riversShort"), new ImageIcon(AgentConfigurationFrame.class
                        .getResource("/images/hydro.gif").getPath().replaceAll("%20", " ")),
                AgentConfigurationFrame.pHydro, "Configure constraints on hydro network");

        // blocks

        // proximite
        AgentConfigurationFrame.pBatiProximite.add(AgentConfigurationFrame.cBatiProximite, cont);
        AgentConfigurationFrame.pBatiProximite.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiProximite.add(AgentConfigurationFrame.tBatiProximiteImp, cont);

        AgentConfigurationFrame.pBatiProximite.setBorder(BorderFactory.createTitledBorder("Proximity"));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBlock.add(AgentConfigurationFrame.pBatiProximite, c);

        // densite ilot
        AgentConfigurationFrame.pBatiDensiteIlot.add(AgentConfigurationFrame.cBatiDensiteIlot, cont);
        AgentConfigurationFrame.pBatiDensiteIlot.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBatiDensiteIlot.add(AgentConfigurationFrame.tBatiDensiteIlotImp, cont);

        AgentConfigurationFrame.pBatiDensiteIlot.setBorder(BorderFactory.createTitledBorder("Block density"));
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBlock.add(AgentConfigurationFrame.pBatiDensiteIlot, c);

        // spatial distribution
        AgentConfigurationFrame.pSpatialDistribution.add(AgentConfigurationFrame.cSpatialDistribution, cont);
        AgentConfigurationFrame.pSpatialDistribution.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pSpatialDistribution.add(AgentConfigurationFrame.tSpatialDistribution, cont);

        AgentConfigurationFrame.pSpatialDistribution
                .setBorder(BorderFactory.createTitledBorder("Spatial distribution"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBlock.add(AgentConfigurationFrame.pSpatialDistribution, c);

        // big building preservation
        AgentConfigurationFrame.pBigBuildings.add(AgentConfigurationFrame.cBigBuildings, cont);
        AgentConfigurationFrame.pBigBuildings.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pBigBuildings.add(AgentConfigurationFrame.tBigBuildings, cont);

        AgentConfigurationFrame.pBigBuildings.setBorder(BorderFactory.createTitledBorder("Big buildings preservation"));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pBlock.add(AgentConfigurationFrame.pBigBuildings, c);

        AgentConfigurationFrame.tabbedPane.addTab(
                I18N.getString("ThemeLabel.blockShort"), new ImageIcon(AgentConfigurationFrame.class
                        .getResource("/images/block.gif").getPath().replaceAll("%20", " ")),
                AgentConfigurationFrame.pBlock, "Configure constraints on blocks");

        // town constraints

        // street density
        AgentConfigurationFrame.pStreetDensity.add(AgentConfigurationFrame.cStreetDensity, cont);
        AgentConfigurationFrame.pStreetDensity.add(new JLabel("Importance"), cont);
        AgentConfigurationFrame.pStreetDensity.add(AgentConfigurationFrame.tStreetDensity, cont);

        AgentConfigurationFrame.pStreetDensity.setBorder(BorderFactory.createTitledBorder("Street density"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        AgentConfigurationFrame.pTown.add(AgentConfigurationFrame.pStreetDensity, c);

        AgentConfigurationFrame.tabbedPane.addTab(I18N.getString("ThemeLabel.townShort"),
                new ImageIcon(
                        AgentConfigurationFrame.class.getResource("/images/town.gif").getPath().replaceAll("%20", " ")),
                AgentConfigurationFrame.pTown, "Configure constraints on towns");

        // panneau des boutons

        // bouton valider
        AgentConfigurationFrame.bValider.setPreferredSize(new Dimension(110, 30));
        AgentConfigurationFrame.bValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                AgentConfigurationFrame.this.validateValues();
                AgentUtil.instanciateConstraints();
                AgentConfigurationFrame.this.setVisible(false);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        AgentConfigurationFrame.panneauBoutons.add(AgentConfigurationFrame.bValider, c);

        // bouton annuler
        AgentConfigurationFrame.bAnnuler.setPreferredSize(new Dimension(110, 30));
        AgentConfigurationFrame.bAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                AgentConfigurationFrame.this.resetValues();
                AgentConfigurationFrame.this.setVisible(false);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        AgentConfigurationFrame.panneauBoutons.add(AgentConfigurationFrame.bAnnuler, c);

        // bouton enregistrer
        AgentConfigurationFrame.bEnregistrer.setPreferredSize(new Dimension(110, 30));
        AgentConfigurationFrame.bEnregistrer.addActionListener(new ActionListener() {
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
                        new File(AgentConfigurationFrame.getInstance().getCheminFichierConfigurationAgent()));
                int res = parcourir.showOpenDialog(AgentConfigurationFrame.getInstance());
                if (res == JFileChooser.APPROVE_OPTION) {
                    GeneralisationSpecifications.saveToFile(parcourir.getSelectedFile());
                } else if (res == JFileChooser.ERROR_OPTION) {
                    JOptionPane.showMessageDialog(AgentConfigurationFrame.getInstance(), "Error", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        AgentConfigurationFrame.panneauBoutons.add(AgentConfigurationFrame.bEnregistrer, c);

        this.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridheight = 5;
        this.add(AgentConfigurationFrame.tabbedPane, c);

        c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        this.add(AgentConfigurationFrame.panneauBoutons, c);

        this.pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AgentConfigurationFrame.this.setVisible(false);
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        });

    }

    public void resetValues() {

        // bati

        // taille
        AgentConfigurationFrame.cBatiTaille.setSelected(AgentSpecifications.BUILDING_SIZE_CONSTRAINT);
        AgentConfigurationFrame.tBatiTailleImp.setText("" + AgentSpecifications.BUILDING_SIZE_CONSTRAINT_IMP);
        AgentConfigurationFrame.tBatiTailleAireMini.setText("" + GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT);
        AgentConfigurationFrame.tBatiTailleSeuilSuppression
                .setText("" + GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT);
        // granularite
        AgentConfigurationFrame.cBatiGranularite.setSelected(AgentSpecifications.BUILDING_GRANULARITY);
        AgentConfigurationFrame.tBatiGranulariteImp.setText("" + AgentSpecifications.BULDING_GRANULARITY_IMP);
        AgentConfigurationFrame.tBatiGranulariteLongueurMini
                .setText("" + GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE);
        // equaritte
        AgentConfigurationFrame.tBatiEquarriteImp.setText("" + AgentSpecifications.BUILDING_SQUARENESS_IMP);
        AgentConfigurationFrame.cBatiEquarrite.setSelected(AgentSpecifications.BUILDING_SQUARENESS);
        // largeur locale
        AgentConfigurationFrame.tBatiLargeurLocaleImp.setText("" + AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP);
        AgentConfigurationFrame.cBatiLargeurLocale.setSelected(AgentSpecifications.BUILDING_LOCAL_WIDTH);
        // convexite
        AgentConfigurationFrame.tBatiConvexiteImp.setText("" + AgentSpecifications.BUILDING_CONVEXITY_IMP);
        AgentConfigurationFrame.cBatiConvexite.setSelected(AgentSpecifications.BUILDING_CONVEXITY);
        // elongation
        AgentConfigurationFrame.tBatiElongationImp.setText("" + AgentSpecifications.BUILDING_ELONGATION_IMP);
        AgentConfigurationFrame.cBatiElongation.setSelected(AgentSpecifications.BUILDING_ELONGATION);
        // orientation
        AgentConfigurationFrame.tBatiOrientationImp.setText("" + AgentSpecifications.BUILDING_ORIENTATION_IMP);
        AgentConfigurationFrame.cBatiOrientation.setSelected(AgentSpecifications.BUILDING_ORIENTATION);
        // altitude
        AgentConfigurationFrame.tBatiAltitudeImp.setText("" + AgentSpecifications.BUILDING_ALTITUDE_IMP);
        AgentConfigurationFrame.cBatiAltitude.setSelected(AgentSpecifications.BUILDING_ALTITUDE);
        // occ sol
        AgentConfigurationFrame.tBatiOccSolImp.setText("" + AgentSpecifications.BUILDING_LANDUSE_IMP);
        AgentConfigurationFrame.cBatiOccSol.setSelected(AgentSpecifications.BUILDING_LANDUSE);
        // proximite
        AgentConfigurationFrame.tBatiProximiteImp.setText("" + AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP);
        AgentConfigurationFrame.cBatiProximite.setSelected(AgentSpecifications.BLOCK_BUILDING_PROXIMITY);
        // densite ilot
        AgentConfigurationFrame.tBatiDensiteIlotImp.setText("" + AgentSpecifications.BLOCK_BUILDING_DENSITY_IMP);
        AgentConfigurationFrame.cBatiDensiteIlot.setSelected(AgentSpecifications.BLOCK_BUILDING_DENSITY);

        // routier

        // empatement
        AgentConfigurationFrame.tRoutierEmpatementImp.setText("" + AgentSpecifications.ROAD_COALESCENCE_IMP);
        AgentConfigurationFrame.cRoutierEmpatement.setSelected(AgentSpecifications.ROAD_COALESCENCE);
        AgentConfigurationFrame.tRoutierCoeffPropagationEmpatement
                .setText("" + GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT);
        // impasses
        AgentConfigurationFrame.tRoutierImpassesImp.setText("" + AgentSpecifications.DEAD_END_ROADS_IMP);
        AgentConfigurationFrame.cRoutierImpasses.setSelected(AgentSpecifications.DEAD_END_ROADS);
        // densite
        AgentConfigurationFrame.tRoutierDensiteImp.setText("" + AgentSpecifications.ROAD_DENSITY_IMP);
        AgentConfigurationFrame.cRoutierDensite.setSelected(AgentSpecifications.ROAD_DENSITY);

        // controile deformation des troncons
        AgentConfigurationFrame.tRoutierControleDeformationImp
                .setText("" + AgentSpecifications.ROAD_CONTROL_DISTORTION_IMP);
        AgentConfigurationFrame.cRoutierControleDeformation.setSelected(AgentSpecifications.ROAD_CONTROL_DISTORTION);

        // impasses
        AgentConfigurationFrame.tRoutierImpassesImp.setText("" + AgentSpecifications.DEAD_END_ROADS_IMP);
        AgentConfigurationFrame.cRoutierImpasses.setSelected(AgentSpecifications.DEAD_END_ROADS);
        // densite
        AgentConfigurationFrame.tRoutierDensiteImp.setText("" + AgentSpecifications.ROAD_DENSITY_IMP);
        AgentConfigurationFrame.cRoutierDensite.setSelected(AgentSpecifications.ROAD_DENSITY);

        // hydro

        // proximite routier
        AgentConfigurationFrame.tHydroProximiteRoutierImp.setText("" + AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP);
        AgentConfigurationFrame.cHydroProximiteRoutier.setSelected(AgentSpecifications.RIVER_ROAD_PROXIMITY);
        // ecoulement
        AgentConfigurationFrame.tHydroEcoulementImp.setText("" + AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP);
        AgentConfigurationFrame.cHydroProximiteRoutier.setSelected(AgentSpecifications.RIVER_ROAD_PROXIMITY);
        // platitude lac
        AgentConfigurationFrame.tHydroPlatitudeLacImp.setText("" + AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP);
        AgentConfigurationFrame.cHydroPlatitudeLac.setSelected(AgentSpecifications.LAKE_FLATNESS_PRESERVATION);

    }

    public void validateValues() {

        // bati

        // taille
        AgentSpecifications.BUILDING_SIZE_CONSTRAINT = AgentConfigurationFrame.cBatiTaille.isSelected();
        AgentSpecifications.BUILDING_SIZE_CONSTRAINT_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiTailleImp.getText());
        GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT = Double
                .parseDouble(AgentConfigurationFrame.tBatiTailleAireMini.getText());
        GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT = Double
                .parseDouble(AgentConfigurationFrame.tBatiTailleSeuilSuppression.getText());
        // granularite
        AgentSpecifications.BUILDING_GRANULARITY = AgentConfigurationFrame.cBatiGranularite.isSelected();
        AgentSpecifications.BULDING_GRANULARITY_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiGranulariteImp.getText());
        GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE = Double
                .parseDouble(AgentConfigurationFrame.tBatiGranulariteLongueurMini.getText());
        // equarrite
        AgentSpecifications.BUILDING_SQUARENESS_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiEquarriteImp.getText());
        AgentSpecifications.BUILDING_SQUARENESS = AgentConfigurationFrame.cBatiEquarrite.isSelected();
        // largeur locale
        AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiLargeurLocaleImp.getText());
        AgentSpecifications.BUILDING_LOCAL_WIDTH = AgentConfigurationFrame.cBatiLargeurLocale.isSelected();
        // convexite
        AgentSpecifications.BUILDING_CONVEXITY_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiConvexiteImp.getText());
        AgentSpecifications.BUILDING_CONVEXITY = AgentConfigurationFrame.cBatiConvexite.isSelected();
        // elongation
        AgentSpecifications.BUILDING_ELONGATION_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiElongationImp.getText());
        AgentSpecifications.BUILDING_ELONGATION = AgentConfigurationFrame.cBatiElongation.isSelected();
        // orientation
        AgentSpecifications.BUILDING_ORIENTATION_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiOrientationImp.getText());
        AgentSpecifications.BUILDING_ORIENTATION = AgentConfigurationFrame.cBatiOrientation.isSelected();
        // altitude
        AgentSpecifications.BUILDING_ALTITUDE_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiAltitudeImp.getText());
        AgentSpecifications.BUILDING_ALTITUDE = AgentConfigurationFrame.cBatiAltitude.isSelected();
        // occupation du sol
        AgentSpecifications.BUILDING_LANDUSE_IMP = Double.parseDouble(AgentConfigurationFrame.tBatiOccSolImp.getText());
        AgentSpecifications.BUILDING_LANDUSE = AgentConfigurationFrame.cBatiOccSol.isSelected();
        // proximite
        AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiProximiteImp.getText());
        AgentSpecifications.BLOCK_BUILDING_PROXIMITY = AgentConfigurationFrame.cBatiProximite.isSelected();
        // densite ilot
        AgentSpecifications.BLOCK_BUILDING_DENSITY_IMP = Double
                .parseDouble(AgentConfigurationFrame.tBatiDensiteIlotImp.getText());
        AgentSpecifications.BLOCK_BUILDING_DENSITY = AgentConfigurationFrame.cBatiDensiteIlot.isSelected();

        // routier

        // empatement
        AgentSpecifications.ROAD_COALESCENCE_IMP = Double
                .parseDouble(AgentConfigurationFrame.tRoutierEmpatementImp.getText());
        AgentSpecifications.ROAD_COALESCENCE = AgentConfigurationFrame.cRoutierEmpatement.isSelected();
        GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT = Double
                .parseDouble(AgentConfigurationFrame.tRoutierCoeffPropagationEmpatement.getText());
        // controle deformation des troncons
        AgentSpecifications.ROAD_CONTROL_DISTORTION_IMP = Double
                .parseDouble(AgentConfigurationFrame.tRoutierControleDeformationImp.getText());
        AgentSpecifications.ROAD_CONTROL_DISTORTION = AgentConfigurationFrame.cRoutierControleDeformation.isSelected();
        // presence impasses
        AgentSpecifications.DEAD_END_ROADS_IMP = Double
                .parseDouble(AgentConfigurationFrame.tRoutierImpassesImp.getText());
        AgentSpecifications.DEAD_END_ROADS = AgentConfigurationFrame.cRoutierImpasses.isSelected();
        // densite
        AgentSpecifications.ROAD_DENSITY_IMP = Double.parseDouble(AgentConfigurationFrame.tRoutierDensiteImp.getText());
        AgentSpecifications.ROAD_DENSITY = AgentConfigurationFrame.cRoutierDensite.isSelected();

        // hydro

        // proximite routier
        AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP = Double
                .parseDouble(AgentConfigurationFrame.tHydroProximiteRoutierImp.getText());
        AgentSpecifications.RIVER_ROAD_PROXIMITY = AgentConfigurationFrame.cHydroProximiteRoutier.isSelected();
        // ecoulement
        AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP = Double
                .parseDouble(AgentConfigurationFrame.tHydroEcoulementImp.getText());
        AgentSpecifications.RIVER_FLOW_PRESERVATION = AgentConfigurationFrame.cHydroEcoulement.isSelected();
        // platitude lac
        AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP = Double
                .parseDouble(AgentConfigurationFrame.tHydroPlatitudeLacImp.getText());
        AgentSpecifications.LAKE_FLATNESS_PRESERVATION = AgentConfigurationFrame.cHydroPlatitudeLac.isSelected();

    }

}
