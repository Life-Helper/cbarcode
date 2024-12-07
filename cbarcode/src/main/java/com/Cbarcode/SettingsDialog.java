package com.Cbarcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class SettingsDialog extends JDialog {
    private JTextField filePathField;
    private JButton saveButton;
    private JButton browseButton;

    public SettingsDialog(JFrame parent) throws IOException {
        super(parent, "설정", true);
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(parent);

        filePathField = new JTextField(20);
        saveButton = new JButton("저장");
        browseButton = new JButton("탐색");

        // 기존 경로를 로드하여 텍스트 필드에 표시
        String savedPath = SettingsManager.loadPath();
        filePathField.setText(savedPath);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("저장 경로:"));
        inputPanel.add(filePathField);
        inputPanel.add(browseButton);

        saveButton.addActionListener(this::saveSettings);
        browseButton.addActionListener(this::browseForPath);

        add(inputPanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void saveSettings(ActionEvent e) {
        String newPath = filePathField.getText();
        SettingsManager.savePath(newPath);
        JOptionPane.showMessageDialog(this, "경로가 저장되었습니다!");
        dispose(); // 다이얼로그 닫기
    }

    private void browseForPath(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 디렉터리만 선택하도록 설정
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedPath = fileChooser.getSelectedFile().getAbsolutePath();
            filePathField.setText(selectedPath); // 선택한 경로를 텍스트 필드에 설정
        }
    }
}
