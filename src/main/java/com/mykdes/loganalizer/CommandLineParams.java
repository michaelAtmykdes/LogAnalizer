package com.mykdes.loganalizer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mmykhaylov
 * 
 * The class allows for relax parameter validation and disregards not understood command line arguments. 
 * In case of required arguments missing it uses default. 
 * Alternatively strict argument validation may be implemented with usage note etc...   
 */
public class CommandLineParams {
    private static final Logger LOGGER = LogManager.getLogger(CommandLineParams.class);
    
    //Current directory
    public static final String DEFAULT_DIRECTORY = ".";
    // default time offset is last 24 hours in sec
    public static final long DEFAULT_START_OFFSET = 24 * 60 * 60;

    private final Path directory;
    private final long startTimeOffset;

    private CommandLineParams() {
        this(null, 0);
    }

    private CommandLineParams(final Path directory, long startTime) {
        this.directory = directory == null ? Paths.get(DEFAULT_DIRECTORY) : directory;
        this.startTimeOffset = startTime > 0 ? startTime :  DEFAULT_START_OFFSET;
    }

    /**
     *
     * @param commandLine
     * @return instance of CommandLineParams
     * @throws IllegalArgumentException when invalid directory or time offset in
     * minutes specified
     *
     */
    public static CommandLineParams parseCommandLine(String[] commandLine) {
        if (commandLine == null || commandLine.length==0) {
            LOGGER.debug("Empty command line, using default parameters");
            return new CommandLineParams();
        }
        long start_time = 0;
        Path dir = null;
        //split command line into arguments array and filter out empty elements 
        final String[] args = Stream.of( commandLine )
                                .map(x->x.trim())
                                .filter(x->!x.isEmpty())
                                .toArray(String[]::new); 
        if (args != null && args.length > 1) {
            for (int i = 0; i < args.length - 1; ++i) {
                final ParamNames paramName = ParamNames.fromString(args[i]);
                if (paramName != null) {
                    switch (paramName) {
                        case DIRECTORY_PARAM:
                            dir = getCheckedDirectory(args[i + 1]);
                            ++i;
                            break;
                        case MINUTES_OFFSET_PARAM:
                            start_time =  getCheckedTimeOffsetInSec(args[i + 1]) ;
                            ++i;
                            break;
                        default:
                    }
                }
            }
        }
        LOGGER.debug("Log Directory " + dir + " time offset " + start_time);
        return new CommandLineParams(dir, start_time);
    }

    public String getDirectory() {
        return directory.toString();
    }
    public Path getAbsolutePath() {
        return directory.toAbsolutePath();
    }

    public long getStartTimeOffsetInSec() {
        return startTimeOffset;
    }

    private static Path getCheckedDirectory(String dir) {
        final Path path = Paths.get(dir);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Invalid log source directory: " + dir);
        }
        return path;
    }

    private static long getCheckedTimeOffsetInSec(String mins) {
        try {
            return Long.valueOf(mins)* 60;
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Invalid time offset in minutes : " + mins);
        }
    }
}
