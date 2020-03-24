/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.osm.schema;

import java.util.Collection;

import fr.ign.cogit.cartagen.core.defaultschema.network.Network;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ReliefField;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscLine;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.ICycleWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IDualCarriageWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ICemetery;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.osm.schema.aero.OsmAirportArea;
import fr.ign.cogit.cartagen.osm.schema.aero.OsmRunwayArea;
import fr.ign.cogit.cartagen.osm.schema.aero.OsmRunwayLine;
import fr.ign.cogit.cartagen.osm.schema.aero.OsmTaxiwayArea;
import fr.ign.cogit.cartagen.osm.schema.aero.OsmTaxiwayLine;
import fr.ign.cogit.cartagen.osm.schema.amenity.OsmHospital;
import fr.ign.cogit.cartagen.osm.schema.amenity.OsmSchool;
import fr.ign.cogit.cartagen.osm.schema.hydro.OsmWaterArea;
import fr.ign.cogit.cartagen.osm.schema.hydro.OsmWaterLine;
import fr.ign.cogit.cartagen.osm.schema.landuse.OsmSimpleLandUseArea;
import fr.ign.cogit.cartagen.osm.schema.nature.OsmCoastline;
import fr.ign.cogit.cartagen.osm.schema.nature.OsmReliefElementPoint;
import fr.ign.cogit.cartagen.osm.schema.nature.OsmTreePoint;
import fr.ign.cogit.cartagen.osm.schema.network.OsmNetworkFace;
import fr.ign.cogit.cartagen.osm.schema.rail.OsmCable;
import fr.ign.cogit.cartagen.osm.schema.rail.OsmRailwayLine;
import fr.ign.cogit.cartagen.osm.schema.rail.OsmRailwayNode;
import fr.ign.cogit.cartagen.osm.schema.road.OsmBranchingCrossroad;
import fr.ign.cogit.cartagen.osm.schema.road.OsmCycleWay;
import fr.ign.cogit.cartagen.osm.schema.road.OsmDualCarriageway;
import fr.ign.cogit.cartagen.osm.schema.road.OsmPathLine;
import fr.ign.cogit.cartagen.osm.schema.road.OsmRoadLine;
import fr.ign.cogit.cartagen.osm.schema.road.OsmRoadNode;
import fr.ign.cogit.cartagen.osm.schema.road.OsmRoundabout;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmBuildPoint;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmBuilding;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmCemetery;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmParkArea;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmPointOfInterest;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmSportsField;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmTown;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmUrbanBlock;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PatteOie;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeometryConversion;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;

public class OSMSchemaFactory extends AbstractCreationFactory {

