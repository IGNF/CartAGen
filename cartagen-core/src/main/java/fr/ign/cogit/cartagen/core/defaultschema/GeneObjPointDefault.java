/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema;

import fr.ign.cogit.cartagen.core.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This class extends the CartAGenGeoObjDefault class. It handles CartAGen
 * ponctual objects that have a (persistent) artifact in a Gothic database.
 * 
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 * @author GTouya
 */
public abstract class GeneObjPointDefault extends GeneObjDefault implements IGeneObjPoint {

	@Override
	public IPoint getGeom() {
		return (IPoint) super.getGeom();
	}

	@Override
	public IGeometry getSymbolGeom() {
		return SLDUtilCartagen.getSymbolGeometry(this);
	}

}
