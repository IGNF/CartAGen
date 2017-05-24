package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.landuse.LandUseParcelAgent;
import fr.ign.cogit.cartagen.agents.gael.field.agent.relief.ReliefFieldAgent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class SmallCompactAgent extends MicroAgentGeneralisation
    implements ISmallCompact {

  @Override
  public IDirectPosition getPosition() {
    if (this.getGeom() == null || this.getGeom().isEmpty()) {
      return null;
    }
    return this.getGeom().centroid();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.api.ISmallCompact#getElevation()
   */
  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.SmallCompact#getElevation()
   */
  @Override
  public double getElevation(ReliefFieldAgent reliefField) {
    if (this.isDeleted()) {
      return -999.9;
    }

    // moyenne des altitudes de quelques points régulierement repartis dans la
    // geometrie de l'agent

    int nbPas = 5;
    double xMin = this.getGeom().envelope().minX();
    double xMax = this.getGeom().envelope().maxX();
    double yMin = this.getGeom().envelope().minY();
    double yMax = this.getGeom().envelope().maxY();
    int nb = 0;
    double sommeZ = 0.0;

    // moyenne des altitudes de points
    for (double x = xMin; x <= xMax; x += (xMax - xMin) / nbPas) {
      for (double y = yMin; y <= yMax; y += (yMax - yMin) / nbPas) {
        DirectPosition pos = new DirectPosition(x, y);
        if (!this.getGeom().contains(new GM_Point(pos))) {
          continue;
        }
        double z = reliefField.getAltitude(pos);
        if (z == -999.9) {
          continue;
        }
        sommeZ += z;
        nb++;
      }
    }
    if (nb == 0) {
      return -999.9;
    }
    return sommeZ / nb;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.api.ISmallCompact#getSlopeVectorOrientation()
   */
  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.SmallCompact#getSlopeVectorOrientation()
   */
  @Override
  public double getSlopeVectorOrientation(ReliefFieldAgent reliefField) {
    if (this.isDeleted()) {
      return 999.9;
    }
    return reliefField.getSlopeAzimutalOrientation(this.getPosition());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.agents.IPetitCompact#getDomaineOccSol()
   */
  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.SmallCompact#getLandUseParcel()
   */
  @Override
  public LandUseParcelAgent getLandUseParcel() {
    return null;
  }

}
