import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class Steganography {

    static byte[] addSaltToText(@NotNull String text) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        return md.digest(text.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String [] args) throws Exception{

        FileReader reader = new FileReader("input.txt");
        BufferedReader in = new BufferedReader(reader);
        String s;
        StringBuilder text = new StringBuilder();
        while((s=in.readLine())!=null)
            text.append(s);

        ImageProcess imageProcess = new ImageProcess();
        byte [] txtBytes = addSaltToText(text.toString());
        BufferedImage img = imageProcess.fetchImage();
        imageProcess.hideText(img,txtBytes);
    }
}


class ImageProcess {
    BufferedImage fetchImage() throws Exception {
        File f = new File("image.png");
        return ImageIO.read(f);
    }

    void hideText(BufferedImage img, byte @NotNull [] txt) {
        int i = 0, j = 0;
        for (byte b : txt) {
            for (int k = 7; k >= 0; k--) {
                Color c = new Color(img.getRGB(j, i));
                byte blue = (byte) c.getBlue();
                int red = c.getRed();
                int green = c.getGreen();
                int bitVal = (b >>> k) & 1;
                blue = (byte) ((blue & 0xFE) | bitVal);
                Color newColor = new Color(red, green, (blue & 0xFF));
                img.setRGB(j, i, newColor.getRGB());
                j++;
            }
            i++;
        }
        System.out.println("Text Hidden");
        try {
            File output = new File("image_new.png");
            ImageIO.write(img, "png", output);
        } catch (Exception ignored) {
        }
    }
}