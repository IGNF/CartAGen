---
  title: Spatial Analysis tools
  layout: page
  order: 5
---

# List of the available spatial analysis tools in CartAGen

> - Date 18/05/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.

A list, not exhaustive yet, of the spatial analysis and data enrichment algorithms available in CartAGen platform.
Spatial analysis is necessary to generalize a map as it enables the adaptation of the process to the characters of map features.
It also enables the enrichment of the dataset with geographic features that were implicit in the input dataset (e.g. roundabouts are only round road sections with no specific semantics in most geographical datasets).

#### [](#header-4)[Road network enrichment][3]

| Algorithm name        | Reference         				| Code 							|
|:----------------------|:----------------------------------|:------------------------------|
| Roundabouts   | [(Touya 2010)][18]	| 	[16]	|
| dual carriageways	| [(Touya 2010)][18]	| [17] |
| branching crossroads    | [(Touya 2010)][18]	| [16] |
| simple crossroads   	| [(Touya 2010)][18]	| [16]  |
| rest areas  	| [(Touya 2010)][18]	|  not yet implemented |
| highway interchange   | [(Touya 2010)][18]	| [17] |
| strokes   | [(Thomson & Richardson 1999)][20]	| [19] |
| dead end zoning   | [(Duchêne et al. 2012)][14]	| [21] |

#### [](#header-4)[Building measures][4] (for individual buildings and building groups/blocks)

| Algorithm name        | Reference         				| Code 							|
|:----------------------|:----------------------------------|:------------------------------|
| Compactness | [MacEachren 1985][38]  | [28] |
| Orientation	| [Duchêne et al. 2003][39]	| [36] |
| Elongation | [AGENT project][43]  | [37] |
| Convexity	| [AGENT project][43] 	| [37] | 	
| Squareness	| [Lokhat & Touya 2016][40] 	| [29] |
| Block density	| [AGENT project][43] 	| [34] |
| Building classification	| [(Steiniger et al. 2004)][13] 	| [33] |
| Corner buildings	| [Taillandier 2008][41] 	| [30] |
| Empty spaces in blocks	| [Gaffuri & Trévisan 2004][42]	| [31] |
| Congestion	|  [AGENT project][43]	| [32] |
| Building alignments	|  [Renard & Duchêne 2014][44]	| [35] |

#### [](#header-4)[Geographic spaces][5] from CollaGen (but can be used in other cases)

| Algorithm name        | Reference         				| Code 							|
|:----------------------|:----------------------------------|:------------------------------|
| Urban areas   | [(Touya 2010)][8] | [UrbanAreas][9]  			|
| Mountain areas	    | [(Touya 2010)][8] | [MountainAreas][10]  			|
| Rural areas   | [(Touya 2010)][8] | [RuralAreas][11]  			|
| Coastal areas	    | [(Touya 2010)][8] | [CoastalAreas][12]  		|  


#### [](#header-4)[Other algorithms][7]

| Algorithm name        | Reference         				| Code 							|
|:----------------------|:----------------------------------|:------------------------------|
|  Line characterisation | [(Plazanet 1995)][26] or [(Buttenfield 1991)][27] | [25]	| 	
|  Line coalescence | [(Mustière 1998)][24] | [23]	| 	
|  Landmarks | [(Touya & Dumont 2017)][15] | [22]	| 	
| Other network enrichments  |  | 	| 	


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
[13]: http://dx.doi.org/10.1111/j.1467-9671.2008.01085.x
[14]: http://dx.doi.org/10.1080/13658816.2011.639302
[15]: https://www.researchgate.net/publication/318463713_Progressive_Block_Graying_and_Landmarks_Enhancing_as_Intermediate_Representations_between_Buildings_and_Urban_Areas
[16]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/network/roads/CrossRoadDetection.java
[17]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/network/roads/RoadStructureDetection.java
[18]: https://onlinelibrary.wiley.com/doi/abs/10.1111/j.1467-9671.2010.01215.x
[19]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/network/roads/RoadStrokesNetwork.java
[20]: http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.202.4737
[21]: https://github.com/IGNF/CartAGen/tree/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/network/deadendzoning
[22]: https://github.com/IGNF/CartAGen/tree/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/landmarks
[23]: https://github.com/IGNF/CartAGen/tree/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/measures/coalescence
[24]: http://recherche.ign.fr/labos/util_basilic/publicDownload.php?id=2426
[25]: https://github.com/IGNF/CartAGen/tree/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/measures/section
[26]: http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.23.2465
[27]: http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.105.6922
[28]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/measures/Compactness.java
[29]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/urban/Squareness.java
[30]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/urban/CornerBuildings.java
[31]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/urban/EmptySpacesDetection.java
[32]: https://github.com/IGNF/CartAGen/tree/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/measures/congestion
[33]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/urban/BuildingClassifierSVM.java
[34]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/measures/DensityMeasures.java
[35]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/spatialanalysis/measures/UrbanAlignmentsMeasures.java
[36]: https://github.com/IGNF/geoxygene/blob/master/geoxygene-spatial/src/main/java/fr/ign/cogit/geoxygene/util/algo/OrientationMeasure.java
[37]: https://github.com/IGNF/geoxygene/blob/master/geoxygene-spatial/src/main/java/fr/ign/cogit/geoxygene/util/algo/CommonAlgorithms.java
[38]:http://www.geovista.psu.edu/publications/maceachren/MacEachren_GA-B_85.pdf
[39]:https://kartographie.geo.tu-dresden.de/downloads/ica-gen/workshop2003/duchene_et_al_v1.pdf
[40]:http://dx.doi.org/10.5311/JOSIS.2016.13.276
[41]:http://recherche.ign.fr/labos/cogit/pdf/THESES/TAILLANDIER/TheseTaillandier.pdf
[42]:https://kartographie.geo.tu-dresden.de/downloads/ica-gen/workshop2004/Gaffuri-v2-ICAWorkshop.pdf
[43]:http://agent.ign.fr/deliverable/DC1.pdf
[44]:http://dx.doi.org/10.1111/tgis.12018
