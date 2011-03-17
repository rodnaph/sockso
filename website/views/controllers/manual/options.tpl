
{include file="header.tpl" title="Sockso Options"}

<h1>Options</h1>

<p>To get at the options, just click the <b>General</b> tab at the top
of the main window.</p>

<p><a href="{$filesUrl}/images/manual/options-1.png"><img
    src="{$filesUrl}/images/manual/options-1-small.png" /></a></p>

<h2>Web Server</h2>

<p>The fields you'll most likely want to change here are the <b>Title</b>
and <b>Tagline</b> fields.  These are the main headings you see on your
Sockso website, change them to something about you.  Unless you know what
you're doing it's best not to touch the <b>Port</b> field.</p>

<h2>Uploads</h2>

<p>This allows you to configue if you want to allow users to upload tracks to
your Sockso server.</p>

<h2>Collection</h2>

<p>These options effect how Sockso deals with your music.  You can change whether
or not Sockso scans your collection when it first starts up, and how often (in
minutes) you want it to check for music that's been added/removed.</p>

<h2>General</h2>

<p>These options don't fit into the above categories.  The first option specifies
whether or not Sockso will start minimised (meaning it'll go straight to your system tray
without you seeing the main window).  The second controls whether or not sockso
will ask you the customary "Are you sure" question when you try to exit.</p>

<h2>Logging</h2>

<p>Sockso allows you to log all request activity that happens on your server.  You
can then dump the logs out in a number of popular formats.</p>

<h2>NAT Setup</h2>

<p>This last set of options allows you to test whether your network setup is correct
and Sockso is available via the internet.  This is an experimental feature so may
not work correctly for you.</p>

{include file="footer.tpl"}
