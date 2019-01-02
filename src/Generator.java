
import java.lang.reflect.Array;
import java.util.*;

//TODO: Erweitern um die Ausschreiben aller generierten Levels mit der Lösung in JSON
public class Generator {

    private final static int HOLE = -1;
    private final static int WALL = 1;
    private final static int MIRROR = 5; //59 - \ - or 57 - / -

    private final static int UP = 8;
    private final static int DOWN = 2;
    private final static int LEFT = 4;
    private final static int RIGHT = 6;

    //define the grid
    private int gridWidth;
    private int gridHeight;
    private int holes; //-1

    //define the elements
    private int walls;
    private int mirrors;
    private int additiveBlocks;

    //define the source(s) and goal
    private int[] sources;
    private int[] sourcesPositions;
    private int[] goals;
    private int[] goalsPositions;

    //the empty grid
    private int[][] grid;
    private int maxElement; //N-th

    public Generator() {
        //default constructor for testing purposes
    }

    public Generator (int gridWidth, int gridHeight, int[][] grid) {
        //constructor for further using for finding solutions to hand made levels
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.grid = grid;
    }

    //for the finding solution(s) of a grid with given sources and goal(s) and given amount of elements
    public Generator (int gridWidth, int gridHeight, int[][] grid, int mirrors) {
        this(gridWidth, gridHeight, grid);
        this.mirrors = mirrors;
    }

