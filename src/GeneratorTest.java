import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GeneratorTest {

    int[][] grid1;
    int[][] solvableLevel;
    Generator generator;
    Generator playableGenerator;

    @Before
    public void generateGrid() {
        //solution for a test level
        grid1 = new int[][] {   {102, 1, -1, 300, 59},
                                {59, 0, -1, 57, 0},
                                {-1, 0, 1, 308, 0},
                                {206, 0, -1, -1, 57} };

        int[][] solvableLevel = new int[][] {{102, 1, -1, 300, 0},
                                            {0, 0, -1, 0, 0},
                                            {-1, 0, -1, 1, 0},
                                            {206, 0, -1, -1, 0}}; //4 mirrors are needed for the solution

        generator = new Generator(5, 4, grid1);
        playableGenerator = new Generator(5, 4, solvableLevel, 4);

    }


    @Test
    public void endLaserPosition1() {

        int calculatedEndLaserPosition = generator.endLaserPosition(0, 2);

        assertEquals("The calculated laser end position doesn't match the expected one", 3, calculatedEndLaserPosition);
    }

    @Test
    public void endLaserPosition2() {

        int calculatedEndLaserPosition = generator.endLaserPosition(0, 6);

        assertEquals("The calculated laser end position doesn't match the expected one", 1, calculatedEndLaserPosition);
    }

    @Test
    public void endLaserPosition3() {

        int calculatedEndLaserPosition = generator.endLaserPosition(15, 6);

        assertEquals("The calculated laser end position doesn't match the expected one", 3, calculatedEndLaserPosition);
    }

    @Test
    public void endLaserPosition6() {

        int calculatedEndLaserPosition = generator.endLaserPosition(15, 8);

        assertEquals("The calculated laser end position doesn't match the expected one", 5, calculatedEndLaserPosition);
    }

    @Test
    public void endLaserPosition4() {

        int calculatedEndLaserPosition = generator.endLaserPosition(13, 8);

        assertEquals("The calculated laser end position doesn't match the expected one", 9, calculatedEndLaserPosition);
    }

    @Test
    public void endLaserPosition5() {

        int calculatedEndLaserPosition = generator.endLaserPosition(13, 6);

        assertEquals("The calculated laser end position doesn't match the expected one", 14, calculatedEndLaserPosition);
    }

    @Test
    public void endLaserPosition7() {

        int[][] grid = new int[][] {   {102, 1, -1, 300, 59},
                                {0, 0, -1, 57, 0},
                                {202, 0, 1, 308, 0},
                                {206, 0, -1, -1, 57} };

        Generator gen = new Generator(5, 4, grid);
        int calculatedEndLaserPosition = gen.endLaserPosition(0, 2);

        assertEquals("The calculated laser end position doesn't match the expected one", 10, calculatedEndLaserPosition);
    }

    @Test
    public void getSourceDirection() {
        int source1 = 104;
        int source2 = 202;
        int source3 = 308;
        int source4 = 406;
        int notValidSource = 509;
        int error = Integer.MIN_VALUE;

        int left = 4;
        int right = 6;
        int up = 8;
        int down = 2;

        assertEquals("The calculated source direction doesn't match the expected one", left, generator.getSourceDirection(source1));
        assertEquals("The calculated source direction doesn't match the expected one", right, generator.getSourceDirection(source4));
        assertEquals("The calculated source direction doesn't match the expected one", up, generator.getSourceDirection(source3));
        assertEquals("The calculated source direction doesn't match the expected one", down, generator.getSourceDirection(source2));
        assertEquals("The calculated direction of a not valid source doesn't match the expected warning", error, generator.getSourceDirection(notValidSource));
    }

    @Test
    public void getElementAtPosition() {

        assertEquals("The found element at given position doesn't match the expected one", 59, generator.getElementAtPosition(5));
        assertEquals("The found element at given position doesn't match the expected one", 102, generator.getElementAtPosition(0));
        assertEquals("The found element at given position doesn't match the expected one", 57, generator.getElementAtPosition(19));
        assertEquals("The found element at given position doesn't match the expected one", -1, generator.getElementAtPosition(7));
        assertEquals("The found element at given position doesn't match the expected one", 308, generator.getElementAtPosition(13));

        assertEquals("The found element at given position doesn't match the expected one", Integer.MIN_VALUE, generator.getElementAtPosition(20));
    }

    /** Took 16,5 s **/
    @Test
    public void findAllSolutions5x5Sources4Mirrors6() {
        int[][] notAsolvableLevel = new int[][] {       {  1,  -1, 206, 900, 300},
                                                        { -1, 406,   0,   1,  -1},
                                                        {  0,   1,   0,  -1,   0},
                                                        {  0,   0,  -1, 506,   0},
                                                        {106,   0,   0,   0,   0}};

        int[] sources = {106, 206, 406, 506};
        int[] goals = {300, 900};
        int[] sourcesPositions = {20, 2, 6, 18};
        int[] goalPosition = {4, 3};
        Generator gen = new Generator(5, 5, notAsolvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(6, notAsolvableLevel, true);

        assertEquals("Amount of found solutions didn't match the expected one", 0, foundSolutions.size());

    }

    @Test
    public void allSourcesReachedTheGoals() {
        int[][] notAsolvableLevel = new int[][] {       {  1,  -1, 206, 900, 300},
                { -1, 406,  59,   1,  -1},
                {  0,   1,   59,  -1,   59},
                {  106,   0,  -1, 506,   59}};

        int[] sources = {106, 206, 406, 506};
        int[] goals = {300, 900};
        int[] sourcesPositions = {15, 2, 6, 18};
        int[] goalPosition = {4, 3};
        Generator gen = new Generator(5, 4, notAsolvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        assertFalse(gen.allSourcesReachedTheGoal(notAsolvableLevel));
    }


    /** Takes more than 20 Minutes and still doesn't solve the level! **/ /** Solved in 18m 25s **/ /** Best time 19s **/
     @Test
    public void findAllSolutionWith6Mirrors() {
        int[][] solvableLevel = new int[][] {       {0, 0, 0, 1, -1},
                                                    {0, 1, 0, 0, 104},
                                                    {-1, -1, 0, 0, 300},
                                                    {208, 0, 0, 0, 0} };

        int[][] solution1 = new int[][] {   {57, 0, 59, 1, -1},
                                            {0, 1, 0, 57, 104},
                                            {-1, -1, 0, 59, 300},
                                            {208, 0, 59, 0, 57} };

         int[][] solution2 = new int[][] {  {57, 0, 59, 1, -1},
                                            {0, 1, 0, 57, 104},
                                            {-1, -1, 59, 0, 300},
                                            {208, 0, 0, 59, 57} };

         ArrayList<int[][]> solutions = new ArrayList<>();
         solutions.add(solution1);
         solutions.add(solution2);

        int[] sources = {104, 208};
        int[] goals = {300};
        int[] sourcesPositions = {9, 15};
        int[] goalPosition = {14};
        Generator gen = new Generator(5, 4, solvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

         ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(6, solvableLevel, false);

         //foundSolutions = gen.removeRepeatingCombinations(foundSolutions); //Why should I do this?

        assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution1, foundSolutions.get(1)));
        assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution2, foundSolutions.get(0)));
    }


    /** Takes less than 3s now!!! **/
    @Test
    public void findAllSolutionWith6MirrorsIterativNeu() {
        int[][] solvableLevel = new int[][] {       {0, 0, 0, 1, -1},
                {0, 1, 0, 0, 104},
                {-1, -1, 0, 0, 300},
                {208, 0, 0, 0, 0} };

        int[][] solution1 = new int[][] {   {57, 0, 59, 1, -1},
                {0, 1, 0, 57, 104},
                {-1, -1, 0, 59, 300},
                {208, 0, 59, 0, 57} };

        int[][] solution2 = new int[][] {  {57, 0, 59, 1, -1},
                {0, 1, 0, 57, 104},
                {-1, -1, 59, 0, 300},
                {208, 0, 0, 59, 57} };

        ArrayList<int[][]> solutions = new ArrayList<>();
        solutions.add(solution1);
        solutions.add(solution2);

        int[] sources = {104, 208};
        int[] goals = {300};
        int[] sourcesPositions = {9, 15};
        int[] goalPosition = {14};
        Generator gen = new Generator(5, 4, solvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative( 6, solvableLevel, false);

          for (int i = 0; i < foundSolutions.size(); i++) {
             System.out.println("Solution " + i + "\n");
             System.out.println(gen.gridToString(foundSolutions.get(i)));
         }

        assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution1, foundSolutions.get(1)));
        assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution2, foundSolutions.get(0)));
    }

    /** for testing of exact number of mirrors**/
    @Test
    public void findAllSolutions8() {
        int[][] solvableLevel = new int[][] {       {0, 300, 0, 104},
                                                    {-1, 0, 0, 0},
                                                    {0, 204, 1, 0}};

        int[] sources = {104, 204};
        int[] goals = {300};
        int[] sourcesPositions = {3, 9};
        int[] goalPosition = {1};
        Generator gen = new Generator(4, 3, solvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(5, solvableLevel, true);

        for (int i = 0; i < foundSolutions.size(); i++) {
            System.out.println("Solution " + i + "\n");
            System.out.println(gen.gridToString(foundSolutions.get(i)));
        }
    }

    /** Takes 5 min 30 s **/
    @Test
    public void findAllSolutions7Mirrors() {
        int[][] solvableLevel = new int[][] {       {100, 200, 1, 1,   -1},
                                                    {  0,   0, -1, -1,   0},
                                                    {  0,   0, 0, 1,   -1},
                                                    {  -1,   0, -1, 0,   0},
                                                    {208,   0, 0, 0, 104}};

        int[] sources = {104, 208};
        int[] goals = {100,200};
        int[] sourcesPositions = {24, 20};
        int[] goalPosition = {0, 1};
        Generator gen = new Generator(5, 5, solvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(6, solvableLevel, true);

        foundSolutions = gen.removeRepeatingCombinations(foundSolutions); //Why should I do this?

        //There are two unique solutions
          for (int i = 0; i < foundSolutions.size(); i++) {
             System.out.println("Solution " + i + "\n");
             System.out.println(gen.gridToString(foundSolutions.get(i)));
         }

        //assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution1, foundSolutions.get(0)));
        //assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution2, foundSolutions.get(1)));
    }

    //Takes about 17s without adapter
    @Test
    public void findAllSolutions7MirrorsIterativNeu() {
        int[][] solvableLevel = new int[][] {       {100, 200, 1, 1,   -1},
                {  0,   0, -1, -1,   0},
                {  0,   0, 0, 1,   -1},
                {  -1,   0, -1, 0,   0},
                {208,   0, 0, 0, 104}};

        int[] sources = {104, 208};
        int[] goals = {100,200};
        int[] sourcesPositions = {24, 20};
        int[] goalPosition = {0, 1};
        Generator gen = new Generator(5, 5, solvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(7, solvableLevel,false);

        //There are two unique solutions
        for (int i = 0; i < foundSolutions.size(); i++) {
            System.out.println("Solution " + i + "\n");
            System.out.println(gen.gridToString(foundSolutions.get(i)));
        }

        //assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution1, foundSolutions.get(0)));
        //assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(solution2, foundSolutions.get(1)));
    }

    @Test
    public void testFindASolution4() {
        int[][] solvableLevel = new int[][] {{0, 0, 300, 0, 204},
                                             {0, 0, -1, 1, 1},
                                             {0, 0, 104, 1, -1},
                                             {-1, 1, -1, -1, 1} };

        int[] sources = {104, 204};
        int[] goals = {300};
        int[] sourcesPositions = {12, 4};
        int[] goalPosition = {2};
        Generator gen = new Generator(5, 4, solvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(3, solvableLevel, false);

        for (int i = 0; i < foundSolutions.size(); i++) {
            System.out.println("Solution " + i + "\n");
            System.out.println(gen.gridToString(foundSolutions.get(i)));
        }

        //assertEquals("The found solution for the given grid doesn't match the expected one", expectedGrid, playableGenerator.findASolution());
    }

    /** Takes > 7s (because 5 mirrors) **/ /** Avg time 3,5s **/ /** Best time 2,7s (with adapter) !!!! Recursiv less than 1s without adapter!!!! **/
   @Test
    public void testFindASolution3() {
        int[][] solvableLevel = new int[][] {   {0, 0, 1, 100},
                                                {0, -1, -1, 0},
                                                {1, 0, 0, 1},
                                                {0, 0, 0, 104}};

       int[][] solution = new int[][] {     {57, 59, 1, 100},
                                            {59, -1, -1, 57},
                                            {1, 0, 0, 1},
                                            {0, 59, 0, 104}};


        int[] sources = {104};
        int[] goals = {100};
        int[] sourcesPositions = {15};
        int[] goalPosition = {3};
        Generator gen = new Generator(4, 4, solvableLevel);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

       ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(5, solvableLevel, false);

       for (int i = 0; i < foundSolutions.size(); i++) {
           System.out.println("Solution " + i + "\n");
           System.out.println(gen.gridToString(foundSolutions.get(i)));
       }
       assertEquals("The found solution for the given grid doesn't match the expected one", solution, foundSolutions.get(0));
    }

   /** with an extra source that is not needed to solve the level **/
    @Test
    public void testFindASolution2() {
        int[][] solvableLevel = new int[][] {       { 102, 1, -1, 300, 0},
                                                    {   0, 0, -1,   0, 0},
                                                    {  -1, 0, -1, 408, 0},
                                                    { 206, 0, -1,  -1, 0} };

        int[][] expectedGrid = new int[][] {        {102, 1, -1, 300, 59},
                                                    {59, 0, -1, 57, 0},
                                                    {-1, 0, -1, 408, 0},
                                                    {206, 0, -1, -1, 57} };

        Generator gen = new Generator(5, 4, solvableLevel, 4);
        int[] sources = {102, 206, 408};
        int[] goals = {300};
        int[] sourcesPositions = {0, 15, 13};
        int[] goalPosition = {3};
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(4, solvableLevel, false);

        assertEquals("The found solution for the given grid doesn't match the expected one", expectedGrid, foundSolutions.get(0));
    }

    @Test
    public void findAllSolutionsIterativ() {
        int[][] notASolvableLevel = new int[][]{{-1, 204, 1, 400, 0},
                                                {1, 300, -1, 102, 0},
                                                {1, 0, 0, -1, 0},
                                                {-1, 0, 404, 1, -1}};

        int[] sources = {102, 204, 404};
        int[] goals = {300, 400};
        int[] sourcesPositions = {8, 1, 17};
        int[] goalPosition = {6, 3};
        Generator gen = new Generator(5, 4, notASolvableLevel, 6);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(2, notASolvableLevel,false);

        for (int i = 0; i < foundSolutions.size(); i++) {
            System.out.println("Solution " + i + "\n");
            System.out.println(gen.gridToString(foundSolutions.get(i)));
        }
    }

    @Test
    public void findASolution1() {
        int[][] expectedGrid = new int[][] {    {1, 1, 102},
                                                {-1, 1, -1},
                                                {57, 0, 57},
                                                {100, 1, 1}};

        int[][] solvableGrid = new int[][] {    {1, 1, 102},
                                                {-1, 1, -1},
                                                {0, 0, 0},
                                                {100, 1, 1}};

        int[] sources = {102};
        int[] goals = {100};
        int[] sourcesPositions = {2};
        int[] goalPosition = {9};
        Generator gen = new Generator(3, 4, solvableGrid, 2);
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(2, solvableGrid,false);

        for (int i = 0; i < foundSolutions.size(); i++) {
            System.out.println("Solution " + i + "\n");
            System.out.println(gen.gridToString(foundSolutions.get(i)));
        }

        assertTrue("The found solution for the given grid doesn't match the expected one", gen.arrayIsEqual(expectedGrid, foundSolutions.get(0)));
    }

    @Test
    public void testFindAllSolutionsOptimization() {
        int[][] solvableLevel = new int[][] {   {106, 0, 300},
                                                {  0, 0, 0},
                                                { -1, 1, -1},
                                                {208, 0, 1} };

        Generator gen = new Generator(3, 4, solvableLevel, 2);
        int[] sources = {106, 208};
        int[] goals = {300};
        int[] sourcesPositions = {0, 9};
        int[] goalPosition = {2};
        gen.setSources(sources);
        gen.setGoals(goals);
        gen.setSourcesPositions(sourcesPositions);
        gen.setGoalsPositions(goalPosition);

        ArrayList<int[][]> foundSolutions = gen.findAllSolutionsIterative(2, solvableLevel, false);

        //assertEquals("The found solution for the given grid doesn't match the expected one", expectedGrid, foundSolutions.get(0));
    }

    @Test
    public void allSourcesReachedTheGoal() {
        int[][] solvedLevel = new int[][] {     {102, 1, -1, 300, 59},
                                                {59,  0, -1,  57,  0},
                                                {-1,  0, -1,   0,  0},
                                                {206, 0, -1,  -1, 57} };

        Generator generator = new Generator(5, 4, solvedLevel);
        int[] sources = {102, 206};
        int[] goals = {300};
        int[] sourcesPositions = {0, 15};
        int[] goalPosition = {3};
        generator.setSources(sources);
        generator.setGoals(goals);
        generator.setSourcesPositions(sourcesPositions);
        generator.setGoalsPositions(goalPosition);

        assertEquals("All sources were expected to reach the goal, but this didn't happen", true, generator.allSourcesReachedTheGoal());
    }

    @Test
    public void allSourcesReachedTheGoal2() {
        int[][] solvedLevel = new int[][] { {102, 406, 400, 300, 59},
                                            {59,  0, -1,  57,  0},
                                            {-1,  0, -1,   0,  0},
                                            {206, 0, -1,  -1, 57}};

        Generator generator = new Generator(5, 4, solvedLevel);
        int[] sources = { 206, 102,406};
        int[] goals = {300, 400};
        int[] sourcesPositions = {15, 0, 1};
        int[] goalPosition = {3, 2};
        generator.setSources(sources);
        generator.setGoals(goals);
        generator.setSourcesPositions(sourcesPositions);
        generator.setGoalsPositions(goalPosition);

        assertEquals("All sources were expected to reach the goal, but this didn't happen", true, generator.allSourcesReachedTheGoal());
    }

    @Test
    public void allSourcesReachedTheGoal3() {
        int[][] notAValidSolvedLevel = {   {206, -1, -1, -1, 0},
                                    {1, 300, 59, 106, 57},
                                    {1, -1, 1, 1, 0},
                                    {1, 59, 0, 59, -1}};

        Generator generator = new Generator(5, 4, notAValidSolvedLevel);
        int[] sources = { 206, 106 };
        int[] goals = {300};
        int[] sourcesPositions = {0, 8};
        int[] goalPosition = {6};
        generator.setSources(sources);
        generator.setGoals(goals);
        generator.setSourcesPositions(sourcesPositions);
        generator.setGoalsPositions(goalPosition);

        assertEquals("All sources were expected to reach the goal, but this didn't happen", false, generator.allSourcesReachedTheGoal());
    }

    @Test
    public void allSourcesReachedTheGoal4() {
        int[][] notAValidSolvedLevel = {   {-1, 1, 57, -1, 0},
                                            {0, 300, 1, -1, 0},
                                            {-1, 1, 206, -1, 59},
                                            {0, 1, 1, 106, 0}};

        Generator generator = new Generator(5, 4, notAValidSolvedLevel);
        int[] sources = { 206, 106 };
        int[] goals = {300};
        int[] sourcesPositions = {12, 18};
        int[] goalPosition = {6};
        generator.setSources(sources);
        generator.setGoals(goals);
        generator.setSourcesPositions(sourcesPositions);
        generator.setGoalsPositions(goalPosition);

        assertEquals("All sources were expected to reach the goal, but this didn't happen", false, generator.allSourcesReachedTheGoal());
    }

    @Test
    public void allSourcesReachedTheGoal5() {
        int[][] notAValidSolvedLevel = {{0, -1, 59, 1, 1},
                                        {108, 57, -1, -1, 204},
                                        {-1, 59, 57, 1, 300},
                                        {1, 1, 1, -1, -1} };


        Generator generator = new Generator(5, 4, notAValidSolvedLevel);
        int[] sources = { 204, 108 };
        int[] goals = {300};
        int[] sourcesPositions = {9, 5};
        int[] goalPosition = {14};
        generator.setSources(sources);
        generator.setGoals(goals);
        generator.setSourcesPositions(sourcesPositions);
        generator.setGoalsPositions(goalPosition);

        assertEquals("All sources were expected to reach the goal, but this didn't happen", false, generator.allSourcesReachedTheGoal());
    }

    @Test
    public void borderRowAndColumnsAreUsed() {
        int[][] notAValidLevel = {  {57,-1,104,202,1},
                                    {59,-1,300,-1,0},
                                    {1,-1,59,57,1},
                                    {1,1,-1,-1,-1}};

        Generator generator = new Generator(5, 4, notAValidLevel);
        int[] sources = {104, 202};
        int[] goals = {300};
        int[] sourcesPositions = {2, 3};
        int[] goalPosition = {7};
        generator.setSources(sources);
        generator.setGoals(goals);
        generator.setSourcesPositions(sourcesPositions);
        generator.setGoalsPositions(goalPosition);

        assertEquals("A not valid level with a not used border row was found valid", false, generator.borderRowsAndColumnsAreUsed());
    }

    @Test
    public void gridToString() {
        int[][] grid = { {102, 406, 400, 300, 59},
                {59,  0, -1,  57,  0},
                {-1,  0, -1,   0,  0},
                {206, 0, -1,  -1, 57}};

        String expectedResult = "[102] [406] [400] [300] [59] " + "\n" + "[59] [0] [-1] [57] [0] " + "\n" + "[-1] [0] [-1] [0] [0] " + "\n" + "[206] [0] [-1] [-1] [57] " + "\n";
        String actualResult = generator.gridToString(grid);

        assertEquals("By converting a grid into String the actual result didn't match the expected result", expectedResult, actualResult);
    }


}