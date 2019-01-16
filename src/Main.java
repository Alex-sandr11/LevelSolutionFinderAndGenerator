import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        int[] sources = {100, 200};
        int[] goals = {300};

        Generator generator = new Generator(4, 4, 5, 2, 5, sources, goals);
        //Generator generator = new Generator(4, 4, 3, 3, 4, sources, goals);
        generator.generateRandomLevelsRecursiv(true);

        //generator.generateRandomLevelsIterativ(true);

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

        generator.findAllSolutionsIterativ(4, solvableLevel); */


        //generator.generateAllLevelsWithOneSolutionIterativ();
        //String[][] generatedLevel = generator.generateLevel();
        //generator.findAllSolutionsIterativ(3, generatedLevel);

        //generator.generateAllLevelsWithOneSolutionIterativ();

    }
}
