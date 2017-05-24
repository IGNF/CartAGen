package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseParcelAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * The small compact interface: such an object is an object that behaves almost
 * like a point (but which is not necessary punctual): it displaces relatively
 * easily (further definition in [Duchene 2003])
 * @author julien Gaffuri 1 juil. 2009
 */
public interface ISmallCompact {

  /**
   * @return the object position
   */
  public IDirectPosition getPosition();

  /**
   * @param reliefField The relief field the elevation is computed from
   * @return the elevation of the object. returns -999.9 if the object is
   *         deleted or if the relief is not defined under it.
   */
  public double getElevation(ReliefFieldAgent reliefField);

  /**
   * @param reliefField The relief field the orientation is computed from
   * @return the orientation of the relief under the object. returns 999.9 if
   *         the object is deleted or if the relief is not defined under it. if
   *         the relief is flat, so the slope vector is not defined, returns
   *         -999.9 the returned angle value is in radian within -Pi and Pi. It
   *         is the orientation between the slope vector and the [Ox) axis.
   * 
   */
  public double getSlopeVectorOrientation(ReliefFieldAgent reliefField);

  /**
   * @return the landuse parcel the object is on.
   */
  public LandUseParcelAgent getLandUseParcel();

}
