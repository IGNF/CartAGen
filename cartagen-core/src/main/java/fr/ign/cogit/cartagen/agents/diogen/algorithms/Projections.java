package fr.ign.cogit.cartagen.agents.diogen.algorithms;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;

public class Projections {

  public static int projectAndInsertWithPositionOutside(IDirectPosition point,
      List<IDirectPosition> points) {
    // System.out.println("1 points.size() " + points.size());
    int res = Operateurs.projectAndInsertWithPosition(point, points);
    // System.out.println("2 points.size() " + points.size());
    // GeometryPool geomPool = CartAGenDoc.getInstance().getCurrentDataset()
    // .getGeometryPool();
    if (points.get(0).equals(points.get(1))) {

      // geomPool.addFeatureToGeometryPool(points.get(1).toGM_Point(),
      // Color.RED,
      // 2);
      //
      // geomPool.addFeatureToGeometryPool(points.get(2).toGM_Point(),
      // Color.BLUE,
      // 2);
      //
      // geomPool.addFeatureToGeometryPool(point.toGM_Point(), Color.GREEN, 2);

      Vecteur ab = new Vecteur(points.get(1), points.get(2));
      Vecteur u_ab = ab.vectNorme();
      Vecteur am = new Vecteur(points.get(1), point);
      double lambda = am.prodScalaire(u_ab);
      points.remove(0);
      points.add(0,
          Operateurs.translate(points.get(0), u_ab.multConstante(lambda)));

      res = 0;

      // geomPool.addFeatureToGeometryPool(
      // Operateurs.translate(points.get(0), u_ab.multConstante(lambda))
      // .toGM_Point(), Color.MAGENTA, 2);

    } else if (points.get(points.size() - 1).equals(
        points.get(points.size() - 2))) {

      // geomPool.addFeatureToGeometryPool(points.get(points.size() - 3)
      // .toGM_Point(), Color.RED, 2);
      //
      // geomPool.addFeatureToGeometryPool(points.get(points.size() - 2)
      // .toGM_Point(), Color.BLUE, 2);
      //
      // geomPool.addFeatureToGeometryPool(point.toGM_Point(), Color.GREEN, 2);

      Vecteur ab = new Vecteur(points.get(points.size() - 3), points.get(points
          .size() - 2));

      Vecteur u_ab = ab.vectNorme();
      Vecteur am = new Vecteur(points.get(points.size() - 3), point);
      double lambda = am.prodScalaire(u_ab);
      points.remove(points.size() - 2);
      points.add(Operateurs.translate(points.get(points.size() - 2),
          u_ab.multConstante(lambda)));
      res = points.size() - 1;

      // geomPool.addFeatureToGeometryPool(
      // Operateurs.translate(points.get(points.size() - 2),
      // u_ab.multConstante(lambda)).toGM_Point(), Color.MAGENTA, 2);

    }

    return res;
  }

  public static IDirectPosition pointEnAbscisseCurviligneOutside(
      ILineString ls, double abscisse) {

    IDirectPosition res = Operateurs.pointEnAbscisseCurviligne(ls, abscisse);

    if (res == null) {
      if (abscisse > ls.length()) {
        Vecteur ab = new Vecteur(ls.coord().get(ls.coord().size() - 2), ls
            .coord().get(ls.coord().size() - 1));
        Vecteur u_ab = ab.vectNorme();
        return Operateurs.translate(ls.coord().get(ls.coord().size() - 1),
            u_ab.multConstante(abscisse - ls.length()));
      }

      if (abscisse < 0) {
        Vecteur ab = new Vecteur(ls.coord().get(0), ls.coord().get(1));
        Vecteur u_ab = ab.vectNorme();
        return Operateurs.translate(ls.coord().get(0),
            u_ab.multConstante(abscisse));
      }
    }
    return res;
  }

  public static IDirectPosition projectionOutside(IDirectPosition dp,
      ILineString ls) {

    IDirectPosition res = Operateurs.projection(dp, ls);

    if (res.equals(ls.coord().get(0))) {
      Vecteur ab = new Vecteur(ls.coord().get(0), ls.coord().get(1));
      Vecteur u_ab = ab.vectNorme();
      Vecteur am = new Vecteur(ls.coord().get(0), dp);
      double lambda = am.prodScalaire(u_ab);
      return Operateurs
          .translate(ls.coord().get(0), u_ab.multConstante(lambda));
    }

    if (res.equals(ls.coord().get(ls.coord().size() - 1))) {
      Vecteur ab = new Vecteur(ls.coord().get(ls.coord().size() - 2), ls
          .coord().get(ls.coord().size() - 1));
      Vecteur u_ab = ab.vectNorme();
      Vecteur am = new Vecteur(ls.coord().get(ls.coord().size() - 2), dp);
      double lambda = am.prodScalaire(u_ab);
      return Operateurs.translate(ls.coord().get(ls.coord().size() - 2),
          u_ab.multConstante(lambda));
    }

    return res;
  }

}
