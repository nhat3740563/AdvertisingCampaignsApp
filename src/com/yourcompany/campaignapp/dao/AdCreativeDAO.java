// src/main/java/com/yourcompany/campaignapp/dao/AdCreativeDAO.java
package com.yourcompany.campaignapp.dao;

import com.yourcompany.campaignapp.model.AdCreative; // Đảm bảo bạn có lớp AdCreative trong gói model
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdCreativeDAO implements GenericDAO<AdCreative> {

    @Override
    public AdCreative create(AdCreative creative) throws SQLException {
        String sql = "INSERT INTO ad_creatives (creative_name, creative_type, file_path, campaign_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, creative.getCreativeName());
            pstmt.setString(2, creative.getCreativeType());
            pstmt.setString(3, creative.getFilePath());
            pstmt.setInt(4, creative.getCampaignId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        creative.setCreativeId(generatedKeys.getInt(1));
                        return creative;
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public AdCreative read(int id) throws SQLException {
        String sql = "SELECT creative_id, creative_name, creative_type, file_path, campaign_id FROM ad_creatives WHERE creative_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdCreativeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return null;
    }

    @Override
    public boolean update(AdCreative creative) throws SQLException {
        String sql = "UPDATE ad_creatives SET creative_name = ?, creative_type = ?, file_path = ?, campaign_id = ? WHERE creative_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, creative.getCreativeName());
            pstmt.setString(2, creative.getCreativeType());
            pstmt.setString(3, creative.getFilePath());
            pstmt.setInt(4, creative.getCampaignId());
            pstmt.setInt(5, creative.getCreativeId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM ad_creatives WHERE creative_id = ?";
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
    public List<AdCreative> readAll() throws SQLException {
        List<AdCreative> creatives = new ArrayList<>();
        String sql = "SELECT creative_id, creative_name, creative_type, file_path, campaign_id FROM ad_creatives";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                creatives.add(extractAdCreativeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw e;
        }
        return creatives;
    }

    private AdCreative extractAdCreativeFromResultSet(ResultSet rs) throws SQLException {
        AdCreative creative = new AdCreative();
        creative.setCreativeId(rs.getInt("creative_id"));
        creative.setCreativeName(rs.getString("creative_name"));
        creative.setCreativeType(rs.getString("creative_type"));
        creative.setFilePath(rs.getString("file_path"));
        creative.setCampaignId(rs.getInt("campaign_id"));
        return creative;
    }

    public static void main(String[] args) {
        AdCreativeDAO adCreativeDAO = new AdCreativeDAO();
        try {
            // Đảm bảo campaign_id=1 (hoặc một campaign_id hợp lệ khác) tồn tại trong DB của bạn
            int existingCampaignId = 1;

            // --- TEST CREATE ---
            System.out.println("--- Test Create AdCreative ---");
            AdCreative newCreative = new AdCreative("Spring Promo Image", "image", "/assets/images/spring_promo.jpg", existingCampaignId);
            AdCreative createdCreative = adCreativeDAO.create(newCreative);
            if (createdCreative != null) {
                System.out.println("Created ad creative: " + createdCreative);
            }

            // --- TEST READ ---
            System.out.println("\n--- Test Read AdCreative ---");
            if (createdCreative != null) {
                AdCreative readCreative = adCreativeDAO.read(createdCreative.getCreativeId());
                if (readCreative != null) {
                    System.out.println("Read ad creative: " + readCreative);
                }
            }

            // --- TEST UPDATE ---
            System.out.println("\n--- Test Update AdCreative ---");
            if (createdCreative != null) {
                createdCreative.setCreativeName("Spring Promo Video");
                createdCreative.setCreativeType("video");
                createdCreative.setFilePath("/assets/videos/spring_promo.mp4");
                boolean updated = adCreativeDAO.update(createdCreative);
                System.out.println("Ad creative update successful? " + updated);
                if (updated) {
                    AdCreative updatedCreative = adCreativeDAO.read(createdCreative.getCreativeId());
                    System.out.println("Ad creative after update: " + updatedCreative);
                }
            }

            // --- TEST READ ALL ---
            System.out.println("\n--- Test Read All AdCreatives ---");
            List<AdCreative> allCreatives = adCreativeDAO.readAll();
            System.out.println("All ad creatives:");
            for (AdCreative creative : allCreatives) {
                System.out.println("- " + creative);
            }

            // --- TEST DELETE ---
            System.out.println("\n--- Test Delete AdCreative ---");
            if (createdCreative != null) {
                boolean deleted = adCreativeDAO.delete(createdCreative.getCreativeId());
                System.out.println("Ad creative deletion successful? " + deleted);
                if (deleted) {
                    AdCreative deletedCreative = adCreativeDAO.read(createdCreative.getCreativeId());
                    System.out.println("Ad creative after deletion (should be null): " + deletedCreative);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}