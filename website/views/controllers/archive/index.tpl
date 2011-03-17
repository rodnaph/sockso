
{include file="header.tpl" title="Older news..."}

<h1>Old News...</h1>

{php}
$smarty->assign( 'downloadExtension',
    preg_match('/Macintosh/',$_SERVER['HTTP_USER_AGENT'])
        ? 'dmg' : 'zip' );
{/php}

<h2>7th February 2010</h2>
<p>Sockso 1.2.2 <b>(BROKEN)</b>
-
Users can now change their passwords via the web interface, added
property to disable checking for the latest version, new manual
scheduler, and bug fixes for quotes in track names and amazon cover
fetching.
</p>

<h2>30th December</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.2.1.{$downloadExtension}">Sockso 1.2.1</a>
-
New <a href="index.php?controller=manual&page=scheduling">cron scheduling support" } for collection
scanning, streaming improvement (by mrave), and added palm pre support.
</p>

<h2>26th September</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.2.{$downloadExtension}">Sockso 1.2</a>
-
New faster track indexing, AAC support (by mrave), CPU usage improvements, updated Dutch translation,
lots of other fixes.
</p>

<h2>2nd May</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.1.8.{$downloadExtension}">Sockso 1.1.8</a>
-
Bug fixes for 'connection closed' issue.
</p>

<h2>19th February</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.1.7.{$downloadExtension}">Sockso 1.1.7</a>
-
Bug fixes for MySQL, reduced CPU usage with <a href="index.php?controller=manual&page=properties">toggleable buffering options</a> when streaming,
and removed remaining hard coded text from web interface.
</p>

<h2>19th January</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.1.6.{$downloadExtension}">Sockso 1.1.6</a>
-
Bug fixes for indexing and properties, a smarter 404 page, updated german translation, and upgraded to jQuery 1.3
</p>

<h2>11th January</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.1.5.{$downloadExtension}">Sockso 1.1.5</a>
-
Bug fixes for MySQL backend, and some javascript issues.
</p>

<h2>4th January 2009</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.1.4.{$downloadExtension}">Sockso 1.1.4</a>
-
Bug fixes for searching and logging in / uploading.  Added unpacked javascript, restyled
album and artist pages to better fit long names (and added a new 'JS Player' - unstable).
</p>

<h2>7th December</h2>
<p><a href="http://sockso.googlecode.com/files/sockso-1.1.3.{$downloadExtension}">Sockso 1.1.3</a>
-
Bug fixes.
</p>

<h2>25th November</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.1.2.{$downloadExtension}">Sockso 1.1.2</a>
-
Fixed file uploading (hopefully all platforms).
</p>

<h2>20th October</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.1.1.{$downloadExtension}">Sockso 1.1.1</a>
-
Added french translation, ability to scan collection on demand, and fixed bugs with --datadir,
indexing Ogg files, a missing image, and the name of the scrobble log.
</p>

<h2>7th October</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.1.{$downloadExtension}">Sockso 1.1</a>
-
<a href="index.php?controller=manual&page=mysql">MySQL</a> and (some) <a href="index.php?controller=manual&page=sqlite">SQLite</a> support added,
Sockso is now <a href="index.php?controller=manual&page=customize">more easily customizable</a>, new WMA to MP3 encoding option.
</p>



<h2>19th September</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.9.{$downloadExtension}">Sockso 1.0.9</a>
-
Added SSL support, streaming can now require authentication, users can get their
<a href="index.php?controller=manual&page=scrobbling">scrobble log</a> for tracks
they've listened to, added a new flash player, fixed some bugs.
</p>


<h2>6th September</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.7.{$downloadExtension}">Sockso 1.0.7</a>
-
Add 'related artists' information to pages, improved unicode support and fixed Windows indexing bug (hidden folders now ignored)
</p>


<h2>30th July</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.6.{$downloadExtension}">Sockso 1.0.6</a>
-
Bug fix release for problems losing colors when resizing local cover art.
</p>


<h2>27th July</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.5.{$downloadExtension}">Sockso 1.0.5</a>
-
Added resizing of user artwork, random playing of artists/albums, ability to import playlists,
fixed imageflow squashing bug, dutch and german translations, and informs user if new
versions are available.</p>


<h2>6th July</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.4.{$downloadExtension}">Sockso 1.0.4</a>
-
Fixed system tray icon support on linux.</p>



<h2>5th July</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.3.{$downloadExtension}">Sockso 1.0.3</a>
-
More improvements to the console, and users can now store cover art on the filesystem
with their music.</p>





<h2>26th June</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.2.{$downloadExtension}">Sockso 1.0.2</a>
-
Console - you can enable uploads, and added a new command to delete properties.  Users
can now delete their own playlists from web interface, and the site owner can too.</p>




<h2>24th June</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.1.{$downloadExtension}">Sockso 1.0.1</a>
-
Bug fix release for IE not saving selected playlist type.</p>



<h2>20th June</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-1.0.{$downloadExtension}">Sockso 1.0</a>
-
After over a years development, here is Sockso 1.0!  No big new features since the
last release, just some bug fixing.  Enjoy!</p>


