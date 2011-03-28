
/**
 * Hijacks anchor links to load them via ajax
 *
 */
sockso.Ajaxer = function( options ) {

    this.page = options.page || window.page;

};

/**
 * Hijack all standard js links in the container
 *
 */
sockso.Ajaxer.prototype.init = function() {


    if ( !history.pushState ) { return; }

    this.attach();

    window.onpopstate = this.onPopState.bind( this );

};

/**
 * Handler for when a window history state has been popped
 *
 * @param evt
 *
 */
sockso.Ajaxer.prototype.onPopState = function( evt ) {

    if ( evt.state ) {
        this.loadUrl( evt.state.url );
    }

};

/**
 * Attach the ajaxer to the anchors in the specified container
 *
 * @param container
 */
sockso.Ajaxer.prototype.attach = function( container ) {

    var self = this;

    $( 'a:not([href^=javascript])', container ).click(function() {
        
        var link = $( this );
        var href = link.attr( 'href' );

        self.loadUrl( href );
        
        return false;
        
    });

};

sockso.Ajaxer.prototype.loadUrl = function( href ) {

    var self = this;
    var extraArgs = 'format=ajax';
    var ajaxHref = href;

    ajaxHref += ( href.indexOf('&') !== -1 || href.indexOf('?') !== -1 ) ? '&' : '?';
    ajaxHref += extraArgs;

    $.ajax({
        method: 'GET',
        url: href,
        success: function( html ) {
            self.onLoadUrl( html, href );
        }
    });

};

/**
 * A URL has been loaded, render it
 *
 * @param html
 * @param href
 */
sockso.Ajaxer.prototype.onLoadUrl = function( html, href ) {

    var newContent = $( '#content', html );
    var newTitle = $( 'title' ).html();

    $( '#content' )
        .replaceWith( newContent );

    this.attach( newContent );
    this.page.initContent();
    this.setUrl( href, newTitle );

};

/**
 * Sets the page URL and title
 *
 * @param url
 * @param title
 */
sockso.Ajaxer.prototype.setUrl = function( url, title ) {

    history.pushState(
        { url: url },
        title,
        url
    );

};
