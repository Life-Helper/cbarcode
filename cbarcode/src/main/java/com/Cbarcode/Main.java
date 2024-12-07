package com.Cbarcode;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.jar.Manifest;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("CBarcode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // 메뉴바 및 메뉴 설정
        JMenuBar menuBar = createMenuBar(frame);
        frame.setJMenuBar(menuBar);

        // 메인 UI 구성
        JPanel panel = createMainPanel(frame);
        frame.add(panel);

        frame.setVisible(true);
    }

    private static JMenuBar createMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        // 파일 메뉴
        JMenu fileMenu = new JMenu("파일");
        JMenuItem settingItem = new JMenuItem("설정");
        JMenuItem exitItem = new JMenuItem("종료");
        fileMenu.add(settingItem);
        fileMenu.add(exitItem);

        // 편집 메뉴
        JMenu editMenu = new JMenu("편집");
        JMenuItem clearTextItem = new JMenuItem("텍스트 초기화");
        editMenu.add(clearTextItem);

        // 도움말 메뉴
        JMenu helpMenu = new JMenu("도움말");
        JMenuItem aboutItem = new JMenuItem("정보");
        JMenuItem checkUpdateItem = new JMenuItem("업데이트 확인...");
        helpMenu.add(aboutItem);
        helpMenu.add(checkUpdateItem);

        // 메뉴바에 메뉴 추가
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        // 메뉴 동작 정의
        exitItem.addActionListener(e -> System.exit(0));
        clearTextItem.addActionListener(e -> MainPanel.clearInputText());
        aboutItem.addActionListener(e -> showAboutDialog(frame));
        checkUpdateItem.addActionListener(e -> UpdateChecker.checkForUpdates(frame, getVersion()));
        settingItem.addActionListener(e -> {
            try {
                new SettingsDialog(frame).setVisible(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        return menuBar;
    }

    private static JPanel createMainPanel(JFrame frame) {
        return new MainPanel(frame);
    }

    private static void showAboutDialog(JFrame frame) {
        String installedVersion = getVersion();
        
        JOptionPane.showMessageDialog(frame, 
            "Create Barcode\n" +
            "바코드 생성 프로그램\n\n" +
            "버전: " + installedVersion + "\n" +
            "만든이: Shan\n" +
            "위치: CHA1 Sub-FC_C" +
            "\n\n이걸 누른다고?"
        );
    }

    private static String getVersion() {
        return "v1.3";
    }
}
