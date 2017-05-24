package fr.ign.cogit.cartagen.collagen.geospaces.model;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class EdgeNeighbourhood extends SpaceNeighbourhood {

  @Override
  protected void construireTampon() {
    // on construit une zone tampon grande au-del� de la ligne de bord
    // on prend un seuil de 600 m
    GeographicSpace espace = getEspaces().iterator().next();
    IGeometry buffer = getVoisinage().buffer(600.0, 10);
    IGeometry tampon = buffer.difference(espace.getGeom());

    this.setTampon(tampon);
  }

  public EdgeNeighbourhood(IGeometry voisinage, HashSet<GeographicSpace> espaces) {
    super(voisinage, espaces);
  }

}
