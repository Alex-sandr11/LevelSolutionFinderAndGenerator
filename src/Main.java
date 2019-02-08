/**
 * This class allows user to set the requirements for level generating or set a level and check it for solutions.
 */
public class Main {

    public static void main(String[] args) {


        /** For generating random levels  **/
        int[] sources = {100, 200};
        int[] goals = {300};

        Generator generator = new Generator(5, 4, 7, 5, 4, sources, goals);
        generator.generateRandomLevels(true);


        /** For solutions finding for an existing level. Below is an example **/
        /* int[][] solvableLevel = new int[][] {{102, 1, -1, 300, 0},
                                                {0, 0, -1, 0, 0},
                                                {-1, 0, -1, 1, 0},
                                                {206, 0, -1, -1, 0}};

        Generator generator = new Generator(5,4, solvableLevel);

        //int[] sourcesPositions = {0, 15};
        //int[] goalPosition = {3};

        generator.setSources(sources);
        generator.setGoals(goals);
        generator.setSourcesPositions(sourcesPositions);
        generator.setGoalsPositions(goalPosition);

        generator.findAllSolutionsIterativ(4, solvableLevel, false); */

    }
}
