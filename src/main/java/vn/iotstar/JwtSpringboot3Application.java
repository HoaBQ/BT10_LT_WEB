package vn.iotstar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@SpringBootApplication
public class JwtSpringboot3Application {

    public static void main(String[] args) {
        SpringApplication.run(JwtSpringboot3Application.class, args);
    }

    @Bean
    CommandLineRunner initDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = userRepository.findByEmail("admin@gmail.com").orElse(new User());
            admin.setEmail("admin@gmail.com");
            admin.setUsername("admin");
            admin.setFullName("Quản trị viên (Admin)");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setImages("https://cdn-icons-png.flaticon.com/512/847/847969.png");
            userRepository.save(admin);
            
            System.out.println("==============================================================");
            System.out.println(">>> ĐÃ CẬP NHẬT TÀI KHOẢN MẪU VÀ ICON:");
            System.out.println(">>> Email:    admin@gmail.com");
            System.out.println(">>> Username: admin");
            System.out.println(">>> Password: 123456");
            System.out.println(">>> Avatar:   https://cdn-icons-png.flaticon.com/512/847/847969.png");
            System.out.println("==============================================================");
        };
    }
}
