package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.LoginRequest;
import com.workintech.twitter.dto.request.RegisterRequest;
import com.workintech.twitter.dto.response.AuthResponse;
import com.workintech.twitter.entity.Role;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exceptions.TwitterException;
import com.workintech.twitter.mapper.UserMapper;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new TwitterException(
                "Bu kullanıcı adı zaten kullanılıyor", HttpStatus.BAD_REQUEST);
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new TwitterException(
                "Bu email adresi zaten kullanılıyor", HttpStatus.BAD_REQUEST);
        }
        
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(
            token,
            "Kayıt başarılı",
            userMapper.toResponse(savedUser)
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new TwitterException(
                "Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
        
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(
            token,
            "Giriş başarılı",
            userMapper.toResponse(user)
        );
    }
}
