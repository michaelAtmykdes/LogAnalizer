package com.mykdes.loganalizer.impl;

import com.mykdes.loganalizer.InvalidLogRecordException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class LogRecordImplTest {
    
    public LogRecordImplTest() {
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
     * Test of create LogRecordImpl instance from log record.
     * @throws java.text.ParseException
     */
    @Test
    public void testCreateLogRecordFromString() throws ParseException {
        final String logRecord = "127.0.0.1 user-identifier frank [10/Oct/2017:13:54:00 +0000] \"GET /api/endpoint HTTP/1.0\" 200 5134";
        
        final LogRecordImpl logRecImpl = new LogRecordImpl(logRecord);
   
        assertNotNull(logRecImpl);
        
        assertEquals(logRecImpl.getRemoteHost(), "127.0.0.1");
        assertEquals(logRecImpl.getClientIdentity(), "user-identifier");
        assertEquals(logRecImpl.getRemoteUser(), "frank");
        assertEquals(logRecImpl.getResponseSize(), 5134);
        assertEquals(logRecImpl.getStatus(), 200);
        assertEquals(logRecImpl.getRequest(), "GET /api/endpoint HTTP/1.0");
        
        final SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z"); 
        final Date recDate = format.parse("10/Oct/2017:13:54:00 +0000");
        assertEquals( recDate.toInstant(), logRecImpl.getResponseTime());
    }
    @Test(expected =  InvalidLogRecordException.class)
    public void testCreateLogRecordFromInvalidString() throws ParseException {
        //no user-identifier
        final String logRecord = "127.0.0.1  frank [10/Oct/2017:13:54:00 +0000] \"GET /api/endpoint HTTP/1.0\" 200 5134";      
        final LogRecordImpl logRecImpl = new LogRecordImpl(logRecord);
        fail("The log format is invalid, InvalidLogRecordException had to be thrown");
    
    }

    
    
    
}
