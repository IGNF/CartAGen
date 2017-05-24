package fr.ign.cogit.cartagen.collagen.components.registry;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fr.ign.cogit.cartagen.collagen.enrichment.SpecElementMonitor;
import fr.ign.cogit.cartagen.collagen.geospaces.model.GeographicSpace;
import fr.ign.cogit.cartagen.collagen.processes.model.GeneralisationProcess;
import fr.ign.cogit.cartagen.collagen.processes.model.ProcessResult;
import fr.ign.cogit.cartagen.collagen.resources.ontology.GeoSpaceConcept;
import fr.ign.cogit.cartagen.collagen.resources.specs.SpecificationElement;

public class ProcessRegister {

  private Set<ProcessCapabDescription> descriptions;
  private Set<GeneralisationProcess> processus;
  private Map<ProcessCapabDescription, ProcessResult> succesProc;
  private final static double BAD_RESULT = 4.0;
  private int echIni, echGen;

  public Set<ProcessCapabDescription> getDescriptions() {
    return this.descriptions;
  }

  public void setDescriptions(Set<ProcessCapabDescription> descriptions) {
    this.descriptions = descriptions;
  }

  public ProcessRegister(Set<ProcessCapabDescription> descriptions,
      Set<GeneralisationProcess> processus, int echIni, int echGen) {
    super();
    this.descriptions = descriptions;
    this.processus = processus;
    this.echIni = echIni;
    this.echGen = echGen;
    this.succesProc = new HashMap<ProcessCapabDescription, ProcessResult>();
  }

  /**
   * Pour un espace géographique donné, le registre propose une liste de
   * processus de généralisation applicables par ordre de préférence. Cette
   * liste est construite dans un premier temps à partir des pré-conditions des
   * processus enregistrés. Puis une sélection est réalisée sur ce premier tri à
   * partir des post-conditions.
   * 
   * For a given {@link GeographicSpace} instance, the register provides a list
   * of applicable {@link GeneralisationProcess}, sorted by preference. The list
   * is built in a first stage using the pre-conditions of the registered
   * processes. Then, it is filtered using the post-conditions.
   * 
   * @param espace : l'espace géo que l'on cherche à généraliser
   * @return la liste des processus de généralisation applicables sur espace par
   *         ordre de préférence.
   * @throws Exception
   */
  public List<GeneralisationProcess> request(GeographicSpace espace)
      throws Exception {
    // on récupère d'abord les processus dont la pré-condition colle
    List<ProcessCapabDescription> liste = this.filterProcByPreCondition(espace);

    // puis on classe le résultat en fonction des post-conditions
    List<GeneralisationProcess> liste2 = this.sortProcByPostCondition(espace,
        liste, this.processus);

    // on filtre en fonction des succés/échec
    for (GeneralisationProcess proc : liste2.subList(0, liste2.size())) {
      // on teste si ce processus a déjà connu un succés/échec
      if (this.succesProc.containsKey(proc.getDescription())) {
        ProcessResult res = this.succesProc.get(proc.getDescription());
        // si les résultats ne concernent pas cet espace, on passe au proc
        // suivant
        if (!res.getEspaces().contains(espace)) {
          continue;
        }
        // on évalue alors les résultats précédents sur ce type d'espace
        double eval = res.evaluationMoyenne(espace);
        // si l'évaluation est mauvaise, on met le processus en fin de liste
        if (eval <= ProcessRegister.BAD_RESULT) {
          // TODO amélioration possible par un tri ?
          liste2.remove(proc);
          liste2.add(proc);
        }
      }
    }
    return liste2;
  }

  /**
   * Filters the registered {@link ProcessCapabDescription} using the
   * {@link PreConditionProcess} that match a given {@link GeographicSpace}
   * instance. It corresponds the first stage of the process proposal from the
   * register.
   * @param espace
   * @return
   */
  private List<ProcessCapabDescription> filterProcByPreCondition(
      GeographicSpace espace) {
    List<ProcessCapabDescription> liste = this.search(espace.getConcept(),
        this.echIni, this.echGen);
    return liste;
  }

