package fr.ign.cogit.cartagen.agents.diogen.agent.model;

import java.util.ArrayList;
import java.util.Set;

import fr.ign.cogit.cartagen.agents.cartacom.agent.impl.CartAComAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.diogen.padawan.BorderStrategy;
import fr.ign.cogit.cartagen.agents.diogen.padawan.Environment;
import fr.ign.cogit.cartagen.agents.diogen.padawan.EnvironmentStrategy;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformableImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;

public class CartAComAgentDeformableGeneralisation
    extends CartAComAgentGeneralisation
    implements ICartAComAgentDeformableGeneralisation {

  // GAEL
  public CartAComAgentDeformableGeneralisation(IGeneObj feature) {
    super(feature);
  }

  private GAELDeformable deformable = new GAELDeformableImpl();

  public GAELDeformable getGAELDeformable() {
    return this.deformable;
  }

  @Override
  public void activatePointAgents() throws InterruptedException {
    this.getGAELDeformable().activatePointAgents();
  }

  @Override
  public void activatePointAgents(int nbLimite) throws InterruptedException {
    this.getGAELDeformable().activatePointAgents(nbLimite);
  }

  @Override
  public void ajouterContAngleValeur(double importance, double valeurBut) {
    this.getGAELDeformable().ajouterContAngleValeur(importance, valeurBut);
  }

  @Override
  public void ajouterContAngleValeur(double importance) {
    this.getGAELDeformable().ajouterContAngleValeur(importance);
  }

  @Override
  public void ajouterContPointGarderAltitude(double importance) {
    this.getGAELDeformable().ajouterContPointGarderAltitude(importance);
  }

  @Override
  public void ajouterContPointPosition(double importance) {
    this.getGAELDeformable().ajouterContPointPosition(importance);
  }

  @Override
  public void ajouterContSegmentCouler(double importance) {
    this.getGAELDeformable().ajouterContSegmentCouler(importance);
  }

  @Override
  public void ajouterContSegmentEtrePlat(double importance) {
    this.getGAELDeformable().ajouterContSegmentEtrePlat(importance);
  }

  @Override
  public void ajouterContSegmentLongueur(double importance,
      double longueurBut) {
    this.getGAELDeformable().ajouterContSegmentLongueur(importance,
        longueurBut);
  }

  @Override
  public void ajouterContSegmentLongueur(double importance) {
    this.getGAELDeformable().ajouterContSegmentLongueur(importance);
  }

  @Override
  public void ajouterContSegmentOrientation(double importance,
      double orientationBut) {
    this.getGAELDeformable().ajouterContSegmentOrientation(importance,
        orientationBut);
  }

  @Override
  public void ajouterContSegmentOrientation(double importance) {
    this.getGAELDeformable().ajouterContSegmentOrientation(importance);
  }

  @Override
  public void ajouterContTriangleAire(double importance) {
    this.getGAELDeformable().ajouterContTriangleAire(importance);
  }

  @Override
  public void ajouterContTriangleFaireCouler(double importance) {
    this.getGAELDeformable().ajouterContTriangleFaireCouler(importance);
  }

  @Override
  public void ajouterContTriangleFaireGarderAltitude(double importance) {
    this.getGAELDeformable().ajouterContTriangleFaireGarderAltitude(importance);
  }

  @Override
  public void ajouterContTriangleGarderG(double importance) {
    this.getGAELDeformable().ajouterContTriangleGarderG(importance);
  }

  @Override
  public void ajouterContTriangleGarderOrientationAzimutalePente(
      double importance) {
    this.getGAELDeformable()
        .ajouterContTriangleGarderOrientationAzimutalePente(importance);
  }

  @Override
  public void chargerPointsNonEquilibres() {
    this.getGAELDeformable().chargerPointsNonEquilibres();
  }

  @Override
  public void cleanDecomposition() {
    this.getGAELDeformable().cleanDecomposition();
  }

  @Override
  public void construireSegments(boolean ferme) {
    this.getGAELDeformable().construireSegments(ferme);
  }

  @Override
  public void creerAnglesTriangles() {
    this.getGAELDeformable().creerAnglesTriangles();
  }

  @Override
  public void creerPointsSingletonsAPartirDesPoints() {
    this.getGAELDeformable().creerPointsSingletonsAPartirDesPoints();
  }

  @Override
  public IFeatureCollection<GAELAngle> getAngles() {
    return this.getGAELDeformable().getAngles();
  }

  @Override
  public ArrayList<IPointAgent> getListeAgentsPoints() {
    return this.getGAELDeformable().getListeAgentsPoints();
  }

  @Override
  public IPointAgent getPlusInsatisfait() {
    return this.getGAELDeformable().getPlusInsatisfait();
  }

  @Override
  public IPointAgent getPoint(double x, double y) {
    return this.getGAELDeformable().getPoint(x, y);
  }

  @Override
  public IFeatureCollection<IPointAgent> getPointAgents() {
    return this.getGAELDeformable().getPointAgents();
  }

  @Override
  public IFeatureCollection<GAELPointSingleton> getPointSingletons() {
    return this.getGAELDeformable().getPointSingletons();
  }

  @Override
  public GAELSegment getSegment(IPointAgent p1, IPointAgent p2) {
    return this.getGAELDeformable().getSegment(p1, p2);
  }

  @Override
  public IFeatureCollection<GAELSegment> getSegments() {
    return this.getGAELDeformable().getSegments();
  }

  @Override
  public GAELTriangle getTriangle(IDirectPosition pos) {
    return this.getGAELDeformable().getTriangle(pos);
  }

  @Override
  public GAELTriangle getTriangleInitial(IDirectPosition pos) {
    return this.getGAELDeformable().getTriangleInitial(pos);
  }

  @Override
  public IFeatureCollection<GAELTriangle> getTriangles() {
    return this.getGAELDeformable().getTriangles();
  }

  @Override
  public void supprimerContraintesSubmicro() {
    this.getGAELDeformable().supprimerContraintesSubmicro();
  }

  @Override
  public void triangule() {
    this.getGAELDeformable().triangule();
  }

  @Override
  public void triangule(boolean construireTriangles, IGeometry geom) {
    this.getGAELDeformable().triangule(construireTriangles, geom);
  }

  @Override
  public void registerDisplacement() {
    deformable.registerDisplacement();

  }

  @Override
  public Set<Environment> getBorderedEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderedEnvironments(Set<Environment> borderedEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeBorderedEnvironment(Environment borderedEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public BorderStrategy getBorderStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBorderStrategy(BorderStrategy borderStrategy) {
    // TODO Auto-generated method stub

  }

  @Override
  public AgentSatisfactionState activate(Environment environment)
      throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Environment getEncapsulatedEnv() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEncapsulatedEnv(Environment encapsulatedEnv) {
    // TODO Auto-generated method stub

  }

  @Override
  public Set<Environment> getContainingEnvironments() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addContainingEnvironments(Environment containingEnvironment) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setContainingEnvironments(
      Set<Environment> containingEnvironments) {
    // TODO Auto-generated method stub

  }

  @Override
  public EnvironmentStrategy getEnvironmentStrategy() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setEnvironmentStrategy(EnvironmentStrategy environmentStrategy) {
    // TODO Auto-generated method stub

  }

}
