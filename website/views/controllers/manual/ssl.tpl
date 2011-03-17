
{include file="header.tpl" title="SSL"}

<h1>Starting Sockso with SSL</h1>

<p>If you would like to start Sockso using SSL instead of the default then the
most basic way to do this is just to use the command line switch.</p>

<pre>
$> java -jar sockso.jar --ssl
</pre>

<p>This uses the default unsigned certificate in the keystore that is provided
with the distribution.  If you would like to specify the location of your
own keystore then you will need to specify where to find it, and the password
as command line arguments.</p>

<pre>
$> java -jar sockso.jar --ssl --sslKeystore=/home/me/keystore \
   --sslKeystorePassword=secret
</pre>

<p><b>NOTE:</b> Running Sockso over SSL is not well tested and will impose a performance
hit on your server.</p>

{include file="footer.tpl"}
