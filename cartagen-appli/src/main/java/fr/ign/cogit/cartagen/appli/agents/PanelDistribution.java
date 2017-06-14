package fr.ign.cogit.cartagen.appli.agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fr.ign.cogit.geoxygene.util.algo.MathUtil;

/**
 * a panel displaying a distibution
 * 
 * @author JGaffuri 25 août 2009
 * 
 */
public class PanelDistribution extends JPanel {
  private static final long serialVersionUID = 1L;

  /**
	 */
  private JFreeChart chart;

  /**
   * @param title title of the panel
   * @param data
   * @param max the max value of the Y axis. if negative, automatically take the
   *          max value
   * @param axisVisible if the axis are visible or not
   */
  public PanelDistribution(String title, ArrayList<Double> data, double max,
      boolean axisVisible) {
    super();

    // sort the data
    Collections.sort(data);

    // create the graph's dataset

    // the graph's renderer
    XYBarRenderer barRenderer = new XYBarRenderer(0);
    barRenderer.setBarPainter(new StandardXYBarPainter());
    barRenderer.setShadowVisible(false);
    barRenderer.setSeriesPaint(0, Color.BLACK);

    // create data serie
    XYSeries series = new XYSeries("1");
    int i = data.size() - 1;
    for (Double d : data) {
      // if (d.doubleValue() > max) series.add(i--, max); else
      series.add(i--, d);
    }

    // create the graph's dataset
    XYSeriesCollection collection = new XYSeriesCollection();
    collection.addSeries(series);
    XYBarDataset graphDataset = new XYBarDataset(collection, 0.9);

    // set the bar's size
    graphDataset.setBarWidth(1.2);

    // create the chart
    XYPlot plot = new XYPlot(graphDataset, new NumberAxis(null),
        new NumberAxis(null), barRenderer);
    // do not draw the grid
    plot.setDomainGridlinesVisible(false);
    plot.setRangeGridlinesVisible(false);
    this.chart = new JFreeChart("", null, plot, false);
    this.chart.setBackgroundPaint(Color.WHITE);
    this.chart.setBorderVisible(false);

    // sets the value domain of the Y axis
    if (max > 0) {
      plot.getRangeAxis().setRange(0, max);
    }

    // delete the axes
    plot.getRangeAxis().setVisible(axisVisible);
    plot.getDomainAxis().setVisible(axisVisible);

    // create the chart panel
    ChartPanel chartPanel = new ChartPanel(this.chart);
    chartPanel.setPreferredSize(new Dimension(150, 150));
    chartPanel.setMouseZoomable(true);

    // compute the mean and standard deviation
    double mean = MathUtil.moyenne(data);
    double std = MathUtil.ecartType(data);

    // sets layout
    LayoutManager layout = new BoxLayout(this, BoxLayout.Y_AXIS);
    this.setLayout(layout);

    // set border
    this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

    // add components
    this.add(new JLabel(title));
    this.add(chartPanel);
    this.add(new JLabel("Mean: " + mean));
    this.add(new JLabel("Std: " + std));
  }

  public void export(String path, int width, int height) {
    try {
      ChartUtilities.writeChartAsPNG(new FileOutputStream(new File(path)),
          this.chart, width, height);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
