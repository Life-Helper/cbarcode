package com.Cbarcode;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainPanel extends JPanel {
    private static JTextField inputField;
    private static JLabel barcodeLabel;
    private static JLabel reportLabel;

    public MainPanel(JFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        inputField = new JTextField("Example 1234");
        barcodeLabel = new JLabel();
        reportLabel = new JLabel();

        // 초기 바코드 이미지 생성
        try {
            barcodeLabel.setIcon(new ImageIcon(BarcodeGenerator.generateBarcodeImage(inputField.getText(), false)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // UI 구성
        add(createBarcodePanel());
        add(createInputPanel());
        add(createButtonPanel(frame));
        add(createReportPanel());
    }

    private JPanel createBarcodePanel() {
        JPanel barcodePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        barcodePanel.add(barcodeLabel);
        return barcodePanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputField.setPreferredSize(new Dimension(300, 30));
        inputPanel.add(new JLabel("텍스트 입력:"));
        inputPanel.add(inputField);
        return inputPanel;
    }

    private JPanel createButtonPanel(JFrame frame) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("바코드 저장");
        JButton generateButton = new JButton("바코드 생성");
        JButton printButton = new JButton("바코드 인쇄");

        saveButton.addActionListener(e -> saveBarcode(frame));
        generateButton.addActionListener(e -> generateBarcode());
        printButton.addActionListener(e -> BarcodePrinter.printBarcode(frame, barcodeLabel, inputField));

        buttonPanel.add(saveButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(printButton);
        return buttonPanel;
    }

    private JPanel createReportPanel() {
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        reportPanel.add(reportLabel);
        return reportPanel;
    }

    public static void clearInputText() {
        inputField.setText("");
    }

    private void saveBarcode(JFrame frame) {
        String text = inputField.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "텍스트를 입력하세요!");
            return;
        }

        try {
            barcodeLabel.setIcon(new ImageIcon(BarcodeGenerator.generateBarcodeImage(text, true)));
            reportLabel.setText("저장되었습니다!");
        } catch (IOException | com.google.zxing.WriterException e) {
            JOptionPane.showMessageDialog(frame, "바코드 저장 실패: " + e.getMessage());
        }
    }

    private void generateBarcode() {
        String text = inputField.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "텍스트를 입력하세요!");
            return;
        }

        try {
            barcodeLabel.setIcon(new ImageIcon(BarcodeGenerator.generateBarcodeImage(text, false)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
