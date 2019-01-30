import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        /** For generating random levels  **/
        int[] sources = {100, 200, 400, 500};
        int[] goals = {300, 900};

        Generator generator = new Generator(5, 4, 3, 3, 5, sources, goals);
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
