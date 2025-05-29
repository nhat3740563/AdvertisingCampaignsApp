// src/main/java/com/yourcompany/campaignapp/service/CampaignService.java
package com.yourcompany.campaignapp.service;

import com.yourcompany.campaignapp.dao.CampaignDAO;
import com.yourcompany.campaignapp.model.Campaign;
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator; // Import Comparator
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Import Collectors

public class CampaignService {
    private CampaignDAO campaignDAO;

    public CampaignService() {
        this.campaignDAO = new CampaignDAO();
    }

    public CampaignService(CampaignDAO campaignDAO) {
        this.campaignDAO = campaignDAO;
    }

    // ... (Giữ nguyên các phương thức CRUD đã có) ...

    public Campaign createCampaign(String campaignName, LocalDate startDate, LocalDate endDate, BigDecimal budget, String status, int userId) throws SQLException {
        // ... (Logic đã có) ...
        Campaign campaign = new Campaign(campaignName, startDate, endDate, budget, status, userId);
        return campaignDAO.create(campaign);
    }

    public Campaign getCampaignById(int campaignId) throws SQLException {
        return campaignDAO.read(campaignId);
    }

    public boolean updateCampaign(Campaign campaign) throws SQLException {
        // ... (Logic đã có) ...
        return campaignDAO.update(campaign);
    }

    public boolean deleteCampaign(int campaignId) throws SQLException {
        return campaignDAO.delete(campaignId);
    }

    public List<Campaign> getAllCampaigns() throws SQLException {
        return campaignDAO.readAll();
    }

    // --- Phương thức thống kê mới ---

    /**
     * Lấy tổng ngân sách của tất cả các chiến dịch.
     * @return BigDecimal tổng ngân sách.
     * @throws SQLException nếu có lỗi truy vấn DB.
     */
    public BigDecimal getTotalCampaignBudget() throws SQLException {
        return getAllCampaigns().stream()
                .map(Campaign::getBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Lấy số lượng chiến dịch theo trạng thái.
     * @return Map<String, Long> với key là trạng thái, value là số lượng.
     * @throws SQLException nếu có lỗi truy vấn DB.
     */
    public Map<String, Long> getCampaignCountByStatus() throws SQLException {
        return getAllCampaigns().stream()
                .collect(Collectors.groupingBy(Campaign::getStatus, Collectors.counting()));
    }

    /**
     * Lấy danh sách các chiến dịch được sắp xếp.
     * @param sortBy trường cần sắp xếp ("name", "startDate", "budget", "status")
     * @param ascending true nếu sắp xếp tăng dần, false nếu giảm dần
     * @return List<Campaign> đã sắp xếp
     * @throws SQLException nếu có lỗi truy vấn DB.
     * @throws IllegalArgumentException nếu sortBy không hợp lệ.
     */
    public List<Campaign> getSortedCampaigns(String sortBy, boolean ascending) throws SQLException {
        List<Campaign> campaigns = getAllCampaigns();
        Comparator<Campaign> comparator;

        switch (sortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Campaign::getCampaignName);
                break;
            case "startdate":
                comparator = Comparator.comparing(Campaign::getStartDate);
                break;
            case "budget":
                comparator = Comparator.comparing(Campaign::getBudget);
                break;
            case "status":
                comparator = Comparator.comparing(Campaign::getStatus);
                break;
            default:
                throw new IllegalArgumentException("Invalid sort by field: " + sortBy);
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        campaigns.sort(comparator);
        return campaigns;
    }

    // ... (Giữ nguyên main method để kiểm thử) ...
}