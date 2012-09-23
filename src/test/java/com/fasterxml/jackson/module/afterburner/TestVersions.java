package com.fasterxml.jackson.module.afterburner;

import java.io.*;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

/**
 * Tests to verify that version information is properly accessible
 */
public class TestVersions extends AfterburnerTestBase
{
    /**
     * Not a good to do this, but has to do, for now...
     */
    private final static int MAJOR_VERSION = 2;
    private final static int MINOR_VERSION = 1;

    private final static String GROUP_ID = "com.fasterxml.jackson.module";
    private final static String ARTIFACT_ID = "jackson-module-afterburner";
    
    public void testMapperVersions() throws IOException
    {
        AfterburnerModule module = new AfterburnerModule();
        assertVersion(module);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private void assertVersion(Versioned vers)
    {
        final Version v = vers.version();
        assertFalse("Should find version information (got "+v+")", v.isUknownVersion());
        assertEquals(MAJOR_VERSION, v.getMajorVersion());
        assertEquals(MINOR_VERSION, v.getMinorVersion());
        // Check patch level initially, comment out for maint versions
	//        assertEquals(0, v.getPatchLevel());
        assertEquals(GROUP_ID, v.getGroupId());
        assertEquals(ARTIFACT_ID, v.getArtifactId());
    }
}

