package com.mykdes.loganalizer.impl;

import com.mykdes.loganalizer.InvalidLogRecordException;
import com.mykdes.loganalizer.LogRecord;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mmykhaylov
 * The class is not public to hide implementation details
 * Immutable
 * hashCode/equals implemented 
 *  
 */
class LogRecordImpl implements LogRecord {

    private static final Logger LOGGER = LogManager.getLogger(LogRecordImpl.class);

    
    /**
     * Thread-safe date formatter
     *
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    /*
        original log record, increase a memory usage a bit to improve performance of toString and avoid additional formatting
        this wouldn't work if output record format being different from input.  In that case dynamic formatting would be needed (in constructor or on demand in toString impl)
     */
    private final String originalRecord;

    private final String remoteHost;
    private final String clientIdentity;
    private final String remoteUser;
    private final Instant responseTime;
    private final String request;
    private final int status;
    private final long responseSize;

    /**
     * 
     * @param logRecordStr
     * @throws InvalidLogRecordException 
     */
    
    public LogRecordImpl(String logRecordStr){
        this.originalRecord = logRecordStr;
        if (logRecordStr == null || logRecordStr.isEmpty()) {
            throw new InvalidLogRecordException("Invalid log record");
        }
        /* 
                    Assuming CLF -  "%h %l %u %t \"%r\" %>s %b"
                    it is possible to use regex 
                    but it's slow especially when processing millions of records
                    The algorithm below is chosen for simplcity and clarity but quite brittle and very much format dependant (several spaces in row will cause parsing failure, etc...  )
                    Performance:  O(n) as of java 7  (indexOf and substring) where n is the length of the logRecordStr
        */
        int hostStart = 0,
            hostEnd = checkIndex(logRecordStr.indexOf(" "));
        int identStart = hostEnd + 1,
            identEnd = checkIndex(logRecordStr.indexOf(" ", identStart+1));
        int userStart = identEnd + 1,
            userEnd = checkIndex(logRecordStr.indexOf(" ", userStart+1));
        int dateStart = checkIndex(logRecordStr.indexOf("[", userEnd)) + 1,
            dateEnd = checkIndex(logRecordStr.indexOf("]", dateStart+1));
        int requestStart = checkIndex(logRecordStr.indexOf("\"", dateEnd+1)) + 1,
            requestEnd = checkIndex(logRecordStr.indexOf("\"", requestStart+1));
        int statusStart = requestEnd + 1,
                statusEnd = checkIndex(logRecordStr.indexOf(" ", statusStart+1));
        int responseSizeStart = statusEnd + 1;
        
        this.remoteHost = logRecordStr.substring(hostStart, hostEnd).trim();
        this.clientIdentity = logRecordStr.substring(identStart, identEnd).trim();
        this.remoteUser = logRecordStr.substring(userStart, userEnd).trim();
        this.responseTime = parseDateTimeFromString(logRecordStr.substring(dateStart, dateEnd).trim());
        this.request = logRecordStr.substring(requestStart, requestEnd);
        
        try{
            this.status = Integer.parseInt(logRecordStr.substring(statusStart, statusEnd).trim());
            //Resonse size may be not known:  "-"
            String respSizeStr = logRecordStr.substring(responseSizeStart).trim(); 
            this.responseSize = respSizeStr.equals("-") ? 0 : Integer.parseInt(respSizeStr);
        }catch(NumberFormatException nfex){
           LOGGER.error("Invalid log record: ", nfex);
           throw new InvalidLogRecordException("Invalid log record: "  + nfex.getMessage());
        }
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public String getClientIdentity() {
        return clientIdentity;
    }

    @Override
    public String getRemoteUser() {
        return remoteUser;
    }

    
    /*
        Instant is immutable so returning it does not break class immutability
    */
    @Override
    public  Instant getResponseTime() {
        return responseTime ;
    }

    @Override
    public String getRequest() {
        return request;
    }
    
    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public long getResponseSize() {
        return responseSize;
    }

    /**
     * Assuming default formatting: [day/month/year:hour:minute:second zone] day
     * = 2*digit month = 3*letter year = 4*digit hour = 2*digit minute = 2*digit
     * second = 2*digit zone = (`+' | `-') 4*digit
     * throws InvalidLogRecordException
     * @return Instant
     */
    private Instant parseDateTimeFromString(String dateTimeStr)  {
        try {
            return Instant.from(FORMATTER.parse(dateTimeStr));
        } catch (DateTimeParseException dpe) {
            LOGGER.error("Invalid date format: "  + dateTimeStr, dpe );
            throw new InvalidLogRecordException("Invalid date format " + dateTimeStr + " : " + dpe.getMessage());
        }
    }

    private int checkIndex(int index) throws InvalidLogRecordException {
        if (index == -1) {
            throw new InvalidLogRecordException("value not found");
        }
        return index;
    }

    @Override
    public String toString() {
        /**
         * As we are storing originalRecord we can use it here, otherwise we
         * would need to format the record;
         */
        return originalRecord;
    }

    /**  
     * It is enough to use only originalRecord  for hashCode and equals as 
     * the rest of the fields are calculated based on originalRecord and all the fields are immutable   
    */
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.originalRecord);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogRecordImpl other = (LogRecordImpl) obj;
        if (!Objects.equals(this.originalRecord, other.originalRecord)) {
            return false;
        }
        return true;
    }
}
