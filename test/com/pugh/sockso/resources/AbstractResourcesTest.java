
package com.pugh.sockso.resources;

import junit.framework.TestCase;

/**
 *
 * @author rod
 */
public class AbstractResourcesTest extends TestCase {
    
    public AbstractResourcesTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetLocalesFromFiles() {
        
        final String[] files = new String[] { "sockso.en.txt", "foo.bar", "sockso.it.txt", "car" };
        final String[] expected = new String[] { "en", "it" };
        final String[] actual = AbstractResources.getLocalesFromFiles( files );
        
        assertEquals( expected.length, actual.length );
        assertEquals( expected[0], actual[0] );
        assertEquals( expected[1], actual[1] );
        
    }
    
}
