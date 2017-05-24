package fr.ign.cogit.cartagen.collagen.geospaces.model;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class IntersectionNeighbourhood extends SpaceNeighbourhood {

  @Override
  protected void construireTampon() {
    // on construit une zone tampon petite autour de la zone d'intersection
    // on prend un seuil de 150 m
    IGeometry tampon = getVoisinage().buffer(150.0, 10);
    this.setTampon(tampon);
  }

  public IntersectionNeighbourhood(IGeometry voisinage,
      HashSet<GeographicSpace> espaces) {
    super(voisinage, espaces);
  }
}
