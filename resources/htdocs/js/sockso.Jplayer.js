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
 * @param {string}Êskin
 * @param {boolean} random      Enable random playback
 * @type void
 */ 
sockso.JPlayer = function(cssPrefix, skin, random) {
  var self = this;
  
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
    swfPath: "/file/flash/"
  };

  
  // Currently player item
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
  
  if(this.random == true) {
    $(this.cssSelector.interface + " .jp-random").addClass("jp-random-enabled");
  }
  else {
    $(this.cssSelector.interface + " .jp-random").removeClass("jp-random-enabled");
  }
  
  $(this.cssSelector.jPlayer).jPlayer(this.options);

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
		playlistInit: function(autoplay) {
			if(autoplay) {
				this.playlistChange(this.current);
			} else {
				this.playlistConfig(this.current);
			}
		},
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
		playlistChange: function(index) {
			this.playlistConfig(index);
			$(this.cssSelector.jPlayer).jPlayer("play");
		},
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
		playlistPrev: function() {
			var index = (this.current - 1 >= 0) ? this.current - 1 : this.playlist.length - 1;
			this.playlistChange(index);
		},
    play: function() {
      $(this.cssSelector.jPlayer).jPlayer("play");
    },
    toggleRandom: function(){
      if(this.random == true) {
        this.random = false;
        $(this.cssSelector.interface + " .jp-random").removeClass("jp-random-enabled");
      }
      else {
        this.random = true;
        $(this.cssSelector.interface + " .jp-random").addClass("jp-random-enabled");
      }
    }
});


