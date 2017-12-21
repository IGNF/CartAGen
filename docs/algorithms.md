# List of the available algorithms in CartAGen

> - Date 18/05/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.

A list, not exhaustive yet, of the generalization algorithms available in CartAGen platform.

#### [](#header-4)Line simplification algorithms

| Algorithm name        | Reference         				| Code 							| Description of the implementation 				|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| [Douglas & Peucker][36]    | [Douglas & Peucker 1973][13] 		| JTS implementation  			| basic implementation								|
| [Visvalingam-Whyatt][37]	| [Visvalingam & Whyatt 1993][14]	| [VisvalingamWhyatt][15]  | topology safe implementation (checks potential intersections when removing a vertex)	|
| [Hexagon based][38]    | [Raposo 2013][16]      			| [RaposoSimplification][17] | basic implementation of all versions of the algorithm 	|
| [Accordion][39]    	| [Plazanet 1996][18] 				| [BendSeriesAlgorithm][19]  | Port from the initial ADA code					|
| [Bend schematisation][40]   | [Lecordix et al 1997][20] 		| [BendSeriesAlgorithm][19]  | Port from the initial ADA code					|

#### [](#header-4)Building algorithms (for individual buildings and building groups)

| Algorithm name        | Reference         				| Code 							| Description of the implementation 				|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| [Simplification][43] 	    | Ruas 1988 [reported in AGENT project][21] | [from GeOxygene][22]  			| implemented by Julien Gaffuri (code comments mostly in French)	|
| [Least squares squaring][44] 	| [Lokhat & Touya 2016][23]	| [SquarePolygonLS][24]  | non linear least squares optimize the position of the building vertices, rectifying almost 90° angles	|
| [Enlarge][42]      		| [reported in AGENT project][21]   | JTS implementation | 	|
| [Enlarge to rectangle][42]	| [reported in AGENT project][21] 	| uses JTS smallest surrounding rectangle (SSR)  | 					|
| Rotate   				| [reported in AGENT project][21]	| JTS implementation  |					|
| [Random displacement][45] 	| never published (@Julien Gaffuri)	| [BuildingDisplacementRandom][25]  |	iteratively, a building is randomly displaced (with very small displacements), until the global legibility is optimized (a gradient descent is used)	|
| [displacement in block][46]  | [Ruas 1999][26]					| [BuildingDisplacementRuas][27]  |					|
| [Aggregation][47]  | [Regnauld 1998][28]					| [PolygonAggregation][29]  |	Direct port from Regnauld's PhD thesis, comments only in French for now		|
| [Morphology Amalgamation][63]  | [Damen et al 2008][64]					| [BuildingsAggregation][65]  |	Direct port from the workshop paper, but the square Minkowski is approximated by the CAP_FLAT buffer from JTS library	|
| [Delete overlaping buildings in block][48]  | never published (@Guillaume Touya)	| [BuildingDeletionOverlap][30]  |	Given a threshold of area overlaping and a couple of overlaping buildings, deletes the smallest one |
| [ELECTRE deletion in block][49]  | not yet published (@Guillaume Touya)	| [BuildingsDeletionProximityMultiCriterion][31]  | Uses ELECTRE III multi-criteria	to sort buildings from the first to delete to the last, using criteria such as, relative position, congestion or size	|
| [PROMETHEE deletion in block][50]  | not yet published (@Guillaume Touya)	| [BuildingDeletionPromethee][32]  | Uses PROMETHEE multi-criteria	to sort buildings from the first to delete to the last, using criteria such as, relative position, congestion or size	|
| [Congestion based deletion in block][51]  | [Ruas 1999][26]					| [BuildingsDeletionCongestion][33]  |	comments in French for now	|
| [Building typification][52]  | [Burghardt & Cecconi 2007][34]					| [TypifyBurghardtCecconi][35]  |	slightly adapted implementation to better preserve some specific buildings |

#### [](#header-4)Network algorithms (for individual network section and for whole networks)

