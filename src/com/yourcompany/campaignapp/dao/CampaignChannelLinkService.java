// src/main/java/com/yourcompany/campaignapp/service/CampaignChannelLinkService.java
package com.yourcompany.campaignapp.service;

import com.yourcompany.campaignapp.dao.CampaignChannelLinkDAO;
import com.yourcompany.campaignapp.dao.CampaignDAO; // Để kiểm tra tồn tại của Campaign
import com.yourcompany.campaignapp.dao.AdChannelDAO; // Để kiểm tra tồn tại của AdChannel
import com.yourcompany.campaignapp.model.CampaignChannelLink;
import com.yourcompany.campaignapp.util.DatabaseConnection;
import java.sql.SQLException;
import java.util.List;

public class CampaignChannelLinkService {
    private CampaignChannelLinkDAO linkDAO;
    private CampaignDAO campaignDAO; // Để kiểm tra sự tồn tại của campaign
    private AdChannelDAO adChannelDAO; // Để kiểm tra sự tồn tại của channel

    public CampaignChannelLinkService() {
        this.linkDAO = new CampaignChannelLinkDAO();
        this.campaignDAO = new CampaignDAO();
        this.adChannelDAO = new AdChannelDAO();
    }

    public CampaignChannelLinkService(CampaignChannelLinkDAO linkDAO, CampaignDAO campaignDAO, AdChannelDAO adChannelDAO) {
        this.linkDAO = linkDAO;
        this.campaignDAO = campaignDAO;
        this.adChannelDAO = adChannelDAO;
    }

    public CampaignChannelLink createLink(int campaignId, int channelId) throws SQLException {
        if (campaignId <= 0 || channelId <= 0) {
            throw new IllegalArgumentException("Campaign ID and Channel ID must be positive.");
        }
        // Logic nghiệp vụ: Đảm bảo campaign và channel tồn tại trước khi tạo liên kết
        if (campaignDAO.read(campaignId) == null) {
            throw new IllegalArgumentException("Campaign with ID " + campaignId + " does not exist.");
        }
        if (adChannelDAO.read(channelId) == null) {
            throw new IllegalArgumentException("AdChannel with ID " + channelId + " does not exist.");
        }
        // Kiểm tra xem liên kết đã tồn tại chưa
        if (linkDAO.read(campaignId, channelId) != null) {
            System.out.println("Link between Campaign " + campaignId + " and Channel " + channelId + " already exists.");
            return null;
        }

        CampaignChannelLink link = new CampaignChannelLink(campaignId, channelId);
        return linkDAO.create(link);
    }

    public CampaignChannelLink getLink(int campaignId, int channelId) throws SQLException {
        return linkDAO.read(campaignId, channelId);
    }

    public boolean deleteLink(int campaignId, int channelId) throws SQLException {
        return linkDAO.delete(campaignId, channelId);
    }

    public List<CampaignChannelLink> getAllLinks() throws SQLException {
        return linkDAO.readAll();
    }

    public static void main(String[] args) {
        CampaignChannelLinkService linkService = new CampaignChannelLinkService();
        try {
            // Đảm bảo campaign và channel này tồn tại trong DB của bạn
            int campaignIdForTest = 1; // ID của một Campaign hiện có
            int channelIdForTest = 1;  // ID của một AdChannel hiện có

            // Tạo thêm một channel và campaign để kiểm tra thêm
            int campaignIdForTest2 = 2; // ID của một Campaign hiện có
            int channelIdForTest2 = 2; // ID của một AdChannel hiện có

            // Tạo tạm các đối tượng để đảm bảo chúng tồn tại trong DB cho test
            // Trong ứng dụng thực, bạn sẽ có CampaignService và AdChannelService để làm việc này
            CampaignDAO campDao = new CampaignDAO();
            AdChannelDAO adDao = new AdChannelDAO();
            campDao.create(new Campaign("Test Campaign for Link", LocalDate.now(), null, new BigDecimal("100"), "active", 1)); // user_id=1
            adDao.create(new AdChannel("Test Channel for Link"));


            // --- TEST CREATE ---
            System.out.println("--- Test Create CampaignChannelLink ---");
            CampaignChannelLink createdLink = linkService.createLink(campaignIdForTest, channelIdForTest);
            if (createdLink != null) {
                System.out.println("Created link: " + createdLink);
            } else {
                System.out.println("Failed to create link or link already exists.");
            }

            // Thử tạo một link khác
            CampaignChannelLink createdLink2 = linkService.createLink(campaignIdForTest2, channelIdForTest2);
            if (createdLink2 != null) {
                System.out.println("Created second link: " + createdLink2);
            } else {
                System.out.println("Failed to create second link or link already exists.");
            }


            // --- TEST READ ---
            System.out.println("\n--- Test Read CampaignChannelLink ---");
            CampaignChannelLink readLink = linkService.getLink(campaignIdForTest, channelIdForTest);
            if (readLink != null) {
                System.out.println("Read link: " + readLink);
            } else {
                System.out.println("Link not found.");
            }

            // --- TEST READ ALL ---
            System.out.println("\n--- Test Read All CampaignChannelLinks ---");
            List<CampaignChannelLink> allLinks = linkService.getAllLinks();
            System.out.println("All links in DB:");
            for (CampaignChannelLink link : allLinks) {
                System.out.println("- " + link);
            }

            // --- TEST DELETE ---
            System.out.println("\n--- Test Delete CampaignChannelLink ---");
            if (createdLink != null) {
                boolean deleted = linkService.deleteLink(createdLink.getCampaignId(), createdLink.getChannelId());
                System.out.println("Link deletion successful? " + deleted);
                if (deleted) {
                    System.out.println("Link after deletion (should be null): " + linkService.getLink(createdLink.getCampaignId(), createdLink.getChannelId()));
                }
            }
             if (createdLink2 != null) {
                boolean deleted2 = linkService.deleteLink(createdLink2.getCampaignId(), createdLink2.getChannelId());
                System.out.println("Second link deletion successful? " + deleted2);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Business logic error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error during CampaignChannelLinkService test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}