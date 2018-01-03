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
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;

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
  public final static JCheckBox cBatiGranularite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiGranulariteImp = new JTextField(
      "" + AgentSpecifications.BULDING_GRANULARITY_IMP, 5);
  public final static JTextField tBatiGranulariteLongueurMini = new JTextField(
      "" + GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE, 5);

  // equarrite
  public final static JPanel pBatiEquarrite = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiEquarrite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiEquarriteImp = new JTextField(
      "" + AgentSpecifications.BUILDING_SQUARENESS_IMP, 5);

  // largeur locale
  public final static JPanel pBatiLargeurLocale = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cBatiLargeurLocale = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiLargeurLocaleImp = new JTextField(
      "" + AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP, 5);

  // convexite
  public final static JPanel pBatiConvexite = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiConvexite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiConvexiteImp = new JTextField(
      "" + AgentSpecifications.BUILDING_CONVEXITY_IMP, 5);

  // elongation
  public final static JPanel pBatiElongation = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiElongation = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiElongationImp = new JTextField(
      "" + AgentSpecifications.BUILDING_ELONGATION_IMP, 5);

  // orientation
  public final static JPanel pBatiOrientation = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiOrientation = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiOrientationImp = new JTextField(
      "" + AgentSpecifications.BUILDING_ORIENTATION_IMP, 5);

  // altitude
  public final static JPanel pBatiAltitude = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiAltitude = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiAltitudeImp = new JTextField(
      "" + AgentSpecifications.BUILDING_ALTITUDE_IMP, 5);

  // occ sol
  public final static JPanel pBatiOccSol = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiOccSol = new JCheckBox("Constrain", false);
  public final static JTextField tBatiOccSolImp = new JTextField(
      "" + AgentSpecifications.BUILDING_LANDUSE_IMP, 5);

  // proximite
  public final static JPanel pBatiProximite = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiProximite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiProximiteImp = new JTextField(
      "" + AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP, 5);

  // densite ilot
  public final static JPanel pBatiDensiteIlot = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiDensiteIlot = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiDensiteIlotImp = new JTextField(
      "" + AgentSpecifications.BLOCK_BUILDING_DENSITY_IMP, 5);

  // routier
  public final static JPanel pRoutier = new JPanel(new GridBagLayout());

  // empatement
  public final static JPanel pRoutierEmpatement = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cRoutierEmpatement = new JCheckBox("Constrain",
      false);
  public final static JTextField tRoutierEmpatementImp = new JTextField(
      "" + AgentSpecifications.ROAD_COALESCENCE_IMP, 5);
  public final static JTextField tRoutierCoeffPropagationEmpatement = new JTextField(
      "" + GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT,
      5);

  // controle deformation des troncons
  public final static JPanel pRoutierControleDeformation = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cRoutierControleDeformation = new JCheckBox(
      "Constrain", false);
  public final static JTextField tRoutierControleDeformationImp = new JTextField(
      "" + AgentSpecifications.ROAD_CONTROL_DISTORTION_IMP, 5);

  // impasses
  public final static JPanel pRoutierImpasses = new JPanel(new GridBagLayout());
  public final static JCheckBox cRoutierImpasses = new JCheckBox("Constrain",
      false);
  public final static JTextField tRoutierImpassesImp = new JTextField(
      "" + AgentSpecifications.DEAD_END_ROADS_IMP, 5);

  // densite
  public final static JPanel pRoutierDensite = new JPanel(new GridBagLayout());
  public final static JCheckBox cRoutierDensite = new JCheckBox("Constrain",
      false);
  public final static JTextField tRoutierDensiteImp = new JTextField(
      "" + AgentSpecifications.ROAD_DENSITY_IMP, 5);

  // hydrographie
  public final static JPanel pHydro = new JPanel(new GridBagLayout());

  // proximite routier
  public final static JPanel pHydroProximiteRoutier = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cHydroProximiteRoutier = new JCheckBox(
      "Constrain", false);
  public final static JTextField tHydroProximiteRoutierImp = new JTextField(
      "" + AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP, 5);

  // ecoulement
  public final static JPanel pHydroEcoulement = new JPanel(new GridBagLayout());
  public final static JCheckBox cHydroEcoulement = new JCheckBox("Constrain",
      false);
  public final static JTextField tHydroEcoulementImp = new JTextField(
      "" + AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP, 5);

  // platitude lac
  public final static JPanel pHydroPlatitudeLac = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cHydroPlatitudeLac = new JCheckBox("Constrain",
      false);
  public final static JTextField tHydroPlatitudeLacImp = new JTextField(
      "" + AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP, 5);

  // champ relief
  public final static JPanel pRelief = new JPanel(new GridBagLayout());

  // position points
  public final static JPanel pReliefPositionPoint = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cReliefPositionPoint = new JCheckBox(
      "Constrain", false);
  public final static JTextField tReliefPositionPointImp = new JTextField("",
      5);

  // occ sol
  public final static JPanel pOccSol = new JPanel(new GridBagLayout());

  // position points
  public final static JPanel pOccSolPositionPoint = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cOccSolPositionPoint = new JCheckBox(
      "Constrain", false);
  public final static JTextField tOccSolPositionPointImp = new JTextField("",
      5);

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

    // taille
    GAELConfigurationFrame.pBatiTaille.add(GAELConfigurationFrame.cBatiTaille,
        cont);
    GAELConfigurationFrame.pBatiTaille.add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pBatiTaille
        .add(GAELConfigurationFrame.tBatiTailleImp, cont);
    GAELConfigurationFrame.pBatiTaille.add(new JLabel("Minimal area"), cont);
    GAELConfigurationFrame.pBatiTaille
        .add(GAELConfigurationFrame.tBatiTailleAireMini, cont);
    GAELConfigurationFrame.pBatiTaille.add(new JLabel("Suppression area"),
        cont);
    GAELConfigurationFrame.pBatiTaille
        .add(GAELConfigurationFrame.tBatiTailleSeuilSuppression, cont);

    GAELConfigurationFrame.pBatiTaille
        .setBorder(BorderFactory.createTitledBorder("Size"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiTaille, c);

    // granularite
    GAELConfigurationFrame.pBatiGranularite
        .add(GAELConfigurationFrame.cBatiGranularite, cont);
    GAELConfigurationFrame.pBatiGranularite.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pBatiGranularite
        .add(GAELConfigurationFrame.tBatiGranulariteImp, cont);
    GAELConfigurationFrame.pBatiGranularite.add(new JLabel("Minimal length"),
        cont);
    GAELConfigurationFrame.pBatiGranularite
        .add(GAELConfigurationFrame.tBatiGranulariteLongueurMini, cont);

    GAELConfigurationFrame.pBatiGranularite
        .setBorder(BorderFactory.createTitledBorder("Granularity"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiGranularite,
        c);

    // equarrite
    GAELConfigurationFrame.pBatiEquarrite
        .add(GAELConfigurationFrame.cBatiEquarrite, cont);
    GAELConfigurationFrame.pBatiEquarrite.add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pBatiEquarrite
        .add(GAELConfigurationFrame.tBatiEquarriteImp, cont);

    GAELConfigurationFrame.pBatiEquarrite
        .setBorder(BorderFactory.createTitledBorder("Squareness"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiEquarrite,
        c);

    // largeur locale
    GAELConfigurationFrame.pBatiLargeurLocale
        .add(GAELConfigurationFrame.cBatiLargeurLocale, cont);
    GAELConfigurationFrame.pBatiLargeurLocale.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pBatiLargeurLocale
        .add(GAELConfigurationFrame.tBatiLargeurLocaleImp, cont);

    GAELConfigurationFrame.pBatiLargeurLocale
        .setBorder(BorderFactory.createTitledBorder("Local width"));
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati
        .add(GAELConfigurationFrame.pBatiLargeurLocale, c);

    // convexite
    GAELConfigurationFrame.pBatiConvexite
        .add(GAELConfigurationFrame.cBatiConvexite, cont);
    GAELConfigurationFrame.pBatiConvexite.add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pBatiConvexite
        .add(GAELConfigurationFrame.tBatiConvexiteImp, cont);

    GAELConfigurationFrame.pBatiConvexite
        .setBorder(BorderFactory.createTitledBorder("Convexity"));
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiConvexite,
        c);

    // elongation
    GAELConfigurationFrame.pBatiElongation
        .add(GAELConfigurationFrame.cBatiElongation, cont);
    GAELConfigurationFrame.pBatiElongation.add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pBatiElongation
        .add(GAELConfigurationFrame.tBatiElongationImp, cont);

    GAELConfigurationFrame.pBatiElongation
        .setBorder(BorderFactory.createTitledBorder("Elongation"));
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiElongation,
        c);

    // orientation
    GAELConfigurationFrame.pBatiOrientation
        .add(GAELConfigurationFrame.cBatiOrientation, cont);
    GAELConfigurationFrame.pBatiOrientation.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pBatiOrientation
        .add(GAELConfigurationFrame.tBatiOrientationImp, cont);

    GAELConfigurationFrame.pBatiOrientation
        .setBorder(BorderFactory.createTitledBorder("Orientation"));
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiOrientation,
        c);

    // altitude
    GAELConfigurationFrame.pBatiAltitude
        .add(GAELConfigurationFrame.cBatiAltitude, cont);
    GAELConfigurationFrame.pBatiAltitude.add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pBatiAltitude
        .add(GAELConfigurationFrame.tBatiAltitudeImp, cont);

    GAELConfigurationFrame.pBatiAltitude
        .setBorder(BorderFactory.createTitledBorder("Altitude"));
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiAltitude, c);

    // proximite
    GAELConfigurationFrame.pBatiProximite
        .add(GAELConfigurationFrame.cBatiProximite, cont);
    GAELConfigurationFrame.pBatiProximite.add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pBatiProximite
        .add(GAELConfigurationFrame.tBatiProximiteImp, cont);

    GAELConfigurationFrame.pBatiProximite
        .setBorder(BorderFactory.createTitledBorder("Proximity"));
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiProximite,
        c);

    // densite ilot
    GAELConfigurationFrame.pBatiDensiteIlot
        .add(GAELConfigurationFrame.cBatiDensiteIlot, cont);
    GAELConfigurationFrame.pBatiDensiteIlot.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pBatiDensiteIlot
        .add(GAELConfigurationFrame.tBatiDensiteIlotImp, cont);

    GAELConfigurationFrame.pBatiDensiteIlot
        .setBorder(BorderFactory.createTitledBorder("Block density"));
    c = new GridBagConstraints();
    c.gridx = 3;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pBati.add(GAELConfigurationFrame.pBatiDensiteIlot,
        c);

    GAELConfigurationFrame.panneauOnglets.addTab("Bâti",
        new ImageIcon(GAELConfigurationFrame.class
            .getResource("/images/bati.gif").getPath().replaceAll("%20", " ")),
        GAELConfigurationFrame.pBati, "Configure constraints on urban data");

    // routier

    // empatement
    GAELConfigurationFrame.pRoutierEmpatement
        .add(GAELConfigurationFrame.cRoutierEmpatement, cont);
    GAELConfigurationFrame.pRoutierEmpatement.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pRoutierEmpatement
        .add(GAELConfigurationFrame.tRoutierEmpatementImp, cont);
    GAELConfigurationFrame.pRoutierEmpatement
        .add(new JLabel("Propagation coeff"), cont);
    GAELConfigurationFrame.pRoutierEmpatement
        .add(GAELConfigurationFrame.tRoutierCoeffPropagationEmpatement, cont);

    GAELConfigurationFrame.pRoutierEmpatement
        .setBorder(BorderFactory.createTitledBorder("Coalescence"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pRoutier
        .add(GAELConfigurationFrame.pRoutierEmpatement, c);

    // controle deformation des troncons
    GAELConfigurationFrame.pRoutierControleDeformation
        .add(GAELConfigurationFrame.cRoutierControleDeformation, cont);
    GAELConfigurationFrame.pRoutierControleDeformation
        .add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pRoutierControleDeformation
        .add(GAELConfigurationFrame.tRoutierControleDeformationImp, cont);

    GAELConfigurationFrame.pRoutierControleDeformation
        .setBorder(BorderFactory.createTitledBorder("Deformation control"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pRoutier
        .add(GAELConfigurationFrame.pRoutierControleDeformation, c);

    // impasses
    GAELConfigurationFrame.pRoutierImpasses
        .add(GAELConfigurationFrame.cRoutierImpasses, cont);
    GAELConfigurationFrame.pRoutierImpasses.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pRoutierImpasses
        .add(GAELConfigurationFrame.tRoutierImpassesImp, cont);

    GAELConfigurationFrame.pRoutierImpasses
        .setBorder(BorderFactory.createTitledBorder("Dead ends presence"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pRoutier
        .add(GAELConfigurationFrame.pRoutierImpasses, c);

    // densite
    GAELConfigurationFrame.pRoutierDensite
        .add(GAELConfigurationFrame.cRoutierDensite, cont);
    GAELConfigurationFrame.pRoutierDensite.add(new JLabel("Importance"), cont);
    GAELConfigurationFrame.pRoutierDensite
        .add(GAELConfigurationFrame.tRoutierDensiteImp, cont);

    GAELConfigurationFrame.pRoutierDensite
        .setBorder(BorderFactory.createTitledBorder("Density"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pRoutier
        .add(GAELConfigurationFrame.pRoutierDensite, c);

    GAELConfigurationFrame.panneauOnglets.addTab("Routier",
        new ImageIcon(
            GAELConfigurationFrame.class.getResource("/images/routier.gif")
                .getPath().replaceAll("%20", " ")),
        GAELConfigurationFrame.pRoutier,
        "Configure constraints on road network");

    // hydro

    // proximite routier
    GAELConfigurationFrame.pHydroProximiteRoutier
        .add(GAELConfigurationFrame.cHydroProximiteRoutier, cont);
    GAELConfigurationFrame.pHydroProximiteRoutier.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pHydroProximiteRoutier
        .add(GAELConfigurationFrame.tHydroProximiteRoutierImp, cont);

    GAELConfigurationFrame.pHydroProximiteRoutier.setBorder(
        BorderFactory.createTitledBorder("Proximity of road network"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pHydro
        .add(GAELConfigurationFrame.pHydroProximiteRoutier, c);

    // ecoulement
    GAELConfigurationFrame.pHydroEcoulement
        .add(GAELConfigurationFrame.cHydroEcoulement, cont);
    GAELConfigurationFrame.pHydroEcoulement.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pHydroEcoulement
        .add(GAELConfigurationFrame.tHydroEcoulementImp, cont);

    GAELConfigurationFrame.pHydroEcoulement
        .setBorder(BorderFactory.createTitledBorder("Flow"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pHydro.add(GAELConfigurationFrame.pHydroEcoulement,
        c);

    // platitude lac
    GAELConfigurationFrame.pHydroPlatitudeLac
        .add(GAELConfigurationFrame.cHydroPlatitudeLac, cont);
    GAELConfigurationFrame.pHydroPlatitudeLac.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pHydroPlatitudeLac
        .add(GAELConfigurationFrame.tHydroPlatitudeLacImp, cont);

    GAELConfigurationFrame.pHydroPlatitudeLac
        .setBorder(BorderFactory.createTitledBorder("Lake flatness"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pHydro
        .add(GAELConfigurationFrame.pHydroPlatitudeLac, c);

    GAELConfigurationFrame.panneauOnglets.addTab("Hydro",
        new ImageIcon(GAELConfigurationFrame.class
            .getResource("/images/hydro.gif").getPath().replaceAll("%20", " ")),
        GAELConfigurationFrame.pHydro,
        "Configure constraints on hydro network");

    // champ relief

    // postion points
    GAELConfigurationFrame.pReliefPositionPoint
        .add(GAELConfigurationFrame.cReliefPositionPoint, cont);
    GAELConfigurationFrame.pReliefPositionPoint.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pReliefPositionPoint
        .add(GAELConfigurationFrame.tReliefPositionPointImp, cont);

    GAELConfigurationFrame.pReliefPositionPoint
        .setBorder(BorderFactory.createTitledBorder("Points position"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pRelief
        .add(GAELConfigurationFrame.pReliefPositionPoint, c);

    GAELConfigurationFrame.panneauOnglets.addTab("Relief",
        new ImageIcon(
            GAELConfigurationFrame.class.getResource("/images/relief.gif")
                .getPath().replaceAll("%20", " ")),
        GAELConfigurationFrame.pRelief, "Configure constraints on relief");

    // champ occ sol

    // postion points
    GAELConfigurationFrame.pOccSolPositionPoint
        .add(GAELConfigurationFrame.cOccSolPositionPoint, cont);
    GAELConfigurationFrame.pOccSolPositionPoint.add(new JLabel("Importance"),
        cont);
    GAELConfigurationFrame.pOccSolPositionPoint
        .add(GAELConfigurationFrame.tOccSolPositionPointImp, cont);

    GAELConfigurationFrame.pOccSolPositionPoint
        .setBorder(BorderFactory.createTitledBorder("Points position"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    GAELConfigurationFrame.pOccSol
        .add(GAELConfigurationFrame.pOccSolPositionPoint, c);

    GAELConfigurationFrame.panneauOnglets.addTab("Occ. sol",
        new ImageIcon(
            GAELConfigurationFrame.class.getResource("/images/occsol.gif")
                .getPath().replaceAll("%20", " ")),
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
    GAELConfigurationFrame.panneauBoutons.add(GAELConfigurationFrame.bValider,
        c);

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
    GAELConfigurationFrame.panneauBoutons.add(GAELConfigurationFrame.bAnnuler,
        c);

    // bouton enregistrer
    GAELConfigurationFrame.bEnregistrer
        .setPreferredSize(new Dimension(110, 30));
    GAELConfigurationFrame.bEnregistrer
        .addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            JFileChooser parcourir = new JFileChooser(new File(CartAGenPlugin
                .getInstance().getCheminFichierConfigurationGene()));
            parcourir.setDialogType(JFileChooser.SAVE_DIALOG);
            parcourir.setApproveButtonText("Save");
            parcourir.setDialogTitle("Save");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "XML MiraGe", "xml");
            parcourir.setFileFilter(filter);
            parcourir.setSelectedFile(new File(GAELConfigurationFrame
                .getInstance().getCheminFichierConfigurationAgent()));
            int res = parcourir
                .showOpenDialog(GAELConfigurationFrame.getInstance());
            if (res == JFileChooser.APPROVE_OPTION) {
              GeneralisationSpecifications
                  .saveToFile(parcourir.getSelectedFile());
            } else if (res == JFileChooser.ERROR_OPTION) {
              JOptionPane.showMessageDialog(
                  GAELConfigurationFrame.getInstance(), "Error", "Error",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        });
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    GAELConfigurationFrame.panneauBoutons
        .add(GAELConfigurationFrame.bEnregistrer, c);

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

    // taille
    GAELConfigurationFrame.cBatiTaille
        .setSelected(AgentSpecifications.BUILDING_SIZE_CONSTRAINT);
    GAELConfigurationFrame.tBatiTailleImp
        .setText("" + AgentSpecifications.BUILDING_SIZE_CONSTRAINT_IMP);
    GAELConfigurationFrame.tBatiTailleAireMini
        .setText("" + GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT);
    GAELConfigurationFrame.tBatiTailleSeuilSuppression.setText(
        "" + GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT);
    // granularite
    GAELConfigurationFrame.cBatiGranularite
        .setSelected(AgentSpecifications.BUILDING_GRANULARITY);
    GAELConfigurationFrame.tBatiGranulariteImp
        .setText("" + AgentSpecifications.BULDING_GRANULARITY_IMP);
    GAELConfigurationFrame.tBatiGranulariteLongueurMini
        .setText("" + GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE);
    // equaritte
    GAELConfigurationFrame.tBatiEquarriteImp
        .setText("" + AgentSpecifications.BUILDING_SQUARENESS_IMP);
    GAELConfigurationFrame.cBatiEquarrite
        .setSelected(AgentSpecifications.BUILDING_SQUARENESS);
    // largeur locale
    GAELConfigurationFrame.tBatiLargeurLocaleImp
        .setText("" + AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP);
    GAELConfigurationFrame.cBatiLargeurLocale
        .setSelected(AgentSpecifications.BUILDING_LOCAL_WIDTH);
    // convexite
    GAELConfigurationFrame.tBatiConvexiteImp
        .setText("" + AgentSpecifications.BUILDING_CONVEXITY_IMP);
    GAELConfigurationFrame.cBatiConvexite
        .setSelected(AgentSpecifications.BUILDING_CONVEXITY);
    // elongation
    GAELConfigurationFrame.tBatiElongationImp
        .setText("" + AgentSpecifications.BUILDING_ELONGATION_IMP);
    GAELConfigurationFrame.cBatiElongation
        .setSelected(AgentSpecifications.BUILDING_ELONGATION);
    // orientation
    GAELConfigurationFrame.tBatiOrientationImp
        .setText("" + AgentSpecifications.BUILDING_ORIENTATION_IMP);
    GAELConfigurationFrame.cBatiOrientation
        .setSelected(AgentSpecifications.BUILDING_ORIENTATION);
    // altitude
    GAELConfigurationFrame.tBatiAltitudeImp
        .setText("" + AgentSpecifications.BUILDING_ALTITUDE_IMP);
    GAELConfigurationFrame.cBatiAltitude
        .setSelected(AgentSpecifications.BUILDING_ALTITUDE);
    // occ sol
    GAELConfigurationFrame.tBatiOccSolImp
        .setText("" + AgentSpecifications.BUILDING_LANDUSE_IMP);
    GAELConfigurationFrame.cBatiOccSol
        .setSelected(AgentSpecifications.BUILDING_LANDUSE);
    // proximite
    GAELConfigurationFrame.tBatiProximiteImp
        .setText("" + AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP);
    GAELConfigurationFrame.cBatiProximite
        .setSelected(AgentSpecifications.BLOCK_BUILDING_PROXIMITY);
    // densite ilot
    GAELConfigurationFrame.tBatiDensiteIlotImp
        .setText("" + AgentSpecifications.BLOCK_BUILDING_DENSITY_IMP);
    GAELConfigurationFrame.cBatiDensiteIlot
        .setSelected(AgentSpecifications.BLOCK_BUILDING_DENSITY);

    // routier

    // empatement
    GAELConfigurationFrame.tRoutierEmpatementImp
        .setText("" + AgentSpecifications.ROAD_COALESCENCE_IMP);
    GAELConfigurationFrame.cRoutierEmpatement
        .setSelected(AgentSpecifications.ROAD_COALESCENCE);
    GAELConfigurationFrame.tRoutierCoeffPropagationEmpatement.setText(
        "" + GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT);
    // impasses
    GAELConfigurationFrame.tRoutierImpassesImp
        .setText("" + AgentSpecifications.DEAD_END_ROADS_IMP);
    GAELConfigurationFrame.cRoutierImpasses
        .setSelected(AgentSpecifications.DEAD_END_ROADS);
    // densite
    GAELConfigurationFrame.tRoutierDensiteImp
        .setText("" + AgentSpecifications.ROAD_DENSITY_IMP);
    GAELConfigurationFrame.cRoutierDensite
        .setSelected(AgentSpecifications.ROAD_DENSITY);

    // controile deformation des troncons
    GAELConfigurationFrame.tRoutierControleDeformationImp
        .setText("" + AgentSpecifications.ROAD_CONTROL_DISTORTION_IMP);
    GAELConfigurationFrame.cRoutierControleDeformation
        .setSelected(AgentSpecifications.ROAD_CONTROL_DISTORTION);

    // impasses
    GAELConfigurationFrame.tRoutierImpassesImp
        .setText("" + AgentSpecifications.DEAD_END_ROADS_IMP);
    GAELConfigurationFrame.cRoutierImpasses
        .setSelected(AgentSpecifications.DEAD_END_ROADS);
    // densite
    GAELConfigurationFrame.tRoutierDensiteImp
        .setText("" + AgentSpecifications.ROAD_DENSITY_IMP);
    GAELConfigurationFrame.cRoutierDensite
        .setSelected(AgentSpecifications.ROAD_DENSITY);

    // hydro

    // proximite routier
    GAELConfigurationFrame.tHydroProximiteRoutierImp
        .setText("" + AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP);
    GAELConfigurationFrame.cHydroProximiteRoutier
        .setSelected(AgentSpecifications.RIVER_ROAD_PROXIMITY);
    // ecoulement
    GAELConfigurationFrame.tHydroEcoulementImp
        .setText("" + AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP);
    GAELConfigurationFrame.cHydroProximiteRoutier
        .setSelected(AgentSpecifications.RIVER_ROAD_PROXIMITY);
    // platitude lac
    GAELConfigurationFrame.tHydroPlatitudeLacImp
        .setText("" + AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP);
    GAELConfigurationFrame.cHydroPlatitudeLac
        .setSelected(AgentSpecifications.LAKE_FLATNESS_PRESERVATION);

  }

  public void validateValues() {

    // bati

    // taille
    AgentSpecifications.BUILDING_SIZE_CONSTRAINT = GAELConfigurationFrame.cBatiTaille
        .isSelected();
    AgentSpecifications.BUILDING_SIZE_CONSTRAINT_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiTailleImp.getText());
    GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT = Double
        .parseDouble(GAELConfigurationFrame.tBatiTailleAireMini.getText());
    GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT = Double
        .parseDouble(
            GAELConfigurationFrame.tBatiTailleSeuilSuppression.getText());
    // granularite
    AgentSpecifications.BUILDING_GRANULARITY = GAELConfigurationFrame.cBatiGranularite
        .isSelected();
    AgentSpecifications.BULDING_GRANULARITY_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiGranulariteImp.getText());
    GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE = Double.parseDouble(
        GAELConfigurationFrame.tBatiGranulariteLongueurMini.getText());
    // equarrite
    AgentSpecifications.BUILDING_SQUARENESS_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiEquarriteImp.getText());
    AgentSpecifications.BUILDING_SQUARENESS = GAELConfigurationFrame.cBatiEquarrite
        .isSelected();
    // largeur locale
    AgentSpecifications.BUILDING_LOCAL_WIDTH_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiLargeurLocaleImp.getText());
    AgentSpecifications.BUILDING_LOCAL_WIDTH = GAELConfigurationFrame.cBatiLargeurLocale
        .isSelected();
    // convexite
    AgentSpecifications.BUILDING_CONVEXITY_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiConvexiteImp.getText());
    AgentSpecifications.BUILDING_CONVEXITY = GAELConfigurationFrame.cBatiConvexite
        .isSelected();
    // elongation
    AgentSpecifications.BUILDING_ELONGATION_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiElongationImp.getText());
    AgentSpecifications.BUILDING_ELONGATION = GAELConfigurationFrame.cBatiElongation
        .isSelected();
    // orientation
    AgentSpecifications.BUILDING_ORIENTATION_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiOrientationImp.getText());
    AgentSpecifications.BUILDING_ORIENTATION = GAELConfigurationFrame.cBatiOrientation
        .isSelected();
    // altitude
    AgentSpecifications.BUILDING_ALTITUDE_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiAltitudeImp.getText());
    AgentSpecifications.BUILDING_ALTITUDE = GAELConfigurationFrame.cBatiAltitude
        .isSelected();
    // occupation du sol
    AgentSpecifications.BUILDING_LANDUSE_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiOccSolImp.getText());
    AgentSpecifications.BUILDING_LANDUSE = GAELConfigurationFrame.cBatiOccSol
        .isSelected();
    // proximite
    AgentSpecifications.BLOCK_BUILDING_PROXIMITY_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiProximiteImp.getText());
    AgentSpecifications.BLOCK_BUILDING_PROXIMITY = GAELConfigurationFrame.cBatiProximite
        .isSelected();
    // densite ilot
    AgentSpecifications.BLOCK_BUILDING_DENSITY_IMP = Double
        .parseDouble(GAELConfigurationFrame.tBatiDensiteIlotImp.getText());
    AgentSpecifications.BLOCK_BUILDING_DENSITY = GAELConfigurationFrame.cBatiDensiteIlot
        .isSelected();

    // routier

    // empatement
    AgentSpecifications.ROAD_COALESCENCE_IMP = Double
        .parseDouble(GAELConfigurationFrame.tRoutierEmpatementImp.getText());
    AgentSpecifications.ROAD_COALESCENCE = GAELConfigurationFrame.cRoutierEmpatement
        .isSelected();
    GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT = Double
        .parseDouble(GAELConfigurationFrame.tRoutierCoeffPropagationEmpatement
            .getText());
    // controle deformation des troncons
    AgentSpecifications.ROAD_CONTROL_DISTORTION_IMP = Double.parseDouble(
        GAELConfigurationFrame.tRoutierControleDeformationImp.getText());
    AgentSpecifications.ROAD_CONTROL_DISTORTION = GAELConfigurationFrame.cRoutierControleDeformation
        .isSelected();
    // presence impasses
    AgentSpecifications.DEAD_END_ROADS_IMP = Double
        .parseDouble(GAELConfigurationFrame.tRoutierImpassesImp.getText());
    AgentSpecifications.DEAD_END_ROADS = GAELConfigurationFrame.cRoutierImpasses
        .isSelected();
    // densite
    AgentSpecifications.ROAD_DENSITY_IMP = Double
        .parseDouble(GAELConfigurationFrame.tRoutierDensiteImp.getText());
    AgentSpecifications.ROAD_DENSITY = GAELConfigurationFrame.cRoutierDensite
        .isSelected();

    // hydro

    // proximite routier
    AgentSpecifications.RIVER_ROAD_PROXIMITY_IMP = Double.parseDouble(
        GAELConfigurationFrame.tHydroProximiteRoutierImp.getText());
    AgentSpecifications.RIVER_ROAD_PROXIMITY = GAELConfigurationFrame.cHydroProximiteRoutier
        .isSelected();
    // ecoulement
    AgentSpecifications.RIVER_FLOW_PRESERVATION_IMP = Double
        .parseDouble(GAELConfigurationFrame.tHydroEcoulementImp.getText());
    AgentSpecifications.RIVER_FLOW_PRESERVATION = GAELConfigurationFrame.cHydroEcoulement
        .isSelected();
    // platitude lac
    AgentSpecifications.LAKE_FLATNESS_PRESERVATION_IMP = Double
        .parseDouble(GAELConfigurationFrame.tHydroPlatitudeLacImp.getText());
    AgentSpecifications.LAKE_FLATNESS_PRESERVATION = GAELConfigurationFrame.cHydroPlatitudeLac
        .isSelected();

  }

}