    public OsmGeneObj createGeneObj(Class<?> classObj, OSMResource resource,
            Collection<OSMResource> nodes, OsmGeometryConversion convertor)
            throws Exception {
        if (IRoadLine.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createRoadLine(line, 0);
        }
        if (ICable.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createCable(line);
        }
        if (IBuilding.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly == null)
                return null;
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createBuilding(poly);
        }
        if (ISportsField.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createSportsField(poly);
        }
        if (IWaterLine.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createWaterLine(line, 0);
        }
        if (IWaterArea.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createWaterArea(poly);
        }
        if (IBuildPoint.class.isAssignableFrom(classObj)) {
            IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom(),
                    true);
            return (OsmGeneObj) this.createBuildPoint(pt);
        }
        if (IWaterArea.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createWaterArea(poly);
        }
        if (IAirportArea.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createAirportArea(poly);
        }
        if (IRunwayArea.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createRunwayArea(poly);
        }
        if (IRunwayLine.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createRunwayLine(line);
        }
        if (ITaxiwayArea.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createTaxiwayArea(poly);
        }
        if (ITaxiwayLine.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createTaxiwayLine(line, null);
        }
        if (ISimpleLandUseArea.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly == null || poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createSimpleLandUseArea(poly, 0);
        }
        if (IPointOfInterest.class.isAssignableFrom(classObj)) {
            IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom(),
                    true);
            return new OsmPointOfInterest(pt);
        }
        if (ITreePoint.class.isAssignableFrom(classObj)) {
            IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom(),
                    true);
            return (OsmGeneObj) this.createTreePoint(pt);
        }
        if (ICycleWay.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            return (OsmGeneObj) this.createCycleWay(line);
        }
        if (IRailwayLine.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createRailwayLine(line, 0);
        }
        if (IPathLine.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createPath(line, 0);
        }
        if (IReliefElementPoint.class.isAssignableFrom(classObj)) {
            IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom(),
                    true);
            return (OsmGeneObj) this.createReliefElementPoint(pt);
        }
        if (ICoastLine.class.isAssignableFrom(classObj)) {
            ILineString line = convertor
                    .convertOSMLine((OSMWay) resource.getGeom(), nodes, true);
            if (line.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createCoastLine(line);
        }
        if (ISquareArea.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createSquareArea(poly);
        }
        if (OsmCemetery.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return new OsmCemetery(poly);
        }
        if (OsmSchool.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return new OsmSchool(poly);
        }
        if (OsmHospital.class.isAssignableFrom(classObj)) {
            IPolygon poly = convertor.convertOSMPolygon(
                    (OSMWay) resource.getGeom(), nodes, true);
            if (poly.coord().size() < 4)
                return null;
            return new OsmHospital(poly);
        }
        // TODO
        return null;
    }

    public OsmGeneObj createGeneObj(Class<?> classObj, OSMResource resource,
            IGeometry geom) throws Exception {
        if (IRoadLine.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createRoadLine((ILineString) geom, 0);
        }
        if (ICable.class.isAssignableFrom(classObj)) {

            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createCable((ILineString) geom);
        }
        if (IBuilding.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createBuilding((IPolygon) geom);
        }
        if (ISportsField.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createSportsField((IPolygon) geom);
        }
        if (IWaterLine.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createWaterLine((ILineString) geom, 0);
        }
        if (IWaterArea.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createWaterArea((IPolygon) geom);
        }
        if (IBuildPoint.class.isAssignableFrom(classObj)) {
            return (OsmGeneObj) this.createBuildPoint((IPoint) geom);
        }
        if (IWaterArea.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createWaterArea((IPolygon) geom);
        }
        if (IAirportArea.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createAirportArea((IPolygon) geom);
        }
        if (IRunwayArea.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createRunwayArea((IPolygon) geom);
        }
        if (IRunwayLine.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createRunwayLine((ILineString) geom);
        }
        if (ITaxiwayArea.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createTaxiwayArea((IPolygon) geom);
        }
        if (ITaxiwayLine.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createTaxiwayLine((ILineString) geom,
                    null);
        }
        if (ISimpleLandUseArea.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createSimpleLandUseArea((IPolygon) geom,
                    0);
        }
        if (IPointOfInterest.class.isAssignableFrom(classObj)) {
            return new OsmPointOfInterest((IPoint) geom);
        }
        if (ITreePoint.class.isAssignableFrom(classObj)) {
            return (OsmGeneObj) this.createTreePoint((IPoint) geom);
        }
        if (ICycleWay.class.isAssignableFrom(classObj)) {
            return (OsmGeneObj) this.createCycleWay((ILineString) geom);
        }
        if (IRailwayLine.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createRailwayLine((ILineString) geom, 0);
        }
        if (IPathLine.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createPath((ILineString) geom, 0);
        }
        if (IReliefElementPoint.class.isAssignableFrom(classObj)) {
            return (OsmGeneObj) this.createReliefElementPoint((IPoint) geom);
        }
        if (ICoastLine.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() == 1)
                return null;
            return (OsmGeneObj) this.createCoastLine((ILineString) geom);
        }
        if (ISquareArea.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return (OsmGeneObj) this.createSquareArea((IPolygon) geom);
        }
        if (OsmCemetery.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return new OsmCemetery((IPolygon) geom);
        }
        if (OsmSchool.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return new OsmSchool((IPolygon) geom);
        }
        if (OsmHospital.class.isAssignableFrom(classObj)) {
            if (geom.coord().size() < 4)
                return null;
            return new OsmHospital((IPolygon) geom);
        }
        // TODO
        return null;
    }

    @Override
    public IBuilding createBuilding(IPolygon poly) {
        return new OsmBuilding(poly);
    }

    @Override
    public IBuilding createBuilding(IPolygon poly, String nature) {
        OsmBuilding build = new OsmBuilding(poly);
        build.setNature(nature);
        return build;
    }

    @Override
    public IBuildPoint createBuildPoint(IPoint point) {
        return new OsmBuildPoint(point);
    }

    @Override
    public ISportsField createSportsField(IPolygon poly) {
        return new OsmSportsField(poly);
    }

    @Override
    public IRoadLine createRoadLine(ILineString line, int importance) {
        return new OsmRoadLine(line, -1);
    }

    @Override
    public IRoadLine createRoadLine(TronconDeRoute geoxObj, int importance) {
        return new OsmRoadLine(geoxObj, importance);
    }

    // /////////////////
    // RAILWAY
    // /////////////////

    // RailwayLine
    @Override
    public IRailwayLine createRailwayLine(ILineString line, int importance) {
        return new OsmRailwayLine(line);
    }

    @Override
    public ICable createCable(ILineString line) {
        return new OsmCable(line);
    }

    @Override
    public IWaterLine createWaterLine(ILineString line, int importance) {
        return new OsmWaterLine(line);
    }

    @Override
    public IWaterArea createWaterArea(IPolygon poly) {
        return new OsmWaterArea(poly);
    }

    @Override
    public ISimpleLandUseArea createSimpleLandUseArea(IPolygon poly, int type) {
        return new OsmSimpleLandUseArea(poly);
    }

    @Override
    public IAirportArea createAirportArea(IPolygon geom) {
        return new OsmAirportArea(geom);
    }

    @Override
    public IRunwayArea createRunwayArea(IPolygon geom) {
        return new OsmRunwayArea(geom);
    }

    @Override
    public IRunwayLine createRunwayLine(ILineString geom) {
        return new OsmRunwayLine(geom);
    }

    @Override
    public IReliefField createReliefField(ChampContinu champ) {
        return new ReliefField(champ);
    }

    @Override
    public INetwork createNetwork() {
        return new Network();
    }

    @Override
    public INetwork createNetwork(Reseau res) {
        return new Network(res);
    }

    @Override
    public IRoadNode createRoadNode() {
        return new OsmRoadNode();
    }

    @Override
    public IRoadNode createRoadNode(IPoint point) {
        return new OsmRoadNode(point);
    }

    @Override
    public IRoadNode createRoadNode(Noeud noeud) {
        return new OsmRoadNode(noeud);
    }

    @Override
    public IRoadNode createRoadNode(NoeudRoutier geoxObj) {
        return new OsmRoadNode(geoxObj);
    }

    @Override
    public INetworkFace createNetworkFace(IPolygon poly) {
        return new OsmNetworkFace(poly);
    }

    @Override
    public INetworkFace createNetworkFace(Face geoxObj) {
        return new OsmNetworkFace(geoxObj);
    }

    public ITreePoint createTreePoint(IPoint geom) {
        return new OsmTreePoint(geom);
    }

    @Override
    public ICycleWay createCycleWay(ILineString line) {
        return new OsmCycleWay(line);
    }

    @Override
    public IPathLine createPath(ILineString line, int importance) {
        return new OsmPathLine(line, importance);
    }

    @Override
    public IReliefElementPoint createReliefElementPoint(IPoint point) {
        return new OsmReliefElementPoint(point);
    }

    @Override
    public ICoastLine createCoastLine(ILineString line) {
        return new OsmCoastline(line);
    }

    @Override
    public ISquareArea createSquareArea(IPolygon poly) {
        return new OsmParkArea(poly);
    }

    @Override
    public ITaxiwayArea createTaxiwayArea(IPolygon simple, TaxiwayType type) {
        return new OsmTaxiwayArea(simple, type);
    }

    public ITaxiwayArea createTaxiwayArea(IPolygon simple) {
        return new OsmTaxiwayArea(simple);
    }

    @Override
    public ITaxiwayLine createTaxiwayLine(ILineString geom, TaxiwayType type) {
        return new OsmTaxiwayLine(geom);
    }

    @Override
    public IRailwayNode createRailwayNode() {
        return new OsmRailwayNode();
    }

    @Override
    public IRailwayNode createRailwayNode(IPoint point) {
        return new OsmRailwayNode(point);
    }

    @Override
    public IRailwayNode createRailwayNode(Noeud noeud) {
        return new OsmRailwayNode(noeud);
    }

    @Override
    public IBuilding createBuilding() {
        return new OsmBuilding();
    }

    @Override
    public IUrbanBlock createUrbanBlock() {
        return new OsmUrbanBlock();
    }

    @Override
    public ITown createTown() {
        return new OsmTown();
    }

    @Override
    public ISportsField createSportsField() {
        return new OsmSportsField();
    }

    @Override
    public ICemetery createCemetery() {
        return new OsmCemetery();
    }

    @Override
    public IRoadLine createRoadLine() {
        return new OsmRoadLine();
    }

    @Override
    public IPathLine createPath() {
        return new OsmPathLine();
    }

    @Override
    public IRailwayLine createRailwayLine() {
        return new OsmRailwayLine();
    }

    @Override
    public ICable createCable() {
        return new OsmCable();
    }

    @Override
    public IWaterLine createWaterLine() {
        return new OsmWaterLine();
    }

    @Override
    public IWaterArea createWaterArea() {
        return new OsmWaterArea();
    }

    @Override
    public ISimpleLandUseArea createSimpleLandUseArea() {
        return new OsmSimpleLandUseArea();
    }

    @Override
    public ILabelPoint createLabelPoint() {
        // TODO Auto-generated method stub
        return super.createLabelPoint();
    }

    @Override
    public IMiscPoint createMiscPoint() {
        // TODO Auto-generated method stub
        return super.createMiscPoint();
    }

    @Override
    public IMiscLine createMiscLine() {
        // TODO Auto-generated method stub
        return super.createMiscLine();
    }

    @Override
    public IMiscArea createMiscArea() {
        // TODO Auto-generated method stub
        return super.createMiscArea();
    }

    @Override
    public IDualCarriageWay createDualCarriageways(IPolygon poly,
            int importance) {
        return new OsmDualCarriageway(poly, importance);
    }

    @Override
    public IDualCarriageWay createDualCarriageways(IPolygon poly,
            int importance, Collection<IRoadLine> innerRoads) {
        return new OsmDualCarriageway(poly, importance, innerRoads);
    }

    @Override
    public IDualCarriageWay createDualCarriageways(IPolygon poly,
            int importance, Collection<IRoadLine> innerRoads,
            Collection<IRoadLine> outerRoads) {
        return new OsmDualCarriageway(poly, importance, innerRoads, outerRoads);
    }

    @Override
    public IBranchingCrossroad createBranchingCrossroad() {
        return new OsmBranchingCrossroad();
    }

    @Override
    public IBranchingCrossroad createBranchingCrossroad(PatteOie geoxObj,
            Collection<IRoadLine> roads, Collection<IRoadNode> nodes) {
        return new OsmBranchingCrossroad(geoxObj, roads, nodes);
    }

    @Override
    public IRoundAbout createRoundAbout() {
        return new OsmRoundabout();
    }

    @Override
    public IRoundAbout createRoundAbout(RondPoint geoxObj,
            Collection<IRoadLine> roads, Collection<IRoadNode> nodes) {
        return new OsmRoundabout(geoxObj, roads, nodes);
    }

    @Override
    public IRoundAbout createRoundAbout(IPolygon geom,
            Collection<IRoadLine> externalRoads,
            Collection<IRoadLine> internalRoads,
            Collection<INetworkNode> initialNodes) {
        return new OsmRoundabout(geom, externalRoads, internalRoads,
                initialNodes);
    }

}
