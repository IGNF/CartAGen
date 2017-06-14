/**
 * 
 */
package fr.ign.cogit.cartagen.appli.agents;

import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * @author JGaffuri
 * 
 */
public class FrameDistribution extends JFrame {
  private static final long serialVersionUID = 1L;

  public FrameDistribution(String title, ArrayList<Double> data, double max,
      boolean axisVisible) {
    this.add(new PanelDistribution(title, data, max, axisVisible));
    this.pack();
    this.setLocationByPlatform(true);
  }

}
