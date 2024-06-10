package com.serhatbalki.spring_boot_email_example.service;


import com.serhatbalki.spring_boot_email_example.dto.LoginDto;
import com.serhatbalki.spring_boot_email_example.dto.RegisterDto;
import com.serhatbalki.spring_boot_email_example.dto.ResetPasswordDto;
import com.serhatbalki.spring_boot_email_example.entity.User;
import com.serhatbalki.spring_boot_email_example.repository.UserRepository;
import com.serhatbalki.spring_boot_email_example.util.EmailUtil;
import com.serhatbalki.spring_boot_email_example.util.OtpUtil;
import jakarta.mail.MessagingException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserRepository userRepository;

    public String register(RegisterDto registerDto) {
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(registerDto.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "User registration successful please verify account in 3 minute";
    }

    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (3 * 60)) {
            user.setActive(true);
            userRepository.save(user);
            return "OTP verified you can login";
        }
        return "Please regenerate otp and try again";
    }

    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... please verify account within 3 minute";
    }

    public String login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("User not found with this email: " + loginDto.getEmail()));
        if (!loginDto.getPassword().equals(user.getPassword())) {
            return "Password is incorrect";
        } else if (!user.isActive()) {
            return "your account is not verified";
        }
        return "Login successful";
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        try {
            String passwordResetToken = UUID.randomUUID().toString();
            user.setPasswordResetToken(passwordResetToken);
            userRepository.save(user);
            emailUtil.sendSetPasswordEmail(email, passwordResetToken);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send set password email please try again");
        }
        return "Please check your email to set new password";
    }

    public String resetPassword(String email, ResetPasswordDto resetPasswordDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(resetPasswordDto.getPasswordResetToken())) {
            throw new RuntimeException("Password reset token or email not found");
        }
        user.setPasswordResetToken(null);
        user.setPassword(resetPasswordDto.getPassword());
        userRepository.save(user);
        return "Password reset successful login with new password";
    }
}