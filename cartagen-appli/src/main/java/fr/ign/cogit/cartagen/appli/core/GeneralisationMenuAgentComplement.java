package fr.ign.cogit.cartagen.appli.core;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BuildingAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.PointAgentImpl;
import fr.ign.cogit.cartagen.appli.agents.AgentConfigurationFrame;
import fr.ign.cogit.cartagen.appli.agents.FrameDistribution;
import fr.ign.cogit.cartagen.appli.core.actions.CreateAGENTAgentsAction;
import fr.ign.cogit.cartagen.appli.core.actions.CreateAllAgentsAction;
import fr.ign.cogit.cartagen.appli.core.actions.CreateCartAComAgentsAction;
import fr.ign.cogit.cartagen.appli.core.actions.CreateDiscreteGAELAgentsAction;
import fr.ign.cogit.cartagen.appli.core.actions.CreateGAELAgentsAction;
import fr.ign.cogit.cartagen.appli.core.actions.CreateReliefGAELAgentAction;
import fr.ign.cogit.cartagen.appli.core.actions.CreateReliefGAELFieldAction;
import fr.ign.cogit.cartagen.appli.core.actions.GeneReliefGAELAgentAction;
import fr.ign.cogit.cartagen.appli.core.actions.RunOnSelectionAction;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.appli.MainFrameMenuBar;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.GeneralisationMenus;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationMenuAgentComplement {
    static Logger logger = Logger.getLogger(GeneralisationMenuAgentComplement.class.getName());

    /**
     * @return
     */
    static Logger getLogger() {
        return GeneralisationMenuAgentComplement.logger;
    }

    // generalisation
    private JMenuItem mConfigAgent = new JMenuItem("Agent configuration");
    private JMenuItem mCreateAll = new JMenuItem(new CreateAllAgentsAction());
    private JMenuItem mRunOnSelection = new JMenuItem(new RunOnSelectionAction());

    // AGENT
    private JMenu menuAGENT = new JMenu("AGENT");
    private JMenuItem mCreateAGENTAgents = new JMenuItem(new CreateAGENTAgentsAction());

    // CartACom
    private JMenu menuCartACom = new JMenu("CartACom");
    private JMenuItem mCreateCartAComAgents = new JMenuItem(new CreateCartAComAgentsAction());

    // GAEL
    private JMenu menuGAEL = new JMenu("GAEL");
    private JMenuItem mCreateGAELAgents = new JMenuItem(new CreateGAELAgentsAction());
    private JMenuItem mCreateDiscGAELAgents = new JMenuItem(new CreateDiscreteGAELAgentsAction());
    private JMenuItem mCreateReliefAgent = new JMenuItem(new CreateReliefGAELAgentAction());
    private JMenuItem mGenReliefAgent = new JMenuItem(new GeneReliefGAELAgentAction());
    private JMenuItem mCreateReliefField = new JMenuItem(new CreateReliefGAELFieldAction());

    // agents point
    private JMenu menuAgentsPoint = new JMenu("Point Agents");
    public JCheckBoxMenuItem mAgentsPointVoir = new JCheckBoxMenuItem("Display");
    public JCheckBoxMenuItem mAgentsPointVoirId = new JCheckBoxMenuItem("Display id");
    public JCheckBoxMenuItem mAgentsPointVoirIni = new JCheckBoxMenuItem("Display initial");
    public JCheckBoxMenuItem mAgentsPointVoirLien = new JCheckBoxMenuItem("Display link to initial");
    public JCheckBoxMenuItem mAgentsPointVoirTexteLien = new JCheckBoxMenuItem("Display text on link");
    public JCheckBoxMenuItem mAgentsPointVoirTexteDesequilibre = new JCheckBoxMenuItem("Display unbalance value");
    public JCheckBoxMenuItem mAgentsPointVoirVecteursDeplacement = new JCheckBoxMenuItem("Display vectors");
    public JCheckBoxMenuItem mAgentsPointEnregistrerTrajectoire = new JCheckBoxMenuItem("Store routes");
    private JMenuItem mAgentsPointEffacerTrajectoire = new JMenuItem("Delete routes");
    public JCheckBoxMenuItem mAgentsPointVoirTrajectoire = new JCheckBoxMenuItem("Display routes");
    public JCheckBoxMenuItem mAgentsPointVoirTexteDesequilibreTrajectoire = new JCheckBoxMenuItem(
            "Display unbalance values routes");

    // statistices
    private JMenu menuStats = new JMenu("Agent Stats");
    private JMenuItem mBuildingsSatisfactionDitribution = new JMenuItem("Buildings satisfaction distribution");
    private JMenuItem mBlocksSatisfactionDitribution = new JMenuItem("Blocks satisfaction distribution");

    // Gestion connaissances
    private JMenu menuGestionK = new JMenu("Knowledge Revision");
    private JMenuItem mChargeFichierK = new JMenuItem("Chargement fichier connaissances");
    private JMenuItem mSauvegardeFichierK = new JMenuItem("Sauvegarde fichier connaissances");
    private JMenuItem mChargeEchantillonRev = new JMenuItem("Chargement échantillon révision");
    private JMenuItem mRevisionKBat = new JMenuItem("Révision des connaissances des bâtiments");
    private JMenuItem mRevisionKIlot = new JMenuItem("Révision des connaissances des îlots");
    private JMenuItem mDefinitionK = new JMenuItem("Définition des connaissances");
    private JMenuItem mAcquiKExpert = new JMenuItem("Acquisition des connaissances des experts");
    private JMenuItem mDefBesoinUtilisateur = new JMenuItem("Définition des besoins utilisateurs");
    private JMenuItem mTestCritereVal = new JMenuItem("Test des critères de validité");

    /**
     */
    private static GeneralisationMenuAgentComplement content = null;

    public static GeneralisationMenuAgentComplement getInstance() {
        if (GeneralisationMenuAgentComplement.content == null) {
            GeneralisationMenuAgentComplement.content = new GeneralisationMenuAgentComplement();
        }
        return GeneralisationMenuAgentComplement.content;
    }

    private GeneralisationMenuAgentComplement() {
    }

    public JMenu getMenuComplement(Class<? extends JMenu> menuClass) {
        MainFrameMenuBar menu = CartAGenPlugin.getInstance().getApplication().getMainFrame().getMenuBar();
        for (Component comp : menu.getComponents()) {
            if (menuClass.isInstance(comp)) {
                return menuClass.cast(comp);
            }
        }
        return null;
    }

    /**
     * Creation of the toolbar with all menus
     */
    public void add(JMenuBar menu) {

        new DataThemesAgentComplementGUIComponent();

        // menu generalisation
        this.mConfigAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                AgentConfigurationFrame.getInstance().setVisible(true);
            }
        });
        GeneralisationMenus.getInstance().getMenuGene().addSeparator();
        GeneralisationMenus.getInstance().getMenuGene().add(this.mConfigAgent);

        // menu AGENT
        menuAGENT.add(mCreateAGENTAgents);

        // menu CartACom
        menuCartACom.add(mCreateCartAComAgents);

        // menu GAEL
        menuGAEL.add(mCreateReliefField);
        menuGAEL.add(mCreateGAELAgents);
        menuGAEL.add(mCreateDiscGAELAgents);
        menuGAEL.add(mCreateReliefAgent);
        menuGAEL.add(mGenReliefAgent);

        // menu point

        this.menuAgentsPoint.add(this.mAgentsPointVoirIni);
        this.menuAgentsPoint.add(this.mAgentsPointVoir);
        this.menuAgentsPoint.add(this.mAgentsPointVoirId);
        this.menuAgentsPoint.add(this.mAgentsPointVoirLien);
        this.menuAgentsPoint.add(this.mAgentsPointVoirTexteLien);
        this.menuAgentsPoint.add(this.mAgentsPointVoirTexteDesequilibre);
        this.menuAgentsPoint.addSeparator();
        this.menuAgentsPoint.add(this.mAgentsPointVoirVecteursDeplacement);
        this.menuAgentsPoint.add(this.mAgentsPointEnregistrerTrajectoire);
        this.mAgentsPointEffacerTrajectoire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                for (IPointAgent ap : PointAgentImpl.getLISTE()) {
                    ap.effacerEtats();
                }
            }
        });
        this.menuAgentsPoint.add(this.mAgentsPointEffacerTrajectoire);
        this.menuAgentsPoint.add(this.mAgentsPointVoirTrajectoire);
        this.menuAgentsPoint.add(this.mAgentsPointVoirTexteDesequilibreTrajectoire);

        // menu statistics

        this.mBuildingsSatisfactionDitribution.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Double> data = new ArrayList<Double>();
                for (IBuilding obj : CartAGenDoc.getInstance().getCurrentDataset().getBuildings()) {
                    BuildingAgent ag = (BuildingAgent) AgentUtil.getAgentFromGeneObj(obj);
                    ag.computeSatisfaction();
                    data.add(new Double(ag.getSatisfaction()));
                }
                (new FrameDistribution("Buildings satisfaction", data, 100, true)).setVisible(true);
            }
        });
        this.menuStats.add(this.mBuildingsSatisfactionDitribution);

        this.mBlocksSatisfactionDitribution.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Double> data = new ArrayList<Double>();
                for (IUrbanBlock obj : CartAGenDoc.getInstance().getCurrentDataset().getBlocks()) {
                    BlockAgent ag = (BlockAgent) AgentUtil.getAgentFromGeneObj(obj);
                    ag.computeSatisfaction();
                    data.add(new Double(ag.getSatisfaction()));
                }
                (new FrameDistribution("blocks satisfaction", data, 100, true)).setVisible(true);
            }
        });
        this.menuStats.add(this.mBlocksSatisfactionDitribution);

        // ajour aux menus existants
        Font font = menu.getFont();
        JMenu agentMenu = new JMenu("Agents");
        menu.add(agentMenu, menu.getComponentCount() - 1);
        agentMenu.add(mCreateAll);
        agentMenu.add(mRunOnSelection);
        agentMenu.add(menuAGENT);
        agentMenu.add(menuCartACom);
        agentMenu.add(menuGAEL);
        agentMenu.addSeparator();
        this.menuAgentsPoint.setFont(font);
        agentMenu.add(this.menuAgentsPoint);
        this.menuStats.setFont(font);
        agentMenu.add(this.menuStats);

        // met la bonne police a tous les trucs du menu
        for (int i = 0; i < menu.getComponentCount(); i++) {
            if (!(menu.getComponent(i) instanceof JMenu)) {
                continue;
            }
            JMenu comp = (JMenu) menu.getComponent(i);
            comp.setFont(font);
            for (int j = 0; j < comp.getItemCount(); j++) {
                if (comp.getItem(j) == null) {
                    continue;
                }
                comp.getItem(j).setFont(font);
            }
        }

    }

}
