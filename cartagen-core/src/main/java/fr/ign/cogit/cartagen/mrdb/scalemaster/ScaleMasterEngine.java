/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement.ProcessPriority;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.filter.Filter;

/**
 * This class contains the engine that automatically generalises data at a given
 * scale, following the multi-scale specifications of a ScaleMaster2.0.
 * 
 * @author GTouya
 *
 */
public class ScaleMasterEngine {

    private static String CSV_PROCESSES = "src/main/resources/scalemaster/ScaleMasterProcesses.csv";

    private ScaleMaster scaleMaster;
    private Set<ScaleMasterTheme> themes;
    private Map<String, String> mapAvailableProcesses, mapDatabases;

    public ScaleMaster getScaleMaster() {
        return scaleMaster;
    }

    public void setScaleMaster(ScaleMaster scaleMaster) {
        this.scaleMaster = scaleMaster;
    }

    /**
     * Default constructor with a @ScaleMaster instance.
     * 
     * @param scaleMaster
     */
    public ScaleMasterEngine(ScaleMaster scaleMaster,
            Set<ScaleMasterTheme> themes) {
        super();
        this.scaleMaster = scaleMaster;
        this.themes = themes;
        this.loadAvailableProcesses();
        this.mapDatabases = new HashMap<>();
    }

    /**
     * Load from CSV_PROCESSES the available processes to run the
     * generalisation.
     */
    private void loadAvailableProcesses() {
        this.mapAvailableProcesses = new HashMap<>();
        Path pathToFile = Paths.get(CSV_PROCESSES);
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.UTF_8)) {

            // read the first line from the text file
            String line = br.readLine();

            // loop until all lines are read
            while (line != null) {
                String[] attributes = line.split(",");
                mapAvailableProcesses.put(attributes[0], attributes[1]);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Execute the generalisation from @this ScaleMaster at the given scale.
     * 
     * @param scale
     *            denominator of the output scale.
     */
    @SuppressWarnings("unchecked")
    public void execute(int scale) {
        // if the map of databases has been filled
        if (this.mapDatabases.size() == 0) {
            JOptionPane.showMessageDialog(null,
                    "Please set the databases name first");
            return;
        }

        // first set CartAGen output scale to the given scale
        Legend.setSYMBOLISATI0N_SCALE(scale);

        // then, trigger the required data enrichments
        // TODO

        // ***********************************************
        // main loop on the ScaleLines of the ScaleMaster
        for (ScaleLine scaleLine : this.scaleMaster.getScaleLines()) {

            // first, get the element for this scale
            ScaleMasterElement element = scaleLine.getElementFromScale(scale);

            // then, get the features corresponding to this line theme
            ScaleMasterTheme theme = scaleLine.getTheme();
            String databaseName = element.getDbName();
            String cartagenDbName = this.mapDatabases.get(databaseName);

            CartAGenDB database = CartAGenDoc.getInstance().getDatabases()
                    .get(cartagenDbName);
            CartAGenDataSet dataset = database.getDataSet();
            IPopulation<? extends IGeneObj> features = dataset.getCartagenPop(
                    dataset.getPopNameFromFeatType(theme.getPopulationName()));

            // apply the attribute filter on the features
            Filter filter = element.getOgcFilter();
            IFeatureCollection<IGeneObj> filteredFeats = new FT_FeatureCollection<>();
            for (IGeneObj obj : features) {
                if (filter == null)
                    filteredFeats.add(obj);
                else if (filter.evaluate(obj))
                    filteredFeats.add(obj);
                else
                    obj.eliminate();
            }

            // then, get the processes in this line
            List<ScaleMasterGeneProcess> processes = new ArrayList<>();
            for (int i = 0; i < element.getProcessesToApply().size(); i++) {
                String name = element.getProcessesToApply().get(i);
                ProcessPriority priority = element.getProcessPriorities()
                        .get(i);
                Map<String, Object> parameters = element.getParameters().get(i);

                // create the ScaleMasterGeneProcess instance
                String className = mapAvailableProcesses.get(name);
                ScaleMasterGeneProcess instance = null;
                try {
                    Class<? extends ScaleMasterGeneProcess> classInstance = (Class<? extends ScaleMasterGeneProcess>) Class
                            .forName(className);
                    instance = classInstance.newInstance();
                    instance.setScale(scale);
                    instance.setPriority(priority);

                    // parameterise it
                    for (String paramName : parameters.keySet()) {
                        if (instance.hasParameter(paramName)) {
                            instance.setParameterValue(paramName,
                                    parameters.get(paramName));
                        } else {
                            Object paramValue = parameters.get(paramName);
                            ProcessParameter param = new ProcessParameter(
                                    paramName, paramValue.getClass(),
                                    paramValue);
                            instance.addParameter(param);
                        }
                    }

                    // put it in the list to be ordered
                    processes.add(instance);
                } catch (ClassNotFoundException | InstantiationException
                        | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // sort the list of processes is descending order of priority
            Collections.sort(processes);
            Collections.reverse(processes);

            // loop on the processes to apply them
            for (ScaleMasterGeneProcess process : processes) {
                try {
                    process.execute(filteredFeats, dataset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Set<ScaleMasterTheme> getThemes() {
        return themes;
    }

    public void setThemes(Set<ScaleMasterTheme> themes) {
        this.themes = themes;
    }

    public void addDatabaseMapping(String scaleMasterName,
            String cartagenName) {
        this.mapDatabases.put(scaleMasterName, cartagenName);
    }
}
