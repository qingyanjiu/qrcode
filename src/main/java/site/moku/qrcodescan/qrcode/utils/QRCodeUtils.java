package site.moku.qrcodescan.qrcode.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

@Component
public class QRCodeUtils {
    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸    
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度    
    private static final int WIDTH = 60;
    // LOGO高度    
    private static final int HEIGHT = 60;

    @Value("${logo.imagePath}")
    private String imagePath;

    public String generateBase64Image(String content, boolean needCompress) {
        String result = "";
        try {
            BufferedImage image = createImage(content, needCompress);
            result = encodeBase64Image(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private String encodeBase64Image(BufferedImage image) {
        byte[] buffer = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            buffer = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BASE64Encoder().encode(buffer);
    }


    public BufferedImage createImage(String content,
                                     boolean needCompress) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        // 插入图片
        this.insertImage(image, needCompress);
        return image;
    }

    /**
     * 插入LOGO
     *
     * @param source       二维码图片
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private void insertImage(BufferedImage source,
                             boolean needCompress) throws Exception {
        String filePath = this.getClass().getClassLoader().getResource("").getPath()+imagePath;
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("" + filePath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(filePath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO    
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图    
            g.dispose();
            src = image;
        }
        // 插入LOGO    
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        RoundRectangle2D.Float shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

//    public static void main(String[] args) throws Exception {
//        String text = "http://www.baidu.com";  //这里设置自定义网站url
//        String logoPath = "";
//        String destPath = "C:\\Users\\louisliu\\Desktop\\";
//        System.out.println(QRCodeUtils.createImage(text, logoPath, true));
//    }
}  