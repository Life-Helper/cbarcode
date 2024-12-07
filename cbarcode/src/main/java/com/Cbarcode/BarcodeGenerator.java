package com.Cbarcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BarcodeGenerator {
    public static BufferedImage generateBarcodeImage(String text, boolean save) throws WriterException, IOException {
        // 바코드 생성
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.CODE_128, autoWidthLength(text), 150);

        // 바코드 이미지를 생성
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 전체 이미지 높이를 바코드 위와 아래에 여백을 추가하여 확장
        int totalHeight = barcodeImage.getHeight() + 40; // 위와 아래 여백 20씩 추가
        BufferedImage combinedImage = new BufferedImage(barcodeImage.getWidth(), totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // 배경 색상 설정 (배경을 흰색으로 설정)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, combinedImage.getWidth(), combinedImage.getHeight());

        // 바코드 이미지 그리기 (위쪽 여백 20px 추가 후 그리기)
        g2d.drawImage(barcodeImage, 0, 20, null); // 바코드를 20px 아래로 배치

        // 텍스트 그리기 (바코드 아래에 텍스트 삽입)
        g2d.setColor(Color.BLACK);  // 텍스트 색상 설정
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));  // 폰트 설정
        FontMetrics fm = g2d.getFontMetrics();
        int x = (combinedImage.getWidth() - fm.stringWidth(text)) / 2; // 텍스트 중앙 정렬
        int y = barcodeImage.getHeight() + 38;  // 바코드 아래에 20px 여백 추가
        g2d.drawString(text, x, y);

        g2d.dispose();

        if (save) {
            String outputDirPath = SettingsManager.loadPath();
            File outputDir = new File(outputDirPath);
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("디렉토리를 만들 수 없습니다: " + outputDirPath);
            }

            File outputFile = new File(outputDir, text + ".jpg");
            ImageIO.write(combinedImage, "jpg", outputFile);
        }

        return combinedImage;
    }

    public static int autoWidthLength(String text) {

        // 바코드 이미지 넓이 동적 변경
        if (text.length() > 14) {
            return 450;
        }

        return 400;
    }
}
