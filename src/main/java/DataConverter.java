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
        if(percent<1) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageWriter.setOutput(byteArrayOutputStream);
            imageWriter.write(null, new IIOImage(image, null, null), imageWriteParam);
            byte[] data = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            BufferedImage bImage2 = ImageIO.read(bis);
            return bImage2;
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
