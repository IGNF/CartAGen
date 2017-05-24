package fr.ign.cogit.cartagen.collagen.geospaces.model;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public abstract class SpaceNeighbourhood {

  private IGeometry tampon, voisinage;
  private Set<GeographicSpace> espaces;
  private int id;
  private static AtomicInteger counter = new AtomicInteger();

  public IGeometry getTampon() {
    return tampon;
  }

  public void setTampon(IGeometry tampon) {
    this.tampon = tampon;
  }

  public IGeometry getVoisinage() {
    return voisinage;
  }

  public void setVoisinage(IGeometry voisinage) {
    this.voisinage = voisinage;
  }

  public Set<GeographicSpace> getEspaces() {
    return espaces;
  }

  public void setEspaces(Set<GeographicSpace> espaces) {
    this.espaces = espaces;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object arg0) {
    if (arg0 == null)
      return false;
    if (!(arg0 instanceof SpaceNeighbourhood))
      return false;
    SpaceNeighbourhood autre = (SpaceNeighbourhood) arg0;
    if (!autre.espaces.equals(this.espaces))
      return false;
    if (!autre.voisinage.equals(this.voisinage))
      return false;
    return super.equals(arg0);
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return super.toString();
  }

  public SpaceNeighbourhood(IGeometry voisinage, Set<GeographicSpace> espaces) {
    super();
    this.voisinage = voisinage;
    this.espaces = espaces;
    this.id = counter.getAndIncrement();
    construireTampon();
  }

  protected abstract void construireTampon();

}
