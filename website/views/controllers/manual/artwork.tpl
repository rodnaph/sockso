
{include file="header.tpl" title="Artwork"}

<h1>Artwork</h1>

<p>On the web interface Sockso always tries to display artwork for albums
and artists.  These covers are usually fetched from Amazon so they just appear
and you don't have to do a thing, but if you'd like to use your own artwork
then you can do that as well.  (The Amazon search can be wrong somtimes of course)

<h2>Using Your Own Artwork</h2>

<p>If you want to give your own artwork just put the image files you want to use
in the same directory as the tracks for that album, or in the <b>parent</b> directory
for artists, and Sockso will pick them up to use.  For artists call the file
"artist" (jpg, png or gif), and for albums call it "album". (You can actually specify
what these files should be called by using <a href="index.php?controller=manual&page=properties">properties</a>)</p>

<h2>Watch Out For Caching!</h2>

<p>Because fetching cover images from the net can be slow, Sockso caches
these images in the $HOME/.sockso/covers directory.  Once an image has been
cached here, it will need to be deleted before it's asked for again.</p>

<p>By default, this caching is only done for artwork fetched from the net.
If you want Sockso to cache your local cover images as well (this will
improve performance if you need it) then set the <a href="index.php?controller=manual&page=properties">property</a>
<b>covers.cacheLocal</b> to <i>yes</i>.</p>

{include file="footer.tpl"}
