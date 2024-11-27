package com.Cbarcode;

// https://github.com/Life-Helper/cbarcode/releases/latest

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
// import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("CBarcode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);

        // 메뉴바 생성
        JMenuBar menuBar = new JMenuBar();

        // 메뉴 생성
        JMenu fileMenu = new JMenu("파일");
        JMenu editMenu = new JMenu("편집");
        JMenu helpMenu = new JMenu("도움말");

        // "파일" 메뉴에 항목 추가
        JMenuItem exitItem = new JMenuItem("종료");
        fileMenu.add(exitItem);

        // "편집" 메뉴에 항목 추가
        JMenuItem clearTextItem = new JMenuItem("텍스트 초기화");
        editMenu.add(clearTextItem);

        // "도움말" 메뉴에 항목 추가
        JMenuItem aboutItem = new JMenuItem("정보");
        helpMenu.add(aboutItem);

        // "업데이트 확인..." 메뉴 항목 추가 및 동작 정의
        JMenuItem checkUpdateItem = new JMenuItem("업데이트 확인...");
        helpMenu.add(checkUpdateItem);

        // 메뉴바에 메뉴 추가
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        // 메뉴바를 프레임에 설정
        frame.setJMenuBar(menuBar);

        // UI 구성 요소
        JTextField inputField = new JTextField("Example 1234");
        JButton generateButton = new JButton("바코드 생성");
        JLabel barcodeLabel = new JLabel();
        JLabel reportLabel = new JLabel();
        JButton printButton = new JButton("바코드 인쇄");
        JButton saveButton = new JButton("바코드 저장");

        // "텍스트 입력:"과 텍스트 필드를 가로로 배치하는 새로운 패널
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // 가로로 가운데 정렬
        inputField.setPreferredSize(new Dimension(300, 30)); // 텍스트 입력 칸 높이를 30으로 설정
        inputPanel.add(new JLabel("텍스트 입력:"));
        inputPanel.add(inputField);

        // 프로그램 시작 시 초기 바코드 이미지 표시
        try {
            BufferedImage barcodeImage = generateBarcodeImage(inputField.getText(), false);
            barcodeLabel.setIcon(new ImageIcon(barcodeImage));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        // 바코드 이미지를 가로로 가운데 정렬할 패널
        JPanel barcodePanel = new JPanel();
        barcodePanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // 바코드 이미지 패널 가로 가운데 정렬
        barcodePanel.add(barcodeLabel);  // 바코드 라벨 추가

        // 안내 문장을 가운데 정렬하는 패널
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        reportPanel.add(reportLabel);

        // 버튼들을 가로로 배치하는 새로운 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // 가로로 배치
        buttonPanel.add(saveButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(printButton);

        // 레이아웃 구성
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // 세로 레이아웃
        panel.add(barcodePanel);  // 바코드 이미지를 가운데로 배치
        panel.add(inputPanel);  // 텍스트 입력 패널 추가
        panel.add(buttonPanel);  // 버튼 패널 추가
        panel.add(reportPanel);  // 안내 문장 패널 추가

        frame.add(panel);
        frame.setVisible(true);
        

        // "종료" 메뉴 항목 클릭 시 동작 정의
        exitItem.addActionListener(e -> System.exit(0));

        // "텍스트 초기화" 메뉴 항목 클릭 시 동작 정의
        clearTextItem.addActionListener(e -> inputField.setText(""));

        // "정보" 메뉴 항목 클릭 시 동작 정의
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Create Barcode\n" + //
                        "        바코드 생성 프로그램\n" + //
                        "\n" + //
                        "        버전: v1.0\n" + //
                        "        소속: CHA1 Sub-FC_C HL\n" + //
                        "        만든이: Shan"));


        // Button actions
        saveButton.addActionListener(e -> {
            String text = inputField.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "텍스트를 입력하세요!");
                return;
            }

            try {
                BufferedImage barcodeImage = generateBarcodeImage(text, true);
                barcodeLabel.setIcon(new ImageIcon(barcodeImage));
                barcodeLabel.setText(""); // Remove any previous text
                barcodeLabel.revalidate();

                // Report label에 메시지 표시
                String documentsPath = System.getProperty("user.home") + "\\Documents\\CBarcode\\" + text + ".jpg";
                reportLabel.setForeground(Color.RED); // 텍스트 색상 붉은색으로 설정
                reportLabel.setText(documentsPath + "에 저장되었습니다.");

                // 1초 후 메시지 제거하는 타이머 설정
                Timer timer = new Timer(3000, event -> reportLabel.setText(""));
                timer.setRepeats(false); // 한 번만 실행되도록 설정
                timer.start();
                
            } catch (WriterException | IOException ex) {
                JOptionPane.showMessageDialog(frame, "바코드 생성 오류: " + ex.getMessage());
            }
        });

        generateButton.addActionListener(e -> {
            String text = inputField.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "텍스트를 입력하세요!");
                return;
            }

            try {
                BufferedImage barcodeImage = generateBarcodeImage(text, false);
                barcodeLabel.setIcon(new ImageIcon(barcodeImage));
                barcodeLabel.setText(""); // Remove any previous text
                barcodeLabel.revalidate();
            } catch (WriterException | IOException ex) {
                JOptionPane.showMessageDialog(frame, "바코드 생성 오류: " + ex.getMessage());
            }
        });

        printButton.addActionListener(e -> {
            Icon icon = barcodeLabel.getIcon();
            if (icon == null) {
                JOptionPane.showMessageDialog(frame, "인쇄할 바코드가 없습니다!");
                return;
            }
        
            String text = inputField.getText();
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
        
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(frame, "인쇄 실패: " + ex.getMessage());
                }
            }
        });

        // 설치된 현재 버전
        String installedVersion = "v1.2";

        // "업데이트 확인..." 클릭 시 동작
        checkUpdateItem.addActionListener(e -> {
            String apiUrl = "https://api.github.com/repos/Life-Helper/cbarcode/releases/latest";
            
            try {
                // GitHub API 호출
                String latestVersion = getLatestVersionFromGitHub(apiUrl);
                
                if (latestVersion.equals(installedVersion)) {
                    // 최신 버전과 동일
                    JOptionPane.showMessageDialog(frame, "현재 사용 할 수 있는 최신버전이 없습니다.", "업데이트 확인", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // 새로운 버전이 존재
                    int response = JOptionPane.showConfirmDialog(frame, 
                        "최신 버전(" + latestVersion + ")이 존재합니다. 업데이트 하시겠습니까?", 
                        "업데이트 확인", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (response == JOptionPane.YES_OPTION) {
                        // 업데이트 다운로드 및 실행
                        downloadAndUpdate(latestVersion);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "업데이트 확인 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static BufferedImage generateBarcodeImage(String text, boolean save) throws WriterException, IOException {
        // 바코드 생성
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.CODE_128, 400, 150);

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
            // Get the Documents folder path
            String documentsPath = System.getProperty("user.home") + "\\Documents";
            File outputDir = new File(documentsPath, "CBarcode");

            // Ensure the "create-barcode" directory exists
            if (!outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    throw new IOException("디렉토리를 만들지 못했습니다: " + outputDir.getAbsolutePath());
                }
            }

            // Save the barcode image as a JPG file in the create-barcode folder
            File outputFile = new File(outputDir, text + ".jpg");
            ImageIO.write(combinedImage, "jpg", outputFile);
        }

        return combinedImage; // 바코드 이미지 아래에 텍스트가 포함된 이미지 반환
    }

    // GitHub API 호출하여 최신 버전 가져오기
private static String getLatestVersionFromGitHub(String apiUrl) throws IOException {
    // API 호출
    URL url = new URL(apiUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    
    // 응답 읽기
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        
        // JSON 응답에서 최신 버전 추출
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("tag_name"); // 릴리스의 "tag_name"을 버전으로 가정
    }
}

// 업데이트 다운로드 및 실행
private static void downloadAndUpdate(String latestVersion) {
    try {
        // 다운로드 링크 설정 (예: GitHub 릴리스 실행 파일 URL)
        String downloadUrl = "https://github.com/{username}/{repository}/releases/download/" + latestVersion + "/CBarcodeSetup.exe";
        String savePath = System.getProperty("user.home") + "\\Downloads\\CBarcodeSetup.exe";

        // 파일 다운로드
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }

        // 다운로드 완료 메시지
        JOptionPane.showMessageDialog(null, "업데이트 파일이 다운로드되었습니다:\n" + savePath, "업데이트 완료", JOptionPane.INFORMATION_MESSAGE);

        // 업데이트 실행
        Runtime.getRuntime().exec(savePath);

    } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "업데이트 다운로드 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
    }
}
}