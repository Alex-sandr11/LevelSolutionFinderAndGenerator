
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Version 0.2.0 (Internal works with integers, solution finding (up to 8 mirrors and maybe more), random level generation)
 */

public class Generator {

    private final static int HOLE = -1;
    private final static int WALL = 1;
    private final static int MIRROR = 5; //59 - \ - or 57 - / - //for future development: 55 - durchlässiger Spiegel (lässt durch UND reflectiert)

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

    //define the source(s) and goal(s)
    private int[] sources;
    private int[] sourcesPositions;
    private int[] goals;
    private int[] goalsPositions;

    //the empty grid
    private int[][] grid;
    private int maxElements; //maxElements-1 is the last N-th element

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
     * For generating new levels
     * NB! this.grid = new String[gridHeight][gridWidth];
     */
    public Generator(int gridWidth, int gridHeight, int holes, int walls, int mirrors, int[] sources, int[] goals) {
        int elementsToPlace = (holes + walls + mirrors + sources.length + goals.length);
        this.maxElements = gridHeight * gridWidth;
        if (elementsToPlace > maxElements) {
            System.out.println("There are not enough cells to place all elements!");
            System.exit(1);
        }

        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.holes = holes;
        this.walls = walls;
        this.mirrors = mirrors;
        this.sources = sources;
        this.goals = goals;

        this.grid = new int[gridHeight][gridWidth];
    }

    /**
     * Generates empty level with given amount of holes and walls and also places sources and goals
     * @return
     */
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

    /**
     * Sets sources directions by searching for possible laser directions and randomly choosing one of them.
     * We save each source with the set direction in the array of sources.
     * @return true if it was possible to set a direction for all sources
     *         false if not
     */
    public boolean setSourceDirections() {

        if (!borderRowsAndColumnsAreUsed()) {
            return false;
        }

        for (int i = 0; i < sources.length; i++) {
            int sourceData = sources[i];
            int sourcePosition = sourcesPositions[i];
            ArrayList<Integer> directions = findPossibleLaserDirections(sourcePosition);
            int sourceDirection;
            if (directions.isEmpty()) {
                return false;
            }
            sourceDirection = directions.get(new Random().nextInt(directions.size())); //a random direction from all possible ones
            int sourceWithADirection = buildASource(sourceData, sourceDirection);
            sources[i] = sourceWithADirection;
            placeElementAtPosition(sourcePosition, sourceWithADirection);
        }
        return true;
    }

    public boolean borderRowsAndColumnsAreUsed() {

        for (int column = 0; column < gridWidth; column+=(gridWidth-1)) {
            boolean thereIsFreePlace = false;
            int[] currentColumn;
            if (column == 0) {
                currentColumn = findTheColumnPositions(0);
            } else {
                currentColumn = findTheColumnPositions(maxElements-1);
            }
            for (int i = 0; i < currentColumn.length; i++) {
                if (getElementAtPosition(currentColumn[i]) == 0 || isSourcePosition(currentColumn[i]) || isGoalPosition(currentColumn[i])) {
                    thereIsFreePlace = true;
                }
            }
            if (!thereIsFreePlace) {
                return false;
            }
        }

        for (int row = 0; row < gridHeight; row+=(gridHeight-1)) {
            boolean thereIsFreePlace = false;
            int[] currentRow;
            if (row == 0) {
                currentRow = findTheRowPositions(0);
            } else {
                currentRow = findTheRowPositions(maxElements-1);
            }
            for (int i = 0; i < currentRow.length; i++) {
                if (getElementAtPosition(currentRow[i]) == 0 || isSourcePosition(currentRow[i]) || isGoalPosition(currentRow[i])) {
                    thereIsFreePlace = true;
                }
            }
            if (!thereIsFreePlace) {
                return false;
            }
        }

        //If there is no problem with border rows and columns
        return true;
    }

