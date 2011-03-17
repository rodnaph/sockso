
{include file="header.tpl" title="Community"}

<h1>Sockso Server Community</h1>

<p>As of version 1.3 Sockso provides a way to make your Sockso server a part
of the larger community of Sockso servers people are running.  This is an opt-in
feature and is <b>not</b> enabled by default.</p>

<h2>The Goal</h2>

<p>The purpose of this new functionality is to try and unite the disparate Sockso
    servers that people have running, and allow people to browse and listen to
    new music as easily as possible.</p>

<p>Sockso started as just a server for personal use, for just one person, but hopefully
    if the community takes off people will have a more connected experience.</p>

<h2>What it Does</h2>

<p>The main visible change is the new <a href="index.php?controller=community">Community</a>
    page on the main Sockso website.  This page lists Sockso servers that have
    chosen to be a part of the community.  This page is live, so will only list
    currently running servers should they go up or down.</p>

<p><img src="{$filesUrl}/images/community-www.png" /></p>

<h2>How to Get Involved</h2>

<p>If you'd like your server to become a part of the community just go to
    the <i>General</i> tab in your Sockso GUI and click the checkbox at the
    top of the page.  As long as your server is publicly accessible you should
    then shortly start showing up on the website for people to discover.</p>

<p><img src="{$filesUrl}/images/community-gui.png" /></p>

<p>If you're running without a GUI then this can also be set from the console.</p>

<pre>
$> propset community.enabled yes
</pre>

<h2>Troubleshooting</h2>

<p>If you've tried to opt-in to the community and your server isn't getting listed
    on the website then check that your setup is correct, and that the server is
    available from the internet (if it's not is won't get listed).  If it is and
    you're still having problems try posting in the forum for help.</p>

<h2>Beware of...</h2>

<p>As with Sockso use in general, the music you make available is up to you, so
    check you have the right permission to do so before you put it on the internet.</p>

{include file="footer.tpl"}
