# Tutorial to the advanced use of the AGENT model
This tutorial explains how to use the AGENT model in a more advanced way than in the [agent-based generalization tutorial][2]. Be aware that this implementation is for now mainly based on research code that has not been consolidated. Many bugs still remain and some functionalities described in the papers describing the model are not implemented in CartAGen.

> - Date 23/07/2018.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.

Available agent types, constraints, actions
-------------
To be done...

| Constraint name     | Applicable agents  		| Code 							|
|:--------------------|:----------------------|:------------------|
| Convexity  | 	Buildings	| [CartAGen class][13]	|
| Elongation	| Buildings	|  [CartAGen class][14] |
|  Granularity | Buildings	| [CartAGen class][15]  |
|  Local width	| Buildings	| [CartAGen class][16]  |
|  Orientation	| Buildings	| [CartAGen class][17]  |
|  Size | Buildings	| [CartAGen class][18]  |
|  Squareness | Buildings	| [CartAGen class][19]  |
|  Proximity | 	Blocks	|  [CartAGen class][20] |
|  Density | Blocks		| [CartAGen class][21]  |
|  Spatial Distribution | Blocks		| [CartAGen class][22]  |
|  Street Density | Towns		| [CartAGen class][23]  |
|  Coalescence | Roads, Rivers	| [CartAGen class][24]  |


| Action name  | Applicable agents | Applicable constraints	| Code | Description |
|:-------------|:------------------|:------------------|:----------|:------------|
|   | 		| 	|   | 	|
| 	|   	|   |   | 	|

Changing the default constraints and actions
-------------
To be done...


Implementing new agent types, constraints, actions
-------------
To be done...


Adapting the agent life cycle
-------------
To be done...


Public Deliverables of the AGENT project
-------------
The AGENT project was funded by the European Commission between 1997 and 2000 as part of the Esprit Programme. The project further developed the AGENT model from the ideas of Anne Ruas, and implemented the model in the LAMPS2 platform. The public deliverables contain valuable information about the constraints, measures, and algorithms developed during the project:

- Deliverable DA1, [Generalisation Modelling using an agent paradigm][8]
- Deliverable DA2, [Constraint Analysis][7]
- Deliverable DC1, [Selection of Basic Measures][9]
- Deliverable DC4, [Specifications for measures on MESO level & organisations][10]
- Deliverable DD2, [Selection of Basic Algorithms][11]
- Deliverable DD3, [Strategic Algorithms Using Organisations][12]


See Also
-------------
- [Advanced use of CartACom][3]
- [Advanced use of CollaGen][4]
- [Advanced use of GAEL][5]
- [Advanced use of DIOGEN][6]


[1]: https://umrlastig.github.io/guillaume-touya/
[2]: /tuto_agents.md
[3]: /agents/CartACom_advanced.md
[4]: /agents/CollaGen_advanced.md
[5]: /agents/GAEL_advanced.md
[6]: /agents/DIOGEN_advanced.md
[7]: /agents/AGENT_DA2.pdf
[8]: /agents/DA1.pdf
[9]: /agents/DC1.pdf
[10]: /agents/DC4.pdf
[11]: /agents/DD2.pdf
[12]: /agents/DD3.pdf
[13]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/building/Convexity.java
[14]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/building/Elongation.java
[15]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/building/Granularity.java
[16]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/building/LocalWidth.java
[17]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/building/Orientation.java
[18]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/building/Size.java
[19]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/building/Squareness.java
[20]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/block/Proximity.java
[21]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/block/Density.java
[22]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/block/BuildingsSpatialDistribution.java
[23]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/town/StreetDensity.java
[24]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/agents/core/constraint/section/Coalescence.java
