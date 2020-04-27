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
The ScaleMaster2.0 is an updated version of the ScaleMaster dedicated to automated generalization based on the rules of a ScaleMaster. It was proposed by [Touya & Girres (2013)][2]. It is a machine-readable version of the ScaleMaster, and it goes with an automated engine able to read the ScaleMaster2.0 file, extract the generalisation rules for one scale from the ScaleMaster, and then trigger the automated generalisation.


Input files
-------------

The ScaleMaster2.0 model requires several input files to work, and these files are described in this section of the tutorial.

## ScaleMaster2.xml file

This is the main file that encodes the ScaleMaster multi-scale specifications.

```xml
<scalemaster>
	<scale-line theme="roadl">
		<scale-interval>
			<interval-min>10000</interval-min>
			<interval-max>20000</interval-max>
			<db-name>OSM</db-name>
			<class-name>fr.ign.cogit.cartagen.osm.schema.road.OsmRoadLine</class-name>
		</scale-interval>
		<scale-interval>
			<interval-min>20000</interval-min>
			<interval-max>40000</interval-max>
			<db-name>OSM</db-name>
			<class-name>fr.ign.cogit.cartagen.osm.schema.road.OsmRoadLine</class-name>
			<attribute-selection priority=4>
				<ogc:PropertyIsNotEqualTo>
					<ogc:PropertyName>Importance</ogc:PropertyName>
					<ogc:Literal>0</ogc:Literal>
				</ogc:PropertyIsNotEqualTo>
			</attribute-selection>
			<generalisation-processes>
				<process priority=0>
					<name>douglas_peucker</name>
					<params>
						<parameter name="dp_filtering" type="Double">30</parameter>
					</params>
				</process>
			</generalisation-processes>
		</scale-interval>
    ...
	</scale-line>
  ...
</scalemaster>
```

## ScaleMasterThemes.xml file

The ScaleMasterThemes file describe all the themes that can be used in a ScaleMaster2 model, a theme being a line in the model. With this file, the process can interpret lines of the model as feature classes of CartAGen. For instance, the theme 'roadl' corresponds to the linear road features of the class RoadLine in CartAGen. This file is supposed to be filled for once, and then the map designer that parameterizes the ScaleMaster only needs to know the theme names, and not the name of the classes in CartAGen. The name of the theme is given in the tag <name> of the file (see the extract below).

The tag <concept> describes a reference to a concept in an ontology for map generalization, such as the one promoted by the [ICA commission on map generalization][15]. This tag is just a formal description for now, it is not used in the automatic processing of the file.
The tag <description> contains a textual description of the theme.
The tag <geometry-type> the geometry type of the features in this theme that can be POINT, LINE or POLYGON. All the features in a theme must have the same geometry type, so you have to use two themes if you have lines and polygons for rivers for instance.
The tag <cartagen-feature-type> gives the name of the feature type used in CartAGen for this theme. This feature type can be found in the generic interfaces of the CartAGen [data schema][9].

The code below is an extract of the default ScaleMasterThemes.xml available in CartAGen repository:

```xml
<scale-master-theme>
	<theme>
			<name>roadl</name>
			<concept>road_segment</concept>
			<description>sections of road lines</description>
			<geometry-type>LINE</geometry-type>
			<cartagen-feature-type>RoadLine</cartagen-feature-type>
	</theme>
	<theme>
			<name>building</name>
			<concept>building</concept>
			<description>Building polygon</description>
			<geometry-type>POLYGON</geometry-type>
			<cartagen-feature-type>Building</cartagen-feature-type>
	</theme>
```


## ScaleMasterProcesses.csv file
The ScaleMasterProcesses.csv file lists the processes that can be triggered by the ScaleMaster. It is a two-column csv file with the name of the process in the first column, and the path of the Java class implemeting this process in CartAGen. Available ScaleMaster processes are implemented in the fr.ign.cogit.cartagen.mrdb.processes package, and others can be added (see the tutorial below for instance). The first column uses names of 'generalisation_algorithm' or 'generalisation_process' sub-concepts from the GeneProcessOnto ontology.

```csv
douglas_peucker,fr.ign.cogit.cartagen.mrdb.processes.DouglasPeuckerProcess
touya_road_selection,fr.ign.cogit.cartagen.mrdb.processes.MixedRoadSelectionProcess
stroke_based_selection,fr.ign.cogit.cartagen.mrdb.processes.StrokesRoadSelectionProcess
river_stroke_selection,fr.ign.cogit.cartagen.mrdb.processes.RiverSelectionProcess
```


Using the ScaleMaster2.0 in CartAGen
-------------
In this part of the tutorial, we show you how to use the ScaleMaster2.0 model in CartAGen. The first step is to load a ScaleMaster2.0 model, stored in a XML file. To load a file, in the "ScaleMaster" menu of CartAGen, click on the first menu item "Load a ScaleMaster" (see image below), and then select the XML file you want to load.

![load a ScaleMaster file](docs/assets/images/scalemaster_load.png)

For now, there is no interactive editor for ScaleMaster2.0 models.

Then, you have to define a mapping between the names of the databases from the ScaleMaster and the names of the databases actually loaded in CartAGen. The ScaleMaster2.0 model is supposed to be portable and interoperable (even though there is no other implementation yet), so the names of the databases are not necessarily the sames as the ones used when data is loaded in CartAGen. The other reason is that the model might be designed to be generic enough to work with different specific datasets. For instance, in our case, we loaded the "IGN" dataset in CartAGen with the "calac" name, which is the internal name at IGN for the specific topographic data from which this dataset was extracted. So we need to match "IGN" from the ScaleMaster to "calac" in CartAGen, and this is done by clicking on the menu item "Set databases to use" in the "ScaleMaster" menu. Then, a small dialog opens where you can select the different database names used in the loaded ScaleMaster, and the different database names loaded into CartAGen (see image below). You have to click on the "Add" button to add a mapping in the table and store the mapping.

