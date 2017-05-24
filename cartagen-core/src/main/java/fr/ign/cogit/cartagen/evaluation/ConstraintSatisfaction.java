package fr.ign.cogit.cartagen.evaluation;

public enum ConstraintSatisfaction {
	UNACCEPTABLE,
	NOT_SATISFIED,
	BARELY_SATISFIED,
	FAIR,
	ACCEPTABLE,
	CORRECT,
	VERY_SATISFIED,
	PERFECT;
	
	public static ConstraintSatisfaction valueOf(int value){
		return ConstraintSatisfaction.values()[value];
	}
	
	public static ConstraintSatisfaction valueOfFrench(String value){
	  if(value.equals("PARFAIT")) return PERFECT;
	  if(value.equals("TRES_SATISFAIT")) return VERY_SATISFIED;
	  if(value.equals("CORRECT")) return CORRECT;
	  if(value.equals("MOYEN")) return ACCEPTABLE;
	  if(value.equals("PASSABLE")) return FAIR;
	  if(value.equals("PEU_SATISFAIT")) return BARELY_SATISFIED;
	  if(value.equals("TRES_PEU_SATISFAIT")) return NOT_SATISFIED;
	  if(value.equals("NON_SATISFAIT")) return UNACCEPTABLE;
	  return null;
	}
}
