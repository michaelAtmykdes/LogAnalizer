package com.mykdes.loganalizer;

/**
 *
 * @author mmykhaylov
 * InvalidLogRecordException is by design unchecked: 
 * in case application needs to ignore the exception it should handle it
 * 
 */
public class InvalidLogRecordException extends IllegalArgumentException{
    public InvalidLogRecordException() {
    }
    public InvalidLogRecordException(String message) {
        super(message);
    }    
}
