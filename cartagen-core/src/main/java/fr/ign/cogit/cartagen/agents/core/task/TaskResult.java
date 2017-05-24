/**
 * 
 */
package fr.ign.cogit.cartagen.agents.core.task;

/**
 * A possible result for a task (encapsulated in a subclass of {@link Task}). A
 * TaskResult is valid for one task and has a reference to the corresponding
 * task class. It should be declared as a private final static field of this
 * task class.
 * @author CDuchene
 * 
 */
public class TaskResult {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // Private fields //
  /**
   * The task class (subclass of Task) it is valid for.
   */
  private Class<? extends Task> taskClass;
  /**
   * A short string making the nature of the result understandable.
   */
  private String name;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  /**
   * Sole constructor for a task result. Constructs it from its name and the
   * task class it is valid for.
   * @param taskClass The task class this task result is valid for.
   * @param name Acronym for the result (short and self-comprehensive).
   */
  public TaskResult(Class<? extends Task> taskClass, String name) {
    this.taskClass = taskClass;
    this.name = name;
  }

  // Getters and setters //

  // Other public methods //

  /**
   * Compares this task result to the specified object. Returns true if and only
   * if the object is of class <code>TaskResult</code> and has the same
   * <code>taskClass</code> and <code>name</code> as this task result.
   * @return true if the compared object is of class TaskResult and has the same
   *         name and taskClass as <code>this</code>.
   */
  @Override
  public boolean equals(Object obj) {
    // Check the class first
    if (obj.getClass() != TaskResult.class) {
      return false;
    }
    // Then the taskClass and name
    if ((this.taskClass != ((TaskResult) obj).taskClass)
        || (this.name != ((TaskResult) obj).name)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc} (This is the behaviour inherited from the super class).
   * <p>
   * Here the hashcode is the hascode of the {@link #name} of the task result.
   * This makes it possible to have two similar hashcodes for task results that
   * are different (if they have the same name, but are not linked to the same
   * task class). But it should not decrease too much the performance of the
   * <code>equals</code> method since two task results that are not linked to
   * the same task class should never be compared.
   * @return the hascode of
   */
  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  /**
   * @return The name of this task result.
   */
  @Override
  public String toString() {
    return this.name;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////
  /**
   * The default constructor is made private in order to force the use of the
   * parameterised constructor.
   */
  @SuppressWarnings("unused")
  private TaskResult() {
    super();
  }

}
