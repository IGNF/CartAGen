package fr.ign.cogit.cartagen.agents.diogen.padawan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.cartacom.agent.interfaces.ICartacomAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.GeographicPointAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.model.IDiogenAgent;
import fr.ign.cogit.cartagen.agents.diogen.agent.submicro.ISubmicroAgent;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Assignation;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.Interaction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.InteractionMatrix;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.ConstrainedInteraction;
import fr.ign.cogit.cartagen.agents.diogen.interactionmodel.constrained.RealizableConstrainedInteraction;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

public class Environment {

  private static Logger logger = Logger.getLogger(Environment.class.getName());

  /**
   * The hostAgent (bidirectional reference, automatically managed).
   */
  private IDiogenAgent hostAgent;

  /**
   * Getter for hostAgent. If no associated Agent, returns null.
   * @return the hostAgent
   */
  public IDiogenAgent getHostAgent() {
    return this.hostAgent;
  }

  /**
   * Setter for hostAgent. Also updates the reverse reference from hostAgent to
   * {@code this}. To break the reference use {@code this.setHostAgent(null)}
   * @param hostAgent the hostAgent to set
   */
  public void setHostAgent(IDiogenAgent hostAgent) {
    IDiogenAgent oldHostAgent = this.hostAgent;
    this.hostAgent = hostAgent;
    if (oldHostAgent != null) {
      oldHostAgent.setEncapsulatedEnv(null);
    }
    if (hostAgent != null) {
      if (hostAgent.getEncapsulatedEnv() != this) {
        hostAgent.setEncapsulatedEnv(this);
      }
    }
  }

  /**
   * The containedAgentss set (bidirectional reference, automatically managed).
   */
  private Set<IDiogenAgent> containedAgents = new HashSet<IDiogenAgent>();

  /**
   * Getter for containedAgentss.
   * @return the containedAgentss. It can be empty but not {@code null}.
   */
  public Set<IDiogenAgent> getContainedAgents() {
    return this.containedAgents;
  }

  /**
   * Setter for containedAgentss. Also updates the reverse reference from each
   * element of containedAgentss to {@code this}. To break the reference use
   * {@code this.setContainedAgentss(new HashSet<Agent>())}
   * @param containedAgentss the set of containedAgentss to set
   */
  public void setContainedAgents(Set<IDiogenAgent> containedAgents) {
    Set<IDiogenAgent> oldContainedAgents = new HashSet<IDiogenAgent>(
        this.containedAgents);
    for (IDiogenAgent containedAgent : oldContainedAgents) {
      this.containedAgents.remove(containedAgent);
      containedAgent.removeContainingEnvironments(this);
    }
    for (IDiogenAgent containedAgent : containedAgents) {
      this.containedAgents.add(containedAgent);
      containedAgent.addContainingEnvironments(this);
    }
  }

  /**
   * Adds a Agent to containedAgentss, and updates the reverse reference from
   * the added Agent to {@code this}.
   * @param containedAgents the containedAgents to add
   */
  public void addContainedAgents(IDiogenAgent containedAgent) {
    if (this.containedAgents == null) {
      return;
    }
    // System.out.println("Add agent " + containedAgent.getClass() +
    // " to environment " + this.getEnvironmentType().getEnvironmentTypeName());
    this.containedAgents.add(containedAgent);
    containedAgent.addContainingEnvironments(this);
  }

  /**
   * Removes a Agent from containedAgentss, and updates the reverse reference
   * from the removed Agent by removing {@code this}.
   * @param containedAgents the containedAgents to remove
   */
  public void removeContainedAgents(IDiogenAgent containedAgent) {
    if (this.containedAgents == null) {
      return;
    }
    this.containedAgents.remove(containedAgent);
    containedAgent.removeContainingEnvironments(this);
  }

  public void removeAllContainedAgents() {
    for (IDiogenAgent containedAgent : this.containedAgents) {
      containedAgent.removeContainingEnvironments(this);
    }
    this.containedAgents.clear();
  }

  public boolean contains(IDiogenAgent agent) {
    return this.getContainedAgents().contains(agent);
  }

  // /////////////////////////////////////////////////////////////////////////////

