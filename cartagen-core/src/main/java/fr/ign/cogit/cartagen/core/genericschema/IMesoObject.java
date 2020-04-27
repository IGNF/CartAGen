package fr.ign.cogit.cartagen.core.genericschema;

import java.util.Collection;

public interface IMesoObject<T> {

  public Collection<T> getComponents();
}
