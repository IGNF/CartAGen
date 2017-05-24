package fr.ign.cogit.cartagen.agents.gael.deformation;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author JGaffuri
 */
public interface GAELLinkableFeature {

  public abstract IPointAgent getAgentPointReferant();

  /**
   * @param agentPointReferant
   */
  public abstract void setAgentPointReferant(IPointAgent agentPointReferant);

  public abstract ArrayList<GAELSegment> getSegmentsProximite();

  /**
   * @param linkedFeatureState
   */
  public abstract void goBackToState(GAELLinkedFeatureState linkedFeatureState);

  public void clean();

  public IFeature getFeature();

  public IGeometry getGeom();

  public IGeometry getSymbolGeom();

  public double getSymbolArea();

  public boolean isDeleted();

  public int getId();

  public IPopulation<? extends IFeature> getPopulation();

  public void setDeleted(boolean deleted);

  public void setGeom(IGeometry g);

  public void setId(int Id);

}
