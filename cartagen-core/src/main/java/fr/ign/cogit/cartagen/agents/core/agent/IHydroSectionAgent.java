/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.agent;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;

/**
 * @author JGaffuri
 * 
 */
public interface IHydroSectionAgent extends ISectionAgent {

  @Override
  public IWaterLine getFeature();

  /**
   * @return part du troncon superposee avec le reseau routier
   */
  public double getTauxSuperpositionRoutier();

  public void ajouterContrainteEcoulement(double imp);

  public void ajouterContrainteProximiteRoutier(double imp);

  public void ajouterContraintesSubmicrosProximiteRoutier(double imp);

}
