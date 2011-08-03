var jplayer = null;

/**
 * A JPlayer playlist item which represents a 
 * song in the playlist
 * @param {number} trackId      track id
 * @param {string} trackName    track name
 * @param {number} artistId     artist id
 * @param {string} artistName   artist name
 * @param {number} albumId      album id
 * @param {string} albumName    album name
 * 
 * @type void
 */
sockso.JPlayerPlaylistItem = function(trackId, trackName, artistId, artistName, albumId, albumName) {
  this.trackId = trackId;
  this.trackName = trackName;
  this.artistId = artistId;
  this.artistName = artistName;
  this.albumId = albumId;
  this.albumName = albumName;
}

/*
 * A Html5 audio and video player for jquery.
 * 
 * Developed by Happyworm, jPlayer is Free, Open Source 
 * and dual licensed under the MIT and GPL licenses.
 * 
 * This class is used as a wrapper for the real jPlayer
 * class to fit into the sockso framework.
 * 
 * @see http://www.happyworm.com
 * 
 * @param {string} cssPrefix 
 * @param {string} skin
 * @param {boolean} random Enable random playback
 * @type void
 */ 
sockso.JPlayer = function(cssPrefix, skin, random) {
  
  // Keycodes for keyboard shortcuts and the appropiate action
  this.KEYBOARD_SHORTCUTS = {87: "volumeUp", 38: "volumeUp",
                             83: "volumeDown", 40: "volumeDown",
                             65: "playlistPrev", 37: "playlistPrev",
                             68: "playlistNext", 39: "playlistNext",
                             32: "togglePause", 80: "togglePause"};
  
  var self = this;
  
  this.paused = true;
  this.volume = 0.8;
  
  this.playlist = [];
  this.cssPrefix = cssPrefix;
  this.random = random;

  // Common jplayer options
  this.options = {
    ready: function() {
			jplayer.displayPlaylist();
			jplayer.playlistInit(true); // Parameter is a boolean for autoplay.
		},
		ended: function() {
			jplayer.playlistNext();
		},
		play: function() {
			$(this).jPlayer("pauseOthers");
		},
    supplied: "mp3",
    swfPath: "/file/flash/",
    volume: this.volume
  };

  
  // Current player item
  this.current = 0;
  if(this.random) {
    this.current = ( 0 + parseInt( Math.random() * ( this.playlist.length-1 ) ) );
  }

  // CSS selectors for important dom objects
  this.cssSelector = {
    jPlayer: "#"+cssPrefix+"_jplayer",
    interface: "#"+cssPrefix+"_interface",
    playlist: "#"+cssPrefix+"_playlist",
    trackinfo: "#"+cssPrefix+"_trackinfo"
  };

  this.options.cssSelectorAncestor = this.cssSelector.interface;
  
  // Check if random playing is enabled
  if(this.random == true) {
    $(this.cssSelector.interface + " .jp-random").addClass("jp-random-enabled");
  }
  else {
    $(this.cssSelector.interface + " .jp-random").removeClass("jp-random-enabled");
  }
  
  $(this.cssSelector.jPlayer).jPlayer(this.options);

  // Bind custom button actions
  $(this.cssSelector.interface + " .jp-previous").click(function() {
    self.playlistPrev();
    $(this).blur();
    return false;
  });

  $(this.cssSelector.interface + " .jp-next").click(function() {
    self.playlistNext();
    $(this).blur();
    return false;
  });
  
  $(this.cssSelector.interface + " .jp-random").click(function() {
    self.toggleRandom();
    $(this).blur();
    return false;
  });
  
  // Since jPlayer does not support status querying
  // we have to cache these values ourselves.
  $(this.cssSelector.jPlayer).bind($.jPlayer.event.volumechange, function(event) { 
    this.volume = event.jPlayer.status.volume;
  }.bind(this));
  
  $(this.cssSelector.jPlayer).bind($.jPlayer.event.pause, function(event) { 
    this.paused = true;
  }.bind(this));
  
  $(this.cssSelector.jPlayer).bind($.jPlayer.event.play, function(event) { 
    this.paused = false;
  }.bind(this));
  
  // Add a keyboard shortcut handler
  // We use keydown because keypress wouldn't work with 
  // the arrow keys
  $( document ).keydown(this.keydownHandler.bind(this));
};

