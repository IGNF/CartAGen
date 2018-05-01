# List of the available spatial analysis tools in CartAGen

> - Date 18/05/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.

A list, not exhaustive yet, of the spatial analysis and data enrichment algorithms available in CartAGen platform.

#### [](#header-4)[Road network enrichment][3]

| Algorithm name        | Reference         				| Code 							| 
|:----------------------|:----------------------------------|:------------------------------|
| Roundabouts   | 	| 		| 
| dual carriageways	| 	|  | 
| branching crossroads    | 	|  |
| simple crossroads   	| 	|   |
| rest areas  	| 	|   |
| highway interchange   | 	|  |

#### [](#header-4)[Building measures][4] (for individual buildings and building groups/blocks)

| Algorithm name        | Reference         				| Code 							| 
|:----------------------|:----------------------------------|:------------------------------|
|   |   |  | 
| 	| 	|  | 
|   |   |  |
| 	|  	|  | 	

#### [](#header-4)[Geographic spaces][5] from CollaGen (but can be used in other cases)

| Algorithm name        | Reference         				| Code 							|
|:----------------------|:----------------------------------|:------------------------------|
| Urban areas   | [Touya 2010][8] | [UrbanAreas][9]  			| 
| Mountain areas	    | [Touya 2010][8] | [MountainAreas][10]  			| 
| Rural areas   | [Touya 2010][8] | [RuralAreas][11]  			| 
| Coastal areas	    | [Touya 2010][8] | [CoastalAreas][12]  		|  


#### [](#header-4)[Other algorithms][7]

| Algorithm name        | Reference         				| Code 							| 
|:----------------------|:----------------------------------|:------------------------------|
|   |  | 	| 	
|   |  | 	| 	
|   |  | 	| 	
|   |  | 	| 	
|   |  | 	| 	


- [Return to main page][6]


[1]: http://recherche.ign.fr/labos/cogit/english/accueilCOGIT.php
[2]: https://github.com/IGNF/geoxygene
[3]: /spatial_analysis/road_enrichment.md
[4]: /spatial_analysis/building_measures.md
[5]: /spatial_analysis/geo_spaces.md
[6]: https://ignf.github.io/CartAGen
[7]: /spatial_analysis/other_measures.md
[8]: https://kartographie.geo.tu-dresden.de/downloads/ica-gen/workshop2010/genemr2010_submission_5.pdf
[9]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/geospace/UrbanAreas.java
[10]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/geospace/MountainAreas.java
[11]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/geospace/RuralAreas.java
[12]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/geospace/CostalAreas.java