
{include file="header.tpl" title="Encoders"}

<h1>Encoders</h1>

<p>With Sockso, it's possible to use various different encoders when playing
music.  You probably want to do this for one of the following reasons...</p>

<ul>
<li>Re-encode one format to another (eg. so Ogg files can be played with
Sockso's flash player, which only support mp3s)</li>
<li>Re-encode music at a lower bitrate so it can be listened to on slower
connections</li>
</ul>

<h2>The Encoders Panel</h2>

<p>The encoders panel in Sockso lists the various file formats that Sockso
can handle, and then allows you to specify how Sockso should handle each of
these different types.  The default is to stream the track unaltered.</p>

<p><img src="{$filesUrl}/images/manual/encoders-1.png" /></p>

<h2>Builtin Encoding</h2>

<p>These are in the form of settings and/or scripts that come with Sockso
that allow you to just select an option from the list and have your music encoded.
You will need to have the correct programs installed on your computer though,
Sockso just provides the means to use them.  So for example if you want to use
Lame to re-encode your mp3's at a lower bitrate, you'll need to have it installed
and available in your PATH.</p>

<h2>Custom Encoding</h2>

<p>This option allows you to specify a command to use to re-encode your music.  This
will probably be in the form of a shell script which does whatever you want to the
music before playing it.  Whatever program you use, it should take the path of the
track as its first argument, and return the audio data to stdout (Sockso will then
read it back in and send it to the users music player).

<h1>Console</h1>

<p>You can also set up encoding through the console by editing Sockso's properties.
The properties live under the <b>encoders</b> prefix.  Here's an example that will
enable builtin Lame encoding for mp3's...</p>

<pre>
encoders.mp3 BUILTIN
encoders.mp3.name Lame
encoders.mp3.bitrate 128
</pre>
<br />

<p>So first you need to set the type of encoder for the file extension, this type
can be either NONE, BUILTIN, or CUSTOM, eg...</p>

<pre>
encoders.mp3 NONE
encoders.ogg BUILTIN
encoders.flac CUSTOM
</pre>
<br />

<h2>NONE</h2>

<p>If the value for the file type is set to <b>NONE</b>, then the tracks of this type
will be streamed completely unaltered.</p>

<pre>
encoders.wma NONE
</pre>
<br />

<h2>BUILTIN</h2>

<p>If set to <b>BUILTIN</b>, you'll then need to specify a name for the builtin
encoder that you want to use.  Currently the builtin encoders you can choose
from are...</p>

<ul>
    <li><b>Lame</b> - Re-encodes mp3's to a different bitrate.</li>
    <li><b>OggDecToLame</b> - Converts Ogg to mp3</li>
    <li><b>FlacToLame</b> - Convert FLAC to mp3</li>
</ul>

<pre>
encoders.ogg BUILTIN
encoders.ogg.name OggDecToLame
</pre>
<br />

<h2>CUSTOM</h2>

<p>If you want to specify your own script (as explained above) to do the encoding
then set this to <b>CUSTOM</b>, and use the command property to give the full command
to run your script.</p>

<pre>
encoders.flac CUSTOM
encoders.flac.command /bin/sh /home/me/re-encode.sh
</pre>
<br />

{include file="footer.tpl"}
