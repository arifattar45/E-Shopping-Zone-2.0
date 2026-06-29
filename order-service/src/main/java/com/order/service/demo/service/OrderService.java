package com.order.service.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.order.service.demo.dto.AdminDashboardResponse;
import com.order.service.demo.dto.CartItem;
import com.order.service.demo.dto.CreateOrderRequest;
import com.order.service.demo.dto.CreateOrderResponse;
import com.order.service.demo.dto.OrderRequest;
import com.order.service.demo.dto.VerifyPaymentRequest;
import com.order.service.demo.dto.VerifyPaymentResponse;
import com.order.service.demo.dto.WalletDebitRequest;
import com.order.service.demo.entity.OrderItem;
import com.order.service.demo.entity.Orders;
import com.order.service.demo.enums.OrderStatus;
import com.order.service.demo.enums.PaymentMethod;
import com.order.service.demo.enums.PaymentStatus;
import com.order.service.demo.fiegn.CartClient;
import com.order.service.demo.fiegn.WalletClient;
import com.order.service.demo.repository.OrderItemRepository;
import com.order.service.demo.repository.OrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class OrderService {
	
	@Autowired
	private RazorpayClient razorpayClient;

	@Value("${razorpay.key.id}")
	private String keyId;

	@Value("${razorpay.key.secret}")
	private String keySecret;

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final CartClient cartClient;
    private final WalletClient walletClient;

    public OrderService(
            OrderRepository orderRepo,
            OrderItemRepository itemRepo,
            CartClient cartClient,
            WalletClient walletClient) {

        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.cartClient = cartClient;
        this.walletClient = walletClient;
    }

    public Orders checkout(Long userId, String email, OrderRequest request, boolean paymentVerified) {

        List<CartItem> cartItems = cartClient.getCart(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        PaymentMethod paymentMethod;

        try {
            paymentMethod = PaymentMethod.valueOf(
                    request.getPaymentMethod().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid Payment Method");
        }

        double total = cartItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        // Wallet Payment
        if (paymentMethod == PaymentMethod.WALLET) {

            WalletDebitRequest walletRequest = new WalletDebitRequest();

            walletRequest.setUserId(userId);
            walletRequest.setAmount(BigDecimal.valueOf(total));
            walletRequest.setDescription("Order Payment");

            walletClient.debit(walletRequest);
        }

        Orders order = new Orders();

        // Customer Details
        order.setFullName(request.getFullName());
        order.setPhone(request.getPhone());

        // Address
        order.setHouse(request.getHouse());
        order.setStreet(request.getStreet());
        order.setCity(request.getCity());
        order.setState(request.getState());
        order.setPincode(request.getPincode());

        // User
        order.setUserId(userId);
        order.setUserEmail(email);

        // Payment
        order.setPaymentMethod(paymentMethod);

        if (paymentMethod == PaymentMethod.COD) {

            order.setPaymentStatus(PaymentStatus.PENDING);

        } else if (paymentMethod == PaymentMethod.WALLET) {

            order.setPaymentStatus(PaymentStatus.SUCCESS);

        } else if(paymentMethod==PaymentMethod.UPI){

            order.setPaymentStatus(
                paymentVerified
                    ? PaymentStatus.SUCCESS
                    : PaymentStatus.PENDING
            );

        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());

        Orders saved = orderRepo.save(order);

        for (CartItem cartItem : cartItems) {

            OrderItem item = new OrderItem();

            item.setOrderId(saved.getId());
            item.setProductId(cartItem.getProductId());
            item.setProductName(cartItem.getProductName());
            item.setImageUrl(cartItem.getImageUrl());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPrice());

            itemRepo.save(item);
        }

        cartClient.clearCart(userId);

        saved.setItems(itemRepo.findByOrderId(saved.getId()));

        return saved;
    }

    public List<Orders> getMyOrders(Long userId) {

        List<Orders> orders = orderRepo.findByUserId(userId);

        for (Orders order : orders) {
            order.setItems(itemRepo.findByOrderId(order.getId()));
        }

        return orders;
    }

    public List<Orders> getAllOrders() {
        return orderRepo.findAll();
    }

    public AdminDashboardResponse getDashboardData() {

        AdminDashboardResponse dto = new AdminDashboardResponse();

        dto.setTotalRevenue(orderRepo.getTotalRevenue());
        dto.setTotalOrders(orderRepo.count());

        // Temporary
        dto.setTotalProducts(10L);
        dto.setTotalUsers(5L);

        return dto;
    }

    public Orders updateStatus(Long id, String status) {

        Orders order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot modify delivered order");
        }

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.CANCELLED) {

            order.setPaymentStatus(PaymentStatus.REFUNDED);

            // TODO:
            // If payment method == WALLET
            // Credit amount back to wallet.
        }

        return orderRepo.save(order);
    }

    public Orders cancelOrder(Long userId, Long orderId) {

        Orders order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }

        order.setStatus(OrderStatus.CANCELLED);

        if (order.getPaymentMethod() == PaymentMethod.WALLET) {

            order.setPaymentStatus(PaymentStatus.REFUNDED);

            // TODO:
            // walletClient.credit(...)
        }

        return orderRepo.save(order);
    }
    
    
    public CreateOrderResponse createRazorpayOrder(
            CreateOrderRequest request) throws Exception {

        JSONObject options = new JSONObject();

        options.put("amount", request.getAmount() * 100);

        options.put("currency", "INR");

        options.put("receipt", "order_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(options);

        CreateOrderResponse response = new CreateOrderResponse();

        response.setOrderId(order.get("id"));

        response.setAmount(((Number) order.get("amount")).longValue());

        response.setCurrency(order.get("currency"));

        response.setKey(keyId);

        return response;
    }
    
    public VerifyPaymentResponse verifyPayment(
            Long userId,
            String email,
            VerifyPaymentRequest request) throws Exception {

        JSONObject attributes = new JSONObject();

        attributes.put("razorpay_order_id", request.getRazorpayOrderId());
        attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
        attributes.put("razorpay_signature", request.getRazorpaySignature());

        // Verify Razorpay Signature
        Utils.verifyPaymentSignature(attributes, keySecret);

        // Convert VerifyPaymentRequest -> OrderRequest
        OrderRequest orderRequest = new OrderRequest();

        orderRequest.setFullName(request.getFullName());
        orderRequest.setPhone(request.getPhone());

        orderRequest.setHouse(request.getHouse());
        orderRequest.setStreet(request.getStreet());
        orderRequest.setCity(request.getCity());
        orderRequest.setState(request.getState());
        orderRequest.setPincode(request.getPincode());

        orderRequest.setPaymentMethod("UPI");

        // Reuse existing checkout logic
        checkout(userId, email, orderRequest, true);

        VerifyPaymentResponse response = new VerifyPaymentResponse();

        response.setSuccess(true);
        response.setMessage("Order placed successfully.");

        return response;
    }
}