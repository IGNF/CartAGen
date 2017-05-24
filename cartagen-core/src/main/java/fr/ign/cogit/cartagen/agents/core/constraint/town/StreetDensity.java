package fr.ign.cogit.cartagen.agents.core.constraint.town;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.agents.core.agent.ITownAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork.CitySize;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

public class StreetDensity extends GeographicObjectConstraintImpl {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private double totalArea, deadEndArea, deadEndDensity, streetDensity;
  private double deadEndMinLength;
  private double roadGoal;
  private AtomicInteger counter = new AtomicInteger(0);

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public StreetDensity(IGeographicAgent agent, double importance) {
    super(agent, importance);
    this.deadEndMinLength = GeneralisationSpecifications.ROADS_DEADEND_MIN_LENGTH
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    this.computeGoalValue();
    this.computeCurrentValue();
    this.computeSatisfaction();
  }

  // Getters and setters //
  public double getTotalArea() {
    return this.totalArea;
  }

  public void setTotalArea(double totalArea) {
    this.totalArea = totalArea;
  }

  public double getDeadEndArea() {
    return this.deadEndArea;
  }

  public void setDeadEndArea(double deadEndArea) {
    this.deadEndArea = deadEndArea;
  }

  public double getDeadEndDensity() {
    return this.deadEndDensity;
  }

  public void setDeadEndDensity(double deadEndDensity) {
    this.deadEndDensity = deadEndDensity;
  }

  public double getStreetDensity() {
    return this.streetDensity;
  }

  public void setStreetDensity(double streetDensity) {
    this.streetDensity = streetDensity;
  }

  public void setRoadGoal(double roadGoal) {
    this.roadGoal = roadGoal;
  }

  public double getRoadGoal() {
    return this.roadGoal;
  }

  // Other public methods //

  @Override
  public void computeCurrentValue() {
    this.totalArea = 0.0;
    this.deadEndArea = 0.0;
    for (IRoadLine obj : ((ITownAgent) this.getAgent()).getStreetNetwork()
        .getRoads()) {
      if (obj.isEliminated()) {
        continue;
      }
      this.totalArea += SectionSymbol.getSymbolExtent(obj).area();
    }
    for (DeadEndGroup deg : ((ITownAgent) this.getAgent()).getDeadEnds()) {
      for (INetworkSection leaf : deg.getLeafs()) {
        if (leaf.isEliminated()) {
          continue;
        }
        if (leaf.getGeom().length() < this.deadEndMinLength) {
          this.deadEndArea += SectionSymbol.getSymbolExtent(leaf).area();
        }
      }
    }
    this.streetDensity = this.totalArea / this.getAgent().getGeom().area();
    this.deadEndDensity = this.deadEndArea / this.getAgent().getGeom().area();
  }

  @Override
  public void computeSatisfaction() {
    this.computeCurrentValue();
    double diffRoad = this.streetDensity - this.getRoadGoal();
    if (diffRoad < 0.0) {
      diffRoad = 0.05;
    }
    int s = 100 - (int) (100 * (diffRoad + 10.0 * this.deadEndDensity));
    if (s < 0) {
      this.setSatisfaction(0);
    } else {
      this.setSatisfaction(s);
    }
  }

  @Override
  public void computeGoalValue() {
    double townFactor = 1.0;
    StreetNetwork net = ((ITownAgent) this.getAgent()).getStreetNetwork();
    if (net.getSize().equals(CitySize.MEDIUM)) {
      townFactor = 0.9;
    } else if (net.getSize().equals(CitySize.SMALL)) {
      townFactor = 0.8;
    }

    double area = 0.0;
    for (IRoadLine obj : ((ITownAgent) this.getAgent()).getStreetNetwork()
        .getRoads()) {
      if (obj.isEliminated()) {
        continue;
      }
      area += SectionSymbol.getSymbolExtentAtScale(obj, 15000.0).area();
    }
    this.setRoadGoal(area / this.getAgent().getGeom().area() * townFactor);
  }

  @Override
  public void computePriority() {
    this.setPriority(10);
  }

  @Override
  public Set<ActionProposal> getActions() {
    Set<ActionProposal> actionProposals = new HashSet<ActionProposal>();

    // hack to propose the action only once (because the algorithm doesn't
    // select more than the first application when it's triggered several
    // times).
    int nb = counter.getAndIncrement();
    if (nb < 1) {
      /*
       * Action actionToPropose = new StreetSelectionRuas(this.getAgent(), this,
       * 1.0, ((ITownAgent) this.getAgent()).getStreetNetwork(),
       * this.deadEndMinLength, ((ITownAgent) this.getAgent()).getDeadEnds());
       * actionProposals.add(new ActionProposal(this, true, actionToPropose,
       * 1.0));
       */
    }
    return actionProposals;
  }
  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
