
{php}
$this->assign( 'downloadExtension',
    preg_match('/Macintosh/',$_SERVER['HTTP_USER_AGENT'])
        ? 'dmg' : 'zip' );
{/php}

        <!-- end latest-post -->
    </div>


    <!-- start recent-posts -->
    <div id="recent-posts">

        <p>If you love using Sockso and would like to give a little back,
        try a donation to help out and support open source development.</p>

        <form class="donate" name="_xclick" action="https://www.paypal.com/cgi-bin/webscr" method="post">
            <input type="hidden" name="cmd" value="_xclick">
            <input type="hidden" name="business" value="rod.naph@gmail.com">
            <input type="hidden" name="item_name" value="Sockso">
            <input type="hidden" name="currency_code" value="GBP">
            <input type="hidden" name="amount" value="5">
            <input type="image" src="{$smutty->baseUrl}/images/donate_now.png" border="0" name="submit" alt="Make payments with PayPal - it's fast, free and secure!">
        </form>

        <h1>Latest News &amp; Releases <a href="http://code.google.com/feeds/p/sockso/downloads/basic"><img src="{$smutty->baseUrl}/images/rss_icon.gif" alt="RSS Icon" /></a></h1>

        <div class="post">
            <h2>30th January 2011</h2>
            <p><a href="http://sockso.googlecode.com/files/sockso-1.2.6.{$downloadExtension}">Sockso 1.2.6</a>
            -
            Fix for selecting the playlist in some browsers, and added new {link url={ controller="manual" action="webAdmin" } text="web admin console"}.
            </p>
        </div>

        <div class="post">
            <h2>17th December</h2>
            <p><a href="http://sockso.googlecode.com/files/sockso-1.2.5.{$downloadExtension}">Sockso 1.2.5</a>
            -
            Minor bug fix for adding items to the playlist when browsing folders.
            </p>
        </div>

        <div class="post">
            <h2>10th April</h2>
            <p><a href="http://sockso.googlecode.com/files/sockso-1.2.4.{$downloadExtension}">Sockso 1.2.4</a>
            -
            Patches from Will to improve HTTP/HTTPS streaming performance, and HTML compliance, improved
            music tree in GUI to cope with any size collections, hidden files now ignored on indexing,
            and some other bugs.
            </p>
        </div>

        <div class="post">
            <h2>8th February</h2>
            <p><a href="http://sockso.googlecode.com/files/sockso-1.2.3.{$downloadExtension}">Sockso 1.2.3</a>
            -
            Fixed broken 1.2.2 release.
            </p>
        </div>

        <ul>
            <li>{link url={ controller="index" action="news" } text="older news..."}</li>
        </ul>

    </div>
    <!-- end recent-posts -->
    </div>
	<!-- end content -->
	<!-- start sidebar -->
	<div id="sidebar">
            <ul>
                <li>
                    <h2>Libraries</h2>
                    <ul>

                        <li><a href="http://hsqldb.org/">HSQLDB</a></li>
                        <li><a href="http://www.jgoodies.com/">JGoodies</a></li>
                        <li><a href="http://www.jamon.org/">Jamon</a></li>
                        <li><a href="http://www.jcraft.com/jorbis/">JOrbis</a></li>
                        <li><a href="http://musicplayer.sourceforge.net/">XSPF Player</a></li>
                        <li><a href="http://jdic.dev.java.net/">JDIC</a></li>
                        <li><a href="http://systray.sourceforge.net/">Systray</a></li>
                        <li><a href="http://limewire.com">Limewire</a></li>
                        <li><a href="http://www.kde-look.org/content/show.php?content=25668">Crystal Clear</a></li>
                    </ul>
                </li>
                <li>
                    <h2>&nbsp;</h2>
                    <ul>
                        <li><a href="http://jopt-simple.sourceforge.net/">JOpt Simple</a></li>
                        <li><a href="http://logging.apache.org/log4j/">Log4j</a></li>
                        <li><a href="http://jquery.com">JQuery</a></li>
                        <li><a href="http://easymock.org">EasyMock</a></li>
                        <li><a href="http://ant.apache.org">Ant</a></li>
                        <li><a href="http://junit.org">JUnit</a></li>
                        <li><a href="http://sourceforge.net/projects/jarbundler/">Jar Bundler Ant</a></li>
                        <li><a href="http://browserlaunch2.sourceforge.net/">BrowserLauncher</a></li>
                        <li><a href="http://jflac.sourceforge.net/">jFlac</a></li>
                    </ul>
                </li>
                <li>
                        <h2>Random</h2>
                        <ul>
                                <li><a href="http://last.fm">last.fm</a></li>
                                <li><a href="http://arstechnica.com">arstechnica</a></li>
                                <li><a href="http://slashdot.org">slashdot</a></li>
                                <li><a href="http://ejohn.org">ejohn</a></li>
                                <li><a href="http://www.thedailywtf.com">wtf</a></li>
                        </ul>
                </li>
                <li>
                        <h2>Manual</h2>
                        <ul>
                                <li>{link url={ controller="manual" action="installing" } text="Installing Sockso" }</li>
                                <li>{link url={ controller="manual" action="gui" } text="Collection Manager" }</li>
                                <li>{link url={ controller="manual" action="www" } text="Web Interface" }</li>
                                <li>{link url={ controller="manual" action="properties" } text="Properties" }</li>
                                <li>{link url={ controller="manual" action="customize" } text="Customizing" }</li>
                                <li>{link url={ controller="manual" action="console" } text="The Console" }</li>
                                <li>{link url={ controller="manual" action="artwork" } text="Cover Artwork" }</li>
                        </ul>
                </li>
            </ul>
            <div style="clear: both;">&nbsp;</div>
	</div>
    <!-- end sidebar -->
    </div>
    <!-- end page -->
    <div id="footer">
	<p id="legal">&copy;2008 Rod.  Some Rights Reserved.  Designed by <a href="http://www.freecsstemplates.org/">Free CSS Templates</a></p>
    </div>
    <center>
        <p>
            <a href="http://www.kernel.org"><img src="{$smutty->baseUrl}/images/icon-linux.png" alt="Linux Logo" /></a>
            <a href="http://www.microsoft.com/windows"><img src="{$smutty->baseUrl}/images/icon-windows.png" alt="Windows Logo" /></a>
            <a href="http://www.apple.com/macosx"><img src="{$smutty->baseUrl}/images/icon-osx.png" alt="OSX Logo" /></a>
        </p>
        <p>{smutty_link}</p>
    </center>
</body>
</html>
