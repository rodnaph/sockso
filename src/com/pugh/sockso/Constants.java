
package com.pugh.sockso;

/**
 *  provides static constants for the application
 * 
 */

public class Constants {

    //**********************************************************
    //**********************************************************
    //
    //  PROPERTIES
    //
    //**********************************************************
    //**********************************************************

    //
    //  gui app
    //
    public static final String APP_START_MINIMIZED = "app.startMinimized";
    public static final String APP_CONFIRM_EXIT = "app.confirmExit";
    //
    //  server
    //
    public static final String SERVER_HOST = "server.host";
    public static final String SERVER_HOST_LAST_UPDATED = "server.host.lastUpdated";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_BASE_PATH = "server.basepath";
    public static final String SERVER_KEY = "server.key";
    //
    //  www
    //
    public static final String WWW_TITLE = "www.title";
    public static final String WWW_TAGLINE = "www.tagline";
    public static final String WWW_FLASHPLAYER_DONTFILTERMP3S = "www.flashPlayer.dontFilterMp3s";
    public static final String WWW_IMAGEFLOW_DISABLE = "www.imageflow.disable";
    public static final String WWW_SKIN = "www.skin";
    // downloads
    public static final String WWW_DOWNLOADS_DISABLE = "www.disableDownloads";
    // users
    public static final String WWW_USERS_REQUIRE_LOGIN = "users.requireLogin";
    public static final String WWW_USERS_DISABLE_REGISTRATION = "users.disableRegistration";
    public static final String WWW_USERS_REQUIRE_ACTIVATION = "users.requireActivation";
    // streaming
    public static final String STREAM_REQUIRE_LOGIN = "stream.requireLogin";
    public static final String STREAM_USE_BUFFER = "stream.useBuffer";
    public static final String STREAM_BUFFER_SIZE = "stream.bufferSize";
    public static final String STREAM_PAUSE_MILLIS = "stream.pauseMillis";
    // uploads
    public static final String WWW_UPLOADS_ENABLED = "uploads.enabled";
    public static final String WWW_UPLOADS_ALLOW_ANONYMOUS = "uploads.allowAnonymous";
    public static final String WWW_UPLOADS_COLLECTION_ID = "uploads.collectionId";
    // playlists
    public static final String WWW_RANDOM_TRACK_LIMIT = "playlists.random.trackLimit";
    // browsing
    public static final String WWW_BROWSE_FOLDERS_ENABLED = "browse.folders.enabled";
    public static final String WWW_BROWSE_FOLDERS_ONLY = "browse.folders.only";
    public static final String WWW_BROWSE_POPULAR_TRACK_COUNT = "browse.popularTracks.count";
    public static final String WWW_BROWSE_LATEST_TRACKS_COUNT = "browse.latestTracks.count";
    public static final String WWW_BROWSE_LATEST_ALBUMS_COUNT = "browse.latestAlbums.count";
    public static final String WWW_BROWSE_LATEST_ARTISTS_COUNT = "browse.latestArtists.count";
    public static final String WWW_BROWSE_RECENT_TRACKS_COUNT = "browse.recentTracks.count";
    public static final String WWW_BROWSE_TOP_ARTISTS_COUNT = "browse.topArtists.count";
    // logging
    public static final String WWW_LOG_REQUESTS_ENABLED = "log.requests.enable";
    //
    //  collection manager
    //
    public static final String COLLMAN_SCAN_ONSTART = "collman.scan.onStart";
    public static final String COLLMAN_SCAN_INTERVAL = "collman.scan.interval";
    public static final String COLLMAN_ARTIST_REMOVE_PREFIXES = "collman.artists.removePrefixes";
    
    //**********************************************************
    //**********************************************************
    //
    //  MISC
    //
    //**********************************************************
    //**********************************************************

    public static final long SERVER_IP_TIMEOUT = 60 * 20 * 1000; // milliseconds
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_COOKIE_DATE_FORMAT = "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'";
    public static final long ONE_WEEK_IN_MILLIS = 60 * 60 * 24 * 7 * 1000;

    public static final String VERSION_CHECK_DISABLED = "version.checkDisabled";

    public static final String URL_CHAR_ENCODING = "UTF-8";

    public static final String COVERS_CACHE_LOCAL = "covers.cacheLocal";
    public static final String COVERS_ALBUM_FILE = "covers.album.file";
    public static final String COVERS_ARTIST_FILE = "covers.artist.file";
    public static final String COVERS_XSPF_DISPLAY = "covers.xspf.display";
    public static final String COVERS_FILE_FALLBACK = "covers.file.fallback";

    public static final String COVERS_DISABLED = "www.covers.disable";
    public static final String COVERS_DISABLE_REMOTE_FETCHING = "www.covers.disableRemoteFetching";

    public static final String DEFAULT_ARTWORK_WIDTH = "covers.defaultWidth";
    public static final String DEFAULT_ARTWORK_HEIGHT = "covers.defaultHeight";

    //**********************************************************
    //**********************************************************
    //
    //  URLS
    //
    //**********************************************************
    //**********************************************************

    public static final String WEBSITE_URL = "http://sockso.pu-gh.com";
    public static final String VERSION_LATEST_URL = WEBSITE_URL + "/version/latest";

    //**********************************************************
    //**********************************************************
    //
    //  DEV
    //
    //**********************************************************
    //**********************************************************

    public static final String DEV_ENABLED = "dev.enabled";
 
    //**********************************************************
    //**********************************************************
    //
    //  MAIL
    //
    //**********************************************************
    //**********************************************************

    public static final String MAIL_ENABLED = "mail.enabled";
    public static final String MAIL_HOST = "mail.host";
    public static final String MAIL_USER = "mail.user";
    public static final String MAIL_PASS = "mail.pass";
    public static final String MAIL_TYPE = "mail.type";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    //**********************************************************
    //**********************************************************
    //
    //  SCHEDULER
    //
    //**********************************************************
    //**********************************************************

    public static final String SCHED = "scheduler";
    public static final String SCHED_SIMPLE_INTERVAL = "scheduler.simple.interval";
    public static final String SCHED_CRON_TAB = "scheduler.cron.tab";
    
    //**********************************************************
    //**********************************************************
    //
    //  COMMUNITY
    //
    //**********************************************************
    //**********************************************************

    public static final String COMMUNITY_ENABLED = "community.enabled";
    public static final String COMMUNITY_PING_URL = "community.pingUrl";

}
