package fr.ign.cogit.cartagen.agents.core.agent.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.agents.core.agent.ISectionAgent;
import fr.ign.cogit.cartagen.agents.core.agent.MesoAgentGeneralisation;
import fr.ign.cogit.cartagen.agents.core.agent.urban.BlockAgent;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * Meso agent road controlling micro road agents for recursive generalisation
 * based on calescence
 * @author J. Renard 04/05/2010
 * 
 */
public class MesoSectionAgent extends MesoAgentGeneralisation<SectionAgent> {
  private static Logger logger = LogManager.getLogger(BlockAgent.class.getName());

  /**
   * Constructor for meso section agent
   * @param section the micro section agent that is at the origin of the
   *          creation of {@code this}
   */
  @SuppressWarnings("unchecked")
  public MesoSectionAgent(ISectionAgent section) {
    super();
    this.setCorrespondingSectionMicroAgent(section);
    this.setFeature(this.getCorrespondingSectionMicroAgent().getFeature());
    this.setInitialGeom(section.getGeom());
    // Retrieve and store population of the dataset containind 'section'
    CartAGenDataSet currentDataset = CartAGenDoc.getInstance()
        .getCurrentDataset();
    String popName = currentDataset.getPopNameFromObj(section.getFeature());
    this.populationContainingCorrespondingFeature = (IPopulation<INetworkSection>) currentDataset
        .getPopulation(popName);
  }

