
{include file="header.tpl" title="Playing Music"}

<h1>Playing Music</h1>

<p>To play music with Sockso, you just have to click the little
green play icons next to the music you want to listen to.  What happens
after you click play depends on the play type you have selected.  In the
top right hand corner of the screen you'll see the play options which
look like this...</p>

<p><img src="{$filesUrl}images/manual/playing-1.png" /></p>

<p>The default is <b>Flash Player</b>, which means that tracks will be
played by a popup flash player right there on the website.  You don't
need to have another music player like iTunes or Windows Media Player
to use this.</p>

<p><b>NB:</b> The one problem with the flash player is that it can only
play MP3's, so it will filter to only these tracks when playing playlists.
<b>But</b> you can use Sockso's <a href="index.php?controller=manual&page=encoders">encoders</a>
options to re-encode music to mp3 on-the-fly!  When you have this set up you
can disable this filtering by setting the <b>www.flashPlayer.dontFilterMp3s</b>
property to yes.</p>

<p>If you do want to use your own media player though (iTunes, WMP, Winamp, etc...)
then you can select either the <b>M3U</b>, <b>Pls</b>, or <b>XSPF</b> options.  These
will send the playlist to your computer so you can use your preferred
music application to listen.</p>

{include file="footer.tpl"}