    /**
     * NB! this.grid = new String[gridHeight][gridWidth];
     */
    public Generator(int gridWidth, int gridHeight, int holes, int walls, int mirrors, int additiveBlocks, int[] sources, int[] goals) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.holes = holes;
        this.walls = walls;
        this.mirrors = mirrors;
        this.additiveBlocks = additiveBlocks;
        this.sources = sources;
        this.goals = goals;
        this.maxElement = gridHeight * gridWidth;
        this.grid = new int[gridHeight][gridWidth];
    }

    //for generating new levels
    public Generator(int gridWidth, int gridHeight, int holes, int walls, int mirrors, int[] sources, int[] goals) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.holes = holes;
        this.walls = walls;
        this.mirrors = mirrors;
        this.sources = sources;
        this.goals = goals;
        this.maxElement = gridHeight * gridWidth;
        this.grid = new int[gridHeight][gridWidth];
    }

    /** Further methods are used for automatic level generation   **/

    public int[][] generateEmptyLevel() {

        fillTheEmptyGrid();

        for (int i = 0; i < holes; i++) {
            int position = randomFreePlace();
            placeElementAtPosition(position, HOLE);
            //System.out.println("A hole was set on position " + position);
        }
        for (int i = 0; i < walls; i++) {
            int position = randomFreePlace();
            placeElementAtPosition(position, WALL);
            //System.out.println("A wall was set on position " + position);
        }

        placeSourcesAndGoals();

        return this.grid;
    }

    public void setSourceDirections() {
        for (int i = 0; i < sources.length; i++) {
            int sourceData = sources[i];
            int sourcePosition = sourcesPositions[i];
            ArrayList<Integer> directions = findPossibleLaserDirections(sourcePosition);
            Random rand = new Random();
            int sourceDirection = directions.get(rand.nextInt(directions.size())); //a random direction from all possible ones
            int sourceWithADirection = buildASource(sourceData, sourceDirection);
            sources[i] = sourceWithADirection;
            placeElementAtPosition(sourcePosition, sourceWithADirection);
        }
        //System.out.println("This level was generated: " + "\n" + gridToString(this.grid));
    }

    private void fillTheEmptyGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                this.grid[i][j] = 0;
            }
        }
    }

    private void placeSourcesAndGoals() {
        sourcesPositions = new int[sources.length];
        goalsPositions = new int[goals.length];
        for (int i = 0; i < sources.length; i++) {
            int sourcePosition = randomFreePlace();
            placeElementAtPosition(sourcePosition, sources[i]);
            sourcesPositions[i] = sourcePosition;
            //System.out.println("A source was set on " + sourcePosition);
        }
        for (int i = 0; i < goals.length; i++) {
            int goalPosition = randomFreePlace();
            placeElementAtPosition(randomFreePlace(), goals[i]);
            goalsPositions[i] = goalPosition;
            //System.out.println("A goal was set on " + goalPosition);
        }

    }

    /** Further methods are used for filling the needed fields of a Generator if the prepared grid was given **/

    public void findSourcesAndGoals() {
        //TODO: OPTIONAL to implement (better after changing Strings to Integer )
    }

      /*  public String[][] addMirror(int mirrors, int index, String[][] lastGrid, ArrayList<Integer> emptyCells) {
            if (mirrors == 0) {
                System.out.println("Mirrors == 0. Returned the last grid: " + "\n" + gridToString(lastGrid) + "\n");
                return lastGrid;
            }
            //ArrayList<Integer> emptyCells = findAllEmptyCells(lastGrid);
            else if (index >= emptyCells.size() - 1) {
                System.out.println("No more empty cells. Returned the last grid: " + "\n" + gridToString(lastGrid) + "\n");
                return lastGrid;
            } else {
                String[][] currentGrid = copyOf2DArray(this.grid);
                placeElementAtPosition(emptyCells.get(index), "M7", lastGrid);
                if (!allSourcesReachedTheGoal(currentGrid)) {
                    for (int i = mirrors; i > 1; i--) {
                    System.out.println("The goal is not reached yet, we go deeper with" + "\n" +
                            gridToString(currentGrid) + "\n");
                        return addMirror(mirrors - 1, index + 1, currentGrid, emptyCells); //lastGrid?
                    }
                } else {
                    System.out.println("All sources reached the goal. Returned the current grid: " + "\n" + gridToString(currentGrid));
                    return currentGrid;
                }
            }

            System.out.println("Why do I get this???");
            return lastGrid;
        } */

        //TODO: OPTIONAL Erweitern um die AdditiveBlöcke
    public ArrayList<int[][]> findAllSolutionsIterativ(int mirrors, int[][] grid, boolean exactNumberOfPlayableElements) {
        //At first we check whether this level isn't already solved without any playable elements
            if (allSourcesReachedTheGoal(grid)) {
                ArrayList<int[][]> gridIsAlreadySolved = new ArrayList<int[][]>();

                //System.out.println("Grid is already solved!");
                return gridIsAlreadySolved;
            }

            ArrayList<int[][]> allSolutions = new ArrayList<>();

            ArrayList<int[][]> allPossibleCombinations = new ArrayList<>();
            allPossibleCombinations.add(grid);

            int lastIndex = 0;
            for (int i = 0; i < mirrors; i++) {
                if (i > 0 && mirrors != 1) { //we need only combinations where ALL mirrors are placed, so we delete the other ones
                    lastIndex = allPossibleCombinations.size() - 1;
                }

                ArrayList<int[][]> newCombinations = new ArrayList<>();
                ArrayList<int[][]> combinationsToAdd = new ArrayList<>();
                for (int combination = 0; combination < allPossibleCombinations.size(); combination++) {
                    newCombinations.addAll(allPositionsForAnElement(57, allPossibleCombinations.get(combination)));
                    newCombinations.addAll(allPositionsForAnElement(59, allPossibleCombinations.get(combination)));
                }
                for (int[][] combination : newCombinations) {
                    if (allSourcesReachedTheGoal(combination)) { //if there is a solution, we don't need to proceed on this one
                        allSolutions.add(combination);
                    } else {
                        combinationsToAdd.add(combination);
                    }
                }
                //If we are not in last iteration and already have found some solutions
                if (i < mirrors-1 && allSolutions.size() > 0) {
                    if (exactNumberOfPlayableElements) {
                        ArrayList<int[][]> emptyList = new ArrayList<>();
                        return emptyList;
                    } else {
                        break;
                    }

                }
                allPossibleCombinations.subList(0, lastIndex+1).clear(); //we remove the old ones (or the empty grid at index 0)
                allPossibleCombinations.addAll(combinationsToAdd);
                //System.out.println("Mirrors: " + (i+1));
                allPossibleCombinations = removeRepeatingCombinations(allPossibleCombinations);
                //System.out.println("After removing all repeating combinations: " + allPossibleCombinations.size());
            }

            /* for (int[][] combination : allPossibleCombinations) {
                if (allSourcesReachedTheGoal(combination)) {
                    allSolutions.add(combination);
                }
            } */

            //JUST FOR TESTING! REMOVE WHEN READY
             /* if (allSolutions.size() > 0) {
                 for (int[][] solution : allSolutions) {
                     System.out.println(gridToString(solution));
                 }
                for (int[][] combination : allPossibleCombinations) {
                    if (allSourcesReachedTheGoal(combination)) {
                        allSolutions.add(combination);
                    }
                }
            } */

            ArrayList<int[][]> uniqueSolutions = removeRepeatingCombinations(allSolutions);
           /* for (int i = 0; i < allSolutions.size(); i++) {
                for(int j = i+1; j < allSolutions.size(); j++) {
                    if (arrayIsEqual(allSolutions.get(i), allSolutions.get(j))) {
                        uniqueSolutions.remove(allSolutions.get(i));
                    }
                }
            } */

            //System.out.println("All combinations: " + (allPossibleCombinations.size()-1));
            //System.out.println("Solutions found: " + allSolutions.size());
            //System.out.println("All " + uniqueSolutions.size() + " unique solutions: " + "\n");
             if (uniqueSolutions.size() > 1) {
                 System.out.println("Found a level with more than one solution. Solutions: ");
                for (int j = 0; j < uniqueSolutions.size(); j++) {
                    System.out.println(gridToString(uniqueSolutions.get(j)) + "\n");
                }
            }

            return uniqueSolutions;
        }

    public ArrayList<int[][]> allPositionsForAnElement (int element, int[][]grid){
        ArrayList<int[][]> allPositions = new ArrayList<>();
        ArrayList<Integer> emptyPlaces = findAllEmptyCells(grid);

        for (Integer emptyPlacePosition : emptyPlaces) {
            int[][] currentGrid = copyOf2DArray(grid);
            placeElementAtPosition(emptyPlacePosition, element, currentGrid);
            allPositions.add(currentGrid);
        }

        return allPositions;
    }

    public boolean arrayIsEqual(int[][] array1, int[][] array2) {
        for (int row = 0; row < array1.length; row++) {
            for (int col = 0; col < array1[row].length; col++) {
                if (array1[row][col] != (array2[row][col])) {
                    return false;
                }
            }
        }
        return true;
    }

   /* public int[][] findASolution() {
        return addMirror(mirrors, 0, this.grid, findAllEmptyCells());
   } */


   //TODO intern mit Integern arbeiten? Soll schneller sein als mit Strings
   public ArrayList<int[][]> generateAllLevelsWithOneSolutionIterativ() {
       ArrayList<int[][]> allLevels = new ArrayList<>();
       ArrayList<int[][]> allUniqueLevels = new ArrayList<>();
       fillTheEmptyGrid();
       allLevels.add(this.grid); // we add an empty grid at first

       int lastIndex = 0;

       for (int i = 0; i < holes; i++) {
           ArrayList<int[][]> newCombinations = new ArrayList<>();
           if (i > 0 && holes != 1) { //we need only combinations where ALL holes are placed, so we delete the other ones
               lastIndex = allLevels.size() - 1;
           }
           for (int combination = 0; combination < allLevels.size(); combination++) {
               newCombinations.addAll(allPositionsForAnElement(-1, allLevels.get(combination)));
           }
           allLevels.addAll(newCombinations);
           allLevels.subList(0, lastIndex+1).clear(); //we remove the old ones (or the empty grid at index 0)
           System.out.println("After clearing not needed holes there are " + allLevels.size() + " combinations");
       }
       System.out.println("After adding all holes there are " + allLevels.size() + " combinations");
       //allLevels.subList(0, lastIndex+1).clear();
       //System.out.println("After clearing not needed holes there are " + allLevels.size() + " combinations");
       allLevels = removeRepeatingCombinations(allLevels);
       System.out.println("After removing all repeating combinations there are " + allLevels.size() + " combinations" + "\n");

      /* for (String[][] level: allLevels) {
           System.out.println(gridToString(level));
       } */

       lastIndex = allLevels.size() - 1;
       for (int i = 0; i < walls; i++) {
           if (i > 0 && walls != 1) { //if there is only one wall, we can't remove anything
               allLevels.subList(0, lastIndex+1).clear();
               System.out.println("After clearing all holes/not needed walls there are " + allLevels.size() + " combinations");
               lastIndex = allLevels.size() - 1;
           }
           ArrayList<int[][]> newCombinations = new ArrayList<>();
           for (int combination = 0; combination < allLevels.size(); combination++) {
               newCombinations.addAll(allPositionsForAnElement(1, allLevels.get(combination)));
               //TODO: Die Gleichheit prüfen
           }

           allLevels.addAll(newCombinations);
           allLevels.subList(0, lastIndex+1).clear(); //we remove the grids with just holes for not dealing with them in further iteration steps
           System.out.println("After clearing not needed walls there are " + allLevels.size() + " combinations");
       }
       System.out.println("After adding all walls there are " + allLevels.size() + " combinations");
       allLevels.subList(0, lastIndex+1).clear();
       System.out.println("After clearing not needed walls there are " + allLevels.size() + " combinations");
       allLevels = removeRepeatingCombinations(allLevels);
       System.out.println("After removing all repeating combinations there are " + allLevels.size() + " combinations"  + "\n");

       lastIndex = allLevels.size() - 1;
       for (int i = 0; i < sources.length; i++) {
           if (i == sources.length-1 && sources.length != 1) { //if there is only one source, we can't remove anything
               allLevels.subList(0, lastIndex+1).clear();
               System.out.println(" NB! After clearing all holes and walls there are " + allLevels.size() + " combinations");
               lastIndex = allLevels.size() - 1;
           }
           ArrayList<int[][]> newCombinations = new ArrayList<>();
           for (int combination = 0; combination < allLevels.size(); combination++) {
               for (int source : sources) {

                   ArrayList<Integer> emptyPlaces = findAllEmptyCells(allLevels.get(combination));
                   ArrayList<int[][]> allPositions = new ArrayList<>();

                   for (Integer emptyPlacePosition : emptyPlaces) {
                       for (int direction : findPossibleLaserDirections(emptyPlacePosition)) {
                           int[][] currentGrid = copyOf2DArray(allLevels.get(combination));
                           placeElementAtPosition(emptyPlacePosition, buildASource(source, direction), currentGrid);
                           allPositions.add(currentGrid);
                       }
                   }
                   newCombinations.addAll(allPositions);
                   newCombinations = removeRepeatingCombinations(newCombinations);
                   System.out.println("After removing all repeating combinations in this step there are " + newCombinations.size() + " new combinations");
               }
           }
           if (i == 0) {
               allLevels.subList(0, lastIndex+1).clear(); //we remove the grids with just holes and walls
           }
           allLevels.addAll(newCombinations);
       }
       System.out.println("After adding all sources there are " + allLevels.size() + " combinations");
       allLevels.subList(0, lastIndex+1).clear();
       System.out.println("After clearing not needed sources there are " + allLevels.size() + " combinations");
       allLevels = removeRepeatingCombinations(allLevels);
       System.out.println("After removing all repeating combinations there are " + allLevels.size() + " combinations");

       //TODO Remove all not playable levels
       for (int combination = 0; combination < allLevels.size(); combination++) {

       }

       lastIndex = allLevels.size() - 1;
       for (int i = 0; i < goals.length; i++) {
           if (i == goals.length-1 && goals.length != 1) { //if there is only one source, we can't remove anything
               allLevels.subList(0, lastIndex+1).clear();
               System.out.println(" NB! After clearing all sources there are " + allLevels.size() + " combinations");
               lastIndex = allLevels.size() - 1;
           }
           ArrayList<int[][]> newCombinations = new ArrayList<>();
           for (int combination = 0; combination < allLevels.size(); combination++) {
               for (int goal : goals) {
                   newCombinations.addAll(allPositionsForAnElement(goal, allLevels.get(combination)));
               }

           }
           allLevels.addAll(newCombinations);
       }
       System.out.println("After adding all goals there are " + allLevels.size() + " combinations");
       allLevels.subList(0, lastIndex+1).clear();
       System.out.println("After clearing not needed goals there are " + allLevels.size() + " combinations");
       allLevels = removeRepeatingCombinations(allLevels);
       System.out.println("After removing all repeating combinations there are " + allLevels.size() + " combinations");

       //TODO make sure that there is no garbage in the list
       System.out.println("All possible levels found: " + (allLevels.size() - 1));

       //now we are going to check all created levels and their solutions
       for (int level = 0; level < allLevels.size(); level++) {
           ArrayList<int[][]> allSolutions = new ArrayList<>();

           ArrayList<int[][]> allPossibleCombinations = new ArrayList<>();
           allPossibleCombinations.add(allLevels.get(level));
           for (int i = 0; i < mirrors; i++) {
               ArrayList<int[][]> newCombinations = new ArrayList<>();
               for (int combination = 0; combination < allPossibleCombinations.size(); combination++) {
                   newCombinations.addAll(allPositionsForAnElement(57, allPossibleCombinations.get(combination)));
                   newCombinations.addAll(allPositionsForAnElement(59, allPossibleCombinations.get(combination)));
               }
               allPossibleCombinations.addAll(newCombinations);
           }

           for (int[][] combination : allPossibleCombinations) {
               if (allSourcesReachedTheGoal(combination)) {
                   allSolutions.add(combination);
               }
           }

           ArrayList<int[][]> uniqueSolutions = new ArrayList<>(allSolutions);
           for (int i = 0; i < allSolutions.size(); i++) {
               for(int j = i+1; j < allSolutions.size(); j++) {
                   if (arrayIsEqual(allSolutions.get(i), allSolutions.get(j))) {
                       uniqueSolutions.remove(allSolutions.get(i));
                   }
               }
           }

           System.out.println("All combinations for the level: " + (allPossibleCombinations.size()-1));
           System.out.println("Solutions found: " + allSolutions.size());
           System.out.println("All " + uniqueSolutions.size() + " unique solutions: " + "\n");

           if (uniqueSolutions.size() == 1) {
               allUniqueLevels.add(uniqueSolutions.get(0));
               System.out.println(gridToString(uniqueSolutions.get(0)) + "\n");
           }

          /* for (int j = 0; j < uniqueSolutions.size(); j++) {
               System.out.println(gridToString(uniqueSolutions.get(j)) + "\n");
           } */
       }
       return allUniqueLevels;
   }

   public void generateRandomLevels(boolean exactNumberOfPlayableElements) {
       int levelCount = 0;
       int[] originSources = Arrays.copyOf(this.sources, sources.length);

       while(true) {
            this.sources = Arrays.copyOf(originSources, originSources.length);
            this.grid = generateEmptyLevel();
            setSourceDirections();
            ArrayList<int[][]> allSolutions = findAllSolutionsIterativ(mirrors, this.grid, exactNumberOfPlayableElements);
            if (allSolutions.size() == 1) {
                if (!arrayIsEqual(allSolutions.get(0), this.grid)) {
                    System.out.println("Found a level no." + levelCount + " with an unique solution: ");
                    System.out.println(gridToString(allSolutions.get(0)));
                }
            } else if (allSolutions.size() == 0) {
                //System.out.println("No solutions to the level was found");
            } else {
                //System.out.println("This level had more than one solution");
            }
            levelCount++;
        }

   }

    private ArrayList<int[][]> removeRepeatingCombinations(ArrayList<int[][]> listToClear) {
        ArrayList<int[][]> uniqueCombinations = new ArrayList<>(listToClear);
        for (int i = 0; i < listToClear.size(); i++) {
            for(int j = i+1; j < listToClear.size(); j++) {
                if (arrayIsEqual(listToClear.get(i), listToClear.get(j))) {
                    uniqueCombinations.remove(listToClear.get(i));
                }
            }
        }
        return uniqueCombinations;
    }

    /**
     * For the first time just one goal without extra sources that aren't needed (e.g. source C for AB goal)
     * NB! a primitive version without checking whether the goal is not only reached but also filled correctly
     */
    public boolean allSourcesReachedTheGoal() {
        return allSourcesReachedTheGoal(this.grid);
    }

    public boolean allSourcesReachedTheGoal(int[][] grid) {
        boolean reached = false;

        for (int goalIndex = 0; goalIndex < goals.length; goalIndex++) {
            reached = false;
            int result = 0;

            int goalPosition = goalsPositions[goalIndex];
            int goalData = goals[goalIndex];

            for (int i = 0; i < sourcesPositions.length; i++) {
                int endPositionForASource = endLaserPosition(sourcesPositions[i], getSourceDirection(sources[i]), grid);
                if (endPositionForASource == goalPosition) {
                    result += getSourceData(sources[i]);
                } //else {
                    //return false; //We don't do it, because there can be sources for another goal or not needed sources
                //}
            }

            if (result == goalData) {
                reached = true;
            }
        }

        return reached;
    }

    public ArrayList<Integer> findAllEmptyCells() {
        return findAllEmptyCells(this.grid);
    }

    public ArrayList<Integer> findAllEmptyCells(int[][] grid) {
        ArrayList<Integer> emptyCells = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int currentPosition = i * grid[i].length + j;
                if (grid[i][j] == 0) {
                    emptyCells.add(currentPosition);
                }
            }
        }

        return emptyCells;
    }

    public int buildASource(int sourceData, int direction) {
     return sourceData + direction;
    }

    public String rotateMirror(String currentMirror) {
        if (currentMirror.equals("M7")) {
            return "M9";
        } else if (currentMirror.equals("M9")){
            return "M7";
        }
        return null; //FIXME catch this error
    }

    public int getSourceDirection(int source) {
        int sourceDirection = source % 100;
        if (sourceDirection != 2 && sourceDirection != 4 && sourceDirection !=6 && sourceDirection !=8) {
            return Integer.MIN_VALUE;
        }
        return sourceDirection;

    }

    public int getSourceData(int source) {
        int data = ((source + 91) / 100 ) * 100;
        if (data < 100 || data > 900 ) { //FIXME maybe there is a better solution
            return Integer.MIN_VALUE;
        }
        return data;
    }

    //FIXME: make it private after testing
    public int endLaserPosition(int position, int direction, int[][] grid) {
        switch(direction) {
            case UP: return getStopPositionAbove(position, grid);
            case DOWN: return getStopPositionBelow(position, grid);
            case LEFT: return getStopPositionOnLeft(position, grid);
            case RIGHT: return getStopPositionOnRight(position, grid);
            default: return Integer.MIN_VALUE; //Some error happened!
        }
    }

    public int endLaserPosition(int position, int direction) {
        return endLaserPosition(position, direction, this.grid);
    }

    private int getStopPositionOnLeft(int position, int[][] grid) { //if the laser shoot to the left
        int[] rowPositions = findTheRowPositions(position);
        for(int i = rowPositions.length-1; i >= 0; i--) {
            if (rowPositions[i] < position) { //only if it on the left from the position
                int currentElement = getElementAtPosition(rowPositions[i], grid);
                if (currentElement == 57 || currentElement == 59 ) { //if there is a mirror on the way
                    if ((currentElement-50) == 7) { //if mirror stands like this: /
                        return getStopPositionBelow(rowPositions[i], grid);
                    } else { //if mirror stands like this: \
                        return getStopPositionAbove(rowPositions[i], grid);
                    }
                }
                else if (currentElement != 0 && currentElement != -1 ) { //if there is something on the way (not an empty cell, not a hole)
                    return rowPositions[i];
                }
            }

        }
        return rowPositions[0]; //no mirrors or obstacles on the way, return the leftest element
    }

    private int getStopPositionOnLeft(int position) { //if the laser shoot to the left
        return getStopPositionOnLeft(position, this.grid);
    }

    private int getStopPositionOnRight(int position, int[][] grid) { //if the laser shoot to the right
        int[] rowPositions = findTheRowPositions(position);
        for(int i = 0; i < rowPositions.length; i++) {
            if (rowPositions[i] > position) { //only if it on the right from the position
                int currentElement = getElementAtPosition(rowPositions[i], grid);
                if (currentElement == 57 || currentElement == 59 ) { //if there is a mirror on the way
                    if ((currentElement-50) == 7) { //if mirror stands like this: /
                        return getStopPositionAbove(rowPositions[i], grid);
                    } else { //if mirror stands like this: \
                        return getStopPositionBelow(rowPositions[i], grid);
                    }
                }
                else if (currentElement != 0 && currentElement != -1 ) { //if there is something on the way (not an empty cell, not a hole)
                    return rowPositions[i];
                }
            }

        }
        return rowPositions[rowPositions.length-1]; //no mirrors or obstacles on the way, return the leftest element
    }

    private int getStopPositionOnRight(int position) { //if the laser shoot to the right
        return getStopPositionOnRight(position, this.grid);
    }

    private int getStopPositionBelow(int position, int[][] grid) { //if the laser shoot down
        int[] columnPositions = findTheColumnPositions(position);
        for(int i = 0; i < columnPositions.length; i++) {
            if (columnPositions[i] > position) { //only if it below the position
                int currentElement = getElementAtPosition(columnPositions[i] ,grid);
                if (currentElement == 57 || currentElement == 59 ) { //if there is a mirror on the way
                    if ((currentElement-50) == 7) { //if mirror stands like this: /
                        return getStopPositionOnLeft(columnPositions[i], grid);
                    } else { //if mirror stands like this: \
                        return getStopPositionOnRight(columnPositions[i], grid);
                    }
                }
                else if (currentElement != 0 && currentElement != -1 ) { //if there is something on the way (not an empty cell, not a hole)
                    return columnPositions[i];
                }
            }

        }
        return columnPositions[columnPositions.length-1]; //no mirrors or obstacles on the way, return the leftest element
    }

    private int getStopPositionBelow(int position) { //if the laser shoot down
        return getStopPositionBelow(position, this.grid);
    }

    private int getStopPositionAbove(int position, int[][] grid) { //if the laser shoot up
        int[] columnPositions = findTheColumnPositions(position);
        for(int i = columnPositions.length-1; i >= 0; i--) {
            if (columnPositions[i] < position) { //only if it above the position
                int currentElement = getElementAtPosition(columnPositions[i], grid);
                if (currentElement == 57 || currentElement == 59 ) { //if there is a mirror on the way
                    if ((currentElement-50) == 7) { //if mirror stands like this: /
                        return getStopPositionOnRight(columnPositions[i], grid);
                    } else { //if mirror stands like this: \
                        return getStopPositionOnLeft(columnPositions[i], grid);
                    }
                }
                else if (currentElement != 0 && currentElement != -1 ) { //if there is something on the way (not an empty cell, not a hole)
                    return columnPositions[i];
                }
            }

        }
        return columnPositions[0]; //no mirrors or obstacles on the way, return the leftest element
    }

    private int getStopPositionAbove(int position) { //if the laser shoot up
        return getStopPositionAbove(position, this.grid);
    }

    /**
     * Returns an array of boolean for all directions in the following order: Up, Down, Left, Right
     * @return an array of boolean for all directions
     */
    private boolean[] findLaserDirections(int sourcePosition) {
        boolean[] possibleDirections = new boolean[4]; //default boolean value is false

        int[] columnPositions = findTheColumnPositions(sourcePosition);
        for(int i = 0; i < columnPositions.length; i++) {
            if (columnPositions[i] < sourcePosition) {
                possibleDirections[0] = true;
            }
            else if (columnPositions[i] > sourcePosition){
                possibleDirections[1] = true;
            }
        }

        int[] rowPositions = findTheRowPositions(sourcePosition);
        for(int i = 0; i < rowPositions.length; i++) {
            if (rowPositions[i] < sourcePosition) {
                possibleDirections[2] = true;
            }
            else if (rowPositions[i] > sourcePosition){
                possibleDirections[3] = true;
            }
        }

        return possibleDirections;
    }

    private ArrayList<Integer> findPossibleLaserDirections(int sourcePosition) {
        HashSet<Integer> possibleDirections = new HashSet<>(); //default boolean value is false

        int[] columnPositions = findTheColumnPositions(sourcePosition);
        for(int i = 0; i < columnPositions.length; i++) {
            if (columnPositions[i] < sourcePosition) {
                possibleDirections.add(8);
            }
            else if (columnPositions[i] > sourcePosition){
                possibleDirections.add(2);
            }
        }

        int[] rowPositions = findTheRowPositions(sourcePosition);
        for(int i = 0; i < rowPositions.length; i++) {
            if (rowPositions[i] < sourcePosition) {
                possibleDirections.add(4);
            }
            else if (rowPositions[i] > sourcePosition){
                possibleDirections.add(6);
            }
        }
        ArrayList<Integer> endList = new ArrayList<>(possibleDirections);

        return endList;
    }

    private int[][] copyOf2DArray(int[][] arrayToCopy) {
        int[][] copy = new int[gridHeight][gridWidth];
        for (int row = 0; row < arrayToCopy.length; row++) {
            for (int col = 0; col < arrayToCopy[row].length; col++) {
                copy[row][col] = arrayToCopy[row][col];
            }
        }
        return copy;
    }

    //FIXME: Warning if there is no more free place in the grid
    public int randomFreePlace() {
        Random random = new Random();
        int randomNum = random.nextInt(maxElement); //maxElement is Nth, range of nextInt is 0 to N-1, exactly what we need
        //while (getElementAtPosition(randomNum) == HOLE || getElementAtPosition(randomNum) == WALL) {
        while (getElementAtPosition(randomNum) != 0) {
            randomNum = random.nextInt(maxElement);
        }
        //System.out.println("Random place " + randomNum);
        return randomNum;
    }

    private void placeElementAtPosition(int positionToSetOn, int element) {
        placeElementAtPosition(positionToSetOn, element, this.grid);
    }

    private void placeElementAtPosition(int positionToSetOn, int element, int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int currentPosition = i * grid[i].length + j;
                if (currentPosition == positionToSetOn) {
                    grid[i][j] = element;
                    return;
                }
            }
        }
    }

    private void removeElementAtPosition(int positionToRemoveFrom) {
        placeElementAtPosition(positionToRemoveFrom, 0);
    }

    public int getElementAtPosition(int positionToCheck) {
        return getElementAtPosition(positionToCheck, this.grid);
    }

    public int getElementAtPosition(int positionToCheck, int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int currentPosition = i * grid[i].length + j;
                if (currentPosition == positionToCheck) {
                    return grid[i][j];
                }
            }
        }
        return Integer.MIN_VALUE; //FIXME: make it better than that
    }

    public String gridToString(int[][] grid) {
        String result = "";
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                result += "[" + grid[i][j] + "] ";
            }
            result += "\n";
        }
        return result;
    }


    private int[] findTheColumn(int position) {
        int[] column = new int[gridHeight];

        int numberInRow = position;
        while (numberInRow >= gridWidth) {
            numberInRow -= gridWidth;
        }

        for (int i = 0; i < gridHeight; i++) {
            column[i] = this.grid[numberInRow][i];
        }

        return column;
    }

    private int[] findTheColumnPositions(int position) {
        int[] column = new int[gridHeight];

        int numberInRow = position;
        while (numberInRow >= gridWidth) {
            numberInRow -= gridWidth;
        }

        for (int i = 0; i < gridHeight; i++) {
            column[i] = gridWidth * i + numberInRow;
        }

        return column;
    }

    private int[] findTheRowPositions(int position) {
        int[] row = new int[gridWidth];

        int rowNumber = 0;
        int rowMaxElement;
        for(int i = 1; i < gridHeight; i++) {
            rowMaxElement = i*gridWidth - 1;
            if (position <= rowMaxElement) {
                    break;
            }
            rowNumber++;
        }

        for(int i = 0; i < gridWidth; i++){
            row[i] = gridWidth*rowNumber + i;
        }

        return row;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] gridToSet) {
        grid = gridToSet;
    }

    public void setSources(int[] sources) {
        this.sources = sources;
    }

    public void setSourcesPositions(int[] sourcesPositions) {
        this.sourcesPositions = sourcesPositions;
    }

    public void setGoals(int[] goals) {
        this.goals = goals;
    }

    public void setGoalsPositions(int[] goalsPositions) {
        this.goalsPositions = goalsPositions;
    }

    /** THIS IS OLD DANGEROUS CODE! DON'T TOUCH IT WITHOUT A SPECIAL NEED **/

    //Assuming we have a grid that certainly has at least one solution with the given amount of playable elements
     /* public String[][] findASolution() {
        String[][] solution = copyOf2DArray(grid);

        int positionIndex = 0;
        while (!allSourcesReachedTheGoal()) {
            ArrayList<Integer> emptyCells = findAllEmptyCells();

            for (int i = 0; i < mirrors; i++) {
                for (Integer positionOfAFreePlace : emptyCells) {
                    System.out.println(gridToString(this.grid) + "\n");
                    placeElementAtPosition(positionOfAFreePlace, "M7");
                    System.out.println(gridToString(this.grid) + "\n");
                    emptyCells = findAllEmptyCells();
                }
            }
        }

        ArrayList<String[][]> allPossibleCombinations = new ArrayList<>();
    } */

}