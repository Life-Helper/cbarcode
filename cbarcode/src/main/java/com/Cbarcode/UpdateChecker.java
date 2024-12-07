package com.Cbarcode;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

public class UpdateChecker {

    public static void checkForUpdates(JFrame frame, String installedVersion) {
        JDialog progressDialog = createProgressDialog(frame, "업데이트 확인 중...");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String apiUrl = "https://api.github.com/repos/Life-Helper/cbarcode/releases/latest";
                try {
                    String latestVersion = getLatestVersion(apiUrl);

                    System.out.println("latestVersion: " + latestVersion);
                    System.out.println("installedVersion: " + installedVersion);

                    String[] removeLtsStr = latestVersion.substring(1).split("\\.");
                    String[] removeToInstallStr = installedVersion.substring(1).split("\\.");

                    for (int i = 0; i < Math.min(removeLtsStr.length, removeToInstallStr.length); i++) {
                        int latestPart = Integer.parseInt(removeLtsStr[i]);
                        int installedPart = Integer.parseInt(removeToInstallStr[i]);

                        if (latestPart > installedPart) {
                            // 새로운 버전이 존재
                            int response = JOptionPane.showConfirmDialog(frame,
                                    "최신 버전(" + latestVersion + ")이 존재합니다. 업데이트 하시겠습니까?",
                                    "업데이트 확인",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);

                            if (response == JOptionPane.YES_OPTION) {
                                openLatestReleasePage(frame, apiUrl);
                            }
                            return null;
                        }
                    }

                    String message = String.format(
                            "현재 사용 할 수 있는 최신버전이 없습니다.\n" +
                                    "최신버전: %s\n" +
                                    "설치버전: %s",
                            latestVersion, installedVersion
                    );
                    JOptionPane.showMessageDialog(frame, message, "업데이트 확인", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "업데이트 확인 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }

            @Override
            protected void done() {
                progressDialog.dispose(); // 작업 완료 후 프로그래스 다이얼로그 닫기
            }
        };

        worker.execute();
        progressDialog.setVisible(true); // 작업 진행 중 다이얼로그 표시
    }

    private static JDialog createProgressDialog(JFrame parent, String message) {
        JDialog dialog = new JDialog(parent, "처리 중...", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 100);
        dialog.setLocationRelativeTo(parent);

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // 진행 상태를 알 수 없는 작업에 사용
        dialog.add(label, BorderLayout.CENTER);
        dialog.add(progressBar, BorderLayout.SOUTH);

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 닫기 버튼 비활성화
        return dialog;
    }

    private static String getLatestVersion(String apiUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();

        return new JSONObject(response.toString()).getString("tag_name");
    }

    private static void openLatestReleasePage(JFrame frame, String apiUrl) {
        try {
            String latestVersion = getLatestVersion(apiUrl);
            String releasePageUrl = "https://github.com/Life-Helper/cbarcode/releases/tag/" + latestVersion;
            Desktop.getDesktop().browse(new URI(releasePageUrl)); // 기본 웹 브라우저로 열기
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "업데이트 페이지를 열 수 없습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
