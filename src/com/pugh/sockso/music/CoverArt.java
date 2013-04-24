package com.pugh.sockso.music;

import com.pugh.sockso.Constants;
import com.pugh.sockso.Properties;
import com.pugh.sockso.resources.Locale;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class CoverArt {

    private static Logger log = Logger.getLogger(CoverArt.class);

    private String itemName;
    private BufferedImage image;

    public CoverArt(String itemName) {
        this.itemName = itemName;
    }

    public CoverArt(String itemName, BufferedImage image) {
        this.itemName = itemName;
        this.image = image;
    }

    public String getItemName() {

        return this.itemName;
    }

    public BufferedImage getImage() {

        return this.image;
    }

    public void setImage(BufferedImage image) {

        this.image = image;
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
    public BufferedImage scale(final int width, final int height) {

        if (image != null) {
            final int origWidth = image.getWidth();
            final int origHeight = image.getHeight();

            // check if we need to resize at all
            if (width == origWidth && height == origHeight) {
                return image;
            }

            return scale(calcScalingFactor(origWidth, origHeight, width, height));
        }

        return null;
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
    private BufferedImage scale(final double dScaleFactor) {

        // calculate new width and height
        int iWidth = (int) (image.getWidth() * dScaleFactor);
        int iHeight = (int) (image.getHeight() * dScaleFactor);

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
        this.image = bufferedImage;

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
    private double calcScalingFactor(int srcWidth, int srcHeight, int targetWidth, int targetHeight) {

        final boolean tall = (srcHeight > srcWidth);
        final double factor = (double) (tall ? targetHeight : targetWidth)
                / (double) (tall ? srcHeight : srcWidth);

        return factor;

    }

    /**
     *  Creates the no-cover image for music items with no artwork
     *
     *  @param locale
     *
     *  @return
     *
     */
    public static BufferedImage createNoCoverImage(final Properties properties, final Locale locale) {
        
        final int width  = (int) properties.get(Constants.DEFAULT_ARTWORK_WIDTH, 115);
        final int height = (int) properties.get(Constants.DEFAULT_ARTWORK_HEIGHT, 115);

        final BufferedImage cover = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = cover.createGraphics();

        // background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // border
        g.setColor(new Color(200, 200, 200));
        g.drawRect(0, 0, width - 1, height - 1);

        // text
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana", Font.ITALIC, 10));
        g.drawString(locale.getString("www.text.noCover"), 10, 20);

        return cover;
    }
}