![Set the databases used in the ScaleMaster](docs/assets/images/scalemaster_set_database.png)

Then, you can run a ScaleMaster generalisation by clicking on the "Run the ScaleMaster" menu item in the "ScaleMaster" menu. You have to select the final scale you want to generalise to ("25000" for 1:25k), and the data is automatically generalised.


Extend a ScaleMaster2.0
-------------
Starting from the very basic ScaleMaster2.0 model available in CartAGen in the ScaleMaster2_tutorial.xml file, this part of the tutorial shows you how to add new elements (i.e. scale ranges) in an existing line, how to add new processes in an element, and how to add new themes/lines in the model.

At the beginning, the tutorial ScaleMaster2.0 file is as follows and only contains only one line/theme (road lines) and two scale ranges:
* 1:10k to 1:20k where no generalisation from the "IGN" dataset is required;
* 1:20k to 1:40k where an attribute filter is applied (importance not equal to "4"), and a Douglas&Peucker filter is applied to the road geometries.

```xml
<scalemaster>
	<name>tutorial</name>
	<global-range>
		<interval-min>10000</interval-min>
		<interval-max>200000</interval-max>
	</global-range>
	<scale-line theme="roadl">
		<scale-interval>
			<interval-min>10000</interval-min>
			<interval-max>20000</interval-max>
			<db-name>IGN</db-name>
		</scale-interval>
		<scale-interval>
			<interval-min>20000</interval-min>
			<interval-max>40000</interval-max>
			<db-name>IGN</db-name>
			<attribute-selection priority="4">
				<ogc:PropertyIsNotEqualTo>
					<ogc:PropertyName>Importance</ogc:PropertyName>
					<ogc:Literal>0</ogc:Literal>
				</ogc:PropertyIsNotEqualTo>
			</attribute-selection>
			<generalisation-processes>
				<process priority="0">
					<name>douglas_peucker</name>
					<params>
						<parameter name="dp_filering" type="Double">20</parameter>
					</params>
				</process>
			</generalisation-processes>
		</scale-interval>
	</scale-line>
</scalemaster>
```

Then, to extend this minimal ScaleMaster model, we will add a new range the road lines theme. For instance, we will add a new 1:40k to 1:60k scale ranges where the Douglas & Peucker filtering is bigger.

```xml
<scale-interval>
	<interval-min>40000</interval-min>
	<interval-max>60000</interval-max>
	<db-name>IGN</db-name>
	<attribute-selection priority="4">
		<ogc:PropertyIsNotEqualTo>
			<ogc:PropertyName>Importance</ogc:PropertyName>
			<ogc:Literal>0</ogc:Literal>
		</ogc:PropertyIsNotEqualTo>
	</attribute-selection>
	<generalisation-processes>
		<process priority="0">
			<name>douglas_peucker</name>
			<params>
				<parameter name="dp_filering" type="Double">25</parameter>
			</params>
		</process>
	</generalisation-processes>
</scale-interval>
```

Then, at these scales, we would like to add a selection process (here a Strokes-based selection process, similar to the one proposed by [Thomson & Richardson][16]). A new process is added in the <generalisation-processes> tag. The priority of the selection process is bigger than the filtering to make sure selection is applied first (no need to simplify the roads that are eliminated...).

```xml
		<process priority="1">
			<name>stroke_based_selection</name>
			<params>
				<parameter name="min_length" type="Double">2000.0</parameter>
				<parameter name="min_T" type="Integer">3</parameter>
				<parameter name="attribute" type="Boolean">yes</parameter>
			</params>
		</process>
```

Then, we can add a new theme/line to the ScaleMaster2.0, for instance water lines (see code below). In this case, the same scale ranges are used, but there is no obligation of having similar scale ranges in different lines of a ScaleMaster model. Be sure that the new themes added in the ScaleMaster are also included in the ScaleMasterThemes file.

```xml
</scale-line>
	<scale-line theme="waterl">
	<scale-interval>
		<interval-min>10000</interval-min>
		<interval-max>20000</interval-max>
		<db-name>IGN</db-name>
	</scale-interval>
	<scale-interval>
		<interval-min>20000</interval-min>
		<interval-max>40000</interval-max>
		<db-name>IGN</db-name>
		<generalisation-processes>
			<process priority="0">
				<name>douglas_peucker</name>
				<params>
					<parameter name="dp_filering" type="Double">20</parameter>
				</params>
			</process>
		</generalisation-processes>
	</scale-interval>
	<scale-interval>
		<interval-min>40000</interval-min>
		<interval-max>60000</interval-max>
		<db-name>IGN</db-name>
		<generalisation-processes>
			<process priority="0">
				<name>douglas_peucker</name>
				<params>
					<parameter name="dp_filering" type="Double">25</parameter>
				</params>
			</process>
			<process priority="1">
				<name>river_stroke_selection</name>
				<params>
					<parameter name="min_length" type="Double">1500.0</parameter>
					<parameter name="horton_order" type="Integer">2</parameter>
					<parameter name="remove" type="Boolean">yes</parameter>
					<parameter name="min_braided_area" type="Double">20000.0</parameter>
				</params>
			</process>
		</generalisation-processes>
	</scale-interval>
</scale-line>
```

All these ScaleMaster models can be tested on the Alpe d'Huez tutorial data provided with CartAGen, or with other datasets imported into the platform.

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
[16]: http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.202.4737
