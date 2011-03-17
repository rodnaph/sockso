
{include file="header.tpl" title="Properties"}

<h1>Properties</h1>

<p>Sockso's behaviour is controlled mainly through the properties stored in
the database.  You have complete access to all these properties from the
Sockso command line.  To set a property you need to use the <b>propset</b>
command.  This takes the name of property and then the value you want to
set it to.  eg.</p>

<pre>
#SoCkSo#> propset www.title my new title
</pre>

<p><b>NOTE:</b> There is no <b>:</b>!</p>

<p>The properties Sockso currently supports are:</p>

<h3>Web Interface</h3>

<p><b>www.title</b>: text (main title on web interface)</p>
<p><b>www.tagline</b>: text (smaller heading)</p>
<p><b>www.disableDownloads</b>: yes/no</p>
<p><b>users.requireLogin</b>: yes/no</p>
<p><b>users.disableRegistration</b>: yes/no</p>
<p><b>www.flashPlayer.dontFilterMp3s</b>: yes/no (don't limit flash player to mp3's)</p>
<p><b>www.imageflow.disable</b>: yes/no (disable imageflow)</p>
<p><b>www.similarArtists.disable</b>: yes/no (disable similar artists)</p>
<p><b>www.covers.disable</b>: yes/no (disable cover images)</p>
<p><b>www.covers.disableRemoteFetching</b>: yes/no (disable fetching covers from internet)</p>
<p><b>www.skin</b>: text (name of the skin)</p>

<h3>Server</h3>

<p><b>server.host</b>: ip address</p>
<p><b>server.host.lastUpdated</b>: number (unix timestamp)</p>
<p><b>server.port</b>: number</p>
<p><b>server.basepath</b>: path</p>

<h3>Streaming</h3>
<p><b>stream.requireLogin</b>: yes/no (require auth for streaming, this requires
users.requireLogin to be set to 'yes' as well)</p>

<h3>Uploads</h3>

<p><b>uploads.enabled</b>: yes/no (enables uploads)</p>
<p><b>uploads.requireLogin</b>: yes/no (need login to upload?)</p>
<p><b>uploads.collectionId</b>: number (collection id to store uploads in)</p>

<h3>Browsing</h3>

<p><b>browse.folders.enabled</b>: yes/no (enable folder browsing?)</p>
<p><b>browse.folders.only</b>: yes/no (use folder browsing only?)</p>
<p><b>browse.popularTracks.count</b>: number (how many popular tracks)</p>
<p><b>browse.topArtists.count</b>: number (how many top artists)</p>
<p><b>browse.recentTracks.count</b>: number (how many recent tracks)</p>
<p><b>browse.latestArtists.count</b>: number (how many latest artists)</p>
<p><b>browse.latestAlbums.count</b>: number (how many latest albums)</p>
<p><b>browse.latestTracks.count</b>: number (how many latest tracks)</p>

<h3>Logging</h3>

<p><b>log.request.enable</b>: yes/no (enables request logging)</p>

<h3>Playback</h3>

<p><b>playlists.random.trackLimit</b>: number (number of tracks in random playlists)</p>

<h3>Application</h3>

<p><b>app.startMinimized</b>: yes/no</p>
<p><b>app.confirmExit</b>: yes/no</p>

<h3>Collection</h3>

<p><b>collman.scan.onStart</b>: yes/no (scan collection on startup?)</p>
<p><b>collman.scan.interval</b>: number (minutes between collection scans)</p>
<p><b>collman.artists.removePrefixes</b>: text (commer seperated list of prefixes to remove)</p>

<p><b>NB:</b> When setting prefixes to remove, if you only specify one you'll
need to put a commer after it if it ends with a space eg. 'The ,'</p>

<h3>Artwork</h3>

<p><b>covers.artist.file</b>: text (file name of artist image)</p>
<p><b>covers.album.file</b>: text</p>
<p><b>covers.cacheLocal</b>: yes/no (cache local cover art?)</p>
<p><b>covers.defaultWidth</b>: number (pixel width to resize images to)</p>
<p><b>covers.defaultHeight</b>: number (pixel height to resize images to)</p>
<p><b>covers.xspf.display</b>: album/artist (in XSPF, show artist or album artwork?)</p>

<h3>Misc</h3>

<p><b>version.checkDisabled</b>: yes/no (disable checking for updates)

{include file="footer.tpl"}
