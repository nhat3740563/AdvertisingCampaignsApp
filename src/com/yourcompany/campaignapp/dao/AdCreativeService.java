// src/main/java/com/yourcompany/campaignapp/service/AdCreativeService.java
package com.yourcompany.campaignapp.service;

import com.yourcompany.campaignapp.dao.AdCreativeDAO;
import com.yourcompany.campaignapp.model.AdCreative;
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.sql.SQLException;
import java.util.List;

public class AdCreativeService {
    private AdCreativeDAO adCreativeDAO;

    public AdCreativeService() {
        this.adCreativeDAO = new AdCreativeDAO();
    }

    public AdCreativeService(AdCreativeDAO adCreativeDAO) {
        this.adCreativeDAO = adCreativeDAO;
    }

    public AdCreative createAdCreative(String creativeName, String creativeType, String filePath, int campaignId) throws SQLException {
        if (creativeName == null || creativeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Creative name cannot be empty.");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty.");
        }
        if (campaignId <= 0) {
            throw new IllegalArgumentException("Invalid Campaign ID.");
        }
        // Thêm logic kiểm tra loại creative hợp lệ nếu cần

        AdCreative creative = new AdCreative(creativeName, creativeType, filePath, campaignId);
        return adCreativeDAO.create(creative);
    }

    public AdCreative getAdCreativeById(int creativeId) throws SQLException {
        return adCreativeDAO.read(creativeId);
    }

    public boolean updateAdCreative(AdCreative creative) throws SQLException {
        if (creative.getCreativeId() <= 0) {
            throw new IllegalArgumentException("Creative ID must be valid for update.");
        }
        if (creative.getCreativeName() == null || creative.getCreativeName().trim().isEmpty()) {
            throw new IllegalArgumentException("Creative name cannot be empty.");
        }
        if (creative.getFilePath() == null || creative.getFilePath().trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty.");
        }
        if (creative.getCampaignId() <= 0) {
            throw new IllegalArgumentException("Invalid Campaign ID.");
        }

        return adCreativeDAO.update(creative);
    }

    public boolean deleteAdCreative(int creativeId) throws SQLException {
        return adCreativeDAO.delete(creativeId);
    }

    public List<AdCreative> getAllAdCreatives() throws SQLException {
        return adCreativeDAO.readAll();
    }

    public static void main(String[] args) {
        AdCreativeService adCreativeService = new AdCreativeService();
        try {
            int existingCampaignId = 1; // Đảm bảo Campaign với ID này tồn tại

            // --- TEST CREATE ---
            System.out.println("--- Test Create AdCreative ---");
            AdCreative createdCreative = adCreativeService.createAdCreative(
                "Service Test Creative", "image", "/images/service_test.jpg", existingCampaignId
            );
            if (createdCreative != null) {
                System.out.println("Created ad creative: " + createdCreative);
            }

            // --- TEST READ ---
            System.out.println("\n--- Test Read AdCreative ---");
            if (createdCreative != null) {
                AdCreative readCreative = adCreativeService.getAdCreativeById(createdCreative.getCreativeId());
                if (readCreative != null) {
                    System.out.println("Read ad creative: " + readCreative);
                }
            }

            // --- TEST UPDATE ---
            System.out.println("\n--- Test Update AdCreative ---");
            if (createdCreative != null) {
                createdCreative.setCreativeName("Updated Service Creative");
                createdCreative.setCreativeType("video");
                createdCreative.setFilePath("/videos/updated_service.mp4");
                boolean updated = adCreativeService.updateAdCreative(createdCreative);
                System.out.println("Ad creative update successful? " + updated);
                if (updated) {
                    System.out.println("Ad creative after service update: " + adCreativeService.getAdCreativeById(createdCreative.getCreativeId()));
                }
            }

            // --- TEST READ ALL ---
            System.out.println("\n--- Test Read All AdCreatives ---");
            List<AdCreative> allCreatives = adCreativeService.getAllAdCreatives();
            System.out.println("All ad creatives in DB:");
            for (AdCreative ac : allCreatives) {
                System.out.println("- " + ac.getCreativeName() + " (" + ac.getCreativeType() + ")");
            }

            // --- TEST DELETE ---
            System.out.println("\n--- Test Delete AdCreative ---");
            if (createdCreative != null) {
                boolean deleted = adCreativeService.deleteAdCreative(createdCreative.getCreativeId());
                System.out.println("Ad creative deletion successful? " + deleted);
                if (deleted) {
                    System.out.println("Ad creative after deletion (should be null): " + adCreativeService.getAdCreativeById(createdCreative.getCreativeId()));
                }
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Business logic error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error during AdCreativeService test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}