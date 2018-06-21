package com.mykdes.loganalizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author mmykhaylov
 */
public class LogFileFilter {
    private static final Comparator<Path> PATH_BYNAME_COMPARATOR = Comparator.comparing(path -> path.toString()); 

    public LogFileFilter() {
    }
    /**
     * @param logDirectory - directory where log files are located
     * @param startDateTime - a start datetime - only files modified after the time are returned 
     * @return sorted set of log files
     * @throws java.io.IOException
     * 
     * The filter excludes directories, selects only files with extension ".log".  
     * other option would be to use regex: "[0-9a-zA-Z]+-[0-9]{2,6}\.log" to examine filenames
     * The filter sorts files by filename (for simplicity and assuming given in the assignment log names pattern),
     * other sorting options include sorting by last file modification date or file index parsed out of the file name.
     * The filter selects only files that has been modified after given time. 
     */
    public SortedSet<Path> filter( final Path logDirectory,  final Instant startDateTime) throws IOException{
        final SortedSet<Path> filteredList = new TreeSet(PATH_BYNAME_COMPARATOR);
        Files.newDirectoryStream(logDirectory, ( filepath ) -> { 
                    return  filepath.toString().endsWith(".log")  && 
                            filepath.toFile().isFile() && 
                            Files.getLastModifiedTime(filepath).toInstant().isAfter(startDateTime);
                }).forEach( path -> filteredList.add(path) );
        return filteredList;
    }
}