  /**
   * Sorts a given list of {@link GeneralisationProcess} instances, according to
   * the quality of matching between the {@link PostConditionProcess} of the
   * related {@link ProcessCapabDescription} and the actual monitors existing
   * inside the given {@link GeographicSpace} instance. It corresponds to the
   * third stage of the register process proposal.
   * @param espace
   * @param liste
   * @param processes
   * @return
   * @throws Exception
   */
  private List<GeneralisationProcess> sortProcByPostCondition(
      GeographicSpace espace, List<ProcessCapabDescription> liste,
      Collection<GeneralisationProcess> processes) throws Exception {
    // get the constraints from the space object
    Set<SpecElementMonitor> specs = espace.getMonitors();

    List<GeneralisationProcess> listeFinale = new ArrayList<GeneralisationProcess>();
    // filter the process description according to the constraints
    Collections.sort(liste,
        new FilterProcComparator(specs, espace.getConcept()));
    Collections.reverse(liste);
    // get the process corresponding to the description
    for (ProcessCapabDescription descr : liste) {
      for (GeneralisationProcess proc : processes) {
        if (proc.getClass().getName().equals(descr.getProcessJavaClass())) {
          listeFinale.add(proc);
          break;
        }
      }
    }
    return listeFinale;
  }

  // STANDARD FUNCTIONS OF REGISTERS FOLLOWING THE UDDI AND DF-FIPA STANDARDS
  /**
   * Registers a process by its formal description (i.e.
   * {@link ProcessCapabDescription} instance).
   */
  public void register(ProcessCapabDescription service) {
    this.descriptions.add(service);
  }

  /**
   * Removes a process, designated by its formal description (i.e.
   * {@link ProcessCapabDescription} instance), from the registers .
   */
  public void unRegister(ProcessCapabDescription service) {
    this.descriptions.remove(service);
  }

  /**
   * Change the register description of a {@link GeneralisationProcess}.
   * @param serviceIni
   * @param serviceModifie
   */
  public void modify(ProcessCapabDescription serviceIni,
      ProcessCapabDescription serviceModifie) {
    this.descriptions.remove(serviceIni);
    this.descriptions.add(serviceModifie);
  }

  /**
   * Searches the registered {@link ProcessCapabDescription} that are compatible
   * a given scale range and {@link GeoSpaceConcept}. It is the second stage of
   * the request for processes.
   * @param space
   * @param echIni
   * @param echGen
   * @return
   */
  public List<ProcessCapabDescription> search(GeoSpaceConcept space, int echIni,
      int echGen) {
    List<ProcessCapabDescription> processes = new ArrayList<ProcessCapabDescription>();
    // boucle sur les descriptions de processus
    for (ProcessCapabDescription descr : this.descriptions) {
      for (PreConditionProcess pre : descr.getPreConditions()) {
        if (pre.estCompatible(space, echIni, echGen)) {
          processes.add(descr);
          break;
        }
      }
    }
    Collections.sort(processes, new DescrProcessPreComparator(space));
    return processes;
  }

  /**
   * Searches the registered {@link ProcessCapabDescription} that contain a
   * given {@link PostConditionProcess}.
   * @param post
   * @return
   */
  public List<ProcessCapabDescription> search(PostConditionProcess post) {
    List<ProcessCapabDescription> processes = new ArrayList<ProcessCapabDescription>();
    // boucle sur les descriptions de processus
    for (ProcessCapabDescription descr : this.descriptions) {
      if (descr.getPostConditions().contains(post)) {
        processes.add(descr);
      }
    }
    Collections.sort(processes, new DescrProcessPostComparator(post));
    return processes;
  }

  /**
   * Computes the matching ratio between a {@link ProcessCapabDescription} and
   * its {@link PostConditionProcess}, and a given set of
   * {@link SpecElementMonitor} instances.
   * @param proc
   * @param specs
   * @return
   */
  private double getRatioSpecsForProcess(ProcessCapabDescription proc,
      Set<SpecElementMonitor> specs) {
    double total = 0.0, managed = 0.0;
    System.out.println(specs.size());
    for (SpecElementMonitor moniteur : specs) {
      SpecificationElement spec = moniteur.getElementSpec();
      total += spec.getImportance() * moniteur.getImportance();
      if (proc.handlesSpecification(spec)) {
        managed += spec.getImportance() * moniteur.getImportance()
            * proc.getPostConditionFromSpec(spec).getConfidenceRate().ordinal()
            / 5.0;
      }
    }
    if (total != 0.0) {
      return managed / total;
    }
    return 0.0;
  }

