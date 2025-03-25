package org.example.datn_website_be.service;

import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.dto.request.PromotionCreationRequest;
import org.example.datn_website_be.dto.request.PromotionRequest;
import org.example.datn_website_be.dto.request.PromotionUpdateRequest;
import org.example.datn_website_be.dto.request.PromotionUpdatesRequest;
import org.example.datn_website_be.dto.response.*;

import org.example.datn_website_be.model.Promotion;
import org.example.datn_website_be.model.PromotionDetail;

import org.example.datn_website_be.repository.PromotionRepository;
import org.example.datn_website_be.webconfig.NotificationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class PromotionService {

    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    PromotionDetailService promotionDetailService;

    @Autowired
    RandomPasswordGeneratorService randomCodePromotion;
    @Autowired
    NotificationController notificationController;

    //chuyển trạng thái từ sắp diễn ra thành diễn ra
    @Transactional
    public void updateUpcomingDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        //tìm kiếm trạng thái đang diễn ra
        List<Promotion> expiredDiscounts = promotionRepository.findUpcomingDiscounts(now, Status.UPCOMING.toString());
        if (!expiredDiscounts.isEmpty()) {
            for (Promotion discount : expiredDiscounts) {
                //cập nhật trạng thái
                discount.setStatus(Status.ONGOING.toString());
                //cập nhật trạng thái cho promotionDetail
                promotionDetailService.updatePromotionDetailUpcoming(discount.getId());
            }
            promotionRepository.saveAll(expiredDiscounts);
            notificationController.sendNotification();
        }
    }

    //chuyển các trạng thái sắp diễn ra, đang diễn ra, kết thúc sớm thành kết thúc
    @Transactional
    public void updateFinishedDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        //tìm kiếm trạng thái đang sắp diễn ra, đang diễn ra, kết thúc sớm
        List<String> status = new ArrayList<>(Arrays.asList(Status.ONGOING.toString(), Status.UPCOMING.toString(), Status.ENDING_SOON.toString()));
        List<Promotion> expiredDiscounts = promotionRepository.findFinishedDiscounts(now, status);
        if (!expiredDiscounts.isEmpty()) {
            for (Promotion discount : expiredDiscounts) {
                //cập nhật trạng thái
                discount.setStatus(Status.FINISHED.toString());
                //cập nhật trạng thái cho promotionDetail
                promotionDetailService.updatePromotionDetailFinished(discount.getId());
            }
            promotionRepository.saveAll(expiredDiscounts);
            notificationController.sendNotification();
        }
    }

    public Promotion updateStatus(Long id, boolean aBoolean) {
        Date now = new Date();  // Sử dụng Date thay vì LocalDateTime
        Optional<Promotion> promotionOptional = promotionRepository.findPromotionByIdAndStatus(id, Arrays.asList(Status.ONGOING.toString(), Status.UPCOMING.toString(), Status.ENDING_SOON.toString()));

        // Kiểm tra nếu promotion không tồn tại
        if (!promotionOptional.isPresent()) {
            throw new RuntimeException("Đợt giảm giá không nằm trong trạng thái đang sắp diễn ra, đang diễn ra, kết thúc sớm");
        }

        String newStatus;

        // So sánh thời gian sử dụng Date thay vì LocalDateTime
        if (aBoolean && promotionOptional.get().getStartAt().after(now) && promotionOptional.get().getEndAt().after(now)) {
            newStatus = Status.UPCOMING.toString();
        } else if (aBoolean && promotionOptional.get().getStartAt().before(now) && promotionOptional.get().getEndAt().after(now)) {
            newStatus = Status.ONGOING.toString();
        } else {
            newStatus = Status.ENDING_SOON.toString();
        }

        // Cập nhật trạng thái của promotion
        promotionOptional.get().setStatus(newStatus);
        // Lưu lại promotion đã được cập nhật
        Promotion promotion = promotionRepository.save(promotionOptional.get());
        promotionDetailService.updateStatusPromotionDetail(promotion.getId(), promotion.getStatus());
        notificationController.sendNotification();
        return promotion;
    }

    public List<PromotionResponse> getAllPromotion() {
        return promotionRepository.listPromotionResponse();
    }
