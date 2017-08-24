# Description of the Ruas Displacement Algorithm for Buildings in a Block

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------
This algorithm amalgamates and simplifies close buildings while retaining their square shapes, using sequences of morphological dilation and erosion (with a square instead of the standard circle to preserve building shapes). The algorithm works as follows:

1.  dilation
2.  erosion
3. 	erosion
4.  dilation
5.  simplification

That is the dilation step that amagamates the buildings when they are close enough, i.e. if the gap between the buildings is smaller than the size of the dilation.

![Building amalgamation based on morphological operators](docs/assets/images/damen_et_al_principles.png)

The initial paper by Damen et al., presented at 12th ICA Workshop on Generalisation and Multiple Representation in 2008 (Montpellier, France), can be found [here][2]
The code of the algorithm can be found [here][3].

Examples of generalization
-------------
![Initial buildings](docs/assets/images/morpho_amalgamation_before.png)
![Amalgamated building](docs/assets/images/morpho_amalgamation_after.png)


When to use the algorithm?
-------------
The algorithm is dedicated to the attached or semi-detached alignments of buildings that we can usually find in countries such as UK or the Netherlands.


See Also
-------------
- [Regnauld's building aggregation algorithm][4]


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: https://kartographie.geo.tu-dresden.de/downloads/ica-gen/workshop2008/04_Damen_et_al.pdf
[3]: https://github.com/IGNF/CartAGen/blob/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/algorithms/block/BuildingsAggregation.java
[4]: docs/algorithms/buildings/aggregation_regnauld.md
