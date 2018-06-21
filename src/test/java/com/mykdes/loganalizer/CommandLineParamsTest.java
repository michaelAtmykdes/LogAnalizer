package com.mykdes.loganalizer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mmykhaylov
 */
public class CommandLineParamsTest {

    public CommandLineParamsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseCommandLine method, of class CommandLineParams.
     */
    @Test
    public void testParseCommandLineWithGoodArgument() {
        System.out.println("Test parseCommandLine with valid command line Arguments");
        final String commandLine = "   -d .    -m   20  ";
        try {
            final CommandLineParams clp = CommandLineParams.parseCommandLine(commandLine.split(" "));
            assertNotNull(clp);
            assertEquals(".", clp.getDirectory());
            assertEquals(20, (int)(((float)clp.getStartTimeOffsetInSec())/(60)));
        } catch (IllegalArgumentException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testParseCommandLineWithDefaultArgument() {
        System.out.println("Test parseCommandLine with valid command line Arguments");
        final String commandLine = "  g-a-r-b-a-g-e ";
        try {
            final CommandLineParams clp = CommandLineParams.parseCommandLine(commandLine.split(" "));
            assertNotNull(clp);
            assertEquals(CommandLineParams.DEFAULT_DIRECTORY, clp.getDirectory());
            assertEquals(CommandLineParams.DEFAULT_START_OFFSET, clp.getStartTimeOffsetInSec());
        } catch (IllegalArgumentException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testParseCommandLineWithSomeGarbage() {
        System.out.println("Test parseCommandLine with valid command line Arguments but with some garbage present");
        final String commandLine = "g0 -d   ./  g1 -m    20 g3";
        try {
            final CommandLineParams clp = CommandLineParams.parseCommandLine(commandLine.split(" "));
            assertNotNull(clp);
            assertEquals(".", clp.getDirectory());
        } catch (IllegalArgumentException ex) {
            fail(ex.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseCommandLineWhenDirectoryMissing() {
        System.out.println("Test parseCommandLine fails when directory not specified");
        final String commandLine = "-d -m 20";
        CommandLineParams.parseCommandLine(commandLine.split(" "));
        fail("IllegalArgumentException should be thrown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseCommandLineWhenDirectoryIsInvalid() {
        System.out.println("Test parseCommandLine fails when directory is invalid");
        final String commandLine = "-d /notExstingirectory -m 20";
        CommandLineParams.parseCommandLine(commandLine.split(" "));
        fail("IllegalArgumentException should be thrown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseCommandLineWithInvalidTimeOffset() {
        System.out.println("Test parseCommandLine fails when time offset is invalid");
        final String commandLine = "-d ./ -m 20m";
        CommandLineParams.parseCommandLine(commandLine.split(" "));
        fail("IllegalArgumentException should be thrown");
    }
}
