/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pugh.sockso.music;

import java.awt.image.BufferedImage;
import com.pugh.sockso.tests.SocksoTestCase;
import com.pugh.sockso.tests.TestLocale;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Nathan Perrier
 */
public class CoverArtTest extends SocksoTestCase {

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testGetItemName() {
        String itemName = "al123";
        CoverArt coverArt = new CoverArt(itemName);
        String result = coverArt.getItemName();
        assertEquals(itemName, result);
        // TODO: Test with null?
    }

    public void testGetImage() throws IOException {
        String itemName = "al123";
        BufferedImage image = ImageIO.read(new File("test\\data\\covers\\" + itemName + ".jpg"));
        CoverArt coverArt = new CoverArt(itemName, image);
        BufferedImage result = coverArt.getImage();
        assertEquals(image, result);
        // TODO: Test with null?
    }

    public void testSetImage() throws IOException {
        String itemName = "al123";
        BufferedImage image = ImageIO.read(new File("test\\data\\covers\\" + itemName + ".jpg"));
        CoverArt coverArt = new CoverArt(itemName);
        coverArt.setImage(image);
        BufferedImage result = coverArt.getImage();
        assertEquals(image, result);
        // TODO: Test with null?
    }

    public void testScale() throws IOException {
        int width  = 200;  // 115 <- scaling factor 200/115
        int height = 170;  //  (200/115) * 98 = 170 (+-1)
        int delta = 1;     
        String itemName = "al123";
        BufferedImage image = ImageIO.read(new File("test\\data\\covers\\" + itemName + ".jpg"));
        CoverArt coverArt = new CoverArt(itemName, image);
        BufferedImage result = coverArt.scale(width, height);
        assertEquals(width, result.getWidth(), delta);   // 200
        assertEquals(height, result.getHeight(), delta); // 170
    }

    public void testCreateNoCoverImage() {
        BufferedImage result = CoverArt.createNoCoverImage(new TestLocale());
        assertNotNull(result);
    }
}
