# Description of the Building Aggregation Algorithm by N. Regnauld

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.


Description of the algorithm
-------------
This algorithm aggregates two close buildings by determining the joining polygon that makes the most compact shape.

The image below shows how edges of each polygon are extended to straight lines and the intersections between these straight lines are used to create different possible aggregated polygons. In the case below, the polygon b) is preferred by the algorithm.

![Principles of the aggregation algorithm](/images/regnauld_aggreg.png)

See more details about the algorithm in [N. Regnauld's PhD][3] (in French).

Examples of generalization
-------------

![Example of aggregation from N. Regnauld's PhD](/images/regnauld_results_1.png)

![Example of aggregation from N. Regnauld's PhD](/images/regnauld_results_2.png)

When to use the algorithm?
-------------

This algorithm was initially developed for the aggregation of two close buildings. It can be extended to other man-made polygon features, what C. Duchêne calls *small compact features*, *e.g.* sports fields, greenhouses or similar map features.

See Also
-------------
- [Morphological amalgamation algorithm][2]


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: /algorithms/line/morpho_amalgamation.md
[3]: http://recherche.ign.fr/labos/cogit/pdf/THESES/REGNAULD/These_Regnauld_1998.pdf
