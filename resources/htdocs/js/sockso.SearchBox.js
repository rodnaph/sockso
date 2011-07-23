
/**
 *  An input box which will search the collection and display the results
 *  below it in a drop down list
 *
 *  var search = new sockso.SearchBox();
 *  search.init( '#parentElement' );
 *
 */

sockso.SearchBox = function( options ) {

    var self = this;
    var results = null;
    var input = null;
    var searchInputTimeoutId = null;
    var keepFocusTimeoutId = null;
    var ajaxer = options.ajaxer;

    var onInputKeyUp = function() {

        // wait till there's a pause in typing before doing the search
        if ( searchInputTimeoutId != null ) {
            clearTimeout( searchInputTimeoutId );
        }

        searchInputTimeoutId = setTimeout( function() {
            self.makeQuery( input.attr('value') );
        }, '1000' );

    };

    var onInputBlur = function() {

        keepFocusTimeoutId = setTimeout(function() {
            results.hide();
        }, 500 );

    };

    var onInputFocus = function() {

        // if there's something in the search field, and we have results...
        if ( $(this).attr('value') != undefined && $('li',results).length > 0 ) {
            results.show();
        }

    };

    /**
     *  initiates the query for whatever is in the search input field
     *
     */

    this.makeQuery = function( query ) {

        if ( query == undefined || query == '' )
            results.hide();

        else {
            $.getJSON(
                Properties.getUrl('/json/search/' + encodeURIComponent(query)),
                {},
                function( data ) {
                    self.showResults( data );
                }
            );
        }

    };

    /**
     *  Makes a MusicItem object from a result object
     * 
     *  @return MusicItem
     *  
     */
    
    this.makeMusicItem = function( item ) {

        return new MusicItem(
            item.id,
            item.name
        );
        
    };

    /**
     *  updates the search results with those specified and then
     *  shows the search drop-down box
     *
     */

    this.showResults = function( items ) {

        results.empty();

        if ( items.length > 0 ) {
            for ( var i=0; i<items.length; i++ ) {
                var item = this.makeMusicItem( items[i] );
                results.append( sockso.util.getMusicElement(item,true) );
            }
        }

        else {
            $( '<li></li>' )
                .append( 'Nothing found sorry...' )
                .appendTo( results );
        }

        // show element
        results.show();

        ajaxer.attach( results );

    };

    /**
     *  Takes a parent ID to attach the search box functionality to and
     *  creates all the required elements, etc...
     *
     *  @param parentId
     *
     */

    this.init = function( parentId ) {

        input = $( '<input type="text" />' )
                        .addClass( 'input search-input' )
                        .attr({ id: 'search-input' }) // @TODO move to class css
                        .appendTo( parentId );
        var pos = input.offset();

        results = $( '<ul></ul>' )
                        .css({
                            top: (pos.top + 13) + 'px',
                            left: pos.left + 'px'
                        })
                        .addClass( 'search-results' )
                        .attr({ id: 'search-results' }) // @TODO - move to class css
                        .hide()
                        .click(function() {
                            // if there's a timeout waiting to hide the results,
                            // then we want to clear it.
                            if ( keepFocusTimeoutId != null ) {
                                clearTimeout( keepFocusTimeoutId );
                            }
                            input.focus();
                        })
                        .appendTo( 'body' );

        // add handlers to search input

        input.keyup( onInputKeyUp )
             .blur( onInputBlur )
             .focus( onInputFocus );

    };

};
