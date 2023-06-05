/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema;

/**
 * Default abstract implementation for the interface ICartAGenGeoObj. This class
 * does not handle the geometry itself but through an association (delegation)
 * to IGeneralisable
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.persistence.CollectionType;
import fr.ign.cogit.cartagen.core.persistence.Encoded1To1Relation;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraphLinkableFeature;
import fr.ign.cogit.geoxygene.contrib.graphe.INode;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.util.ReflectionUtil;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import org.apache.logging.log4j.LogManager;

/**
 * @author CDuchene
 */
public class GeneObjDefault extends FT_Feature implements IGeneObj {

  private static Logger logger = Logger
      .getLogger(GeneObjDefault.class.getName());

  /**
   * The initial geometry of the GeneObj
   */

  private IGeometry initialGeom = null;

  /**
   * Id of the symbol associated to {@code this}
   */
  private int symbolId = -1;

  /**
   * L'objet support associated to {@code this} AJOUT KUSAY
   */
  // private BDSupportObj supportObj = null;

  /**
   * getter for initialGeom
   */
  @Override
  public IGeometry getInitialGeom() {
    return this.initialGeom;
  }

  /**
   * setter for initialGeom
   */
  @Override
  public void setInitialGeom(IGeometry initialGeom) {
    if (initialGeom != null)
      this.initialGeom = (IGeometry) initialGeom.clone();
  }

  // Attribute eliminated and getter/setter
  /**
   * True if the object has to be considered as eliminated by the generalisation
   * process, False otherwise
   */
  private boolean eliminated = false;

  /**
   * getter for eliminated
   */
  @Override
  public boolean isEliminated() {
    return this.eliminated;
  }

  /**
   * setter for eliminated
   */
  public void setEliminated(boolean elim) {
    this.eliminated = elim;
    this.setDeleted(elim);
  }

  // Attribute hasBeenCreated and getter/setter
  /**
   * True if the object has been created during the generalisation process,
   * False if it is part of the original data
   */
  private boolean hasBeenCreated = false;

  /**
   * getter for hasBeenCreated
   */
  @Override
  public boolean hasBeenCreated() {
    return this.hasBeenCreated;
  }

  /**
   * setter for hasBeenCreated
   */
  @Override
  public void setBeenCreated(boolean created) {
    this.hasBeenCreated = created;
  }

  // Attribute stemmingFromN1Transfo and getter/setter
  /**
   * True if the object has been created during a n-1 generalisation operation
   * (e.g. aggregation), False otherwise
   */
  private boolean stemmingFromN1Transfo;

  /**
   * getter for stemmingFromN1Transfo
   */
  @Override
  public boolean isStemmingFromN1Transfo() {
    return this.stemmingFromN1Transfo;
  }

  /**
   * setter for stemmingFromN1Transfo
   */
  public void setStemmingFromN1Transfo(boolean stemmingFromN1Transfo) {
    this.stemmingFromN1Transfo = stemmingFromN1Transfo;
  }

  // Attribute stemmingFromMNTransfo and getter/setter
  /**
   * True if the object has been created during a m-n generalisation operation
   * (e.g. typification), False otherwise
   */
  private boolean stemmingFromMNTransfo;

  /**
   * getter for stemmingFromMNTransfo
   */
  @Override
  public boolean isStemmingFromMNTransfo() {
    return this.stemmingFromMNTransfo;
  }

  /**
   * setter for stemmingFromMNTransfo
   */
  public void setStemmingFromMNTransfo(boolean stemmingFromMNTransfo) {
    this.stemmingFromMNTransfo = stemmingFromMNTransfo;
  }

  // ///////////////////////////////////////////
  // Attribute antecedents and getter/setters
  // ///////////////////////////////////////////
  /**
   * /** The antecedents set (bidirectional reference, automatically managed).
   * Set of CartAGen objects from which {@code this} is the generalisation
   * result.
   */
  private Set<IGeneObj> antecedents = new HashSet<IGeneObj>();

  /**
   * Getter for antecedents.
   * @return the antecedents. It can be empty but not {@code null}.
   * 
   */
  // TODO Probleme la méthode surchargeant ne peut pas retourner un type plus
  // précis
  // que la méthode qu'elle surcharge... je ne sais pas comment gérer ça.
  @Override
  public Set<IGeneObj> getAntecedents() {
    return this.antecedents;
  }

