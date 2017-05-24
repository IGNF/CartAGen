package fr.ign.cogit.cartagen.evaluation.global;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.xerces.dom.DocumentImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.evaluation.ConstraintSatisfaction;
import fr.ign.cogit.cartagen.evaluation.SpecificationMonitor;
import fr.ign.cogit.geoxygene.util.XMLUtil;

public class ConstraintSatisfactionDistribution {

  private Map<SpecificationMonitor, ConstraintSatisfaction> distribution;
  private JFreeChart chart, chartIni, chartPrev;
  private Frequency freq, freqIni, freqPrev;
  private DescriptiveStatistics stats, statsIni, statsPrev;
  private int valuesNb;
  private String name;
  private DefaultCategoryDataset dataset, datasetIni, datasetPrev;

  public ConstraintSatisfactionDistribution(String name,
      Map<SpecificationMonitor, ConstraintSatisfaction> distribution) {
    super();
    this.name = name;
    this.distribution = distribution;
    this.freq = new Frequency();
    this.stats = new DescriptiveStatistics();
    this.freqIni = new Frequency();
    this.statsIni = new DescriptiveStatistics();
    this.freqPrev = new Frequency();
    this.statsPrev = new DescriptiveStatistics();
    this.dataset = this.buildEmptyDataset();
    this.datasetIni = this.buildEmptyDataset();
    this.datasetPrev = this.buildEmptyDataset();
    String chartName = "Constraints distribution";
    this.setValuesNb(0);
    for (SpecificationMonitor m : distribution.keySet()) {
      int previous = m.getPreviousStates().get(m.getPreviousStates().size() - 1)
          .ordinal() + 1;
      int initial = m.getPreviousStates().get(0).ordinal() + 1;
      for (int i = 0; i < m.getImportance(); i++) {
        dataset.incrementValue(1.0, "Constraints number",
            m.getSatisfaction().ordinal() + 1);
        datasetIni.incrementValue(1.0, "Constraints number", initial);
        datasetPrev.incrementValue(1.0, "Constraints number", previous);
        freq.addValue(m.getSatisfaction().ordinal() + 1);
        stats.addValue(m.getSatisfaction().ordinal() + 1);
        freqIni.addValue(initial);
        statsIni.addValue(initial);
        freqPrev.addValue(previous);
        statsPrev.addValue(previous);
        setValuesNb(getValuesNb() + 1);
      }
    }
    this.chart = ChartFactory.createBarChart(chartName, "Satisfaction",
        "Constraints number", dataset, PlotOrientation.VERTICAL, false, false,
        false);
    this.chartIni = ChartFactory.createBarChart(chartName, "Satisfaction",
        "Constraints number", datasetIni, PlotOrientation.VERTICAL, false,
        false, false);
    this.chartPrev = ChartFactory.createBarChart(chartName, "Satisfaction",
        "Constraints number", datasetPrev, PlotOrientation.VERTICAL, false,
        false, false);
  }

