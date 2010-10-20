
BaseTest = TestCase( 'sockso.BaseTest' );

BaseTest.prototype.testConstructor = function() {

    assertNotNull( new sockso.Base({}) );

};