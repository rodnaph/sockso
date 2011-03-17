
{include file="header.tpl" title="Customizing Sockso!"}

<h1>Customizing Sockso!</h1>

<p>If you'd like to change how Sockso looks for your users, this page explains
what you need to know.  It's pretty simple to get started, so read this short
guide and you should be ready to get going!</p>

<h2>1 - Resources</h2>

<p>All the resources (images, css, etc...) that Sockso uses can be found in the
<b>resources</b> folder in the main directory.  Inside that folder you'll see
a structure like this:</p>

<pre>
sockso
   resources
      htdocs
      icons
      images
      locales

</pre>

<p><i>(If you're on OSX and want to customize Sockso it's easiest to download
the linux version)</i></p>

<h2>2 - Understanding the folders</h2>

<p>The first folder <b>htdocs</b> is probably going to be the most interesting
one when customizing Sockso.  This folder contains all the CSS, Javascript and
images used in the web interface.  Just get in there and start playing around!</p>

<p>The <b>icons</b> and <b>images</b> folders contain the icons and images that
are used by the GUI.  The <b>locales</b> folder contains translation text used
by Sockso for displaying different languages.  The files are named sockso.LANG_CODE.txt,
where LANG_CODE is the correct language code.</p>

<p>With that information you should be able to play around with the images and
the CSS and make your Sockso look however you like!</p>

<h2>3 - Skins</h2>

<p>Sockso uses<a href="index.php?controller=manual&page=skins">skins</a> to display different designs
for the web interface, and which one is used can
be set with the property <b>www.skin</b>.  These are stored in the <b>skins</b>
folder inside <b>htdocs</b>.  By default Sockso uses the "original"
skin, so you'll see a bunch of files and folders in there for it.</p>

<p>If you want to add your own skin, just create a new folder for it inside
the <b>htdocs/skins</b> folder, then create the file <b>css/default.css</b>.  You're now
set to change the styling all you need, and switch between this skin and others.</p>

<h2>4 - Sharing your design</h2>

<p>So now you have your new design, you may want to be able to share it with
other Sockso users.  If you've created a new skin for the web interface then sharing
will be as easy as sending that folder to someone!  They can then copy it into
their <b>htdocs/skins</b> folder and switch their<a href="index.php?controller=manual&page=skins">skin</a>
using the <b>www.skin</b> property.</p>

<p>You can post your custom resources files online, maybe on the Sockso 
<a href="http://forums.pu-gh.com/viewforum.php?f=4">custom resources forum</a>!</p>

<h2>4 - Hacking Sockso</h2>

<p>If you'd like to go one step further and really get into changing Sockso you're
going to need to start hacking the code.  Get over to the <a href="index.php?controller=source">source code</a>
page and grab yourself a copy!</p>

{include file="footer.tpl"}
