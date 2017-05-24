package fr.ign.cogit.cartagen.collagen.geospaces.model;

import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class AdjacencyNeighbourhood extends SpaceNeighbourhood {

  @Override
  protected void construireTampon() {
    // on construit une zone tampon moyenne autour de la ligne d'adjacence
    // on prend un seuil de 300 m
    IGeometry tampon = getVoisinage().buffer(300.0, 10);
    this.setTampon(tampon);
  }

  public AdjacencyNeighbourhood(IGeometry voisinage,
      Set<GeographicSpace> espaces) {
    super(voisinage, espaces);
  }
}
