/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.appli.plugins.process;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterEngine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterXMLParser;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.panel.XMLFileFilter;

/**
 * Extra menu to use the ScaleMaster2.0 model in CartAGen.
 * 
 * @author GTouya
 * 
 */
public class ScaleMaster2Plugin extends JMenu {

    private static String THEME_FILE = "src/main/resources/scalemaster/ScaleMasterTheme.xml";

    private ProjectFrame view;
    private GeOxygeneApplication appli;
    private ScaleMaster scaleMaster;
    private Set<ScaleMasterTheme> themes;
    private ScaleMasterEngine engine;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ScaleMaster2Plugin(GeOxygeneApplication appli, String title) {
        super(title);
        this.appli = appli;
        this.view = appli.getMainFrame().getSelectedProjectFrame();
        this.add(new JMenuItem(new LoadScaleMasterAction()));
        this.add(new JMenuItem(new SetDatabasesAction()));
        JMenu editMenu = new JMenu("Edit a ScaleMaster");
        this.add(editMenu);
        // TODO
        this.addSeparator();
        this.add(new JMenuItem(new RunScaleMasterAction()));
    }

    /**
     * Loads a ScaleMaster from XML file.
     * 
     * @author GTouya
     * 
     */
    class LoadScaleMasterAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new XMLFileFilter());
            int returnVal = fc.showSaveDialog(view.getGui());
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File fic = fc.getSelectedFile();
            ScaleMasterXMLParser parser = new ScaleMasterXMLParser(fic);
            try {
                themes = parser.parseScaleMasterThemes(new File(THEME_FILE));
                scaleMaster = parser.parseScaleMaster(themes);
                engine = new ScaleMasterEngine(scaleMaster, themes);
            } catch (DOMException | ClassNotFoundException
                    | ParserConfigurationException | SAXException
                    | IOException e) {
                e.printStackTrace();
            }
        }

        public LoadScaleMasterAction() {
            putValue(Action.SHORT_DESCRIPTION,
                    "Load a ScaleMaster2.0, stored in XML");
            putValue(Action.NAME, "Load a ScaleMaster");
        }
    }

    /**
     * 
     * @author GTouya
     * 
     */
    class RunScaleMasterAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            int scale = Integer.valueOf(
                    JOptionPane.showInputDialog("Choose the output scale"));
            engine.execute(scale);
        }

        public RunScaleMasterAction() {
            putValue(Action.SHORT_DESCRIPTION,
                    "Run the ScaleMaster on loaded data");
            putValue(Action.NAME, "Run the ScaleMaster");
        }
    }

    /**
     * 
     * @author GTouya
     * 
     */
    class SetDatabasesAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            SetDatabasesFrame frame = new SetDatabasesFrame(scaleMaster,
                    CartAGenDoc.getInstance());
            frame.setVisible(true);
        }

        public SetDatabasesAction() {
            putValue(Action.SHORT_DESCRIPTION,
                    "Set the databases to use with the ScaleMaster");
            putValue(Action.NAME, "Set Databases to use");
        }
    }

    class SetDatabasesFrame extends JFrame implements ActionListener {

        private static final long serialVersionUID = 1L;
        JTable tableDatabases;
        JComboBox<String> comboScaleMaster;
        JComboBox<Object> comboCartAGen;
        DefaultTableModel tableModel;
        JButton okBtn, addBtn;

        public SetDatabasesFrame(ScaleMaster scaleMaster,
                CartAGenDoc document) {
            JPanel pCombos = new JPanel();
            comboScaleMaster = new JComboBox<String>(
                    scaleMaster.getDatabases());
            comboCartAGen = new JComboBox<Object>(
                    document.getDatabases().keySet().toArray());
            addBtn = new JButton("Add");
            addBtn.addActionListener(this);
            addBtn.setActionCommand("Add");
            addBtn.setPreferredSize(new Dimension(100, 50));
            pCombos.add(comboScaleMaster);
            pCombos.add(comboCartAGen);
            pCombos.add(addBtn);
            pCombos.setLayout(new BoxLayout(pCombos, BoxLayout.X_AXIS));

            JPanel pTable = new JPanel();
            Object[] headers = new String[2];
            headers[0] = "ScaleMaster name";
            headers[1] = "name in CartAGen";
            tableModel = new DefaultTableModel(null, headers);
            tableDatabases = new JTable(tableModel);
            pTable.add(tableDatabases);
            pTable.setLayout(new BoxLayout(pTable, BoxLayout.X_AXIS));

            JPanel panelOK = new JPanel();
            okBtn = new JButton("OK");
            okBtn.addActionListener(this);
            okBtn.setActionCommand("OK");
            okBtn.setPreferredSize(new Dimension(100, 50));
            panelOK.add(okBtn);
            panelOK.setLayout(new BoxLayout(panelOK, BoxLayout.X_AXIS));

            this.getContentPane().add(Box.createVerticalGlue());
            this.getContentPane().add(pCombos);
            this.getContentPane().add(Box.createVerticalGlue());
            this.getContentPane().add(pTable);
            this.getContentPane().add(Box.createVerticalGlue());
            this.getContentPane().add(panelOK);
            this.getContentPane().add(Box.createVerticalGlue());
            this.getContentPane().setLayout(
                    new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
            this.setVisible(true);
            this.pack();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("OK")) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    engine.addDatabaseMapping(
                            (String) tableModel.getValueAt(i, 0),
                            (String) tableModel.getValueAt(i, 1));
                }
                this.setVisible(false);
            } else if (e.getActionCommand().equals("Add")) {
                String[] row = new String[2];
                row[0] = comboScaleMaster.getSelectedItem().toString();
                row[1] = comboCartAGen.getSelectedItem().toString();
                tableModel.addRow(row);
                this.pack();
            }
        }

    }
}
