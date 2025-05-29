package com.yourcompany.campaignapp.app;

import com.yourcompany.campaignapp.model.AdChannel;
import com.yourcompany.campaignapp.model.AdCreative;
import com.yourcompany.campaignapp.model.Campaign;
import com.yourcompany.campaignapp.model.User;
import com.yourcompany.campaignapp.service.AdChannelService;
import com.yourcompany.campaignapp.service.AdCreativeService;
import com.yourcompany.campaignapp.service.CampaignChannelLinkService;
import com.yourcompany.campaignapp.service.CampaignService;
import com.yourcompany.campaignapp.service.UserService;
import com.yourcompany.campaignapp.util.DatabaseConnection; // Để đóng kết nối khi thoát
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class CampaignAppConsole {
    private UserService userService = new UserService();
    private CampaignService campaignService = new CampaignService();
    private AdCreativeService adCreativeService = new AdCreativeService();
    private AdChannelService adChannelService = new AdChannelService();
    private CampaignChannelLinkService campaignChannelLinkService = new CampaignChannelLinkService();

    private Scanner scanner = new Scanner(System.in);
    private User currentUser = null; // Người dùng hiện đang đăng nhập

    public static void main(String[] args) {
        CampaignAppConsole app = new CampaignAppConsole();
        app.run();
    }

    public void run() {
        System.out.println("Chào mừng đến với Ứng dụng Quản lý Chiến dịch Quảng cáo!");

        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n--- Menu Xác thực ---");
        System.out.println("1. Đăng nhập");
        System.out.println("2. Đăng ký");
        System.out.println("3. Thoát");
        System.out.print("Chọn chức năng: ");
        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    System.out.println("Cảm ơn bạn đã sử dụng ứng dụng. Tạm biệt!");
                    DatabaseConnection.closeConnection(); // Đóng kết nối DB khi thoát
                    System.exit(0);
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Lỗi đầu vào: " + e.getMessage());
        }
    }

    private void login() throws SQLException {
        System.out.print("Nhập tên đăng nhập: ");
        String username = scanner.nextLine();
        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();

        currentUser = userService.login(username, password);
        if (currentUser != null) {
            System.out.println("Đăng nhập thành công! Chào mừng, " + currentUser.getUsername() + " (" + currentUser.getRole() + ").");
        } else {
            System.out.println("Tên đăng nhập hoặc mật khẩu không đúng.");
        }
    }

    private void register() throws SQLException {
        System.out.println("--- Đăng ký người dùng mới ---");
        System.out.print("Nhập tên đăng nhập: ");
        String username = scanner.nextLine();
        System.out.print("Nhập mật khẩu: ");
        String password = scanner.nextLine();
        System.out.print("Nhập email: ");
        String email = scanner.nextLine();
        // Mặc định role là 'user' khi đăng ký từ console
        String role = "user";

        User newUser = userService.registerUser(username, password, email, role);
        if (newUser != null) {
            System.out.println("Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } else {
            System.out.println("Đăng ký thất bại. Tên đăng nhập có thể đã tồn tại.");
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Menu Chính ---");
        System.out.println("Xin chào, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        System.out.println("1. Quản lý Người dùng");
        System.out.println("2. Quản lý Chiến dịch");
        System.out.println("3. Quản lý Tài liệu Quảng cáo");
        System.out.println("4. Quản lý Kênh Quảng cáo");
        System.out.println("5. Liên kết Chiến dịch & Kênh");
        System.out.println("6. Đăng xuất");
        System.out.println("7. Thoát ứng dụng");
        System.out.print("Chọn chức năng: ");
        String choice = scanner.nextLine();

        try {
            switch (choice) {
                case "1":
                    if ("admin".equals(currentUser.getRole())) {
                        manageUsers();
                    } else {
                        System.out.println("Bạn không có quyền truy cập chức năng này.");
                    }
                    break;
                case "2":
                    manageCampaigns();
                    break;
                case "3":
                    manageAdCreatives();
                    break;
                case "4":
                    manageAdChannels();
                    break;
                case "5":
                    manageCampaignChannelLinks();
                    break;
                case "6":
                    currentUser = null;
                    System.out.println("Bạn đã đăng xuất.");
                    break;
                case "7":
                    System.out.println("Cảm ơn bạn đã sử dụng ứng dụng. Tạm biệt!");
                    DatabaseConnection.closeConnection();
                    System.exit(0);
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi cơ sở dữ liệu: " + e.getMessage());
        } catch (IllegalArgumentException | DateTimeParseException e) {
            System.err.println("Lỗi đầu vào: " + e.getMessage());
        }
    }

    // --- Quản lý Người dùng ---
    private void manageUsers() throws SQLException {
        while (true) {
            System.out.println("\n--- Quản lý Người dùng ---");
            System.out.println("1. Xem tất cả người dùng");
            System.out.println("2. Xem người dùng theo ID");
            System.out.println("3. Cập nhật người dùng");
            System.out.println("4. Xóa người dùng");
            System.out.println("5. Quay lại Menu Chính");
            System.out.print("Chọn chức năng: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    listAllUsers();
                    break;
                case "2":
                    viewUserById();
                    break;
                case "3":
                    updateUser();
                    break;
                case "4":
                    deleteUser();
                    break;
                case "5":
                    return; // Quay lại menu chính
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }

    private void listAllUsers() throws SQLException {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Không có người dùng nào.");
        } else {
            System.out.println("\n--- Danh sách Người dùng ---");
            users.forEach(System.out::println);
        }
    }

    private void viewUserById() throws SQLException {
        System.out.print("Nhập User ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        User user = userService.getUserById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("Không tìm thấy người dùng với ID " + id);
        }
    }

    private void updateUser() throws SQLException {
        System.out.print("Nhập User ID của người dùng cần cập nhật: ");
        int id = Integer.parseInt(scanner.nextLine());
        User userToUpdate = userService.getUserById(id);

        if (userToUpdate == null) {
            System.out.println("Không tìm thấy người dùng với ID " + id);
            return;
        }

        System.out.println("Cập nhật thông tin cho người dùng: " + userToUpdate.getUsername());
        System.out.print("Nhập tên đăng nhập mới (Enter để giữ nguyên '" + userToUpdate.getUsername() + "'): ");
        String newUsername = scanner.nextLine();
        if (!newUsername.isEmpty()) {
            userToUpdate.setUsername(newUsername);
        }

        System.out.print("Nhập email mới (Enter để giữ nguyên '" + userToUpdate.getEmail() + "'): ");
        String newEmail = scanner.nextLine();
        if (!newEmail.isEmpty()) {
            userToUpdate.setEmail(newEmail);
        }

        System.out.print("Nhập role mới (user/admin/editor) (Enter để giữ nguyên '" + userToUpdate.getRole() + "'): ");
        String newRole = scanner.nextLine();
        if (!newRole.isEmpty()) {
            userToUpdate.setRole(newRole);
        }

        // Cập nhật mật khẩu riêng
        System.out.print("Bạn có muốn cập nhật mật khẩu không? (y/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            System.out.print("Nhập mật khẩu mới: ");
            String newPassword = scanner.nextLine();
            if (userService.updatePassword(id, newPassword)) {
                System.out.println("Mật khẩu đã được cập nhật thành công.");
            } else {
                System.out.println("Cập nhật mật khẩu thất bại.");
            }
        }

        if (userService.updateUser(userToUpdate)) {
            System.out.println("Người dùng đã được cập nhật thành công.");
        } else {
            System.out.println("Cập nhật người dùng thất bại.");
        }
    }

    private void deleteUser() throws SQLException {
        System.out.print("Nhập User ID của người dùng cần xóa: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (userService.deleteUser(id)) {
            System.out.println("Người dùng đã được xóa thành công.");
        } else {
            System.out.println("Xóa người dùng thất bại hoặc không tìm thấy người dùng.");
        }
    }

    // --- Quản lý Chiến dịch ---
    private void manageCampaigns() throws SQLException {
        while (true) {
            System.out.println("\n--- Quản lý Chiến dịch ---");
            System.out.println("1. Tạo chiến dịch mới");
            System.out.println("2. Xem tất cả chiến dịch");
            System.out.println("3. Xem chiến dịch theo ID");
            System.out.println("4. Cập nhật chiến dịch");
            System.out.println("5. Xóa chiến dịch");
            System.out.println("6. Quay lại Menu Chính");
            System.out.print("Chọn chức năng: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createCampaign();
                    break;
                case "2":
                    listAllCampaigns();
                    break;
                case "3":
                    viewCampaignById();
                    break;
                case "4":
                    updateCampaign();
                    break;
                case "5":
                    deleteCampaign();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }

    private void createCampaign() throws SQLException {
        System.out.println("--- Tạo Chiến dịch mới ---");
        System.out.print("Nhập tên chiến dịch: ");
        String name = scanner.nextLine();
        System.out.print("Nhập ngày bắt đầu (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Nhập ngày kết thúc (YYYY-MM-DD, bỏ trống nếu không có): ");
        String endDateStr = scanner.nextLine();
        LocalDate endDate = endDateStr.isEmpty() ? null : LocalDate.parse(endDateStr);
        System.out.print("Nhập ngân sách: ");
        BigDecimal budget = new BigDecimal(scanner.nextLine());
        System.out.print("Nhập trạng thái (draft/active/completed/paused): ");
        String status = scanner.nextLine();

        // Tự động gán userId của người đang đăng nhập
        Campaign newCampaign = campaignService.createCampaign(name, startDate, endDate, budget, status, currentUser.getUserId());
        if (newCampaign != null) {
            System.out.println("Chiến dịch đã được tạo: " + newCampaign);
        } else {
            System.out.println("Tạo chiến dịch thất bại.");
        }
    }

    private void listAllCampaigns() throws SQLException {
        List<Campaign> campaigns = campaignService.getAllCampaigns();
        if (campaigns.isEmpty()) {
            System.out.println("Không có chiến dịch nào.");
        } else {
            System.out.println("\n--- Danh sách Chiến dịch ---");
            campaigns.forEach(System.out::println);
        }
    }

    private void viewCampaignById() throws SQLException {
        System.out.print("Nhập Campaign ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        Campaign campaign = campaignService.getCampaignById(id);
        if (campaign != null) {
            System.out.println(campaign);
        } else {
            System.out.println("Không tìm thấy chiến dịch với ID " + id);
        }
    }

    private void updateCampaign() throws SQLException {
        System.out.print("Nhập Campaign ID của chiến dịch cần cập nhật: ");
        int id = Integer.parseInt(scanner.nextLine());
        Campaign campaignToUpdate = campaignService.getCampaignById(id);

        if (campaignToUpdate == null) {
            System.out.println("Không tìm thấy chiến dịch với ID " + id);
            return;
        }

        System.out.println("Cập nhật thông tin cho chiến dịch: " + campaignToUpdate.getCampaignName());
        System.out.print("Nhập tên chiến dịch mới (Enter để giữ nguyên '" + campaignToUpdate.getCampaignName() + "'): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            campaignToUpdate.setCampaignName(newName);
        }

        System.out.print("Nhập ngày bắt đầu mới (YYYY-MM-DD, Enter để giữ nguyên '" + campaignToUpdate.getStartDate() + "'): ");
        String newStartDateStr = scanner.nextLine();
        if (!newStartDateStr.isEmpty()) {
            campaignToUpdate.setStartDate(LocalDate.parse(newStartDateStr));
        }

        System.out.print("Nhập ngày kết thúc mới (YYYY-MM-DD, Enter để giữ nguyên '" + (campaignToUpdate.getEndDate() != null ? campaignToUpdate.getEndDate() : "") + "'): ");
        String newEndDateStr = scanner.nextLine();
        if (!newEndDateStr.isEmpty()) {
            campaignToUpdate.setEndDate(LocalDate.parse(newEndDateStr));
        } else if (campaignToUpdate.getEndDate() != null && newEndDateStr.isEmpty()) {
            // Nếu người dùng xóa ngày kết thúc
            campaignToUpdate.setEndDate(null);
        }


        System.out.print("Nhập ngân sách mới (Enter để giữ nguyên '" + campaignToUpdate.getBudget() + "'): ");
        String newBudgetStr = scanner.nextLine();
        if (!newBudgetStr.isEmpty()) {
            campaignToUpdate.setBudget(new BigDecimal(newBudgetStr));
        }

        System.out.print("Nhập trạng thái mới (draft/active/completed/paused, Enter để giữ nguyên '" + campaignToUpdate.getStatus() + "'): ");
        String newStatus = scanner.nextLine();
        if (!newStatus.isEmpty()) {
            campaignToUpdate.setStatus(newStatus);
        }

        if (campaignService.updateCampaign(campaignToUpdate)) {
            System.out.println("Chiến dịch đã được cập nhật thành công.");
        } else {
            System.out.println("Cập nhật chiến dịch thất bại.");
        }
    }

    private void deleteCampaign() throws SQLException {
        System.out.print("Nhập Campaign ID của chiến dịch cần xóa: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (campaignService.deleteCampaign(id)) {
            System.out.println("Chiến dịch đã được xóa thành công.");
        } else {
            System.out.println("Xóa chiến dịch thất bại hoặc không tìm thấy chiến dịch.");
        }
    }

    // --- Quản lý Tài liệu Quảng cáo ---
    private void manageAdCreatives() throws SQLException {
        while (true) {
            System.out.println("\n--- Quản lý Tài liệu Quảng cáo ---");
            System.out.println("1. Tạo tài liệu quảng cáo mới");
            System.out.println("2. Xem tất cả tài liệu quảng cáo");
            System.out.println("3. Xem tài liệu quảng cáo theo ID");
            System.out.println("4. Cập nhật tài liệu quảng cáo");
            System.out.println("5. Xóa tài liệu quảng cáo");
            System.out.println("6. Quay lại Menu Chính");
            System.out.print("Chọn chức năng: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createAdCreative();
                    break;
                case "2":
                    listAllAdCreatives();
                    break;
                case "3":
                    viewAdCreativeById();
                    break;
                case "4":
                    updateAdCreative();
                    break;
                case "5":
                    deleteAdCreative();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }

    private void createAdCreative() throws SQLException {
        System.out.println("--- Tạo Tài liệu Quảng cáo mới ---");
        System.out.print("Nhập tên tài liệu: ");
        String name = scanner.nextLine();
        System.out.print("Nhập loại (image/video/text): ");
        String type = scanner.nextLine();
        System.out.print("Nhập đường dẫn file: ");
        String filePath = scanner.nextLine();
        System.out.print("Nhập Campaign ID liên kết: ");
        int campaignId = Integer.parseInt(scanner.nextLine());

        AdCreative newCreative = adCreativeService.createAdCreative(name, type, filePath, campaignId);
        if (newCreative != null) {
            System.out.println("Tài liệu quảng cáo đã được tạo: " + newCreative);
        } else {
            System.out.println("Tạo tài liệu quảng cáo thất bại.");
        }
    }

    private void listAllAdCreatives() throws SQLException {
        List<AdCreative> creatives = adCreativeService.getAllAdCreatives();
        if (creatives.isEmpty()) {
            System.out.println("Không có tài liệu quảng cáo nào.");
        } else {
            System.out.println("\n--- Danh sách Tài liệu Quảng cáo ---");
            creatives.forEach(System.out::println);
        }
    }

    private void viewAdCreativeById() throws SQLException {
        System.out.print("Nhập Creative ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        AdCreative creative = adCreativeService.getAdCreativeById(id);
        if (creative != null) {
            System.out.println(creative);
        } else {
            System.out.println("Không tìm thấy tài liệu quảng cáo với ID " + id);
        }
    }

    private void updateAdCreative() throws SQLException {
        System.out.print("Nhập Creative ID của tài liệu quảng cáo cần cập nhật: ");
        int id = Integer.parseInt(scanner.nextLine());
        AdCreative creativeToUpdate = adCreativeService.getAdCreativeById(id);

        if (creativeToUpdate == null) {
            System.out.println("Không tìm thấy tài liệu quảng cáo với ID " + id);
            return;
        }

        System.out.println("Cập nhật thông tin cho tài liệu quảng cáo: " + creativeToUpdate.getCreativeName());
        System.out.print("Nhập tên tài liệu mới (Enter để giữ nguyên '" + creativeToUpdate.getCreativeName() + "'): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            creativeToUpdate.setCreativeName(newName);
        }

        System.out.print("Nhập loại mới (Enter để giữ nguyên '" + creativeToUpdate.getCreativeType() + "'): ");
        String newType = scanner.nextLine();
        if (!newType.isEmpty()) {
            creativeToUpdate.setCreativeType(newType);
        }

        System.out.print("Nhập đường dẫn file mới (Enter để giữ nguyên '" + creativeToUpdate.getFilePath() + "'): ");
        String newFilePath = scanner.nextLine();
        if (!newFilePath.isEmpty()) {
            creativeToUpdate.setFilePath(newFilePath);
        }

        System.out.print("Nhập Campaign ID mới (Enter để giữ nguyên '" + creativeToUpdate.getCampaignId() + "'): ");
        String newCampaignIdStr = scanner.nextLine();
        if (!newCampaignIdStr.isEmpty()) {
            int newCampaignId = Integer.parseInt(newCampaignIdStr);
            if (campaignService.getCampaignById(newCampaignId) != null) { // Kiểm tra campaign có tồn tại không
                creativeToUpdate.setCampaignId(newCampaignId);
            } else {
                System.out.println("Campaign ID không hợp lệ, giữ nguyên Campaign ID cũ.");
            }
        }

        if (adCreativeService.updateAdCreative(creativeToUpdate)) {
            System.out.println("Tài liệu quảng cáo đã được cập nhật thành công.");
        } else {
            System.out.println("Cập nhật tài liệu quảng cáo thất bại.");
        }
    }

    private void deleteAdCreative() throws SQLException {
        System.out.print("Nhập Creative ID của tài liệu quảng cáo cần xóa: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (adCreativeService.deleteAdCreative(id)) {
            System.out.println("Tài liệu quảng cáo đã được xóa thành công.");
        } else {
            System.out.println("Xóa tài liệu quảng cáo thất bại hoặc không tìm thấy.");
        }
    }

    // --- Quản lý Kênh Quảng cáo ---
    private void manageAdChannels() throws SQLException {
        while (true) {
            System.out.println("\n--- Quản lý Kênh Quảng cáo ---");
            System.out.println("1. Tạo kênh mới");
            System.out.println("2. Xem tất cả kênh");
            System.out.println("3. Xem kênh theo ID");
            System.out.println("4. Cập nhật kênh");
            System.out.println("5. Xóa kênh");
            System.out.println("6. Quay lại Menu Chính");
            System.out.print("Chọn chức năng: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createAdChannel();
                    break;
                case "2":
                    listAllAdChannels();
                    break;
                case "3":
                    viewAdChannelById();
                    break;
                case "4":
                    updateAdChannel();
                    break;
                case "5":
                    deleteAdChannel();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }

    private void createAdChannel() throws SQLException {
        System.out.println("--- Tạo Kênh Quảng cáo mới ---");
        System.out.print("Nhập tên kênh: ");
        String name = scanner.nextLine();
        AdChannel newChannel = adChannelService.createAdChannel(name);
        if (newChannel != null) {
            System.out.println("Kênh quảng cáo đã được tạo: " + newChannel);
        } else {
            System.out.println("Tạo kênh quảng cáo thất bại.");
        }
    }

    private void listAllAdChannels() throws SQLException {
        List<AdChannel> channels = adChannelService.getAllAdChannels();
        if (channels.isEmpty()) {
            System.out.println("Không có kênh quảng cáo nào.");
        } else {
            System.out.println("\n--- Danh sách Kênh Quảng cáo ---");
            channels.forEach(System.out::println);
        }
    }

    private void viewAdChannelById() throws SQLException {
        System.out.print("Nhập Channel ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        AdChannel channel = adChannelService.getAdChannelById(id);
        if (channel != null) {
            System.out.println(channel);
        } else {
            System.out.println("Không tìm thấy kênh quảng cáo với ID " + id);
        }
    }

    private void updateAdChannel() throws SQLException {
        System.out.print("Nhập Channel ID của kênh cần cập nhật: ");
        int id = Integer.parseInt(scanner.nextLine());
        AdChannel channelToUpdate = adChannelService.getAdChannelById(id);

        if (channelToUpdate == null) {
            System.out.println("Không tìm thấy kênh quảng cáo với ID " + id);
            return;
        }

        System.out.println("Cập nhật thông tin cho kênh: " + channelToUpdate.getChannelName());
        System.out.print("Nhập tên kênh mới (Enter để giữ nguyên '" + channelToUpdate.getChannelName() + "'): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            channelToUpdate.setChannelName(newName);
        }

        if (adChannelService.updateAdChannel(channelToUpdate)) {
            System.out.println("Kênh quảng cáo đã được cập nhật thành công.");
        } else {
            System.out.println("Cập nhật kênh quảng cáo thất bại.");
        }
    }

    private void deleteAdChannel() throws SQLException {
        System.out.print("Nhập Channel ID của kênh cần xóa: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (adChannelService.deleteAdChannel(id)) {
            System.out.println("Kênh quảng cáo đã được xóa thành công.");
        } else {
            System.out.println("Xóa kênh quảng cáo thất bại hoặc không tìm thấy.");
        }
    }

    // --- Quản lý Liên kết Chiến dịch & Kênh ---
    private void manageCampaignChannelLinks() throws SQLException {
        while (true) {
            System.out.println("\n--- Quản lý Liên kết Chiến dịch & Kênh ---");
            System.out.println("1. Tạo liên kết mới");
            System.out.println("2. Xem tất cả liên kết");
            System.out.println("3. Xóa liên kết");
            System.out.println("4. Quay lại Menu Chính");
            System.out.print("Chọn chức năng: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createCampaignChannelLink();
                    break;
                case "2":
                    listAllCampaignChannelLinks();
                    break;
                case "3":
                    deleteCampaignChannelLink();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }
        }
    }

    private void createCampaignChannelLink() throws SQLException {
        System.out.println("--- Tạo Liên kết mới ---");
        System.out.print("Nhập Campaign ID: ");
        int campaignId = Integer.parseInt(scanner.nextLine());
        System.out.print("Nhập Channel ID: ");
        int channelId = Integer.parseInt(scanner.nextLine());

        Campaign newCampaign = campaignService.getCampaignById(campaignId);
        AdChannel newChannel = adChannelService.getAdChannelById(channelId);

        if (newCampaign == null) {
            System.out.println("Campaign với ID " + campaignId + " không tồn tại. Vui lòng tạo Campaign trước.");
            return;
        }
        if (newChannel == null) {
            System.out.println("Channel với ID " + channelId + " không tồn tại. Vui lòng tạo Channel trước.");
            return;
        }

        campaignChannelLinkService.createLink(campaignId, channelId);
        System.out.println("Liên kết đã được tạo giữa Campaign " + campaignId + " và Channel " + channelId);
    }

    private void listAllCampaignChannelLinks() throws SQLException {
        List<com.yourcompany.campaignapp.model.CampaignChannelLink> links = campaignChannelLinkService.getAllLinks();
        if (links.isEmpty()) {
            System.out.println("Không có liên kết nào.");
        } else {
            System.out.println("\n--- Danh sách Liên kết Chiến dịch & Kênh ---");
            links.forEach(System.out::println);
        }
    }

    private void deleteCampaignChannelLink() throws SQLException {
        System.out.print("Nhập Campaign ID của liên kết cần xóa: ");
        int campaignId = Integer.parseInt(scanner.nextLine());
        System.out.print("Nhập Channel ID của liên kết cần xóa: ");
        int channelId = Integer.parseInt(scanner.nextLine());

        if (campaignChannelLinkService.deleteLink(campaignId, channelId)) {
            System.out.println("Liên kết đã được xóa thành công.");
        } else {
            System.out.println("Xóa liên kết thất bại hoặc không tìm thấy.");
        }
    }
}