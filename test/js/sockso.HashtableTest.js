
Hashtable = TestCase( 'sockso.Hashtable' );

Hashtable.prototype.testCreate = function() {

    expectAsserts( 1 );
    assertNotNull( new sockso.Hashtable() );
    
}

Hashtable.prototype.testGetAndSet = function() {

    var p = new sockso.Hashtable();
    
    assertEquals( '', p.get('foo') );
    assertEquals( 'baz', p.get('foo','baz') ); // use default

    p.set( 'foo', 'bar' );
    assertEquals( 'bar', p.get('foo') );
    assertEquals( 'bar', p.get('foo','baz') );

};

Hashtable.prototype.testSetData = function() {

    expectAsserts( 2 );

    var p = new sockso.Hashtable();

    assertEquals( '', p.get('foo') );

    p.setData({ foo: 'bar' });

    assertEquals( 'bar', p.get('foo') );

};
