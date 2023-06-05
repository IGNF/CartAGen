package fr.ign.cogit.cartagen.appli.core;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentSpecifications;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.network.hydro.HydroNetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.network.road.RoadNetworkAgent;
import fr.ign.cogit.cartagen.agents.core.agent.urban.UrbanAlignmentAgent;
import fr.ign.cogit.cartagen.appli.agents.FrameInfoAgent;
import fr.ign.cogit.cartagen.appli.core.actions.CreateAllAgentsAction;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.appli.core.geoxygene.selection.SelectionUtil;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.agent.Agent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationRightPanelAgentComplement implements AgentObserver {

	static Logger logger = LogManager.getLogger(GeneralisationRightPanelAgentComplement.class.getName());

	static Logger getLogger() {
		return GeneralisationRightPanelAgentComplement.logger;
	}

	public JButton bInfoSelection = new JButton("Infos selection");

	public JPanel pMoteurAgent = new JPanel(new GridBagLayout());
	public JButton bCreateAgentAgents = new JButton("Create AGENT agents");
	public JButton bCreateGaelAgents = new JButton("Create GAEL agents");
	public JButton bCreateCartacomAgents = new JButton("Create CartACom agents");
	public JButton bCreateAgents = new JButton(new CreateAllAgentsAction());
	public JButton bChargerSelection = new JButton("Load selection");
	public JButton bVider = new JButton("Empty");
	public JButton bRemove = new JButton("Remove");
	public JButton bDemarrerArreterMoteur = new JButton("");
	public JCheckBox cAfficherAgentActif = new JCheckBox("Display activated agent", true);
	public JCheckBox cTjsCentrerSurAgentActif = new JCheckBox("Always center on activated agent", false);
	public JButton bcentrerSurAgentActif = new JButton("Center on active agent");
	public JCheckBox cFairePauses = new JCheckBox("Pause", false);
	public JCheckBox cStockerEtats = new JCheckBox("Store states", false);
	public JLabel lNbAgentsListe = new JLabel("0");
	public JButton bRestoreAll = new JButton("Restore all agents");
	private JList<IAgent> jList = new JList<>();
	private boolean slowMotion = false;

	/*
	 * public JCheckBox cSuivreGraphique=new JCheckBox("Suivre sur le graphique"
	 * ,false); public XYSeries donneesGraphique=new XYSeries("1"); public int
	 * indexGraphique=0; public ChartPanel chartPanel;
	 */

	/**
	 */
	private static GeneralisationRightPanelAgentComplement content = null;

	public static GeneralisationRightPanelAgentComplement getInstance() {
		if (GeneralisationRightPanelAgentComplement.content == null) {
			GeneralisationRightPanelAgentComplement.content = new GeneralisationRightPanelAgentComplement();
		}
		return GeneralisationRightPanelAgentComplement.content;
	}

	private GeneralisationRightPanelAgentComplement() {
	}

	/**
	 * 
	 */
	public void add(JPanel rightPanel) {
		Font font = rightPanel.getFont();

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(1, 5, 1, 5);

		// charge les agents des objets selectionnes dans la pile du moteur.
		this.pMoteurAgent.setFont(font);
		this.pMoteurAgent.setBorder(BorderFactory.createTitledBorder("Moteur agent"));

		this.bCreateAgentAgents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AgentUtil.createAgentAgentsInDataset(CartAGenDoc.getInstance().getCurrentDataset());
			}
		});
		this.bCreateAgentAgents.setFont(font);
		this.bCreateAgentAgents.setToolTipText("Create AGENT agents associated to the objects of the whole dataset");
		this.pMoteurAgent.add(this.bCreateAgentAgents, c);

		this.bCreateGaelAgents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AgentUtil.createGaelAgentsInDataset(CartAGenDoc.getInstance().getCurrentDataset());
			}
		});
		this.bCreateGaelAgents.setFont(font);
		this.bCreateGaelAgents.setToolTipText("Create GAEL agents associated to the objects of the whole dataset");
		this.pMoteurAgent.add(this.bCreateGaelAgents, c);

		this.bCreateCartacomAgents.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AgentUtil.createCartacomAgentsInDataset(CartAGenDoc.getInstance().getCurrentDataset());
			}
		});
		this.bCreateCartacomAgents.setFont(font);
		this.bCreateCartacomAgents
				.setToolTipText("Create CartACom agents associated to the objects of the whole dataset");
		this.pMoteurAgent.add(this.bCreateCartacomAgents, c);

		c.insets = new Insets(1, 5, 15, 5);
		this.bCreateAgents.setFont(font);
		this.bCreateAgents.setToolTipText("Create agents associated to the objects of the whole dataset");
		this.pMoteurAgent.add(this.bCreateAgents, c);

		c.insets = new Insets(1, 5, 1, 5);
		// bouton d'info sur selection a jouter au panneau de selection
		this.bInfoSelection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (IFeature obj : SelectionUtil.getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
					if (!(obj instanceof IGeneObj)) {
						continue;
					}
					GeographicObjectAgentGeneralisation ago = (GeographicObjectAgentGeneralisation) AgentUtil
							.getAgentFromGeneObj((IGeneObj) obj);
					if (ago == null) {
						continue;
					}
					ago.printInfosConsole();
					FrameInfoAgent fia = new FrameInfoAgent(ago);
					fia.setVisible(true);
				}
			}
		});
		this.bInfoSelection.setFont(font);
		this.bInfoSelection.setToolTipText("Informations on the generalisation tree of the selected agents");
		this.pMoteurAgent.add(this.bInfoSelection, c);

		this.bChargerSelection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AgentGeneralisationScheduler.getInstance().initList();

				Collection<IFeature> features = SelectionUtil
						.getSelectedObjects(CartAGenPlugin.getInstance().getApplication());
				if (features == null || features.isEmpty()) {
					features = null;
					features = CartAGenPlugin.getInstance().getApplication().getMainFrame().getSelectedProjectFrame()
							.getLayerViewPanel().getSelectedFeatures();
				}

				for (IFeature obj : features) {
					if (!(obj instanceof IGeneObj)) {
						continue;
					}
					GeographicAgentGeneralisation ago = AgentUtil.getAgentFromGeneObj((IGeneObj) obj);
					if (ago == null) {
						continue;
					}
					GeneralisationRightPanelAgentComplement.getLogger().info("Chargement de " + ago);
					AgentGeneralisationScheduler.getInstance().add(ago);

				}
				majAgentsList();
			}
		});
		this.bChargerSelection.setFont(font);
		this.bChargerSelection.setToolTipText("Charge les agents selectionnés dans la liste du moteur");
		this.pMoteurAgent.add(this.bChargerSelection, c);

		this.bVider.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AgentGeneralisationScheduler.getInstance().initList();
			}
		});
		this.bVider.setFont(font);
		this.bVider.setToolTipText("Empty scheduler list");
		this.pMoteurAgent.add(this.bVider, c);

		this.bRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (Object o : GeneralisationRightPanelAgentComplement.this.jList.getSelectedValuesList()) {
					AgentGeneralisationScheduler.getInstance().remove((IAgent) o);
				}
			}
		});
		this.bRemove.setFont(font);
		this.bRemove.setToolTipText("Remove the selected agents from the scheduler list");
		this.pMoteurAgent.add(this.bRemove, c);

		// la liste
		this.jList.setFont(font);
		this.jList.setVisibleRowCount(10);
		c.fill = GridBagConstraints.HORIZONTAL;
		this.pMoteurAgent.add(new JScrollPane(this.jList), c);
		c.fill = GridBagConstraints.NONE;

		this.lNbAgentsListe.setFont(font);
		this.pMoteurAgent.add(this.lNbAgentsListe, c);

		this.bDemarrerArreterMoteur.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (AgentGeneralisationScheduler.getInstance().getThread() == null) {
					AgentGeneralisationScheduler.getInstance().activate();
					GeneralisationRightPanelAgentComplement.this.bDemarrerArreterMoteur
							.setIcon(new ImageIcon("images/stop.gif"));
				} else {
					AgentGeneralisationScheduler.getInstance().deactivate();
					GeneralisationRightPanelAgentComplement.this.bDemarrerArreterMoteur
							.setIcon(new ImageIcon("images/start.gif"));
				}
			}
		});
		this.bDemarrerArreterMoteur.setIcon(new ImageIcon(GeneralisationRightPanelAgentComplement.class
				.getResource("/images/start.gif").getPath().replaceAll("%20", " ")));
		this.bDemarrerArreterMoteur.setToolTipText("Start/stop scheduler");
		this.bDemarrerArreterMoteur.setFont(font);
		this.pMoteurAgent.add(this.bDemarrerArreterMoteur, c);

		this.cAfficherAgentActif.setToolTipText("Affiche l'agent en cours d'activation");
		this.cAfficherAgentActif.setFont(font);
		this.pMoteurAgent.add(this.cAfficherAgentActif, c);

		this.bcentrerSurAgentActif.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Agent.getActivatedAgent() != null) {
					try {
						CartAGenPlugin.getInstance().getApplication().getMainFrame().getSelectedProjectFrame()
								.getLayerViewPanel().getViewport()
								.center(((GeographicObjectAgent) Agent.getActivatedAgent()).getFeature());
					} catch (NoninvertibleTransformException e) {
						e.printStackTrace();
					}
				}
			}
		});
		this.bcentrerSurAgentActif.setToolTipText("Centrer la vue sur l'agent en cours d'activation");
		this.bcentrerSurAgentActif.setFont(font);
		this.pMoteurAgent.add(this.bcentrerSurAgentActif, c);

		this.cTjsCentrerSurAgentActif
				.setToolTipText("Toujours centrer automatiquement la vue sur l'agent en cours d'activation");
		this.cTjsCentrerSurAgentActif.setFont(font);
		this.pMoteurAgent.add(this.cTjsCentrerSurAgentActif, c);

		this.cFairePauses.setToolTipText(
				"Fait une pause d'un court instant après l'activation d'un agent de la pile pour permettre de mieux suivre le processus");
		this.cFairePauses.setFont(font);
		this.pMoteurAgent.add(this.cFairePauses, c);

		this.cStockerEtats.setToolTipText(
				"Stocke tous les états rencontrés par les agents, pour pouvoir étudier leur évolution (consomme beaucoup de mémoire)");
		this.cStockerEtats.setFont(font);
		this.cStockerEtats.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				AgentSpecifications.getLifeCycle()
						.setStoreStates(GeneralisationRightPanelAgentComplement.this.cStockerEtats.isSelected());
			}
		});
		this.pMoteurAgent.add(this.cStockerEtats, c);

		// Restore all agents to initial state
		this.bRestoreAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Roads
				if (CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork() != null) {
					((RoadNetworkAgent) AgentUtil
							.getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork()))
									.goBackToInitialState();
				}
				// Hydro
				if (CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork() != null) {
					HydroNetworkAgent agent = ((HydroNetworkAgent) AgentUtil
							.getAgentFromGeneObj(CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()));
					if (agent != null)
						agent.goBackToInitialState();
				}
				// Buildings
				for (IBuilding obj : CartAGenDoc.getInstance().getCurrentDataset().getBuildings()) {
					((GeographicObjectAgentGeneralisation) AgentUtil.getAgentFromGeneObj(obj)).goBackToInitialState();
				}
				// Blocks
				for (IUrbanBlock obj : CartAGenDoc.getInstance().getCurrentDataset().getBlocks()) {
					((GeographicObjectAgentGeneralisation) AgentUtil.getAgentFromGeneObj(obj)).goBackToInitialState();
				}
				// Urban alignments
				for (IUrbanAlignment obj : CartAGenDoc.getInstance().getCurrentDataset().getUrbanAlignments()) {
					((UrbanAlignmentAgent) AgentUtil.getAgentFromGeneObj(obj)).computeShapeLine();
				}
			}
		});
		this.bRestoreAll.setFont(font);
		this.bRestoreAll.setToolTipText("Restore intial state of all agents of the dataset");
		this.pMoteurAgent.add(this.bRestoreAll, c);

		rightPanel.add(this.pMoteurAgent, c);

		// graphique
		/*
		 * add(cSuivreGraphique, c);
		 * 
		 * donneesGraphique=new XYSeries("1"); indexGraphique=0; XYSeriesCollection
		 * col=new XYSeriesCollection(); //IntervalXYDataset dataset=new
		 * XYBarDataset(col, 1.0); col.addSeries(donneesGraphique); //for (int
		 * i=0;i<5000;i++) donneesGraphique.add(i, 5.0+i*i);
		 * 
		 * JFreeChart chart=ChartFactory.createXYBarChart("","",false,"",new
		 * XYBarDataset(col, 1.0),PlotOrientation.VERTICAL,false,false,false);
		 * ((NumberAxis )((XYPlot)chart.getPlot()).getDomainAxis()).setStandardTickUnits
		 * (NumberAxis.createIntegerTickUnits()); chartPanel = new
		 * ChartPanel(chart,false,false,false,false,false);
		 * chartPanel.setMouseZoomable(true); chartPanel.setPreferredSize(new
		 * java.awt.Dimension(largeur-2,150)); //setContentPane(chartPanel);
		 * add(chartPanel, c);
		 */

	}

	public void majAgentsList() {
		DefaultListModel<IAgent> listModel = new DefaultListModel<>();
		for (IAgent a : AgentGeneralisationScheduler.getInstance().getList()) {
			listModel.addElement(a);
		}
		this.jList.setModel(listModel);
		this.lNbAgentsListe.setText(Integer.toString(this.jList.getModel().getSize()));
	}

	public boolean isSlowMotion() {
		return slowMotion;
	}

	public void setSlowMotion(boolean slowMotion) {
		this.slowMotion = slowMotion;
	}

	@Override
	public void update() {
		majAgentsList();
	}

}