$.extend( sockso.JPlayer.prototype, {
  
  /** 
   * Adds a track to the playlist
   * 
   * @param {sockso.PlaylistItem} track 
   * @type void
   */
  addTrack: function(track) {
    this.playlist.push(track);
  },
  
  /**
   * Displays the playlist
   * 
   * @type void
   */
  displayPlaylist: function() {
			var self = this;
      
      // Empty the playlist ul
			$(this.cssSelector.playlist + " ul").empty();
      
			for (i=0; i < this.playlist.length; i++) {
				var listItem = (i === this.playlist.length-1) ? "<li class='jp-playlist-last'>" : "<li>";
				listItem += "<a href='#' id='"+this.cssPrefix+"_playlist_item_" + i +"' tabindex='1'>"+ this.playlist[i].trackName +" ("+this.playlist[i].artistName+")</a>";


				listItem += "</li>";

				// Associate playlist items with their media
				$(this.cssSelector.playlist + " ul").append(listItem);
				$(this.cssSelector.playlist + "_item_" + i).data("index", i).click(function() {
					var index = $(this).data("index");
					if(self.current !== index) {
						self.playlistChange(index);
					} else {
						$(self.cssSelector.jPlayer).jPlayer("play");
					}
					$(this).blur();
					return false;
				});
			}
		},
    
    /**
     * Initialize the playlist
     * 
     * @param {boolean} autoplay  Start playing automatically?
     * @type void
     */
		playlistInit: function(autoplay) {
			if(autoplay) {
				this.playlistChange(this.current);
			} else {
				this.playlistConfig(this.current);
			}
		},
    
    /**
     * Update the information for the actually
     * played song
     * 
     * @param {number} index  Index of the song which is being played
     * @type void
     */
		playlistConfig: function(index) {
			$(this.cssSelector.playlist + "_item_" + this.current).removeClass("jp-playlist-current").parent().removeClass("jp-playlist-current");
			$(this.cssSelector.playlist + "_item_" + index).addClass("jp-playlist-current").parent().addClass("jp-playlist-current");
			this.current = index;
			$(this.cssSelector.jPlayer).jPlayer("setMedia", {mp3:Properties.getUrl('/stream/' + this.playlist[this.current].trackId)});
      $(this.cssSelector.trackinfo)
      
      $(this.cssSelector.trackinfo + " .albumCover")
            .empty()
            .append(
                $( '<img></img>' )
                    .attr({
                        src: Properties.getUrl('/file/cover/al' +this.playlist[this.current].albumId)
                    })
            );
      
      $(this.cssSelector.trackinfo + " .artistName").html(this.playlist[this.current].artistName);
      $(this.cssSelector.trackinfo + " .albumName").html(this.playlist[this.current].albumName);
      $(this.cssSelector.trackinfo + " .trackName").html(this.playlist[this.current].trackName);
		},
    
    /**
     * Change the actually played song
     * 
     * @param {number} index  Index of the song to play
     * @type void
     */
		playlistChange: function(index) {
			this.playlistConfig(index);
			$(this.cssSelector.jPlayer).jPlayer("play");
		},
    
    /**
     * Play the next track
     * 
     * @type void
     */
		playlistNext: function() {
      var index;
      
      if(this.random) {
        index = ( 0 + parseInt( Math.random() * ( this.playlist.length-1 ) ) );
      }
      else {
        index = (this.current + 1 < this.playlist.length) ? this.current + 1 : 0;
      }
      
			this.playlistChange(index);
		},
    
    /**
     * Play the previous track
     * 
     * @type void
     */
		playlistPrev: function() {
			var index = (this.current - 1 >= 0) ? this.current - 1 : this.playlist.length - 1;
			this.playlistChange(index);
		},
    
    /**
     * Start playing
     * 
     * @type void
     */
    play: function() {
      $(this.cssSelector.jPlayer).jPlayer("play");
    },
    
    /**
     * Toggles the random play ("shuffle") mode
     * 
     * @type void
     */
    toggleRandom: function(){
      if(this.random == true) {
        this.random = false;
        $(this.cssSelector.interface + " .jp-random").removeClass("jp-random-enabled");
      }
      else {
        this.random = true;
        $(this.cssSelector.interface + " .jp-random").addClass("jp-random-enabled");
      }
    },
    
    /**
     *  Handle keypress events for shortcuts
     *  Hotkeys are defined in KEYBOARD_SHORTCUTS
     *    
     *  @param event
     *  @type void
     */
    keydownHandler: function (event) {
      
      event.preventDefault();
      event.stopPropagation();
      
      var keyCode = event.keyCode || event.which;
      
      // Call the action if it exists
      if(this.KEYBOARD_SHORTCUTS[keyCode] != undefined) {
        this[this.KEYBOARD_SHORTCUTS[keyCode]]();
      }
    },
    
    /**
     * Toggles pause on/off
     * 
     * @type void
     */
    togglePause: function() {
      if(this.paused) {
        this.play();
      }
      else {
        $(this.cssSelector.jPlayer).jPlayer("pause");
      }
    },
    
    /**
     * Increases the volume by 10%
     * 
     * @type void
     */
    volumeUp: function() {
      $(this.cssSelector.jPlayer).jPlayer("volume", this.volume + 0.1)
    },
    
    /**
     * Decreases the volume by 10%
     * 
     * @type void
     */
    volumeDown: function() {
      $(this.cssSelector.jPlayer).jPlayer("volume", this.volume - 0.1)
    }
    
});


