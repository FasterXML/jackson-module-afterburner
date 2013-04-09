package perftest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

public class TestDeserializePerf
{
    public final static class Bean
    {
        public int a, b, c, d;
        public int e, f, g, h;

        public Bean setUp() {
            a = 1;
            b = 999;
            c = -1000;
            d = 13;
            e = 6;
            f = -33;
            g = 0;
            h = 123456789;
            return this;
        }
        
        public void setA(int v) { a = v; }
        public void setB(int v) { b = v; }
        public void setC(int v) { c = v; }
        public void setD(int v) { d = v; }

        public void setE(int v) { e = v; }
        public void setF(int v) { f = v; }
        public void setG(int v) { g = v; }
        public void setH(int v) { h = v; }

        @Override
        public int hashCode() {
            return a + b + c + d + e + f + g + h;
        }
    }
    
    public static void main(String[] args) throws Exception
    {
//        JsonFactory f = new org.codehaus.jackson.smile.SmileFactory();
        JsonFactory f = new JsonFactory();
        ObjectMapper mapperSlow = new ObjectMapper(f);
        ObjectMapper mapperFast = new ObjectMapper(f);
        
        // !!! TEST -- to get profile info, comment out:
//        mapperSlow.registerModule(new AfterburnerModule());

        mapperFast.registerModule(new AfterburnerModule());
        new TestDeserializePerf().testWith(mapperSlow, mapperFast);
    }

    private void testWith(ObjectMapper slowMapper, ObjectMapper fastMapper)
        throws IOException
    {
        byte[] json = slowMapper.writeValueAsBytes(new Bean().setUp());
        boolean fast = true;
        
        while (true) {
            long now = System.currentTimeMillis();

            ObjectMapper m = fast ? fastMapper : slowMapper;
            Bean bean = null;
            
            for (int i = 0; i < 199999; ++i) {
                bean = m.readValue(json, Bean.class);
            }
            long time = System.currentTimeMillis() - now;
            
            System.out.println("Mapper (fast: "+fast+"; "+bean.hashCode()+"); took "+time+" msecs");

            fast = !fast;
        }
    }
   }
