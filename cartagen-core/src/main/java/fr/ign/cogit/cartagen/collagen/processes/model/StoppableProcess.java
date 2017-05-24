package fr.ign.cogit.cartagen.collagen.processes.model;

import java.util.Collection;
import java.util.Map;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * A generalisation process that can be stopped during the generalisation
 * process, then resumed. For instance, the Agent process can be stopped between
 * two agents lifecycles, whereas a Least Squares process cannot be stopped.
 * @author GTouya
 * 
 */
public interface StoppableProcess {

  /**
   * Update the information on the features handled by the process. When a
   * process is stopped, the features it was handling can be modified or
   * eliminated by another process, so {@code this}'s internal system to manage
   * features modifications has be aware of the updates.
   * @param objetsModifies
   * @param objetsElimines
   * @throws GothicException
   */
  public void updateProcess(Map<IGeneObj, IGeometry> objetsModifies,
      Collection<IGeneObj> objetsElimines);

  public void resumeProcess();

  public boolean isStopped();

  public void setStop(boolean arrete);
}
