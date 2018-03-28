# Description of Li-Openshaw Line Simplification Algorithm

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------
The Li-Openshaw algorithm][5] simplifies lines based on the so-called natural principle: at a given scale, a feature with a size smaller than a certain perception limitation can be completely removed, as it cannot be properly seen.
The algorithm exists with different versions, but the one implemented here is the raster mode: a raster grid is put on top of the line (or polygon) to be simplified with the size of the cell being the minimum size that can be seen at the output scale.
Then, all the consecutive vertices of the line that are included in a same cell are replaced by a single vertex.


Examples of generalization
-------------
To do...


When to use the algorithm?
-------------
The algorithm was designed to generalize natural lines, e.g. rivers, coastlines, countour lines.


See Also
-------------
- [Visvalingam-Whyatt algorithm][2]
- [Hexagon based Raposo algorithm][3]
- [Douglas-Peucker algorithm][4]


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: /CartAGen/docs/algorithms/line/visvalingam.md
[3]: /CartAGen/docs/algorithms/line/raposo.md
[4]: /CartAGen/docs/algorithms/line/douglas_peucker.md
[5]: https://www.tandfonline.com/doi/abs/10.1080/02693799208901921
