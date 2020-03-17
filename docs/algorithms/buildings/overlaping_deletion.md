# Description of the Overlaping Buildings Deletion Algorithm for Building Groups

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------
Algorithm to delete the buildings of a block using the overlapping rate of the buildings: at each iteration, the building with the highest overlapping rate is deleted if this rate is higher than a threshold. The overlapping rate is the area of the building divided by the sum of the areas of intersections with the overlapping buildings. If two buildings have the same overlapping rate, the smallest one is ranked first.

| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| minimumRate    | the overlapping ratio over which a building is removed	| double, % 			| 			0.4					|

Examples of generalization
-------------
To be filled...


When to use the algorithm?
-------------
This algorithm is useful as a first path when a building block is overcrowded and there is no need to compute a smart deletion yet. It happens frequently when buildings are to generalized to scales below 1:50k.



See Also
-------------
- [Bend schematization algorithm][2]


[1]: https://umrlastig.github.io/guillaume-touya/
[2]: /algorithms/line/bend_schematization.md
