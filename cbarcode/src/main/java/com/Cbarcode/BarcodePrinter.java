package com.Cbarcode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import javax.swing.*;

public class BarcodePrinter {

    // printBarcode 메서드가 JFrame과 JLabel을 처리하도록 수정
    public static void printBarcode(JFrame frame, JLabel barcodeLabel, JTextField inputField) {
        // 바코드 이미지 아이콘을 JLabel에서 가져옵니다.
        Icon icon = barcodeLabel.getIcon();
        if (icon == null) {
            JOptionPane.showMessageDialog(frame, "인쇄할 바코드가 없습니다!");
            return;
        }

        // 바코드에 입력된 텍스트를 가져옵니다.
        String text = inputField.getText(); // 바코드 텍스트 (예: "Example 1234")
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "텍스트를 입력하세요!");
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        
        // 인쇄 작업 이름을 바코드 텍스트로 설정
        job.setJobName(text);
    
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
    
            Graphics2D g2d = (Graphics2D) graphics;
    
            // 좌표계를 페이지의 출력 가능 영역으로 변환
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    
            // Icon에서 이미지를 가져옴
            BufferedImage barcodeImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = barcodeImage.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
    
            // 이미지를 90도 회전 및 크기 조정
            double scale = 0.5; // 이미지 크기를 50%로 줄임
            int rotatedWidth = (int) (barcodeImage.getHeight() * scale);
            int rotatedHeight = (int) (barcodeImage.getWidth() * scale);
    
            // 회전과 크기 조정 적용
            g2d.rotate(Math.toRadians(90), rotatedWidth / 2.0, rotatedHeight / 2.0); // 90도 회전
            g2d.scale(scale, scale); // 크기 조정
    
            // 이미지 출력
            g2d.drawImage(barcodeImage, 0, -rotatedWidth, null);
    
            return Printable.PAGE_EXISTS;
        });
        // 프린터 다이얼로그 표시 후 인쇄
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(frame, "인쇄 실패: " + ex.getMessage());
            }
        }
    }
}
