/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

import org.semanticweb.owlapi.model.OWLClass;

import fr.ign.cogit.geoxygene.filter.Filter;

public class ScaleMasterTheme implements Comparable<ScaleMasterTheme> {

    private String name, description, populationName;

    private GeometryType geometryType;

    /**
     * The attribute query that selects the objects from the CartAGen Geo
     * classes related to {@code this} scale master theme when the class
     * contains objects from several themes. May be null if not relevant.
     */
    private Filter filter;

    private OWLClass geoConcept;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGeometryType(GeometryType geometryType) {
        this.geometryType = geometryType;
    }

    public GeometryType getGeometryType() {
        return geometryType;
    }

    @Override
    public String toString() {
        return name;
    }

    public ScaleMasterTheme(String name, String populationName,
            GeometryType geometryType) {
        super();
        this.name = name;
        this.populationName = populationName;
        this.setGeometryType(geometryType);
    }

    /**
     * Constructor of a theme with only its name.
     * 
     * @param name
     */
    public ScaleMasterTheme(String name) {
        super();
        this.name = name;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public OWLClass getGeoConcept() {
        return geoConcept;
    }

    public void setGeoConcept(OWLClass geoConcept) {
        this.geoConcept = geoConcept;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(ScaleMasterTheme o) {
        return this.name.compareTo(o.name);
    }

    public String getPopulationName() {
        return populationName;
    }

    public void setPopulationName(String populationName) {
        this.populationName = populationName;
    }

}
