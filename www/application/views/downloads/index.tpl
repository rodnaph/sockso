
{include file="include/header.tpl" title="Downloads"}

<div id="welcome">

	<h1>Sockso Downloads</h1>

	<p>Below are listed all the available <b>Sockso</b> downloads up to version
        1.1.3.  After this release downloads were moved to
        <a href="http://code.google.com/p/sockso/downloads">Google Code</a>.  To get the source code
        please go to the {link url={ controller="source" } text="source code"} page.</p>

	<ul>
	{foreach item="file" from=$files}
		<li><a href="{$smutty->baseUrl}/downloads/{$file}">{$file}</a></li>
	{/foreach}
	</ul>
	
</div>

{include file="include/footer.tpl"}
