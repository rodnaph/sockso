
{include file="header.tpl"}

{php}
$smarty->assign(
    'latestVersion',
    file_get_contents(sprintf(
        '%s/views/controllers/version/latest.tpl',
        $smarty->getTemplateVars( 'siteDir' )
    ))
);
{/php}

<div id="welcome">
    <h1>
    Download for
        <a href="http://sockso.googlecode.com/files/sockso-{$latestVersion}.zip" title="Download for Windows">Windows <img src="{$filesUrl}images/icon-windows.png" alt="Windows Logo" /></a>,
        <a href="http://sockso.googlecode.com/files/sockso-{$latestVersion}.dmg" title="Download for Mac">Mac <img src="{$filesUrl}images/icon-osx.png" alt="OSX Logo" /></a> or
        <a href="http://sockso.googlecode.com/files/sockso-{$latestVersion}.zip" title="Download for Linux">Linux <img src="{$filesUrl}images/icon-linux.png" alt="Linux Logo" /></a>
    </h1>
    <p><strong>Sockso</strong> is a <a href="http://www.gnu.org/licenses/gpl.txt">free</a>,
        <a href="index.php?controller=source">open-source</a>, personal music server for everyone!  It's
    designed to be as simple as possible so that anyone with a mouse and
    some mp3's can get their friends listening to their music across the internet
    in <strong>seconds</strong>!</p>

    <p>
        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="580" height="378">
            <param name="movie" value="files/sockso/swf/bootstrap.swf"></param>
            <param name="quality" value="high"></param>
            <param name="bgcolor" value="#FFFFFF"></param>
            <param name="flashVars" value="thumb=files/sockso/images/using-sockso.png&amp;content=files/sockso/swf/using-sockso.swf&amp;width=580&amp;height=378"></param>
            <param name="allowFullScreen" value="true"></param>
            <param name="scale" value="showall"></param>
            <param name="allowScriptAccess" value="always"></param>
            <embed src="files/sockso/swf/bootstrap.swf" quality="high" bgcolor="#FFFFFF" width="580" height="378" type="application/x-shockwave-flash" allowScriptAccess="always" flashVars="thumb=files/sockso/images/using-sockso.png&amp;content=files/sockso/swf/using-sockso.swf&amp;width=580&amp;height=378" allowFullScreen="true" scale="showall"></embed>
        </object>
    </p>

    <h3>Sockso features</h3>
    <ul>
        <li><a href="index.php?controller=manual&page=installing">Simple setup</a> (no install! just double click and go!)</li>
        <li>Supports MP3, OGG Vorbis, Flac and WMA</li>
        <li>Easy <a href="index.php?controller=manual&page=www">web-interface</a> for your friends,
            and a <a href="index.php?controller=manual&page=gui">GUI</a> for you!</li>
        <li>Online flash music player, playlists, search, etc...</li>
        <li>Download single tracks, or entire albums/artists or playlists</li>
        <li>Statistics like most played, recently popular, etc...</li>
    </ul>
    <p>See a <a href="index.php?controller=manual&page=features">complete feature list</a></p>
    <p><a href="{$filesUrl}/images/album.png"><img src="{$filesUrl}/images/album-small.png" alt="Viewing an album" /></a></p>
    <p><img src="{$filesUrl}/images/gui.png" alt="GUI Interface Screenshot" /></p>
    <p><a href="{$filesUrl}/images/homepage.png"><img src="{$filesUrl}/images/homepage-small.png" alt="The homepage" /></a></p>

</div>

{include file="footer.tpl"}
