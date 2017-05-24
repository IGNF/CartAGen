package fr.ign.cogit.cartagen.agents.gael.field.agent.partition.administrative;

import fr.ign.cogit.cartagen.agents.gael.field.agent.partition.PartitionFieldAgent;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public final class AdministrativeFieldAgent extends PartitionFieldAgent {

  private IGeneObj geneObj = null;

  public IGeneObj getFeature() {
    return this.geneObj;
  }

  public void setFeature(IGeneObj geoObj) {
    geoObj.addToGeneArtifacts(this);
    this.geneObj = geoObj;
  }

  public AdministrativeFieldAgent(IGeneObj field) {
    super();
    this.setFeature(field);
  }

  public void instanciateConstraints() {
  }

}
