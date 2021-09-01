package com.company.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

import javax.imageio.ImageIO;

public class ImageChangeUtils {

    /**
     * base64文字列への画像
     *
     * @param file 画像ファイル
     * @return
     */
    public static String imageToBase64ByFile(File file) throws IOException {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(file);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 暗号化
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * base64でエンコードされた文字列を画像に変換し、ファイルに書き込みます
     *
     * @param imgStr base64エンコーディング文字列
     * @param path   ピクチャーパス
     * @return
     */
    public static boolean base64StrToImage(String imgStr, String path) {
        if (imgStr == null)
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 復号化
            byte[] b = decoder.decodeBuffer(imgStr);
            // データ処理
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            //フォルダが存在しない場合は、自動的に作成されます
            File tempFile = new File(path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(tempFile);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void joinImage(File fromFile,File toFile,String path){
        try {
            Integer allWidth = 0;	// 画像の全幅
            Integer w1 = 0;
            Integer w2 = 0;
            Integer h1 = 0;
            Integer h2 = 0;

            BufferedImage fromRead = ImageIO.read(fromFile);
            BufferedImage toRead = ImageIO.read(toFile);
            w1 = fromRead.getWidth();
            w2 = toRead.getWidth();
            allWidth = w1 + w2;
            h1 = fromRead.getHeight();
            h2 = toRead.getHeight();
            int hh = h1 > h2 ? h1 : h2;

            BufferedImage combined = new BufferedImage(allWidth, hh, BufferedImage.TYPE_INT_RGB);
            Graphics g = combined.getGraphics();
//            g.setColor(Color.WHITE);
//            g.fillRect(0,0,allWidth,hh);

            // 水平方向の構成
            g.drawImage(fromRead, 0, 0, null);
//          g.setColor(Color.BLACK);
            g.drawRect(w1, 0, w1, hh);
            g.drawImage(toRead, w1, 0, null);

            ImageIO.write(combined, "png", new File(path));
        } catch (Exception e) {
            //e.printStackTrace();
            LogUtils.error(e.getMessage());
        }
    }
}
