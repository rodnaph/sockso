
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

PropertiesTest.prototype.testStartsWith = function() {
    
    var p = new sockso.Properties();

};

PropertiesTest.prototype.testEndsWith = function() {

};

PropertiesTest.prototype.testGetUrl = function() {

    expectAsserts( 9 );

    var p = new sockso.Properties();
    
    assertEquals( '/foo', p.getUrl('foo') );
    assertEquals( '/foo', p.getUrl('/foo') );
    
    assertEquals( '/file/skins/original/foo', p.getUrl('<skin>/foo') );
    assertEquals( '/file/skins/original/foo', p.getUrl('/<skin>/foo') );
    
    p.set('www.skin','other');

    assertEquals( '/file/skins/other/foo', p.getUrl('<skin>/foo') );
    
    p.set('server.basepath','other');
    
    assertEquals( '/other/file/skins/other/foo', p.getUrl('<skin>/foo') );
    
    p.set('server.basepath','http://other.com');

    assertEquals( 'http://other.com/file/skins/other/foo', p.getUrl('<skin>/foo') );
    
    assertEquals( 'http://test.com/foo', p.getUrl('http://test.com/foo') );
    assertEquals( 'https://test.com/foo', p.getUrl('https://test.com/foo') );

};
