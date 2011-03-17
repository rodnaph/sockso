
<h1>Latest News &amp; Releases <a href="http://code.google.com/feeds/p/sockso/downloads/basic"><img src="{$filesUrl}images/rss_icon.gif" alt="RSS Icon" /></a></h1>

{foreach from=$aoNews item="news"}

    <div class="post">
        <h2>{$news->date_created|date_format:"%B %e, %Y"}</h2>
        <p>{$news->body}</p>
    </div>

{/foreach}

<ul>
    <li><a href="index.php?controller=news&page=2">older news...</a></li>
</ul>