  /**
   * Setter for antecedents. Also updates the reverse reference from each
   * element of antecedents to {@code this}. To break the reference use
   * {@code this.setAntecedents(new HashSet<IGeneObj>())}
   * @param antecedents the set of antecedents to set
   */
  @Override
  public void setAntecedents(Set<IGeneObj> antecedents) {
    Set<IGeneObj> oldAntecedents = new HashSet<IGeneObj>(this.antecedents);
    for (IGeneObj antecedent : oldAntecedents) {
      this.antecedents.remove(antecedent);
      antecedent.getResultingObjects().remove(this);
    }
    for (IGeneObj antecedent : antecedents) {
      this.antecedents.add(antecedent);
      antecedent.getResultingObjects().add(this);
    }
  }

  /**
   * Adds a IGeneObj to antecedents, and updates the reverse reference from the
   * added IGeneObj to {@code this}.
   * @param antecedent the antecedent to remove
   */
  @Override
  public void addAntecedent(IGeneObj antecedent) {
    if (antecedent == null) {
      return;
    }
    this.antecedents.add(antecedent);
    antecedent.getResultingObjects().add(this);
  }

  /**
   * Removes a IGeneObj from antecedents, and updates the reverse reference from
   * the removed IGeneObj by removing {@code this}.
   * @param antecedent the antecedent to remove
   */
  @Override
  public void removeAntecedent(IGeneObj antecedent) {
    if (antecedent == null) {
      return;
    }
    this.antecedents.remove(antecedent);
    antecedent.getResultingObjects().remove(this);
  }

  // /////////////////////////////////////////////
  // Attribute resultingObjects and getter/setter
  // /////////////////////////////////////////////

  /**
   * The resultingObjects set (bidirectional reference, automatically managed).
   * Set of objects which are the result of the generalisaiton of {@code this}.
   */
  private Set<IGeneObj> resultingObjects = new HashSet<IGeneObj>();

  /**
   * Getter for resultingObjects. // TODO Probleme la méthode surchargeant ne
   * peut pas retourner un type plus // précis // que la méthode qu'elle
   * surcharge... je ne sais pas comment gérer ça.
   * @return the resultingObjects. It can be empty but not {@code null}.
   */
  @Override
  public Set<IGeneObj> getResultingObjects() {
    return this.resultingObjects;
  }

  /**
   * Setter for resultingObjects. Also updates the reverse reference from each
   * element of resultingObjects to {@code this}. To break the reference use
   * {@code this.setResultingObjects(new HashSet<IGeneObj>())}
   * @param resultingObjects the set of resultingObjects to set
   */
  @Override
  public void setResultingObjects(Set<IGeneObj> resultingObjects) {
    Set<IGeneObj> oldResultingObjects = new HashSet<IGeneObj>(
        this.resultingObjects);
    for (IGeneObj resultingObject : oldResultingObjects) {
      this.resultingObjects.remove(resultingObject);
      resultingObject.getAntecedents().remove(this);
    }
    for (IGeneObj resultingObject : resultingObjects) {
      this.resultingObjects.add(resultingObject);
      resultingObject.getAntecedents().add(this);
    }
  }

  /**
   * Adds a IGeneObj to resultingObjects, and updates the reverse reference from
   * the added IGeneObj to {@code this}.
   * @param resultingObject the resultingObject to add
   */
  @Override
  public void addResultingObject(IGeneObj resultingObject) {
    if (resultingObject == null) {
      return;
    }
    this.resultingObjects.add(resultingObject);
    resultingObject.getAntecedents().add(this);
  }

  /**
   * Removes a IGeneObj from resultingObjects, and updates the reverse reference
   * from the removed IGeneObj by removing {@code this}.
   * @param resultingObject the resultingObject to remove
   */
  @Override
  public void removeResultingObject(IGeneObj resultingObject) {
    if (resultingObject == null) {
      return;
    }
    this.resultingObjects.remove(resultingObject);
    resultingObject.getAntecedents().remove(this);
  }

  // Attribut GeoxObj
  /**
   * Associated Geoxygene object
   */
  @Override
  public IFeature getGeoxObj() {
    return null;
  }