<h2>14th May</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.9.2.{$downloadExtension}">Sockso 0.9.2</a>
-
Important bug fixes to the collection manager, greatly speeded up Flac file indexing,
improved the accuracy of album artwork fetching, and other bug fixes (tray icon
on linux is still broken though...).</p>



<h2>11th May</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.9.1.{$downloadExtension}">Sockso 0.9</a>
-
Flac files are now supported, and the codebase has been cleaned up.  This should
be the final release before 1.0 next month.</p>



<h2>14th April</h2>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.11.{$downloadExtension}">Sockso 0.8.11</a>
-
Changes to audio tags now picked up,
new <a href="index.php?controller=manual&page=encoders">ogg-mp3 encoding</a> option, custom encoding ability enabled,
users can <a href="index.php?controller=manual&page=cmdline">specify data directory</a> and change amount of
<a href="index.php?controller=manual&page=runningOnServer">info on www</a> (eg. popular tracks)</p>




<h3>24th March</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.10.{$downloadExtension}">Sockso 0.8.10</a>
-
Added ability to play folders in folder browsing mode</p>



<h3>1st March</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.9.{$downloadExtension}">Sockso 0.8.9</a>
-
Bug fix release for folder browsing on Windows</p>



<h3>25th February</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.8.{$downloadExtension}">Sockso 0.8.8</a>
-
<a href="index.php?controller=manual&page=imageflow">Imageflow</a> album art,
<a href="index.php?controller=manual&page=folderBrowsing">folder browsing</a>,
GUI console, reworked random playlists</p>



<h3>19th January</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.7.{$downloadExtension}">Sockso 0.8.7</a>
Released (Bug fix release)</p>

<h3>15th January 2008</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.6.{$downloadExtension}">Sockso 0.8.6</a>
Released (<b>Highlights:</b> Now <a href="index.php?controller=manual&page=customize">customizable</a>,
Random playlists, bug fixes, new - in progress - encoders panel)</p>

<h3>5th December 2007</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.5.{$downloadExtension}">Sockso 0.8.5</a>
Released (<b>Highlights:</b> Uploads, OSX app version, Norwegian translation, lots of bug fixes)</p>

<h3>23st October</h3>
<p>Sockso <a href="http://forums.pu-gh.com/index.php?c=2">Forums</a> opened!</p>

<h3>20st October</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.4.zip">Sockso 0.8.2</a>
Released (<b>Highlights:</b> disable downloading, embedded flash player, user specific languages)</p>

<h3>5th September</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.8.1.zip">Sockso 0.8</a>
Released (<b>Highlights:</b> User login/registration options, NAT tester, many more tweaks)</p>

<h3>29th July</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.7.2.zip">Sockso 0.7.2</a>
Released (<b>Highlights:</b> Console added so you can run Sockso on servers without a GUI)</p>

<h3>24th July</h3>
<p>Sockso gets a <a href="http://www.softpedia.com/reviews/mac/Sockso-Review-60304.shtml">5 Star review</a>
over at softpedia.com.

<h3>18th July</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.7.1.zip">Sockso 0.7.1</a>
Released (<b>Highlights:</b> Fixed major problem starting on Sockso on some platforms)</p>

<h3>13th July</h3>
<p>Created a <a href="index.php?controller=source">Github repository</a> online
so people can get the source any time.</p>

<h3>6th July</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.7.zip">Sockso 0.7</a>
Released (<b>Highlights:</b> Album art, and
<a href="index.php?controller=manual&page=embedding">embedding music</a> in your own website!)</p>

<h3>28th June</h3>
<p><a href="index.php?controller=manual">Sockso manual</a>
added to the site, and <a href="http://www.simplehelp.net/2007/06/27/how-to-use-sockso-as-your-own-personal-streaming-music-server/">a walkthrough</a>
posted on <a href="http://www.simplehelp.net">simplehelp.net</a>.
<b>Sorry for the website problems</b>, I'm having problems with
my hosting company.  Trying to get them to sort it quickly!</p>

<h3>22nd June</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.6.zip">Sockso 0.6</a>
Released (<b>Highlights:</b> Online flash player, XSPF playlists, tweaked UI)</p>

<h3>17th June</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.5.zip">Sockso 0.5</a>
Released (<b>Highlights:</b> Great new look, rss feeds and search)</p>

<h3>16th June</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.4.zip">Sockso 0.4</a>
Released (<b>Highlights:</b> Web playlist, download tracks, albums and artists)</p>

<h3>13th June</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.3.zip">Sockso 0.3</a>
Released (<b>Highlights:</b> Systray support, auto collection updating)</p>

<h3>10th June</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.2.zip">Sockso 0.2</a>
Released (<b>Highlight:</b> OGG and WMA support!)</p>

<h3>6th June</h3>
<p><a href="{$smutty->baseUrl}/downloads/sockso-0.1.zip">Sockso 0.1</a>
Released! (In beta now!)</p>

<h3>4th June</h3>
<p>Sockso website launched, and first (alpha) release made.</p>

{include file="footer.tpl"}
