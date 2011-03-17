
{include file="header.tpl" title="Community"}

<div class="community">

    <h1>Community</h1>

    <p>Listed below are currently active community servers:</p>

    <table>
        <thead>
            <tr>
                <th>Name</th>
                <th class="version">Version</th>
            </tr>
        </thead>
        <tbody>
            {foreach $servers as $server}
            <tr>
                <td class="name">
                    <a href="http://{$server->ip}:{$server->port}{$server->basepath}">
                        {$server->title}
                        -
                        {$server->tagline}
                    </a>
                </td>
                <td class="version">
                    {$server->version}
                </td>
            </tr>
            {foreachelse}
            <tr>
                <td colspan="2" class="empty">
                    Sorry, no servers currently listed...
                </td>
            </tr>
            {/foreach}
        </tbody>
    </table>

    <p><i>(Listing servers active in the last day)</i></p>

    <h2>Listing Your Server</h2>

    <p>If you'd like to list your server as part of the community just check the box
    on the <b>General</b> tab of your Sockso GUI.  More information is available
    on the <a href="index.php?controller=manual&page=community">manual page</a>.</p>

</div>

{include file="footer.tpl"}
