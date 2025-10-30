package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.entity.*;
import com.fastcampus.ecommerce.model.*;
import com.fastcampus.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserAddressRepository userAddressRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ShippingService shippingService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CheckoutRequest checkoutRequest;
    private List<CartItem> cartItems;
    private UserAddress userAddress;
    private Product product;
    private UserAddress sellerAddress;
    private User seller;
    private User buyer;

    @BeforeEach
    void setUp(){
        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setUserId(1L);
        checkoutRequest.setUserAddressId(1L);
        checkoutRequest.setSelectedCartItemIds(Arrays.asList(1L, 2L));

        cartItems = List.of(
            CartItem.builder().cartItemId(1L).productId(1L).quantity(2).price(new BigDecimal("100.00")).build(),
            CartItem.builder().cartItemId(2L).productId(2L).quantity(1).price(new BigDecimal("100.00")).build()
        );

        userAddress = new UserAddress();
        userAddress.setUserAddressId(1L);

        seller = new User();
        seller.setUserId(1L);
        buyer = new User();
        buyer.setUserId(2L);

        product = new Product();
        product.setProductId(1L);
        product.setWeight(new BigDecimal("0.5"));
        product.setUserId(seller.getUserId());

        sellerAddress = new UserAddress();
        sellerAddress.setUserAddressId(2L);
        sellerAddress.setUserId(seller.getUserId());
    }

    @Test
    void tesCheckout_SuccessfulCheckout() {
        // request
        when(cartItemRepository.findAllById(anyList())).thenReturn(cartItems);
        when(userAddressRepository.findById(anyLong())).thenReturn(Optional.of(userAddress));
        when(inventoryService.checkAndLockInventory(anyMap())).thenReturn(Boolean.TRUE);
        when(orderRepository.save(any(Order.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(userAddressRepository.findByUserIdAndIsDefaultTrue(anyLong())).thenReturn(Optional.of(sellerAddress));

        ShippingRateResponse shippingRateResponse = new ShippingRateResponse();
        shippingRateResponse.setShippingFee(new BigDecimal("10.00"));
        when(shippingService.calculateShippingRate(any())).thenReturn(shippingRateResponse);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setXenditInvoiceId("payment123");
        paymentResponse.setXenditInvoiceStatus("PENDING");
        paymentResponse.setXenditPaymentUrl("http://payment.url");
        when(paymentService.create(any(Order.class))).thenReturn(paymentResponse);

        // act
        OrderResponse result = orderService.checkout(checkoutRequest);

        // assert
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals("payment123", result.getXenditInvoiceId());
        assertEquals("http://payment.url", result.getPaymentUrl());

        verify(cartItemRepository).findAllById(checkoutRequest.getSelectedCartItemIds());
        verify(userAddressRepository).findById(checkoutRequest.getUserAddressId());
        verify(inventoryService).checkAndLockInventory(anyMap());
        verify(orderRepository, times(3)).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
        verify(cartItemRepository).deleteAll(anyList());
        verify(shippingService, times(2)).calculateShippingRate(any());
        verify(paymentService).create(any());
        verify(inventoryService).decreaseQuantity(anyMap());
        verify(userAddressRepository, times(2)).findByUserIdAndIsDefaultTrue(anyLong());
    }
}