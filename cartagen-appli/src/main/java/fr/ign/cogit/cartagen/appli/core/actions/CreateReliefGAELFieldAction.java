package fr.ign.cogit.cartagen.appli.core.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.spatialanalysis.fields.FieldEnrichment;

public class CreateReliefGAELFieldAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
        IReliefField field = dataset.getReliefField();
        for (IContourLine line : dataset.getContourLines())
            field.addContourLine(line);
        FieldEnrichment.createTrianglesInReliefField(field);
    }

    public CreateReliefGAELFieldAction() {
        super();
        this.putValue(Action.NAME, "Create all GAEL agents");
    }

}
