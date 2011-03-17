
{include file="header.tpl" title="SQLite Backend"}

<h1>Using SQLite as a backend</h1>

<p><b>NB:</b> Sqlite support in Sockso is currently in development so your mileage
may vary.  There are known issues with database locking problems, amongst other
things.  You have been warned...</p>

<p>You can optionally use SQLite as a backend to Sockso (this requires the
    <a href="index.php?controller=manual&page=optionals">optionals package</a>).  To use it just
pass in <i>sqlite</i> as the database type command line argument, like...</p>

<pre>
$> java -jar sockso.jar --dbtype=sqlite
</pre>

<p>You can also specify where the database is stored by using the <b>--datadir</b>
argument.</p>

{include file="footer.tpl"}