  // Getter/Setter for geom
  /** Retrieve geometry on the associated IGeneralisable */
  @Override
  public IGeometry getGeom() {
    if (this.getGeoxObj() == null) {
      return this.geom;
    }
    return this.getGeoxObj().getGeom();
  }

  public IGeometry cloneGeometry() {
    if (this.getGeoxObj() == null) {
      if (this.geom == null) {
        return null;
      }
      return (IGeometry) this.geom.clone();

    }
    return (IGeometry) this.getGeoxObj().getGeom().clone();
  }

  @Override
  public IGeometry getSymbolGeom() {
    return this.getGeom();
  }

  @Override
  public double getSymbolArea() {
    return this.getSymbolGeom().area();
  }

  /** */
  @Override
  public void setGeom(IGeometry geom) {
    if (this.getGeoxObj() == null) {
      this.geom = geom;
      return;
    }
    this.getGeoxObj().setGeom(geom);
    this.geom = geom;
  }

  @Override
  public IPopulation<? extends IGeneObj> getPopulation() {

    @SuppressWarnings("unchecked")
    IPopulation<? extends IGeneObj> population = (IPopulation<? extends IGeneObj>) super.getPopulation();

    if (population != null) {
      return population;
    }

    String popName = CartAGenDoc.getInstance().getCurrentDataset()
        .getPopNameFromObj(this);
    try {
      return CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(popName, (String) this.getClass()
              .getDeclaredField("FEAT_TYPE_NAME").get(null));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void eliminate() {
    this.setEliminated(true);
    this.setDeleted(true);
  }

  @Override
  public void cancelElimination() {
    this.setEliminated(false);
    this.setDeleted(false);
  }

  // Attribute geneArtifacts and getter/setter
  private Set<Object> geneArtifacts = new HashSet<Object>();

  @Override
  public Set<Object> getGeneArtifacts() {
    return this.geneArtifacts;
  }

  @Override
  public void addToGeneArtifacts(Object artifact) {
    this.geneArtifacts.add(artifact);
  }

  @Override
  public void removeFromGeneArtifacts(Object artifact) {
    this.geneArtifacts.remove(artifact);
  }

  @Override
  public void displaceAndRegister(double dx, double dy) {
    this.setGeom(CommonAlgorithms.translation(this.getGeom(), dx, dy));
  }

  @Override
  public void registerDisplacement() {
    // do nothing as default
  }

  // Attribute symbolId and setter/setter

  @Override
  public int getSymbolId() {
    return this.symbolId;
  }

  @Override
  public void setSymbolId(int symbolId) {
    this.symbolId = symbolId;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + this.getId();
  }

  /**
   * Internal identifier to increment when a GeneObjDefault is created
   */
  private int internalId;
  private static AtomicInteger idCounter = new AtomicInteger();

  @Override
  public int getId() {
    return this.internalId;
  }

  @Override
  public void setId(int id) {
    this.internalId = id;
  }

  /**
   * Useful for storing a persistent identifier on the objects with shapefiles.
   * Do not use for other purposes.
   */
  private int shapeId;

  @Override
  public int getShapeId() {
    return this.shapeId;
  }

  @Override
  public void setShapeId(int id) {
    this.shapeId = id;
  }

  /**
   * Default constructor to initialise a default counter and build sets.
   * 
   * @author GTouya
   */
  public GeneObjDefault() {
    this.internalId = GeneObjDefault.idCounter.getAndIncrement();
    this.resultingObjects = new HashSet<IGeneObj>();
    this.antecedents = new HashSet<IGeneObj>();
    this.featureType = new FeatureType();
    this.featureType.setTypeName(IGeneObj.FEAT_TYPE_NAME);
    this.setDbName(CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
        .getName());
    // this.setDbName("CartAGen_initial_dataset");
  }

  private String dbName;

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public String getDbName() {
    return this.dbName;
  }

  public void setDbName(String name) {
    this.dbName = name;
  }

  /**
   * The possible graphn linkable feature attached to the geneobj
   */
  IGraphLinkableFeature linkableFeature;

  @Override
  public IGraphLinkableFeature getLinkableFeature() {
    return this.linkableFeature;
  }

  @Override
  public void setLinkableFeature(IGraphLinkableFeature linkableFeature) {
    this.linkableFeature = linkableFeature;
  }

  @Override
  public INode getReferentGraphNode() {
    if (this.linkableFeature == null) {
      return null;
    }
    return this.linkableFeature.getReferentGraphNode();
  }

  @Override
  public void setReferentGraphNode(INode referentGraphNode) {
    if (this.linkableFeature != null) {
      this.linkableFeature.setReferentGraphNode(referentGraphNode);
    }
  }

  @Override
  public ArrayList<IEdge> getProximitySegments() {
    if (this.linkableFeature == null) {
      return null;
    }
    return this.linkableFeature.getProximitySegments();
  }

  @Override
  public void clean() {
    if (this.linkableFeature != null) {
      this.linkableFeature.clean();
    }
  }

  @Override
  public IGeneObj getFeature() {
    return this;
  }

  /**
   * This method is only useful for the persistence of IGeneObj relations: as it
   * is not possible to persist interface relations (that are everywhere in the
   * GeneObj), the ids are stored in a private field for each relation; this
   * method fills the id field with the ids of the objects that are actually
   * related. The method uses the {@link EncodedRelation} annotation to find the
   * relations to update in {@code this}.
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws NoSuchFieldException
   */
  @Override
  public void updateRelationIds()
      throws SecurityException, NoSuchMethodException, IllegalArgumentException,
      IllegalAccessException, InvocationTargetException, NoSuchFieldException {
    // loop on the methods of the class
    for (Method m : this.getClass().getDeclaredMethods()) {
      // test if the method is annotated
      if (!m.isAnnotationPresent(EncodedRelation.class)) {
        continue;
      }
      // get the annotation
      EncodedRelation encodedAnnotation = null;
      for (Annotation a : m.getAnnotations()) {
        if (a instanceof EncodedRelation) {
          encodedAnnotation = (EncodedRelation) a;
          break;
        }
      }
      if (encodedAnnotation == null) {
        return;
      }
      // get its values
      Class<? extends IGeneObj> targetEntity = encodedAnnotation.targetEntity();
      Class<? extends IGeneObj>[] targetEntities = encodedAnnotation
          .targetEntities();
      String methodName = "get" + encodedAnnotation.methodName();
      // invoke the getter of the actual relation
      Method meth = this.getClass().getMethod(methodName);
      Collection<?> objects = (Collection<?>) meth.invoke(this);
      // update the ids
      List<Integer> ids = new ArrayList<Integer>();
      if (objects != null) {
        for (Object obj : objects) {
          if (!targetEntity.equals(GeneObjDefault.class)) {
            IGeneObj entity = targetEntity.cast(obj);
            ids.add(entity.getId());
          } else {
            for (Class<? extends IGeneObj> classObject : targetEntities) {
              if (classObject.isInstance(obj)) {
                IGeneObj entity = targetEntity.cast(obj);
                ids.add(entity.getId());
              }
            }
          }
        }
      }

      // update the field with the setter
      GeneObjDefault.logger.finest(this.toString());
      GeneObjDefault.logger.finest(m.getName());
      GeneObjDefault.logger.finest("" + ids.size());
      String fieldName = m.getName().substring(3, 4).toLowerCase()
          .concat(m.getName().substring(4));
      GeneObjDefault.logger.finest(fieldName);
      Field field = this.getClass().getDeclaredField(fieldName);
      if (!field.isAccessible()) {
        field.setAccessible(true);
      }
      field.set(this, ids);
    }
  }

  /**
   * 
   * {@inheritDoc}
   * <p>
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws NoSuchFieldException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * 
   */
  @SuppressWarnings({ "unchecked" })
  @Override
  public void fillRelationsFromIds() throws IllegalArgumentException,
      IllegalAccessException, InvocationTargetException, SecurityException,
      NoSuchFieldException, NoSuchMethodException {
    // loop on the methods of the class
    for (Method m : this.getClass().getDeclaredMethods()) {
      // test if the method is annotated
      if (!m.isAnnotationPresent(EncodedRelation.class)
          && !m.isAnnotationPresent(Encoded1To1Relation.class)) {
        continue;
      }
      // get the annotation
      EncodedRelation encodedAnnotation = null;
      Encoded1To1Relation encoded1To1Annotation = null;
      for (Annotation a : m.getAnnotations()) {
        if (a.annotationType().equals(EncodedRelation.class)) {
          encodedAnnotation = (EncodedRelation) a;
          break;
        }
        if (a.annotationType().equals(Encoded1To1Relation.class)) {
          encoded1To1Annotation = (Encoded1To1Relation) a;
          break;
        }
      }

      if (encodedAnnotation == null && encoded1To1Annotation == null) {
        continue;
      }

      if (m.isAnnotationPresent(EncodedRelation.class)) {
        // get its values
        Class<? extends IGeneObj> targetEntity = encodedAnnotation
            .targetEntity();
        Class<? extends IGeneObj>[] targetEntities = encodedAnnotation
            .targetEntities();
        String methodName = "set" + encodedAnnotation.methodName();

        // invoke m to get the collection of ids
        Collection<Integer> ids = (Collection<Integer>) m.invoke(this);
        // make the collection of objects from the ids
        // the collection is not typed as the type is unknown at compilation
        Collection<IGeneObj> objs = new HashSet<IGeneObj>();
        if (encodedAnnotation.collectionType().equals(CollectionType.LIST)) {
          objs = new ArrayList<IGeneObj>();
        }
        if (encodedAnnotation.collectionType()
            .equals(CollectionType.FEATURE_COLLECTION)) {
          objs = new FT_FeatureCollection<IGeneObj>();
        }

        // get the inverse relation if not nToM
        List<Method> inverseMethods = new ArrayList<Method>();
        if (!encodedAnnotation.nToM() && encodedAnnotation.inverse()) {
          String methName = "set" + encodedAnnotation.invMethodName();
          if (!targetEntity.equals(GeneObjDefault.class))
            inverseMethods.add(ReflectionUtil.getInheritedMethod(targetEntity,
                methName, encodedAnnotation.invClass()));
          else {
            for (Class<? extends IGeneObj> target : targetEntities) {
              inverseMethods.add(ReflectionUtil.getInheritedMethod(target,
                  methName, encodedAnnotation.invClass()));
            }
          }
        }

        // then, get the population related to targetEntity
        Set<IGeneObj> objectsToParse = new HashSet<IGeneObj>();
        if (!targetEntity.equals(GeneObjDefault.class)) {
          String popName = CartAGenDoc.getInstance().getCurrentDataset()
              .getPopNameFromClass(targetEntity);
          Field field = targetEntity.getField("FEAT_TYPE_NAME");
          String featType = (String) field.get(null);
          IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) CartAGenDoc
              .getInstance().getCurrentDataset()
              .getCartagenPop(popName, featType);
          objectsToParse.addAll(pop);
        } else {
          for (Class<? extends IGeneObj> target : targetEntities) {
            String popName = CartAGenDoc.getInstance().getCurrentDataset()
                .getPopNameFromClass(target);
            Field field = target.getField("FEAT_TYPE_NAME");
            String featType = (String) field.get(null);
            IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) CartAGenDoc
                .getInstance().getCurrentDataset()
                .getCartagenPop(popName, featType);
            objectsToParse.addAll(pop);
          }
        }
        // TODO this part of the code is not optimised: a query would be better

        // loop on the object population
        for (IGeneObj obj : objectsToParse) {
          if (!ids.contains(obj.getId())) {
            continue;
          }
          objs.add(obj);
          // now set the inverse relation
          if (inverseMethods.size() != 0 && !encodedAnnotation.nToM()
              && encodedAnnotation.inverse()) {
            for (Method inverseMethod : inverseMethods) {
              if (inverseMethod.getDeclaringClass().isInstance(obj))
                inverseMethod.invoke(obj, this);
            }
          }
        }

        // invoke the setter to set the relation
        Class<?> declaredClass = encodedAnnotation.collectionType()
            .getClassObject();
        ReflectionUtil
            .getInheritedMethod(this.getClass(), methodName, declaredClass)
            .invoke(this, objs);
      } else {
        // 1 to 1 relation case
        // get its value
        Class<? extends IGeneObj> targetEntity = encoded1To1Annotation
            .targetEntity();
        String methodName = "set" + encoded1To1Annotation.methodName();

        // invoke m to get the collection of ids
        int id = (Integer) m.invoke(this);
        IGeneObj obj = null;

        // then, get the population related to targetEntity
        String popName = CartAGenDoc.getInstance().getCurrentDataset()
            .getPopNameFromClass(targetEntity);
        Field field = targetEntity.getField("FEAT_TYPE_NAME");
        String featType = (String) field.get(null);
        IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) CartAGenDoc
            .getInstance().getCurrentDataset()
            .getCartagenPop(popName, featType);

        // get the inverse relation
        Method inverseMethod = null;
        if (encoded1To1Annotation.inverse()) {
          String methName = "set" + encoded1To1Annotation.invName();
          inverseMethod = ReflectionUtil.getInheritedMethod(targetEntity,
              methName, encoded1To1Annotation.invClass());
        }

        // loop on the object population
        for (IGeneObj object : pop) {
          if (id != object.getId()) {
            continue;
          }
          obj = object;
          // now set the inverse relation
          if (inverseMethod != null && encoded1To1Annotation.inverse()) {
            inverseMethod.invoke(obj, this);
          }
        }

        // invoke the setter to set the relation
        Class<?> declaredClass = encoded1To1Annotation.invClass();
        ReflectionUtil
            .getInheritedMethod(this.getClass(), methodName, declaredClass)
            .invoke(this, obj);
      }
    }
  }

  @Override
  public void restoreGeoxObjects() {
    // do nothing as this geoxObj is null
  }

  @Override
  public void restoreGeoxRelations() {
    // do nothing as this geoxObj is null
  }

  @Override
  public void copyAttributes(IGeneObj obj) {
    // do not copy anything as a default method.
  }

  @Override
  public Object getAttribute(String nomAttribut) {
    if (nomAttribut.equals("initialGeom"))
      return this.getInitialGeom();
    if (nomAttribut.equals("id"))
      return this.getId();
    return super.getAttribute(nomAttribut);
  }

  @Override
  public void setAttribute(String nomAttribut, Object value) {
    if (value != null) {
      AttributeType attribute = new AttributeType();
      attribute.setNomField(nomAttribut);
      attribute.setMemberName(nomAttribut);
      if (nomAttribut.length() != 0) {
        // Case ID
        if (attribute.getMemberName().equals("id")) { //$NON-NLS-1$
          this.setId((Integer) value);
        } else {
          // Other case
          // Builds the setter name
          String nomFieldMaj = Character.toUpperCase(nomAttribut.charAt(0))
              + nomAttribut.substring(1);
          // System.out.println("nomFieldMaj : " + nomFieldMaj);
          Class<?> classe = this.getClass();
          Method methodSetter = null;
          try {
            // While the superclass is not the root class OR while the method is
            // not found
            while (!classe.equals(Object.class) && methodSetter == null) {
              // System.out.print("Class :" + classe.getName() + "\n");
              // Get the method for the current class
              Method listMethod[] = classe.getDeclaredMethods();
              // Compare the methods name with the one searched
              for (Method m : listMethod) {
                // System.out.print("Methode :" + m.getName() + "\n");
                // if the method is found : save it in methodSetter and get out
                if (m.getName().equals("set" + nomFieldMaj)) {
                  methodSetter = m;
                  break;
                } else if (m.getName().equals(nomAttribut)) {
                  methodSetter = m;
                  break;
                }
              }
              // System.out.print("SuperClass");
              // else : search the method in the superclass ones
              classe = classe.getSuperclass();
            }
            // Get the right class
            classe = this.getClass();
            // two possible cases : method found or null
            if (methodSetter != null) {
              methodSetter.invoke(this, value);
            }
          } catch (SecurityException e) {
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger
                  .debug("SecurityException pendant l'appel de la méthode "
                      + nomAttribut + " sur la classe " + classe);
            }
          } catch (IllegalArgumentException e) {
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug(
                  "IllegalArgumentException pendant l'appel de la méthode "
                      + nomAttribut + " sur la classe " + classe);
            }
          } catch (IllegalAccessException e) {
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger
                  .debug("IllegalAccessException pendant l'appel de la méthode "
                      + nomAttribut + " sur la classe " + classe);
            }
          } catch (InvocationTargetException e) {
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug(
                  "InvocationTargetException pendant l'appel de la méthode "
                      + nomAttribut + " sur la classe " + classe);
            }
          }
        }
      }
    }
  }
}
