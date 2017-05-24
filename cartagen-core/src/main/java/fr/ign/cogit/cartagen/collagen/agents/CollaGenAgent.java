package fr.ign.cogit.cartagen.collagen.agents;

public interface CollaGenAgent {

  public void computeSatisfaction();

  public int lifeCycle() throws InterruptedException;

  public String getName();
}
