package fr.ign.cogit.cartagen.agents.diogen.hikingroutes.csproutes;

/**
 * 
 * @author JTeulade-Denantes
 * 
 *         This class implements all the different constraints we need to create
 *         a CSP.
 */
public class constraintsStaticFunctionsForCSP {
  //
  // private static Logger LOGGER =
  // Logger.getLogger(constraintsStaticFunctionsForCSP.class.getName());
  //
  // /**
  // * this function returns a node score according the number of crossed roads
  // and routes
  // * @param node whose you want to have the score
  // * @return the final score
  // */
  // public static double getNodeCrossingsScore(StrokeNode node) {
  // if (node == null) {
  // LOGGER.error("Problem to evaluate a null node");
  // return 0;
  // }
  // //What weight do we want to give for a node crossing?
  // double nodeCrossingsWeight = 0.5;
  // //this map saves the routes we already found the score
  // List<String> routesAlreadySeen = new ArrayList<String>();
  // //we get the ordered list
  // List<String> orderedRoutesAndRoads = orderedRoutesAndRoads(node);
  // //This map gives all the crossings for a route. So for a crossing between
  // two routes, we add two objects in this map
  // Map<String, List<String>> crossingsRouteMap = new HashMap<String,
  // List<String>>();
  //
  // double score = 0;
  // for (Stroke stroke : Iterables.concat(node.getInStrokes(),
  // node.getOutStrokes())) {
  // for (String route : ((RoadStrokeForRoutes) stroke).getRoutesName()) {
  // if (!routesAlreadySeen.contains(route)) {
  // routesAlreadySeen.add(route);
  // //we add the score related to this route
  // score += nodeCrossingsForARoute(orderedRoutesAndRoads, route,
  // crossingsRouteMap);
  // }
  // }
  // }
  // //we multiply the final score by the weight for the node crossings
  // score *= nodeCrossingsWeight;
  //
  // //how quickly clusters score will grow?
  // double clusterWeight = 2;
  // LOGGER.debug("score without crossingsRouteMap = " + score);
  // LOGGER.debug("crossingsRouteMap = " + crossingsRouteMap);
  // int clusterOccurrences = 0;
  // String mostCrossingsRoute = null;
  // //we iterate while we didn't see all the crossings
  // while (!crossingsRouteMap.isEmpty()) {
  // int sizeMax = 0;
  // //we look for the route with the most crossings number, it s
  // mostCrossingsRoute
  // for (String route : crossingsRouteMap.keySet()) {
  // if (crossingsRouteMap.get(route).size() > sizeMax) {
  // sizeMax = crossingsRouteMap.get(route).size();
  // mostCrossingsRoute = route;
  // }
  // }
  // //we found a crossings cluster
  // score += Math.exp(clusterOccurrences-1);
  // clusterOccurrences += clusterWeight;
  // //once we found it, we remove all its occurrence from crossingsRouteMap
  // for (String route : crossingsRouteMap.get(mostCrossingsRoute)) {
  // crossingsRouteMap.get(route).remove(mostCrossingsRoute);
  // if (crossingsRouteMap.get(route).isEmpty()) {
  // crossingsRouteMap.remove(route);
  // }
  // }
  // crossingsRouteMap.remove(mostCrossingsRoute);
  // }
  // return score;
  // }
  //
  //
  // /**
  // * this function returns the score for a route on a node
  // * @param orderedRoutesAndRoads, the ordered list related to the node
  // * @param route whose we want to have the score
  // * @param crossingsRouteMap, with all the crossings for a route
  // * @return
  // */
  // private static int nodeCrossingsForARoute(List<String>
  // orderedRoutesAndRoads, String route, Map<String, List<String>>
  // crossingsRouteMap) {
  // LOGGER.debug("orderedRoutesAndRoads = " + orderedRoutesAndRoads);
  // LOGGER.debug("on calcule le score pour l'itinéraire " + route);
  // //This set saves all the routes
  // Set<String> alreadySeen = new HashSet<String>();
  // //This list splits orderedRoutesAndRoads list in different sections
  // delimited by route argument
  // List<Set<String>> sections = new ArrayList<Set<String>>();
  // int firstOccurrenceIndex = orderedRoutesAndRoads.indexOf(route);
  // if (firstOccurrenceIndex ==-1) {
  // LOGGER.error("the route " + route + " can't be found in the list " +
  // orderedRoutesAndRoads);
  // return 0;
  // }
  // int routeOccurrence = 0;
  // sections.add(new HashSet<String>());
  // for (int i = (firstOccurrenceIndex + 1)%orderedRoutesAndRoads.size() ; i !=
  // firstOccurrenceIndex ; i = (i+1)%orderedRoutesAndRoads.size()) {
  // if (orderedRoutesAndRoads.get(i).equals(route)) {
  // //it's a new section
  // routeOccurrence++;
  // sections.add(new HashSet<String>());
  // } else {
  // //we add the route in the current section
  // sections.get(routeOccurrence).add(orderedRoutesAndRoads.get(i));
  // alreadySeen.add(orderedRoutesAndRoads.get(i));
  // }
  // }
  // LOGGER.debug("sections = " + sections);
  // int score = 0;
  // int localScore;
  // for (String differentRoute : alreadySeen) {
  // localScore = routeCrossings(sections, differentRoute);
  // score += localScore;
  // //we add the crossings we found between route and differentRoute
  // for (int i = 0 ; i <localScore ; i++) {
  // if (!crossingsRouteMap.containsKey(route)) {
  // crossingsRouteMap.put(route, new ArrayList<String>());
  // }
  // if (!crossingsRouteMap.containsKey(differentRoute)) {
  // crossingsRouteMap.put(differentRoute, new ArrayList<String>());
  // }
  // //we add it in twice for each route
  // crossingsRouteMap.get(route).add(differentRoute);
  // crossingsRouteMap.get(differentRoute).add(route);
  // }
  // }
  //
  // //we remove the route, we have just processed
  // while (orderedRoutesAndRoads.remove(route)) ;
  // LOGGER.debug("on trouve un score = " + score);
  // return score;
  // }
  //
  // /**
  // * This functions counts the crossings for a route in a list
  // * @param sections related to a route
  // * @param route
  // * @return the number of crossings between the route argument and the other
  // one related to the sections
  // */
  // private static int routeCrossings (List<Set<String>> sections, String
  // route) {
  // int sectionsSize = sections.size();
  // int incrIndex = sectionsSize ;
  // // we find the last occurrence of the route in sections
  // while (!sections.get(--incrIndex).contains(route));
  // int decrIndex = incrIndex;
  // int index=1;
  // //we stop when we checked all the sets
  // while (decrIndex >= index && (incrIndex + index)%sectionsSize != decrIndex)
  // {
  // if (sections.get((incrIndex + index)%sectionsSize).contains(route)) {
  // //we found a a set containing the route
  // incrIndex += index;
  // index = 1;
  // } else if (sections.get(decrIndex - index).contains(route)) {
  // //we found a a set containing the route
  // decrIndex -= index;
  // index = 1;
  // } else {
  // //we keep searching
  // index++;
  // }
  // }
  // return incrIndex-decrIndex;
  // }
  //
  //
  //
  // /**
  // * This function returns ordered routes and roads related to this nodes
  // * @param node
  // * @return the ordered list of strokes
  // */
  // private static List<String> orderedRoutesAndRoads(StrokeNode node) {
  //
  // RoadStrokeForRoutes routeStroke;
  // int routePosition, carriedObjectsNumber, inStroke;
  // List<String> globalClockwise = new ArrayList<String>();
  //
  // //For all the ordered strokes
  // for (Stroke stroke : node.orderedStrokes()) {
  // if (stroke == null) {
  // globalClockwise.add("non fictive road");
  // continue;
  // }
  // routeStroke = (RoadStrokeForRoutes) stroke;
  // carriedObjectsNumber = routeStroke.getCarriedObjectsNumber();
  //
  // //we create an array with the good size
  // String[] strokeClockwise = new String[2*carriedObjectsNumber + 1];
  // // if the stroke is going out the node, we have to take the inverse of its
  // route position
  // inStroke = (node.getInStrokes().contains(stroke)) ? 1 : -1;
  // //for all the routes
  // for (int i = 0 ; i< routeStroke.getCarriedObjectsNumber() ; i++) {
  // routePosition = routeStroke.getRoutesPositions().get(i) * inStroke;
  // //to have positive index, we add the routes number.
  // strokeClockwise[carriedObjectsNumber + routePosition] =
  // routeStroke.getRoutesName().get(i);
  // }
  // for (int i = 0 ; i<strokeClockwise.length ; i++) {
  // if (strokeClockwise[i]!=null) {
  // globalClockwise.add(strokeClockwise[i]);
  // } else if (i == carriedObjectsNumber && !routeStroke.isFictive()) {
  // //we had a key allowing to save non fictive road
  // globalClockwise.add("non fictive road");
  // }
  // }
  // }
  //
  // return globalClockwise;
  //
  // }
  //
  //
  // /**
  // * This function returns a score on the relative positions for a node.
  // * The goal is to further a route at the same relative position
  // * @param node
  // * @return the score
  // */
  // public static double getNodeRelativePositionsScore(StrokeNode node) {
  // if (node == null) {
  // LOGGER.info("Problem to evaluate a null node in getNodeRelativePositionsScore function");
  // return 0;
  // }
  // //this map will contain for a route all the relative positions according to
  // this node
  // Map<String,List<Integer>> nodeRoutesPositions = new
  // HashMap<String,List<Integer>>();
  //
  // RoadStrokeForRoutes roadStrokeForRoutes;
  //
  // int inStroke;
  // for (Stroke stroke : Iterables.concat(node.getInStrokes(),
  // node.getOutStrokes())) {
  // roadStrokeForRoutes = ((RoadStrokeForRoutes) stroke);
  // // if the stroke is going out the node, we have to take the inverse of its
  // route position
  // inStroke = (node.getInStrokes().contains(stroke)) ? 1 : -1;
  // for (int i = 0 ; i < roadStrokeForRoutes.getRoutesName().size() ; i++) {
  // String routeName = roadStrokeForRoutes.getRoutesName().get(i);
  // if (nodeRoutesPositions.get(routeName) == null) {
  // nodeRoutesPositions.put(routeName , new ArrayList<Integer>());
  // }
  // nodeRoutesPositions.get(routeName).add(roadStrokeForRoutes.getRoutesPositions().get(i)
  // * inStroke);
  // }
  // }
  //
  // double score = 0;
  // for (String routeName : nodeRoutesPositions.keySet()) {
  // List<Integer> routePositions = nodeRoutesPositions.get(routeName);
  // LOGGER.debug(routeName + " -> " + routePositions);
  // if (routePositions.size() < 2) {
  // continue;
  // }
  // int routeScore = 0;;
  // //we choose to count only the minimum of each position
  // for (int i = 0 ; i < routePositions.size() ; i++) {
  // int minScore = -1;
  // for (int j = i + 1 ; j < routePositions.size() ; j++) {
  // int localScore = Math.abs(routePositions.get(i) + routePositions.get(j));
  // if (minScore == -1 || localScore < minScore) {
  // minScore = localScore;
  // }
  // }
  // LOGGER.debug("minScore = " + minScore);
  // if (minScore != -1) {
  // routeScore += minScore;
  // }
  // }
  // // score += (routeScore == 1) ? 1 : routeScore/2;
  // score += Math.log(routeScore*2 + 1);
  // LOGGER.debug("score = " + score);
  // }
  //
  // LOGGER.debug("routesRelativePositionsForNode returns the score " + score);
  // return score;
  //
  // }
  //
  // /**
  // * This function counts the number of internal crossed roads along the
  // stroke
  // * @param stroke whose you want to have a score
  // * @return the score
  // */
  // public static int getInternalCrossedScore(RoadStrokeForRoutes stroke) {
  // if (stroke == null) {
  // LOGGER.info("Problem to evaluate a null stroke in getInternalCrossedScore function");
  // return 0;
  // }
  // int crossedRoadsRight = stroke.crossedRoadsNumber(true);
  // int crossedRoadsLeft = stroke.crossedRoadsNumber(false);
  // int rightRoutes = 0;
  // int leftRoutes = 0;
  //
  // for (int position : stroke.getRoutesPositions()) {
  // if (position > 0) {
  // //we have to check the orientation of the arc
  // rightRoutes = crossedRoadsRight;
  // } else if (position < 0) {
  // leftRoutes = crossedRoadsLeft;
  // }
  // }
  //
  // int score = Math.max(rightRoutes, leftRoutes) - Math.min(crossedRoadsRight,
  // crossedRoadsLeft);
  // LOGGER.debug("getInternalCrossedRoadsScore returns the score " + score);
  // return score;
  // }
  //
  // /**
  // * This function solves a CSP problem with 3 possible algorithms
  // * @param data
  // * @param algorithmChoice
  // */
  // public static void CSPSolver(CdBDataset data, int algorithmChoice) {
  //
  //
  // RoadStrokeForRoutes[] strokes =
  // sortStrokes(data.getSelectedRouteStrokes());
  //
  // //this map links each node to its number of related strokes
  // Map<StrokeNode, Integer> nodeOccurrences = new HashMap<StrokeNode,
  // Integer>();
  // for (StrokeNode node : data.getRouteStrokeNodes()) {
  // nodeOccurrences.put(node, node.getInStrokes().size() +
  // node.getOutStrokes().size());
  // }
  //
  // List<Integer> minConfigSolution = new ArrayList<>();
  // LOGGER.debug("Let's go !");
  // long timer = System.currentTimeMillis();
  // double minScore = 0;
  // try {
  // //we call the algorithm
  // if (algorithmChoice == 0) {
  // minScore = thresholdBacktracking(strokes, nodeOccurrences,
  // minConfigSolution, false);
  // } else if (algorithmChoice == 1) {
  // minScore = thresholdBacktracking(strokes, nodeOccurrences,
  // minConfigSolution, true);
  // }else if (algorithmChoice == 2) {
  // minScore = dichotomicBacktracking(strokes, nodeOccurrences,
  // minConfigSolution);
  // }
  // //we print the solution
  // LOGGER.info("\ntimer = " + (System.currentTimeMillis() - timer)/1000);
  // LOGGER.info("on a fini !!! et minScore = " + minScore);
  // for (int i = 0 ; i < strokes.length ;i++) {
  // strokes[i].setRouteConfiguration(minConfigSolution.get(i));
  // }
  // LOGGER.info("minConfigSolution");
  // for (int i = 0 ; i < minConfigSolution.size() ;i++) {
  // LOGGER.info(strokes[i] + " -> " + strokes[i].getRouteConfigurationIndex() +
  // " (" + strokes[i].getRoutesPositions() + ")");
  // }
  //
  // //we add the solution in our map
  // for (RoadStrokeForRoutes routeStroke : data.getSelectedRouteStrokes()){
  // for (int i = 0 ; i < routeStroke.getCarriedObjectsNumber() ; i++) {
  // routeStroke.setRoadRelativePosition(routeStroke.getRoutesName().get(i),
  // routeStroke.getRoutesPositions().get(i));
  // }
  // }
  //
  // } catch (Exception e) {
  // LOGGER.info("Raté..... =) timer = " + (System.currentTimeMillis() -
  // timer)/1000);
  // LOGGER.info("minConfigSolution");
  // for (int i = 0 ; i < minConfigSolution.size() ;i++) {
  // LOGGER.info(strokes[i] + " -> " + minConfigSolution.get(i) + " (" +
  // RoutesConfigurations.getConfiguration(strokes[i].getCarriedObjectsNumber(),
  // minConfigSolution.get(i)) + ")");
  // }
  // throw(e);
  // }
  // }
  //
  // /**
  // * this function orders the strokes according to the node complexity
  // * @param selectedRouteStrokes
  // * @return the ordered array
  // */
  // private static RoadStrokeForRoutes[] sortStrokes(
  // Set<RoadStrokeForRoutes> selectedRouteStrokes) {
  // RoadStrokeForRoutes[] strokes = new
  // RoadStrokeForRoutes[selectedRouteStrokes.size()];
  //
  // //This map gives for a routes number, all the nodes with this routes number
  // Map<Integer, Set<StrokeNode>> routesNumberNodes = new HashMap<Integer,
  // Set<StrokeNode>>();
  // //for the selected route strokes
  // for (RoadStrokeForRoutes stroke : selectedRouteStrokes) {
  // //we identify the initial node
  // StrokeNode node = stroke.getStrokeInitialNode();
  // int routesNumberNode = 1;
  // //for all the strokes related to this node, we evaluate the node complexity
  // for (Stroke nodeStroke :
  // Iterables.concat(node.getInStrokes(),node.getOutStrokes())) {
  // if (selectedRouteStrokes.contains(nodeStroke)) {
  // //we choose to multiply the strokes size configuration, good approximation
  // to the node complexity
  // routesNumberNode *=
  // RoutesConfigurations.getConfigurationsNumber(((RoadStrokeForRoutes)
  // nodeStroke).getCarriedObjectsNumber());
  // } else {
  // //otherwise, we don't care about this node because we can't access to all
  // its strokes
  // routesNumberNode = 0;
  // break;
  // }
  // }
  // //we add the node to the map
  // if (!routesNumberNodes.containsKey(routesNumberNode)) {
  // routesNumberNodes.put(routesNumberNode, new HashSet<StrokeNode>());
  // }
  // routesNumberNodes.get(routesNumberNode).add(node);
  //
  // //idem for the final node
  // node = stroke.getStrokeFinalNode();
  // routesNumberNode = 1;
  // for (Stroke nodeStroke :
  // Iterables.concat(node.getInStrokes(),node.getOutStrokes())) {
  // if (selectedRouteStrokes.contains(nodeStroke)) {
  // routesNumberNode *=
  // RoutesConfigurations.getConfigurationsNumber(((RoadStrokeForRoutes)
  // nodeStroke).getCarriedObjectsNumber());
  // } else {
  // routesNumberNode = 0;
  // break;
  // }
  // }
  // if (!routesNumberNodes.containsKey(routesNumberNode)) {
  // routesNumberNodes.put(routesNumberNode, new HashSet<StrokeNode>());
  // }
  // routesNumberNodes.get(routesNumberNode).add(node);
  // }
  //
  // //this list reverses the key set to get the nodes from the most complex to
  // the least
  // List<Integer> routesNumberNodesKeys = new
  // ArrayList<Integer>(routesNumberNodes.keySet());
  // Collections.sort(routesNumberNodesKeys, Collections.reverseOrder());
  //
  // //this list returns the ordered strokes
  // List<RoadStrokeForRoutes> strokesAlreadySeen = new
  // ArrayList<RoadStrokeForRoutes>();
  // for (Integer key : routesNumberNodesKeys) {
  // for (StrokeNode node : routesNumberNodes.get(key)) {
  // for (Stroke stroke :
  // Iterables.concat(node.getInStrokes(),node.getOutStrokes())) {
  // if (selectedRouteStrokes.contains(stroke) &&
  // !strokesAlreadySeen.contains(stroke)) {
  // strokesAlreadySeen.add((RoadStrokeForRoutes) stroke);
  // }
  // }
  // }
  // }
  //
  // for (int i = 0 ; i < strokesAlreadySeen.size() ; i++) {
  // strokes[i] = strokesAlreadySeen.get(i);
  // LOGGER.info(i + " -> " + strokes[i] + " (" +
  // strokes[i].getCarriedObjectsNumber() + ")");
  // }
  //
  // return strokes;
  // }
  //
  //
  // /**
  // * This function uses a dichotomic approach to solve the CSP problem
  // * @param strokes, the variables of our CSP problem
  // * @param nodeOccurrences, a map between a node and the number of its
  // related strokes
  // * @param minScore, an upper bound of the optimal score
  // * @param minConfigSolution, the configuration of the solution
  // * @return the optimal score
  // */
  // private static int dichotomicBacktracking(RoadStrokeForRoutes[] strokes,
  // Map<StrokeNode, Integer> nodeOccurrences, List<Integer> minConfigSolution)
  // {
  // int score;
  // int count = 0;
  // int i = 0;
  // int dichotomicMax = 1000;
  // int dichotomicMin = 0;
  // boolean first = true;
  // Map<StrokeNode, Integer> minNodeOccurrences = null;
  //
  // while (count < 1260000000 && dichotomicMin + 1 < dichotomicMax) {
  // count ++;
  // if (count%500000 == 0) {
  // LOGGER.info(" dichotomicMin = " + dichotomicMin + " et dichotomicMax = " +
  // dichotomicMax);
  // System.out.print("strokes : ");
  // for (int j = 0 ; j < strokes.length ;j++) {
  // System.out.print(" + " + strokes[j].getRouteConfigurationIndex() + " (" +
  // strokes[j].getRoutesPositions() + ")");
  // }
  // System.out.println("\n");
  // }
  // if (strokes[0].getRoutesPositions() == null) {
  // dichotomicMin = (dichotomicMax + dichotomicMin)/2;
  // LOGGER.info("on n'a pas trouvé de solutions, on augmente le min à " +
  // dichotomicMin);
  // //we didn' find any solutions
  // for (StrokeNode node : nodeOccurrences.keySet()) {
  // nodeOccurrences.put(node, minNodeOccurrences.get(node));
  // }
  // for (int j = 0 ;j <strokes.length ; j++) {
  // strokes[j].setRouteConfiguration(minConfigSolution.get(i));
  // }
  // i = strokes.length-1;
  // strokes[i].backtrack(nodeOccurrences);
  //
  // } else if (i >= strokes.length) {
  // score = (int) strokes[strokes.length-1].getStrokesScore();
  // LOGGER.debug("+++++++++on est tt en bas avec i = " + i +" et position = " +
  // strokes[strokes.length-1].getRouteConfigurationIndex() + " et score = " +
  // score );
  // if (first) {
  // System.out.println("Le premier score trouvé est " + score);
  // first = false;
  // }
  // if (score <= (dichotomicMin + dichotomicMax)/2) {
  // dichotomicMax = score;
  // LOGGER.info("on a trouvé une meilleure solution, on réduit le max à " +
  // dichotomicMax);
  // if (minNodeOccurrences == null) {
  // minNodeOccurrences = new HashMap<>(nodeOccurrences);
  // }
  // //we found a solution
  // minConfigSolution.clear();;
  // for (RoadStrokeForRoutes stroke : strokes) {
  // minConfigSolution.add(stroke.getRouteConfigurationIndex());
  // }
  // }
  // i = strokes.length-1;
  // strokes[i].backtrack(nodeOccurrences);
  // } else if (strokes[i].getRoutesPositions()== null) {
  // LOGGER.debug("-> -> -> bloqué, il faut remonter pour i = " + i );
  // strokes[i].resetRouteConfiguration();
  // i--;
  // strokes[i].backtrack(nodeOccurrences);
  // } else {
  // LOGGER.debug("stroke i = " + i + " et position = " +
  // strokes[i].getRouteConfigurationIndex() + " (" +
  // RoutesConfigurations.getConfiguration(strokes[i].getCarriedObjectsNumber(),
  // strokes[i].getRouteConfigurationIndex()) +")");
  // score = (int) strokes[i].updateScores((i==0) ? null : strokes[i-1],
  // nodeOccurrences);
  // LOGGER.debug("updateScores = " + score);
  // if (score <= (dichotomicMin + dichotomicMax)/2) {
  // i++;
  // } else {
  // strokes[i].backtrack(nodeOccurrences);
  // LOGGER.debug("\tscore trop haut (cf dichotomicMin = " + dichotomicMin +
  // ")");
  // }
  // }
  // }
  // return dichotomicMin;
  // }
  //
  //
  //
  // /**
  // * This function uses a threshold approach to solve the CSP problem
  // * @param strokes, the variables of our CSP problem
  // * @param nodeOccurrences, a map between a node and the number of its
  // related strokes
  // * @param minScore, an upper bound of the optimal score
  // * @param minConfigSolution, the configuration of the solution
  // * @param threshold, a boolean to know if we use a threshold or not
  // * @return the optimal score
  // */
  // private static double thresholdBacktracking(RoadStrokeForRoutes[] strokes,
  // Map<StrokeNode, Integer> nodeOccurrences, List<Integer> minConfigSolution,
  // boolean threshold) {
  //
  // int[] treeDepth = new int[strokes.length];
  // double score;
  // int count = 0;
  // int i = 0;
  // double minScore = minInitialization(strokes, new
  // HashMap<>(nodeOccurrences), minConfigSolution);
  //
  // for (int j = 0 ;j < strokes.length ;j++) {
  // strokes[j].resetRouteConfiguration();
  // }
  //
  // while(count<1260000000 && strokes[0].getRoutesPositions() != null) {
  // count ++;
  // if (count%500000 == 0) {
  // LOGGER.info(" minScore = " + minScore);
  // System.out.print("strokes : ");
  // for (int j = 0 ; j < strokes.length ;j++) {
  // System.out.print(" + " + strokes[j].getRouteConfigurationIndex() + " (" +
  // strokes[j].getRoutesPositions() + ")");
  // }
  // System.out.println("\n");
  // }
  // if (i >= strokes.length) {
  // score = strokes[strokes.length-1].getStrokesScore();
  // LOGGER.debug("+++++++++on est tt en bas avec i = " + i +" et position = " +
  // strokes[strokes.length-1].getRouteConfigurationIndex() + " et score = " +
  // score );
  // if (score < minScore) {
  // LOGGER.debug("on trouve une nouvelle solution !");
  // minScore = score;
  // minConfigSolution.clear();
  // for (RoadStrokeForRoutes stroke : strokes) {
  // minConfigSolution.add(stroke.getRouteConfigurationIndex());
  // }
  // }
  // i = strokes.length-1;
  // treeDepth[i]++;
  // strokes[i].backtrack(nodeOccurrences);
  // } else if (strokes[i].getRoutesPositions()== null) {
  // LOGGER.debug("-> -> -> bloqué, il faut remonter pour i = " + i );
  // strokes[i].resetRouteConfiguration();
  // i--;
  // strokes[i].backtrack(nodeOccurrences);
  // } else {
  // LOGGER.debug("stroke i = " + i + " et position = " +
  // strokes[i].getRouteConfigurationIndex() + " (" +
  // RoutesConfigurations.getConfiguration(strokes[i].getCarriedObjectsNumber(),
  // strokes[i].getRouteConfigurationIndex()) +")");
  // score = strokes[i].updateScores((i==0) ? null : strokes[i-1],
  // nodeOccurrences);
  // LOGGER.debug("updateScores = " + score);
  // if (!threshold || score < minScore) {
  // i++;
  // } else {
  // treeDepth[i]++;
  // strokes[i].backtrack(nodeOccurrences);
  // LOGGER.debug("\tscore trop haut (cf minScore = " + minScore + ")");
  // }
  // }
  // }
  // System.out.println("treeDepth:");
  // for (int j = 0 ; j < treeDepth.length ; j++) {
  // if (treeDepth[j] != 0)
  // System.out.println(j + " -> " +treeDepth[j]);
  // }
  // return minScore;
  // }
  //
  //
  //
  // /**
  // * this function tries to find the best initialization of the minimum score
  // thanks to a stochastic algorithm
  // * @param strokes, the variables of our CSP problem
  // * @param nodeOccurrences, a map between a node and the number of its
  // related strokes
  // * @param minConfigSolution related to the bbest score
  // * @return the best score we found
  // */
  // private static double minInitialization(RoadStrokeForRoutes[] strokes,
  // Map<StrokeNode, Integer> nodeOccurrences, List<Integer> minConfigSolution)
  // {
  // int count = 0;
  // double score;
  // double minScore = 10000;
  // int i = 0;
  // // we choose arbitrarily a threshold
  // while( count < 500000) {
  // count ++;
  // if (i >= strokes.length) {
  // score = strokes[strokes.length-1].getStrokesScore();
  // if (score < minScore) {
  // minScore = score;
  // minConfigSolution.clear();
  // for (RoadStrokeForRoutes stroke : strokes) {
  // minConfigSolution.add(stroke.getRouteConfigurationIndex());
  // }
  // }
  // //we restart everything
  // i = 0;
  // for (StrokeNode node : nodeOccurrences.keySet()) {
  // nodeOccurrences.put(node, node.getInStrokes().size() +
  // node.getOutStrokes().size());
  // }
  // } else if (strokes[i].getRoutesPositions()== null) {
  // System.out.println("problem pour i = " + i +
  // " et getCarriedObjectsNumber = " + strokes[i].getCarriedObjectsNumber() +
  // " et getRouteConfigurationIndex =  " +
  // strokes[i].getRouteConfigurationIndex());
  // } else {
  // score = strokes[i].updateScores((i==0) ? null : strokes[i-1],
  // nodeOccurrences);
  // if (score >= minScore) {
  // //we restart everything
  // i = -1;
  // for (StrokeNode node : nodeOccurrences.keySet()) {
  // nodeOccurrences.put(node, node.getInStrokes().size() +
  // node.getOutStrokes().size());
  // }
  // }
  // i++;
  // }
  // if (i < strokes.length) {
  // // we choose a random configuration every time
  // strokes[i].randomRouteConfiguration();
  // }
  // }
  // LOGGER.info("minInitialization = " + minScore);
  // return minScore;
  // }
  //

}
