
LocaleTest = TestCase( 'sockso.LocaleTest' );

LocaleTest.prototype.testConstructor = function() {

    expectAsserts( 1 );
    assertNotNull( new sockso.Locale() );

};

LocaleTest.prototype.testSetData = function() {

    expectAsserts( 2 );

    var l = new sockso.Locale();

    assertEquals( '', l.getString('foo') );

    l.setData({ foo: 'bar' });
    
    assertEquals( 'bar', l.getString('foo') );

};

LocaleTest.prototype.testGetWithReplacements = function() {

    expectAsserts( 2 );

    var l = new sockso.Locale();
    l.setData({ foo: 'bar %1' });

    assertEquals( 'bar %1', l.getString('foo') );
    assertEquals( 'bar baz', l.getString('foo',['baz']) );

};
