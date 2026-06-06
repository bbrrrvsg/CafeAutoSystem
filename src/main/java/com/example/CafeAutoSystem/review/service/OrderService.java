package com.example.CafeAutoSystem.review.service;

import com.example.CafeAutoSystem.review.dto.OrderCreateRequestDto;
import com.example.CafeAutoSystem.review.dto.OrderItemRequestDto;
import com.example.CafeAutoSystem.review.dto.OrderResponseDto;
import com.example.CafeAutoSystem.review.entity.CafeOrder;
import com.example.CafeAutoSystem.review.entity.Menu;
import com.example.CafeAutoSystem.review.entity.OrderDetail;
import com.example.CafeAutoSystem.review.repository.CafeOrderRepository;
import com.example.CafeAutoSystem.review.repository.MenuRepository;
import com.example.CafeAutoSystem.review.repository.OrderDetailRepository;
import com.example.CafeAutoSystem.stock.dto.OrderRequest;
import com.example.CafeAutoSystem.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final MenuRepository menuRepository;
    private final CafeOrderRepository cafeOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final QrCodeService qrCodeService;
    private final StockService stockService;

    // 주문 요청 메뉴 목록을 기반으로 주문과 주문상세를 저장한다.
    public OrderResponseDto createOrder(OrderCreateRequestDto request) {
        int totalPrice = calculateTotalPrice(request.getItems());

        CafeOrder savedOrder = cafeOrderRepository.save(CafeOrder.create(totalPrice));

        String reviewPageUrl = createReviewPageUrl(savedOrder.getOrderId());
        String qrImagePath = qrCodeService.createQrImage(savedOrder.getOrderId(), reviewPageUrl);
        savedOrder.updateQrUrl(qrImagePath);

        List<OrderDetail> details = createOrderDetails(savedOrder, request.getItems());

        return OrderResponseDto.from(savedOrder, details);
    }

    // 주문 번호로 디지털 영수증에 필요한 주문 정보를 조회한다.
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId) {
        CafeOrder order = cafeOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다: " + orderId));

        List<OrderDetail> details = orderDetailRepository.findByCafeOrder(order);

        return OrderResponseDto.from(order, details);
    }

    // 메뉴 가격과 수량을 곱해 총 주문금액을 계산한다.
    private int calculateTotalPrice(List<OrderItemRequestDto> items) {
        int totalPrice = 0;
        for (OrderItemRequestDto item : items) {
            Menu menu = findMenu(item.getMenuId());
            totalPrice += menu.getMenuPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    // 주문상세 목록을 생성하고 저장한다.
    private List<OrderDetail> createOrderDetails(CafeOrder order, List<OrderItemRequestDto> items) {
        List<OrderDetail> details = new ArrayList<>();
        for (OrderItemRequestDto item : items) {
            Menu menu = findMenu(item.getMenuId());

            // 재고 차감 연결
            stockService.processOrder(OrderRequest.builder()
                    .menuName(menu.getMenuName())
                    .quantity(item.getQuantity())
                    .build());

            details.add(orderDetailRepository.save(OrderDetail.create(order, menu, item.getQuantity())));
        }
        return details;
    }

    // 메뉴 ID로 메뉴를 조회한다.
    private Menu findMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + menuId));
    }

    // QR 이미지 안에 인코딩할 리뷰 작성 페이지 URL을 생성한다.
    private String createReviewPageUrl(Long orderId) {
        return "http://localhost:8080/review/write?orderId=" + orderId;
    }
}
