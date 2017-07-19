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

  public static JTabbedPane panneauOnglets = new JTabbedPane();

  // bati
  public static JPanel pBati = new JPanel(new GridBagLayout());

  // taille
  public final static JPanel pBatiTaille = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiTaille = new JCheckBox("Constrain", false);
  public final static JTextField tBatiTailleImp = new JTextField(
      "" + AgentSpecifications.TAILLE_BATIMENT_IMP, 5);
  public final static JTextField tBatiTailleAireMini = new JTextField(
      "" + GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT, 5);
  public final static JTextField tBatiTailleSeuilSuppression = new JTextField(
      "" + GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT, 5);

  // granularite
  public final static JPanel pBatiGranularite = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiGranularite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiGranulariteImp = new JTextField(
      "" + AgentSpecifications.GRANULARITE_BATIMENT_IMP, 5);
  public final static JTextField tBatiGranulariteLongueurMini = new JTextField(
      "" + GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE, 5);

  // equarrite
  public final static JPanel pBatiEquarrite = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiEquarrite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiEquarriteImp = new JTextField(
      "" + AgentSpecifications.EQUARRITE_BATIMENT_IMP, 5);

  // largeur locale
  public final static JPanel pBatiLargeurLocale = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cBatiLargeurLocale = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiLargeurLocaleImp = new JTextField(
      "" + AgentSpecifications.LARGEUR_LOCALE_BATIMENT_IMP, 5);

  // convexite
  public final static JPanel pBatiConvexite = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiConvexite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiConvexiteImp = new JTextField(
      "" + AgentSpecifications.CONVEXITE_BATIMENT_IMP, 5);

  // elongation
  public final static JPanel pBatiElongation = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiElongation = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiElongationImp = new JTextField(
      "" + AgentSpecifications.ELONGATION_BATIMENT_IMP, 5);

  // orientation
  public final static JPanel pBatiOrientation = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiOrientation = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiOrientationImp = new JTextField(
      "" + AgentSpecifications.ORIENTATION_BATIMENT_IMP, 5);

  // altitude
  public final static JPanel pBatiAltitude = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiAltitude = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiAltitudeImp = new JTextField(
      "" + AgentSpecifications.ALTITUDE_BATIMENT_IMP, 5);

  // occ sol
  public final static JPanel pBatiOccSol = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiOccSol = new JCheckBox("Constrain", false);
  public final static JTextField tBatiOccSolImp = new JTextField(
      "" + AgentSpecifications.OCCSOL_BATIMENT_IMP, 5);

  // proximite
  public final static JPanel pBatiProximite = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiProximite = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiProximiteImp = new JTextField(
      "" + AgentSpecifications.PROXIMITE_BATIMENT_IMP, 5);

  // densite ilot
  public final static JPanel pBatiDensiteIlot = new JPanel(new GridBagLayout());
  public final static JCheckBox cBatiDensiteIlot = new JCheckBox("Constrain",
      false);
  public final static JTextField tBatiDensiteIlotImp = new JTextField(
      "" + AgentSpecifications.DENSITE_ILOT_BATIMENT_IMP, 5);

  // routier
  public final static JPanel pRoutier = new JPanel(new GridBagLayout());

  // empatement
  public final static JPanel pRoutierEmpatement = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cRoutierEmpatement = new JCheckBox("Constrain",
      false);
  public final static JTextField tRoutierEmpatementImp = new JTextField(
      "" + AgentSpecifications.ROUTIER_EMPATEMENT_IMP, 5);
  public final static JTextField tRoutierCoeffPropagationEmpatement = new JTextField(
      "" + GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT,
      5);

  // controle deformation des troncons
  public final static JPanel pRoutierControleDeformation = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cRoutierControleDeformation = new JCheckBox(
      "Constrain", false);
  public final static JTextField tRoutierControleDeformationImp = new JTextField(
      "" + AgentSpecifications.ROUTIER_CONTROLE_DEFORMATION_IMP, 5);

  // impasses
  public final static JPanel pRoutierImpasses = new JPanel(new GridBagLayout());
  public final static JCheckBox cRoutierImpasses = new JCheckBox("Constrain",
      false);
  public final static JTextField tRoutierImpassesImp = new JTextField(
      "" + AgentSpecifications.ROUTIER_IMPASSES_IMP, 5);

  // densite
  public final static JPanel pRoutierDensite = new JPanel(new GridBagLayout());
  public final static JCheckBox cRoutierDensite = new JCheckBox("Constrain",
      false);
  public final static JTextField tRoutierDensiteImp = new JTextField(
      "" + AgentSpecifications.ROUTIER_DENSITE_IMP, 5);

  // hydrographie
  public final static JPanel pHydro = new JPanel(new GridBagLayout());

  // proximite routier
  public final static JPanel pHydroProximiteRoutier = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cHydroProximiteRoutier = new JCheckBox(
      "Constrain", false);
  public final static JTextField tHydroProximiteRoutierImp = new JTextField(
      "" + AgentSpecifications.PROXIMITE_HYDRO_ROUTIER_IMP, 5);

  // ecoulement
  public final static JPanel pHydroEcoulement = new JPanel(new GridBagLayout());
  public final static JCheckBox cHydroEcoulement = new JCheckBox("Constrain",
      false);
  public final static JTextField tHydroEcoulementImp = new JTextField(
      "" + AgentSpecifications.ECOULEMENT_HYDRO_IMP, 5);

  // platitude lac
  public final static JPanel pHydroPlatitudeLac = new JPanel(
      new GridBagLayout());
  public final static JCheckBox cHydroPlatitudeLac = new JCheckBox("Constrain",
      false);
  public final static JTextField tHydroPlatitudeLacImp = new JTextField(
      "" + AgentSpecifications.PLATITUDE_LAC_IMP, 5);

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
    AgentConfigurationFrame.pBatiTaille.add(AgentConfigurationFrame.cBatiTaille,
        cont);
    AgentConfigurationFrame.pBatiTaille.add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pBatiTaille
        .add(AgentConfigurationFrame.tBatiTailleImp, cont);
    AgentConfigurationFrame.pBatiTaille.add(new JLabel("Minimal area"), cont);
    AgentConfigurationFrame.pBatiTaille
        .add(AgentConfigurationFrame.tBatiTailleAireMini, cont);
    AgentConfigurationFrame.pBatiTaille.add(new JLabel("Suppression area"),
        cont);
    AgentConfigurationFrame.pBatiTaille
        .add(AgentConfigurationFrame.tBatiTailleSeuilSuppression, cont);

    AgentConfigurationFrame.pBatiTaille
        .setBorder(BorderFactory.createTitledBorder("Size"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiTaille, c);

    // granularite
    AgentConfigurationFrame.pBatiGranularite
        .add(AgentConfigurationFrame.cBatiGranularite, cont);
    AgentConfigurationFrame.pBatiGranularite.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pBatiGranularite
        .add(AgentConfigurationFrame.tBatiGranulariteImp, cont);
    AgentConfigurationFrame.pBatiGranularite.add(new JLabel("Minimal length"),
        cont);
    AgentConfigurationFrame.pBatiGranularite
        .add(AgentConfigurationFrame.tBatiGranulariteLongueurMini, cont);

    AgentConfigurationFrame.pBatiGranularite
        .setBorder(BorderFactory.createTitledBorder("Granularity"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiGranularite,
        c);

    // equarrite
    AgentConfigurationFrame.pBatiEquarrite
        .add(AgentConfigurationFrame.cBatiEquarrite, cont);
    AgentConfigurationFrame.pBatiEquarrite.add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pBatiEquarrite
        .add(AgentConfigurationFrame.tBatiEquarriteImp, cont);

    AgentConfigurationFrame.pBatiEquarrite
        .setBorder(BorderFactory.createTitledBorder("Squareness"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiEquarrite,
        c);

    // largeur locale
    AgentConfigurationFrame.pBatiLargeurLocale
        .add(AgentConfigurationFrame.cBatiLargeurLocale, cont);
    AgentConfigurationFrame.pBatiLargeurLocale.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pBatiLargeurLocale
        .add(AgentConfigurationFrame.tBatiLargeurLocaleImp, cont);

    AgentConfigurationFrame.pBatiLargeurLocale
        .setBorder(BorderFactory.createTitledBorder("Local width"));
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati
        .add(AgentConfigurationFrame.pBatiLargeurLocale, c);

    // convexite
    AgentConfigurationFrame.pBatiConvexite
        .add(AgentConfigurationFrame.cBatiConvexite, cont);
    AgentConfigurationFrame.pBatiConvexite.add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pBatiConvexite
        .add(AgentConfigurationFrame.tBatiConvexiteImp, cont);

    AgentConfigurationFrame.pBatiConvexite
        .setBorder(BorderFactory.createTitledBorder("Convexity"));
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiConvexite,
        c);

    // elongation
    AgentConfigurationFrame.pBatiElongation
        .add(AgentConfigurationFrame.cBatiElongation, cont);
    AgentConfigurationFrame.pBatiElongation.add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pBatiElongation
        .add(AgentConfigurationFrame.tBatiElongationImp, cont);

    AgentConfigurationFrame.pBatiElongation
        .setBorder(BorderFactory.createTitledBorder("Elongation"));
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiElongation,
        c);

    // orientation
    AgentConfigurationFrame.pBatiOrientation
        .add(AgentConfigurationFrame.cBatiOrientation, cont);
    AgentConfigurationFrame.pBatiOrientation.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pBatiOrientation
        .add(AgentConfigurationFrame.tBatiOrientationImp, cont);

    AgentConfigurationFrame.pBatiOrientation
        .setBorder(BorderFactory.createTitledBorder("Orientation"));
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiOrientation,
        c);

    // altitude
    AgentConfigurationFrame.pBatiAltitude
        .add(AgentConfigurationFrame.cBatiAltitude, cont);
    AgentConfigurationFrame.pBatiAltitude.add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pBatiAltitude
        .add(AgentConfigurationFrame.tBatiAltitudeImp, cont);

    AgentConfigurationFrame.pBatiAltitude
        .setBorder(BorderFactory.createTitledBorder("Altitude"));
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiAltitude, c);

    // proximite
    AgentConfigurationFrame.pBatiProximite
        .add(AgentConfigurationFrame.cBatiProximite, cont);
    AgentConfigurationFrame.pBatiProximite.add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pBatiProximite
        .add(AgentConfigurationFrame.tBatiProximiteImp, cont);

    AgentConfigurationFrame.pBatiProximite
        .setBorder(BorderFactory.createTitledBorder("Proximity"));
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiProximite,
        c);

    // densite ilot
    AgentConfigurationFrame.pBatiDensiteIlot
        .add(AgentConfigurationFrame.cBatiDensiteIlot, cont);
    AgentConfigurationFrame.pBatiDensiteIlot.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pBatiDensiteIlot
        .add(AgentConfigurationFrame.tBatiDensiteIlotImp, cont);

    AgentConfigurationFrame.pBatiDensiteIlot
        .setBorder(BorderFactory.createTitledBorder("Block density"));
    c = new GridBagConstraints();
    c.gridx = 3;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pBati.add(AgentConfigurationFrame.pBatiDensiteIlot,
        c);

    AgentConfigurationFrame.panneauOnglets.addTab("Bâti",
        new ImageIcon(AgentConfigurationFrame.class
            .getResource("/images/bati.gif").getPath().replaceAll("%20", " ")),
        AgentConfigurationFrame.pBati, "Configure constraints on urban data");

    // routier

    // empatement
    AgentConfigurationFrame.pRoutierEmpatement
        .add(AgentConfigurationFrame.cRoutierEmpatement, cont);
    AgentConfigurationFrame.pRoutierEmpatement.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pRoutierEmpatement
        .add(AgentConfigurationFrame.tRoutierEmpatementImp, cont);
    AgentConfigurationFrame.pRoutierEmpatement
        .add(new JLabel("Propagation coeff"), cont);
    AgentConfigurationFrame.pRoutierEmpatement
        .add(AgentConfigurationFrame.tRoutierCoeffPropagationEmpatement, cont);

    AgentConfigurationFrame.pRoutierEmpatement
        .setBorder(BorderFactory.createTitledBorder("Coalescence"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pRoutier
        .add(AgentConfigurationFrame.pRoutierEmpatement, c);

    // controle deformation des troncons
    AgentConfigurationFrame.pRoutierControleDeformation
        .add(AgentConfigurationFrame.cRoutierControleDeformation, cont);
    AgentConfigurationFrame.pRoutierControleDeformation
        .add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pRoutierControleDeformation
        .add(AgentConfigurationFrame.tRoutierControleDeformationImp, cont);

    AgentConfigurationFrame.pRoutierControleDeformation
        .setBorder(BorderFactory.createTitledBorder("Deformation control"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pRoutier
        .add(AgentConfigurationFrame.pRoutierControleDeformation, c);

    // impasses
    AgentConfigurationFrame.pRoutierImpasses
        .add(AgentConfigurationFrame.cRoutierImpasses, cont);
    AgentConfigurationFrame.pRoutierImpasses.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pRoutierImpasses
        .add(AgentConfigurationFrame.tRoutierImpassesImp, cont);

    AgentConfigurationFrame.pRoutierImpasses
        .setBorder(BorderFactory.createTitledBorder("Dead ends presence"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pRoutier
        .add(AgentConfigurationFrame.pRoutierImpasses, c);

    // densite
    AgentConfigurationFrame.pRoutierDensite
        .add(AgentConfigurationFrame.cRoutierDensite, cont);
    AgentConfigurationFrame.pRoutierDensite.add(new JLabel("Importance"), cont);
    AgentConfigurationFrame.pRoutierDensite
        .add(AgentConfigurationFrame.tRoutierDensiteImp, cont);

    AgentConfigurationFrame.pRoutierDensite
        .setBorder(BorderFactory.createTitledBorder("Density"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pRoutier
        .add(AgentConfigurationFrame.pRoutierDensite, c);

    AgentConfigurationFrame.panneauOnglets.addTab("Routier",
        new ImageIcon(
            AgentConfigurationFrame.class.getResource("/images/routier.gif")
                .getPath().replaceAll("%20", " ")),
        AgentConfigurationFrame.pRoutier,
        "Configure constraints on road network");

    // hydro

    // proximite routier
    AgentConfigurationFrame.pHydroProximiteRoutier
        .add(AgentConfigurationFrame.cHydroProximiteRoutier, cont);
    AgentConfigurationFrame.pHydroProximiteRoutier.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pHydroProximiteRoutier
        .add(AgentConfigurationFrame.tHydroProximiteRoutierImp, cont);

    AgentConfigurationFrame.pHydroProximiteRoutier.setBorder(
        BorderFactory.createTitledBorder("Proximity of road network"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pHydro
        .add(AgentConfigurationFrame.pHydroProximiteRoutier, c);

    // ecoulement
    AgentConfigurationFrame.pHydroEcoulement
        .add(AgentConfigurationFrame.cHydroEcoulement, cont);
    AgentConfigurationFrame.pHydroEcoulement.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pHydroEcoulement
        .add(AgentConfigurationFrame.tHydroEcoulementImp, cont);

    AgentConfigurationFrame.pHydroEcoulement
        .setBorder(BorderFactory.createTitledBorder("Flow"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pHydro.add(AgentConfigurationFrame.pHydroEcoulement,
        c);

    // platitude lac
    AgentConfigurationFrame.pHydroPlatitudeLac
        .add(AgentConfigurationFrame.cHydroPlatitudeLac, cont);
    AgentConfigurationFrame.pHydroPlatitudeLac.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pHydroPlatitudeLac
        .add(AgentConfigurationFrame.tHydroPlatitudeLacImp, cont);

    AgentConfigurationFrame.pHydroPlatitudeLac
        .setBorder(BorderFactory.createTitledBorder("Lake flatness"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pHydro
        .add(AgentConfigurationFrame.pHydroPlatitudeLac, c);

    AgentConfigurationFrame.panneauOnglets.addTab("Hydro",
        new ImageIcon(AgentConfigurationFrame.class
            .getResource("/images/hydro.gif").getPath().replaceAll("%20", " ")),
        AgentConfigurationFrame.pHydro,
        "Configure constraints on hydro network");

    // champ relief

    // postion points
    AgentConfigurationFrame.pReliefPositionPoint
        .add(AgentConfigurationFrame.cReliefPositionPoint, cont);
    AgentConfigurationFrame.pReliefPositionPoint.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pReliefPositionPoint
        .add(AgentConfigurationFrame.tReliefPositionPointImp, cont);

    AgentConfigurationFrame.pReliefPositionPoint
        .setBorder(BorderFactory.createTitledBorder("Points position"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pRelief
        .add(AgentConfigurationFrame.pReliefPositionPoint, c);

    AgentConfigurationFrame.panneauOnglets.addTab("Relief",
        new ImageIcon(
            AgentConfigurationFrame.class.getResource("/images/relief.gif")
                .getPath().replaceAll("%20", " ")),
        AgentConfigurationFrame.pRelief, "Configure constraints on relief");

    // champ occ sol

    // postion points
    AgentConfigurationFrame.pOccSolPositionPoint
        .add(AgentConfigurationFrame.cOccSolPositionPoint, cont);
    AgentConfigurationFrame.pOccSolPositionPoint.add(new JLabel("Importance"),
        cont);
    AgentConfigurationFrame.pOccSolPositionPoint
        .add(AgentConfigurationFrame.tOccSolPositionPointImp, cont);

    AgentConfigurationFrame.pOccSolPositionPoint
        .setBorder(BorderFactory.createTitledBorder("Points position"));
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.fill = GridBagConstraints.BOTH;
    AgentConfigurationFrame.pOccSol
        .add(AgentConfigurationFrame.pOccSolPositionPoint, c);

    AgentConfigurationFrame.panneauOnglets.addTab("Occ. sol",
        new ImageIcon(
            AgentConfigurationFrame.class.getResource("/images/occsol.gif")
                .getPath().replaceAll("%20", " ")),
        AgentConfigurationFrame.pOccSol, "Configure constraints on landscape");

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
    AgentConfigurationFrame.panneauBoutons.add(AgentConfigurationFrame.bValider,
        c);

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
    AgentConfigurationFrame.panneauBoutons.add(AgentConfigurationFrame.bAnnuler,
        c);

    // bouton enregistrer
    AgentConfigurationFrame.bEnregistrer
        .setPreferredSize(new Dimension(110, 30));
    AgentConfigurationFrame.bEnregistrer
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
            parcourir.setSelectedFile(new File(AgentConfigurationFrame
                .getInstance().getCheminFichierConfigurationAgent()));
            int res = parcourir
                .showOpenDialog(AgentConfigurationFrame.getInstance());
            if (res == JFileChooser.APPROVE_OPTION) {
              GeneralisationSpecifications
                  .saveToFile(parcourir.getSelectedFile());
            } else if (res == JFileChooser.ERROR_OPTION) {
              JOptionPane.showMessageDialog(
                  AgentConfigurationFrame.getInstance(), "Error", "Error",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        });
    c = new GridBagConstraints();
    c.gridx = 2;
    c.gridy = 0;
    c.insets = new Insets(5, 5, 5, 5);
    AgentConfigurationFrame.panneauBoutons
        .add(AgentConfigurationFrame.bEnregistrer, c);

    this.setLayout(new GridBagLayout());
    c = new GridBagConstraints();
    c.gridheight = 5;
    this.add(AgentConfigurationFrame.panneauOnglets, c);

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
    AgentConfigurationFrame.cBatiTaille
        .setSelected(AgentSpecifications.TAILLE_BATIMENT);
    AgentConfigurationFrame.tBatiTailleImp
        .setText("" + AgentSpecifications.TAILLE_BATIMENT_IMP);
    AgentConfigurationFrame.tBatiTailleAireMini
        .setText("" + GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT);
    AgentConfigurationFrame.tBatiTailleSeuilSuppression.setText(
        "" + GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT);
    // granularite
    AgentConfigurationFrame.cBatiGranularite
        .setSelected(AgentSpecifications.GRANULARITE_BATIMENT);
    AgentConfigurationFrame.tBatiGranulariteImp
        .setText("" + AgentSpecifications.GRANULARITE_BATIMENT_IMP);
    AgentConfigurationFrame.tBatiGranulariteLongueurMini
        .setText("" + GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE);
    // equaritte
    AgentConfigurationFrame.tBatiEquarriteImp
        .setText("" + AgentSpecifications.EQUARRITE_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiEquarrite
        .setSelected(AgentSpecifications.EQUARRITE_BATIMENT);
    // largeur locale
    AgentConfigurationFrame.tBatiLargeurLocaleImp
        .setText("" + AgentSpecifications.LARGEUR_LOCALE_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiLargeurLocale
        .setSelected(AgentSpecifications.LARGEUR_LOCALE_BATIMENT);
    // convexite
    AgentConfigurationFrame.tBatiConvexiteImp
        .setText("" + AgentSpecifications.CONVEXITE_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiConvexite
        .setSelected(AgentSpecifications.CONVEXITE_BATIMENT);
    // elongation
    AgentConfigurationFrame.tBatiElongationImp
        .setText("" + AgentSpecifications.ELONGATION_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiElongation
        .setSelected(AgentSpecifications.ELONGATION_BATIMENT);
    // orientation
    AgentConfigurationFrame.tBatiOrientationImp
        .setText("" + AgentSpecifications.ORIENTATION_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiOrientation
        .setSelected(AgentSpecifications.ORIENTATION_BATIMENT);
    // altitude
    AgentConfigurationFrame.tBatiAltitudeImp
        .setText("" + AgentSpecifications.ALTITUDE_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiAltitude
        .setSelected(AgentSpecifications.ALTITUDE_BATIMENT);
    // occ sol
    AgentConfigurationFrame.tBatiOccSolImp
        .setText("" + AgentSpecifications.OCCSOL_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiOccSol
        .setSelected(AgentSpecifications.OCCSOL_BATIMENT);
    // proximite
    AgentConfigurationFrame.tBatiProximiteImp
        .setText("" + AgentSpecifications.PROXIMITE_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiProximite
        .setSelected(AgentSpecifications.PROXIMITE_BATIMENT);
    // densite ilot
    AgentConfigurationFrame.tBatiDensiteIlotImp
        .setText("" + AgentSpecifications.DENSITE_ILOT_BATIMENT_IMP);
    AgentConfigurationFrame.cBatiDensiteIlot
        .setSelected(AgentSpecifications.DENSITE_ILOT_BATIMENT);

    // routier

    // empatement
    AgentConfigurationFrame.tRoutierEmpatementImp
        .setText("" + AgentSpecifications.ROUTIER_EMPATEMENT_IMP);
    AgentConfigurationFrame.cRoutierEmpatement
        .setSelected(AgentSpecifications.ROUTIER_EMPATEMENT);
    AgentConfigurationFrame.tRoutierCoeffPropagationEmpatement.setText(
        "" + GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT);
    // impasses
    AgentConfigurationFrame.tRoutierImpassesImp
        .setText("" + AgentSpecifications.ROUTIER_IMPASSES_IMP);
    AgentConfigurationFrame.cRoutierImpasses
        .setSelected(AgentSpecifications.ROUTIER_IMPASSES);
    // densite
    AgentConfigurationFrame.tRoutierDensiteImp
        .setText("" + AgentSpecifications.ROUTIER_DENSITE_IMP);
    AgentConfigurationFrame.cRoutierDensite
        .setSelected(AgentSpecifications.ROUTIER_DENSITE);

    // controile deformation des troncons
    AgentConfigurationFrame.tRoutierControleDeformationImp
        .setText("" + AgentSpecifications.ROUTIER_CONTROLE_DEFORMATION_IMP);
    AgentConfigurationFrame.cRoutierControleDeformation
        .setSelected(AgentSpecifications.ROUTIER_CONTROLE_DEFORMATION);

    // impasses
    AgentConfigurationFrame.tRoutierImpassesImp
        .setText("" + AgentSpecifications.ROUTIER_IMPASSES_IMP);
    AgentConfigurationFrame.cRoutierImpasses
        .setSelected(AgentSpecifications.ROUTIER_IMPASSES);
    // densite
    AgentConfigurationFrame.tRoutierDensiteImp
        .setText("" + AgentSpecifications.ROUTIER_DENSITE_IMP);
    AgentConfigurationFrame.cRoutierDensite
        .setSelected(AgentSpecifications.ROUTIER_DENSITE);

    // hydro

    // proximite routier
    AgentConfigurationFrame.tHydroProximiteRoutierImp
        .setText("" + AgentSpecifications.PROXIMITE_HYDRO_ROUTIER_IMP);
    AgentConfigurationFrame.cHydroProximiteRoutier
        .setSelected(AgentSpecifications.PROXIMITE_HYDRO_ROUTIER);
    // ecoulement
    AgentConfigurationFrame.tHydroEcoulementImp
        .setText("" + AgentSpecifications.ECOULEMENT_HYDRO_IMP);
    AgentConfigurationFrame.cHydroProximiteRoutier
        .setSelected(AgentSpecifications.PROXIMITE_HYDRO_ROUTIER);
    // platitude lac
    AgentConfigurationFrame.tHydroPlatitudeLacImp
        .setText("" + AgentSpecifications.PLATITUDE_LAC_IMP);
    AgentConfigurationFrame.cHydroPlatitudeLac
        .setSelected(AgentSpecifications.PLATITUDE_LAC);

  }

  public void validateValues() {

    // bati

    // taille
    AgentSpecifications.TAILLE_BATIMENT = AgentConfigurationFrame.cBatiTaille
        .isSelected();
    AgentSpecifications.TAILLE_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiTailleImp.getText());
    GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT = Double
        .parseDouble(AgentConfigurationFrame.tBatiTailleAireMini.getText());
    GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT = Double
        .parseDouble(
            AgentConfigurationFrame.tBatiTailleSeuilSuppression.getText());
    // granularite
    AgentSpecifications.GRANULARITE_BATIMENT = AgentConfigurationFrame.cBatiGranularite
        .isSelected();
    AgentSpecifications.GRANULARITE_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiGranulariteImp.getText());
    GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE = Double.parseDouble(
        AgentConfigurationFrame.tBatiGranulariteLongueurMini.getText());
    // equarrite
    AgentSpecifications.EQUARRITE_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiEquarriteImp.getText());
    AgentSpecifications.EQUARRITE_BATIMENT = AgentConfigurationFrame.cBatiEquarrite
        .isSelected();
    // largeur locale
    AgentSpecifications.LARGEUR_LOCALE_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiLargeurLocaleImp.getText());
    AgentSpecifications.LARGEUR_LOCALE_BATIMENT = AgentConfigurationFrame.cBatiLargeurLocale
        .isSelected();
    // convexite
    AgentSpecifications.CONVEXITE_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiConvexiteImp.getText());
    AgentSpecifications.CONVEXITE_BATIMENT = AgentConfigurationFrame.cBatiConvexite
        .isSelected();
    // elongation
    AgentSpecifications.ELONGATION_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiElongationImp.getText());
    AgentSpecifications.ELONGATION_BATIMENT = AgentConfigurationFrame.cBatiElongation
        .isSelected();
    // orientation
    AgentSpecifications.ORIENTATION_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiOrientationImp.getText());
    AgentSpecifications.ORIENTATION_BATIMENT = AgentConfigurationFrame.cBatiOrientation
        .isSelected();
    // altitude
    AgentSpecifications.ALTITUDE_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiAltitudeImp.getText());
    AgentSpecifications.ALTITUDE_BATIMENT = AgentConfigurationFrame.cBatiAltitude
        .isSelected();
    // occupation du sol
    AgentSpecifications.OCCSOL_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiOccSolImp.getText());
    AgentSpecifications.OCCSOL_BATIMENT = AgentConfigurationFrame.cBatiOccSol
        .isSelected();
    // proximite
    AgentSpecifications.PROXIMITE_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiProximiteImp.getText());
    AgentSpecifications.PROXIMITE_BATIMENT = AgentConfigurationFrame.cBatiProximite
        .isSelected();
    // densite ilot
    AgentSpecifications.DENSITE_ILOT_BATIMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tBatiDensiteIlotImp.getText());
    AgentSpecifications.DENSITE_ILOT_BATIMENT = AgentConfigurationFrame.cBatiDensiteIlot
        .isSelected();

    // routier

    // empatement
    AgentSpecifications.ROUTIER_EMPATEMENT_IMP = Double
        .parseDouble(AgentConfigurationFrame.tRoutierEmpatementImp.getText());
    AgentSpecifications.ROUTIER_EMPATEMENT = AgentConfigurationFrame.cRoutierEmpatement
        .isSelected();
    GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT = Double
        .parseDouble(AgentConfigurationFrame.tRoutierCoeffPropagationEmpatement
            .getText());
    // controle deformation des troncons
    AgentSpecifications.ROUTIER_CONTROLE_DEFORMATION_IMP = Double.parseDouble(
        AgentConfigurationFrame.tRoutierControleDeformationImp.getText());
    AgentSpecifications.ROUTIER_CONTROLE_DEFORMATION = AgentConfigurationFrame.cRoutierControleDeformation
        .isSelected();
    // presence impasses
    AgentSpecifications.ROUTIER_IMPASSES_IMP = Double
        .parseDouble(AgentConfigurationFrame.tRoutierImpassesImp.getText());
    AgentSpecifications.ROUTIER_IMPASSES = AgentConfigurationFrame.cRoutierImpasses
        .isSelected();
    // densite
    AgentSpecifications.ROUTIER_DENSITE_IMP = Double
        .parseDouble(AgentConfigurationFrame.tRoutierDensiteImp.getText());
    AgentSpecifications.ROUTIER_DENSITE = AgentConfigurationFrame.cRoutierDensite
        .isSelected();

    // hydro

    // proximite routier
    AgentSpecifications.PROXIMITE_HYDRO_ROUTIER_IMP = Double.parseDouble(
        AgentConfigurationFrame.tHydroProximiteRoutierImp.getText());
    AgentSpecifications.PROXIMITE_HYDRO_ROUTIER = AgentConfigurationFrame.cHydroProximiteRoutier
        .isSelected();
    // ecoulement
    AgentSpecifications.ECOULEMENT_HYDRO_IMP = Double
        .parseDouble(AgentConfigurationFrame.tHydroEcoulementImp.getText());
    AgentSpecifications.ECOULEMENT_HYDRO = AgentConfigurationFrame.cHydroEcoulement
        .isSelected();
    // platitude lac
    AgentSpecifications.PLATITUDE_LAC_IMP = Double
        .parseDouble(AgentConfigurationFrame.tHydroPlatitudeLacImp.getText());
    AgentSpecifications.PLATITUDE_LAC = AgentConfigurationFrame.cHydroPlatitudeLac
        .isSelected();

  }

}
