/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.appli.plugins.machinelearning;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.deeplearning.vector2image.CoordinateTransformation;
import fr.ign.cogit.cartagen.appli.core.geoxygene.CartAGenPlugin;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.deeplearning.vector2image.Roads2Image;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * This plugin contains methods to generate different types of training datasets
 * for deep learning techniques.
 * 
 * @author gtouya
 *
 */
public class TrainingDatasetGenerator extends JMenu {

    private static int IMAGE_SIZE = 128;
    private static final long serialVersionUID = 1L;
    private static TrainingDatasetGenerator instance = null;
    private static Logger logger = Logger
            .getLogger(TrainingDatasetGenerator.class.getName());

    public TrainingDatasetGenerator() {
        // Exists only to defeat instantiation.
        super();
    }

    public static TrainingDatasetGenerator getInstance() {
        if (TrainingDatasetGenerator.instance == null) {
            TrainingDatasetGenerator.instance = new TrainingDatasetGenerator(
                    "Training Data Generator");
        }
        return TrainingDatasetGenerator.instance;
    }

    public TrainingDatasetGenerator(String title) {
        super(title);
        TrainingDatasetGenerator.instance = this;

        JMenu buildingMenu = new JMenu("Building Generalisation");
        JMenu urbanMenu = new JMenu("Urban Map Generalisation");
        JMenu roadMenu = new JMenu("Road Generalisation");
        JMenu enrichMenu = new JMenu("Data Enrichment");
        this.add(buildingMenu);
        this.add(roadMenu);
        roadMenu.add(new RoadPiX2PixAction());
        this.add(urbanMenu);
        urbanMenu.add(new FromLayerAction());
        urbanMenu.add(new FromSymbolAction());
        this.add(enrichMenu);
        enrichMenu.add(new BlockImagesAction());
        enrichMenu.add(new RoadImagesAction());
        enrichMenu.add(new AliImagesAction());
        this.addSeparator();
        this.add(new ChangeSizeAction());
    }

