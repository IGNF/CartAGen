# Tutorial on how to generalize data loaded in CartAGen
The building blocks of map generalization are the algorithms that are triggered on one map object or on group of map objects. Before orchestrating their application on a complete map, it is necessary to learn of to test them on a sample dataset, and that's the aim of this tutorial.
The tutorial:
- first explains how to trigger the available from the GUI menu buttons; 
- then, the tutorial describes how these algorithms can be located in the CartAGen library;
- then, for developers (CartAGen is mainly dedicated to developers for now), the following section of the tutorial shows an example where an algorithm is encapsulated in a piece of code to enlarge and displace of the buildings of a dataset;
- finally the tutorial explains how to add one of the any algorithms of the research literature taht are not implemented in CartAGen yet.

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Triggering algorithms with the menu buttons
-------------
#### [](#header-4)Generic algorithms

#### [](#header-4)Theme specific algorithms


Where are the algorithms in the Java library?
-------------

> TIP: To know where the code of an algorithm is, look at the code of the menu button that triggers the algorithm (in the package `fr.ign.cogit.cartagen.appli.core.themes` of cartagen-appli module).

> There is a list of the available algorithms with descriptions [at the bottom of this page][2]


Example: enlarging and displacing buildings in the sample dataset
-------------
##### [](#header-5) What we want to do
What we want to do in this example is to generalize the buildings of the [sample dataset][3] to the 1:30,000 scale, by enlarging them, simplifying them and removing symbol overlaps (between two buildings and between buildings and road symbols) by a [contextual displacement][5].
The contextual displacement works on a group of buildings to displace them optimally in the free space around them, so first, groups of buildings have to be computed, and each of these groups will be generalize as a whole.

##### [](#header-5) Set the scale and update symbol sizes

##### [](#header-5) Compute the building groups

##### [](#header-5) Loop on the building groups

##### [](#header-5) Enlarge and simplify all the buildings of the group

##### [](#header-5) Displace the buildings in the group

Guidelines to add a new generalization algorithm into CartAGen
-------------
This explains shows the best practices to add a new algorithm in CartAGen. The guidelines are examplified with the addition of the amalgamation algorithm from [Damen et al. (2008)][6].

#### [](#header-4)Where to write the code of the algorithm?
The algorithms are all located in the same package :```fr.ign.cogit.cartagen.algorithms```. The package contains several subpackages that gather "similar" algorithms even though the categorization is quite rough.
In our example, the algorithm amalgamates a group of close buildings in a building block, so we choose to add it in the ```fr.ign.cogit.cartagen.algorithms.block``` package. This package contains a class (```fr.ign.cogit.cartagen.algorithms.block.BuildingAggregation```) for building aggregation (quite similar to amalgamation) algorithms in which we will be adding the new algorithm.

#### [](#header-4)Is there a template to write an algorithm in CartAGen?
No, there is no template to write the code of a new algorithm in CartAGen. The algorithm can be coded as a public method in a class that contains several algorithms, as a static class (that's the chosen way in this tutorial but not the best way), or as a class.
However, there are some guidelines to make the algorithm useful for all the users of CartAGen:
- use the centralized schema interfaces as input/output of the algorithm: e.g. if the algorithm processes buildings, use the ```IBuilding``` interface.
- if the algorithm is generic in terms of features, prefer the geometry interfaces (IPoint, ILineString, or IPolygon) as input/output of the algorithm. For instance, the Douglas & Peucker algorithm is not dedicated to a specific features, so the CartAGen implementations use ILineString (and IPolygon) as input and output of the algorithm, because it can process any feature with a line (or polygon) geometry.

```java
    public static Collection<IBuilding> computeMorphologicalAmalgamation(Collection<IBuilding> buildings, double bufferSize){
        Collection<IBuilding> outCollection = new HashSet<>();
        
		// TODO
        
		return outCollection;
    }
```

#### [](#header-4)Documenting the added algorithm

See Also
-------------
- [List of available algorithms][2]
- [tutorial on data loading][3]
- [tutorial to generalize loaded data with agent-based processes][4]


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: /algorithms.md
[3]: /tuto_import_data.md
[4]: /tuto_agents.md
[5]: /algorithms/buildings/random_displacement.md
[6]: https://kartographie.geo.tu-dresden.de/downloads/ica-gen/workshop2008/04_Damen_et_al.pdf
