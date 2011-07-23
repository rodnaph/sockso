
package com.pugh.sockso.tests;

public class SocksoTestCaseTest extends SocksoTestCase {
    
    public void testAssertOccurrancesFindsPositiveResult() {
        assertSubstringCount( 2, "a bb a bb", "bb" );
    }
    
}
