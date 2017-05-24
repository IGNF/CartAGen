package fr.ign.cogit.cartagen.agents.diogen.hikingroutes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ColorRouteNameMap {

  private static ColorRouteNameMap singleton;

  public static ColorRouteNameMap getInstance() {
    if (singleton == null) {
      singleton = new ColorRouteNameMap();
    }
    return singleton;
  }

  private ColorRouteNameMap() {
    super();
    this.map = new Hashtable<String, Color>();
  }

  private Map<String, Color> map;

  public Set<String> getRoutesNames() {
    return this.map.keySet();
  }

  public Color getRouteColor(String name) {
    if (map.containsKey(name)) {
      return map.get(name);
    }
    Color color = chooseRandomColor();
    map.put(name, color);
    return color;
  }

  private int maxColor = 5;
  private final double base_random_value = 0;
  private List<Integer> alreadyChoosen = new ArrayList<Integer>();

  private Random generator = new Random(1111);

  /**
   * Choose a random brightly colour.
   * @return
   */
  private Color chooseRandomColor() {
    float h;
    if (alreadyChoosen.size() >= maxColor) {
      alreadyChoosen.clear();
      for (int i = 0; i < maxColor; i++) {
        alreadyChoosen.add(2 * i);
      }
      maxColor = 2 * maxColor;
      Collections.sort(alreadyChoosen);
    }
    int hInt = (int) Math.floor(generator.nextDouble()
        * (maxColor - this.alreadyChoosen.size()));
    for (int i : alreadyChoosen) {
      if (hInt >= i)
        hInt++;
    }

    h = (float) (base_random_value + (double) hInt / maxColor);
    alreadyChoosen.add(hInt);
    Collections.sort(alreadyChoosen);

    float s = (float) (0.75);
    float v = (float) (0.75);
    return Color.getHSBColor(h, s, v);

  }

  public void setRouteColor(String name, Color color) {
    map.put(name, color);
  }

}
