package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.schema;

import java.awt.Color;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fr.ign.cogit.cartagen.agents.diogen.hikingroutes.ColorRouteNameMap;

@Entity
@Access(AccessType.PROPERTY)
public abstract class HikingRoute extends Route implements IHikingRoute {

  private String name;

  public HikingRoute() {
    super();
  }

  public HikingRoute(String name) {
    super();
    this.setName(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Transient
  public Color getColor() {
    return ColorRouteNameMap.getInstance().getRouteColor(this.getName());
  }

}
