package pl.auctane.order.dtos.order;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderProductDto {
    private Long orderId;
    private Long productId;
}