    private class ChangeSizeAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO
                }
            });
            th.start();
        }

        public ChangeSizeAction() {
            super();
            this.putValue(Action.NAME, "Change image size");
        }

    }
    
    /**
     * This action construct mask of shape and location for road, water and building at 1:25 000 
     * and the corresponding symbolized generalisation at 1:50 000.
     * 
     * @author ACourtial
     *
     */
    private class FromLayerAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private String build25Name="build25";
        private String build50Name="build50";
        private String road25Name="road25";
        private String road50Name="road50";
        private String water25Name="water25";
        private String water50Name="water50";
        private String gray50Name="gray50";
        private String outputPath="D://urbandataset//fromlayer";
	    private Color building_color = new Color(137,110,90);
	    private Color graying_color = new Color(230, 230, 210);
	    private Color road_color = new Color(250, 250, 200);
	    private Color water_color = new Color(170, 210, 230);
    	private GeOxygeneApplication application;
    	private int imageSize=512; private float scale=500; private int recov=60;
    	
  	
        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                	application = CartAGenPlugin.getInstance().getApplication();
        	 		Layer build25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(build25Name);
        			Layer build50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(build50Name);
        			Layer road25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(road25Name);
        			Layer road50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(road50Name);
        			Layer water25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(water25Name);
        			Layer water50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(water50Name);
        			Layer gray50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(gray50Name);
        			
        			IFeatureCollection<? extends IFeature> build25 = build25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> build50 = build50Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> road25 = road25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> road50 = road50Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> water25 = water25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> water50 = water50Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> gray50 = gray50Layer.getFeatureCollection();
        			
        			IEnvelope carte=road25.envelope(); IDirectPosition p1=carte.getLowerCorner(); IDirectPosition p2=carte.getUpperCorner();
        	    	double xmin=carte.minX(); double ymin=carte.minY(); double xmax=carte.maxX(); double ymax=carte.maxY();
        	    	IEnvelope fenetre= (IEnvelope) carte.clone();
        	    	
        	    	double pas_intern=scale;
        	    	double pas_extern=scale*recov/100;
        	    	int nb=0;
        	    	p2.setCoordinate(xmin+pas_intern, ymin+(pas_intern)); 
        	    	while (p1.getX()<xmax){
        	    		p1.setCoordinate(p1.getX(), ymin);
        	    		p2.setCoordinate(p2.getX(), ymin+(pas_intern));
        	    		while(p1.getY()<ymax){
        	    		    fenetre.setLowerCorner(p1);
        	    		    fenetre.setUpperCorner(p2);
        	                Collection<IFeature> build50s =(Collection<IFeature>)  build50.select((IEnvelope) fenetre);
        	                Collection<IFeature> road50s =(Collection<IFeature>)  road50.select((IEnvelope) fenetre);
        	                Collection<IFeature> water50s =(Collection<IFeature>)  water50.select((IEnvelope) fenetre);
        	                Collection<IFeature> gray50s =(Collection<IFeature>)  gray50.select((IEnvelope) fenetre);
        	                Collection<IFeature> build25s =(Collection<IFeature>)  build25.select((IEnvelope) fenetre);
        	                Collection<IFeature> road25s =(Collection<IFeature>)  road25.select((IEnvelope) fenetre);
        	                Collection<IFeature> water25s =(Collection<IFeature>)  water25.select((IEnvelope) fenetre);

    	                	
        	                if(build25s.isEmpty()) {
        	                	p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	                	p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);
        	                	continue;
        	                }

        	                
        	    		    BufferedImage buildImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_BYTE_GRAY);
        	    		    BufferedImage roadImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_BYTE_GRAY);
        	    		    BufferedImage waterImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_BYTE_GRAY);
        	    		    BufferedImage targetImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_INT_RGB);
        	    		    
        	    		    Graphics2D buildGraphic= buildImg.createGraphics();
        	    		    buildGraphic.setBackground(new Color(0, 0, 0));
        	    		    buildGraphic.clearRect(0, 0, imageSize, imageSize);
        	                
        	    		    Graphics2D roadGraphic= roadImg.createGraphics();
        	    		    roadGraphic.setBackground(new Color(0, 0, 0));
        	    		    roadGraphic.clearRect(0, 0, imageSize, imageSize);
        	                
        	    		    Graphics2D waterGraphic= waterImg.createGraphics();
        	    		    waterGraphic.setBackground(new Color(0, 0, 0));
        	    		    waterGraphic.clearRect(0, 0, imageSize, imageSize);
        	    	
        	    		    Graphics2D targetGraphic= targetImg.createGraphics();
        	    		    targetGraphic.setBackground(new Color(255, 255, 255));
        	    		    targetGraphic.clearRect(0, 0, imageSize, imageSize);

        	                
        	    		    IEnvelope env = fenetre.getGeom().getEnvelope();
        	    	        double xMin = env.minX();
        	    	        double yMin = env.minY();
        	    	        double ratio = 0.0;

        	    	        ratio = (imageSize - 2 * 3) / env.length();

        	    	        CoordinateTransformation transform= new CoordinateTransformation(xMin, yMin, ratio, 3, imageSize);

        	    	        for (IFeature build : build50s) {
        	                    IMultiSurface<IPolygon> geom = (IMultiSurface)( build.getGeom());
        	                    for (IPolygon elem : geom){
        	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
        	                    	int numPoints1 = viewDirectPositionList1.size();
        	                        int[] xpoints1 = new int[numPoints1];
        	                        int[] ypoints1 = new int[numPoints1];
        	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                            xpoints1[i] = (int) p3.getX();
        	                            ypoints1[i] = (int) p3.getY();}
        	                        targetGraphic.setColor(building_color);
        	                        targetGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                        for (int i=0; i<elem.sizeInterior();i++){ 
        	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
        	                        	int numPoints2 = viewDirectPositionList2.size();
        	                            int[] xpoints2 = new int[numPoints2];
        	                            int[] ypoints2 = new int[numPoints2];
        	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
        	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
        	                                xpoints2[j] = (int) p3.getX();
        	                                ypoints2[j] = (int) p3.getY();}
                                
        	                            targetGraphic.setColor(Color.WHITE);
        	                            targetGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);}
        	                    }
        	                }
        	                for (IFeature build : build25s) {
        	                    IMultiSurface<IPolygon> geom = (IMultiSurface)( build.getGeom());
        	                    for (IPolygon elem : geom){
        	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
        	                    	int numPoints1 = viewDirectPositionList1.size();
        	                        int[] xpoints1 = new int[numPoints1];
        	                        int[] ypoints1 = new int[numPoints1];
        	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                            xpoints1[i] = (int) p3.getX();
        	                            ypoints1[i] = (int) p3.getY();}
        	                        buildGraphic.setColor(Color.WHITE);
        	                        buildGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                        for (int i=0; i<elem.sizeInterior();i++){ 
        	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
        	                        	int numPoints2 = viewDirectPositionList2.size();
        	                            int[] xpoints2 = new int[numPoints2];
        	                            int[] ypoints2 = new int[numPoints2];
        	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
        	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
        	                                xpoints2[j] = (int) p3.getX();
        	                                ypoints2[j] = (int) p3.getY();
        	                            }
        	                            buildGraphic.setColor(Color.BLACK);
        	                            buildGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);
        	                        }
        	                    }
        	                }
        	                for (IFeature water : water50s) {
          	                    IMultiSurface<IPolygon> geom = (IMultiSurface)(  water.getGeom());
        	                    for (IPolygon elem : geom){
        	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
        	                    	int numPoints1 = viewDirectPositionList1.size();
        	                        int[] xpoints1 = new int[numPoints1];
        	                        int[] ypoints1 = new int[numPoints1];
        	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                            xpoints1[i] = (int) p3.getX();
        	                            ypoints1[i] = (int) p3.getY();}
        	                        targetGraphic.setColor(water_color);
        	                        targetGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                        for (int i=0; i<elem.sizeInterior();i++){ 
        	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
        	                        	int numPoints2 = viewDirectPositionList2.size();
        	                            int[] xpoints2 = new int[numPoints2];
        	                            int[] ypoints2 = new int[numPoints2];
        	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
        	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
        	                                xpoints2[j] = (int) p3.getX();
        	                                ypoints2[j] = (int) p3.getY();}
                                
        	                            targetGraphic.setColor(Color.WHITE);
        	                            targetGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);
        	                     }
        	                    }
        	                 }
        	                 for (IFeature water : water25s) {
           	                    IMultiSurface<IPolygon> geom = (IMultiSurface)(  water.getGeom());
         	                    for (IPolygon elem : geom){
         	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
         	                    	int numPoints1 = viewDirectPositionList1.size();
         	                        int[] xpoints1 = new int[numPoints1];
         	                        int[] ypoints1 = new int[numPoints1];
         	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
         	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
         	                            xpoints1[i] = (int) p3.getX();
         	                            ypoints1[i] = (int) p3.getY();}
         	                        waterGraphic.setColor(Color.WHITE);
         	                        waterGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
         	                        for (int i=0; i<elem.sizeInterior();i++){ 
         	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
         	                        	int numPoints2 = viewDirectPositionList2.size();
         	                            int[] xpoints2 = new int[numPoints2];
         	                            int[] ypoints2 = new int[numPoints2];
         	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
         	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
         	                                xpoints2[j] = (int) p3.getX();
         	                                ypoints2[j] = (int) p3.getY();}
                                 
         	                            waterGraphic.setColor(Color.BLACK);
         	                            waterGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);
         	                        }
         	                    }
        	                }
        	                for (IFeature route : road50s) {

        	                    IGeometry geom =  route.getGeom();
        	                    IDirectPositionList viewDirectPositionList1 = transform.transform(geom.coord());
        	                    int numPoints1 = viewDirectPositionList1.size();
        	                    int[] xpoints1 = new int[numPoints1];
        	                    int[] ypoints1 = new int[numPoints1];
        	                    for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                        IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                        xpoints1[i] = (int) p3.getX();
        	                        ypoints1[i] = (int) p3.getY();
        	                    }
        	                    
        	                    targetGraphic.setColor(Color.BLACK);
        	                    targetGraphic.setStroke(new BasicStroke(5,0,2));
        	                    targetGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                    targetGraphic.setColor(road_color);
        	                    targetGraphic.setStroke(new BasicStroke(3,0,2));
        	                    targetGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                }
        	                for (IFeature route : road25s) {

        	                	IGeometry geom =  route.getGeom();
        	                	IDirectPositionList viewDirectPositionList1 = transform.transform(geom.coord());
        	                	int numPoints1 = viewDirectPositionList1.size();
        	                	int[] xpoints1 = new int[numPoints1];
        	                	int[] ypoints1 = new int[numPoints1];
        	                	for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                		IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                		xpoints1[i] = (int) p3.getX();
        	                		ypoints1[i] = (int) p3.getY();
        	                	}

                            
        	                	roadGraphic.setColor(Color.WHITE);
        	                	roadGraphic.setStroke(new BasicStroke(3,0,2));
        	                	roadGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                }
        	                for (IFeature gray_item : gray50) {
        	                	IGeometry geom =  gray_item.getGeom();
        	                	IDirectPositionList viewDirectPositionList1 = transform.transform(geom.coord());
        	                	int numPoints1 = viewDirectPositionList1.size();
        	                	int[] xpoints1 = new int[numPoints1];
        	                	int[] ypoints1 = new int[numPoints1];
        	                	for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                		IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                		xpoints1[i] = (int) p3.getX();
        	                		ypoints1[i] = (int) p3.getY();
        	                	}
        	                	targetGraphic.setColor(graying_color);
        	                	targetGraphic.setStroke(new BasicStroke(1));
        	                	targetGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                }


        	                File outputfile1 = new File(outputPath +"//build//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(buildImg, "png", outputfile1);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }
        	    			File outputfile2 = new File(outputPath +"//road//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(roadImg, "png", outputfile2);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }
        	    			File outputfile3 = new File(outputPath +"//water//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(waterImg, "png", outputfile3);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }

        	                File outputfileTarget = new File(outputPath +"//target//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(targetImg, "png", outputfileTarget);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }

        	    			nb+=1;
        	    			p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	    			p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);

        	    		}
        	    		p1.setCoordinate(p1.getX()+pas_extern, p1.getY());
        	    		p2.setCoordinate(p2.getX()+pas_extern, p2.getY());
        	    	}

                }
            });
            th.start();
        }

        public FromLayerAction() {
            super();
            this.putValue(Action.NAME, "From theme tiles");
        }


        
        
    }
    

    /**
     * This action construct symbolised tiles from data at 1:25 000 and at 1:50 000. 
     * 
     * 
     * @author ACourtial
     *
     */
    private class FromSymbolAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private String build25Name="build25";
        private String build50Name="build50";
        private String road25Name="road25";
        private String road50Name="road50";
        private String water25Name="water25";
        private String water50Name="water50";
        private String gray50Name="gray50";
        private String outputPath="D://urbandataset//fromsymbol";
	    private Color building_color = new Color(137,110,90);
	    private Color graying_color = new Color(230, 230, 210);
	    private Color road_color = new Color(250, 250, 200);
	    private Color water_color = new Color(170, 210, 230);
    	private GeOxygeneApplication application;
    	private int imageSize=512; private float scale=500; private int recov=60;
    	
  	
        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                	application = CartAGenPlugin.getInstance().getApplication();
        	 		Layer build25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(build25Name);
        			Layer build50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(build50Name);
        			Layer road25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(road25Name);
        			Layer road50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(road50Name);
        			Layer water25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(water25Name);
        			Layer water50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(water50Name);
        			Layer gray50Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(gray50Name);
        			
        			IFeatureCollection<? extends IFeature> build25 = build25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> build50 = build50Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> road25 = road25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> road50 = road50Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> water25 = water25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> water50 = water50Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> gray50 = gray50Layer.getFeatureCollection();
        			
        			IEnvelope carte=road25.envelope(); IDirectPosition p1=carte.getLowerCorner(); IDirectPosition p2=carte.getUpperCorner();
        	    	double xmin=carte.minX(); double ymin=carte.minY(); double xmax=carte.maxX(); double ymax=carte.maxY();
        	    	IEnvelope fenetre= (IEnvelope) carte.clone();
        	    	
        	    	double pas_intern=scale;
        	    	double pas_extern=scale*recov/100;
        	    	int nb=0;
        	    	p2.setCoordinate(xmin+pas_intern, ymin+(pas_intern)); 
        	    	while (p1.getX()<xmax){
        	    		p1.setCoordinate(p1.getX(), ymin);
        	    		p2.setCoordinate(p2.getX(), ymin+(pas_intern));
        	    		while(p1.getY()<ymax){
        	    		    fenetre.setLowerCorner(p1);
        	    		    fenetre.setUpperCorner(p2);
        	                Collection<IFeature> build50s =(Collection<IFeature>)  build50.select((IEnvelope) fenetre);
        	                Collection<IFeature> road50s =(Collection<IFeature>)  road50.select((IEnvelope) fenetre);
        	                Collection<IFeature> water50s =(Collection<IFeature>)  water50.select((IEnvelope) fenetre);
        	                Collection<IFeature> gray50s =(Collection<IFeature>)  gray50.select((IEnvelope) fenetre);
        	                Collection<IFeature> build25s =(Collection<IFeature>)  build25.select((IEnvelope) fenetre);
        	                Collection<IFeature> road25s =(Collection<IFeature>)  road25.select((IEnvelope) fenetre);
        	                Collection<IFeature> water25s =(Collection<IFeature>)  water25.select((IEnvelope) fenetre);

    	                	
        	                if(build25s.isEmpty()) {
        	                	p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	                	p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);
        	                	continue;
        	                }

        	                
        	    		    BufferedImage inputImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_INT_RGB);
        	    		    BufferedImage targetImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_INT_RGB);
        	    		    

        	    		    Graphics2D inputGraphic= inputImg.createGraphics();
        	    		    inputGraphic.setBackground(new Color(255, 255, 255));
        	    		    inputGraphic.clearRect(0, 0, imageSize, imageSize);
        	    		    
        	    		    Graphics2D targetGraphic= targetImg.createGraphics();
        	    		    targetGraphic.setBackground(new Color(255, 255, 255));
        	    		    targetGraphic.clearRect(0, 0, imageSize, imageSize);

        	                
        	    		    IEnvelope env = fenetre.getGeom().getEnvelope();
        	    	        double xMin = env.minX();
        	    	        double yMin = env.minY();
        	    	        double ratio = 0.0;

        	    	        ratio = (imageSize - 2 * 3) / env.length();

        	    	        CoordinateTransformation transform= new CoordinateTransformation(xMin, yMin, ratio, 3, imageSize);

        	    	        for (IFeature build : build50s) {
        	                    IMultiSurface<IPolygon> geom = (IMultiSurface)( build.getGeom());
        	                    for (IPolygon elem : geom){
        	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
        	                    	int numPoints1 = viewDirectPositionList1.size();
        	                        int[] xpoints1 = new int[numPoints1];
        	                        int[] ypoints1 = new int[numPoints1];
        	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                            xpoints1[i] = (int) p3.getX();
        	                            ypoints1[i] = (int) p3.getY();}
        	                        targetGraphic.setColor(building_color);
        	                        targetGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                        for (int i=0; i<elem.sizeInterior();i++){ 
        	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
        	                        	int numPoints2 = viewDirectPositionList2.size();
        	                            int[] xpoints2 = new int[numPoints2];
        	                            int[] ypoints2 = new int[numPoints2];
        	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
        	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
        	                                xpoints2[j] = (int) p3.getX();
        	                                ypoints2[j] = (int) p3.getY();}
                                
        	                            targetGraphic.setColor(Color.WHITE);
        	                            targetGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);}
        	                    }
        	                }
        	                for (IFeature build : build25s) {
        	                    IMultiSurface<IPolygon> geom = (IMultiSurface)( build.getGeom());
        	                    for (IPolygon elem : geom){
        	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
        	                    	int numPoints1 = viewDirectPositionList1.size();
        	                        int[] xpoints1 = new int[numPoints1];
        	                        int[] ypoints1 = new int[numPoints1];
        	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                            xpoints1[i] = (int) p3.getX();
        	                            ypoints1[i] = (int) p3.getY();}
        	                        inputGraphic.setColor(building_color);
        	                        inputGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                        for (int i=0; i<elem.sizeInterior();i++){ 
        	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
        	                        	int numPoints2 = viewDirectPositionList2.size();
        	                            int[] xpoints2 = new int[numPoints2];
        	                            int[] ypoints2 = new int[numPoints2];
        	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
        	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
        	                                xpoints2[j] = (int) p3.getX();
        	                                ypoints2[j] = (int) p3.getY();
        	                            }
        	                            inputGraphic.setColor(Color.WHITE);
        	                            inputGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);
        	                        }
        	                    }
        	                }
        	                for (IFeature water : water50s) {
          	                    IMultiSurface<IPolygon> geom = (IMultiSurface)(  water.getGeom());
        	                    for (IPolygon elem : geom){
        	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
        	                    	int numPoints1 = viewDirectPositionList1.size();
        	                        int[] xpoints1 = new int[numPoints1];
        	                        int[] ypoints1 = new int[numPoints1];
        	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                            xpoints1[i] = (int) p3.getX();
        	                            ypoints1[i] = (int) p3.getY();}
        	                        targetGraphic.setColor(water_color);
        	                        targetGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                        for (int i=0; i<elem.sizeInterior();i++){ 
        	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
        	                        	int numPoints2 = viewDirectPositionList2.size();
        	                            int[] xpoints2 = new int[numPoints2];
        	                            int[] ypoints2 = new int[numPoints2];
        	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
        	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
        	                                xpoints2[j] = (int) p3.getX();
        	                                ypoints2[j] = (int) p3.getY();}
                                
        	                            targetGraphic.setColor(Color.WHITE);
        	                            targetGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);
        	                     }
        	                    }
        	                 }
        	                 for (IFeature water : water25s) {
           	                    IMultiSurface<IPolygon> geom = (IMultiSurface)(  water.getGeom());
         	                    for (IPolygon elem : geom){
         	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
         	                    	int numPoints1 = viewDirectPositionList1.size();
         	                        int[] xpoints1 = new int[numPoints1];
         	                        int[] ypoints1 = new int[numPoints1];
         	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
         	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
         	                            xpoints1[i] = (int) p3.getX();
         	                            ypoints1[i] = (int) p3.getY();}
         	                        inputGraphic.setColor(water_color);
         	                        inputGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
         	                        for (int i=0; i<elem.sizeInterior();i++){ 
         	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
         	                        	int numPoints2 = viewDirectPositionList2.size();
         	                            int[] xpoints2 = new int[numPoints2];
         	                            int[] ypoints2 = new int[numPoints2];
         	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
         	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
         	                                xpoints2[j] = (int) p3.getX();
         	                                ypoints2[j] = (int) p3.getY();}
                                 
         	                            inputGraphic.setColor(Color.WHITE);
         	                            inputGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);
         	                        }
         	                    }
        	                }
        	                for (IFeature route : road50s) {

        	                    IGeometry geom =  route.getGeom();
        	                    IDirectPositionList viewDirectPositionList1 = transform.transform(geom.coord());
        	                    int numPoints1 = viewDirectPositionList1.size();
        	                    int[] xpoints1 = new int[numPoints1];
        	                    int[] ypoints1 = new int[numPoints1];
        	                    for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                        IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                        xpoints1[i] = (int) p3.getX();
        	                        ypoints1[i] = (int) p3.getY();
        	                    }
        	                    
        	                    targetGraphic.setColor(Color.BLACK);
        	                    targetGraphic.setStroke(new BasicStroke(5,0,2));
        	                    targetGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                    targetGraphic.setColor(road_color);
        	                    targetGraphic.setStroke(new BasicStroke(3,0,2));
        	                    targetGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                }
        	                for (IFeature route : road25s) {

        	                	IGeometry geom =  route.getGeom();
        	                	IDirectPositionList viewDirectPositionList1 = transform.transform(geom.coord());
        	                	int numPoints1 = viewDirectPositionList1.size();
        	                	int[] xpoints1 = new int[numPoints1];
        	                	int[] ypoints1 = new int[numPoints1];
        	                	for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                		IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                		xpoints1[i] = (int) p3.getX();
        	                		ypoints1[i] = (int) p3.getY();
        	                	}

                            
        	                    inputGraphic.setColor(Color.BLACK);
        	                    inputGraphic.setStroke(new BasicStroke(5,0,2));
        	                    inputGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                    inputGraphic.setColor(road_color);
        	                    inputGraphic.setStroke(new BasicStroke(3,0,2));
        	                    inputGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                }
        	                for (IFeature gray_item : gray50) {
        	                	IGeometry geom =  gray_item.getGeom();
        	                	IDirectPositionList viewDirectPositionList1 = transform.transform(geom.coord());
        	                	int numPoints1 = viewDirectPositionList1.size();
        	                	int[] xpoints1 = new int[numPoints1];
        	                	int[] ypoints1 = new int[numPoints1];
        	                	for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                		IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                		xpoints1[i] = (int) p3.getX();
        	                		ypoints1[i] = (int) p3.getY();
        	                	}
        	                	targetGraphic.setColor(graying_color);
        	                	targetGraphic.setStroke(new BasicStroke(1));
        	                	targetGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                }


        	                File outputfile1 = new File(outputPath +"//input//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(inputImg, "png", outputfile1);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }
        	    		

        	                File outputfileTarget = new File(outputPath +"//target//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(targetImg, "png", outputfileTarget);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }

        	    			nb+=1;
        	    			p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	    			p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);

        	    		}
        	    		p1.setCoordinate(p1.getX()+pas_extern, p1.getY());
        	    		p2.setCoordinate(p2.getX()+pas_extern, p2.getY());
        	    	}

                }

            });
            th.start();
        }

        public FromSymbolAction() {
            super();
            this.putValue(Action.NAME, "From symbolised tiles");
        }

    }
    

    /**
     * This action gets all road features and generates a gray image,
     * that represents the probability to be selected after generalisation,
     * within the build up area, using a sliding windows. 
     * 
     * 
     * @author ACourtial
     *
     */
    private class RoadImagesAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private String build25Name="build25";
        private String road25Name="road25";
        private String outputPath="D://urbandataset//road_enrichment";
    	private GeOxygeneApplication application;
    	private int imageSize=512; private float scale=500; private int recov=60;
    	
  	
        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                	application = CartAGenPlugin.getInstance().getApplication();
        	 		Layer build25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(build25Name);
        			Layer road25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(road25Name);

        			IFeatureCollection<? extends IFeature> build25 = build25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> road25 = road25Layer.getFeatureCollection();

        			IEnvelope carte=road25.envelope(); IDirectPosition p1=carte.getLowerCorner(); IDirectPosition p2=carte.getUpperCorner();
        	    	double xmin=carte.minX(); double ymin=carte.minY(); double xmax=carte.maxX(); double ymax=carte.maxY();
        	    	IEnvelope fenetre= (IEnvelope) carte.clone();
        	    	
        	    	double pas_intern=scale;
        	    	double pas_extern=scale*recov/100;
        	    	int nb=0;
        	    	p2.setCoordinate(xmin+pas_intern, ymin+(pas_intern)); 
        	    	while (p1.getX()<xmax){
        	    		p1.setCoordinate(p1.getX(), ymin);
        	    		p2.setCoordinate(p2.getX(), ymin+(pas_intern));
        	    		while(p1.getY()<ymax){
        	    		    fenetre.setLowerCorner(p1);
        	    		    fenetre.setUpperCorner(p2);
        	                Collection<IFeature> build25s =(Collection<IFeature>)  build25.select((IEnvelope) fenetre);
        	                Collection<IFeature> road25s =(Collection<IFeature>)  road25.select((IEnvelope) fenetre);

    	                	
        	                if(build25s.isEmpty()) {
        	                	p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	                	p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);
        	                	continue;
        	                }

        	                
        	    		    BufferedImage targetImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_INT_RGB);
        	    		    
        	    		    
        	    		    Graphics2D targetGraphic= targetImg.createGraphics();
        	    		    targetGraphic.setBackground(new Color(0, 0, 0));
        	    		    targetGraphic.clearRect(0, 0, imageSize, imageSize);

        	                
        	    		    IEnvelope env = fenetre.getGeom().getEnvelope();
        	    	        double xMin = env.minX();
        	    	        double yMin = env.minY();
        	    	        double ratio = 0.0;

        	    	        ratio = (imageSize - 2 * 3) / env.length();

        	    	        CoordinateTransformation transform= new CoordinateTransformation(xMin, yMin, ratio, 3, imageSize);

        	    	        

        	                for (IFeature route : road25s) {

        	                	IGeometry geom =  route.getGeom();
        	                	IDirectPositionList viewDirectPositionList1 = transform.transform(geom.coord());
        	                	int numPoints1 = viewDirectPositionList1.size();
        	                	int[] xpoints1 = new int[numPoints1];
        	                	int[] ypoints1 = new int[numPoints1];
        	                	for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                		IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                		xpoints1[i] = (int) p3.getX();
        	                		ypoints1[i] = (int) p3.getY();
        	                	}

        	                	int x=(int)(((Float.parseFloat(route.getAttribute("p").toString())/100))*255);
        	                	System.out.println(x);
        	                	targetGraphic.setColor(new Color(x,x,x));
        	                	targetGraphic.setStroke(new BasicStroke(3,0,2));
        	                	targetGraphic.drawPolyline(xpoints1, ypoints1, numPoints1);
        	                }
        	                


        	                File outputfile1 = new File(outputPath +"//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(targetImg, "png", outputfile1);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }
        	    
        	    			nb+=1;
        	    			p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	    			p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);

        	    		}
        	    		p1.setCoordinate(p1.getX()+pas_extern, p1.getY());
        	    		p2.setCoordinate(p2.getX()+pas_extern, p2.getY());
        	    	}

                }
            });
            th.start();
        }

        public RoadImagesAction() {
            super();
            this.putValue(Action.NAME, "Generate images of road information");
        }

    }
    
    /**
     * This action gets all alignement features and generates masks of aligned areas,
     * within the build up area, using a sliding windows. 
     * 
     * @author ACourtial
     *
     */
    private class AliImagesAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private String build25Name="build25";
        private String ali25Name="ali25";
        private String road25Name="road25";
        private String outputPath="D://urbandataset//alignement_enrichment";
    	private GeOxygeneApplication application;
    	private int imageSize=512; private float scale=500; private int recov=60;
    	
  	
        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                	application = CartAGenPlugin.getInstance().getApplication();
        	 		Layer build25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(build25Name);
        			Layer ali25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(ali25Name);
        			Layer road25Layer = application.getMainFrame().getSelectedProjectFrame().getLayer(road25Name);
        			
        			IFeatureCollection<? extends IFeature> build25 = build25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> ali25 = ali25Layer.getFeatureCollection();
        			IFeatureCollection<? extends IFeature> road25 = road25Layer.getFeatureCollection();
        			
        			IEnvelope carte=road25.envelope(); IDirectPosition p1=carte.getLowerCorner(); IDirectPosition p2=carte.getUpperCorner();
        	    	double xmin=carte.minX(); double ymin=carte.minY(); double xmax=carte.maxX(); double ymax=carte.maxY();
        	    	IEnvelope fenetre= (IEnvelope) carte.clone();
        	    	
        	    	double pas_intern=scale;
        	    	double pas_extern=scale*recov/100;
        	    	int nb=0;
        	    	p2.setCoordinate(xmin+pas_intern, ymin+(pas_intern)); 
        	    	while (p1.getX()<xmax){
        	    		p1.setCoordinate(p1.getX(), ymin);
        	    		p2.setCoordinate(p2.getX(), ymin+(pas_intern));
        	    		while(p1.getY()<ymax){
        	    		    fenetre.setLowerCorner(p1);
        	    		    fenetre.setUpperCorner(p2);
        	                Collection<IFeature> build25s =(Collection<IFeature>)  build25.select((IEnvelope) fenetre);
        	                Collection<IFeature> ali25s =(Collection<IFeature>)  ali25.select((IEnvelope) fenetre);
   	                	
        	                if(build25s.isEmpty()) {
        	                	p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	                	p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);
        	                	continue;
        	                }
        	                
        	    		    BufferedImage buildImg = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_BYTE_GRAY);
        	    		    
        	    		    Graphics2D buildGraphic= buildImg.createGraphics();
        	    		    buildGraphic.setBackground(new Color(0, 0, 0));
        	    		    buildGraphic.clearRect(0, 0, imageSize, imageSize);
        	                        	                
        	    		    IEnvelope env = fenetre.getGeom().getEnvelope();
        	    	        double xMin = env.minX();
        	    	        double yMin = env.minY();
        	    	        double ratio = 0.0;

        	    	        ratio = (imageSize - 2 * 3) / env.length();

        	    	        CoordinateTransformation transform= new CoordinateTransformation(xMin, yMin, ratio, 3, imageSize);


        	                for (IFeature ali : ali25s) {
        	                    IMultiSurface<IPolygon> geom = (IMultiSurface)( ali.getGeom());
        	                    for (IPolygon elem : geom){
        	                    	IDirectPositionList viewDirectPositionList1 = transform.transform(elem.getExterior().coord());
        	                    	int numPoints1 = viewDirectPositionList1.size();
        	                        int[] xpoints1 = new int[numPoints1];
        	                        int[] ypoints1 = new int[numPoints1];
        	                        for (int i = 0; i < viewDirectPositionList1.size(); i++) {
        	                            IDirectPosition p3 = viewDirectPositionList1.get(i);
        	                            xpoints1[i] = (int) p3.getX();
        	                            ypoints1[i] = (int) p3.getY();}
        	                        buildGraphic.setColor(Color.WHITE);
        	                        buildGraphic.fillPolygon(xpoints1, ypoints1, numPoints1);
        	                        for (int i=0; i<elem.sizeInterior();i++){ 
        	                        	IDirectPositionList viewDirectPositionList2 = transform.transform(elem.getInterior(i).coord());
        	                        	int numPoints2 = viewDirectPositionList2.size();
        	                            int[] xpoints2 = new int[numPoints2];
        	                            int[] ypoints2 = new int[numPoints2];
        	                            for (int j = 0; j < viewDirectPositionList2.size(); j++) {
        	                                IDirectPosition p3 = viewDirectPositionList2.get(j);
        	                                xpoints2[j] = (int) p3.getX();
        	                                ypoints2[j] = (int) p3.getY();
        	                            }
        	                            buildGraphic.setColor(Color.BLACK);
        	                            buildGraphic.fillPolygon(xpoints2, ypoints2, numPoints2);
        	                        }
        	                    }
        	                }
        	                

        	                File outputfile1 = new File(outputPath +"//image"+ nb + ".png");
        	                
        	                try {
        	                	ImageIO.write(buildImg, "png", outputfile1);
        	                } catch (IOException f) {
        	                	f.printStackTrace();
        	                }
        	    			

        	    			nb+=1;
        	    			p1.setCoordinate(p1.getX(), p1.getY()+pas_extern);
        	    			p2.setCoordinate(p2.getX(), p2.getY()+pas_extern);

        	    		}
        	    		p1.setCoordinate(p1.getX()+pas_extern, p1.getY());
        	    		p2.setCoordinate(p2.getX()+pas_extern, p2.getY());
        	    	}

                }
            });
            th.start();
        }

        public AliImagesAction() {
            super();
            this.putValue(Action.NAME, "Generate images of alignments areas");
        }

    }

    /**
     * This action gets all urban block features and generates an image per
     * block, centered on the block. Over an area threshold, the blocks are
     * shrinked by homethety to fit in the image.
     * 
     * @author GTouya
     *
     */
    private class BlockImagesAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    // get the current dataset
                    CartAGenDataSet dataset = CartAGenDoc.getInstance()
                            .getCurrentDataset();

                    // then loop on the buildings to create example images from
                    // each
                    for (IUrbanBlock block : dataset.getBlocks()) {
                        // get the geometry of the block
                        IPolygon polygon = (IPolygon) block.getGeom();

                        // get the transform for the geographic coordinates to
                        // the image
                        // coordinates
                        CoordinateTransformation transform = getCoordTransfoFromPolygon(
                                polygon, 5, IMAGE_SIZE, 0.0);

                        // Generate a blank RGB image
                        BufferedImage bi = new BufferedImage(IMAGE_SIZE,
                                IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = bi.createGraphics();
                        g2d.setBackground(Color.LIGHT_GRAY);
                        g2d.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

                        IDirectPositionList viewDPList = transform
                                .transform(block.getGeom().coord());
                        Shape shapeBlock = toPolygonShape(viewDPList);
                        // compute symbol width in pixels
                        if (shapeBlock != null) {
                            g2d.setColor(Color.DARK_GRAY);
                            g2d.setStroke(new BasicStroke(2));
                            g2d.draw(shapeBlock);
                        }

                        /*
                         * // add the roads casing shape in the image for
                         * (INetworkSection road :
                         * block.getSurroundingNetwork()) { IDirectPositionList
                         * viewDirectPositionList = transform
                         * .transform(road.getGeom().coord()); Shape shape =
                         * toPolylineShape(viewDirectPositionList); // compute
                         * symbol casing width in pixels double casingWidth =
                         * SLDUtilCartagen.getSymbolMaxWidth(road); float
                         * pixelCasingWidth =
                         * transform.getPixelWidth(casingWidth); //
                         * System.out.println(viewDirectPositionList); //
                         * System.out.println(pixelCasingWidth); // draw casing
                         * if (shape != null) { g2d.setColor(Color.DARK_GRAY);
                         * g2d.setStroke(new BasicStroke(2));
                         * g2d.setPaint(Color.DARK_GRAY); g2d.draw(shape); } }
                         * 
                         * // add the roads inner shape in the image for
                         * (INetworkSection road :
                         * block.getSurroundingNetwork()) { IDirectPositionList
                         * viewDirectPositionList = transform
                         * .transform(road.getGeom().coord()); Shape shape =
                         * toPolylineShape(viewDirectPositionList); // compute
                         * symbol inner width in pixels double innerWidth =
                         * SLDUtilCartagen.getSymbolInnerWidth(road); float
                         * pixelInnerWidth =
                         * transform.getPixelWidth(innerWidth); // draw inner
                         * symbol if (shape != null) {
                         * g2d.setColor(Color.LIGHT_GRAY.brighter());
                         * g2d.setStroke(new BasicStroke(1));
                         * g2d.setPaint(Color.LIGHT_GRAY.brighter());
                         * g2d.draw(shape); } }
                         */

                        // add the buildings shape in the image
                        for (IUrbanElement building : block
                                .getUrbanElements()) {
                            IDirectPositionList viewDirectPositionList = transform
                                    .transform(building.getGeom().coord());
                            Shape shape = toPolygonShape(
                                    viewDirectPositionList);
                            // compute symbol width in pixels
                            if (shape != null) {
                                g2d.setColor(Color.DARK_GRAY);
                                g2d.setStroke(new BasicStroke());
                                g2d.fill(shape);
                            }
                        }

                        File outputfile2 = new File("D://tensorflow//block_"
                                + block.getId() + ".png");

                        try {
                            ImageIO.write(bi, "png", outputfile2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            th.start();
        }

        public BlockImagesAction() {
            super();
            this.putValue(Action.NAME, "Generate images of urban blocks");
        }

    }

    /**
     * 
     * @param polygon
     *            the polygon to draw in the image
     * @param gap
     *            the pixel margin left between the geographic feature and the
     *            border of the image.
     * @param imageSize
     *            the size of the image
     * @param maxArea
     *            the maximum area over which the polygon is shrinked
     * @return
     */
    private CoordinateTransformation getCoordTransfoFromPolygon(
            IPolygon polygon, int gap, int imageSize, double maxArea) {
        double enlargementRatio = polygon.area() / maxArea;

        // xMin is 10, xMax is imageSize-10, same for y coordinate.
        // there is a need for a translation and a homothetic transformation
        IEnvelope env = polygon.getEnvelope();
        double xMin = env.minX();
        double yMin = env.minY();
        double ratio = 0.0;
        if (env.width() > env.length()) {
            ratio = (imageSize - 2 * gap) / env.width();
        } else
            ratio = (imageSize - 2 * gap) / env.length();

        if (ratio > 1.0)
            ratio = 1.0;

        if (enlargementRatio < 1.0)
            ratio = ratio * enlargementRatio;

        return new CoordinateTransformation(xMin, yMin, ratio, gap, imageSize);
    }

    class CoordinateTransformation {

        private double xMin, yMin;
        private double homotheticRatio;
        private int gap;
        private int imageSize;

        CoordinateTransformation(double xMin, double yMin,
                double homotheticRatio, int gap, int imageSize) {
            this.gap = gap;
            this.homotheticRatio = homotheticRatio;
            this.xMin = xMin;
            this.yMin = yMin;
            this.imageSize = imageSize;
        }

        public float getPixelWidth(double groundWidth) {
            return (float) (groundWidth * homotheticRatio);
        }

        IDirectPositionList transform(IDirectPositionList coordinates) {
            IDirectPositionList newCoordinates = new DirectPositionList();
            for (IDirectPosition dp : coordinates) {
                double x = (dp.getX() - xMin) * homotheticRatio + gap;
                double y = (dp.getY() - yMin) * homotheticRatio + gap;
                if (y < imageSize / 2)
                    y = y + 2 * (imageSize / 2 - y);
                else
                    y = y - 2 * (y - imageSize / 2);
                newCoordinates.add(new DirectPosition(x, y));
            }
            return newCoordinates;
        }
    }

    /**
     * Transform a direct position list in view coordinates to an awt shape.
     * 
     * @param viewDirectPositionList
     *            a direct position list in view coordinates
     * @return A shape representing the polygon in view coordinates
     */
    private Shape toPolygonShape(
            final IDirectPositionList viewDirectPositionList) {
        int numPoints = viewDirectPositionList.size();
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];
        for (int i = 0; i < viewDirectPositionList.size(); i++) {
            IDirectPosition p = viewDirectPositionList.get(i);
            xpoints[i] = (int) p.getX();
            ypoints[i] = (int) p.getY();
        }
        return new Polygon(xpoints, ypoints, numPoints);
    }

    /**
     * Transform a direct position list in view coordinates to an awt shape.
     * 
     * @param viewDirectPositionList
     *            a direct position list in view coordinates
     * @return A shape representing the polygon in view coordinates
     */
    private Shape toPolylineShape(
            final IDirectPositionList viewDirectPositionList) {
        Path2D.Double path = new Path2D.Double();
        for (int i = 0; i < viewDirectPositionList.size(); i++) {
            IDirectPosition p = viewDirectPositionList.get(i);
            path.moveTo(p.getX(), p.getY());
        }
        path.closePath();
        return path;
    }

    /**
     * 
     * @author GTouya
     *
     */
    private class RoadPiX2PixAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    GeOxygeneApplication application = CartAGenPlugin
                            .getInstance().getApplication();

                    Layer layer_alpes_250k = application.getMainFrame()
                            .getSelectedProjectFrame()
                            .getLayer("routes_250k_alpes");
                    Layer layer_Bduni = application.getMainFrame()
                            .getSelectedProjectFrame()
                            .getLayer("routes_bduni_alpes");

                    Roads2Image rasterizer = new Roads2Image(
                            layer_Bduni.getFeatureCollection(),
                            layer_alpes_250k.getFeatureCollection());

                    List<BufferedImage> images = rasterizer
                            .createStickedBufferedImages(256, 5);
                    int i = 0;
                    for (BufferedImage bi : images) {

                        File outputfile2 = new File(
                                "D://deep_datasets//roads//road_" + i + ".png");
                        i++;
                        try {
                            ImageIO.write(bi, "png", outputfile2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            th.start();
        }

        public RoadPiX2PixAction() {
            super();
            this.putValue(Action.NAME, "Generate images of roads for Pix2Pix");
        }

    }

}
