package com.yourcompany.campaignapp.gui;

import javax.swing.SwingUtilities;
import com.yourcompany.campaignapp.service.CampaignService;
import com.yourcompany.campaignapp.util.DatabaseConnection; // Để đóng kết nối
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import java.math.BigDecimal; // Thêm import này

public class DashboardFrame extends JFrame {

    private CampaignService campaignService;

    public DashboardFrame() {
        super("Bảng điều khiển Chiến dịch Quảng cáo");
        this.campaignService = new CampaignService(); // Khởi tạo CampaignService

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Tạo JTabbedPane để chứa các loại biểu đồ khác nhau
        JTabbedPane tabbedPane = new JTabbedPane();

        try {
            // 1. Biểu đồ tròn (Pie Chart) - Số lượng chiến dịch theo trạng thái
            Map<String, Long> campaignStatusData = campaignService.getCampaignCountByStatus();
            JFreeChart pieChart = createCampaignStatusPieChart(campaignStatusData);
            ChartPanel pieChartPanel = new ChartPanel(pieChart);
            tabbedPane.addTab("Chiến dịch theo Trạng thái (Pie)", pieChartPanel);

            // 2. Biểu đồ cột (Bar Chart) - Có thể hiển thị tổng ngân sách theo trạng thái (nếu muốn phức tạp hơn)
            // Hoặc đơn giản là số lượng chiến dịch theo trạng thái dưới dạng cột
            JFreeChart barChart = createCampaignStatusBarChart(campaignStatusData);
            ChartPanel barChartPanel = new ChartPanel(barChart);
            tabbedPane.addTab("Chiến dịch theo Trạng thái (Bar)", barChartPanel);

            add(tabbedPane, BorderLayout.CENTER);

            // Thêm một label hiển thị tổng ngân sách
            BigDecimal totalBudget = campaignService.getTotalCampaignBudget();
            JLabel totalBudgetLabel = new JLabel("<html><b>Tổng Ngân sách Chiến dịch:</b> " + totalBudget + " VNĐ</html>", SwingConstants.CENTER);
            totalBudgetLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(totalBudgetLabel, BorderLayout.NORTH);


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu biểu đồ: " + e.getMessage(), "Lỗi Cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Đóng kết nối DB khi cửa sổ chính đóng
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    DatabaseConnection.closeConnection();
                }
            });
        }
    }

    private JFreeChart createCampaignStatusPieChart(Map<String, Long> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            dataset.setValue(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
        }

        return ChartFactory.createPieChart(
                "Phân bổ Chiến dịch theo Trạng thái", // Tiêu đề biểu đồ
                dataset,
                true, // Hiển thị legend
                true, // Hiển thị tooltips
                false // Không hiển thị URLs
        );
    }

    private JFreeChart createCampaignStatusBarChart(Map<String, Long> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Số lượng Chiến dịch", entry.getKey());
        }

        return ChartFactory.createBarChart(
                "Số lượng Chiến dịch theo Trạng thái", // Tiêu đề biểu đồ
                "Trạng thái", // Nhãn trục X
                "Số lượng", // Nhãn trục Y
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // Hướng biểu đồ
                true, // Hiển thị legend
                true, // Hiển thị tooltips
                false // Không hiển thị URLs
        );
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame ex = new DashboardFrame();
            ex.setVisible(true);
        });
    }
}
