package fr.ign.cogit.cartagen.collagen.resources.specs.rules;

import fr.ign.cogit.cartagen.collagen.resources.ontology.ProcessingConcept;

public class ORConclusion {
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private boolean positive, advice;
  private ProcessingConcept action;
  private OperationRule rule;

  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public ORConclusion(boolean positive, boolean advice, ProcessingConcept action) {
    super();
    this.positive = positive;
    this.advice = advice;
    this.action = action;
  }
  
  // Getters and setters //
  public boolean isPositive() {
    return positive;
  }
  public void setPositive(boolean positive) {
    this.positive = positive;
  }
  public ProcessingConcept getAction() {
    return action;
  }
  public void setAction(ProcessingConcept action) {
    this.action = action;
  }
  public boolean isAdvice() {
    return advice;
  }
  public void setAdvice(boolean advice) {
    this.advice = advice;
  }

  public void setRule(OperationRule rule) {
    this.rule = rule;
  }

  public OperationRule getRule() {
    return rule;
  }
  
  // Other public methods //

  ////////////////////////////////////////////
  //           Protected methods            //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //         Package visible methods        //
  ////////////////////////////////////////////

  //////////////////////////////////////////
  //           Private methods            //
  //////////////////////////////////////////

}

