package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.entity.*;
import com.fastcampus.ecommerce.model.CheckoutRequest;
import com.fastcampus.ecommerce.model.ShippingRateResponse;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
            CartItem.builder().cartItemId(1L).productId(1L).quantity(2).price(new BigDecimal("100.00")).build()
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
        when(cartItemRepository.findAllById(anyList())).thenReturn(cartItems);
        when(userAddressRepository.findById(anyLong())).thenReturn(Optional.of(userAddress));
        when(inventoryService.checkAndLockInventory(anyMap())).thenReturn(Boolean.TRUE);
        when(orderRepository.save(any(Order.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(userAddressRepository.findByUserIdAndIsDefaultTrue(anyLong())).thenReturn(Optional.of(sellerAddress));

        ShippingRateResponse shippingRateResponse = new ShippingRateResponse();
    }
}