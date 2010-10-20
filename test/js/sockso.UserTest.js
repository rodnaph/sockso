
UserTest = TestCase( 'sockso.UserTest' );

UserTest.prototype.testConstructor = function() {

    assertNotNull( new sockso.User({}) );

};