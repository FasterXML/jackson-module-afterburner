package com.fasterxml.jackson.module.afterburner;

import java.io.*;

import org.codehaus.jackson.map.ObjectMapper;

public class ManualTest
{
    public final static class IntBean
    {
        public int getA() { return 37; }
        public int getB() { return -123; }
        public int getC() { return 0; }
        public int getD() { return 999999; }
    }
    
    public static void main(String[] args) throws Exception
    {
        ObjectMapper mapperSlow = new ObjectMapper();
        ObjectMapper mapperFast = new ObjectMapper();
        
        // !!! TEST
//        mapperSlow.registerModule(new AfterburnerModule());

        mapperFast.registerModule(new AfterburnerModule());
        new ManualTest().testWith(mapperSlow, mapperFast);
    }

    private void testWith(ObjectMapper slowMapper, ObjectMapper fastMapper)
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean fast = false;
        final IntBean bean = new IntBean();
        
        while (true) {
            long now = System.currentTimeMillis();

            ObjectMapper m = fast ? fastMapper : slowMapper;
            int len = 0;
            
            for (int i = 0; i < 399999; ++i) {
                out.reset();
                m.writeValue(out, bean);
                len = out.size();
            }
            long time = System.currentTimeMillis() - now;
            
            System.out.println("Mapper (fast: "+fast+"; "+len+"); took "+time+" msecs");

                    fast = !fast;
        }
    }
   
}
