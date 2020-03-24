# Description of Accordion Line Simplification/Caricature Algorithm

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------
The Accordion algorithm is dedicated to the caricature of sinuous bend series. Like the musical instrument, the Accordion algorithm stretches the road to enlarge each bend of the series. The algorithm was developed in the 90's by François Lecordix at IGN France, and details can be found in [this article][4]. The figure below extracted from the paper shows how each is enlarged at its base, following the axis of the bend series.

![Accordion principles](/images/accordion_principles.png)

There is no parameter in this implementation of Accordion, the algorithm is only monitored by the output scale of the map.

Examples of generalization
-------------
The image below shows the generalisation of a road with bend series at the 1:50k scale (but the symbols are the ones optimised for 1:10k). The road generalised is the one selected in yellow, and the line in red is the output of Accordion. It can be noticed that this algorithm does not preserve road connections, so diffusion algorithms have to be coupled to Accordion to make sure roads remain connected to each other.

![Accordion example](/images/accordion.png)


When to use the algorithm?
-------------
The algorithm is part of the toolbox to generalise mountain roads that contain sinuous bend series. The algorithm is rather used when there is room to enlarge the bend series. When the diffusion of the enlargement to the connected roads causes more legibility problems than the ones solved by Accordion, [Bend schematization][2] should be preferred. Please read [the paper][5] by Cécile Duchêne to learn more on the orchestration of algorithms for mountain roads.



See Also
-------------
- [Bend schematization algorithm][2]

- [Return to home page][3]

[1]: https://umrlastig.github.io/guillaume-touya/
[2]: /bend_schematization.md
[3]: https://ignf.github.io/CartAGen
[4]: https://link.springer.com/article/10.1023/A:1009736628698
[5]: http://recherche.ign.fr/labos/util_basilic/publicDownload.php?id=3044
