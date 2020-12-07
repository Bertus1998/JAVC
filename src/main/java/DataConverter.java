import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataConverter {
    public static byte[] convertImageToByte( BufferedImage image, int quality) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", bos );
        byte [] data = bos.toByteArray();
        return data;
    }
    public static BufferedImage convertByteToImage(byte [] bytesImage) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytesImage);
        BufferedImage image = ImageIO.read(bis);
        return image;
    }
}
