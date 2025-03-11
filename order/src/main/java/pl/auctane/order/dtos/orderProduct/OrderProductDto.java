package pl.auctane.order.dtos.orderProduct;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderProductDto {
    private Long orderId;
    private Long productId;
}