@Transactional
    public Promotion createPromotion(PromotionCreationRequest promotionCreationRequest) {
        promotionCreationRequest.getPromotionRequest().validateEndDates();
        Promotion promotion = promotionRepository.save(convertPromotionRequestDTO(promotionCreationRequest.getPromotionRequest()));
        if (promotionCreationRequest.getProductPromoRequest() != null && !promotionCreationRequest.getProductPromoRequest().isEmpty()) {
            promotionDetailService.createPromotionDetail(promotion, promotionCreationRequest.getProductPromoRequest());
        }
        return promotion;
    }
@Transactional
    public Promotion updatePromotion(PromotionUpdatesRequest promotionUpdatesRequest) {
            Optional<Promotion> promotionOptional = promotionRepository.findById(promotionUpdatesRequest.getPromotionRequest().getId());
            if (!promotionOptional.isPresent()) {
                throw new RuntimeException("Đợt giảm giá không tồn tại!");
            }
            if (promotionOptional.get().getStatus().equals(Status.ENDING_SOON.toString())) {
                throw new RuntimeException("Vui lòng bật trạng thái ENDING_SOON lên trước khi thao tác");
            }
            if (promotionUpdatesRequest.getPromotionRequest().getStartAt() != null &&
                    promotionOptional.get().getStartAt() != null) {

                if (promotionUpdatesRequest.getPromotionRequest().getStartAt()
                        .compareTo(promotionOptional.get().getStartAt()) < 0) {
                    throw new RuntimeException("Ngày bắt đầu của yêu cầu cập nhật không thể sớm hơn ngày bắt đầu của khuyến mãi hiện tại.");
                }
            }
            promotionUpdatesRequest.getPromotionRequest().validateEndDates();
            promotionUpdatesRequest.getPromotionRequest().updatePromotionStatus();
            promotionOptional.get().setStartAt(promotionUpdatesRequest.getPromotionRequest().getStartAt());
            promotionOptional.get().setEndAt(promotionUpdatesRequest.getPromotionRequest().getEndAt());
            promotionOptional.get().setStatus(promotionUpdatesRequest.getPromotionRequest().getStatus());
            Promotion promotion = promotionRepository.save(promotionOptional.get());
            if (promotionUpdatesRequest.getPromotionDetailRequest() != null && !promotionUpdatesRequest.getPromotionDetailRequest().isEmpty()) {
                promotionDetailService.updatePromotionDetail(promotion, promotionUpdatesRequest.getPromotionDetailRequest());
            }
            notificationController.sendNotification();
            return promotion;
    }

    private Promotion convertPromotionRequestDTO(PromotionRequest promotionRequest) {
        String codePromotion = generatePromotionCode();
        Promotion promotion = Promotion.builder()
                .codePromotion(codePromotion)
                .name(promotionRequest.getName())
                .note(promotionRequest.getNote())
                .value(promotionRequest.getValue())
                .type(promotionRequest.getType())
                .startAt(promotionRequest.getStartAt())
                .endAt(promotionRequest.getEndAt())
                .build();
        promotion.setStatus(Status.UPCOMING.toString());
        return promotion;
    }

    private String generatePromotionCode() {
        return "PROMO" + randomCodePromotion.getCodePromotion();
    }

    public PromotionDetailResponse getPromotionDetailResponse(Long idPromotion) {
        Optional<Promotion> promotion = promotionRepository.findById(idPromotion);
        if (promotion.isEmpty()) {
            throw new RuntimeException("Đối tượng giảm giá sản phẩm không tồn tại");
        }
        List<ProductPromotionResponse> productPromotionResponses = promotionDetailService.findProductPromotionResponseByIdPromotion(promotion.get().getId());
        PromotionDetailResponse promotionDetailResponse = new PromotionDetailResponse(promotion.get(), productPromotionResponses);
        return promotionDetailResponse;
    }

}
