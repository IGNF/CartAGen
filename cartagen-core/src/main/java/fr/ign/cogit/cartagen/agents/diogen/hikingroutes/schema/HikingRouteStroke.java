package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes.RoadStrokeForRoutes;
import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes.TronconDeRouteItineraireImpl;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.defaultschema.network.NetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarriedObject;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.persistence.CollectionType;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseauFlagPair;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauFlagPairImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.NoeudReseauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

@Entity
@Access(AccessType.PROPERTY)
public class HikingRouteStroke extends NetworkSection
    implements IHikingRouteStroke {

  @Transient
  private RoadStrokeForRoutes roadStrokeForRoute;

  @Transient
  public RoadStrokeForRoutes getRoadStrokeForRoute() {
    return roadStrokeForRoute;
  }

  public void setRoadStrokeForRoute(RoadStrokeForRoutes roadStrokeForRoute) {
    this.roadStrokeForRoute = roadStrokeForRoute;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    if (roadStrokeForRoute != null) {
      if (roadStrokeForRoute.getGeom() != null)
        return (ILineString) roadStrokeForRoute.getGeom();
      if (roadStrokeForRoute.getGeomStroke() != null)
        return roadStrokeForRoute.getGeomStroke();
    }
    return super.getGeom();
  }

  @Override
  public void setGeom(IGeometry g) {
    super.setGeom(g);
  }

  @Transient
  private Set<ICarryingRoadLine> roads = new HashSet<ICarryingRoadLine>();

  @Transient
  public Set<ICarryingRoadLine> getRoads() {
    return roads;
  }

  public void setRoads(Set<ICarryingRoadLine> roads) {
    this.roads = roads;
  }

  @Transient
  private ArrayList<ArcReseauFlagPair> arcReseau;

  @Transient
  private Reseau reseau = new ReseauImpl();

  public HikingRouteStroke() {
    super();
  }

  public HikingRouteStroke(RoadStrokeForRoutes roadStrokeForRoute) {
    super();
    this.roadStrokeForRoute = roadStrokeForRoute;
    // this.arcReseau = roadStrokeForRoute.getFeatures();
    arcReseau = new ArrayList<>();
    for (ArcReseauFlagPair feature : roadStrokeForRoute.getOrientedFeatures()) {
      arcReseau.add((ArcReseauFlagPair) feature);
      roads.add(((TronconDeRouteItineraireImpl) (feature.getArcReseau()))
          .getRoadSection());
      ((TronconDeRouteItineraireImpl) (feature.getArcReseau())).getRoadSection()
          .setRoadStrokeForRoute(this);
    }
  }

  @Transient
  private void computeArcReseau() {

    ArrayList<ArcReseauFlagPair> arcsSet = new ArrayList<ArcReseauFlagPair>();
    Set<NoeudReseau> noeuds = new HashSet<NoeudReseau>();
    for (ICarryingRoadLine roadSection : this.roads) {
      roadSection.setRoadStrokeForRoute(this);
      ArcReseau arcToAdd = new TronconDeRouteItineraireImpl(reseau, false,
          roadSection);
      NoeudReseau noeudIni = null;
      NoeudReseau noeudFin = null;
      for (NoeudReseau noeudReseau : noeuds) {

        if (noeudIni == null && noeudReseau.getGeom()
            .equals((roadSection.getInitialNode()).getGeom())) {
          noeudIni = noeudReseau;
        }
        if (noeudFin == null && noeudReseau.getGeom()
            .equals((roadSection.getFinalNode()).getGeom())) {
          noeudFin = noeudReseau;
        }
        if (noeudFin != null && noeudIni != null)
          break;
      }
      if (noeudIni == null) {
        noeudIni = new NoeudReseauImpl();
        noeudIni.setGeom(roadSection.getInitialNode().getGeom());
        noeuds.add(noeudIni);
      }
      if (noeudFin == null) {
        noeudFin = new NoeudReseauImpl();
        noeudFin.setGeom(roadSection.getFinalNode().getGeom());
        noeuds.add(noeudFin);
      }
      noeudIni.getArcsSortants().add(arcToAdd);
      arcToAdd.setNoeudInitial(noeudIni);
      noeudFin.getArcsEntrants().add(arcToAdd);
      arcToAdd.setNoeudFinal(noeudFin);
      arcsSet.add(new ArcReseauFlagPairImpl(arcToAdd));
    }
    this.arcReseau = arcsSet;
  }

  @Override
  public boolean isEliminated() {
    return super.isEliminated();
  }

  @Override
  @Id
  public int getId() {
    return super.getId();
  }

  private List<Integer> roadStrokesForRouteRoutesIds = new ArrayList<Integer>();

  public void setRoadStrokesForRouteRoutesIds(
      List<Integer> roadStrokesForRouteRoutesIds) {
    this.roadStrokesForRouteRoutesIds = roadStrokesForRouteRoutesIds;
  }

  @ElementCollection
  @CollectionTable(name = "RoadStrokesForRouteRoutesIds", joinColumns = @JoinColumn(name = "roadStrokeForRoute"))
  @Column(name = "RoadStrokesForRouteRoutesIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntity = ICarryingRoadLine.class, invClass = HikingRouteStroke.class, methodName = "Roads", invMethodName = "RoadStrokeForRoute", nToM = false, collectionType = CollectionType.SET)
  public List<Integer> getRoadStrokesForRouteRoutesIds() {
    return this.roadStrokesForRouteRoutesIds;
  }

  @Override
  public void restoreGeoxRelations() {
    computeArcReseau();
    if (this.getGeom() != null && arcReseau != null && !arcReseau.isEmpty()) {
      HikingDataset dataset = (HikingDataset) CartAGenDoc.getInstance()
          .getCurrentDataset();
      roadStrokeForRoute = new RoadStrokeForRoutes(
          dataset.getRouteStrokesNetwork(), arcReseau, this.getGeom());
      roadStrokeForRoute.instantiateFlagsOfArcReseau();
    }
  }

  @Override
  @Transient
  public void addCarriedObject(ICarriedObject route) {
    // TODO Auto-generated method stub
  }

  @Override
  @Transient
  public Collection<ICarriedObject> getCarriedObjects() {
    if (roads.isEmpty())
      return null;
    return roads.iterator().next().getCarriedObjects();
  }

  @Override
  @Transient
  public double distance(boolean left) {
    if (roads.isEmpty())
      return 0.0;
    ICarryingRoadLine road = ((TronconDeRouteItineraireImpl) arcReseau.get(0)
        .getArcReseau()).getRoadSection();
    if (!arcReseau.get(0).getFlag()) {
      left = !left;
    }
    // System.out.println("road.distance(left): " + road.distance(left));
    return road.distance(left);
  }

  @Override
  @Transient
  public double maxWidth() {
    if (roads.isEmpty())
      return 0.0;
    return roads.iterator().next().maxWidth();
  }

  @Override
  @Transient
  public double getWidth() {
    if (roads.isEmpty())
      return 0.0;
    return roads.iterator().next().getWidth();
  }

  @Override
  public double getInternWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Direction getDirection() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setDirection(Direction direction) {
    // TODO Auto-generated method stub

  }

  @Override
  public INetworkNode getInitialNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    // TODO Auto-generated method stub

  }

  @Override
  public INetworkNode getFinalNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isDeadEnd() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    // TODO Auto-generated method stub

  }
}
