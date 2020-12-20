import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.*;

public class DataConverter {
    static ImageWriter imageWriter;
    static ImageWriteParam imageWriteParam;
    static Iterator<ImageWriter> writers;
    public static BufferedImage qualityOfImage(float percent, BufferedImage image) throws IOException {
        System.out.println(percent);
        if(percent<100) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream);
            imageWriter.setOutput(ios);
            imageWriter.write(null, new IIOImage(image, null, null), imageWriteParam);
            byte[] data = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            return ImageIO.read(bis);
        }
        else {

            return image;
        }
    }


  public static void  configureDataConverter()
   {
       imageWriter = ImageIO.getImageWritersByFormatName("jpeg").next();
       imageWriteParam = imageWriter.getDefaultWriteParam();
       imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
       writers = ImageIO.getImageWritersByFormatName("jpg");


   }
}
