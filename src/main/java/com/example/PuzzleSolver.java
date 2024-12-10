package com.example;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PuzzleSolver {
    private static final Logger LOGGER = Logger.getLogger(PuzzleSolver.class.getName());
    private static final String PATH = "source.txt";
    private static List<String> fragments = new ArrayList<>();
    private static Map<String, List<String>> graph = new HashMap<>();
    private static String longestPath = "";

    public static void main(String[] args) {
        loadFragments(PATH);
        buildGraph();
        findLongestPath();
        LOGGER.log(Level.INFO,"The longest sequence: " + longestPath);
        LOGGER.log(Level.INFO,"The longest path: " + longestPath.length());

    }

    private static void loadFragments(String fileName) {
        ClassLoader classLoader = PuzzleSolver.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                fragments.add(line.trim());
            }
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.WARNING,"File reading error: " + e.getMessage());
        }
    }

    private static void buildGraph() {
        for (String fragment : fragments) {
            String suffix = fragment.substring(fragment.length() - 2);
            graph.putIfAbsent(fragment, new ArrayList<>());
            for (String other : fragments) {
                if (!fragment.equals(other)) {
                    String prefix = other.substring(0, 2);
                    if (suffix.equals(prefix)) {
                        graph.get(fragment).add(other);
                    }
                }
            }
        }
    }

    private static void findLongestPath() {
        for (String fragment : fragments) {
            String path = iterativeDFS(fragment);
            if (path.length() > longestPath.length()) {
                longestPath = path;
            }
        }
    }

    private static String iterativeDFS(String startFragment) {
        Deque<String> stack = new ArrayDeque<>();
        Map<String, String> pathMap = new HashMap<>();
        stack.push(startFragment);
        pathMap.put(startFragment, startFragment);

        String longestPath = startFragment;

        while (!stack.isEmpty()) {
            String current = stack.pop();
            String currentPath = pathMap.get(current);

            for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!pathMap.containsKey(neighbor)) {
                    String newPath = currentPath + neighbor.substring(2);
                    pathMap.put(neighbor, newPath);
                    stack.push(neighbor);

                    if (newPath.length() > longestPath.length()) {
                        longestPath = newPath;
                    }
                }
            }
        }

        return longestPath;
    }
}
