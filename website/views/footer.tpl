
{php}
$smarty->assign( 'downloadExtension',
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
            <input type="image" src="{$filesUrl}images/donate_now.png" border="0" name="submit" alt="Make payments with PayPal - it's fast, free and secure!">
        </form>

        <h1>Latest News &amp; Releases <a href="http://code.google.com/feeds/p/sockso/downloads/basic"><img src="{$filesUrl}images/rss_icon.gif" alt="RSS Icon" /></a></h1>

        {include file="news-latest.tpl"}

        <ul>
            <li><a href="index.php?controller=archive">older news...</a></li>
        </ul>

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
                                <li><a href="index.php?controller=manual&page=installing">Installing Sockso</a></li>
                                <li><a href="index.php?controller=manual&page=gui">Collection Manager</a></li>
                                <li><a href="index.php?controller=manual&page=www">Web Interface</a></li>
                                <li><a href="index.php?controller=manual&page=properties">Properties</a></li>
                                <li><a href="index.php?controller=manual&page=customize">Customizing</a></li>
                                <li><a href="index.php?controller=manual&page=console">The Console</a></li>
                                <li><a href="index.php?controller=manual&action=artwork">Cover Artwork</a></li>
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
            <a href="http://www.kernel.org"><img src="{$filesUrl}images/icon-linux.png" alt="Linux Logo" /></a>
            <a href="http://www.microsoft.com/windows"><img src="{$filesUrl}images/icon-windows.png" alt="Windows Logo" /></a>
            <a href="http://www.apple.com/macosx"><img src="{$filesUrl}images/icon-osx.png" alt="OSX Logo" /></a>
        </p>
    </center>
</body>
</html>
