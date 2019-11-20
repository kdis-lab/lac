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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import junit.framework.TestSuite;

public class RunnerTest extends TestSuite {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void getEnvironmentLacThreadsFromEnv() throws Exception {
        environmentVariables.set("LAC_THREADS", "5");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("config.yml").getFile());
        Runner.main(new String[] { file.getAbsolutePath() });
        assertThat(outContent.toString(), containsString("Starting executions with 5 threads..."));
    }

    @Test
    public void stopsWhenNoConfigurationFileIsProvided() throws Exception {
        exit.expectSystemExitWithStatus(-1);
        Runner.main(new String[] {});
        assertEquals("Configuration file must be specified", outContent.toString());
    }

    @Test
    public void specifyingMultipleConfigurationFilesAreRead() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("config.yml").getFile());
        File file2 = new File(classLoader.getResource("config2.yml").getFile());
        String[] configs = new String[] { file.getAbsolutePath(), file2.getAbsolutePath() };
        Runner.main(configs);
        for (int i = 0; i < configs.length; i++) {
            assertThat(outContent.toString(), containsString("Configuration file: " + configs[i]));
        }
    }

    @Test
    public void showOutputPerExecution() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("config2.yml").getFile());
        Runner.main(new String[] { file.getAbsolutePath() });
        Thread.sleep(500);
        assertThat(outContent.toString(), containsString("Algorithm: ACCF"));
        assertThat(outContent.toString(), containsString("Training accuracy: 1.0"));
        assertThat(outContent.toString(), containsString("Test accuracy: 1.0"));
        assertThat(outContent.toString(), containsString("Number of rules: 6"));
        assertThat(outContent.toString(), containsString("Dataset: dataset"));
    }

    @Test
    public void showErrorWhenAlgorithDoesNotWork() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("config.yml").getFile());
        Runner.main(new String[] { file.getAbsolutePath() });
        Thread.sleep(500);
        assertThat(outContent.toString(), containsString("Error in algorithm ACCF"));
        assertThat(outContent.toString(), containsString("Error in algorithm MAC"));
    }
}
