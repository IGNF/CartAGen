# Description of Visvalingam & Whyatt Line Simplification Algorithm

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------
This algorithm performs a line simplification that produces less angular results than the filtering algorithm of [Douglas and Peucker][2].
The algorithm was proposed by [Visvalingam & Whyatt 1993][5].

The principle of the algorithm is to select the vertices to delete (the less characteristic ones) rather than choosing the vertices to keep (in the Douglas and Peucker algorithm). 
To select the vertices to delete, there is an iterative process, and at each iteration, the triangles formed by three consecutive vertices are computed. If the area of the smallest triangle is smaller than a threshold ("area_tolerance" parameter), the middle vertex is deleted, and another iteration starts.

| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| area_tolerance   | the minimum triangle area to keep a vertex in the line |  double (m²) |  	|


Examples of generalization
-------------
![A coastline simplified by the Visvalingam-Whyatt algorithm](/images/visvalingam_coastline.png)

When to use the algorithm?
-------------
The algorithm is relevant for the simplification of natural line or polygon features such as rivers, forests, or coastlines.

Available variations
-------------
In the same Java class, there is a variation of the algorithm that is topology safe, which means that if other features are passed as parameters of the algorithm, the variation algorithm checks that the simplified line does not intersect any of these additional lines.
If the removal of a point creates an intersection, the removal is just backtrack and the algorithm continues with the next vertex.

See Also
-------------
- [Douglas-Peucker algorithm][2]
- [Hexagon based Raposo algorithm][3]
- [Li-Openshaw algorithm][4]

- [Return to home page][6]


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: /douglas_peucker.md
[3]: /raposo.md
[4]: /li_openshaw.md
[5]: http://www.tandfonline.com/doi/abs/10.1179/000870493786962263
[6]: https://ignf.github.io/CartAGen
