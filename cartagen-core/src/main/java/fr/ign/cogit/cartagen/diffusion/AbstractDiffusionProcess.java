/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.diffusion;

public abstract class AbstractDiffusionProcess implements DiffusionProcess {

  private double minDispl = 0.05;
  private double absorptionRatio = 0.15;

  @Override
  public double getMinimumDisplacement() {
    return minDispl;
  }

  @Override
  public void setMinimumDisplacement(double minDisplacement) {
    this.minDispl = minDisplacement;
  }

  @Override
  public double getAbsorptionRatio() {
    return absorptionRatio;
  }

  @Override
  public void setAbsorptionRatio(double absorptionRatio) {
    this.absorptionRatio = absorptionRatio;
  }

}
