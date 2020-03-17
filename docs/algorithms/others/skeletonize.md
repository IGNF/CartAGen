# Description of the Skeletonize Algorithm

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------
The skeletonize algorithm collapses a polygon into a line based on the straight skeleton of the polygon.
If several polygons, or lines, are initially connected (i.e. they touch each other) the skeleton is also connected by the algorithm.

The algorithm was first proposed by [Haunert & Sester 2008][3] and the implementation is inspired from [Felkel & Obdrzalek][4].

| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| no parameter    |  |   |  	|


Examples of generalization
-------------
![A river area skeletonized](/images/straight_skeleton.png)

When to use the algorithm?
-------------
The algorithm can be used to collapse thin areas into lines (e.g. rivers, hedges, taxiways). When the shapes of the areas are not very thin, or are very complex, the algorithm still provides good skeleton but can be very slow.


Available variations
-------------
There is a variation of the skeletonize algorithm available in the same Java class. The variation computes the medial axis (based on the TIN built inside the polygon) instead of the straight skeleton. The algorithm is faster but sometimes gives less good results than the straight skeleton.
![A river area skeletonized](/images/medial_axis_skeleton.png)

See Also
-------------
- [Spinalize algorithm][2]

- [Return to home page][5]

[1]: https://umrlastig.github.io/guillaume-touya/
[2]: /spinalize.md
[3]: https://link.springer.com/article/10.1007%2Fs10707-007-0028-x
[4]: http://www.dma.fi.upm.es/mabellanas/tfcs/skeleton/html/documentacion/Straight%20Skeletons%20Implementation.pdf
[5]: https://ignf.github.io/CartAGen