  /**
   * Simply retrieves the registered {@link ProcessCapabDescription} for a given
   * {@link GeneralisationProcess} instance.
   * @param proc
   * @return
   */
  private ProcessCapabDescription getDescriptionFromProcess(
      GeneralisationProcess proc) {
    for (ProcessCapabDescription descr : this.descriptions) {
      if (descr.getProcessJavaClass().equals(proc.getClass().getName())) {
        return descr;
      }
    }
    return null;
  }

  /**
   * Displays the result of a register request in a swing frame.
   * @param espace
   * @param requete
   * @throws Exception
   */
  public void afficheRequete(GeographicSpace espace,
      List<GeneralisationProcess> requete) throws Exception {
    JFrame frame = new JFrame("Request to CollaGen Registry");
    // a panel to display the request
    JPanel pReq = new JPanel();
    JLabel lbl1 = new JLabel("Requested Space: ");
    lbl1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
    JLabel lbl2 = new JLabel(espace.getConcept() + " ");
    lbl2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
    JLabel lbl3 = new JLabel(espace.getId() + " ");
    lbl3.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
    pReq.add(lbl1);
    pReq.add(lbl2);
    pReq.add(lbl3);
    pReq.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createLineBorder(Color.BLACK)));
    pReq.setLayout(new BoxLayout(pReq, BoxLayout.X_AXIS));

    // a JTable to display the answer to the request
    DefaultTableModel model = new DefaultTableModel(new String[] { "Ranking",
        "Process Name", "Confidence Rate", "Constraint Matching" }, 0);
    for (int i = 0; i < requete.size(); i++) {
      ProcessCapabDescription proc = this
          .getDescriptionFromProcess(requete.get(i));
      int confidence = proc.getPreConditionFromSpace(espace).getConfidenceRate()
          .ordinal();
      Set<SpecElementMonitor> specs = espace.getMonitors();
      double constraints = this.getRatioSpecsForProcess(proc, specs);
      constraints = Math.rint(constraints * 100.0) / 100.0;
      model.addRow(
          new String[] { String.valueOf(i + 1), requete.get(i).getName(),
              String.valueOf(confidence), String.valueOf(constraints) });
    }
    JTable table = new JTable(model);
    // JPanel pTable = new JPanel();
    // pTable.add(table);
    // pTable.setLayout(new BoxLayout(pTable, BoxLayout.X_AXIS));

    // frame layout
    frame.getContentPane().add(pReq);
    frame.getContentPane().add(new JScrollPane(table));
    frame.getContentPane()
        .setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
    frame.setVisible(true);
  }

  /**
   * A comparator that sorts {@link ProcessCapabDescription} instances according
   * to pre and post-conditions. Used by the register to handle the requests.
   * @author GTouya
   * 
   */
  class FilterProcComparator implements Comparator<ProcessCapabDescription> {

    Set<SpecElementMonitor> specs;
    private GeoSpaceConcept space;

    @Override
    public int compare(ProcessCapabDescription arg0,
        ProcessCapabDescription arg1) {
      double ratio0 = ProcessRegister.this.getRatioSpecsForProcess(arg0,
          this.specs);
      double ratio1 = ProcessRegister.this.getRatioSpecsForProcess(arg1,
          this.specs);
      PreConditionProcess pre0 = null;
      for (PreConditionProcess p : arg0.getPreConditions()) {
        if (p.getSpace().equals(this.space)) {
          pre0 = p;
          break;
        }
      }
      PreConditionProcess pre1 = null;
      for (PreConditionProcess p : arg1.getPreConditions()) {
        if (p.getSpace().equals(this.space)) {
          pre1 = p;
          break;
        }
      }
      if (pre0 == null || pre1 == null) {
        return -1;
      }
      if (!pre0.getSpace().equals(pre1.getSpace())) {
        return 0;
      }
      return (int) (pre0.getConfidenceRate().ordinal() * ratio0
          - pre1.getConfidenceRate().ordinal() * ratio1);
    }

    FilterProcComparator(Set<SpecElementMonitor> specs, GeoSpaceConcept space) {
      this.specs = specs;
      this.space = space;
    }
  }
}