  // Reference (m-n) d'une classe A (cette classe) vers une classe B
  // Definir ci-dessous les noms de classes et d'attributs à utiliser
  // puis virer ces lignes
  // Classe A : Environment
  // Un A, vu de B : borderedEnvironment
  // Le même avec une majuscule au début : BorderedEnvironment
  // Classe B : GeographicObjectAgent
  // Un B, vu de A : borderAgent
  // Le même avec une majuscule au début : BorderAgent

  // ////////////// Code à mettre dans la classe Environment //

  /**
   * The borderAgents set (bidirectional reference, automatically managed).
   */
  private Set<IDiogenAgent> borderAgents = new HashSet<IDiogenAgent>();

  /**
   * Getter for borderAgents.
   * @return the borderAgents. It can be empty but not {@code null}.
   */
  public Set<IDiogenAgent> getBorderAgents() {
    return this.borderAgents;
  }

  /**
   * Setter for borderAgents. Also updates the reverse reference from each
   * element of borderAgents to {@code this}. To break the reference use
   * {@code this.setBorderAgents(new HashSet<GeographicObjectAgent>())}
   * @param borderAgents the set of borderAgents to set
   */
  public void setBorderAgents(Set<IDiogenAgent> borderAgents) {
    Set<IDiogenAgent> oldBorderAgents = new HashSet<IDiogenAgent>(
        this.borderAgents);
    for (IDiogenAgent borderAgent : oldBorderAgents) {
      this.borderAgents.remove(borderAgent);
      borderAgent.getBorderedEnvironments().remove(this);
    }
    for (IDiogenAgent borderAgent : borderAgents) {
      this.borderAgents.add(borderAgent);
      borderAgent.getBorderedEnvironments().add(this);
    }
  }

  /**
   * Adds a GeographicObjectAgent to borderAgents, and updates the reverse
   * reference from the added GeographicObjectAgent to {@code this}.
   * @param borderAgent the borderAgent to add
   */
  public void addBorderAgent(IDiogenAgent borderAgent) {
    if (borderAgent == null) {
      return;
    }
    this.borderAgents.add(borderAgent);
    borderAgent.getBorderedEnvironments().add(this);
  }

  /**
   * Removes a GeographicObjectAgent from borderAgents, and updates the reverse
   * reference from the removed GeographicObjectAgent by removing {@code this}.
   * @param borderAgent the borderAgent to remove
   */
  public void removeBorderAgent(IDiogenAgent borderAgent) {
    if (borderAgent == null) {
      return;
    }
    this.borderAgents.remove(borderAgent);
    borderAgent.getBorderedEnvironments().remove(this);
  }

  private EnvironmentType environmentType;

  /**
   * Setter for environmentType.
   * @param environmentType the environmentType to set
   */
  public void setEnvironmentType(EnvironmentType environmentType) {
    this.environmentType = environmentType;
  }

  /**
   * Getter for environmentType.
   * @return the environmentType
   */
  public EnvironmentType getEnvironmentType() {
    return this.environmentType;
  }

  public void changeEnvironmentTypeToRoadNeighbourhood() {
    this.setEnvironmentType(
        EnvironmentTypesList.getRoadNeighbourhoodEnvironmentType());
  }

  /**
   * Change the value of environmentType to Block
   */
  public void changeEnvironmentTypeToBlock() {
    this.setEnvironmentType(EnvironmentTypesList.getBlockEnvironmentType());
  }

  /**
   * Change the value of environmentType to Deformable
   */
  public void changeEnvironmentTypeToDeformable() {
    this.setEnvironmentType(
        EnvironmentTypesList.getDeformableEnvironmentType());
  }

  /**
   * Change the value of environmentType to Town
   */
  public void changeEnvironmentTypeToTown() {
    this.setEnvironmentType(EnvironmentTypesList.getTownEnvironmentType());

  }

  /**
   * Change the value of environmentType to Network
   */
  public void changeEnvironmentTypeToNetwork() {
    this.setEnvironmentType(EnvironmentTypesList.getNetworkEnvironmentType());

  }

  /**
   * Change the value of environmentType to Global
   */
  public void changeEnvironmentTypeToGlobal() {
    this.setEnvironmentType(EnvironmentTypesList.getGlobalEnvironmentType());

  }

