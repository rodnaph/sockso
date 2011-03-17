
{include file="header.tpl" title="Running Sockso"}

<h1>Running Sockso on a Server</h1>

<p>If you want to install Sockso on a server without a GUI then you can
start it up with the <b>--nogui</b> option, and you'll be presented
with a console where you can manage everything.</p>

<p><b>Windows</b><br />
$>windows.bat --nogui<br />
<br />
<b>Linux (and others)</b><br />
$>sh linux.sh --nogui
</p>

<p>To find out the available commands type <b>help</b> at the console.</p>

<p>For a list of other command line switches see the
<a href="index.php?controller=manual&page=cmdline">command line options</a> page.</p>

<h2>Sockso Properties</h2>

<p>You can change Sockso's behaviour through the console by changing it's
<a href="index.php?controller=manual&page=properties">properties</a>.</p>

<h2>Administration Mode</h2>

<p>If you have Sockso running in the background, maybe started via an init.d
script it would be a pain to have to stop and start it so you can see the
GUI to do some adminitration.  So, if you're using <a href="index.php?controller=manual&page=mysql">MySQL</a>
as a backend, you have the option of starting Sockso in <i>admin mode</i>.<p>

<pre>
$> java -jar sockso.jar --admin --dbtype=mysql etc...
</pre>

<p>This will start up a version of Sockso without a web server so you can make
changes to the process running in the background.</p>

{include file="footer.tpl"}
