package com.serhatbalki.spring_boot_email_example.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class OtpUtil {

    public String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999999);
        String output = Integer.toString(randomNumber);

        while (output.length() < 7) {
            output = "0" + output;
        }
        return output;
    }
}