  public ConstraintSatisfactionDistribution(File file)
      throws ParserConfigurationException, SAXException, IOException {
    super();
    // on commence par ouvrir le doucment XML pour le parser
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();
    Element root = (Element) doc
        .getElementsByTagName("constraint-monitors-distribution").item(0);
    // get the name
    Element nameElem = (Element) root.getElementsByTagName("name").item(0);
    this.name = nameElem.getChildNodes().item(0).getNodeValue();

    this.distribution = new HashMap<>();
    Element perfectElem = (Element) root.getElementsByTagName("PERFECT")
        .item(0);
    int nb = Integer
        .valueOf(perfectElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(
          new ToySpecificationMonitor(ConstraintSatisfaction.PERFECT),
          ConstraintSatisfaction.PERFECT);
    Element veryElem = (Element) root.getElementsByTagName("VERY_SATISFIED")
        .item(0);
    nb = Integer.valueOf(veryElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(
          new ToySpecificationMonitor(ConstraintSatisfaction.VERY_SATISFIED),
          ConstraintSatisfaction.VERY_SATISFIED);
    Element correctElem = (Element) root.getElementsByTagName("CORRECT")
        .item(0);
    nb = Integer.valueOf(correctElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(
          new ToySpecificationMonitor(ConstraintSatisfaction.CORRECT),
          ConstraintSatisfaction.CORRECT);
    Element accElem = (Element) root.getElementsByTagName("ACCEPTABLE").item(0);
    nb = Integer.valueOf(accElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(
          new ToySpecificationMonitor(ConstraintSatisfaction.ACCEPTABLE),
          ConstraintSatisfaction.ACCEPTABLE);
    Element fairElem = (Element) root.getElementsByTagName("FAIR").item(0);
    nb = Integer.valueOf(fairElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(new ToySpecificationMonitor(ConstraintSatisfaction.FAIR),
          ConstraintSatisfaction.FAIR);
    Element bareElem = (Element) root.getElementsByTagName("BARELY_SATISFIED")
        .item(0);
    nb = Integer.valueOf(bareElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(
          new ToySpecificationMonitor(ConstraintSatisfaction.BARELY_SATISFIED),
          ConstraintSatisfaction.BARELY_SATISFIED);
    Element notElem = (Element) root.getElementsByTagName("NOT_SATISFIED")
        .item(0);
    nb = Integer.valueOf(notElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(
          new ToySpecificationMonitor(ConstraintSatisfaction.NOT_SATISFIED),
          ConstraintSatisfaction.NOT_SATISFIED);
    Element unaccElem = (Element) root.getElementsByTagName("UNACCEPTABLE")
        .item(0);
    nb = Integer.valueOf(unaccElem.getChildNodes().item(0).getNodeValue());
    for (int i = 0; i < nb; i++)
      distribution.put(
          new ToySpecificationMonitor(ConstraintSatisfaction.UNACCEPTABLE),
          ConstraintSatisfaction.UNACCEPTABLE);

    this.freq = new Frequency();
    this.stats = new DescriptiveStatistics();
    this.freqIni = new Frequency();
    this.statsIni = new DescriptiveStatistics();
    this.freqPrev = new Frequency();
    this.statsPrev = new DescriptiveStatistics();
    this.dataset = this.buildEmptyDataset();
    this.datasetIni = this.buildEmptyDataset();
    this.datasetPrev = this.buildEmptyDataset();
    String chartName = "Constraints distribution";
    this.setValuesNb(0);
    for (SpecificationMonitor m : distribution.keySet()) {
      int previous = m.getPreviousStates().get(m.getPreviousStates().size() - 1)
          .ordinal() + 1;
      int initial = m.getPreviousStates().get(0).ordinal() + 1;
      for (int i = 0; i < m.getImportance(); i++) {
        dataset.incrementValue(1.0, "Constraints number",
            m.getSatisfaction().ordinal() + 1);
        datasetIni.incrementValue(1.0, "Constraints number", initial);
        datasetPrev.incrementValue(1.0, "Constraints number", previous);
        freq.addValue(m.getSatisfaction().ordinal() + 1);
        stats.addValue(m.getSatisfaction().ordinal() + 1);
        freqIni.addValue(initial);
        statsIni.addValue(initial);
        freqPrev.addValue(previous);
        statsPrev.addValue(previous);
        setValuesNb(getValuesNb() + 1);
      }
    }
    this.chart = ChartFactory.createBarChart(chartName, "Satisfaction",
        "Constraints number", dataset, PlotOrientation.VERTICAL, false, false,
        false);
    this.chartIni = ChartFactory.createBarChart(chartName, "Satisfaction",
        "Constraints number", datasetIni, PlotOrientation.VERTICAL, false,
        false, false);
    this.chartPrev = ChartFactory.createBarChart(chartName, "Satisfaction",
        "Constraints number", datasetPrev, PlotOrientation.VERTICAL, false,
        false, false);
  }

  public Map<SpecificationMonitor, ConstraintSatisfaction> getDistribution() {
    return distribution;
  }

  public void setDistribution(
      HashMap<SpecificationMonitor, ConstraintSatisfaction> distribution) {
    this.distribution = distribution;
  }

  public JFreeChart getChart() {
    return chart;
  }

  public void setChart(JFreeChart chart) {
    this.chart = chart;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Show the distribution in a frame with an histogram.
   */
  public void showChart() {
    String nom = this.name + " Satisfaction Distribution";
    JDialog dialog = new JDialog();
    dialog.setTitle(nom);
    dialog.add(new ChartPanel(chart));
    dialog.setSize(400, 300);
    dialog.setVisible(true);
  }

  /**
   * Show the initial distribution in a frame with an histogram.
   */
  public void showChartIni() {
    String nom = this.name + " Initial Satisfaction Distribution";
    JDialog dialog = new JDialog();
    dialog.setTitle(nom);
    dialog.add(new ChartPanel(chartIni));
    dialog.setSize(400, 300);
    dialog.setVisible(true);
  }

  /**
   * Show the previous distribution in a frame with an histogram.
   */
  public void showChartPrev() {
    String nom = this.name + " Previous Satisfaction Distribution";
    JDialog dialog = new JDialog();
    dialog.setTitle(nom);
    dialog.add(new ChartPanel(chartPrev));
    dialog.setSize(400, 300);
    dialog.setVisible(true);
  }

  /**
   * Build an empty dataset for printing the distribution in a chart.
   * @return
   */
  private DefaultCategoryDataset buildEmptyDataset() {
    DefaultCategoryDataset newDataset = new DefaultCategoryDataset();
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.PERFECT.ordinal() + 1));
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.VERY_SATISFIED.ordinal() + 1));
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.CORRECT.ordinal() + 1));
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.ACCEPTABLE.ordinal() + 1));
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.FAIR.ordinal() + 1));
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.BARELY_SATISFIED.ordinal() + 1));
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.NOT_SATISFIED.ordinal() + 1));
    newDataset.addValue(0.0, "Constraints number",
        new Integer(ConstraintSatisfaction.UNACCEPTABLE.ordinal() + 1));
    return newDataset;
  }

  /**
   * Compute the satisfaction mean weighted by importance.
   * @return
   */
  public double getMean() {
    return stats.getMean();
  }

  /**
   * Compute the satisfaction standard deviation weighted by importance.
   * @return
   */
  public double getStandardDeviation() {
    return stats.getStandardDeviation();
  }

  public double getFreqSatisfactionDifference() {
    double freqSatisf = 1 - freq.getCumPct(5);
    double freqSatisfIni = 1 - freqIni.getCumPct(5);
    return freqSatisf - freqSatisfIni;
  }

  public double getFreqNonSatisfactionDifference() {
    double freqPeuSatisf = freq.getCumPct(2);
    double freqPeuSatisfIni = freqIni.getCumPct(2);
    return freqPeuSatisfIni - freqPeuSatisf;
  }

  public double getFreqIniNonSatisfaction() {
    return freqIni.getCumPct(2);
  }

  public double getFreqNonSatisfaction() {
    return freq.getCumPct(2);
  }

  public double getFreqIniSatisfaction() {
    return 1 - freqIni.getCumPct(5);
  }

  public double getFreqSatisfaction() {
    return 1 - freq.getCumPct(5);
  }

  public long getCardinal() {
    return this.stats.getN();
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * Get the satisfactions in a sorted list from least satisfied to most
   * satisfied
   * @return
   */
  public List<ConstraintSatisfaction> toList() {
    ArrayList<ConstraintSatisfaction> list = new ArrayList<ConstraintSatisfaction>();
    list.addAll(distribution.values());
    Collections.sort(list);
    return list;
  }

  /**
   * Builds a distribution from the previous satisfaction values of this
   * distribution.
   * @return
   */
  public ConstraintSatisfactionDistribution getPreviousDistribution() {
    HashMap<SpecificationMonitor, ConstraintSatisfaction> prevDistrib = new HashMap<SpecificationMonitor, ConstraintSatisfaction>();
    for (SpecificationMonitor m : distribution.keySet()) {
      ConstraintSatisfaction previous = m.getPreviousStates()
          .get(m.getPreviousStates().size() - 1);
      prevDistrib.put(m, previous);
    }
    ConstraintSatisfactionDistribution previous = new ConstraintSatisfactionDistribution(
        this.name + "_previous", prevDistrib);
    return previous;
  }

  /**
   * Prints the details of the distribution in the console as a map
   * (satisfaction value: number of monitors).
   */
  public void print() {
    System.out.println("Distribution: " + this.name);
    int rowIndex = dataset.getRowIndex("Constraints number");
    for (int i = 0; i < dataset.getColumnCount(); i++) {
      String toPrint = dataset.getColumnKey(i) + " : "
          + dataset.getValue(rowIndex, i);
      System.out.println(toPrint);
    }
  }

  /**
   * Prints the details of the distribution in the console as a map
   * (satisfaction value: number of monitors).
   */
  public void printPrev() {
    System.out.println("Distribution: " + this.name + " - previous");
    int rowIndex = datasetPrev.getRowIndex("Constraints number");
    for (int i = 0; i < datasetPrev.getColumnCount(); i++) {
      String toPrint = datasetPrev.getColumnKey(i) + " : "
          + datasetPrev.getValue(rowIndex, i);
      System.out.println(toPrint);
    }
  }

  public void exportToXml() throws TransformerException, IOException {
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(null);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    // path est le chemin jusqu'au fichier
    File path = fc.getSelectedFile();

    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc.createElement("constraint-monitors-distribution");
    // name element
    Element nameElem = xmlDoc.createElement("name");
    Node node = xmlDoc.createTextNode(name);
    nameElem.appendChild(node);
    root.appendChild(nameElem);

    int rowIndex = dataset.getRowIndex("Constraints number");
    for (int i = 0; i < dataset.getColumnCount(); i++) {
      int index = Integer.valueOf(dataset.getColumnKey(i).toString());
      Element elem = xmlDoc
          .createElement(ConstraintSatisfaction.valueOf(index - 1).name());
      Node n = xmlDoc.createTextNode(dataset.getValue(rowIndex, i).toString());
      elem.appendChild(n);
      root.appendChild(elem);
    }

    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, path);
  }

  public int getValuesNb() {
    return valuesNb;
  }

  public void setValuesNb(int valuesNb) {
    this.valuesNb = valuesNb;
  }
}
