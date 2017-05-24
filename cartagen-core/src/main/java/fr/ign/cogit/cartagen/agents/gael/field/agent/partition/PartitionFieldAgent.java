/**
 * @author julien Gaffuri 1 sept. 2008
 */
package fr.ign.cogit.cartagen.agents.gael.field.agent.partition;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.AgentUtil;
import fr.ign.cogit.cartagen.agents.gael.field.agent.FieldAgent;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * @author julien Gaffuri 1 sept. 2008
 */
public abstract class PartitionFieldAgent extends FieldAgent {
  private static Logger logger = Logger
      .getLogger(PartitionFieldAgent.class.getName());

  /**
   */
  private IFeatureCollection<IGeneObjSurf> parcelles = new FT_FeatureCollection<IGeneObjSurf>();

  /**
   * @return Les parcelles composant la partition
   */
  public IFeatureCollection<IGeneObjSurf> getParcelles() {
    return this.parcelles;
  }

  public IGeneObjSurf getDomaine(double x, double y) {
    IPolygon poly;
    for (IGeneObjSurf par : this.parcelles) {
      poly = par.getGeom();
      if (poly.contains(new GM_Point(new DirectPosition(x, y)))) {
        return par;
      }
    }
    return null;
  }

  @Override
  public void cleanDecomposition() {
    super.cleanDecomposition();
    for (IGeneObjSurf dom : this.parcelles) {
      ((ParcelAgent) AgentUtil.getAgentFromGeneObj(dom)).cleanDecomposition();
    }
  }

  /**
   * construit les parcelles blanches, pour avoir une vraie partition
   */
  public void construireParcellesVides() {
    PartitionFieldAgent.logger.fatal(
        "revoir construireParcellesVides de agent champ partion - enlever polygonizer");
    /*
     * GM_Object nouilles=new GM_MultiCurve();
     * 
     * //ajoute les limites de parcelles d'occupation du sol for(AgentParcelle
     * par:parcelles) { GM_Polygon poly=(GM_Polygon)par.getGeometry();
     * nouilles=nouilles.union(poly.getExterior()); int
     * nb=poly.getInterior().size(); for(int i=0; i<nb; i++)
     * nouilles=nouilles.union(poly.getInterior(i)); }
     * 
     * GM_Polygon env=Algos.homothetie((GM_Polygon)nouilles.convexHull(),1.3);
     * GM_LineString ls=new GM_LineString(env.getExterior().coord());
     * nouilles=nouilles.union(ls);
     * 
     * Polygonizer pg=new Polygonizer(); pg.add(nouilles); Collection<?>
     * col=pg.getPolygons();
     * 
     * for(Object obj:col){ GM_Polygon poly=(GM_Polygon)obj;
     * 
     * //verifie si le polygone n'est pas deja un domaine boolean existe=false;
     * for(AgentParcelle par:parcelles) {
     * if(poly.intersection(par.getGeometry()).area()>0.05) { existe=true;
     * break; } } //cree un domaine vide si le domaine n'existe pas deja if
     * (!existe) new ParcelleOccSol(poly, 0, 0.0); }
     */
  }

}
