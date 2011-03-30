
/**
 * Hijacks anchor links to load them via ajax
 *
 * @param options
 */
sockso.Ajaxer = function( options ) {

    this.page = options.page;

};

/**
 * Hijack all standard js links in the container
 *
 */
sockso.Ajaxer.prototype.init = function() {

    if ( history && history.pushState ) {
        this.attach();
        $( window ).bind(
            'popstate',
            this.onPopState.bind( this )
        );
    }

};

/**
 * Handler for when a window history state has been popped
 *
 * @param evt
 *
 */
sockso.Ajaxer.prototype.onPopState = function( evt ) {

    var state = evt.originalEvent.state;

    if ( state ) {
        this.loadUrl( state.url );
    }

};

/**
 * Attach the ajaxer to the anchors in the specified container
 *
 * @param container
 */
sockso.Ajaxer.prototype.attach = function( container ) {

    $( 'a:not([href^=javascript])', container )
        .click( this.onClick.bind(this) );

};

/**
 * A link has been clicked
 *
 */
sockso.Ajaxer.prototype.onClick = function( evt ) {
        
    var link = $( evt.target );
    var href = link.attr( 'href' );

    this.loadUrl( href );

    history.pushState( {url:href}, '', href );

    return false;

};

/**
 * Load a new URL into the page
 *
 * @param href
 *
 */
sockso.Ajaxer.prototype.loadUrl = function( href ) {

    $.ajax({
        method: 'GET',
        url: href,
        success: this.onLoadUrl.bind( this )
    });

};

/**
 * A URL has been loaded, render it
 *
 * @param html
 */
sockso.Ajaxer.prototype.onLoadUrl = function( html ) {

    var newContent = $( '#content', html );
    var newTitle = html.match( new RegExp('<title>(.*?)</title>') )[ 1 ];

    $( '#content' ).replaceWith( newContent );

    this.attach( newContent );
    this.page.initContent();
    this.setTitle( newTitle );

};

/**
 * Sets the page title
 *
 * @param title
 */
sockso.Ajaxer.prototype.setTitle = function( title ) {

    document.title = title;

};
