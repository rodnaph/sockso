
{include file="header.tpl" title="MySQL"}

<h1>Using MySQL as a backend</h1>

<p>By default Sockso uses the Java database engine HSQLDB.  This provides a nice
fast easy way to get a database up and running in an application and works
excellently for most uses of Sockso.</p>

<p>If you have a very large collection though you may run into some performance
problems and possibly "out of memory" errors when accessing some of the pages.
In these cases you have the option of changing the database engine Sockso uses,
and for this MySQL support is available.</p>

<p><b>NB:</b> Sockso requires MySQL 5+</p>

<h2>1) Download Sockso's Optional Components</h2>

<p>You will need to <a href="index.php?controller=manual&page=optionals">install the optional components</a>
first to use MySQL.</p>

<h2>2) Create the database</h2>

<p>The first thing you will need to do is create a MySQL database for Sockso
to use.  It doesn't need to have any tables in it, Sockso will take care of
creating these when it starts up, but the database does need to exist.</p>

<h2>3) Start Sockso with MySQL</h2>

<p>When you have created the database just start Sockso with the following
command line switches (changing the values to match your set up obviously)
which tell it to use MySQL, and the connection information for the database.</p>

<pre>
$> java -jar sockso.jar --dbtype=mysql \
      --dbhost=localhost \
      --dbuser=myuser \
      --dbpass=secret \
      --dbname=socksodb
</pre>

<p>Sockso will now be running with MySQL.</p>

<p><b>NB:</b> This will be a completely blank database.</p>

{include file="footer.tpl"}
