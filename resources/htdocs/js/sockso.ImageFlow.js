/***
 *	ImageFlow 0.8
 *
 *	This code is based on Michael L. Perrys Cover flow in Javascript.
 *	For he wrote that "You can take this code and use it as your own" [1]
 *	this is my attempt to improve some things. Feel free to use it! If
 *	you have any questions on it leave me a message in my shoutbox [2].
 *
 *	The reflection is generated server-sided by a slightly hacked
 *	version of Richard Daveys easyreflections [3] written in PHP.
 *
 *	The mouse wheel support is an implementation of Adomas Paltanavicius
 *	JavaScript mouse wheel code [4].
 *
 *	Thanks to Stephan Droste ImageFlow is now compatible with Safari 1.x.
 *
 *	[1] http://www.adventuresinsoftware.com/blog/?p=104#comment-1981
 *	[2] http://shoutbox.finnrudolph.de/
 *	[3] http://reflection.corephp.co.uk/v2.php
 *	[4] http://adomas.org/javascript-mouse-wheel/
 *
 *      NOTE: This script has been modified for use in Sockso.
 *
 */
sockso.ImageFlow = function() {}

/**
 * Initialise imageflow for this page
 *
 */
sockso.ImageFlow.prototype.init = function() {

    // Configuration variables
    var conf_reflection_p = 0.5;         // Sets the height of the reflection in % of the source image
    var conf_focus = 4;                  // Sets the numbers of images on each side of the focussed one
    var conf_slider_width = 14;          // Sets the px width of the slider div
    var conf_images_cursor = 'pointer';  // Sets the cursor type for all images default is 'default'
    var conf_slider_cursor = 'default';  // Sets the slider cursor type: try "e-resize" default is 'default'

    // Id names used in the HTML
    var conf_imageflow = 'imageflow';    // Default is 'imageflow'
    var conf_loading = 'loading';        // Default is 'loading'
    var conf_images = 'images';          // Default is 'images'
    var conf_captions = 'captions';      // Default is 'captions'
    var conf_scrollbar = 'scrollbar';    // Default is 'scrollbar'
    var conf_slider = 'slider';          // Default is 'slider'

    // Define global variables
    var caption_id = 0;
    var new_caption_id = 0;
    var current = 0;
    var target = 0;
    var mem_target = 0;
    var timer = 0;
    var array_images = new Array();
    var new_slider_pos = 0;
    var dragging = false;
    var dragobject = null;
    var dragx = 0;
    var posx = 0;
    var new_posx = 0;
    var xstep = 150;

    function step()
    {
            switch (target < current-1 || target > current+1)
            {
                    case true:
                            moveTo(current + (target-current)/3);
                            window.setTimeout(step, 50);
                            timer = 1;
                            break;

                    default:
                            timer = 0;
                            break;
            }
    }

    function glideTo(x, new_caption_id)
    {
            // Animate gliding to new x position
            target = x;
            mem_target = x;
            if (timer == 0)
            {
                    window.setTimeout(step, 50);
                    timer = 1;
            }

            // Display new caption
            caption_id = new_caption_id;
             var imgItem = img_div.childNodes.item( array_images[caption_id] );
            captionText = imgItem.getAttribute( 'alt' );
             var id = imgItem.getAttribute( 'src' ).match( /^.*\/(.*?)$/ )[ 1 ];
            if (captionText == '') captionText = '&nbsp;';
            caption_div.innerHTML = captionText;

             sockso.util.getMusicElement( new MusicItem(id,captionText), true )
                .appendTo( $(caption_div).empty() );

            // Set scrollbar slider to new position
            if (dragging == false)
            {
                    new_slider_pos = (scrollbar_width * (-(x*100/((max-1)*xstep))) / 100) - new_posx;
                    slider_div.style.marginLeft = (new_slider_pos - conf_slider_width) + 'px';
            }
    }

    function moveTo(x)
    {
            current = x;
            var zIndex = max;

            // Main loop
            for (var index = 0; index < max; index++)
            {
                    var image = img_div.childNodes.item(array_images[index]);
                    var current_image = index * -xstep;

                    // Don't display images that are not conf_focussed
                    if ((current_image+max_conf_focus) < mem_target || (current_image-max_conf_focus) > mem_target)
                    {
                            image.style.visibility = 'hidden';
                            image.style.display = 'none';
                    }
                    else
                    {
                            var z = Math.sqrt(10000 + x * x) + 100;
                            var xs = x / z * size + size;

                            // Still hide images until they are processed, but set display style to block
                            image.style.display = 'block';

                            // Process new image height and image width
                            var new_img_h = (image.h / image.w * image.pc) / z * size;
                            switch ( new_img_h > max_height )
                            {
                                    case false:
                                            var new_img_w = image.pc / z * size;
                                            break;

                                    default:
                                            new_img_h = max_height;
                                            var new_img_w = image.w * new_img_h / image.h;
                                            break;
                            }
                            var new_img_top = (images_width * 0.34 - new_img_h) + images_top + ((new_img_h / (conf_reflection_p + 1)) * conf_reflection_p);

                            // Set new image properties
                            image.style.left = xs - (image.pc / 2) / z * size + images_left + 'px';
                            image.style.height = new_img_h + 'px';
                            image.style.width = new_img_w + 'px';
                            image.style.top = (new_img_top - 90) + 'px';
                            image.style.visibility = 'visible';

                            // Set image layer through zIndex
                            switch ( x < 0 )
                            {
                                    case true:
                                            zIndex++;
                                            break;

                                    default:
                                            zIndex = zIndex - 1;
                                            break;
                            }

                            // Change zIndex and onclick function of the focussed image
                            switch ( image.i == caption_id )
                            {
                                    case false:
                                            image.onclick = function() { glideTo(this.x_pos, this.i); };
                                            break;

                                    default:
                                            zIndex = zIndex + 1;
                                            image.onclick = function() {

                                              // work out the type and id of the music item

                                              var data = this.src.match( /^.*\/(\w+\d+)$/ );
                                              if ( data.length > 1 ) {

                                                  var type = ( data[1].substring( 0, 2 ) == 'ar' )
                                                      ? 'artist' : 'album';
                                                  var id = data[1].substring( 2 );

                                                  document.location = '/browse/' +type+ '/' +id;

                                              }

                                            };
                                            break;
                            }
                            image.style.zIndex = zIndex;
                    }
                    x += xstep;
            }
    }

    // Main function
    function refresh(onload)
    {
            // Cache document objects in global variables
            imageflow_div = document.getElementById(conf_imageflow);
            img_div = document.getElementById(conf_images);
            scrollbar_div = document.getElementById(conf_scrollbar);
            slider_div = document.getElementById(conf_slider);
            caption_div = document.getElementById(conf_captions);

            // Cache global variables, that only change on refresh
            images_width = img_div.offsetWidth;
            images_top = imageflow_div.offsetTop;
            images_left = imageflow_div.offsetLeft;
            max_conf_focus = conf_focus * xstep;
            size = images_width * 0.5;
            scrollbar_width = images_width * 0.6;
            conf_slider_width = conf_slider_width * 0.5;
            max_height = images_width * 0.51;

            // Change imageflow div properties
            imageflow_div.style.height = (max_height - 20) + 'px';

            // Change images div properties
            img_div.style.height = images_width * 0.338 + 'px';

            // Change captions div properties
            caption_div.style.width = images_width + 'px';
            caption_div.style.marginTop = images_width * 0.03 + 'px';

            // Change scrollbar div properties
            scrollbar_div.style.marginTop = images_width * 0.02 + 'px';
            scrollbar_div.style.marginLeft = images_width * 0.2 + 'px';
            scrollbar_div.style.width = scrollbar_width + 'px';

            // Set slider attributes
            slider_div.onmousedown = function () { dragstart(this); };
            slider_div.style.cursor = conf_slider_cursor;

            // Cache EVERYTHING!
            max = img_div.childNodes.length;
            var i = 0;
            for (var index = 0; index < max; index++)
            {
                    var image = img_div.childNodes.item(index);
                    if (image.nodeType == 1)
                    {
                            array_images[i] = index;

                            // Set image onclick by adding i and x_pos as attributes!
                            image.onclick = function() { glideTo(this.x_pos, this.i); };
                            image.x_pos = (-i * xstep);
                            image.i = i;

                            // Add width and height as attributes ONLY once onload
                            if(onload == true)
                            {
                                    image.w = image.width;
                                    image.h = image.height;
                            }

                            // Check source image format. Get image height minus reflection height!
                            switch ((image.w + 1) > (image.h / (conf_reflection_p + 1)))
                            {
                                    // Landscape format
                                    case true:
                                            image.pc = 118;
                                            break;

                                    // Portrait and square format
                                    default:
                                            image.pc = 100;
                                            break;
                            }

                            // Set ondblclick event
                            image.url = image.getAttribute('longdesc');

                            // Set image cursor type
                            image.style.cursor = conf_images_cursor;

                            i++;
                    }
            }
            max = array_images.length;

            // Display images in current order
            moveTo(current);
            glideTo(current, caption_id);
    }

    // Show/hide element functions
    function show(id)
    {
            var element = document.getElementById(id);
            element.style.visibility = 'visible';
    }
    function hide(id)
    {
            var element = document.getElementById(id);
            element.style.visibility = 'hidden';
            element.style.display = 'none';
    }

    // Refresh ImageFlow on window resize
    window.onresize = function()
    {
            if(document.getElementById(conf_imageflow)) refresh();
    };

    // Handle the wheel angle change (delta) of the mouse wheel
    function handle(delta)
    {
            var change = false;
            switch (delta > 0)
            {
                    case true:
                            if(caption_id >= 1)
                            {
                                    target = target + xstep;
                                    new_caption_id = caption_id - 1;
                                    change = true;
                            }
                            break;

                    default:
                            if(caption_id < (max-1))
                            {
                                    target = target - xstep;
                                    new_caption_id = caption_id + 1;
                                    change = true;
                            }
                            break;
            }

            // Glide to next (mouse wheel down) / previous (mouse wheel up) image
            if (change == true)
            {
                    glideTo(target, new_caption_id);
            }
    }

    // Event handler for mouse wheel event
    function wheel(event)
    {
            var delta = 0;
            if (!event) event = window.event;
            if (event.wheelDelta)
            {
                    delta = event.wheelDelta / 120;
            }
            else if (event.detail)
            {
                    delta = -event.detail / 3;
            }
            if (delta) handle(delta);
            if (event.preventDefault) event.preventDefault();
            event.returnValue = false;
    }

    // Initialize mouse wheel event listener
    function initMouseWheel()
    {
            if(window.addEventListener) imageflow_div.addEventListener('DOMMouseScroll', wheel, false);
            imageflow_div.onmousewheel = wheel;
    }

    // This function is called to drag an object (= slider div)
    function dragstart(element)
    {
            dragobject = element;
            dragx = posx - dragobject.offsetLeft + new_slider_pos;
    }

    // This function is called to stop dragging an object
    function dragstop()
    {
            dragobject = null;
            dragging = false;
    }

    // This function is called on mouse movement and moves an object (= slider div) on user action
    function drag(e)
    {
            posx = document.all ? window.event.clientX : e.pageX;
            if(dragobject != null)
            {
                    dragging = true;
                    new_posx = (posx - dragx) + conf_slider_width;

                    // Make sure, that the slider is moved in proper relation to previous movements by the glideTo function
                    if(new_posx < ( - new_slider_pos)) new_posx = - new_slider_pos;
                    if(new_posx > (scrollbar_width - new_slider_pos)) new_posx = scrollbar_width - new_slider_pos;

                    var slider_pos = (new_posx + new_slider_pos);
                    var step_width = slider_pos / ((scrollbar_width) / (max-1));
                    var image_number = Math.round(step_width);
                    var new_target = (image_number) * -xstep;
                    var new_caption_id = image_number;

                    dragobject.style.left = new_posx + "px";
                    glideTo(new_target, new_caption_id);
            }
    }

    // Initialize mouse event listener
    function initMouseDrag()
    {
            document.onmousemove = drag;
            document.onmouseup = dragstop;
    }

    function getKeyCode(event)
    {
            event = event || window.event;
            return event.keyCode;
    }

    document.onkeydown = function(event)
    {
            var charCode  = getKeyCode(event);
            switch (charCode)
            {
                    // Right arrow key
                    case 39:
                            handle(-1);
                            break;

                    // Left arrow key
                    case 37:
                            handle(1);
                            break;
            }
    };

    /**
     *  creates an imageflow display for a music list, the musiclist
     *  is then hidden (so it can be displayed again)
     *
     *  show content and initialize mouse event listening after loading
     *
     */

    function imageflow_init()
    {
            if(document.getElementById(conf_imageflow))
            {
                    refresh(true);
                    show(conf_images);
                    show(conf_scrollbar);
                    initMouseWheel();
                    initMouseDrag();
            }
    }

    /**
     *  sets up image flow for a music list
     *
     *  @param musiclist
     *
     */

    function if_createImageFlow( musiclist ) {

        // destroy any other image flows
        $( '#imageflow' ).remove();

        var imageflow = $( '<div></div>' ).attr( 'id', 'imageflow' );
        var images = $( '<div></div>' ).attr( 'id', 'images' );

        $( 'ul li.musicitem' ).each( function(i,item){

            var anchors = $( 'a', item );
            var itemData = $( item ).attr( 'id' );
            $( '<img />' )
                .addClass( 'cover' )
                .attr({
                    src: '/file/cover/' + itemData,
                    alt: $(anchors[anchors.length-1]).html()
                })
                .appendTo( images );

        });

        images.appendTo( imageflow );
        $( '<div id="scrollbar"><div id="slider"></div></div><div id="captions"></div>' )
            .appendTo( imageflow );
        $( musiclist )
            .css({
                margin: '0px',
                padding: '0px'
            })
            .append( imageflow );

        imageflow_init();

    }

    /**
     *  looks for any musiclists on the page and adds imageflow
     *  links to them
     *
     */

    function if_addLinks() {

        $( 'ul.imageflow-list' ).each(function(i,musiclist) {

            if ( $('li',musiclist).length > 1 ) {

                var wrap = $( '<div></div>' );
                var link = $( '<a></a>' )
                    .html( 'ImageFlow' )
                    .addClass( 'toggler' )
                    .attr( 'href', 'javascript:;' )
                    .click(function(){
                        var imageflow = $( '#imageflow' );
                        if ( imageflow.length > 0 ) {
                            $.each(imageflow,function(){
                                $(this).remove();
                            });
                        }
                        else if_createImageFlow( wrap );
                        $( this )
                            .blur()
                            .toggleClass( 'togglerHide' );
                    });

               $( '<div></div>' )
                    .addClass( 'imageflow_toggler' )
                    .append( link )
                    .append( wrap )
                    .insertBefore( musiclist );

            }

        });

    }

    if_addLinks();

};