  /**
   * Return the interaction matrix associated to this environment.
   * 
   * @return
   */
  public InteractionMatrix<ConstrainedInteraction> getInteractionMatrix() {
    return this.getEnvironmentType().getInteractionMatrix();
  }

  /**
   * Return the neighborhood of agent in this environment.
   * 
   * @param agent
   * @return
   */
  public Set<IDiogenAgent> getNeighborhood(IDiogenAgent agent) {
    Set<IDiogenAgent> neighborhood = new HashSet<IDiogenAgent>();
    // for now, only host agent can see the contained agents.
    if (agent == this.hostAgent) {
      neighborhood.addAll(this.getContainedAgents());
    } else if (agent instanceof ICartacomAgent) {
      ICartacomAgent cAgent = (ICartacomAgent) agent;
      for (IAgent target : cAgent.getAgentsSharingRelation()) {
        if (((IDiogenAgent) target).getContainingEnvironments()
            .contains(this)) {
          neighborhood.add((IDiogenAgent) target);
        }
      }
    }
    if (agent instanceof GeographicPointAgent) {
      for (ISubmicroAgent target : ((GeographicPointAgent) agent)
          .getSubmicroAgents()) {
        if (target.getContainingEnvironments().contains(this)) {
          neighborhood.add(target);
        }
      }
    }

    return neighborhood;

  }

  private boolean alreadyDoneInteraction(IAgent source, Set<IAgent> targets,
      Interaction interaction,
      Set<RealizableConstrainedInteraction> alreadyDone) {
    logger.debug("Source " + source + ", targets " + targets + " interaction "
        + interaction);
    for (RealizableConstrainedInteraction rci : alreadyDone) {
      logger.debug("Realisable interaction : source " + rci.getSource()
          + ", targets " + targets + " interaction " + interaction);
      if (!rci.getInteraction().equals(interaction)) {
        continue;
      }
      if (!rci.getSource().equals(source)) {
        continue;
      }
      if (targets == null || targets.isEmpty()) {
        if (rci.getTargets() == null || rci.getTargets().isEmpty()) {
          return true;
        } else {
          continue;
        }
      }
      if (rci.getTargets() == null || rci.getTargets().isEmpty()) {
        continue;
      }
      if (!targets.containsAll(rci.getTargets())
          || !rci.getTargets().containsAll(targets)) {
        continue;
      }
      return true;
    }
    return false;
  }

  public List<RealizableConstrainedInteraction> getRealizableInteractions(
      IDiogenAgent agent,
      Set<RealizableConstrainedInteraction> alreadyDoneInteractionsList) {
    return getRealizableInteractions(agent, alreadyDoneInteractionsList, false);
  }

