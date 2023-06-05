package fr.ign.cogit.cartagen.agents.core.agent;

import java.util.ArrayList;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformable;
import fr.ign.cogit.cartagen.agents.gael.deformation.GAELDeformableImpl;
import fr.ign.cogit.cartagen.agents.gael.deformation.IPointAgent;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELAngle;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELPointSingleton;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELSegment;
import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.GAELTriangle;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * A geographic agent for generalisation
 * 
 * @author JGaffuri
 * 
 */
public abstract class GeographicAgentGeneralisation extends GeographicAgent
    implements GAELDeformable {

  private static Logger logger = LogManager
      .getLogger(GeographicAgentGeneralisation.class.getName());

  // GAEL

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

  /**
   * retrieves the previous state name of a given state name (by earsing the end
   * of the name)
   * @param etatCourantNom
   * @return
   */
  public static String etatPrecNom(String etatCourantNom) {
    String[] nomEtatSplit = etatCourantNom.split("-");
    String nomEtat = nomEtatSplit[0];
    for (int i = 1; i < nomEtatSplit.length - 1; i++) {
      nomEtat += "-" + nomEtatSplit[i];
    }
    return nomEtat;
  }

  /**
   * Renvoie le nom du nouvel etat. Etat init : 1, plus ses successeurs 1-1,
   * 1-2, ..., leurs successeurs 1-1-1, 1-1-2,..., 1-2-1
   * @param etatCourant
   * @param nomEtats
   * @return
   */
  public static String nomNouvelEtat(String etatCourant, Set<String> nomEtats) {
    int cpt = 1;
    while (nomEtats.contains(etatCourant + "-" + cpt)) {
      cpt++;
    }
    return etatCourant + "-" + cpt;
  }

  /**
   * compute the satisfaction of a state (used with the knowledge based
   * lifecycle)
   * 
   * @param etat
   */
  public void calculerSatisfaction(AgentState etat) {

    int nb = this.getConstraints().size();
    if (GeographicAgentGeneralisation.logger.isDebugEnabled()) {
      GeographicAgentGeneralisation.logger
          .debug("calcul de satisfaction de l'agent " + this
              + " (nb contraintes=" + nb + ")");
    }

    // si l'agent n'a pas de contrainte ou qu'il est supprime, il est
    // parfaitement satisfait
    if (nb == 0 || this instanceof GeographicObjectAgent
        && ((GeographicObjectAgentGeneralisation) this).isDeleted()) {
      if (GeographicAgentGeneralisation.logger.isDebugEnabled()) {
        GeographicAgentGeneralisation.logger.debug("   S=100");
      }

      this.setSatisfaction(100.0);
      etat.setSatisfaction(100);

      // stocke les valeurs de satisfaction des contraintes
      for (Constraint cont : this.getConstraints()) {
        etat.getValeursMesures().put(cont.getClass().getSimpleName(),
            new Double(100));
      }
      return;
    }

    // calcul de la moyenne des satisfactions des contrainte ponderee par leur
    // importance
    double sommeSatisfactions = 0.0;
    double sommeImportances = 0.0;
    for (Constraint cont : this.getConstraints()) {
      // calcul de satisfaction de la contrainte
      GeographicConstraint cont_ = (GeographicConstraint) cont;
      cont_.computeSatisfaction();

      // stocke la valeur de satisfaction
      etat.getValeursMesures().put(cont.getClass().getSimpleName(),
          new Double(cont_.getSatisfaction()));

      sommeSatisfactions += cont_.getImportance() * cont_.getSatisfaction();
      sommeImportances += cont_.getImportance();

      if (GeographicAgentGeneralisation.logger.isTraceEnabled()) {
        GeographicAgentGeneralisation.logger
            .trace("   Cont: " + cont_.getClass().getSimpleName() + " imp="
                + cont_.getImportance() + " s=" + cont_.getSatisfaction());
      }
    }
    if (sommeImportances == 0) {
      this.setSatisfaction(100.0);
    } else {
      this.setSatisfaction(sommeSatisfactions / sommeImportances);
    }
    etat.setSatisfaction(this.getSatisfaction());
    if (GeographicAgentGeneralisation.logger.isDebugEnabled()) {
      GeographicAgentGeneralisation.logger
          .debug("   S=" + this.getSatisfaction());
    }
  }

}
