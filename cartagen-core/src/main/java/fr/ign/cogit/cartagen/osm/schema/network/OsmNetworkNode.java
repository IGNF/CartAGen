package fr.ign.cogit.cartagen.osm.schema.network;

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObjPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;

/*
 * ###### IGN / CartAGen ###### Title: WaterPoint Description: Noeuds de réseau
 * Author: J. Renard Date: 18/09/2009
 */

public abstract class OsmNetworkNode extends OsmGeneObjPoint
    implements INetworkNode {

  private Noeud noeud;

  public Noeud getNoeud() {
    return this.noeud;
  }

  public void setNoeud(Noeud noeud) {
    this.noeud = noeud;
  }

  private Collection<INetworkSection> inSections, outSections;

  public OsmNetworkNode(Noeud n) {
    super();
    this.noeud = n;
    this.setInitialGeom(this.noeud.getGeom());
    this.setEliminated(false);

    // Topology links for entering network sections
    this.inSections = new HashSet<INetworkSection>();
    for (Arc arc : n.getEntrants()) {
      IFeature feat = arc.getCorrespondant(0);
      if (!(feat instanceof INetworkSection)) {
        continue;
      }
      INetworkSection section = (INetworkSection) feat;
      this.inSections.add(section);
      section.setFinalNode(this);
    }

    // Topology links for exiting network sections
    this.outSections = new HashSet<INetworkSection>();
    for (Arc arc : n.getSortants()) {
      IFeature feat = arc.getCorrespondant(0);
      if (!(feat instanceof INetworkSection)) {
        continue;
      }
      INetworkSection section = (INetworkSection) feat;
      this.outSections.add(section);
      section.setInitialNode(this);
    }

  }

  public OsmNetworkNode() {
    super();
  }

  @Override
  public IFeature getGeoxObj() {
    return this.noeud;
  }

  @Override
  public double getWidth() {
    return 0.0;
  }

  @Override
  public IDirectPosition getPosition() {
    return this.getGeom().coord().get(0);
  }

  @Override
  public Collection<INetworkSection> getInSections() {
    return this.inSections;
  }

  @Override
  public void setInSections(Collection<INetworkSection> inSections) {
    this.inSections = inSections;
  }

  @Override
  public Collection<INetworkSection> getOutSections() {
    return this.outSections;
  }

  @Override
  public void setOutSections(Collection<INetworkSection> outSections) {
    this.outSections = outSections;
  }

  /**
   * @return le degre du carrefour, c'est a dire le nombre de troncons non
   *         supprimes arrivant ou partant de ce carrefour.
   */
  @Override
  public int getDegree() {
    int nb = 0;
    for (INetworkSection tr : this.inSections) {
      if (!tr.isDeleted()) {
        nb++;
      }
    }
    for (INetworkSection tr : this.outSections) {
      if (!tr.isDeleted()) {
        nb++;
      }
    }
    return nb;
  }

  /**
   * @return the maximum importance of all sections connected to the node
   */
  @Override
  public int getSectionsMaxImportance() {
    int importance = 0;
    for (INetworkSection tr : this.inSections) {
      if (tr.isDeleted()) {
        continue;
      }
      if (tr.getImportance() > importance) {
        importance = tr.getImportance();
      }
    }
    for (INetworkSection tr : this.outSections) {
      if (tr.isDeleted()) {
        continue;
      }
      if (tr.getImportance() > importance) {
        importance = tr.getImportance();
      }
    }
    return importance;
  }

}
