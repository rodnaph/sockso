
TestCase( 'Sockso Core' ).prototype = {

    testBindAllowsBindingFunctionsToAScope: function() {
        var scope = { foo: 'bar' };
        var called = false;
        var func = function() {
            called = true;
            assertEquals( this, scope );
        };
        (func.bind( scope ))();
        assertTrue( called );
    },

    testStringStartsWith: function() {
        var str = "thisbar";
        assertTrue( str.startsWith("this") );
        assertFalse( str.startsWith("bar") );
        assertFalse( str.startsWith("THIS") );
    },

    testStringEndsWith: function() {
        var str = "thisbar";
        assertTrue( str.endsWith("bar") );
        assertFalse( str.endsWith("THis") );
        assertFalse( str.endsWith("BAR") );
    }

};
