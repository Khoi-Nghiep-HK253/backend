package com.hcmut.divvy.config;

import com.hcmut.divvy.entity.*;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpensePayerRepository expensePayerRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final ActivityRepository activityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Reference Currencies & Categories if not present
        Currency vnd = currencyRepository.findByAcronym("VND")
                .orElseGet(() -> currencyRepository.save(Currency.builder().name("Vietnamese Dong").acronym("VND").build()));
        currencyRepository.findByAcronym("USD")
                .orElseGet(() -> currencyRepository.save(Currency.builder().name("US Dollar").acronym("USD").build()));

        Category food = categoryRepository.findByName("Ăn uống")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Ăn uống").icon("food").build()));
        categoryRepository.findByName("Di chuyển")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Di chuyển").icon("transport").build()));
        Category hotel = categoryRepository.findByName("Nhà ở / Khách sạn")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Nhà ở / Khách sạn").icon("hotel").build()));
        categoryRepository.findByName("Giải trí")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Giải trí").icon("entertainment").build()));
        categoryRepository.findByName("Mua sắm")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Mua sắm").icon("shopping").build()));
        categoryRepository.findByName("Khác")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Khác").icon("other").build()));

        if (userRepository.count() > 0) {
            log.info("Database already contains user data. Skipping sample users/groups seeding.");
            return;
        }

        log.info("===============================================================================");
        log.info("Initializing Dev Sample Data via Java DevDataSeeder...");
        log.info("===============================================================================");

        // 2. Seed Users with BCrypt encoded password "123456"
        String defaultPassword = passwordEncoder.encode("123456");

        User hung = userRepository.save(User.builder()
                .username("hungtri")
                .firstname("Hung").lastname("Tri")
                .phone("0901111111").email("hung@example.com")
                .hashPassword(defaultPassword).role("USER").build());

        User khanh = userRepository.save(User.builder()
                .username("khanhnt")
                .firstname("Khanh").lastname("Nguyen")
                .phone("0902222222").email("khanh@example.com")
                .hashPassword(defaultPassword).role("USER").build());

        User an = userRepository.save(User.builder()
                .username("anle")
                .firstname("An").lastname("Le")
                .phone("0903333333").email("an@example.com")
                .hashPassword(defaultPassword).role("USER").build());

        User binh = userRepository.save(User.builder()
                .username("binhpham")
                .firstname("Binh").lastname("Pham")
                .phone("0904444444").email("binh@example.com")
                .hashPassword(defaultPassword).role("USER").build());

        userRepository.save(User.builder()
                .username("adminuser")
                .firstname("Admin").lastname("System")
                .phone("0900000000").email("admin@example.com")
                .hashPassword(defaultPassword).role("ADMIN").build());

        log.info("Seeded 5 sample Users (password: 123456).");

        // 3. Seed Groups
        Group groupDaLat = groupRepository.save(Group.builder()
                .category(hotel)
                .defaultCurrency(vnd)
                .name("Chuyến đi Đà Lạt")
                .note("Nhóm bạn đi Đà Lạt 3 ngày 2 đêm")
                .startDate(LocalDate.of(2026, 8, 1))
                .endDate(LocalDate.of(2026, 8, 3))
                .build());

        Group groupHouse = groupRepository.save(Group.builder()
                .category(food)
                .defaultCurrency(vnd)
                .name("Tiền nhà chung cư")
                .note("Chi phí sinh hoạt hàng tháng")
                .build());

        // 4. Seed Group Members
        groupMemberRepository.save(GroupMember.builder().group(groupDaLat).user(hung).role("ADMIN").build());
        groupMemberRepository.save(GroupMember.builder().group(groupDaLat).user(khanh).role("MEMBER").build());
        groupMemberRepository.save(GroupMember.builder().group(groupDaLat).user(an).role("MEMBER").build());
        groupMemberRepository.save(GroupMember.builder().group(groupDaLat).user(binh).role("MEMBER").build());

        groupMemberRepository.save(GroupMember.builder().group(groupHouse).user(hung).role("ADMIN").build());
        groupMemberRepository.save(GroupMember.builder().group(groupHouse).user(khanh).role("MEMBER").build());

        // 5. Seed Invitations
        groupInvitationRepository.save(GroupInvitation.builder()
                .group(groupDaLat).inviter(hung).invitee(binh)
                .status("ACCEPTED").token("tok_abc123")
                .message("Đi Đà Lạt cùng bọn mình nhé!")
                .expiresAt(LocalDateTime.of(2026, 7, 30, 0, 0))
                .build());

        // 6. Seed Sample Expense
        Expense expHotel = expenseRepository.save(Expense.builder()
                .group(groupDaLat).currency(vnd).category(hotel)
                .description("Đặt phòng khách sạn 2 đêm")
                .totalAmount(new BigDecimal("2400000.00"))
                .expenseDate(LocalDate.of(2026, 8, 1))
                .build());

        expensePayerRepository.save(ExpensePayer.builder().expense(expHotel).user(hung).amount(new BigDecimal("2400000.00")).build());

        for (User member : List.of(hung, khanh, an, binh)) {
            expenseShareRepository.save(ExpenseShare.builder().expense(expHotel).user(member).amount(new BigDecimal("600000.00")).build());
        }

        // 7. Seed Activity Log
        activityRepository.save(Activity.builder()
                .user(hung).entityType("GROUP").entityId(groupDaLat.getId())
                .topic("Tạo nhóm").description("hungtri đã tạo nhóm 'Chuyến đi Đà Lạt'")
                .build());

        log.info("===============================================================================");
        log.info("SUCCESS: Java DevDataSeeder finished populating sample data!");
        log.info("Sample logins: [hungtri, khanhnt, anle, binhpham, adminuser] / password: 123456");
        log.info("===============================================================================");
    }
}
