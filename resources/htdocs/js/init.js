
if ( !window.Properties ) {
    Properties = new sockso.Properties();
}

$(function() {

    var page = new sockso.Page();
    
    page.initLayout();
    page.initContent();

    window.page = page;

});
