package com.fasterxml.jackson.module.afterburner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestAccessFallback extends AfterburnerTestBase
{
    @SuppressWarnings("serial")
    static class BogusTestError extends IllegalAccessError {
        public BogusTestError(String msg) {
            super(msg);
        }
    }
    
    static class MyBean
    {
        private String e;

        public MyBean() { }

        MyBean(String e)
        {
            setE(e);
        }

        public void setE(String e)
        {
            for (StackTraceElement elem : new Throwable().getStackTrace()) {
                if (elem.getClassName().contains("Access4Jackson")) {
                    throw new BogusTestError("boom!");
                }
            }
            this.e = e;
        }

        public String getE()
        {
            for (StackTraceElement elem : new Throwable().getStackTrace()) {
                if (elem.getClassName().contains("Access4Jackson")) {
                    throw new BogusTestError("boom!");
                }
            }
            return e;
        }
    }

    private static final String BEAN_JSON = "{\"e\":\"a\"}";

    public void testSerializeAccess() throws Exception
    {
        ObjectMapper abMapper = mapperWithModule();
        assertEquals(BEAN_JSON, abMapper.writeValueAsString(new MyBean("a")));
    }

    public void testDeserializeAccess() throws Exception
    {
        ObjectMapper abMapper = mapperWithModule();
        MyBean bean = abMapper.readValue(BEAN_JSON, MyBean.class);
        assertEquals("a", bean.getE());
    }
}
