
PropertiesTest = TestCase( 'sockso.Properties' );

PropertiesTest.prototype.testCreate = function() {

    expectAsserts( 1 );
    assertNotNull( new sockso.Properties() );
    
}

PropertiesTest.prototype.testGetAndSet = function() {

    var p = new sockso.Properties();
    
    assertEquals( '', p.get('foo') );
    assertEquals( 'baz', p.get('foo','baz') ); // use default

    p.set( 'foo', 'bar' );
    assertEquals( 'bar', p.get('foo') );
    assertEquals( 'bar', p.get('foo','baz') );

};

PropertiesTest.prototype.testSetData = function() {

    expectAsserts( 2 );

    var p = new sockso.Properties();

    assertEquals( '', p.get('foo') );

    p.setData({ foo: 'bar' });

    assertEquals( 'bar', p.get('foo') );

};
