package com.mykdes.loganalizer;

import com.mykdes.loganalizer.impl.LogRecordFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mmykhaylov
 */
public class LogAnalizer {

    private static final Logger LOGGER = LogManager.getLogger(LogAnalizer.class); 
    
    private final LogRecordProducer recordProducer = new LogRecordFactory();
    private final LogFileFilter fileFilter = new LogFileFilter();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("Starting Log Analizer");
        
        try{
            final CommandLineParams params = CommandLineParams.parseCommandLine(args);
            (new LogAnalizer()).run(params);
            LOGGER.info("Log analizer is done...");
        }catch(Exception ex){
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * Not covered by Unit Tests because of lack of time 
     * The method selects log files from specified directory
     * The selected files are sorted. 
     * It is assumed that log records are sorted by date within each file and later files contain later log records
     * For each file  processFile is called 
*/
    
    private void run(final CommandLineParams params) {
        try {
            final Instant startDateTime = Instant.now().minusSeconds(params.getStartTimeOffsetInSec());
            fileFilter.filter(params.getAbsolutePath(), Instant.now().minusSeconds(params.getStartTimeOffsetInSec()))
                    .forEach(f -> processFile(f, startDateTime));
        } catch (IOException ex) {
            LOGGER.error("Error analazing log files: " + ex.getMessage());
        }
    }

    /**
     * Not covered by Unit Tests because of lack of time 
     * The method iterates through records in the given log file, converts log strings into LogRecord, 
     * filters LogRecords based on the LogRecord datetime and then outputs to console selected line
     * Note  that converting logRecords to logRecord instances allows easily implement more filtering options (for example by status or by remote host)
     * also it allows to collect some analytics.
     * These not implemented here because not required by the assignment and because of lack of time
     * 
     * If we have invalid log record (InvalidLogRecordException is thrown), we log it and ignore it   
    */
    private void processFile(final Path pathToFile, final Instant startDateTime) {
        final Function<String, LogRecord> producer =  (line) -> {
            try{
                return recordProducer.produce(line);
            }catch(InvalidLogRecordException ilr){
                LOGGER.warn("Ignoring invalid record: " + line, ilr);
                return null;
            }
        }; 
        
        try {
            Files.lines(pathToFile)
                    .map(line -> producer.apply(line))
                    .filter(record -> record !=null && startDateTime != null && startDateTime.isBefore(record.getResponseTime()))
                    .forEach(System.out::println);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
