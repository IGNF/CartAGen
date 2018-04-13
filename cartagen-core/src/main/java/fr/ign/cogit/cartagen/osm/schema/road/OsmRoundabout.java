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

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjSurf;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PatteOie;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;

/**
 * OpenStreetMap implementation of the {@link IRoundAbout} interface.
 * @author GTouya
 *
 */
public class OsmRoundabout extends OsmGeneObjSurf implements IRoundAbout {

  /**
   * Associated Geoxygene schema object
   */
  private RondPoint geoxObj;
  private Set<INetworkNode> simples;
  private Set<IRoadLine> internalRoads;
  private Set<IRoadLine> externalRoads;
  private double diameter;
  private int nbLegs;
  private HashSet<IBranchingCrossroad> branchings;

  /**
   * Default constructor, used by Hibernate.
   */
  public OsmRoundabout() {
    super();
  }

  /**
   * Constructor
   */
  public OsmRoundabout(RondPoint geoxObj, Collection<IRoadLine> roads,
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
    for (IRoadLine road : roads) {
      if (geoxObj.getRoutesInternes().contains(road.getGeoxObj())) {
        this.internalRoads.add(road);
      }
      if (geoxObj.getRoutesExternes().contains(road.getGeoxObj())) {
        this.externalRoads.add(road);
      }
    }
    this.diameter = geoxObj.getDiameter();
    this.nbLegs = geoxObj.getNbLegs();
    this.branchings = new HashSet<IBranchingCrossroad>();
  }

  /**
   * Constructor from a geometry, sets of internal and external roads, and a set
   * of road nodes. Warning this constructor does not instantiate the reference
   * to adjacent branching crossroads: it is left null and should be
   * instantiated separately later (and the same on the associated Geoxygen
   * RondPoint feature created inside this constructor).
   * @param geom Geometry of the roundabout
   * @param externalRoads Collection of the roads connected to the roundabout
   * @param internalRoads Collection of the roads internal to the roundabout
   * @param initialNodes Collection of the nodes internal to the roundabout
   */
  public OsmRoundabout(IPolygon geom, Collection<IRoadLine> externalRoads,
      Collection<IRoadLine> internalRoads,
      Collection<INetworkNode> initialNodes) {

    super();

    // Retrieve the sets of GeOxygen (not CartAGen) internal and external roads
    // from the input sets.
    HashSet<TronconDeRoute> geoxExternalRoads = new HashSet<TronconDeRoute>();
    for (IRoadLine roadLine : externalRoads) {
      geoxExternalRoads.add((TronconDeRoute) roadLine.getGeoxObj());
    }
    HashSet<TronconDeRoute> geoxInternalRoads = new HashSet<TronconDeRoute>();
    for (IRoadLine roadLine : internalRoads) {
      geoxInternalRoads.add((TronconDeRoute) roadLine.getGeoxObj());
    }
    // Same thing for road nodes
    Set<NoeudReseau> geoxNodes = new HashSet<NoeudReseau>();
    for (INetworkNode roadNode : initialNodes) {
      geoxNodes.add((NoeudReseau) roadNode.getGeoxObj());
    }
    // Construct the Geoxygen RondPoint associated to <this>
    RondPoint rondPoint = new RondPoint(geom, geoxNodes, geoxInternalRoads,
        geoxExternalRoads, new HashSet<PatteOie>());

    // Now fill in the attributes
    this.geoxObj = rondPoint;
    this.setInitialGeom(rondPoint.getGeom());
    this.setEliminated(false);
    this.simples = new HashSet<INetworkNode>(initialNodes);
    this.externalRoads = new HashSet<IRoadLine>(externalRoads);
    this.internalRoads = new HashSet<IRoadLine>(internalRoads);
    this.diameter = rondPoint.getDiameter();
    this.nbLegs = this.geoxObj.getNbLegs();
    this.branchings = new HashSet<IBranchingCrossroad>();
  }

  @Override
  public Set<IRoadLine> getInternalRoads() {
    return this.internalRoads;
  }

  @Override
  public void setInternalRoads(Set<IRoadLine> internalRoads) {
    this.internalRoads = internalRoads;
  }

  @Override
  public Set<IRoadLine> getExternalRoads() {
    return this.externalRoads;
  }

  @Override
  public void setExternalRoads(Set<IRoadLine> externalRoads) {
    this.externalRoads = externalRoads;
  }

  @Override
  public Set<INetworkNode> getSimples() {
    return this.simples;
  }

  @Override
  public void setSimples(Set<INetworkNode> simples) {
    this.simples = simples;
  }

  @Override
  public Set<IBranchingCrossroad> getBranchings() {
    return this.branchings;
  }

  @Override
  public double getDiameter() {
    return diameter;
  }

  public void setNbLegs(int nbLegs) {
    this.nbLegs = nbLegs;
  }

  /**
   * Get the number of legs of the roundabout, i.e. the number of external
   * roads.
   * @return
   */
  public int getNbLegs() {
    return this.nbLegs;
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
