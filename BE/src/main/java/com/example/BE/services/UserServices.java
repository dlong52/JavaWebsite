package com.example.BE.services;

import com.example.BE.dto.SignupRequest;
import com.example.BE.models.User;
import com.example.BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole("user"); // Set role as needed
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Integer id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setRole(userDetails.getRole());
            user.setAddress(userDetails.getAddress());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setImage(userDetails.getImage());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
    }

    public Optional<User> partialUpdateUser(Integer id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            // Kiểm tra và cập nhật từng thuộc tính nếu chúng không null
            if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
                user.setUsername(userDetails.getUsername());
            }
            if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword())); // Mã hóa mật khẩu
            }
            if (userDetails.getRole() != null && !userDetails.getRole().isEmpty()) {
                user.setRole(userDetails.getRole());
            }
            if (userDetails.getAddress() != null && !userDetails.getAddress().isEmpty()) {
                user.setAddress(userDetails.getAddress());
            }
            if (userDetails.getPhoneNumber() != null && !userDetails.getPhoneNumber().isEmpty()) {
                user.setPhoneNumber(userDetails.getPhoneNumber());
            }
            if (userDetails.getImage() != null && !userDetails.getImage().isEmpty()) {
                user.setImage(userDetails.getImage());
            }
            // Cập nhật thời gian
            user.setUpdatedAt(LocalDateTime.now());
            // Lưu lại đối tượng user đã cập nhật
            return userRepository.save(user);
        });
    }

    public boolean deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
