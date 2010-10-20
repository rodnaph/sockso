
{include file="include/header.tpl" title="Skins"}

<h1>Skins</h1>

<p>Sockso uses skins to present different looks for the web interface.  You can
switch between the different skins provided, or
{link url={ action="customize" } text="create your own" }!</p>

<p>The current skin is controlled by the property <b>www.skin</b>.  You can easily
set it from the console by using this command...</p>

<pre>
propset www.skin original
</pre>

<p>Sockso currently ships with the following skins:</p>

<ol>
<li><b>original</b> - The original Sockso skin</li>
<li><b>bold</b> - A high color, bold skin</li>
</ol>

<p>If you do create your own skins please {link url={ controller="contact" } text="send them in" } and they'll get included
with the next release!</p>

{include file="include/footer.tpl"}
