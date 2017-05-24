package fr.ign.cogit.cartagen.agents.gael.deformation;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Interface of an object deformable using GAEL. Such object can be decomposed
 * into small parts (the so-called submicro objects) These parts can be
 * constrained The points composing the object can then displace to balance
 * these constraints
 * @author JGaffuri
 */
public interface GAELDeformable {

  /**
   * @return The point agents composing the object.
   */
  public abstract IFeatureCollection<IPointAgent> getPointAgents();

  /**
   * @return The points singletons composing the object.
   */
  public abstract IFeatureCollection<GAELPointSingleton> getPointSingletons();

  /**
   * @return The segments composing the object.
   */
  public abstract IFeatureCollection<GAELSegment> getSegments();

  /**
   * @return The triangles composing the object.
   */
  public abstract IFeatureCollection<GAELTriangle> getTriangles();

  /**
   * @return The angles composing the object.
   */
  public abstract IFeatureCollection<GAELAngle> getAngles();

  public abstract ArrayList<IPointAgent> getListeAgentsPoints();

  public abstract void cleanDecomposition();

  /**
   * gere l'activation des agents point de la liste. la liste doit avoir ete
   * initialisee au prealable avec les agents point a activer initialement
   * @throws InterruptedException
   */
  public abstract void activatePointAgents() throws InterruptedException;

  /**
   * gere l'activation des agents point de la liste. la liste doit avoir ete
   * initialisee au prealable avec les agents point a activer initialement
   * @param nbLimite
   * @throws InterruptedException
   */
  public abstract void activatePointAgents(int nbLimite)
      throws InterruptedException;

  // parmi les agents-point de l'agent geo, retourne la plus insatisfait
  /**
   */
  public abstract IPointAgent getPlusInsatisfait();

  public abstract void chargerPointsNonEquilibres();

  // contraintes sur les points
  public abstract void ajouterContPointPosition(double importance);

  public abstract void ajouterContPointGarderAltitude(double importance);

  // contraintes sur les segments
  public abstract void ajouterContSegmentLongueur(double importance);

  public abstract void ajouterContSegmentLongueur(double importance,
      double longueurBut);

  public abstract void ajouterContSegmentOrientation(double importance);

  public abstract void ajouterContSegmentOrientation(double importance,
      double orientationBut);

  public abstract void ajouterContSegmentCouler(double importance);

  public abstract void ajouterContSegmentEtrePlat(double importance);

  // contraintes sur les angles
  public abstract void ajouterContAngleValeur(double importance,
      double valeurBut);

  public abstract void ajouterContAngleValeur(double importance);

  // contraintes sur les triangles
  public abstract void ajouterContTriangleAire(double importance);

  public abstract void ajouterContTriangleGarderG(double importance);

  public abstract void ajouterContTriangleGarderOrientationAzimutalePente(
      double importance);

  public abstract void ajouterContTriangleFaireCouler(double importance);

  public abstract void ajouterContTriangleFaireGarderAltitude(
      double importance);

  // supprime toutes les contraintes submicro
  public abstract void supprimerContraintesSubmicro();

  /**
   * renvoit un triangle contenant (x,y) utilise un index spatial sur la liste
   * de triangles si elle a ete cree
   * @param x
   * @param y
   * @return
   */
  public abstract GAELTriangle getTriangle(IDirectPosition pos);

  /**
   * renvoit l'enventuel segment liant p1 et p2
   * @param p1
   * @param p2
   * @return
   */
  public abstract GAELSegment getSegment(IPointAgent p1, IPointAgent p2);

  /**
   * @param x
   * @param y
   * @return
   */
  public abstract GAELTriangle getTriangleInitial(IDirectPosition pos);

  public abstract void creerPointsSingletonsAPartirDesPoints();

  public abstract void creerAnglesTriangles();

  /**
   * effectue une triangulation des agents point de l'agent
   * @param construireTriangles
   */
  public abstract void triangule(boolean construireTriangles, IGeometry geom);

  public abstract void triangule();

  /**
   * parmi les points de l'objet, renvoit celui qui lui apparient eventuellement
   * en une certaine position, null sinon
   * @param x
   * @param y
   * @return
   */
  public abstract IPointAgent getPoint(double x, double y);

  /**
   * construit les segments à partir des agents points
   * @param ferme
   */
  public abstract void construireSegments(boolean ferme);

  public void registerDisplacement();

}
