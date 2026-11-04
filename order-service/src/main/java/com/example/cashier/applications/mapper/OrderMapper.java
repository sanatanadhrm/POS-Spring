package com.example.cashier.applications.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.event.OrderCreatedEventItem;
import com.example.cashier.applications.dto.event.OrderRestoreEvent;
import com.example.cashier.applications.dto.request.order.CheckoutOrderRequest;
import com.example.cashier.applications.dto.request.product.DeductBatchItemProductRequest;
import com.example.cashier.applications.dto.request.product.DeductBatchProductRequest;
import com.example.cashier.applications.dto.response.order.CheckoutOrderResponse;
import com.example.cashier.applications.dto.response.orderitem.CheckoutOrderItemResponse;
import com.example.cashier.applications.dto.response.product.GetBatchProductResponse;
import com.example.cashier.domains.orders.entity.OrderItems;

import com.example.cashier.domains.orders.entity.Orders;

@Component
public class OrderMapper {

        private CheckoutOrderItemResponse mapOrderItemToCheckoutOrderItemResponse(OrderItems orderItem) {
                return CheckoutOrderItemResponse.builder()
                                .productId(orderItem.getProductId())
                                .productVariantId(orderItem.getProductVariant())
                                .qty(orderItem.getQty())
                                .unitPrice(orderItem.getUnitPrice())
                                .subtotal(orderItem.getSubtotal())
                                .build();
        }

        private List<CheckoutOrderItemResponse> mapListOrderItemToCheckoutOrderItemResponse(
                        List<OrderItems> orderItems) {
                return orderItems.stream()
                                .map(this::mapOrderItemToCheckoutOrderItemResponse)
                                .collect(Collectors.toList());
        }

        public CheckoutOrderResponse mapOrdersToCheckoutOrderResponse(Orders orders) {
                return CheckoutOrderResponse.builder()
                                .id(orders.getId())
                                .storeId(orders.getStoreId())
                                .cashierId(orders.getCashierId())
                                .orderType(orders.getOrderType())
                                .tableNumber(orders.getTableNumber())
                                .paymentMethod(orders.getPaymentMethod())
                                .cashReceived(orders.getCashReceived())
                                .changeAmount(orders.getChangeAmount())
                                .totalPrice(orders.getTotalPrice())
                                .createdAt(orders.getCreatedAt())
                                .items(this.mapListOrderItemToCheckoutOrderItemResponse(orders.getItems()))
                                .build();
        }

        public OrderCreatedEventItem mapOrderItemToOrderCreatedEventItem(OrderItems orderItem,
                        Map<String, GetBatchProductResponse> productResponse) {
                return OrderCreatedEventItem.builder()
                                .productName(productResponse.get(orderItem.getProductVariant()).getName())
                                .qty(orderItem.getQty())
                                .unitPrice(orderItem.getUnitPrice())
                                .subtotal(orderItem.getSubtotal())
                                .build();
        }

        public DeductBatchProductRequest mapOrderPaylotToDeductBatchProductRequest(CheckoutOrderRequest payload,
                        String storeId) {
                List<DeductBatchItemProductRequest> deductItems = payload.getItems().stream()
                                .map(item -> new DeductBatchItemProductRequest(item.getProductId(), item.getQty(),
                                                item.getProductVariantId()))
                                .toList();
                return DeductBatchProductRequest.builder()
                                .storeId(storeId)
                                .items(deductItems).build();
        }

        public OrderRestoreEvent mapOrderPayloadToOrderRestoreEvent(CheckoutOrderRequest payload,
                        String storeId) {
                List<OrderRestoreEvent.RestoreItem> itemsToRestore = payload.getItems().stream()
                                .map(item -> new OrderRestoreEvent.RestoreItem(item.getProductId(), item.getQty()))
                                .toList();
                OrderRestoreEvent result = new OrderRestoreEvent(
                                storeId,
                                itemsToRestore);
                return result;
        }

}
