/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.osm.schema.road;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Transient;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PatteOie;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * OpenStreetMap implementation of the {@link IBranchingCrossroad} interface,
 * used when creating a branching crossroad by enrichment in a dataset with an
 * OSM implementation.
 * @author GTouya
 *
 */
public class OsmBranchingCrossroad extends OsmGeneObjSurf
    implements IBranchingCrossroad {

  /**
   * Associated Geoxygene schema object
   */
  private PatteOie geoxObj;
  private Set<INetworkNode> simples;
  private Set<IRoadLine> internalRoads;
  private Set<IRoadLine> externalRoads;
  private Set<IRoadLine> mainRoadIntern;
  private IRoadLine minorRoadExtern;
  private IRoundAbout roundAbout;

  /**
   * Default constructor, used by Hibernate.
   */
  public OsmBranchingCrossroad() {
    super();
  }

  /**
   * Constructor
   */
  public OsmBranchingCrossroad(PatteOie geoxObj, Collection<IRoadLine> roads,
      Collection<IRoadNode> nodes) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.simples = new HashSet<INetworkNode>();
    for (IRoadNode node : nodes) {
      if (geoxObj.getNoeuds().contains(node.getGeoxObj())) {
        this.simples.add(node);
      }
    }
    this.internalRoads = new HashSet<IRoadLine>();
    this.externalRoads = new HashSet<IRoadLine>();
    this.mainRoadIntern = new HashSet<IRoadLine>();
    for (IRoadLine road : roads) {
      if (geoxObj.getRoutesInternes().contains(road.getGeoxObj())) {
        this.internalRoads.add(road);
      }
      if (geoxObj.getRoutesExternes().contains(road.getGeoxObj())) {
        this.externalRoads.add(road);
      }
      if (geoxObj.getMainRoadIntern().contains(road.getGeoxObj())) {
        this.mainRoadIntern.add(road);
      }
      if (road.getGeoxObj().equals(geoxObj.getMinorRoadExtern())) {
        this.minorRoadExtern = road;
      }
    }
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public Set<IRoadLine> getExternalRoads() {
    return this.externalRoads;
  }

  @Override
  @Transient
  public Set<IRoadLine> getInternalRoads() {
    return this.internalRoads;
  }

  @Override
  @Transient
  public Set<IRoadLine> getMainRoadIntern() {
    return this.mainRoadIntern;
  }

  @Override
  @Transient
  public IRoadLine getMinorRoadExtern() {
    return this.minorRoadExtern;
  }

  @Override
  @Transient
  public IRoundAbout getRoundAbout() {
    return this.roundAbout;
  }

  @Override
  public void setExternalRoads(Set<IRoadLine> externalRoads) {
    this.externalRoads = externalRoads;
  }

  @Override
  public void setInternalRoads(Set<IRoadLine> internalRoads) {
    this.internalRoads = internalRoads;
  }

  @Override
  public void setMainRoadIntern(Set<IRoadLine> mainRoadIntern) {
    this.mainRoadIntern = mainRoadIntern;
  }

  @Override
  public void setMinorRoadExtern(IRoadLine minorRoadExtern) {
    this.minorRoadExtern = minorRoadExtern;
  }

  @Override
  public void setRoundAbout(IRoundAbout roundAbout) {
    this.roundAbout = roundAbout;
  }

  @Override
  @Transient
  public Set<INetworkNode> getSimples() {
    return this.simples;
  }

  @Override
  public void setSimples(Set<INetworkNode> simples) {
    this.simples = simples;
  }

}
