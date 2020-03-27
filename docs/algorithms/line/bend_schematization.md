# Description of Bend Schematization Line Simplification/Caricature Algorithm

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------
Bend schematization is a caricature algorithm that removes one (or more) bend of a series while preserving the global shape of the bend series. It was proposed in [(Lecordix et al., 1997)][4] and initially implemented in the late PlaGe platform. The was implemented in CartAGen directly from the initial Ada PlaGe code. The figure below shows the principles of the algorithm. Bends in the series are identified using inflexion points, and when the middle bends are removed, the inflexion points are displaced along the axis of the series, the road is distorted to cushion the displacement of the inflexion points.

![Bend Schematization principles](/images/bend_schem_principles.png)


Examples of generalization
-------------

The figure below extracted from [(Lecordix et al., 1997)][4] shows the results of the algorithm. In the image below, we can see two black points that delineate the geometry that was processed in the algorithm. The algorithm does not work if the line is not homogeneous in sinuosity, so the algorithm should only be used after the segmentation of the road into homogeneous parts.

![Bend Schematization results](/images/bend_schem_results.png)


When to use the algorithm?
-------------
The algorithm is part of the toolbox to generalise mountain roads that contain sinuous bend series. The algorithm is rather used when there is no room to enlarge the bend series. Please read [the paper][5] by Cécile Duchêne to learn more on the orchestration of algorithms for mountain roads.




See Also
-------------
- [Accordion algorithm][2]

- [Return to home page][3]


[1]: https://umrlastig.github.io/guillaume-touya/
[2]: /accordion.md
[3]: https://ignf.github.io/CartAGen
[4]: https://link.springer.com/article/10.1023/A:1009736628698
[5]: http://recherche.ign.fr/labos/util_basilic/publicDownload.php?id=3044
