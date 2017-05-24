package fr.ign.cogit.cartagen.agents.core.constraint.block;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.core.agent.GeographicObjectAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicObjectConstraintImpl;

/**
 * Contrainte qui incite a preserver la repartition spatiale entre batiments
 * d'un ilot la repartition est calculée de la façon suivante:
 * 
 * @author patrick Taillandier 5 févr. 2009
 * 
 */
public class BuildingsSpatialDistribution
    extends GeographicObjectConstraintImpl {

  // Taille du buffer autour des bâtiments
  /**
   */
  private final double tailleBuffer = GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT
      * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
  /**
   */
  private Set<IGeometry> buffersBati;
  /**
   */
  private Set<IGeometry> batiInit = null;

  public BuildingsSpatialDistribution(GeographicObjectAgentGeneralisation agent,
      double importance) {
    super(agent, importance);
  }

  @Override
  public void computePriority() {
  }

  @Override
  public void computeSatisfaction() {
    if (this.batiInit == null) {
      this.computeGoalValue();
    }

    this.computeCurrentValue();
    int nbBatiNonSup = 0;
    for (IGeometry geomBati : this.batiInit) {
      boolean nonSup = true;
      for (IGeometry geomBuffer : this.buffersBati) {
        if (geomBati.intersects(geomBuffer)) {
          nonSup = false;
          break;
        }
      }
      if (nonSup) {
        nbBatiNonSup++;
      }
    }
    double propNonSup = nbBatiNonSup * 100 / (this.batiInit.size() + 0.0);
    if (propNonSup < 5) {
      this.setSatisfaction(100);
    } else {
      this.setSatisfaction(Math.max((int) (100 - 2 * propNonSup), 1));
    }
  }

  @Override
  public void computeGoalValue() {
    this.batiInit = new HashSet<IGeometry>();
    BlockAgent ai = (BlockAgent) this.getAgent();
    for (GeographicObjectAgent ago : ai.getComponents()) {
      this.batiInit.add(ago.getInitialGeom());
    }
  }

  @Override
  public void computeCurrentValue() {
    // On place dans la valeur courante l'ensemble des buffer créés au niveau de
    // chaque bâtiments
    this.buffersBati = new HashSet<IGeometry>();
    BlockAgent ai = (BlockAgent) this.getAgent();
    for (GeographicObjectAgent ago : ai.getComponents()) {
      if (!ago.isDeleted()) {
        this.buffersBati.add(ago.getGeom().buffer(this.tailleBuffer));
      }
    }

  }

  @Override
  public Set<ActionProposal> getActions() {
    return null;
  }

}
