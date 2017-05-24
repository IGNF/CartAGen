package fr.ign.cogit.cartagen.agents.gael.deformation.submicrogeneobj;

import fr.ign.cogit.cartagen.agents.gael.deformation.submicro.ISubMicro;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public interface ISubmicroGeneObj extends IGeneObj, ISubMicro {

  /**
   * 
   * @return
   */
  ISubMicro getSubMicro();
}
