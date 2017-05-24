package fr.ign.cogit.cartagen.agents.diogen.padawan;

public class DefaultEnvironmentStrategy implements EnvironmentStrategy {

  private static DefaultEnvironmentStrategy singletonObject;

  /** A private Constructor prevents any other class from instantiating. */
  private DefaultEnvironmentStrategy() {
    // empty for now
  }

  public static synchronized DefaultEnvironmentStrategy getInstance() {
    if (singletonObject == null) {
      singletonObject = new DefaultEnvironmentStrategy();
    }
    return singletonObject;
  }
}
