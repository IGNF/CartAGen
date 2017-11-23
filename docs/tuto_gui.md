# Tutorial to use the CartAGen user interface
This tutorial explains how to use the CartAGen application user interface, and its main components.

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



Starting CartAGen Application
-------------
CartAgen is not an executable program yet, you need to clone the Java project into an IDE, such as [Eclipse][2] first, before being used. Please refer to the tutorials of your IDE to know how to clone a Github project.
Once the project is cloned, there are Java main programs that can be used to run CartAGen:
- [CartAGenApplicationAgent][3] runs the standard CartAGen application and opens the CartAGen GUI.
- [CartAGenApplicationNoAgent][4] runs a CartAGen application without the interfaces related to agent management.

Both are located in the cartgen-appli project, which contains all the code related to cartagen GUI.

CartAGen plugins: how it works and how to add new ones
-------------



Description of the menus of the application
-------------


GeOxygene Plugins
-------------
The CartAGen application is just an extension of the GeOxygene application, with new interface components. 
So, the application can also access to GeOxygene plugins, that work similarly to CartAGen plugins: they usually add a menu with items to access to the geoprocessing algorithms of the GeOxygene platform (data matching, conflation, topological processing, styling, etc.).

The choice of the GeOxygene plugins that are loaded in the CartAGen application is made by filling the geoxygene-configuration.xml file in the cartagen-appli project. The plugins appear in the "Plugins GeOx" menu of the GUI (see image below).

![Menu to access to GeOxygene plugins](assets/images/geox_plugins.png)

You can find more information on GeOxygene capabilities on [this website][5]. Be aware that a large part of the GeOxygene documentation is in French.

The geometry pool
-------------
The rendering of geographic features works with layers of geographic features, following the OGC standards. But sometimes, it is useful to draw additional geometries, such as final or intermediate geometries of a generalisation algorithm. 
To allow this, CartAGen uses a specific layer, called the Geometry Pool, where any geometry can be rendered with a specific style, on top of the other layers (see image below).

![The centroid of each building is displayed in the geometry pool](assets/images/geom_pool.png)

The geometry pool can be made visible, or can be hidden, by using the "visible" check box, in the CartAGen-Config>Geometry Pool menu (see image below).

![Making the geometry pool visible](assets/images/geom_pool.png)

The CartAGen-Config>Geometry Pool menu also allows the manipulation of the geometry pool (adding the selected features' geometry, clearing the pool, etc.). 
The geometry pool can also be handled in the code. The example code below shows how to retrieve the geometry pool associated with a CartAGen database:

```java
GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
```

> TIP: some interface components may be labeled in French, even when using an English locale setting. Please report an issue on Github to have it corrected quickly!

See Also
-------------
- [tutorial on data loading][8]
- [tutorial to generalize loaded data][6]
- [tutorial on CartAGen data schema][9]
- [tutorial to use agent-based generalization][7]

[1]: http://recherche.ign.fr/labos/cogit/english/accueilCOGIT.php
[2]: https://www.eclipse.org/
[3]: https://github.com/IGNF/CartAGen/blob/master/cartagen-appli/src/main/java/fr/ign/cogit/cartagen/appli/core/CartAGenApplicationAgent.java
[4]: https://github.com/IGNF/CartAGen/blob/master/cartagen-appli/src/main/java/fr/ign/cogit/cartagen/appli/core/CartAGenApplicationNoAgent.java
[5]: http://ignf.github.io/geoxygene/
[6]: /tuto_generalization_algo.md
[7]: /tuto_agents.md
[8]: /tuto_import_data.md
[9]: /tuto_schema.md
