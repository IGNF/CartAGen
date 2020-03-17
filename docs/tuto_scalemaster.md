# Tutorial on the use of ScaleMaster2.0 to monitor generalization
This tutorial explains how to use the ScaleMaster2.0 model to monitor multi-scale automated generalization in CartAGen.

> - Date 04/10/2018.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



The Basic Concepts of the ScaleMaster framework
-------------
The ScaleMaster framework was first proposed by [Brewer & Buttenfield (2007)][3]
To be done...
Further experiments on multi-scale maps design with the ScaleMaster were presented by [Brewer & Buttenfield (2009)][4].

The ScaleMaster2.0
-------------
The ScaleMaster2.0 is updated version of the ScaleMaster dedicated to automated generalization based on the rules of a ScaleMaster. It was proposed by [Touya & Girres (2013)][2]


> To go further in the use of AGENT, see [this advanced tutorial][10].

Input files
-------------

The ScaleMaster2.0 model requires several input files to work, and these files are described in this section of the tutorial.

## ScaleMasterThemes.xml file

The ScaleMasterThemes file describe all the themes that can be used in a ScaleMaster2 model, a theme being a line in the model. With this file, the process can interpret lines of the model as feature classes of CartAGen. For instance, the theme 'roadl' corresponds to the linear road features of the class RoadLine in CartAGen. This file is supposed to be filled for once, and then the map designer that parameterizes the ScaleMaster only needs to know the theme names, and not the name of the classes in CartAGen. The name of the theme is given in the tag <name> of the file (see the extract below).

The tag <concept> describes a reference to a concept in an ontology for map generalization, such as the one promoted by the [ICA commission on map generalization][15]. This tag is just a formal description for now, it is not used in the automatic processing of the file.
The tag <description> contains a textual description of the theme.
The tag <geometry-type> the geometry type of the features in this theme that can be POINT, LINE or POLYGON. All the features in a theme must have the same geometry type, so you have to use two themes if you have lines and polygons for rivers for instance.

The code below is an extract of the default ScaleMasterThemes.xml available in CartAGen repository:

```xml
<scale-master-theme>
    <theme>
        <name>roadl</name>
        <concept>road</concept>
        <description>sections of road lines</description>
        <geometry-type>LINE</geometry-type>
        <cartagen-classes>
            <class>fr.ign.cogit.cartagen.core.defaultschema.road.RoadLine</class>
            <class>fr.ign.cogit.cartagen.osm.schema.road.OsmRoadLine</class>
        </cartagen-classes>
    </theme>
    <theme>
        <name>building</name>
        <concept>building</concept>
        <description>Building polygon</description>
        <geometry-type>POLYGON</geometry-type>
        <cartagen-classes>
            <class>fr.ign.cogit.cartagen.core.defaultschema.urban.Building</class>
            <class>fr.ign.cogit.cartagen.osm.schema.urban.OsmBuilding</class>
        </cartagen-classes>
    </theme>
```

See Also
-------------
- [tutorial on data loading][8]
- [tutorial to generalize loaded data][9]
- [tutorial on CartAGen data schema][7]
- [advanced tutorial on AGENT][10]
- [advanced tutorial on CartACom][11]
- [advanced tutorial on GAEL][12]
- [advanced tutorial on DIOGEN][13]
- [advanced tutorial on CollaGen][14]

- [Return to main page][6]


[1]: https://umrlastig.github.io/guillaume-touya/[2]: http://dx.doi.org/10.1080/15230406.2013.809233
[3]: http://dx.doi.org/10.1559/152304007780279078
[4]: http://dx.doi.org/10.1007/s10707-009-0083-6
[5]: http://www.tandfonline.com/doi/full/10.1080/23729333.2017.1300997
[6]: https://ignf.github.io/CartAGen
[7]: /tuto_agents.md
[8]: /tuto_import_data.md
[9]: /tuto_schema.md
[10]: /agents/AGENT_advanced.md
[11]: /agents/CartACom_advanced.md
[12]: /agents/GAEL_advanced.md
[13]: /agents/DIOGEN_advanced.md
[14]: /agents/CollaGen_advanced.md
[15]: http://generalisation.icaci.org/generalisation-ontologies.html
