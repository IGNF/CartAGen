package fr.ign.cogit.cartagen.collagen.processes.implementation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.CartAComLifeCycle;
import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.cartacom.conversation.ConversationsManager;
import fr.ign.cogit.cartagen.agents.core.AgentGeneralisationScheduler;
import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.collagen.components.orchestration.Conductor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.processes.model.StoppableProcess;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.BasicLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;

public class CartAComProcess extends GeneralisationProcess
    implements StoppableProcess {

  private AgentObserver observer;

  public CartAComProcess(Conductor chefO, AgentObserver observer) {
    super(chefO);
    this.observer = observer;
  }

  @Override
  protected void updateEliminations() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void incrementStates() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadXMLDescription() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void triggerGeneralisation(GeographicSpace space) {
    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
    // first, create AGENT agents in the space for micro auto-generalisation
    AgentUtil.createAgentAgentsInArea(dataset, (IPolygon) space.getGeom(),
        "/xml/cartacom/configAgentForCartACom.xml");
    // first, create CartACom agents in the space
    AgentUtil.createCartacomAgentsInArea(dataset, (IPolygon) space.getGeom());

    // initialisation des agents (voir InitCartAComAgentsInWindow dans
    // PAAMS2014Menu)
    Collection<IGeneObj> allCacGeneObjOfWindow = CartAComAgentGeneralisation
        .getAllCartAComGeneObjs().select(space.getGeom());
    // En deduit les agents CartACom associes
    Set<ICartAComAgentGeneralisation> allCACAgentsOfArea = AgentUtil
        .getCartAComAgentSetFromGeneObjs(allCacGeneObjOfWindow);
    // Lance l'initialisation de l'environnement sur ces agents
    for (ICartAComAgentGeneralisation cacAgent : allCACAgentsOfArea) {

      cacAgent.initialiseEnvironmentRepresentation();
    }
    // Puis lance l'initialisation des contraintes sur ces agents
    for (ICartAComAgentGeneralisation cacAgent : allCACAgentsOfArea) {

      cacAgent.initialiseRelationalConstraints();
      cacAgent.setLifeCycle(CartAComLifeCycle.getInstance());
      cacAgent.clearConversations();
    }
    // initialise the conversation manager
    ConversationsManager manager = ConversationsManager.getInstance();
    manager.reset();

    TreeExplorationLifeCycle.getInstance().attach(observer);
    BasicLifeCycle.getInstance().attach(observer);

    // on met les agents dans le scheduler
    // initialisation
    AgentGeneralisationScheduler.getInstance().initList();
    for (ICartAComAgentGeneralisation agent : allCACAgentsOfArea) {
      AgentGeneralisationScheduler.getInstance().add(agent);

      // run generalisation
      AgentGeneralisationScheduler.getInstance().activate();
    }
  }

  @Override
  public String getName() {
    return "Urban AGENT";
  }

  @Override
  public void updateProcess(Map<IGeneObj, IGeometry> objetsModifies,
      Collection<IGeneObj> objetsElimines) {
    // TODO Auto-generated method stub

  }

  @Override
  public void resumeProcess() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isStopped() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setStop(boolean arrete) {
    // TODO Auto-generated method stub

  }

  /**
   * Shortcut to run the process on a given {@link GeographicSpace} instance.
   * @param space
   */
  public void runOnGeoSpace(GeographicSpace space) {
    triggerGeneralisation(space);
  }
}
