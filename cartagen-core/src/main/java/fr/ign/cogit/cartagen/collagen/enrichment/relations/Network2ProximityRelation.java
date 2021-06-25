package fr.ign.cogit.cartagen.collagen.enrichment.relations;

import fr.ign.cogit.cartagen.collagen.resources.ontology.GeographicRelation;
import fr.ign.cogit.cartagen.core.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.LinearProximity;

public class Network2ProximityRelation extends CollaGenRelation {

	private double symbolOverlap, minDist;
	private double largeurSymb1, largeurSymb2;

	/**
	 * Constructeur à partir des composants de la relation. Construit la relation
	 * Gothic en plus de la relation Java.
	 * 
	 * @param obj1
	 * @param obj2
	 * @param concept
	 */
	public Network2ProximityRelation(IGeneObjLin res1, IGeneObjLin res2, GeographicRelation concept) {
		super(res1, res2, concept);
		computeGeom();
		this.largeurSymb1 = SLDUtilCartagen.getSymbolMaxWidthMapMm(res1) / 2.0;
		this.largeurSymb2 = SLDUtilCartagen.getSymbolMaxWidthMapMm(res2) / 2.0;
		computeSymbolOverlap();
		computeMinDist();
	}

	public double getSymbolOverlap() {
		return symbolOverlap;
	}

	public void setSymbolOverlap(double symbolOverlap) {
		this.symbolOverlap = symbolOverlap;
	}

	public double getMinDist() {
		return minDist;
	}

	public void setMinDist(double minDist) {
		this.minDist = minDist;
	}

	public double getLargeurSymb() {
		return largeurSymb1 + largeurSymb2;
	}

	private void computeSymbolOverlap() {
		IGeometry buffer1 = this.getObj1().getGeom().buffer(largeurSymb1);
		IGeometry buffer2 = this.getObj2().getGeom().buffer(largeurSymb2);
		IGeometry overlap = buffer1.union(buffer2);
		if (overlap == null) {
			this.setSymbolOverlap(0.0);
			return;
		}
		if (overlap.area() == 0.0) {
			this.setSymbolOverlap(0.0);
			return;
		}
		this.setSymbolOverlap(overlap.area());
		// on enlève l'overlap au niveau de l'intersection potentielle
		if (getObj1().getGeom().intersects(getObj2().getGeom())) {
			IGeometry inter = getObj1().getGeom().union(getObj2().getGeom());
			double largeur = Math.min(largeurSymb1, largeurSymb2);
			IGeometry buffer = inter.buffer(largeur);
			this.setSymbolOverlap(Math.max(0.0, getSymbolOverlap() - buffer.area()));
		}
	}

	private void computeMinDist() {
		GeometryProximity proxi = new GeometryProximity(obj1.getGeom(), obj2.getGeom());
		minDist = proxi.getDistance();
		if (minDist == 0) {
			// cas avec intersection
			LinearProximity proxiLin = new LinearProximity((ILineString) obj1.getGeom(), (ILineString) obj2.getGeom());
			minDist = proxiLin.getMinDistAwayIntersection().length();
		}
	}

	@Override
	public int qualiteRelation() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void computeGeom() {
		GeometryProximity proxi = new GeometryProximity(obj1.getGeom(), obj2.getGeom());
		geom = proxi.toSegment();
	}

	@Override
	public IFeature cloneGeom() throws CloneNotSupportedException {
		return null;
	}
}
