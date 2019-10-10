package fr.ign.cogit.cartagen.core.dataset.json;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.json.JSONToLayerMapping.JSONToLayerMatching;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;

public class JSONLoader {

    private String layer;
    private JSONToLayerMapping mapping;
    private JSONToLayerMatching matching;
    private Method method;
    private IGeneObj element;
    private String elementClass;
    private GeometryType geometryType;
    private AbstractCreationFactory factory;
    private CartAGenDataSet dataset;

    public boolean genericJSONLoader(String path, CartAGenDataSet dataset,
            AbstractCreationFactory factory, JSONToLayerMapping mapping,
            boolean createGeoClass) throws NoSuchFieldException,
            SecurityException, InvocationTargetException {

        this.mapping = mapping;
        this.factory = factory;
        this.dataset = dataset;

        /*
         * FeatureJSON io = new FeatureJSON(); FeatureIterator<SimpleFeature>
         * features;
         * 
         * try { InputStream stream = new FileInputStream(path);
         * FeatureCollection<FeatureType, SimpleFeature> fc = io
         * .readFeatureCollection(stream); String layer = fc.toString();
         * System.out.println(layer);
         * System.out.println(fc.getSchema().getName());
         * System.out.println(fc.getID()); features =
         * io.streamFeatureCollection(stream); while (features.hasNext()) {
         * SimpleFeature feature = features.next(); // String layer =
         * feature.getFeatureType().getTypeName(); JSONToLayerMatching matching
         * = mapping .getMatchingFromName(layer); Method method =
         * matching.getCreationMethod(); IGeneObj element = (IGeneObj)
         * method.invoke(factory); // set the geometry IGeometry geom =
         * AdapterFactory .toGM_Object((Geometry) feature.getDefaultGeometry());
         * if (geom == null) continue; element.setGeom(geom);
         * 
         * // set the attributes for (String attrJava :
         * matching.getListAttr().keySet()) { String attrJSON =
         * mapping.getJSONAttribute(layer, attrJava); Object value = null; value
         * = feature.getAttribute(attrJSON); element.setAttribute(attrJava,
         * value); } } } catch (Exception e) { e.printStackTrace(); }
         */
        try {
            JsonReader reader = new JsonReader(new FileReader(path));
            reader.setLenient(true);

            reader.beginObject();
            while (reader.hasNext()) {
                JsonToken token = reader.peek();
                if (token.equals(JsonToken.BEGIN_ARRAY))
                    handleArray(reader);
                else if (token.equals(JsonToken.END_OBJECT)) {
                    reader.endObject();
                    return true;
                } else
                    handleNonArrayToken(reader, token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Layer: " + layer);
        // create geoclass
        JSONGeoClass geoClass = new JSONGeoClass(layer, elementClass,
                geometryType);
        CartAGenDB database = dataset.getCartAGenDB();
        if (!database.getClasses().contains(geoClass)) {
            database.addClass(geoClass);
        }

        return true;
    }

    /**
     * Handle a json array. The first token would be JsonToken.BEGIN_ARRAY.
     * Arrays may contain objects or primitives.
     * 
     * @param reader
     * @throws IOException
     */
    public void handleArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (true) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleObject(reader);
            } else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
            } else
                handleNonArrayToken(reader, token);
        }
    }

    /**
     * Handle an Object. Consume the first token which is BEGIN_OBJECT. Within
     * the Object there could be array or non array tokens. We write handler
     * methods for both. Noe the peek() method. It is used to find out the type
     * of the next token without actually consuming it.
     * 
     * @param reader
     * @throws IOException
     */
    private void handleObject(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY))
                handleArray(reader);
            else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
                return;
            } else
                handleNonArrayToken(reader, token);
        }

    }

    /**
     * Handle non array non object tokens
     * 
     * @param reader
     * @param token
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void handleNonArrayToken(JsonReader reader, JsonToken token)
            throws IOException {
        if (token.equals(JsonToken.NAME)) {
            String name = reader.nextName();
            // System.out.println(name);
            if (name.equals("name")) {
                this.layer = reader.nextString();
                this.matching = mapping.getMatchingFromName(layer);
                this.method = matching.getCreationMethod();

            } else if (name.equals("type")) {
                if (reader.nextString().equals("Feature")) {
                    // this is a new feature, create it
                    try {
                        this.element = (IGeneObj) method.invoke(factory);
                        this.elementClass = (String) element.getClass()
                                .getField("FEAT_TYPE_NAME").get(null);
                        IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) dataset
                                .getCartagenPop(
                                        dataset.getPopNameFromObj(element),
                                        elementClass);
                        pop.add(element);
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            } else if (name.equals("properties")) {
                reader.beginObject();
                System.out.println("after properties");
                while (reader.hasNext()) {
                    String attrName = reader.nextName();
                    String javaName = matching.getJavaAttribute(attrName);
                    JsonToken nextToken = reader.peek();
                    if (nextToken.equals(JsonToken.STRING)) {
                        String value = reader.nextString();
                        element.setAttribute(javaName, value);
                    } else if (nextToken.equals(JsonToken.NUMBER)) {
                        Double value = reader.nextDouble();
                        element.setAttribute(javaName, value);
                    } else if (nextToken.equals(JsonToken.BOOLEAN)) {
                        Boolean value = reader.nextBoolean();
                        element.setAttribute(javaName, value);
                    }
                }
            } else if (name.equals("geometry")) {
                // TODO add geometry
                reader.beginObject();
                System.out.println("after geometry");
                reader.nextName();
                String geomType = reader.nextString();
                System.out.println(geomType);
                if (geomType.equals("GeometryCollection")) {
                    // do not process geometry collections for now
                    // remove feature from the collection
                    String elementClass;
                    try {
                        elementClass = (String) element.getClass()
                                .getField("FEAT_TYPE_NAME").get(null);

                        IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) dataset
                                .getCartagenPop(
                                        dataset.getPopNameFromObj(element),
                                        elementClass);
                        pop.remove(element);
                    } catch (IllegalArgumentException | IllegalAccessException
                            | NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                } else if (geomType.equals("LineString")) {
                    this.geometryType = GeometryType.LINE;
                    System.out.println("case linestring");
                    reader.nextName();
                    reader.beginArray();
                    IDirectPositionList dpl = new DirectPositionList();
                    // loop on the vertices array
                    while (true) {
                        JsonToken nextToken = reader.peek();
                        if (nextToken.equals(JsonToken.END_ARRAY)) {
                            reader.endArray();
                            ILineString geom = GeometryEngine.getFactory()
                                    .createILineString(dpl);
                            element.setGeom(geom);
                            System.out.println(element.getGeom());
                            break;
                        } else {
                            reader.beginArray();
                            // get the new position
                            double x = reader.nextDouble();
                            double y = reader.nextDouble();
                            double z = reader.nextDouble();
                            reader.endArray();
                            IDirectPosition position = new DirectPosition(x, y,
                                    z);
                            dpl.add(position);

                        }
                    }
                    System.out.println(reader.peek().toString());
                } else if (geomType.equals("Point")) {
                    this.geometryType = GeometryType.POINT;
                    // TODO
                } else if (geomType.equals("Polygon")) {
                    this.geometryType = GeometryType.POLYGON;
                    // TODO
                }
            }

        } else if (token.equals(JsonToken.STRING))
            System.out.println(reader.nextString());
        else if (token.equals(JsonToken.NUMBER))
            System.out.println(reader.nextDouble());
        else
            reader.skipValue();
    }
}
