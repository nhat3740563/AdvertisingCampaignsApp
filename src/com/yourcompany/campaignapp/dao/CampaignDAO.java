// src/main/java/com/yourcompany/campaignapp/dao/CampaignDAO.java
package com.yourcompany.campaignapp.dao;

import com.yourcompany.campaignapp.model.Campaign; // Đảm bảo bạn có lớp Campaign trong gói model
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal; // Import thêm BigDecimal

public class CampaignDAO implements GenericDAO<Campaign> {

    @Override
    public Campaign create(Campaign campaign) throws SQLException {
        String sql = "INSERT INTO campaigns (campaign_name, start_date, end_date, budget, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, campaign.getCampaignName());
            pstmt.setDate(2, Date.valueOf(campaign.getStartDate()));
            pstmt.setDate(3, campaign.getEndDate() != null ? Date.valueOf(campaign.getEndDate()) : null);
            pstmt.setBigDecimal(4, campaign.getBudget());
            pstmt.setString(5, campaign.getStatus());
            pstmt.setInt(6, campaign.getUserId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        campaign.setCampaignId(generatedKeys.getInt(1));
                        return campaign;
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public Campaign read(int id) throws SQLException {
        String sql = "SELECT campaign_id, campaign_name, start_date, end_date, budget, status, user_id FROM campaigns WHERE campaign_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCampaignFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public boolean update(Campaign campaign) throws SQLException {
        String sql = "UPDATE campaigns SET campaign_name = ?, start_date = ?, end_date = ?, budget = ?, status = ?, user_id = ? WHERE campaign_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, campaign.getCampaignName());
            pstmt.setDate(2, Date.valueOf(campaign.getStartDate()));
            pstmt.setDate(3, campaign.getEndDate() != null ? Date.valueOf(campaign.getEndDate()) : null);
            pstmt.setBigDecimal(4, campaign.getBudget());
            pstmt.setString(5, campaign.getStatus());
            pstmt.setInt(6, campaign.getUserId());
            pstmt.setInt(7, campaign.getCampaignId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM campaigns WHERE campaign_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public List<Campaign> readAll() throws SQLException {
        List<Campaign> campaigns = new ArrayList<>();
        String sql = "SELECT campaign_id, campaign_name, start_date, end_date, budget, status, user_id FROM campaigns";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                campaigns.add(extractCampaignFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw e;
        }
        return campaigns;
    }

    private Campaign extractCampaignFromResultSet(ResultSet rs) throws SQLException {
        Campaign campaign = new Campaign();
        campaign.setCampaignId(rs.getInt("campaign_id"));
        campaign.setCampaignName(rs.getString("campaign_name"));
        campaign.setStartDate(rs.getDate("start_date").toLocalDate());

        Date endDateSql = rs.getDate("end_date");
        campaign.setEndDate(endDateSql != null ? endDateSql.toLocalDate() : null);

        campaign.setBudget(rs.getBigDecimal("budget"));
        campaign.setStatus(rs.getString("status"));
        campaign.setUserId(rs.getInt("user_id"));
        return campaign;
    }

    public static void main(String[] args) {
        CampaignDAO campaignDAO = new CampaignDAO();
        try {
            // Đảm bảo user_id=1 (hoặc một user_id hợp lệ khác) tồn tại trong DB của bạn
            int existingUserId = 1;

            // --- TEST CREATE ---
            System.out.println("--- Test Create Campaign ---");
            Campaign newCampaign = new Campaign("Spring Collection 2025", LocalDate.of(2025, 3, 1),
                                               LocalDate.of(2025, 5, 31), new BigDecimal("50000.00"),
                                               "draft", existingUserId);
            Campaign createdCampaign = campaignDAO.create(newCampaign);
            if (createdCampaign != null) {
                System.out.println("Created campaign: " + createdCampaign);
            }

            // --- TEST READ ---
            System.out.println("\n--- Test Read Campaign ---");
            if (createdCampaign != null) {
                Campaign readCampaign = campaignDAO.read(createdCampaign.getCampaignId());
                if (readCampaign != null) {
                    System.out.println("Read campaign: " + readCampaign);
                }
            }

            // --- TEST UPDATE ---
            System.out.println("\n--- Test Update Campaign ---");
            if (createdCampaign != null) {
                createdCampaign.setStatus("active");
                createdCampaign.setBudget(new BigDecimal("55000.00"));
                boolean updated = campaignDAO.update(createdCampaign);
                System.out.println("Campaign update successful? " + updated);
                if (updated) {
                    Campaign updatedCampaign = campaignDAO.read(createdCampaign.getCampaignId());
                    System.out.println("Campaign after update: " + updatedCampaign);
                }
            }

            // --- TEST READ ALL ---
            System.out.println("\n--- Test Read All Campaigns ---");
            List<Campaign> allCampaigns = campaignDAO.readAll();
            System.out.println("All campaigns:");
            for (Campaign campaign : allCampaigns) {
                System.out.println("- " + campaign);
            }

            // --- TEST DELETE ---
            System.out.println("\n--- Test Delete Campaign ---");
            if (createdCampaign != null) {
                boolean deleted = campaignDAO.delete(createdCampaign.getCampaignId());
                System.out.println("Campaign deletion successful? " + deleted);
                if (deleted) {
                    Campaign deletedCampaign = campaignDAO.read(createdCampaign.getCampaignId());
                    System.out.println("Campaign after deletion (should be null): " + deletedCampaign);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}