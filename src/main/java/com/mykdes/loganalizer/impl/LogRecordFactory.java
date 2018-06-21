package com.mykdes.loganalizer.impl;

import com.mykdes.loganalizer.InvalidLogRecordException;
import com.mykdes.loganalizer.LogRecord;
import com.mykdes.loganalizer.LogRecordProducer;

/**
 *
 * @author mmykhaylov
 */
public class LogRecordFactory implements LogRecordProducer{

    
    public LogRecordFactory() {
    }

    /**
     * 
     * @param logRecordStr
     * @return new LogRecord instance
     * @throws InvalidLogRecordException 
     */
    @Override
    public LogRecord produce(String logRecordStr) {    
            return new LogRecordImpl(logRecordStr);
    }
    
    
}
