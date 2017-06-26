# Understanding CartAGen data schema for generalization
CartAGen uses a centralized data schema to load and generalize geographic information, following the principles of multiple-representation databases (see [Balley et al. 2004][1] for instance). This page explains how it works, describes the content of the centralized schema, and shows how to extend it if necessary.

> - Date 22/06/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



What is CartAGen data schema for generalization?
-------------

![simplified view of the schema](assets/images/schema_gene_simplifie.png)

Description of the interfaces of the centralized schema
-------------

#### [](#header-4)Administrative schema
![administrative schema](assets/images/admin_schema.png)

#### [](#header-4)Airport schema
[ ![airport schema](assets/images/airport_schema_small.png) ](assets/images/airport_schema.png)

#### [](#header-4)Hydrography schema
[ ![hydrography schema](assets/images/hydro_schema_small.png) ](assets/images/hydro_schema.png)

Implementations of the centralized schema
-------------
There are two available implementations of the centralized schema:
- [a default one][3] that only implements the interfaces without any additional attribute,
- an OSM implementation that also stores raw tags and OSM metadata on each feature.

How to extend the centralized schema
-------------

This part of the tutorial explains how to extend the centralized schema when a type of feature you want to process does not exist in the schema yet. A use case based on OpenStreetMap (OSM) cycle ways illustrates here the different steps.


### [](#header-3) Add a new interface to the centralized schema

The first step is to check if there is an interface that models cycle ways in the CartAGen centralized schema. Now there is one existing, but let's pretend there is none. 
We want to model cycle ways as a network, so the new interface has to extend the _INetworkSection_ interface. If our new type of feature were a point feature, it would extend _IGeneObjPoint_, _IGeneObjLin_ for line features not structured in networks, and _IGeneObjSurf_ for polygon features.

The code below defines the new interface ICycleWay, whose fields are derived from common characteristics of OSM cycle ways: the cycle way surface and its width (named realWidth here to avoid confusions with the getWidth() method used in CartAGen to retrieve map symbol width).
```java
public interface ICycleWay extends INetworkSection {

  /**
   * The getter for the surface of the cycle way, e.g. "paved" or "asphalt".
   * @return
   */
  public String getSurface();

  public void setSurface(String surface);

  /**
   * The getter for the real or "ground" width of the cycle way, different from
   * the getWidth method inherited from INetworkSection that gives the symbol
   * width.
   * @return
   */
  public double getRealWidth();

  public void setRealWidth(double width);
}
```

### [](#header-3) Implementing the new interface

Il existe une implémentation par défaut des interfaces d'objets géographiques de CartAGen dans le package fr.ign.cogit.cartagen.core.defaultschema, mais il en existe d'autres, par exemple :
* une implémentation dite "gothic" dont les objets peuvent être utilisés dans le logiciel Clarity (fr.ign.cogit.cartagen.gothic.gothicBasedCoreSchema)
* plusieurs implémentation pour les données du PEA REP (VMAP, MGCP++, VMAP1++) (geoxygene-cartagen fr.ign.cogit.cartagen.pearep)
* une implémentation pour les données OSM, pour laquelle les objets contiennent tous leurs tags OSM (fr.ign.cogit.cartagen.osm.osmschema)

Pour réellement importer nos pistes cyclables, il faut choisir une implémentation et implémenter notre interface ICycleWay dans cette implémentation. Dans notre exemple, nous prenons l'implémentation OSM et créons la classe suivante :

```java
public class OsmCycleWay extends OsmNetworkSection implements ICycleWay {

  @Override
  public double getWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getInternWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public INetworkNode getInitialNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public INetworkNode getFinalNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    // TODO Auto-generated method stub
  }

  @Override
  public String getSurface() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setSurface(String surface) {
    // TODO Auto-generated method stub
  }

  @Override
  public double getRealWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setRealWidth(double width) {
    // TODO Auto-generated method stub
  }
```

Étendre ici la classe OsmNetworkSection qui étend elle-même OsmGeneObj permet de bénéficier des méthodes communes aux objets OSM, en rapport avec les tags OSM.
On complète alors les méthodes déclarées dans l'interface :
```java
  private String surface;

  @Override
  public String getSurface() {
    return surface;
  }

  @Override
  public void setSurface(String surface) {
    this.surface = surface;
  }

  private double realWidth;

  @Override
  public double getRealWidth() {
    return realWidth;
  }

  @Override
  public void setRealWidth() {
    this.realWidth = width;
  }
```

### [](#header-3) Extended the factory of the chosen implementation

La construction de certains objets (comme les noeuds de réseau) peut se faire lors de calculs et pour éviter que le code de ces calculs soit dépendant d'une des implémentations, on fait appel à une factory pour construire les objets. Par exemple, la factory abstraite possède la méthode createCycleWay(ILineString line) et il existe une factory par implémentation qui construit par exemple des OsmCycleWay ou des GothicCycleWay. 

On commence par faire un (ou plusieurs) constructeur(s) dans notre classe OsmCycleWay :

```java
  public OsmCycleWay(ILineString line) {
    super();
    this.setGeom(line);
  }
```

Puis, on ajoute la méthode construisant les objets dans la factory abstraite (AbstractCreationFactory dans le package fr.ign.cogit.cartagen.core.genericschema) :

