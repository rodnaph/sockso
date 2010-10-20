
{include file="include/header.tpl" title="Optional Components"}

<h1>Sockso's Optional Components</h1>

<p>Sockso provides support for some of it's features in an optional extra download.
This is meant to ensure that the default Sockso download remains small, but this
won't restrict adding more ambitious features that require extra libraries, etc...</p>

<h1>Download <a href="{$smutty->baseUrl}/downloads/sockso-optionals.zip">sockso-optionals.zip</a></h1>

<p>The optionals download allows features like...</p>

<ol>
<li>{link url={ action="mysql" } text="MySQL Support"}</li>
<li>{link url={ action="sqlite" } text="SQLite Support"} (experimental)</li>
<li>UPNP Router Configuration</li>
</ol>

<h2>Installing</h2>

<p>When you have downloaded the optionals zip file, you'll just need to unpack it
and copy the files to a new folder inside your Sockso folder called <b>lib-opt</b>.
(You'll see there's already a folder there called <b>lib</b> for the main stuff)</p>

<p>That's it, you should be ready to try out the new features now.</p>

{include file="include/footer.tpl"}
