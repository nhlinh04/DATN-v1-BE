package org.example.datn_website_be.service;

import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.dto.response.BillDetailOrderResponse;
import org.example.datn_website_be.dto.response.ProductPromotionResponse;
import org.example.datn_website_be.model.Bill;
import org.example.datn_website_be.model.BillDetail;
import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.model.PromotionDetail;
import org.example.datn_website_be.repository.BillDetailByEmployeeRepository;
import org.example.datn_website_be.repository.BillRepository;
import org.example.datn_website_be.repository.ProductRepository;
import org.example.datn_website_be.repository.PromotionDetailRepository;
import org.example.datn_website_be.webconfig.NotificationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class BillDetailByEmployeeService {
    @Autowired
    BillDetailByEmployeeRepository billDetailByEmployeeRepository;
    @Autowired
    PromotionDetailRepository promotionDetailRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    BillRepository billRepository;
    @Autowired
    NotificationController notificationController;

    public List<BillDetailOrderResponse> getBillDetailsByCodeBill(String codeBill) {
        return billDetailByEmployeeRepository.getBillDetailsByCodeBill(codeBill);
    }
    @Transactional
    public BillDetail plusBillDetail(Long idBillDetail, Long idProduct) {
        BillDetail billDetail = validateAndGetBillDetail(idBillDetail);
        validateAndGetBill(billDetail, Status.WAITING_FOR_PAYMENT);
        ProductPromotionResponse productPromotionResponse = productRepository.findProductDetailByIdProduct(idProduct)
                .orElseThrow(() -> new RuntimeException("Tài nguyên sản phẩm không tồn tại trong hệ thống!"));
        BigDecimal productPrice = calculateDiscountedPrice(productPromotionResponse);
        if (productPromotionResponse.getQuantityProduct() <= 0) {
            throw new RuntimeException("Sản phẩm " + productPromotionResponse.getNameProduct() + " đã hết hàng");
        }
        if((billDetail.getPriceDiscount().setScale(0, RoundingMode.DOWN).compareTo(productPrice.setScale(0, RoundingMode.DOWN)) != 0)){
            throw new RuntimeException("Sản phẩm với mức giá " + billDetail.getPriceDiscount() + " VND đã hết hàng!");
        }
        Product product = productRepository.findById(idProduct)
                .orElseThrow(() -> new RuntimeException("Sản phẩm với ID " + idProduct + " không tồn tại!"));
        if(product.getStatus().equals(Status.INACTIVE.toString())){
            throw new RuntimeException("Sản phẩm " + productPromotionResponse.getNameProduct() + " đã hết hàng");
        }
        double newQuantity = product.getQuantity() - 1;
        product.setQuantity(newQuantity);
        if (newQuantity <= 0) {
            product.setStatus(Status.INACTIVE.toString());
        }
        if (productPromotionResponse.getValue() != null && productPromotionResponse.getValue() > 0) {
            PromotionDetail promotionDetail = promotionDetailRepository.findByIdAndAndStatus(
                            productPromotionResponse.getIdPromotionDetail(), Status.ONGOING.toString())
                    .orElseThrow(() -> new RuntimeException("Xảy ra lỗi khi xử lý sản phẩm giảm giá!"));
            updatePromotionDetail(promotionDetail, 1);
        }
        productRepository.save(product);
        billDetail.setActualQuantity(billDetail.getQuantity() + 1);
        billDetail.setQuantity(billDetail.getQuantity() + 1);
        BillDetail updateBillDetail = billDetailByEmployeeRepository.save(billDetail);
        notificationController.sendNotification();
        return updateBillDetail;
    }
    @Transactional
    public BillDetail subtractBillDetail(Long idBillDetail, Long idProductDetail) {
        BillDetail billDetail = validateAndGetBillDetail(idBillDetail);
        validateAndGetBill(billDetail, Status.WAITING_FOR_PAYMENT);
        Product product = productRepository.findById(idProductDetail)
                .orElseThrow(() -> new RuntimeException("Tài nguyên sản phẩm không tồn tại trong hệ thống!"));

        double newQuantity = billDetail.getQuantity() - 1;

        if (newQuantity <= 0) {
            throw new RuntimeException("Tối thiểu phải có 1 sản phẩm");
        }
        billDetail.setQuantity(newQuantity);
        billDetail.setActualQuantity(newQuantity);

        product.setQuantity(product.getQuantity() + 1);

        productRepository.save(product);
        BillDetail updateBillDetail = billDetailByEmployeeRepository.save(billDetail);
        notificationController.sendNotification();
        return updateBillDetail;
    }
    @Transactional
    public void deleteBillDetail(Long idBillDetail, Long idProductDetail) {
        BillDetail billDetail = validateAndGetBillDetail(idBillDetail);
        validateAndGetBill(billDetail, Status.WAITING_FOR_PAYMENT);

        Product product = productRepository.findById(idProductDetail)
                .orElseThrow(() -> new RuntimeException("Tài nguyên sản phẩm không tồn tại trong hệ thống!"));
        double newQuantity = billDetail.getQuantity() + product.getQuantity();

        product.setQuantity(newQuantity);

        productRepository.save(product);

        billDetailByEmployeeRepository.delete(billDetail);
        notificationController.sendNotification();
    }

    private BillDetail validateAndGetBillDetail(Long idBillDetail) {
        return billDetailByEmployeeRepository.findById(idBillDetail)
                .orElseThrow(() -> new RuntimeException("Hóa đơn chi tiết không tồn tại!"));
    }
    private Bill validateAndGetBill(BillDetail billDetail, Status expectedStatus) {
        Bill bill = billRepository.findBillByBillDetails(billDetail)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));
        if (!bill.getStatus().equals(expectedStatus.toString())) {
            throw new RuntimeException("Hóa đơn không ở trạng thái " + expectedStatus + "!");
        }
        return bill;
    }
    private BigDecimal calculateDiscountedPrice(ProductPromotionResponse productPromotionResponse) {
        BigDecimal price = productPromotionResponse.getPricePerBaseUnit();
        if (productPromotionResponse.getValue() != null && productPromotionResponse.getValue() > 0) {
            price = price.multiply(BigDecimal.valueOf(1 - productPromotionResponse.getValue() / 100))
                    .setScale(0, RoundingMode.DOWN);
        }
        return price;
    }
    private void updatePromotionDetail(PromotionDetail promotionDetail, int quantityToReduce) {
        double newQuantity = promotionDetail.getQuantity() - quantityToReduce;
        promotionDetail.setQuantity(newQuantity);
        if (newQuantity <= 0) {
            promotionDetail.setStatus(Status.FINISHED.toString());
        }
        promotionDetailRepository.save(promotionDetail);
    }
}
