package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

public interface IStyleDependingGeomFeature extends IFeature {

  void computeGeom();

}
