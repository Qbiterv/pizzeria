package pl.auctane.order.dtos.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductIdWithQuantityDto {
    Long productId;

    int quantity;

    public ProductIdWithQuantityDto() {}
    public ProductIdWithQuantityDto(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    public ProductWithQuantityDto toProductWithQuantityDto(ProductDto productDto) {
        return new ProductWithQuantityDto(productDto, quantity);
    }
}
