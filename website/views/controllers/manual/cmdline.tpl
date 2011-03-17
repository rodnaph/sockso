
{include file="header.tpl" title="Command Line Options"}

<h1>Command Line Options</h1>

<p>Sockso has some extra command line options you can use to change the
way it works.  You pass these commands to Sockso by running it from the
command line and with these options after the startup file (<i>windows.bat</i>
or <i>linux.sh</i>)</p>

<p><b>--nogui</b><br />
Disables the GUI, this can be used to run Sockso on a server.  You will
be presented with a console to manage Sockso.  Type <b>help</b> for a
full list of the commands.</p>

<p><b>--datadir</b><br />
Tells Sockso where to store/look for all it's data.  This can be used to to
make Sockso portable for instance, by setting the data directory to wherever
you want it to be.  The (default) HSQLDB stores its data here, and cached cover
images too.</p>

<p><b>--upnp</b> (<i>EXPERIMENTAL</i>)<br />
Enable UPNP automatic port forwarding.  On startup Sockso will try
to forward the correct port from your UPNP enabled router.  This
requires the <a href="index.php?controller=manual&page=optionals">optionals package</a>.</p>

<p><b>--logtype=</b>(default|dev)<br />
Sets the type of messages Sockso will output.  "default" is the default and
just produces the usual information about what Sockso is doing.  "dev" can
be used to provide much more detailed information.
</p>

<p><b>--ip=</b>(ip address)<br />
Tell Sockso a fixed IP address to use (by default it'll try and work it
out itself).
</p>

<p><b>--locale=</b>(en|it|nb|de)<br />
Here you can specify the language to use for the management interface (through
the web interface Sockso will pick the language according to that specified
by the users browser).
</p>

<p><b>--query=</b>(optional filename)<br />
Specifies to run a query on the database and output the results as XML instead
of starting Sockso.  You can specify a file to read the SQL from, or Sockso
will try to read the SQL from stdin.
</p>

<p><b>--resourcestype=</b>(file|jar)<br />
This will tell Sockso whether to load it's resources from the jar file, or look
on the filesystem in the <b>resources</b> folder (default).
</p>

<h2>SSL</h2>

<p><b>--ssl</b><br />
Run Sockso using HTTPS instead of HTTP.
</p>

<p><b>--sslKeystore=</b>(filename)<br />
Specifies the location of a keystore to use for SSL
</p>

<p><b>--sslKeystorePassword=</b>(password)<br />
Specifies a password for the SSL keystore
</p>

<h2>Databases</h2>

<p><b>--dbtype=</b>(hsql|mysql|sqlite)<br />
The type of database to use.  The MySQL and SQLite database require the
<a href="index.php?controller=manual&page=optionals">optionals installed</a>.
</p>

<p><b>--dbhost=</b>(DNS or IP)<br />
The host for the database server
</p>

<p><b>--dbuser=</b>(username)<br />
Database user
</p>

<p><b>--dbpass=</b>(password)<br />
Database user's password
</p>

<p><b>--dbname=</b>(name)<br />
Name of the database to use
</p>


<p><b>--help</b><br />
Prints out information about the command line options.</p>

{include file="footer.tpl"}
