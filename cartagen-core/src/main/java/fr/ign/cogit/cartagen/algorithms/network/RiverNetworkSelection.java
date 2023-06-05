package fr.ign.cogit.cartagen.algorithms.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.rivers.RiverStroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.rivers.RiverStrokesNetwork;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class RiverNetworkSelection {

    private static Logger logger = LogManager
            .getLogger(RiverNetworkSelection.class);

    private RiverStrokesNetwork net;
    private Map<ArcReseau, IWaterLine> map;
    /**
     * the minimum value for the Horton order of a stroke to be selected
     */
    private int hortonParam;
    /**
     * The minimum length for a river stroke to be selected.
     */
    private double lengthParam;
    /**
     * The minimum area for island formed by braided streams to be kept during
     * selection.
     */
    private double braidedAreaParam;
    /**
     * If true, the algorithm removes all braided streams.
     */
    private boolean removeBraided = false;

    public RiverNetworkSelection(int hortonParam, double lengthParam,
            double braidedAreaParam, boolean removeBraided) {
        super();
        buildStrokes();
        this.hortonParam = hortonParam;
        this.lengthParam = lengthParam;
        this.braidedAreaParam = braidedAreaParam;
        this.removeBraided = removeBraided;
    }

    /**
     * Build the strokes network from the water lines of the current dataset.
     * The network is supposed to have good flow directions.
     */
    private void buildStrokes() {
        // first enrich the network
        NetworkEnrichment.buildTopology(
                CartAGenDoc.getInstance().getCurrentDataset(),
                CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork(),
                false);

        // then, create the strokes network
        HashSet<ArcReseau> arcs = new HashSet<ArcReseau>();
        this.map = new HashMap<ArcReseau, IWaterLine>();
        for (IGeneObj feat : CartAGenDoc.getInstance().getCurrentDataset()
                .getHydroNetwork().getSections()) {
            if (feat.isEliminated()) {
                continue;
            }
            arcs.add((ArcReseau) feat.getGeoxObj());
            map.put((ArcReseau) feat.getGeoxObj(), (IWaterLine) feat);
        }
        this.net = new RiverStrokesNetwork(arcs);
        net.findSourcesAndSinks();
        // net.correctDoubleSources();
        net.buildRiverStrokes();
    }

    public Set<IWaterLine> selection() {
        Set<IWaterLine> eliminated = new HashSet<>();
        for (Stroke stroke : this.net.getStrokes()) {
            // selection on length
            if (((RiverStroke) stroke)
                    .getLength() < (Double) this.lengthParam) {
                for (ArcReseau arc : stroke.getFeatures()) {
                    eliminated.add(this.map.get(arc));
                }
            }
            // selection on horton order
            ((RiverStroke) stroke).computeHortonOrder();
            if (((RiverStroke) stroke)
                    .getHortonOrder() < (Integer) this.hortonParam) {
                for (ArcReseau arc : stroke.getFeatures()) {
                    eliminated.add(this.map.get(arc));
                }
            }

            if (this.removeBraided) {
                if (((RiverStroke) stroke).isBraided()) {
                    logger.trace("braided stroke " + stroke + " is eliminated");
                    for (ArcReseau arc : stroke.getFeatures()) {
                        eliminated.add(this.map.get(arc));
                    }
                }
            }
        }
        return eliminated;
    }

    public RiverStrokesNetwork getNet() {
        return net;
    }

    public void setNet(RiverStrokesNetwork net) {
        this.net = net;
    }
}
