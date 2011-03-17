
{include file="header.tpl" title="Command Line Options"}

<h1>Scheduling</h1>

<p>The scheduling functionality allows you to control how often Sockso scans
your collection for new/updated/removed tracks.  If you have a very large
collection you may want to make this less frequent than the default (which
is every 30 minutes).</p>

<h2>Simple Scheduling (default)</h2>

<p>By default Sockso uses a very simple scheduling technique, and just runs
a new scan every 30 minutes.  You can alter this frequency through the
GUI on the General panel.</p>

<h2>Cron Scheduling</h2>

<p>If you have demanding needs, or just want to take more control over scanning
then Sockso provides a cron like scheduler.  This allows you to specify the
frequency for collection scans using cron syntax.</p>

<h3>Enabling</h3>

<p>To enable this just enter the following command into the console:</p>

<pre>
propset scheduler cron
</pre>

<p>To revert back to simple scheduling just set this back to <b>simple</b> instead
of <b>cron</b>.</p>

<p><b>NB:</b> In Sockso versions 1.2.6 and below you will need to restart Sockso
any time you change the scheduler for it to take effect.</p>

<h3>Configuring</h3>

<p>You can now configure the cron scheduler (changes take effect straight away
when it is running) using the following property:</p>

<pre>
propset scheduler.cron.tab */30 * * * *
</pre>

<p>The above command sets scanning to run every 30 minutes.  For more information
about how to use cron see <a href="http://en.wikipedia.org/wiki/Cron#crontab_syntax">the wikipedia article</a>.
You can also use the special crontab @ commands, like this:</p>

<pre>
propset scheduler.cron.tab @hourly
</pre>

<h2>Manual Scheduling</h2>

<p>If you'd like to never have indexing run automatically you can use the manual
scheduler.</p>

<pre>
propset scheduler manual
</pre>

<p>With this scheduler running you'll have to manually run the scan.</p>

{include file="footer.tpl"}
