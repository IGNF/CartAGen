# CartAGen
CartAGen is a plugin for [GeOxygene][2] dedicated to cartographic generalisation maintained by [COGIT research team][1] from IGN France, the French national mapping agency. CartAGen is a research platform and is mainly focused on map generalization research needs.

> - Date 18/05/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description
-------------

CartAGen is a Java research platform dedicated to map generalization and [built upon many years of research at IGN France][6]. CartAGen can be seen as a plugin for [GeOxygene][2] Java platform or as a standalone platform.
It contains implementations of many generalization algorithms of the literature, but most of all, it contains implementations of [several map generalization processes][7] that automatically orchestrate these algorithms, including complete or partial implementations of the multi-agents based [AGENT][9], [CartACom][10], [GAEL][11], and [CollaGen][12] models.

AGENT generalization of a small town to 1:60k:
![AGENT generalization of a small town to 1:50k](docs/assets/images/AGENT_results.png)

AGENT mountain road generalization:
![AGENT mountain road generalization](docs/assets/images/agent_roads.png)

CartACom generalization to 1:50k:
![CartACom generalization to 1:50k](docs/assets/images/cartacom_results.png)

Getting Started
-------------

- [How to load a geographical dataset into CartAGen?][3]
- [How to trigger generalization algorithms in CartAGen?][4]
- [How to use the agent-based generalization processes?][5]
- [Understanding CartAGen data schema for generalization][8]


Watch videos
-------------

[![GAEL relief generalization](https://img.youtube.com/vi/b3wlWVkD74Y/0.jpg)](https://www.youtube.com/watch?v=b3wlWVkD74Y)
[![AGENT mountain road generalization](https://img.youtube.com/vi/Ns42t_hwAXw/0.jpg)](https://www.youtube.com/watch?v=Ns42t_hwAXw)

Implemented generalization algorithms
-------------

A list, not exhaustive yet, of the generalization algorithms available in CartAGen platform.

#### [](#header-4)Line simplification algorithms

| Algorithm name        | Reference         				| Code 							| Description of the implementation 				|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| Douglas & Peucker     | [Douglas & Peucker 1973][13] 		| JTS implementation  			| basic implementation								|
| Visvalingam-Whyatt	| [Visvalingam & Whyatt 1993][14]	| [VisvalingamWhyatt.java][15]  | topology safe implementation (checks potential intersections when removing a vertex)	|
| Hexagon based         | [Raposo 2013][16]      			| [RaposoSimplification.java][17] | basic implementation of all versions of the algorithm 	|
| Accordion          	| [Plazanet 1996][18] 				| [BendSeriesAlgorithm.java][19]  | Port from the initial ADA code					|
| Bend schematisation   | [Lecordix et al 1997][20] 		| [BendSeriesAlgorithm.java][19]  | Port from the initial ADA code					|

#### [](#header-4)Building algorithms (for individual buildings and building groups)

| Algorithm name        | Reference         				| Code 							| Description of the implementation 				|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| Simplification	    | Ruas 1988 [reported in AGENT project][21] | [from GeOxygene][22]  			| implemented by Julien Gaffuri (code comments mostly in French)	|
| Least squares squaring	| [Lokhat & Touya 2016][23]	| [SquarePolygonLS.java][24]  | non linear least squares optimize the position of the building vertices, rectifying almost 90° angles	|
| Enlarge        		| [reported in AGENT project][21]   | JTS implementation | 	|
| Enlarge to rectangle	| [reported in AGENT project][21] 	| uses JTS smallest surrounding rectangle (SSR)  | 					|
| Rotate   				| [reported in AGENT project][21]	| JTS implementation  |					|
| Random displacement	| never published (@Julien Gaffuri)	| [BuildingDisplacementRandom.java][25]  |	iteratively, a building is randomly displaced (with very small displacements), until the global legibility is optimized (a gradient descent is used)	|
| displacement in block | [Ruas 1999][26]					| [BuildingDisplacementRuas.java][27]  |					|

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