  /**
   * Constructor for meso road section
   * @param section the micro section agent that is at the origin of the
   *          creation of {@code this}
   * @param microSections micro sections controlled by the meso road section
   */
  public MesoSectionAgent(ISectionAgent section,
      List<SectionAgent> microSections) {
    this(section);
    // link with micro road sections
    this.setComponents(microSections);
    for (SectionAgent s : microSections) {
      s.setControllingMeso(this);
    }
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  /**
   * Section Micro Agent corresponding to the meso created for decomposition
   */
  private ISectionAgent correspondingSectionMicroAgent;

  /**
   * @param correspondingSectionMicroAgent
   */
  public void setCorrespondingSectionMicroAgent(
      ISectionAgent correspondingSectionMicroAgent) {
    this.correspondingSectionMicroAgent = correspondingSectionMicroAgent;
  }

  /**
   * @return
   */
  public ISectionAgent getCorrespondingSectionMicroAgent() {
    return this.correspondingSectionMicroAgent;
  }

  /**
   * Holds the geographic population of the dataset that used to contain the
   * gene obj (feature) associated to the micro section agent that gave birth to
   * {@code this}, before its decomposition (the gene obj is removed from the
   * population during its decomposition
   */
  private IPopulation<INetworkSection> populationContainingCorrespondingFeature;

  /**
   * Decomposes {@code this} into parts, i.e. creates as many section agents as
   * there are geometries in the list in parameter, registers them as components
   * of {@code this}, and initialises their constraints. Also ensures that those
   * parts are known from the dataset instead of the section agent that gave
   * birth to {@code this}.
   * @param partsGeomsList a list of the geometries of the parts to create. The
   *          union of these geometries is expected to be equal to the geometry
   *          of the meso, but no control of this is done
   * @throws NoSuchMethodException No constructor found //TODO harmoniser
   *           gestion des excetions dans cette methode
   */
  @SuppressWarnings("unchecked")
  public void decomposeIntoParts(List<ILineString> partsGeomsList) {

    // ===
    // Initialisation of needed parameters to create the micro sections
    // ===

    // Retrieve the section micro agent encapsulating the meso section agent +
    // its class + its corresponding gene obj + its class + its importance
    ISectionAgent encapsulatingSectionAgent = this
        .getCorrespondingSectionMicroAgent();
    Class<? extends ISectionAgent> sectionAgentClass = encapsulatingSectionAgent
        .getClass();
    INetworkSection encapsulatingSectionFeature = encapsulatingSectionAgent
        .getFeature();
    Class<? extends INetworkSection> sectionFeatureClass = encapsulatingSectionFeature
        .getClass();
    int importance = encapsulatingSectionFeature.getImportance();
    this.setInitialGeom(
        (ILineString) encapsulatingSectionAgent.getInitialGeom().clone());
    // Retrieve the network agent to which the encapsulating micro belongs, and
    // removes it from this network - we do not break the reverse link on
    // purpose, so that when <this> recomposes itself later, there is nothing
    // more to do but adding it (again) to the network agent's components
    NetworkAgent networkAgent = encapsulatingSectionAgent.getNetwork();
    networkAgent.getComponents().remove(encapsulatingSectionAgent);
    // Retrieve the encapsulating section from the geographic population it
    // belongs to
    this.populationContainingCorrespondingFeature
        .remove(encapsulatingSectionFeature);
    // Recup de toutes les collections et boucle dessus pour en virer le
    // encapsulatingFeature inutiles a priori (lignes au-dessus suffisent et
    // partiellement double emploi avec ajout au INetwork
    /*
     * List<IFeatureCollection<IFeature>> sectionFeatureCollectionsList =
     * encapsulatingSectionFeature .getFeatureCollections();
     * List<IFeatureCollection<IFeature>> copyOfSectionFeatureCollectionsList =
     * new ArrayList<IFeatureCollection<IFeature>>(
     * sectionFeatureCollectionsList);
     */
    // TODO CONTOURNEMENT PAS PROPRE il faudrait ajouter sur IFeature une
    // méthode clearFeatureCollections() ou une méthode setFeatureCollections()
    // appelable avec comme argument un liste vide pour
    // qu'on puisse supprimer un objet de toutes les FeaturesCollections
    // auxquelles il appartient (en gros, faire en sorte que le lien soit géré
    // complètemnet en bidirectionnel). Le code mis en commentaire ci-dessous
    // plante parce que la mise à jour du lien inverse génère une
    // ConcurrentModificationException (tentative de supprimer un élément de la
    // liste qu'on est en train de parcourir). Correction temporaire moche...
    // for (IFeatureCollection<IFeature> iFeatureCollection :
    // sectionsFeatureCollectionsList) {
    // iFeatureCollection.remove(encapsulatingSectionFeature);
    // }
    // Go through the Feature Collections the section feature belongs to
    /*
     * for (IFeatureCollection<IFeature> iFeatureCollection :
     * sectionFeatureCollectionsList) { // Remove the section feature from the
     * feature collection and updates the // feature colelction accordingly
     * (this code has been copied and adapted // from method
     * FT_FetaureCollection.remove(IFeature feat)
     * iFeatureCollection.getElements().remove(encapsulatingSectionFeature); if
     * (iFeatureCollection.hasSpatialIndex() &&
     * iFeatureCollection.getSpatialIndex().hasAutomaticUpdate()) {
     * iFeatureCollection.getSpatialIndex().update( encapsulatingSectionFeature,
     * -1); } iFeatureCollection.fireActionPerformed(new
     * FeatureCollectionEvent(this, encapsulatingSectionFeature,
     * FeatureCollectionEvent.Type.REMOVED,
     * encapsulatingSectionFeature.getGeom())); } // Now clear the collection on
     * section feature side sectionFeatureCollectionsList.clear();
     */
    // ===
    // Preparation of the loop through the geometries
    // ===

    // Preparation of the loop to create section agents with the same importance
    // and the geometries of the list in parameter, in the same class as the
    // encapsulating section micro agent.
    // Identify the right constructors and prepare (not completely completed)
    // arrays of parameter values they will be passed, to construct
    // 1. the INetworkSections of the right class (same as encapsulating feature
    // section): look for a constructor that takes an ILineString and an integer
    // as arguments
    Class<?>[] sectionFeatureConstructorArgTypes = new Class[] {
        ILineString.class, int.class };
    Constructor<? extends INetworkSection> sectionFeatureConstructor = null;
    try {
      sectionFeatureConstructor = sectionFeatureClass
          .getConstructor(sectionFeatureConstructorArgTypes);
    } catch (SecurityException e1) {
      e1.printStackTrace();
    } catch (NoSuchMethodException e1) {
      e1.printStackTrace();
    }
    // Prepare array of future argument values (will be updated during loop)
    Object[] sectionFeatureConstructorArgs = new Object[] { null,
        Integer.valueOf(importance) };
    // 2. the SectionAgent of the right class: look for a constructor that could
    // accept as parameters: the newtork agent (already known), and
    // encapsulating section feature (because eventually we will pass to the
    // constructor the newly created section feature, which will be of same
    // class as the encapsulating section feature)
    Constructor<? extends SectionAgent> sectionAgentConstructor = null;
    Constructor<?>[] constructors = sectionAgentClass.getConstructors();
    for (Constructor<?> constructor : constructors) {
      Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
      if (constructorParameterTypes.length != 2) {
        continue;
      }
      if ((constructorParameterTypes[0].isInstance(networkAgent)
          && constructorParameterTypes[1]
              .isInstance(encapsulatingSectionFeature))) {
        // Now we have found the constructor
        sectionAgentConstructor = (Constructor<SectionAgent>) constructor;
        break;
      }
    }
    // Check if we found a constructor
    if (sectionFeatureConstructor == null || sectionAgentConstructor == null) {
      MesoSectionAgent.logger.error(
          "Meso decomposition: no constructor defined with network section feature and importance (int) as parameters");
      try {
        throw new java.lang.NoSuchMethodException(
            sectionAgentClass.getName() + ": constructor missing accepting ("
                + networkAgent.getClass().getName() + " , "
                + encapsulatingSectionFeature.getClass().getName()
                + ") as parameter types");
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
      return;
    }
    // Prepare array of future argument values (will be updated during loop)
    Object[] sectionAgentConstructorArgs = new Object[] { networkAgent, null };

    // ===
    // Loop through the geometries
    // ===
    try {
      for (ILineString partGeom : partsGeomsList) {
        // Creates the INetworkSection of the same class as
        // encapsulatingSectionFeature
        sectionFeatureConstructorArgs[0] = partGeom;
        INetworkSection sectionFeature = sectionFeatureConstructor
            .newInstance(sectionFeatureConstructorArgs);
        // Creates the SectionAgent of the same class as
        // encapsulatingSectionAgent
        sectionAgentConstructorArgs[1] = sectionFeature.getClass()
            .cast(sectionFeature);
        SectionAgent sectionAgent = sectionAgentConstructor
            .newInstance(sectionAgentConstructorArgs);
        // Register the created section agent as a component of <this>
        this.getComponents().add(sectionAgent);
        sectionAgent.setControllingMeso(this);
        sectionAgent.setMesoAgent(this);
        // Initialise the created section agent
        sectionAgent.instantiateConstraints();
        sectionAgent.setTriggeredByMeso(0);
        // Add the created section feature to the right population of
        // the dataset
        this.populationContainingCorrespondingFeature.add(sectionFeature);
        // Add the created section feature to the right FeatureCollections of
        // the dataset
        /*
         * for (IFeatureCollection<IFeature> iFeatureCollection :
         * copyOfSectionFeatureCollectionsList) {
         * iFeatureCollection.add(sectionFeature); }
         */
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void instantiateConstraints() {
    // Nothing to instantiate
  }

  /**
   * Choice of the next micro section to generalise The computation takes into
   * account the type of coalescence and the coalescence strength If returns
   * null, it means that no micro section is constrained by coalescence, so the
   * meso section is well generalised
   */

  public SectionAgent getBestComponentToActivate() {

    double coalescenceMax = 1.0;
    SectionAgent sectionMax = null;
    /*
     * for (SectionAgent s : this.getComponents()) { if (s.getTriggeredByMeso()
     * != 0 && s.getTriggeredByMeso() != 2) { continue; } LineGetCoalescenceSide
     * algoCoalescenceType = new LineGetCoalescenceSide( s.getFeature()); int
     * coalescenceType = 0; try { coalescenceType =
     * algoCoalescenceType.compute(); // returns 0,1,2,3 or 4 // depending on
     * the // type of coalescence } catch (GothicException e) { // do nothing
     * coalescenceType = 0; } double coalescence = coalescenceType +
     * SectionSymbol.getCoalescence(s.getFeature()); // adds the coalesence //
     * strngth (between 0 // & 1) to the // coalescence type
     * System.out.println(coalescence); if (coalescence > coalescenceMax) {
     * coalescenceMax = coalescence; sectionMax = s; } } if (sectionMax != null)
     * { sectionMax.setTriggeredByMeso(sectionMax.getTriggeredByMeso() + 1); }
     */
    // FIXME was working only with Clarity
    return sectionMax;
  }

  /**
   * Recomposition of the meso road from its partitioned micro sections already
   * reconnected And auto-destruction of the meso
   */

  public void recomposeMesoSection() {

    // Retrieves the network agent that used to be associated to the
    // corresponding micro before being 'transformed into' <this>
    ISectionAgent encapsulatingSectionAgent = this
        .getCorrespondingSectionMicroAgent();
    NetworkAgent networkAgent = encapsulatingSectionAgent.getNetwork();
    INetworkSection encapsulatingSectionFeature = encapsulatingSectionAgent
        .getFeature();

    // Auto-destruction of <this> and its micro components: ensure nothing more
    // points to them - loop on the section agents components
    // And re-creation of the whole geometry
    IDirectPositionList coordList = new DirectPositionList();
    for (SectionAgent s : this.getComponents()) {
      for (int j = 0; j < s.getGeom().coord().size(); j++) {
        coordList.add(s.getGeom().coord().get(j));
      }
      // Break the link to <this>
      s.setControllingMeso(null);
      s.setMesoAgent(null);
      // Remove the section agent from its network
      networkAgent.getComponents().remove(s);
      INetworkSection sectionFeature = s.getFeature();
      // Normally removing the section from its geographic population is enough
      this.populationContainingCorrespondingFeature.remove(sectionFeature);
      s.deleteAndRegister();
    }
    this.setComponents(null);
    this.setCorrespondingSectionMicroAgent(null);

    // Affectation of the new geometry
    encapsulatingSectionFeature.setGeom(new GM_LineString(coordList));

    // 'Regeneration' of the corresponding ('encapsulating') micro: adds it
    // (again) to its network and adds it encapsulated feature to the feature
    // collections of the dataset
    encapsulatingSectionAgent.setInitialGeom(this.getInitialGeom());
    encapsulatingSectionAgent.setDeleted(false);
    networkAgent.getComponents().add(encapsulatingSectionAgent);
    this.populationContainingCorrespondingFeature
        .add(encapsulatingSectionFeature);

  }

  /**
   * Propagation of the geometrical modifications of a modified section of a
   * meso line to the others At the beginning, the micro sections are not
   * continuous anymore The meso line is supposed to progressively absorb the
   * modifications to become continuous again, its end nodes not being moved
   * @param sectionsList : the different sections of the line, well ordered
   * @param currentSection : the position of the section whose geometry has been
   *          modified
   * @throws InterruptedException
   */

  @Override
  public void manageInternalSideEffects(GeographicObjectAgent geoObj) {
    if (!(geoObj instanceof SectionAgent)) {
      return;
    }
    SectionAgent currentSection = (SectionAgent) geoObj;
    for (int i = 0; i < this.getComponents().size(); i++) {
      if (this.getComponents().get(i).equals(currentSection)) {
        try {
          this.propagateModifToSections(i);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return;
      }
    }
  }

  /**
   * Algorithm to propagate the geometrical modifications of one section of a
   * line to the others At the beginning, the current section is not continuous
   * anymore with the whole line The global line is supposed to progressively
   * absorb the modifications to become continuous again, its end nodes not
   * being moved.
   * @param currentSection : the position of the section whose geometry has been
   *          modified
   * @throws InterruptedException
   */

  private void propagateModifToSections(int currentSection) throws Exception {

    int pause = 200;

    // ///////////////////////////
    // First part of the line
    // ///////////////////////////

    if (currentSection != 0) {

      // Points which lead the propagation: ending points of the first part of
      // the line and first point of the modified section
      IDirectPosition startFirstPart = this.getComponents().get(0).getGeom()
          .coord().get(0);
      IDirectPosition endFirstPart = this.getComponents()
          .get(currentSection - 1).getGeom().coord().get(this.getComponents()
              .get(currentSection - 1).getGeom().coord().size() - 1);
      IDirectPosition startCurrentSection = this.getComponents()
          .get(currentSection).getGeom().coord().get(0);

      // Enlargment coefficient
      double actualLength = Math
          .sqrt(Math.pow(startFirstPart.getX() - endFirstPart.getX(), 2.0)
              + Math.pow(startFirstPart.getY() - endFirstPart.getY(), 2.0));
      double newLength = Math.sqrt(Math
          .pow(startFirstPart.getX() - startCurrentSection.getX(), 2.0)
          + Math.pow(startFirstPart.getY() - startCurrentSection.getY(), 2.0));
      double coeff = 1;
      if (actualLength != 0.0) {
        coeff = newLength / actualLength;
      }

      // Actual direction of the last part of the line
      double actualAngle = Math.atan(
          endFirstPart.getY() - startFirstPart.getY() / endFirstPart.getX()
              - startFirstPart.getX());
      if (endFirstPart.getX() < startFirstPart.getX()) {
        actualAngle += Math.PI;
      }
      if (actualAngle < 0) {
        actualAngle += 2 * Math.PI;
      }

      // Actual direction of the last part of the line
      double newAngle = Math.atan(startCurrentSection.getY()
          - startFirstPart.getY() / startCurrentSection.getX()
          - startFirstPart.getX());
      if (startCurrentSection.getX() < startFirstPart.getX()) {
        newAngle += Math.PI;
      }
      if (newAngle < 0) {
        newAngle += 2 * Math.PI;
      }

      // Rotation of the last part of the line to allow translated reconnection
      double rotationAngle = newAngle - actualAngle;
      if (rotationAngle != 0) {
        for (int i = 0; i < currentSection - 1; i++) {
          SectionAgent section = this.getComponents().get(i);
          section.setGeom(CommonAlgorithms.rotation(section.getGeom(),
              startFirstPart, rotationAngle));
        }
      }

      if (coeff != 1) {

        // Direction of translation after rotation
        ILineSegment propagationSegment = new GM_LineSegment(
            startCurrentSection, startFirstPart);

        // Translation of all points of of all sections of the last part of the
        // line
        for (int i = currentSection - 1; i > -1; i--) {
          SectionAgent section = this.getComponents().get(i);
          if (section.getTriggeredByMeso() == 1) {
            section.setTriggeredByMeso(2);
          }
          for (int j = section.getGeom().coord().size() - 1; j > -1; j--) {
            if (i == 0 && j == 0) {
              continue;
            }
            IDirectPosition currentPoint = section.getGeom().coord().get(j);
            // Projection of the current point on the propagation direction
            IDirectPosition projection = CommonAlgorithms.getNearestPoint(
                propagationSegment, new GM_Point(currentPoint));
            // Computation of translation parameters
            double actualDX = projection.getX() - startFirstPart.getX();
            double actualDY = projection.getY() - startFirstPart.getY();
            double newDX = (coeff - 1.0) * actualDX;
            double newDY = (coeff - 1.0) * actualDY;
            // Translation of the point
            currentPoint.setX(currentPoint.getX() + newDX);
            currentPoint.setY(currentPoint.getY() + newDY);
            // Current point update
            section.getGeom().coord().remove(j);
            section.getGeom().coord().add(j, currentPoint);
          }
        }

        // Last point treatment - to ensure good reconnection
        this.getComponents().get(currentSection - 1).getGeom().coord()
            .remove(this.getComponents().get(currentSection - 1).getGeom()
                .coord().size() - 1);
        this.getComponents().get(currentSection - 1).getGeom().coord()
            .add(new DirectPosition(startCurrentSection.getX(),
                startCurrentSection.getY()));

        /*
         * if
         * (GeneralisationRightPanelAgentComplement.getInstance().cFairePauses
         * .isSelected()) { try { Thread.sleep(pause); } catch
         * (InterruptedException e) { e.printStackTrace(); }
         * CartagenApplication.getInstance().getFrame().getVisuPanel()
         * .activate(); }
         */

      }

    }

    // ///////////////////////////
    // Last part of the line
    // ///////////////////////////

    if (currentSection != this.getComponents().size() - 1) {

      // Points which lead the propagation: ending points of the last part of
      // the line and last point of the modified section
      IDirectPosition startLastPart = this.getComponents()
          .get(currentSection + 1).getGeom().coord().get(0);
      IDirectPosition endLastPart = this.getComponents()
          .get(this.getComponents().size() - 1).getGeom().coord()
          .get(this.getComponents().get(this.getComponents().size() - 1)
              .getGeom().coord().size() - 1);
      IDirectPosition endCurrentSection = this.getComponents()
          .get(currentSection).getGeom().coord()
          .get(this.getComponents().get(currentSection).getGeom().coord().size()
              - 1);

      // Enlargment coefficient
      double actualLength = Math
          .sqrt(Math.pow(endLastPart.getX() - startLastPart.getX(), 2.0)
              + Math.pow(endLastPart.getY() - startLastPart.getY(), 2.0));
      double newLength = Math
          .sqrt(Math.pow(endLastPart.getX() - endCurrentSection.getX(), 2.0)
              + Math.pow(endLastPart.getY() - endCurrentSection.getY(), 2.0));
      double coeff = 1.0;
      if (actualLength != 0.0) {
        coeff = newLength / actualLength;
      }

      // Actual direction of the last part of the line
      double actualAngle = Math.atan(startLastPart.getY()
          - endLastPart.getY() / startLastPart.getX() - endLastPart.getX());
      if (startLastPart.getX() < endLastPart.getX()) {
        actualAngle += Math.PI;
      }
      if (actualAngle < 0) {
        actualAngle += 2 * Math.PI;
      }

      // Actual direction of the last part of the line
      double newAngle = Math.atan(endCurrentSection.getY()
          - endLastPart.getY() / endCurrentSection.getX() - endLastPart.getX());
      if (endCurrentSection.getX() < endLastPart.getX()) {
        newAngle += Math.PI;
      }
      if (newAngle < 0) {
        newAngle += 2 * Math.PI;
      }

      // Rotation of the last part of the line to allow translated reconnection
      double rotationAngle = newAngle - actualAngle;
      if (rotationAngle != 0) {
        for (int i = currentSection + 1; i < this.getComponents().size(); i++) {
          SectionAgent section = this.getComponents().get(i);
          section.setGeom(CommonAlgorithms.rotation(section.getGeom(),
              endLastPart, rotationAngle));
        }
      }

      if (coeff != 1.0) {

        // Direction of translation after rotation
        ILineSegment propagationSegment = new GM_LineSegment(endCurrentSection,
            endLastPart);

        // Translation of all points of of all sections of the last part of the
        // line
        for (int i = currentSection + 1; i < this.getComponents().size(); i++) {
          SectionAgent section = this.getComponents().get(i);
          if (section.getTriggeredByMeso() == 1) {
            section.setTriggeredByMeso(2);
          }
          for (int j = 0; j < section.getGeom().coord().size(); j++) {
            if (i == this.getComponents().size() - 1
                && j == section.getGeom().coord().size() - 1) {
              continue;
            }
            IDirectPosition currentPoint = section.getGeom().coord().get(j);
            // Projection of the current point on the propagation direction
            IDirectPosition projection = CommonAlgorithms.getNearestPoint(
                propagationSegment, new GM_Point(currentPoint));
            // Computation of translation parameters
            double actualDX = projection.getX() - endLastPart.getX();
            double actualDY = projection.getY() - endLastPart.getY();
            double newDX = (coeff - 1.0) * actualDX;
            double newDY = (coeff - 1.0) * actualDY;
            // Translation of the point
            currentPoint.setX(currentPoint.getX() + newDX);
            currentPoint.setY(currentPoint.getY() + newDY);
            // Current point update
            section.getGeom().coord().remove(j);
            section.getGeom().coord().add(j, currentPoint);
          }
        }

        // First point treatment - to ensure good reconnection
        this.getComponents().get(currentSection + 1).getGeom().coord()
            .remove(0);
        this.getComponents().get(currentSection + 1).getGeom().coord().add(0,
            new DirectPosition(endCurrentSection.getX(),
                endCurrentSection.getY()));

        /*
         * if
         * (GeneralisationRightPanelAgentComplement.getInstance().cFairePauses
         * .isSelected()) { try { Thread.sleep(pause); } catch
         * (InterruptedException e) { e.printStackTrace(); }
         * CartagenApplication.getInstance().getFrame().getVisuPanel()
         * .activate(); }
         */

      }

    }

  }

  /**
   * If extremities of the meso are modified, this algorithm diffuses the
   * modifications to the road sections of the neighbourhood to progressively
   * absorb the translations
   * @throws InterruptedException
   * @throws GothicException
   */

  public void diffuseModificationsToNeighbourhood()
      throws InterruptedException {
    /*
     * // Gothic commit of modified road section IRoadLine modifiedRoad =
     * (IRoadLine) this.getFeature(); ((GothicBasedGeneObj)
     * modifiedRoad).modifyInGothicDB(); Collection gothicModifiedRoad = new
     * Set(GothDatatype.OBJECT_ID); gothicModifiedRoad
     * .addElement(((GothicBasedGeneObj) modifiedRoad).getGothObj());
     * 
     * // Recuperation of the limited set of road sections designed to absorb //
     * modifications double bufferDist = 150.0 * Legend.getSYMBOLISATI0N_SCALE()
     * / 1000; HashMap<IRoadLine, GothicObject> limitedSetMap = new
     * HashMap<IRoadLine, GothicObject>(); Set gothicLimitedSet = new
     * Set(GothDatatype.OBJECT_ID); IFeatureCollection<IGeneObj> limitedSet =
     * CartagenQuadX.inArea("RoadLine", (IPolygon)
     * this.getGeom().buffer(bufferDist)); limitedSet.remove(this.getFeature());
     * CartagenApplication.getInstance().getFrame().getLayerManager()
     * .addToGeometriesPool(this.getGeom().buffer(bufferDist));
     * System.out.println(limitedSet.size()); for (IGeneObj geoObj : limitedSet)
     * { System.out.println(geoObj); if (!(geoObj instanceof
     * GothicBasedRoadLine)) { continue; } IRoadLine road = (IRoadLine) geoObj;
     * limitedSetMap.put(road, ((GothicBasedGeneObj) road).getGothObj());
     * gothicLimitedSet.addElement(((GothicBasedGeneObj) road).getGothObj()); }
     * 
     * // Call of the Gothic diffusion GothicObjectDiffusion diffusion = new
     * GothicObjectDiffusion();
     * diffusion.applyLimited(Cache.getCache().getGothicVac(),
     * gothicModifiedRoad, gothicLimitedSet);
     * 
     * // Update of the Java road sections for (IGeneObj geoObj : limitedSet) {
     * if (!(geoObj instanceof IRoadLine)) { continue; } IRoadLine road =
     * (IRoadLine) geoObj; GothicObject gothObj = limitedSetMap.get(road);
     * Geometry gothGeom = (Geometry) gothObj.getValue("geometry");
     * road.setGeom(GothicToGeoxygene.convert(gothGeom)); }
     * 
     * if (MesoSectionAgent.logger.isInfoEnabled()) {
     * MesoSectionAgent.logger.info("Diffusion of modifications"); }
     */
    // FIXME was working only with Clarity
  }

  /**
   * Algorithm to propagate the geometrical modifications of one section of a
   * line to the others At the beginning, the current section is not continuous
   * anymore with the whole line The global line is supposed to progressively
   * absorb the modifications to become continuous again, its end nodes not
   * being moved. Contains some bugs of reconnection
   * @throws InterruptedException
   */

  @SuppressWarnings("unused")
  private void propagateModifToSectionsOtherMethod(int currentSection) {

    int pause = 200;

    // ///////////////////////////
    // First part of the line
    // ///////////////////////////

    if (currentSection != 0) {

      // Points which lead the propagation: ending points of the first part of
      // the line and first point of the modified section
      IDirectPosition startFirstPart = this.getComponents().get(0).getGeom()
          .coord().get(0);
      IDirectPosition endFirstPart = this.getComponents()
          .get(currentSection - 1).getGeom().coord().get(this.getComponents()
              .get(currentSection - 1).getGeom().coord().size() - 1);
      IDirectPosition startCurrentSection = this.getComponents()
          .get(currentSection).getGeom().coord().get(0);

      // Direction of propagation
      double propagationDirection = Math
          .atan((startCurrentSection.getY() - startFirstPart.getY())
              / (startCurrentSection.getX() - startFirstPart.getX()));
      if (startCurrentSection.getX() < startFirstPart.getX()) {
        propagationDirection += Math.PI;
      }
      if (propagationDirection < 0) {
        propagationDirection += 2 * Math.PI;
      }

      // Longitudinal maximal translation - for end point of the first part to
      // reconnect the modified section
      double longLength = Math.sqrt(Math
          .pow(startCurrentSection.getX() - startFirstPart.getX(), 2.0)
          + Math.pow(startCurrentSection.getY() - startFirstPart.getY(), 2.0));
      double r = ((startFirstPart.getY() - endFirstPart.getY())
          * (startFirstPart.getY() - startCurrentSection.getY())
          - (startFirstPart.getX() - endFirstPart.getX())
              * (startCurrentSection.getX() - startFirstPart.getX()))
          / (Math.pow(longLength, 2.0));
      double maxLongTranslation = longLength * (1 - r);

      // Length of the first part of the line along the direction of propagation
      double firstPartLength = longLength * r;

      // Transverse maximal translation - for end point of the first part to
      // reconnect the modified section
      DirectPosition transPoint = new DirectPosition(
          startCurrentSection.getX() - Math.sin(propagationDirection),
          startCurrentSection.getY() + Math.cos(propagationDirection));
      double transLength = Math
          .sqrt(Math.pow(transPoint.getX() - startCurrentSection.getX(), 2.0)
              + Math.pow(transPoint.getY() - startCurrentSection.getY(), 2.0));
      r = ((startCurrentSection.getY() - endFirstPart.getY())
          * (startCurrentSection.getY() - transPoint.getY())
          - (startCurrentSection.getX() - endFirstPart.getX())
              * (transPoint.getX() - startCurrentSection.getX()))
          / (Math.pow(transLength, 2.0));
      double maxTransTranslation = transLength * (1 - r);

      // Translation of all points of of all sections of the first part of the
      // line
      for (int i = 0; i < currentSection; i++) {
        SectionAgent section = this.getComponents().get(i);
        if (section.getTriggeredByMeso() == 1) {
          section.setTriggeredByMeso(2);
        }
        for (int j = 0; j < section.getGeom().coord().size(); j++) {
          IDirectPosition currentPoint = section.getGeom().coord().get(j);
          // Projection of the current point on the propagation direction
          r = ((startFirstPart.getY() - currentPoint.getY())
              * (startFirstPart.getY() - startCurrentSection.getY())
              - (startFirstPart.getX() - currentPoint.getX())
                  * (startCurrentSection.getX() - startFirstPart.getX()))
              / (Math.pow(longLength, 2.0));
          double ratio = longLength * r / firstPartLength;
          // Longitudinal translation of the current point
          double longTranslation = ratio * maxLongTranslation;
          currentPoint.setX(currentPoint.getX()
              + longTranslation * Math.cos(propagationDirection));
          currentPoint.setY(currentPoint.getY()
              + longTranslation * Math.sin(propagationDirection));
          // Transversal translation of the current point
          double transTranslation = ratio * maxTransTranslation;
          currentPoint.setX(currentPoint.getX()
              - transTranslation * Math.sin(propagationDirection));
          currentPoint.setY(currentPoint.getY()
              + transTranslation * Math.cos(propagationDirection));
          // Current point update
          section.getGeom().coord().remove(j);
          section.getGeom().coord().add(j, currentPoint);
        }
      }

      // Last point treatment - to ensure good reconnection
      this.getComponents().get(currentSection - 1).getGeom().coord().remove(
          this.getComponents().get(currentSection - 1).getGeom().coord().size()
              - 1);
      this.getComponents().get(currentSection - 1).getGeom().coord()
          .add(new DirectPosition(startCurrentSection.getX(),
              startCurrentSection.getY()));

      /*
       * if (GeneralisationRightPanelAgentComplement.getInstance().cFairePauses
       * .isSelected()) { try { Thread.sleep(pause); } catch
       * (InterruptedException e) { e.printStackTrace(); }
       * CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
       * }
       */

    }

    // ///////////////////////////
    // Last part of the line
    // ///////////////////////////

    if (currentSection != this.getComponents().size() - 1) {

      // Points which lead the propagation: ending points of the last part of
      // the line and last point of the modified section
      IDirectPosition startLastPart = this.getComponents()
          .get(currentSection + 1).getGeom().coord().get(0);
      IDirectPosition endLastPart = this.getComponents()
          .get(this.getComponents().size() - 1).getGeom().coord()
          .get(this.getComponents().get(this.getComponents().size() - 1)
              .getGeom().coord().size() - 1);
      IDirectPosition endCurrentSection = this.getComponents()
          .get(currentSection).getGeom().coord()
          .get(this.getComponents().get(currentSection).getGeom().coord().size()
              - 1);

      // Direction of propagation
      double propagationDirection = Math.atan(endCurrentSection.getY()
          - endLastPart.getY() / endCurrentSection.getX() - endLastPart.getX());
      if (endCurrentSection.getX() < endLastPart.getX()) {
        propagationDirection += Math.PI;
      }
      if (propagationDirection < 0) {
        propagationDirection += 2 * Math.PI;
      }

      // Longitudinal maximal translation - for start point of the last part to
      // reconnect the modified section
      double longLength = Math
          .sqrt(Math.pow(endCurrentSection.getX() - endLastPart.getX(), 2.0)
              + Math.pow(endCurrentSection.getY() - endLastPart.getY(), 2.0));
      double r = ((endLastPart.getY() - startLastPart.getY())
          * (endLastPart.getY() - endCurrentSection.getY())
          - (endLastPart.getX() - startLastPart.getX())
              * (endCurrentSection.getX() - endLastPart.getX()))
          / (Math.pow(longLength, 2.0));
      double maxLongTranslation = longLength * (1 - r);

      // Length of the last part of the line along the direction of propagation
      double lastPartLength = longLength * r;

      // Transverse maximal translation - for start point of the first part to
      // reconnect the modified section
      DirectPosition transPoint = new DirectPosition(
          endCurrentSection.getX() - Math.sin(propagationDirection),
          endCurrentSection.getY() + Math.cos(propagationDirection));
      double transLength = Math
          .sqrt(Math.pow(transPoint.getX() - endCurrentSection.getX(), 2.0)
              + Math.pow(transPoint.getY() - endCurrentSection.getY(), 2.0));
      r = ((endCurrentSection.getY() - startLastPart.getY())
          * (endCurrentSection.getY() - transPoint.getY())
          - (endCurrentSection.getX() - startLastPart.getX())
              * (transPoint.getX() - endCurrentSection.getX()))
          / (Math.pow(transLength, 2.0));
      double maxTransTranslation = transLength * (1 - r);

      // Translation of all points of of all sections of the last part of the
      // line
      for (int i = currentSection + 1; i < this.getComponents().size(); i++) {
        SectionAgent section = this.getComponents().get(i);
        if (section.getTriggeredByMeso() == 1) {
          section.setTriggeredByMeso(2);
        }
        for (int j = 0; j < section.getGeom().coord().size(); j++) {
          IDirectPosition currentPoint = section.getGeom().coord().get(j);
          // Projection of the current point on the propagation direction
          r = ((endLastPart.getY() - currentPoint.getY())
              * (endLastPart.getY() - endCurrentSection.getY())
              - (endLastPart.getX() - currentPoint.getX())
                  * (endCurrentSection.getX() - endLastPart.getX()))
              / (Math.pow(longLength, 2.0));
          double ratio = longLength * r / lastPartLength;
          // Longitudinal translation of the current point
          double longTranslation = ratio * maxLongTranslation;
          currentPoint.setX(currentPoint.getX()
              + longTranslation * Math.cos(propagationDirection));
          currentPoint.setY(currentPoint.getY()
              + longTranslation * Math.sin(propagationDirection));
          // Transversal translation of the current point
          double transTranslation = ratio * maxTransTranslation;
          currentPoint.setX(currentPoint.getX()
              - transTranslation * Math.sin(propagationDirection));
          currentPoint.setY(currentPoint.getY()
              + transTranslation * Math.cos(propagationDirection));
          // Current point update
          section.getGeom().coord().remove(j);
          section.getGeom().coord().add(j, currentPoint);
        }
      }

      // First point treatment - to ensure good reconnection
      this.getComponents().get(currentSection + 1).getGeom().coord().remove(0);
      this.getComponents().get(currentSection + 1).getGeom().coord().add(0,
          new DirectPosition(endCurrentSection.getX(),
              endCurrentSection.getY()));

      /*
       * if (GeneralisationRightPanelAgentComplement.getInstance().cFairePauses
       * .isSelected()) { try { Thread.sleep(pause); } catch
       * (InterruptedException e) { e.printStackTrace(); }
       * CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
       * }
       */

    }

  }

  /**
   * Algorithm to propagate the geometrical modifications of one section of a
   * line to the others At the beginning, the current section is not continuous
   * anymore with the whole line The global line is supposed to progressively
   * absorb the modifications to become continuous again, its end nodes not
   * being moved. Supposed to be faster than current used method, but not proved
   * in practical. Overall, this algorithm is still full of bugs
   * @param currentSection : the position of the section whose geometry has been
   *          modified
   * @throws InterruptedException
   */

  @SuppressWarnings("unused")
  private void propagateModifToSectionsOtherMethod2(int currentSection) {

    int pause = 200;

    // Working copy of original micro geometries before propagation
    ArrayList<ILineString> initSectionsCopy = new ArrayList<ILineString>();
    for (SectionAgent s : this.getComponents()) {
      initSectionsCopy.add(new GM_LineString(s.getGeom().coord()));
    }

    // ///////////////////////////
    // First part of the line
    // ///////////////////////////

    if (currentSection != 0) {

      // First point treatment - to ensure good reconnection
      IDirectPosition startCurrentSection = this.getComponents()
          .get(currentSection).getGeom().coord().get(0);
      this.getComponents().get(currentSection - 1).getGeom().coord().remove(
          this.getComponents().get(currentSection - 1).getGeom().coord().size()
              - 1);
      this.getComponents().get(currentSection - 1).getGeom().coord()
          .add(new DirectPosition(startCurrentSection.getX(),
              startCurrentSection.getY()));

      // Trace of last point treated
      IDirectPosition lastPoint = new DirectPosition();
      IDirectPosition lastInitPoint = new DirectPosition();
      boolean isPropagationFinished = false;

      // Propagation loop over section vertices
      for (int i = currentSection - 1; i > -1; i--) {
        SectionAgent section = this.getComponents().get(i);
        ILineString initGeom = initSectionsCopy.get(i);
        if (section.getTriggeredByMeso() == 1) {
          section.setTriggeredByMeso(2);
        }
        for (int j = section.getGeom().coord().size() - 1; j > -1; j--) {

          // First point: already treated
          if (i == currentSection - 1
              && j == section.getGeom().coord().size() - 1) {
            lastPoint = section.getGeom().coord().get(j);
            lastInitPoint = initGeom.getControlPoint(j);
            continue;
          }

          // Computation of remaining displacement
          double remainingDisp = lastPoint.distance(lastInitPoint);
          if (remainingDisp <= GeneralisationSpecifications.getRESOLUTION()
              * Math.sqrt(2.0)) {
            isPropagationFinished = true;
            break;
          }
          double unitVectorDispX = (lastPoint.getX() - lastInitPoint.getX())
              / remainingDisp;
          double unitVectorDispY = (lastPoint.getY() - lastInitPoint.getY())
              / remainingDisp;
          double absorptionVectorNorm = remainingDisp
              * GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT;

          // Initial vector previous point - current point
          double initVectorX = initGeom.getControlPoint(j).getX()
              - lastInitPoint.getX();
          double initVectorY = initGeom.getControlPoint(j).getY()
              - lastInitPoint.getY();
          double initVectorNorm = initGeom.getControlPoint(j)
              .distance(lastInitPoint);
          if (initVectorNorm == 0.0) {
            lastPoint = section.getGeom().coord().get(j);
            lastInitPoint = initGeom.getControlPoint(j);
            continue;
          }

          // Direction of displacement
          double push = 0.0;
          double scalProd = initVectorX * unitVectorDispX
              + initVectorY * unitVectorDispY;
          if (scalProd > 0.0) {
            push = -1.0; // previous point was pushed
          } else if (scalProd < 0.0) {
            push = 1.0; // previous point was pulled
          } else {
            push = 0.0; // perpendicular displacement
          }

          // If perpendicular displacement -> no absorption
          if (push == 0.0) {
            absorptionVectorNorm = 0.0;
          } else if (push == -1.0) {
            absorptionVectorNorm = Math.min(absorptionVectorNorm,
                (Math.pow(initVectorNorm, 2.0)) / (-push * scalProd));
          }
          // If current point is pushed -> displacement is limited as twice of
          // initVectorNorm
          else {
            if (GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT != 1.0) {
              absorptionVectorNorm = Math.min(absorptionVectorNorm,
                  2.0 * initVectorNorm);
            }
          }

          // Absorption vector
          double AbsorptionX = absorptionVectorNorm * unitVectorDispX;
          double AbsorptionY = absorptionVectorNorm * unitVectorDispY;

          // Translation of the current point with absorption
          section.getGeom().coord().remove(j);
          section.getGeom().coord().add(j,
              new DirectPosition(lastPoint.getX() + initVectorX - AbsorptionX,
                  lastPoint.getY() + initVectorY - AbsorptionY));

          // Update of last point treated
          lastPoint = section.getGeom().coord().get(j);
          lastInitPoint = initGeom.getControlPoint(j);

        }
        if (isPropagationFinished) {
          break;
        }
      }

      // Pause
      /*
       * if (GeneralisationRightPanelAgentComplement.getInstance().cFairePauses
       * .isSelected()) { try { Thread.sleep(pause); } catch
       * (InterruptedException e) { e.printStackTrace(); }
       * CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
       * }
       */

    }

    // ///////////////////////////
    // Last part of the line
    // ///////////////////////////

    if (currentSection != this.getComponents().size() - 1) {

      // First point treatment - to ensure good reconnection
      IDirectPosition endCurrentSection = this.getComponents()
          .get(currentSection).getGeom().coord()
          .get(this.getComponents().get(currentSection).getGeom().coord().size()
              - 1);
      this.getComponents().get(currentSection + 1).getGeom().coord().remove(0);
      this.getComponents().get(currentSection + 1).getGeom().coord().add(0,
          new DirectPosition(endCurrentSection.getX(),
              endCurrentSection.getY()));

      // Trace of last point treated
      IDirectPosition lastPoint = new DirectPosition();
      IDirectPosition lastInitPoint = new DirectPosition();
      boolean isPropagationFinished = false;

      // Propagation loop over section vertices
      for (int i = currentSection + 1; i < this.getComponents().size(); i++) {
        SectionAgent section = this.getComponents().get(i);
        ILineString initGeom = initSectionsCopy.get(i);
        if (section.getTriggeredByMeso() == 1) {
          section.setTriggeredByMeso(2);
        }
        for (int j = 0; j < section.getGeom().coord().size(); j++) {

          // First point: already treated
          if (i == currentSection + 1 && j == 0) {
            lastPoint = section.getGeom().coord().get(j);
            lastInitPoint = initGeom.getControlPoint(j);
            continue;
          }

          // Computation of remaining displacement
          double remainingDisp = lastPoint.distance(lastInitPoint);
          if (remainingDisp <= GeneralisationSpecifications.getRESOLUTION()
              * Math.sqrt(2.0)) {
            isPropagationFinished = true;
            break;
          }
          double unitVectorDispX = (lastPoint.getX() - lastInitPoint.getX())
              / remainingDisp;
          double unitVectorDispY = (lastPoint.getY() - lastInitPoint.getY())
              / remainingDisp;
          double absorptionVectorNorm = remainingDisp
              * GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT;

          // Initial vector previous point - current point
          double initVectorX = initGeom.getControlPoint(j).getX()
              - lastInitPoint.getX();
          double initVectorY = initGeom.getControlPoint(j).getY()
              - lastInitPoint.getY();
          double initVectorNorm = initGeom.getControlPoint(j)
              .distance(lastInitPoint);
          if (initVectorNorm == 0.0) {
            lastPoint = section.getGeom().coord().get(j);
            lastInitPoint = initGeom.getControlPoint(j);
            continue;
          }

          // Direction of displacement
          double push = 0.0;
          double scalProd = initVectorX * unitVectorDispX
              + initVectorY * unitVectorDispY;
          if (scalProd > 0.0) {
            push = -1.0; // previous point was pushed
          } else if (scalProd < 0.0) {
            push = 1.0; // previous point was pulled
          } else {
            push = 0.0; // perpendicular displacement
          }

          // If perpendicular displacement -> no absorption
          if (push == 0.0) {
            absorptionVectorNorm = 0.0;
          } else if (push == -1.0) {
            absorptionVectorNorm = Math.min(absorptionVectorNorm,
                (Math.pow(initVectorNorm, 2.0)) / (-push * scalProd));
          }
          // If current point is pushed -> displacement is limited as twice of
          // initVectorNorm
          else {
            if (GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT != 1.0) {
              absorptionVectorNorm = Math.min(absorptionVectorNorm,
                  2.0 * initVectorNorm);
            }
          }

          // Absorption vector
          double AbsorptionX = absorptionVectorNorm * unitVectorDispX;
          double AbsorptionY = absorptionVectorNorm * unitVectorDispY;

          // Translation of the current point with absorption
          section.getGeom().coord().remove(j);
          section.getGeom().coord().add(j,
              new DirectPosition(lastPoint.getX() + initVectorX - AbsorptionX,
                  lastPoint.getY() + initVectorY - AbsorptionY));

          // Update of last point treated
          lastPoint = section.getGeom().coord().get(j);
          lastInitPoint = initGeom.getControlPoint(j);

        }
        if (isPropagationFinished) {
          break;
        }
      }

      // Pause
      /*
       * if (GeneralisationRightPanelAgentComplement.getInstance().cFairePauses
       * .isSelected()) { try { Thread.sleep(pause); } catch
       * (InterruptedException e) { e.printStackTrace(); }
       * CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
       * }
       */

    }

  }

}
