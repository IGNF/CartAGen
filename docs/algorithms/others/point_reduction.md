# Description of K-Means Point Reduction Algorithm

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Description of the algorithm
-------------

The algorithm computes a K-Mean clustering of the points using the Euclidean distance between points, and then replaces each cluster by a single point.

More details from Wikipedia on [K-Means clustering][3].


| Parameter name        | Description         				| Type 							| Default value			|
|:----------------------|:----------------------------------|:------------------------------|:--------------------------------------------------|
| k    | the number of clusters to build (i.e. the number of points after reduction	| integer			| 								|
| shrinkRatio    | if k is set to 0, the algorithm rather uses this ratio to compute how many points are kept (0.5 means half the points are kept) 	| double (between 0 and 1) 			| 								|

Examples of generalization
-------------
![K-Means point reduction using the centroid of the cluster](/images/kmeans_centroid.png)
![K-Means point reduction using the nearest point to the centroid of the cluster](/images/kmeans_point.png)

When to use the algorithm?
-------------
The algorithm is dedicated to features represented by point clouds.


See Also
-------------
- [point cloud covering algorithm][2]

- [Return to home page][4]


[1]: https://umrlastig.github.io/guillaume-touya/
[2]: /algorithms/line/point_cover.md
[3]: https://en.wikipedia.org/wiki/K-means_clustering
[4]: https://ignf.github.io/CartAGen
