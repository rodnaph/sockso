
Session = TestCase( 'sockso.Session' );

Session.prototype.testConstructor = function() {

    expectAsserts( 1 );
    assertNotNull( new sockso.Session() );

};

Session.prototype.testSetAndGet = function() {

    expectAsserts( 1 );

    var sess = new sockso.Session();
    var value = 'jaskdjakdjaldjalsdj';

    sess.set( 'foo', value );

    sess.get('foo',function(data) {
        assertEquals( value, data );
    });

};