| Algorithm name        | Reference         				| Code 							| Description of the implementation 				|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| [River network selection][79]   | [Touya 2007][54] | [RiverNetworkSelection][41]  			|  some of the structures detection algorithms (e.g. irrigation areas) have not been ported from the initial implementation on 1Spatial's LAMPS2 software.	|
| [Roundabout detection/collapse][80]	    | [Touya 2010][53] | [CollapseRoundabout][58]  			| 	|
| [Road network selection][81]   | [Touya 2010][53] | [RoadNetworkTrafficBasedSelection][59]  			|  some of the structures detection algorithms (e.g. highway interchange) have not been ported	from the initial implementation on 1Spatial's LAMPS2 software.|
| [Strokes based road selection][82]	    | [Thomson & Richardson 1999][57] | [RoadNetworkStrokesBasedSelection][60]  		|  Two criteria can be used for Stroke saliency: lenght of course, and the number T-shaped intersections	|
| [Collapse parallel railways][83]	    | [Touya & Girres 2014][55] | [CollapseParallelRailways][61]  			| 	|
| [Typify side tracks][84]	    | [Savino & Touya 2015][56] | [TypifySideTracks][62]  			| 	|


#### [](#header-4)Other algorithms

| Algorithm name        | Reference         				| Code 							| Description of the implementation 				|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| [Point cloud reduction][70]   |  | [KMeansReduction][69]  			| 	|
| [Point cloud covering][71]	    | [Galton & Duckham 2006][66] | [DelaunayNonConvexHull][67]  			| 	|
| [Spinalize][72]   | [Touya & Girres 2014][66] | [Spinalize][74]  			| 	|
| [Skeletonize][76]   | [Haunert & Sester 2008][77] | [Skeletonize][75]  			| 	|
| [Airport generalization][73]   | [Touya & Girres 2014][66] | [AirportTypification][68]  			| 	|


- [Return to main page][78]