```java
  @SuppressWarnings("unused")
  public ICycleWay createCycleWay(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICycleWay");
    return null;
  }
```

On surcharge ensuite cette méthode dans la factory de l'implémentation OSM (fr.ign.cogit.cartagen.osm.osmschema.OSMSchemaFactory) :

```java
  @Override
  public ICycleWay createCycleWay(ILineString line) {
    return new OsmCycleWay(line);
  }
```

Enfin, il y a une dernière étape spécifique aux implémentations OSM car le fichier initial .osm contient en vrac toutes les données : il faut compléter une méthode générique de la factory qui lance la bonne méthode de la factory en fonction du type de l'entité OSM (évaluée grâce à ses tags):

```java
	public OsmGeneObj createGeneObj(Class<?> classObj, OSMResource resource,
			Collection<OSMResource> nodes) {
		if (IRoadLine.class.isAssignableFrom(classObj)) {
			ILineString line = OsmGeometryConversion.convertOSMLine(
					(OSMWay) resource.getGeom(), nodes);
			return (OsmGeneObj) this.createRoadLine(line, 0);
		}

		...

	        if (ICycleWay.class.isAssignableFrom(classObj)) {
                        ILineString line = OsmGeometryConversion
                          .convertOSMLine((OSMWay) resource.getGeom(), nodes);
                         return (OsmGeneObj) this.createCycleWay(line);
                }
		// TODO
		return null;
	}
```

### [](#header-3) Creating a dedicated population in the dataset 

Une fois que l'on a construit nos objets géographiques comme des objets Java dans une classe adaptée, on ne peut pas les utiliser directement dans CartAGen. La structure de stockage des données dans CartAGen est expliquée dans ce [[Documentation_persistance_CartAGen|tutoriel sur la persistence]]. Un projet est représenté par un objet document de la classe ''CartAGenDoc'', qui contient une ou plusieurs bases de données (par exemple une base pour la BD Topo et une pour les données OSM). Chaque base de données est associée à un unique objet ''CartAGenDataset'' dont la fonction est de stocker et de permettre l'accès au données chargées dans la base de données. Il faut donc stocker nos données dans le dataset associé à la base de données OSM créé au chargement.
Dans le dataset, les données sont stockées dans des populations, instances de la l'interface ''IPopulation'' : il y a par exemple, une population pour les routes, les bâtiments, les cours d'eau, etc. Pour nos nouvelles données, il faut créer une nouvelle population adaptée et donc modifier la classe ''CartAGenDataset''. Certaines implémentations avec des données spécifiques ont une classe de dataset qui étend ''CartAGenDataset'' et dans ce cas, c'est la sous-classe qu'il faut modifier (de la même manière que ''CartAGenDataset''). C'est le cas d'OSM, pour lequel il faut donc modifier la classe ''OsmDataset'' du package *fr.ign.cogit.cartagen.osm.importexport*. Quatre types de modifications sont nécessaires dans la classe :

* Ajout du nom de la population que l'on stocke dans une variable statique :

```java
public class OsmDataset extends CartAGenDataSet {

	// ///////////////////////////////////////
	// STANDARD NAMES OF DATASET POPULATIONS
	// ///////////////////////////////////////

	public static final String TREE_POINT_POP = "trees";
	public static final String CYCLEWAY_POP = "cycleWay";
```


* On complète la méthode getPopNameFromObj(IFeature obj) en ajoutant avant le return final :

```java
    if (obj instanceof ICycleWay) {
      return OsmDataset.CYCLEWAY_POP;
    }
```

* On complète la méthode getPopNameFromClass(Class<?> classObj) en ajoutant avant le return final :

```java
    if (ICycleWay.class.isAssignableFrom(classObj)) {
      return OsmDataset.CYCLEWAY_POP;
    }
```


* Enfin, on ajoute une méthode de raccourci permettant d'accéder directement, dans le code d'un programme, à la population des pistes cyclables quand on le souhaite :

```java
  /**
   * Gets the cycleways of the dataset
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ICycleWay> getCycleWays() {
    return (IPopulation<ICycleWay>) this.getCartagenPop(
        OsmDataset.CYCLEWAY_POP, ICycleWay.FEAT_TYPE_NAME);
  }
```

### [](#header-3) Displaying the new features

Le fait de stocker nos nouvelles données dans le dataset ne permet pas directement de les afficher car le dataset ne gère pas l'affichage, qui est fait à partir de layers. Un layer commande l'affichage d'une ou plusieurs populations du dataset, et un ''LayerManager'' gère l'ensemble des layers via un objet de la classe ''LayerGroup''.
Comme pour le dataset, il existe un ''LayerGroup'' générique et des classes qui étendent ce layer, ''OsmLayerGroup'' pour le cas des données OSM. Ces classes doivent être modifiées pour l'intégration des pistes cyclables dans les layers, en cinq étapes :

![cycleway displayed with pink dashes](assets/images/Affichage_piste_cyclable.png)



[1]: http://www.tandfonline.com/doi/abs/10.1080/13658810410001672881
[2]: https://github.com/IGNF/geoxygene
[3]: https://github.com/IGNF/CartAGen/tree/master/cartagen-core/src/main/java/fr/ign/cogit/cartagen/core/defaultschema
