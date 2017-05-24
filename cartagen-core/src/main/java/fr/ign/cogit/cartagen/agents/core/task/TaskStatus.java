package fr.ign.cogit.cartagen.agents.core.task;

/**
 * Describes the status of a task with respect to the on-going process
 * @author CDuchene
 */
public enum TaskStatus {
  /**
   * The task is foreseen but has not yet started
   */
  NOT_STARTED,
  /**
   * The task is currently active
   */
  PROCESSING,
  /**
   * The task has stated and has stopped to wait for a generated task or a
   * generated conversation to finish.
   */
  WAITING,
  /**
   * The task was waiting, but all its generated tasks and conversations are now
   * finished, therefore it can resume.
   */
  RESUMABLE,
  /**
   * The task is finished.
   */
  FINISHED
}
