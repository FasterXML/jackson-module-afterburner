package com.fasterxml.jackson.module.afterburner.deser;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerTestBase;

/**
 * Simple tests to try to see that handling of semi-standard types
 * from javax.* work.
 */
public class TestJavaxTypes extends AfterburnerTestBase
{
    public void testGregorianCalendar() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        DatatypeFactory f = DatatypeFactory.newInstance();
        XMLGregorianCalendar in = f.newXMLGregorianCalendar();
        in.setYear(2014);
        in.setMonth(3);
        
        String json = mapper.writeValueAsString(in);
        assertNotNull(json);
        XMLGregorianCalendar out = mapper.readValue(json, XMLGregorianCalendar.class);
        assertNotNull(out);
        
        // minor sanity check
        assertEquals(in.getYear(), out.getYear());
        
    }
}