    /**
     * Fills the grid with 0 (0 is an empty place)
     */
    private void fillTheEmptyGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                this.grid[i][j] = 0;
            }
        }
    }

    /**
     * Places sources (without laser direction) and goals on randomly chosen empty places
     */
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
            placeElementAtPosition(goalPosition, goals[i]);
            goalsPositions[i] = goalPosition;
            //System.out.println("A goal was set on " + goalPosition);
        }

    }

    /**
     * Finds all solutions for a level by going through all empty places and setting mirrors and then checking whether the level was solved.
     *
     * @param mirrors amount of mirrors should be used for solving the level
     * @param grid the grid (level) to solve
     * @param exactNumberOfPlayableElements true if it should search for solutions with the exact given number of mirrors
     *                                      false if solutions with less mirrors are acceptable
     * @return array list of unique solutions
     */
    public ArrayList<int[][]> findAllSolutionsIterative(int mirrors, int[][] grid, boolean exactNumberOfPlayableElements) {
       //At first we check whether this level isn't already solved without any playable elements
        if (allSourcesReachedTheGoal(grid)) {
            //System.out.println("Grid is already solved!");
            return new ArrayList<>();
        }

        ArrayList<int[][]> allSolutions = new ArrayList<>();
        ArrayList<Integer> emptyPlaces = findAllEmptyCells(grid);

        ArrayList<int[][]> allPossibleCombinations = new ArrayList<>();
        ArrayList<int[][]> newestPossibleCombinations = new ArrayList<>();
        allPossibleCombinations.add(grid);

        int lastIndex = 0;
        for (int m = 0; m < mirrors; m++) {
            if (m > 0 && mirrors != 1) { //we need only combinations where ALL mirrors from previous step are placed, so we delete the other ones
                lastIndex = allPossibleCombinations.size() - 1;
            }
            for (int[][] currentGrid : allPossibleCombinations) {
                int lastMirrorPlaceIndex = findTheLastMirrorPlaceIndex(emptyPlaces, currentGrid);

                for (int placeIndex = lastMirrorPlaceIndex; placeIndex < emptyPlaces.size(); placeIndex++) {
                    int[][] newGrid = copyOf2DArray(currentGrid);
                    placeElementAtPosition(emptyPlaces.get(placeIndex), 57, newGrid);
                    newestPossibleCombinations.add(newGrid);

                    newGrid = copyOf2DArray(currentGrid);
                    placeElementAtPosition(emptyPlaces.get(placeIndex), 59, newGrid);
                    newestPossibleCombinations.add(newGrid);
                }
            }
            allPossibleCombinations.addAll(newestPossibleCombinations);
            allPossibleCombinations.subList(0, lastIndex+1).clear(); //we remove the combinations that we don't need anymore

            if (m == 1) {
                allPossibleCombinations.subList(0, 1).clear(); //we remove the empty grid after the first iteration
            }

            for (int[][] combination : allPossibleCombinations) {
                if (allSourcesReachedTheGoal(combination)) {
                    allSolutions.add(combination);
                }
            }

            if (exactNumberOfPlayableElements) {
                if (m < mirrors-1 && allSolutions.size() > 0) {
                    //System.out.println("There was a solution with less mirrors than wanted");
                    return new ArrayList<>();
                }
            }
        }

        ArrayList<int[][]> uniqueSolutions = removeRepeatingCombinations(allSolutions);
        //ArrayList<int[][]> uniqueSolutions = allSolutions;

        //System.out.println("All combinations: " + (allPossibleCombinations.size()-1));
        //System.out.println("Solutions found: " + allSolutions.size());
        //System.out.println("All " + uniqueSolutions.size() + " unique solutions: " + "\n");

        return uniqueSolutions;
    }

    /**
     * Finds the last place (from all initially empty places) where the mirror was placed
     * @param emptyPlaces initially empty places
     * @param currentGrid level with elements
     * @return the last place where a mirror was placed
     */
    private int findTheLastMirrorPlaceIndex(ArrayList<Integer> emptyPlaces, int[][] currentGrid) {
        int maxIndex = 0;
        for (int i = 0; i < emptyPlaces.size(); i++) {
            int currentPosition = emptyPlaces.get(i);
            int currentElement = getElementAtPosition(currentPosition, currentGrid);
            if (currentElement > 50 && currentElement < 60) { // if there is a mirror
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public ArrayList<int[][]> allPositionsForAnElement (int element, ArrayList<Integer> emptyPlaces, int[][]grid){
        ArrayList<int[][]> allPositions = new ArrayList<>();

        for (Integer emptyPlacePosition : emptyPlaces) {
            int[][] currentGrid = copyOf2DArray(grid);
            placeElementAtPosition(emptyPlacePosition, element, currentGrid);
            allPositions.add(currentGrid);
        }

        return allPositions;
    }

    /**
     * Checks whether both arrays are equal
     * @param array1
     * @param array2
     * @return true if arrays are equals, false if not
     */
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

    /**
     * Generates random levels and writes out those who have less or equal to numberOfSolutionsAllowed.
     * @param exactNumberOfPlayableElements
     * @param numberOfSolutionsAllowed
     */
    public void generateRandomLevels(boolean exactNumberOfPlayableElements, int numberOfSolutionsAllowed) {
        int levelCount = 1;
        int[] originSources = Arrays.copyOf(this.sources, sources.length);

        while(true) {
            this.sources = Arrays.copyOf(originSources, originSources.length);
            this.grid = generateEmptyLevel();
            if (setSourceDirections()) {
                ArrayList<int[][]> allSolutions = findAllSolutionsIterative(mirrors, this.grid, exactNumberOfPlayableElements);
                if (allSolutions.size() == 1) {
                    if (!arrayIsEqual(allSolutions.get(0), this.grid)) {
                            System.out.println("Found a level no." + levelCount + " with an unique solution: ");
                            writeGrid(gridForJSON(allSolutions.get(0), levelCount, true));
                    }
                } else if (allSolutions.size() == 0) {
                    //System.out.println("No solutions to the level was found");
                } else {
                    if (allSolutions.size() <= numberOfSolutionsAllowed) {
                        System.out.println("This level no." + levelCount + " had more than one solution: " + allSolutions.size());
                        writeGrid(gridForJSON(allSolutions.get(0), levelCount, true));
                    }
                }
            } else {
                //System.out.println("This is not a solvable level");
            }

            //System.out.println("Level N." + levelCount);
            levelCount++;
        }

    }

    /**
     * Appends a grid to a file for further instantiating on the Unity side
     * @param grid
     */
    private void writeGrid(String grid) {

        FileWriter writer;
        try {
            writer = new FileWriter("Unique levels.json", true);

            writer.write(grid);
            writer.close();

        } catch (IOException e) {
            e = new IOException("Something wrong with File Writer");
            e.printStackTrace();
        }
    }

    /**
     * Goes through all combinations and removes the repeating ones by checking their equality
     * @param listToClear
     * @return array list of unique combinations
     */
    public ArrayList<int[][]> removeRepeatingCombinations(ArrayList<int[][]> listToClear) {
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
     * This method checks whether all goals are reached by the appropriate sources (== filled correctly). If one of goals isn't filled correctly,
     * the method returns false.
     */
    public boolean allSourcesReachedTheGoal() {
        return allSourcesReachedTheGoal(this.grid);
    }

    public boolean allSourcesReachedTheGoal(int[][] grid) {
        //boolean[] completedGoals = new boolean[goals.length];

        for (int goalIndex = 0; goalIndex < goals.length; goalIndex++) {
            //completedGoals[goalIndex] = false;
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

            if (result != goalData) {
                //completedGoals[goalIndex] = true;
                return false;
            }
        }

        /* boolean allReached = true;
        for (int i = 0; i < completedGoals.length; i++)  {
            if (completedGoals[i] == false) {
                return false; //if there is at least one not completed goal, we return false
            }
        }
        return allReached; //if there is no goals that miss their sources, then we return true
        */
        return true;
    }

    /**
     * Finds all empty places on a grid
     * @param grid
     * @return array list of empty places positions
     */
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

    /**
     * Gets the direction of a source, e.g. 2 of 102-source
     * @param source source to get the direction from (e.g. 102)
     * @return the direction of the source (e.g. 2)
     */
    public int getSourceDirection(int source) {
        int sourceDirection = source % 100;
        if (sourceDirection != 2 && sourceDirection != 4 && sourceDirection !=6 && sourceDirection !=8) {
            return Integer.MIN_VALUE;
        }
        return sourceDirection;

    }

    /**
     * Gets the source data from a source (e.g. 100 from 102)
     * @param source
     * @return
     */
    public int getSourceData(int source) {
        int data = ((source + 91) / 100 ) * 100;
        if (data%100 != 0) {
            return Integer.MIN_VALUE; //Some error happened!
        }
        return data;
    }

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

    /**
     * This method and all such methods below functions as following:
     * The appropriate row/column has been checked for elements and if the "laser" stops
     * (e.g. because of the end of the row/column or a wall),
     * this position will be returned.
     * If there was a mirror on the way, the appropriate method will be called.
     * @param position where the source is placed
     * @param grid
     * @return stop position of the "laser"
     */
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
                else if (currentElement != 0 && currentElement != -1) { //if there is something on the way (not an empty cell, not a hole)
                    return rowPositions[i];
                }
            }

        }
        return rowPositions[0]; //no mirrors or obstacles on the way, return the leftest element
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

    private int getStopPositionBelow(int position, int[][] grid) { //if the laser shoot down
        int[] columnPositions = findTheColumnPositions(position);
        for(int i = 0; i < columnPositions.length; i++) {
            if (columnPositions[i] > position) { //only if it below the position
                int currentElement = getElementAtPosition(columnPositions[i], grid);
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

    //FIXME: for future improvement: we can check, whether there are at least one empty place in the row/column

    /**
     * Checks all four directions for a possibility to shoot laser for source on a certain position
     * @param sourcePosition
     * @return arrray list of possible positions
     */
    private ArrayList<Integer> findPossibleLaserDirections(int sourcePosition) {
        HashSet<Integer> possibleDirections = new HashSet<>();

        int[] columnPositions = findTheColumnPositions(sourcePosition);

        for(int i = 0; i < columnPositions.length; i++) {
            int currentPosition = columnPositions[i];
            int currentElement = getElementAtPosition(currentPosition);
            if (currentPosition < sourcePosition) {
                if (currentPosition == sourcePosition - gridWidth) { //if this is the place above
                    if (currentElement != 1 && !isSourcePosition(currentPosition) && !isGoalPosition(currentPosition)) { //added to check whether there is no wall or another source in the cell above
                        possibleDirections.add(8);
                    }
                }
            }

            if (columnPositions[i] > sourcePosition){
                if (currentPosition == sourcePosition + gridWidth) { //if this is the place below
                    if (currentElement != 1 && !isSourcePosition(currentPosition) && !isGoalPosition(currentPosition)) { //added to check whether there is no wall or another source in the cell above
                        possibleDirections.add(2);
                    }
                }

            }
        }

        int[] rowPositions = findTheRowPositions(sourcePosition);
        for(int i = 0; i < rowPositions.length; i++) {
            int currentPosition = rowPositions[i];
            int currentElement = getElementAtPosition(currentPosition);

            if (currentPosition < sourcePosition) {
                if (currentPosition == sourcePosition-1) { //if this is the place on the left
                    if (currentElement != 1 && !isSourcePosition(currentPosition) && !isGoalPosition(currentPosition)) { //added to check whether there is no wall in the cell on the left
                        possibleDirections.add(4);
                    }
                }
            }

            if (currentPosition > sourcePosition){
                if (currentPosition == sourcePosition+1) { //if this is the place on the right
                    if (currentElement != 1 && !isSourcePosition(currentPosition) && !isGoalPosition(currentPosition)) { //added to check whether there is no wall in the cell on the left
                        possibleDirections.add(6);
                    }
                }
            }

        }
        ArrayList<Integer> endList = new ArrayList<>(possibleDirections);

        return endList;
    }

    private boolean isSourcePosition(int currentPosition) {
        for (int position : this.sourcesPositions) {
            if (position == currentPosition) {
                return true;
            }
        }
        return false;
    }

    private boolean isGoalPosition(int currentPosition) {
        for (int position : this.goalsPositions) {
            if (position == currentPosition) {
                return true;
            }
        }
        return false;
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

    //This is just an optional method, which probably makes no so much sense. Thought as "New method that can be helpful for optimization"
    //For future improvement:  Warning if there is no more free place in the grid
    public int randomFreePlace2(ArrayList<Integer> emptySpaces) {
        Random random = new Random();
        int randomNum = random.nextInt(emptySpaces.size()); //maxElements is Nth, range of nextInt is 0 to N-1, exactly what we need
        int randomPlace = getElementAtPosition(emptySpaces.get(randomNum));
        //while (getElementAtPosition(randomNum) == HOLE || getElementAtPosition(randomNum) == WALL) {
        while (getElementAtPosition(randomPlace) != 0) {
            randomNum = random.nextInt(emptySpaces.size());
            randomPlace = getElementAtPosition(emptySpaces.get(randomNum));
        }
        //System.out.println("Random place " + randomNum);
        return randomNum;
    }

    public int randomFreePlace() {
        Random random = new Random();
        int randomNum = random.nextInt(maxElements); //maxElements is Nth, range of nextInt is 0 to N-1, exactly what we need
        //while (getElementAtPosition(randomNum) == HOLE || getElementAtPosition(randomNum) == WALL) {
        while (getElementAtPosition(randomNum) != 0) {
            randomNum = random.nextInt(maxElements);
        }
        //System.out.println("Random place " + randomNum);
        return randomNum;
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

    /** Finding the column/row positions **/
    private int[] findTheColumnPositions(int position) {
        int[] column = new int[gridHeight];

        int numberInRow = position % gridWidth; //das gleiche wie unten, nur in einer Zeile
      /* int numberInRow = position;
         while (numberInRow >= gridWidth) {
            numberInRow -= gridWidth;
        } */

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

    /** Methods producing readable output from grids **/

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

    public String gridForJSON(int[][] grid, int levelNumber, boolean withMirrors) {
        if (!withMirrors) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] > 50 && grid[i][j] < 60) {
                        grid[i][j] = 0;
                    }
                }
            }
        }
        String result = '"' + "level" + levelNumber + '"' + ':' + " [" + "\n";
        for (int i = 0; i < grid.length; i++) {
            result += '"';
            for (int j = 0; j < grid[i].length; j++) {
                result += grid[i][j] + ",";
                if (j == grid[i].length-1) {
                    result = result.substring(0, result.length()-1); //the endIndex excluded, so we remove last comma
                }
            }
            result += '"' + ",";
            if (i == grid.length-1) {
                result = result.substring(0, result.length()-1); //the endIndex excluded, so we remove last comma
            }
            result += "\n";
        }
        result += "]," + "\n";
        return result;
    }

    /** convenience methods **/
    public ArrayList<Integer> findAllEmptyCells() {
        return findAllEmptyCells(this.grid);
    }
    private int getStopPositionOnLeft(int position) { //if the laser shoot to the left
        return getStopPositionOnLeft(position, this.grid);
    }
    private int getStopPositionOnRight(int position) { //if the laser shoot to the right
        return getStopPositionOnRight(position, this.grid);
    }
    private int getStopPositionBelow(int position) { //if the laser shoot down
        return getStopPositionBelow(position, this.grid);
    }
    private int getStopPositionAbove(int position) { //if the laser shoot up
        return getStopPositionAbove(position, this.grid);
    }
    public int getElementAtPosition(int positionToCheck) {
        return getElementAtPosition(positionToCheck, this.grid);
    }
    private void placeElementAtPosition(int positionToSetOn, int element) {
        placeElementAtPosition(positionToSetOn, element, this.grid);
    }

    /** getters and setters **/

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

}