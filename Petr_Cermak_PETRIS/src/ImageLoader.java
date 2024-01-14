import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader {
    /**
     * loads the pngs from the data folder
     * all pngs were made by me
     */
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File("pngs" + path));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
