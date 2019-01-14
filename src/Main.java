import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //int[] sourcesPositions = {0, 15};
        //int[] goalPosition = {3};

        int[] sources = {100, 200}; //400
        int[] goals = {300}; //500, 200

        Generator generator = new Generator(4, 4, 3, 2, 5, sources, goals);
        //Generator generator = new Generator(4, 4, 4, 3, 5, sources, goals);
        generator.generateRandomLevelsRecursiv(false);

        //generator.generateRandomLevelsIterativ(true);

        /* int[][] solvableLevel = new int[][] {{102, 1, -1, 300, 0},
                                                {0, 0, -1, 0, 0},
                                                {-1, 0, -1, 1, 0},
                                                {206, 0, -1, -1, 0}};

        Generator generator = new Generator(5,4, solvableLevel);

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