  /**
   * Return all possible interaction realizable between agent and the agents of
   * its neighbourhood.
   * 
   * @param agent
   * @return
   */
  public List<RealizableConstrainedInteraction> getRealizableInteractions(
      IDiogenAgent agent,
      Set<RealizableConstrainedInteraction> alreadyDoneInteractionsList,
      boolean all) {
    logger.debug("Get interaction in environment of type "
        + this.getEnvironmentType().getEnvironmentTypeName());
    Set<IDiogenAgent> neighborhood = this.getNeighborhood(agent);
    logger.debug("Neighborhood " + neighborhood);
    List<RealizableConstrainedInteraction> realizableInteractions = new ArrayList<RealizableConstrainedInteraction>();

    InteractionMatrix<ConstrainedInteraction> interactionMatrix = this
        .getInteractionMatrix();
    logger.debug("Interaction matrix " + interactionMatrix);
    // for each agent of the neighborhood, get the realizable interaction
    for (IDiogenAgent target : neighborhood) {
      logger.debug(
          "Get interaction for source " + agent + " and target " + target);
      Set<Assignation<ConstrainedInteraction>> assignations = interactionMatrix
          .getAssignations(agent.getClass(), target.getClass());

      // logger.debug("Assignations " + assignations);
      if (assignations != null) {
        for (Assignation<ConstrainedInteraction> assignation : assignations) {
          logger.debug(assignation.getInteraction());
          if (alreadyDoneInteractionsList != null) {
            Set<IAgent> targets = new HashSet<IAgent>();
            targets.add(target);
            boolean alreadyDone = alreadyDoneInteraction(agent, targets,
                assignation.getInteraction(), alreadyDoneInteractionsList);
            logger.debug(assignation.getInteraction() + " already done ? "
                + alreadyDone);
            if (alreadyDone) {
              continue;
            }
          }
          RealizableConstrainedInteraction realizableInteraction = new RealizableConstrainedInteraction(
              assignation.getInteraction(), agent, target, this);
          realizableInteraction.computeConditions();

          logger.debug("realizableInteraction.getPreconditionsValue() "
              + realizableInteraction.getPreconditionsValue());
          if (all || realizableInteraction
              .getPreconditionsValue() > Integer.MIN_VALUE) {
            realizableInteractions.add(realizableInteraction);
          }
        }
      }
      Set<Assignation<ConstrainedInteraction>> hostAssignations = interactionMatrix
          .getHostAssignation(target.getClass());
      if (hostAssignations != null) {
        for (Assignation<ConstrainedInteraction> assignation : hostAssignations) {
          if (alreadyDoneInteractionsList != null) {
            Set<IAgent> targets = new HashSet<IAgent>();
            targets.add(target);
            boolean alreadyDone = alreadyDoneInteraction(agent, targets,
                assignation.getInteraction(), alreadyDoneInteractionsList);
            logger.debug(assignation.getInteraction() + " already done ? "
                + alreadyDone);
            if (alreadyDone) {
              continue;
            }
          }
          RealizableConstrainedInteraction realizableInteraction = new RealizableConstrainedInteraction(
              assignation.getInteraction(), agent, target, this);
          realizableInteraction.computeConditions();
          if (all || realizableInteraction
              .getPreconditionsValue() > Integer.MIN_VALUE) {
            realizableInteractions.add(realizableInteraction);
          }
        }
      }
    }
    Set<Assignation<ConstrainedInteraction>> degenerateAssignations = interactionMatrix
        .getDegenerateAssignation(agent.getClass());
    if (degenerateAssignations != null) {
      for (Assignation<ConstrainedInteraction> assignation : degenerateAssignations) {
        if (alreadyDoneInteractionsList != null) {
          boolean alreadyDone = alreadyDoneInteraction(agent, null,
              assignation.getInteraction(), alreadyDoneInteractionsList);
          logger.debug(
              assignation.getInteraction() + " already done ? " + alreadyDone);
          if (alreadyDone) {
            continue;
          }
        }
        Environment.logger
            .debug("Interaction " + assignation.getInteraction() + " OK");
        RealizableConstrainedInteraction realizableInteraction = new RealizableConstrainedInteraction(
            assignation.getInteraction(), agent, this);
        realizableInteraction.computeConditions();
        Environment.logger.debug("Preconditions value "
            + realizableInteraction.getPreconditionsValue());
        if (all || realizableInteraction
            .getPreconditionsValue() > Integer.MIN_VALUE) {
          boolean test = realizableInteractions.add(realizableInteraction);
          for (RealizableConstrainedInteraction in : realizableInteractions) {
            Environment.logger
                .debug("Compare " + realizableInteraction.compareTo(in));
            Environment.logger
                .debug("Equals " + realizableInteraction.equals(in));
          }
          Environment.logger.debug("Added ? = " + test);
        }
      }
    }
    Set<Assignation<ConstrainedInteraction>> degenerateHostAssignations = interactionMatrix
        .getDegenerateHostAssignation();
    if (degenerateHostAssignations != null) {
      for (Assignation<ConstrainedInteraction> assignation : degenerateHostAssignations) {

        if (alreadyDoneInteractionsList != null) {
          boolean alreadyDone = alreadyDoneInteraction(agent, null,
              assignation.getInteraction(), alreadyDoneInteractionsList);
          logger.debug(
              assignation.getInteraction() + " already done ? " + alreadyDone);
          if (alreadyDone) {
            continue;
          }
        }
        RealizableConstrainedInteraction realizableInteraction = new RealizableConstrainedInteraction(
            assignation.getInteraction(), agent, this);
        realizableInteraction.computeConditions();
        if (all || realizableInteraction
            .getPreconditionsValue() > Integer.MIN_VALUE) {
          realizableInteractions.add(realizableInteraction);
        }
      }
    }

    // System.out.println("realizableInteractions " + realizableInteractions);

    return realizableInteractions;
  }
}