[1]: http://recherche.ign.fr/labos/cogit/english/accueilCOGIT.php
[2]: https://github.com/IGNF/geoxygene
[3]: docs/tuto_import_data.md
[4]: docs/tuto_generalization_algo.md
[5]: docs/tuto_agents.md
[6]: http://aci.ign.fr/2010_Zurich/genemr2010_submission_10.pdf
[7]: https://www.researchgate.net/publication/281967532_Automated_generalisation_results_using_the_agent-based_platform_CartAGen
[8]: docs/tuto_schema.md
[9]: http://icaci.org/files/documents/ICC_proceedings/ICC2001/icc2001/file/f13041.pdf
[10]: http://dx.doi.org/10.1080/13658816.2011.639302
[11]: https://www.researchgate.net/publication/221225232_Systeme_multi-agent_pour_la_deformation_en_generalisation_cartographique
[12]: http://dx.doi.org/10.1007/978-3-642-19143-5_30
[13]: http://dx.doi.org/10.3138/FM57-6770-U75U-7727
[14]: http://www.tandfonline.com/doi/abs/10.1179/000870493786962263
[15]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/polygon/VisvalingamWhyatt.java
[16]: http://dx.doi.org/10.1080/15230406.2013.803707
[17]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/polygon/RaposoSimplification.java
[18]: http://recherche.ign.fr/labos/cogit/pdf/THESES/PLAZANET/These_Plazanet_1996.zip
[19]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/section/BendSeriesAlgorithm.java
[20]: http://dx.doi.org/10.1023/A:1009736628698
[21]: http://agent.ign.fr/deliverable/DD2.html
[22]: https://github.com/IGNF/geoxygene/blob/master/geoxygene-spatial/src/main/java/fr/ign/cogit/geoxygene/generalisation/simplification/SimplificationAlgorithm.java
[23]: http://dx.doi.org/10.5311/JOSIS.2016.13.276
[24]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/polygon/SquarePolygonLS.java
[25]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/displacement/BuildingDisplacementRandom.java
[26]: http://recherche.ign.fr/labos/cogit/pdf/THESES/RUAS/These_Ruas_1999.zip
[27]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/displacement/BuildingDisplacementRuas.java
[28]: http://recherche.ign.fr/labos/cogit/pdf/THESES/REGNAULD/These_Regnauld_1998.pdf
[29]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/polygon/PolygonAggregation.java
[30]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/deletion/BuildingDeletionOverlap.java
[31]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/deletion/BuildingsDeletionProximityMultiCriterion.java
[32]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/deletion/BuildingDeletionPromethee.java
[33]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/deletion/BuildingsDeletionCongestion.java
[34]: http://www.tandfonline.com/doi/abs/10.1080/13658810600912323
[35]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/typification/TypifyBurghardtCecconi.java
[36]: /algorithms/line/douglas_peucker.md
[37]: /algorithms/line/visvalingam.md
[38]: /algorithms/line/raposo.md
[39]: /algorithms/line/accordion.md
[40]: /algorithms/line/bend_schematization.md
[41]: https://github.com/IGNF/CartAGen/tree/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/network/RiverNetworkSelection.java
[42]: /algorithms/buildings/enlarge_enlarge_rectangle.md
[43]: /algorithms/buildings/simplification.md
[44]: /algorithms/buildings/ls_squaring.md
[45]: /algorithms/buildings/random_displacement.md
[46]: /algorithms/buildings/ruas_displacement.md
[47]: /algorithms/buildings/aggregation_regnauld.md
[48]: /algorithms/buildings/overlaping_deletion.md
[49]: /algorithms/buildings/electre_deletion.md
[50]: /algorithms/buildings/promethee_deletion.md
[51]: /algorithms/buildings/congestion_deletion.md
[52]: /algorithms/buildings/building_typification.md
[53]: https://www.researchgate.net/publication/220606082_A_Road_Network_Selection_Process_Based_on_Data_Enrichment_and_Structure_Detection
[54]: https://www.researchgate.net/publication/281967153_River_Network_Selection_based_on_Structure_and_Pattern_Recognition
[55]: https://www.researchgate.net/publication/282274843_Generalising_Unusual_Map_Themes_from_OpenStreetMap
[56]: https://www.researchgate.net/publication/281857051_Automatic_Structure_Detection_and_Generalization_of_Railway_Networks
[57]: http://citeseerx.ist.psu.edu/viewdoc/citations?doi=10.1.1.202.4737
[58]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/section/CollapseRoundabout.java
[59]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/network/roads/RoadNetworkTrafficBasedSelection.java
[60]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/network/roads/RoadNetworkStrokesBasedSelection.java
[61]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/rail/CollapseParallelRailways.java
[62]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/rail/TypifySideTracks.java
[63]: /algorithms/buildings/morpho_amalgamation.md
[64]: https://kartographie.geo.tu-dresden.de/downloads/ica-gen/workshop2008/04_Damen_et_al.pdf
[65]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/BuildingsAggregation.java
[66]: https://link.springer.com/chapter/10.1007%2F11863939_6
[67]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/points/DelaunayNonConvexHull.java
[68]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/facilities/AirportTypification.java
[69]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/points/KMeansReduction.java
[70]: /algorithms/others/point_reduction.md
[71]: /algorithms/others/point_cover.md
[72]: /algorithms/others/spinalize.md
[73]: /algorithms/others/airports.md
[74]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/polygon/Spinalize.java
[75]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/polygon/Skeletonize.java
[76]: /algorithms/others/skeletonize.md
[77]: https://link.springer.com/article/10.1007%2Fs10707-007-0028-x
[78]: https://ignf.github.io/CartAGen
[79]: /algorithms/networks/river_selection.md
[80]: /algorithms/networks/roundabouts.md
[81]: /algorithms/networks/road_selection.md
[82]: /algorithms/networks/strokes_selection.md
[83]: /algorithms/networks/collapse_railways.md
[84]: /algorithms/networks/typify_sidetracks.md