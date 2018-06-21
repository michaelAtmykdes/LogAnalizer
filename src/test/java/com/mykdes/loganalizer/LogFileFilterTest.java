package com.mykdes.loganalizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michaelmykhaylov
 */
public class LogFileFilterTest {

    public LogFileFilterTest() {
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
     * Test of filter method, of class LogFileFilter.
     */
    @Test
    public void testFilter() {
        System.out.println("filter");
        final Path logDirectory = Paths.get("./logs");
        final Instant startDateTime = Instant.now().minus(60, ChronoUnit.DAYS);
        final LogFileFilter instance = new LogFileFilter();

        final Function< Path, Instant> mapFunction = path -> {
            try {
                return Files.getLastModifiedTime(path).toInstant();
            } catch (IOException ex) {
                fail(ex.getMessage());
            }
            return null;
        };
        try {
            final SortedSet<Path> result = instance.filter(logDirectory, startDateTime);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            result.stream()
                    .map(path -> mapFunction.apply(path))
                    .forEach(modifiedInstant -> assertTrue(startDateTime.isBefore(modifiedInstant)));
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

}
