# Description of Douglas & Peucker Line Simplification Algorithm

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------

The Douglas & Peucker algorithm is a line filtering algorithm, which means that it filters the vertices of the line (or polygon) to only retain the most important ones to preserve the shape of the line.
The algorithm iteratively searches the most characteristics vertices of portions of the line and decides to retain or remove them given a distance threshold (see figure below).

![Line simplified by the Douglas & Peucker algorithm](/images/Douglas-Peucker_animated.gif)

The initial paper can be found [here][5]
More details from Wikipedia [here][7].

| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| threshold    | the distance under which a vertex is removed from the line 	| double (meters) 			| 								|


Examples of generalization
-------------


When to use the algorithm?
-------------
The algorithm tends to unsmooth geographic lines, and is rarely used to simplify geographic features. But it can be very useful to quickly filter the vertices of a line inside another algorithm.


See Also
-------------
- [Visvalingam-Whyatt algorithm][2]
- [Hexagon based Raposo algorithm][3]
- [Li-Openshaw algorithm][4]

- [Return to home page][6]


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: /visvalingam.md
[3]: /raposo.md
[4]: /li_openshaw.md
[5]: http://dx.doi.org/10.3138/FM57-6770-U75U-7727
[6]: https://ignf.github.io/CartAGen
[7]: https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm