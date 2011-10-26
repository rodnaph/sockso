package com.pugh.sockso.music;

import com.pugh.sockso.Utils;
import com.pugh.sockso.resources.Locale;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CoverArtUtils {

    public static final String[] CACHE_IMAGE_EXTENSIONS = {"jpg", "gif", "png"};
    public static final String CACHE_IMAGE_TYPE = "jpg";

    //private static Logger log = Logger.getLogger( CoverArtUtils.class );
    private CoverArtUtils() {
    }

    /**
     *  scales an image to the specified width and height and returns a new image
     *
     *  @param origImage
     *  @param width
     *  @param height
     *
     *  @return
     *
     */
    public static BufferedImage scale(final BufferedImage origImage, int width, int height) {

        final int origWidth = origImage.getWidth();
        final int origHeight = origImage.getHeight();

        // check if we need to resize at all
        if (width >= origWidth && height >= origHeight) {
            return origImage;
        }

        return scale( origImage, calcScalingFactor(origWidth, origHeight, width, height) );
    }

    /**
     *  Scale the image by the specified factor
     *
     *  @param image
     *  @param dScaleFactor
     *
     *  @return
     *
     */
    private static BufferedImage scale(Image image, double dScaleFactor) {

        // calculate new width and height
        int iWidth  = (int) (image.getWidth(null)  * dScaleFactor);
        int iHeight = (int) (image.getHeight(null) * dScaleFactor);

        // create a BufferedImage instance
        BufferedImage bufferedImage = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);

        // create the image's graphics
        Graphics2D g = bufferedImage.createGraphics();

        // Drawing hints with focus on quality
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Apply scalefactor
        g.drawImage(image, 0, 0, iWidth, iHeight, null);

        return bufferedImage;

    }

    /**
     *  Calculate the factor to scale the image by
     *
     *  @param srcWidth
     *  @param srcHeight
     *  @param targetWidth
     *  @param targetHeight
     *
     *  @return
     *
     */
    private static double calcScalingFactor(int srcWidth, int srcHeight, int targetWidth, int targetHeight) {

        final boolean tall = (srcHeight > srcWidth);
        final double factor = (double) (tall ? targetHeight : targetWidth)
                            / (double) (tall ? srcHeight : srcWidth);

        return factor;

    }


    public static String getNoCoverName( Locale locale) {

        return "nocover-" + locale.getLangCode();
    }

    /**
     *  Creates the no-cover image for music items with no artwork
     *
     *  @param locale
     *
     *  @return
     *
     */

    protected static BufferedImage createNoCover( final Locale locale ) {

        final int dim = 115;
        final BufferedImage cover = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = cover.createGraphics();

        // background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, dim, dim);

        // border
        g.setColor(new Color(200, 200, 200));
        g.drawRect(0, 0, dim - 1, dim - 1);

        // text
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana", Font.ITALIC, 10));
        g.drawString(locale.getString("www.text.noCover"), 10, 20);

        return cover;
    }

    public static BufferedImage getNoCoverImage( final Locale locale ) throws IOException {

        BufferedImage cover;
        String coverId = getNoCoverName( locale );

        String extension = existsInCache( coverId );

        if( extension != null ){
            cover = getCover( coverId, extension );
        }
        else {
            cover = createNoCover( locale );
            addToCache( cover, coverId, CACHE_IMAGE_TYPE );
        }

        return cover;
    }

    // If we don't know the extension we're looking for (any image type)
    public static String existsInCache( final String coverId ) {

        String extension = null;

        for (String ext : CACHE_IMAGE_EXTENSIONS) {
            if ( existsInCache( coverId, ext ) ){
                extension = ext;
                break;
            }
        }

        return extension;
    }


    // If we know the extension we're looking for
    public static boolean existsInCache( final String coverId, final String extension ) {

        File coverFile = getCoverCacheFile( coverId, extension );
        return (coverFile.isFile() && coverFile.exists());
    }


    public static boolean addToCache(final BufferedImage cover, final String coverId, final String extension ) throws IOException {

        return ImageIO.write( cover, extension, getCoverCacheFile( coverId, extension ) );
    }

        /**
     *  returns the absolute path of the cache file
     *
     *  @param name
     *  @param ext
     *
     *  @return the cache file path
     *
     */

    public static BufferedImage getCover( final String coverId, final String extension ) throws IOException {

        return ImageIO.read( getCoverCacheFile( coverId, extension ) );
    }


    public static File getCoverCacheFile( final String name, final String extension ) {

        return new File(Utils.getCoversDirectory() + File.separator + name + "." + extension);
    }

}
