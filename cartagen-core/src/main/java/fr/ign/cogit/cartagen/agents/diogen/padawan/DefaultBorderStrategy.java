package fr.ign.cogit.cartagen.agents.diogen.padawan;

public class DefaultBorderStrategy implements BorderStrategy {

  private static DefaultBorderStrategy singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private DefaultBorderStrategy() {
    // empty for now
  }

  public static synchronized DefaultBorderStrategy getInstance() {
    if (singletonObject == null) {
      singletonObject = new DefaultBorderStrategy();
    }
    return singletonObject;
  }
}
