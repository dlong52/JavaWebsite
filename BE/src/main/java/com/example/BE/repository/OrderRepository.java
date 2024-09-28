package com.example.BE.repository;
import com.example.BE.models.Order;
import com.example.BE.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    List<Order> findByUser(User user);
    Page<Order> findByUser_UserId(int userId, Pageable pageable);
}
