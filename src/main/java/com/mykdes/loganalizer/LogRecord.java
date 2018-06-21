package com.mykdes.loganalizer;

import java.time.Instant;


/**
 *
 * @author mmykhaylov
 */
public interface LogRecord {
    
    String getRemoteHost();
    String getClientIdentity();
    String getRemoteUser();
    Instant getResponseTime();
    String getRequest();
    int    getStatus();
    long   getResponseSize();
    
    default String getLogRecord(){
        return toString();
    };
    
}
