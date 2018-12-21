/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * This class computes the radical law for a given pair of scales (initial and
 * final), as proposed by Töpfer Pillewizer (1966).
 * 
 * @author gtouya
 *
 */
public class TopferRadicalLaw {

    /**
     * The denominator of the scales, e.g. 25000 for 1:25k scale.
     */
    private double initialScale, finalScale;

    enum ConstantSymbolicExxageration {
        NORMAL, EXXAGERATED, UNEXAGGERATED
    }

    public TopferRadicalLaw(double initialScale, double finalScale) {
        super();
        this.initialScale = initialScale;
        this.finalScale = finalScale;
    }

    public double getInitialScale() {
        return initialScale;
    }

    public void setInitialScale(double initialScale) {
        this.initialScale = initialScale;
    }

    public double getFinalScale() {
        return finalScale;
    }

    public void setFinalScale(double finalScale) {
        this.finalScale = finalScale;
    }

    /**
     * Computes the simple radical law for a number of features in the initial
     * scale, and gives the optimal number of features in the final scale.
     * 
     * @param nbOfFeatures
     * @return
     */
    public double simpleRadicalLaw(int nbOfFeatures) {
        double finalNb = nbOfFeatures * Math.sqrt(this.initialScale / this.finalScale);

        return finalNb;
    }

    /**
     * Computes the simple radical law on length (rather than the number of
     * features) in the initial scale, and gives the optimal length of features
     * in the final scale.
     * 
     * @param features
     *            the collection of features to be reduced
     * @return the optimal length of features in the final scale
     */
    public double simpleRadicalLawLines(IFeatureCollection<IFeature> features) {
        double totalLength = 0.0;
        for (IFeature feat : features)
            totalLength += feat.getGeom().length();

        double finalLength = totalLength * Math.sqrt(this.initialScale / this.finalScale);

        return finalLength;
    }

    /**
     * Computes the simple radical law on area (rather than the number of
     * features) in the initial scale, and gives the optimal total area of
     * features in the final scale. Of course, this radical law only applies to
     * polygon features.
     * 
     * @param features
     *            the collection of features to be reduced
     * @return the optimal length of features in the final scale
     */
    public double simpleRadicalLawPolygons(IFeatureCollection<IFeature> features) {
        double totalArea = 0.0;
        for (IFeature feat : features)
            totalArea += feat.getGeom().area();

        double finalArea = totalArea * Math.sqrt(this.initialScale / this.finalScale);

        return finalArea;
    }

    /**
     * Computes the second radical law of Töpfer, for small scales.
     * 
     * @param nbOfFeatures
     * @param cb
     * @return
     */
    public double secondRadicalLaw(int nbOfFeatures, ConstantSymbolicExxageration cb) {
        double constant = 1;
        if (cb.equals(ConstantSymbolicExxageration.EXXAGERATED))
            constant = Math.sqrt(this.initialScale / this.finalScale);
        else if (cb.equals(ConstantSymbolicExxageration.UNEXAGGERATED))
            constant = Math.sqrt(this.finalScale / this.initialScale);
        double finalNb = nbOfFeatures * Math.sqrt(this.initialScale / this.finalScale) * constant;

        return finalNb;
    }

    /**
     * Computes the powered radical law for a number of features in the initial
     * scale, and gives the optimal number of features in the final scale.
     * 
     * @param nbOfFeatures
     * @param power
     *            the power applied to the scale ratio
     * @return
     */
    public double poweredRadicalLaw(int nbOfFeatures, int power) {
        double finalNb = nbOfFeatures * Math.pow(Math.sqrt(this.initialScale / this.finalScale), power);

        return finalNb;
    }

    /**
     * Computes the powered radical law on length (rather than the number of
     * features) in the initial scale, and gives the optimal length of features
     * in the final scale.
     * 
     * @param features
     *            the collection of features to be reduced
     * @param power
     *            the power applied to the scale ratio
     * @return the optimal length of features in the final scale
     */
    public double poweredRadicalLawLines(IFeatureCollection<IFeature> features, int power) {
        double totalLength = 0.0;
        for (IFeature feat : features)
            totalLength += feat.getGeom().length();

        double finalLength = totalLength * Math.pow(Math.sqrt(this.initialScale / this.finalScale), power);

        return finalLength;
    }

    /**
     * Computes the powered radical law on area (rather than the number of
     * features) in the initial scale, and gives the optimal total area of
     * features in the final scale. Of course, this radical law only applies to
     * polygon features.
     * 
     * @param features
     *            the collection of features to be reduced
     * @param power
     *            the power applied to the scale ratio
     * @return the optimal length of features in the final scale
     */
    public double poweredRadicalLawPolygons(IFeatureCollection<IFeature> features, int power) {
        double totalArea = 0.0;
        for (IFeature feat : features)
            totalArea += feat.getGeom().area();

        double finalArea = totalArea * Math.pow(Math.sqrt(this.initialScale / this.finalScale), power);

        return finalArea;
    }
}
