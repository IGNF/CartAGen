/**
 * 
 */
package fr.ign.cogit.cartagen.agents.gael.deformation;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * A feature linked to a point agent
 * @author JGaffuri
 */
public class GAELLinkableFeatureImpl implements GAELLinkableFeature {

  /**
   * @param feature
   */
  public GAELLinkableFeatureImpl(IFeature feature) {
    this.feature = feature;
  }

  /**
   * un agent point c'est usuellement un agent point situe au centre de l'agent
   */
  private IPointAgent agentPointReferant = null;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.agents.IAgentMicro#getAgentPointReferant()
   */
  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELLinkableFeature#
   * getAgentPointReferant()
   */
  /**
   * @return
   */
  @Override
  public IPointAgent getAgentPointReferant() {
    return this.agentPointReferant;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.agents.IAgentMicro#setAgentPointReferant
   * (fr.ign.cogit.generalisation.lib.agents.AgentPoint)
   */
  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELLinkableFeature#
   * setAgentPointReferant
   * (fr.ign.cogit.generalisation.gaeldeformation.PointAgent)
   */
  /**
   * @param agentPointReferant
   */
  @Override
  public void setAgentPointReferant(IPointAgent agentPointReferant) {
    this.agentPointReferant = agentPointReferant;
  }

  /**
   * les segments issus de la triangulation de agent meso controleur eventuel
   */
  private ArrayList<GAELSegment> segmentsProximite = new ArrayList<GAELSegment>();

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.gaeldeformation.GAELLinkableFeature#
   * getSegmentsProximite()
   */
  /**
   * @return
   */
  @Override
  public ArrayList<GAELSegment> getSegmentsProximite() {
    return this.segmentsProximite;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.generalisation.gaeldeformation.GAELLinkableFeature#
   * goBackToState
   * (fr.ign.cogit.generalisation.gaeldeformation.state.GAELLinkedFeatureState)
   */
  @Override
  public void goBackToState(GAELLinkedFeatureState linkedFeatureState) {
    this.setGeom(linkedFeatureState.getGeometry());
  }

  @Override
  public void clean() {
    this.getSegmentsProximite().clear();
    this.setAgentPointReferant(null);
  }

  private IFeature feature;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.gaeldeformation.GAELLinkableFeature#getFeature
   * ()
   */
  /**
   * @return
   */
  @Override
  public IFeature getFeature() {
    return this.feature;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getGeom()
   */
  @Override
  public IGeometry getGeom() {
    return this.getFeature().getGeom();
  }

  @Override
  public IGeometry getSymbolGeom() {
    return this.getGeom();
  }

  @Override
  public double getSymbolArea() {
    return this.getSymbolGeom().area();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#isDeleted()
   */
  @Override
  public boolean isDeleted() {
    return this.getFeature().isDeleted();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getId()
   */
  @Override
  public int getId() {
    return this.getFeature().getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getPopulation()
   */
  @Override
  public IPopulation<? extends IFeature> getPopulation() {
    return this.getFeature().getPopulation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setEstSupprime(boolean)
   */
  @Override
  public void setDeleted(boolean deleted) {
    this.getFeature().setDeleted(deleted);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setGeom(fr.ign.cogit.geoxygene
   * .api.spatial.geomroot.IGeometry)
   */
  @Override
  public void setGeom(IGeometry g) {
    this.getFeature().setGeom(g);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setId(int)
   */
  @Override
  public void setId(int Id) {
    this.getFeature().setId(Id);
  }

}
