# Description of Airport Generalization Algorithms

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------

Airports are complex objects composed of several geographical features such as runways, taxiways, aprons, or terminals (see UML diagram below). They all have to be generalized and CartAGen contains several algorithms dedicated to this complex feature.

![UML diagram of an airport modeled as a complex feature](/images/airport_uml_model.png)
![illustration of the components of an airport](/images/airport_complex_feature.png)

The paper that describes these algorithms can be found [here][3]

#### [](#header-4)Runway collapse

The polygon to line collapse algorithm used for runways is much simpler than the [skeletonize algorithm][2].
First, we compute the global orientation of the runway using the Minimum Bounding Rectangle (see [Duchêne et al. 2003][4]).
Then, we search for the longest segment that can be built inside the runway polygon with the orientation of the runway, and this segment replaces the runway polygon.


| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| fusion    | if true, the runway polygons that touch are merged before collapse	| boolean		| 	true	|
| collapse    | if true, the runway polygons are collapsed into lines	| boolean		| 	true	|


#### [](#header-4)Taxiway collapse
The taxiway collapse algorithm combines morphological operators to identify thin parts of the taxiway polygons that should be collapsed, and then removes these parts from the initial polygon. The removed thin parts are then skeletonized.

| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
|  openThreshTaxi   |  the width threshold for deciding if parts of a taxiway should be collapsed into a line	| 	double (m)	| 		|


#### [](#header-4)Taxiway typification
Taxiway typification only processes taxiway lines. It identifies the complex junctions in the taxiway network and typifies them into simple junctions.
When junctions have been typified, strokes are computed into the remaining lines, and only the longest strokes are kept, while the small ones are removed.

| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
|   branchingMaxArea  | the maximum area for a face of the taxiway graph to be considered as a possible complex junction	| 	double (m²)	| 	7000.0	|
|  maxAngleBranching   | the maximum angle value to be considered as sharp in the complex junction typification process	| 	double (radians)	| 	1.5	|
|   taxiwayLengthThreshold  | the minimum length for a taxiway stroke to be kept in the map	| 	double (m)	| 		|

#### [](#header-4)Other operators
Simple algorithms are also available to:
- amalgamate apron polygons that are touching or close to each other into a single bigger polygon,
- to simplify apron polygons,
- to simplify terminal polygons.

Examples of generalization
-------------
![runway and taxiway collapse in Dakar airport at the 1:250k](/images/proc_airport.png)

![runway collapse](/images/taxiway_collapse.png)

![taxiway typification](/images/taxiway_typification.png)

When to use the algorithm?
-------------
![example of ScaleMaster to monitor the generalization of an airport](/images/airport_scalemaster.png)

These algorithms have been successfully applied to OpenStreetMap airports from all over the world, and to authoritative datasets.

See Also
-------------
- [skeletonize algorithm][2]


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: /algorithms/line/skeletonize.md
[3]: http://recherche.ign.fr/labos/util_basilic/publicDownload.php?id=3082
[4]: https://kartographie.geo.tu-dresden.de/downloads/ica-gen/workshop2003/duchene_et_al_v1.pdf
