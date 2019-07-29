/**
 * This file is part of Library for Associative Classification (LAC)
 *
 * Copyright (C) 2019
 *   
 * LAC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. You should have 
 * received a copy of the GNU General Public License along with 
 * this program.  If not, see http://www.gnu.org/licenses/
 */
package lac.runner;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class of LAC. It aims at parsing all the configuration files and run all
 * the specified executions.
 */
public class Runner {
    /**
     * It parses the arguments passed as parameters and generate all the executions.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Configuration file must be specified.");
            System.exit(-1);
        }

        ArrayList<ConfigExecution> executions = new ArrayList<ConfigExecution>();
        int lacThreads = getNumberThreads();

        // Read all the configuration files to know how many executions will have to be
        // performed
        for (int j = 0; j < args.length; j++) {
            YamlConfig config = new YamlConfig(args[j]);

            System.out.println("******************************************************************");
            System.out.println("Configuration file: " + args[j]);
            System.out.println("Number of executions: " + config.getExecutions().size());
            System.out.println("******************************************************************");

            for (int i = 0; i < config.getExecutions().size(); i++) {
                ConfigExecution execution = config.getExecutions().get(i);
                executions.add(execution);
            }
        }

        if (lacThreads > 1)
            System.out.println("\n\nStarting executions with " + lacThreads + " threads...\n\n");

        ExecutorService executor = Executors.newFixedThreadPool(lacThreads);
        for (ConfigExecution execution : executions) {
            executor.submit(() -> {
                try {
                    execution.run();

                    long totalTime = execution.getTrainReport().getTotalTime()
                            + execution.getTestReport().getTotalTime();

                    System.out.println("******************************************************************\n"
                            + "Algorithm: " + execution.getNameAlgorithm() + "\n" + "Dataset: "
                            + execution.getTraining().getName() + "\n" + "Runtime: " + totalTime
                            + "ms (Building classifier " + execution.getTrainReport().getTotalTime() + "ms; "
                            + "Test phase " + execution.getTestReport().getTotalTime() + "ms)" + "\n"
                            + "Number of rules: " + execution.getClassifier().getNumberRules() + "\n"
                            + "Training accuracy: " + execution.getTrainReport().getAccuracy() + "\n"
                            + "Test accuracy: " + execution.getTestReport().getAccuracy() + "\n"
                            + "******************************************************************");
                } catch (Exception e) {
                    System.out.println("Error in algorithm " + execution.getNameAlgorithm());
                    e.printStackTrace();
                }
            });
        }

        System.out.flush();
        executor.shutdown();
    }

    /**
     * Get the number of threads to be used. It reads the environment variable or in
     * cases where it has not been set, it uses by default one unique thread
     * 
     * @return the number of threads
     */
    private static int getNumberThreads() {
        String lacThreads = System.getenv("LAC_THREADS");

        try {
            int number = Integer.parseInt(lacThreads);
            return number > 0 ? number : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
