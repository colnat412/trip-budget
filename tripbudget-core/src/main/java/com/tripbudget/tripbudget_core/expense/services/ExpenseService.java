package com.tripbudget.tripbudget_core.expense.services;

import com.tripbudget.tripbudget_core.common.services.HashidsService;
import com.tripbudget.tripbudget_core.expense.dtos.request.CreateExpenseRequest;
import com.tripbudget.tripbudget_core.expense.dtos.request.SplitItemRequest;
import com.tripbudget.tripbudget_core.expense.dtos.request.UpdateExpenseRequest;
import com.tripbudget.tripbudget_core.expense.dtos.response.ExpenseResponse;
import com.tripbudget.tripbudget_core.expense.dtos.response.ExpenseSplitResponse;
import com.tripbudget.tripbudget_core.expense.entities.ExpenseEntity;
import com.tripbudget.tripbudget_core.expense.entities.ExpenseSplitEntity;
import com.tripbudget.tripbudget_core.expense.enums.ExpenseStatus;
import com.tripbudget.tripbudget_core.expense.enums.SplitType;
import com.tripbudget.tripbudget_core.expense.repositories.ExpenseRepository;
import com.tripbudget.tripbudget_core.trip.dtos.response.PageResponse;
import com.tripbudget.tripbudget_core.trip.entities.TripEntity;
import com.tripbudget.tripbudget_core.trip.entities.TripMemberEntity;
import com.tripbudget.tripbudget_core.trip.enums.TripMemberStatus;
import com.tripbudget.tripbudget_core.trip.enums.TripStatus;
import com.tripbudget.tripbudget_core.trip.repositories.TripMemberRepository;
import com.tripbudget.tripbudget_core.trip.repositories.TripRepository;
import com.tripbudget.tripbudget_core.user.entities.UserEntity;
import com.tripbudget.tripbudget_core.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;
    private final HashidsService hashidsService;

    @Transactional
    public ExpenseResponse createExpense(Long currentUserId, Long tripId, CreateExpenseRequest req) {
        TripEntity trip = getValidTrip(tripId);
        validateMembership(tripId, currentUserId);

        Long payerId = (req.payerId() != null && !req.payerId().isBlank())
                ? hashidsService.decode(req.payerId())
                : currentUserId;
        String currency = (req.currency() != null && !req.currency().isBlank())
                ? req.currency().trim().toUpperCase(Locale.ROOT)
                : trip.getBaseCurrency();

        LocalDate expenseDate = req.expenseDate() != null ? req.expenseDate() : LocalDate.now();
        SplitType splitType = req.splitType() != null ? req.splitType() : SplitType.EQUAL;

        ExpenseEntity expense = ExpenseEntity.create(
                tripId,
                payerId,
                req.title(),
                req.category(),
                req.amount(),
                currency,
                expenseDate,
                splitType,
                req.note(),
                req.receiptUrl()
        );

        // Xử lý phân bổ tiền split cho từng thành viên
        buildExpenseSplits(expense, req.amount(), splitType, req.splits(), payerId, tripId, currentUserId);

        ExpenseEntity savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseResponse> getTripExpenses(Long currentUserId, Long tripId, int page, int size) {
        getValidTrip(tripId);
        validateMembership(tripId, currentUserId);

        Pageable pageable = PageRequest.of(page, size);
        Page<ExpenseEntity> expensePage = expenseRepository
                .findByTripIdAndStatusNotAndIsDelFalseOrderByExpenseDateDesc(
                        tripId,
                        ExpenseStatus.DELETED,
                        pageable
                );

        List<ExpenseEntity> content = expensePage.getContent();
        Map<Long, UserEntity> userMap = getUserMapForExpenses(content);
        List<ExpenseResponse> items = content.stream()
                .map(e -> mapToResponse(e, userMap))
                .toList();

        PageResponse.Pagination pagination = new PageResponse.Pagination(
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.getTotalElements(),
                expensePage.getTotalPages(),
                expensePage.hasNext()
        );

        return new PageResponse<>(items, pagination);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseDetail(Long currentUserId, Long tripId, Long expenseId) {
        getValidTrip(tripId);
        validateMembership(tripId, currentUserId);

        ExpenseEntity expense = expenseRepository.findByIdAndTripIdAndIsDelFalse(expenseId, tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        if (expense.getStatus() == ExpenseStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense has been deleted");
        }

        return mapToResponse(expense);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long currentUserId, Long tripId, Long expenseId, UpdateExpenseRequest req) {
        TripEntity trip = getValidTrip(tripId);
        validateMembership(tripId, currentUserId);

        ExpenseEntity expense = expenseRepository.findByIdAndTripIdAndIsDelFalse(expenseId, tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        if (expense.getStatus() == ExpenseStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense has been deleted");
        }

        BigDecimal newAmount = req.amount() != null ? req.amount() : expense.getAmount();
        SplitType newSplitType = req.splitType() != null ? req.splitType() : expense.getSplitType();
        String newCurrency = (req.currency() != null && !req.currency().isBlank())
                ? req.currency().trim().toUpperCase(Locale.ROOT)
                : expense.getCurrency();

        expense.update(
                req.title() != null ? req.title() : expense.getTitle(),
                req.category() != null ? req.category() : expense.getCategory(),
                newAmount,
                newCurrency,
                req.expenseDate() != null ? req.expenseDate() : expense.getExpenseDate(),
                newSplitType,
                req.note(),
                req.receiptUrl()
        );

        Long payerId = (req.payerId() != null && !req.payerId().isBlank())
                ? hashidsService.decode(req.payerId())
                : expense.getPayerId();

        // Cập nhật lại splits nếu có thay đổi splits hoặc thay đổi amount
        if (req.splits() != null && !req.splits().isEmpty()) {
            expense.clearSplits();
            buildExpenseSplits(expense, newAmount, newSplitType, req.splits(), payerId, tripId, currentUserId);
        } else if (req.amount() != null && req.amount().compareTo(expense.getAmount()) != 0) {
            // Tái tính toán lại các split hiện tại theo số tiền mới
            recalculateExistingSplits(expense, newAmount, payerId);
        }

        ExpenseEntity savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    @Transactional
    public void deleteExpense(Long currentUserId, Long tripId, Long expenseId) {
        getValidTrip(tripId);
        validateMembership(tripId, currentUserId);

        ExpenseEntity expense = expenseRepository.findByIdAndTripIdAndIsDelFalse(expenseId, tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        expense.deleteExpense();
        expenseRepository.save(expense);
    }

    // ==========================================
    // LOGIC TÍNH TOÁN VÀ PHÂN BỔ TIỀN (SPLIT)
    // ==========================================

    private void buildExpenseSplits(
            ExpenseEntity expense,
            BigDecimal totalAmount,
            SplitType splitType,
            List<SplitItemRequest> requestedSplits,
            Long payerId,
            Long tripId,
            Long currentUserId
    ) {
        List<Long> targetUserIds = new ArrayList<>();

        if (requestedSplits != null && !requestedSplits.isEmpty()) {
            for (SplitItemRequest item : requestedSplits) {
                if (item.userId() != null && !item.userId().isBlank()) {
                    Long uid = hashidsService.decode(item.userId());
                    if (!targetUserIds.contains(uid)) {
                        targetUserIds.add(uid);
                    }
                }
            }
        }

        // Nếu không truyền danh sách user tham gia -> Mặc định lấy tất cả active members trong trip
        if (targetUserIds.isEmpty()) {
            List<TripMemberEntity> members = tripMemberRepository.findAllByTrip_IdAndStatus(tripId, TripMemberStatus.ACTIVE);
            for (TripMemberEntity m : members) {
                targetUserIds.add(m.getUserId());
            }
            if (targetUserIds.isEmpty()) {
                targetUserIds.add(currentUserId);
            }
        }

        int count = targetUserIds.size();

        if (splitType == SplitType.EQUAL) {
            // Chia đều cho N người, xử lý số dư lẻ dồn cho Payer
            BigDecimal baseShare = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.FLOOR);
            BigDecimal totalAllocated = baseShare.multiply(BigDecimal.valueOf(count));
            BigDecimal remainder = totalAmount.subtract(totalAllocated);

            for (Long uid : targetUserIds) {
                BigDecimal userShare = baseShare;
                // Nếu là người trả tiền (hoặc người đầu tiên nếu payer không tham gia chia), cộng phần lẻ
                if (uid.equals(payerId) || (remainder.compareTo(BigDecimal.ZERO) > 0 && uid.equals(targetUserIds.get(0)))) {
                    userShare = userShare.add(remainder);
                    remainder = BigDecimal.ZERO;
                }

                expense.addSplit(ExpenseSplitEntity.create(
                        expense,
                        uid,
                        userShare,
                        BigDecimal.ONE
                ));
            }
        } else if (splitType == SplitType.EXACT_AMOUNT && requestedSplits != null) {
            for (SplitItemRequest item : requestedSplits) {
                Long uid = hashidsService.decode(item.userId());
                BigDecimal allocated = item.allocatedAmount() != null ? item.allocatedAmount() : BigDecimal.ZERO;
                expense.addSplit(ExpenseSplitEntity.create(
                        expense,
                        uid,
                        allocated,
                        allocated
                ));
            }
        } else if (splitType == SplitType.PERCENTAGE && requestedSplits != null) {
            BigDecimal sumAllocated = BigDecimal.ZERO;
            for (int i = 0; i < requestedSplits.size(); i++) {
                SplitItemRequest item = requestedSplits.get(i);
                Long uid = hashidsService.decode(item.userId());
                BigDecimal pct = item.splitValue() != null ? item.splitValue() : BigDecimal.ZERO;
                BigDecimal allocated = totalAmount.multiply(pct)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR);

                sumAllocated = sumAllocated.add(allocated);

                // Dòng cuối cùng nhận phần bù chênh lệch để tổng bằng 100%
                if (i == requestedSplits.size() - 1) {
                    BigDecimal diff = totalAmount.subtract(sumAllocated);
                    allocated = allocated.add(diff);
                }

                expense.addSplit(ExpenseSplitEntity.create(
                        expense,
                        uid,
                        allocated,
                        pct
                ));
            }
        } else if (splitType == SplitType.SHARE && requestedSplits != null) {
            BigDecimal totalShares = BigDecimal.ZERO;
            for (SplitItemRequest item : requestedSplits) {
                BigDecimal share = item.splitValue() != null ? item.splitValue() : BigDecimal.ONE;
                totalShares = totalShares.add(share);
            }

            if (totalShares.compareTo(BigDecimal.ZERO) <= 0) {
                totalShares = BigDecimal.valueOf(count);
            }

            BigDecimal sumAllocated = BigDecimal.ZERO;
            for (int i = 0; i < requestedSplits.size(); i++) {
                SplitItemRequest item = requestedSplits.get(i);
                Long uid = hashidsService.decode(item.userId());
                BigDecimal share = item.splitValue() != null ? item.splitValue() : BigDecimal.ONE;
                BigDecimal allocated = totalAmount.multiply(share)
                        .divide(totalShares, 2, RoundingMode.FLOOR);

                sumAllocated = sumAllocated.add(allocated);

                if (i == requestedSplits.size() - 1) {
                    BigDecimal diff = totalAmount.subtract(sumAllocated);
                    allocated = allocated.add(diff);
                }

                expense.addSplit(ExpenseSplitEntity.create(
                        expense,
                        uid,
                        allocated,
                        share
                ));
            }
        }
    }

    private void recalculateExistingSplits(ExpenseEntity expense, BigDecimal newTotalAmount, Long payerId) {
        List<ExpenseSplitEntity> splits = expense.getSplits();
        if (splits.isEmpty()) return;

        int count = splits.size();
        SplitType splitType = expense.getSplitType();

        if (splitType == SplitType.EQUAL) {
            BigDecimal baseShare = newTotalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.FLOOR);
            BigDecimal totalAllocated = baseShare.multiply(BigDecimal.valueOf(count));
            BigDecimal remainder = newTotalAmount.subtract(totalAllocated);

            for (ExpenseSplitEntity s : splits) {
                BigDecimal userShare = baseShare;
                if (s.getUserId().equals(payerId)) {
                    userShare = userShare.add(remainder);
                    remainder = BigDecimal.ZERO;
                }
                s.update(userShare, BigDecimal.ONE);
            }
        }
    }

    // ==========================================
    // VALIDATION VÀ HELPER MAPPERS
    // ==========================================

    private TripEntity getValidTrip(Long tripId) {
        TripEntity trip = tripRepository.findActiveTrip(tripId);
        if (trip == null || trip.getStatus() == TripStatus.DELETED || trip.isDel()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
        }
        return trip;
    }

    private void validateMembership(Long tripId, Long currentUserId) {
        boolean isMember = tripMemberRepository.existsByTrip_IdAndUserIdAndStatus(
                tripId,
                currentUserId,
                TripMemberStatus.ACTIVE
        );
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission in this trip");
        }
    }

    private Map<Long, UserEntity> getUserMapForExpenses(List<ExpenseEntity> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> userIds = new HashSet<>();
        for (ExpenseEntity exp : expenses) {
            if (exp.getPayerId() != null) {
                userIds.add(exp.getPayerId());
            }
            if (exp.getSplits() != null) {
                for (ExpenseSplitEntity s : exp.getSplits()) {
                    if (s.getUserId() != null && !s.isDel()) {
                        userIds.add(s.getUserId());
                    }
                }
            }
        }
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<UserEntity> users = userRepository.findAllByIdIn(userIds);
        Map<Long, UserEntity> map = new HashMap<>();
        for (UserEntity u : users) {
            map.put(u.getId(), u);
        }
        return map;
    }

    private ExpenseResponse mapToResponse(ExpenseEntity entity, Map<Long, UserEntity> userMap) {
        UserEntity payer = userMap != null ? userMap.get(entity.getPayerId()) : null;
        String payerName = payer != null ? payer.getName() : null;
        String payerEmail = payer != null ? payer.getEmail() : null;
        String payerAvatarUrl = payer != null ? payer.getAvatarUrl() : null;

        List<ExpenseSplitResponse> splitResponses = entity.getSplits().stream()
                .filter(s -> !s.isDel())
                .map(s -> {
                    UserEntity splitUser = userMap != null ? userMap.get(s.getUserId()) : null;
                    return new ExpenseSplitResponse(
                            hashidsService.encode(s.getId()),
                            hashidsService.encode(s.getUserId()),
                            splitUser != null ? splitUser.getName() : null,
                            splitUser != null ? splitUser.getEmail() : null,
                            splitUser != null ? splitUser.getAvatarUrl() : null,
                            s.getAllocatedAmount(),
                            s.getSplitValue(),
                            s.isSettled()
                    );
                })
                .toList();

        return new ExpenseResponse(
                hashidsService.encode(entity.getId()),
                hashidsService.encode(entity.getTripId()),
                hashidsService.encode(entity.getPayerId()),
                payerName,
                payerEmail,
                payerAvatarUrl,
                entity.getTitle(),
                entity.getCategory(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getExpenseDate(),
                entity.getSplitType(),
                entity.getStatus(),
                entity.getNote(),
                entity.getReceiptUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                splitResponses
        );
    }

    private ExpenseResponse mapToResponse(ExpenseEntity entity) {
        Map<Long, UserEntity> userMap = getUserMapForExpenses(List.of(entity));
        return mapToResponse(entity, userMap);
    }
}
