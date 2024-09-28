package com.example.BE.services;

import com.example.BE.dto.CartDTO;
import com.example.BE.dto.CartItemDTO;
import com.example.BE.models.Cart;
import com.example.BE.models.CartItem;
import com.example.BE.models.Product;
import com.example.BE.models.User;
import com.example.BE.repository.CartItemRepository;
import com.example.BE.repository.CartRepository;
import com.example.BE.repository.ProductRepository;
import com.example.BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServices {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public CartDTO addToCart(int userId, int productId, int quantity, String size) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Product> product = productRepository.findById(productId);

        if (user.isPresent() && product.isPresent()) {
            Cart cart = cartRepository.findByUser(user.get()).orElse(new Cart());
            cart.setUser(user.get());

            // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
            boolean productExists = false;

            for (CartItem item : cart.getItems()) {
                // So sánh productId và size
                if (item.getProduct().getProductId() == productId && item.getSize().equals(size)) {
                    item.setQuantity(item.getQuantity() + quantity); // Cập nhật số lượng
                    cartItemRepository.save(item); // Lưu cập nhật
                    productExists = true; // Đánh dấu là đã có sản phẩm
                    break; // Thoát vòng lặp vì đã tìm thấy sản phẩm
                }
            }
            // Nếu chưa có sản phẩm trong giỏ hàng, tạo mới
            if (!productExists) {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product.get());
                cartItem.setQuantity(quantity);
                cartItem.setSize(size);
                cartItem.setCart(cart);

                // Thêm sản phẩm vào giỏ hàng
                cart.getItems().add(cartItem);
                cartItemRepository.save(cartItem); // Lưu sản phẩm vào giỏ hàng
            }
            // Cập nhật giỏ hàng
            cartRepository.save(cart); // Lưu giỏ hàng
            return convertToCartDTO(cart); // Trả về CartDTO
        }
        return null; // Trả về null nếu không tìm thấy user hoặc product
    }

    public CartDTO getCartByUserId(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Cart cart = cartRepository.findByUser(user.get()).orElse(null);
            return convertToCartDTO(cart); // Convert Cart to CartDTO
        }
        return null;
    }

    public CartItemDTO updateCartItem(long cartItemId, int quantity, String size) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById((int) cartItemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(quantity);
            cartItem.setSize(size);
            cartItemRepository.save(cartItem);
            return convertToCartItemDTO(cartItem); // Convert CartItem to CartItemDTO
        }
        return null; // Trả về null nếu không tìm thấy CartItem
    }
    public boolean deleteCartItem(int cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
            return true;
        }
        return false;
    }
    private CartDTO convertToCartDTO(Cart cart) {
        if (cart == null) {
            return null; // Trả về null nếu cart là null
        }
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setCreatedAt(cart.getCreatedAt());
        cartDTO.setUpdatedAt(cart.getUpdatedAt());
        cartDTO.setUserId(cart.getUser().getUserId());

        // Convert CartItem to CartItemDTO
        List<CartItemDTO> cartItemDTOList = cart.getItems().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        cartDTO.setItems(cartItemDTOList);
        return cartDTO; // Trả về CartDTO
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setSize(cartItem.getSize());
        dto.setCartId(cartItem.getId()); // Sửa thành cartId của Cart
        dto.setProductImage(cartItem.getProduct().getImages()); // Điều chỉnh theo model Product của bạn
        dto.setProductName(cartItem.getProduct().getName()); // Điều chỉnh theo model Product của bạn
        dto.setPrice(cartItem.getProduct().getPrice()); // Thiết lập giá từ Product
        dto.setDiscount(cartItem.getProduct().getDiscount()); // Thiết lập giảm giá từ Product
        dto.setProductId(cartItem.getProduct().getProductId()); // Thêm dòng này để gán productId
        return dto; // Trả về CartItemDTO
    }
}
