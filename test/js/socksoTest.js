
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
    }

};
