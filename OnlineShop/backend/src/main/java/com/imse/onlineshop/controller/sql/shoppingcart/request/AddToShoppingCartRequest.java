package com.imse.onlineshop.controller.sql.shoppingcart.request;

import com.imse.onlineshop.controller.sql.order.response.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToShoppingCartRequest {
  private ProductDTO product;
}
