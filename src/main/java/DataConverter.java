import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.*;

public class DataConverter {
    static ImageWriter imageWriter;
    private static ImageWriteParam imageWriteParam;
    static Iterator<ImageWriter> writers;
    public static BufferedImage qualityOfImage(float percent, BufferedImage image) throws IOException {
        if(percent<100&&image!=null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream);
            getImageWriteParam().setCompressionQuality(percent/100);
            imageWriter.setOutput(ios);
            imageWriter.write(null, new IIOImage(image, null, null), getImageWriteParam());
            byte[] data = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            return ImageIO.read(bis);
        }
        else {

            return image;
        }
    }
    public static float qualityOfAudio(float percent)
    {

        return 8000+360*percent;
    }


  public static void  configureDataConverter()
   {
       imageWriter = ImageIO.getImageWritersByFormatName("jpeg").next();
       setImageWriteParam(imageWriter.getDefaultWriteParam());
       getImageWriteParam().setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
       writers = ImageIO.getImageWritersByFormatName("jpg");


   }

    private static ImageWriteParam getImageWriteParam() {
        return imageWriteParam;
    }

    private static void setImageWriteParam(ImageWriteParam imageWriteParam) {
        DataConverter.imageWriteParam = imageWriteParam;
    }
}
