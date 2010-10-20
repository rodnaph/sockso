
UploadForm = TestCase( 'sockso.UploadForm' );

UploadForm.prototype.testValidateTextInput = function() {

    var formId = 'myUploadForm';
    var form = new sockso.UploadForm( formId );
    var input = $( '<input type="text" />' )
                    .val( 'something' );

    form.error = function() {}; // disable alert();

    $( '<form></form>' )
        .attr({ id: formId })
        .append( input )
        .appendTo( 'body' );

    assertTrue( form.validate() );

    input.val( '' );

    assertFalse( form.validate() );

};
