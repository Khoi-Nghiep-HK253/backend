package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.dto.response.ReceiptScanResponse;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.GroupMember;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.mapper.ExpenseMapper;
import com.hcmut.divvy.repository.CurrencyRepository;
import com.hcmut.divvy.repository.GroupMemberRepository;
import com.hcmut.divvy.repository.GroupRepository;
import com.hcmut.divvy.repository.UserRepository;
import com.hcmut.divvy.service.ReceiptScanService;
import com.hcmut.divvy.service.model.ReceiptExtraction;
import com.hcmut.divvy.service.model.ScanReceiptModel;
import com.hcmut.divvy.validator.ExpenseValidator;
import com.hcmut.divvy.validator.GroupValidator;
import com.hcmut.divvy.validator.ReceiptValidator;
import com.hcmut.divvy.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceiptScanServiceImpl implements ReceiptScanService {

    private static final String SYSTEM_PROMPT = """
            You are an assistant that extracts structured data from receipt/bill photos \
            for a group expense-splitting app. Read the image carefully. \
            Only report values you can actually read on the receipt — never estimate or invent numbers. \
            Leave a field null if it cannot be determined. Receipts may be in Vietnamese or English.""";

    private static final String USER_PROMPT = """
            Extract from this receipt photo: the merchant/store name, \
            the final total amount paid, the ISO 4217 currency code of the amounts (e.g. VND, USD, EUR; \
            infer it from symbols like ₫, đ, $, € — null if it is not stated or cannot be inferred), \
            and the list of line items with their name and price. \
            If the photo is blurry, cropped, or not a receipt, leave totalAmount null.""";

    private static final String DEFAULT_CURRENCY = "VND";

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final CurrencyRepository currencyRepository;
    private final ExpenseMapper expenseMapper;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;
    private final ExpenseValidator expenseValidator;
    private final ReceiptValidator receiptValidator;
    private final ChatClient chatClient;

    @Override
    public ReceiptScanResponse scanReceipt(ScanReceiptModel model) {
        receiptValidator.validateImageFile(model.getImage());

        User caller = userValidator.validateUserExists(userRepository.findByUsername(model.getCurrentUsername()),
                "username", model.getCurrentUsername());
        Group group = groupValidator.validateGroupExists(groupRepository.findById(model.getGroupId()),
                model.getGroupId());

        GroupMember callerMember = groupMemberRepository.findByGroupIdAndUserId(group.getId(), caller.getId())
                .orElse(null);
        expenseValidator.validateIsMember(callerMember);

        ReceiptExtraction extraction = extractReceipt(model.getImage());
        receiptValidator.validateExtraction(extraction.totalAmount());

        Currency currency = resolveCurrency(extraction.currencyCode());

        return expenseMapper.toReceiptScanResponse(extraction, currency);
    }

    /** Currency detected on the receipt if the system supports it, otherwise the default (VND). */
    private Currency resolveCurrency(String detectedCode) {
        if (detectedCode != null && !detectedCode.isBlank()) {
            Optional<Currency> detected = currencyRepository.findByAcronym(detectedCode.trim().toUpperCase());
            if (detected.isPresent()) {
                return detected.get();
            }
            log.info("Receipt currency '{}' is not supported, falling back to {}", detectedCode, DEFAULT_CURRENCY);
        }
        return currencyRepository.findByAcronym(DEFAULT_CURRENCY).orElse(null);
    }

    private ReceiptExtraction extractReceipt(MultipartFile image) {
        try {
            Media media = Media.builder()
                    .mimeType(MimeType.valueOf(image.getContentType()))
                    .data(new ByteArrayResource(image.getBytes()))
                    .build();

            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(u -> u.text(USER_PROMPT).media(media))
                    .call()
                    .entity(ReceiptExtraction.class);
        } catch (IOException e) {
            throw new BusinessException("Could not read the uploaded image.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Receipt OCR call failed", e);
            throw new BusinessException("Could not analyze this receipt right now. Please try again.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
