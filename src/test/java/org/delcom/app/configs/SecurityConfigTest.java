package org.delcom.app.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock HttpSecurity http;
    @Mock ExceptionHandlingConfigurer<HttpSecurity> exceptionHandlingConfigurer;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock AuthenticationException authException;

    @Test
    void testSecurityConfig() throws Exception {
        SecurityConfig config = new SecurityConfig();

        // 1. Cek Password Encoder
        assertNotNull(config.passwordEncoder());

        // 2. Setup Mock "Santai" (Lenient) agar tidak error Stubbing
        // Kembalikan 'http' agar method chaining (.and().something()) tetap jalan
        lenient().when(http.csrf(any())).thenReturn(http);
        lenient().when(http.authorizeHttpRequests(any())).thenReturn(http);
        lenient().when(http.formLogin(any())).thenReturn(http);
        lenient().when(http.logout(any())).thenReturn(http);
        lenient().when(http.rememberMe(any())).thenReturn(http);
        lenient().when(http.sessionManagement(any())).thenReturn(http);

        // 3. JURUS RAHASIA: Membajak ExceptionHandling
        // Saat kode memanggil .exceptionHandling(...), kita tangkap isinya!
        when(http.exceptionHandling(any())).thenAnswer(invocation -> {
            // Ambil "Lambda" yang ada di dalam kodingan aslimu
            Customizer<ExceptionHandlingConfigurer<HttpSecurity>> customizer = 
                invocation.getArgument(0);
            
            // Paksa lambda itu jalan menggunakan object palsu kita
            customizer.customize(exceptionHandlingConfigurer);
            
            return http;
        });

        // 4. Jalankan method utama (securityFilterChain)
        config.securityFilterChain(http);

        // 5. Tangkap 'AuthenticationEntryPoint' yang baru saja didaftarkan oleh lambda tadi
        ArgumentCaptor<AuthenticationEntryPoint> captor = ArgumentCaptor.forClass(AuthenticationEntryPoint.class);
        verify(exceptionHandlingConfigurer).authenticationEntryPoint(captor.capture());

        // 6. TEMBAK! Jalankan method commence() secara manual
        // Ini seolah-olah ada user yang gagal login
        AuthenticationEntryPoint entryPoint = captor.getValue();
        entryPoint.commence(request, response, authException);

        // 7. Cek apakah baris merah (redirect) tereksekusi
        verify(response).sendRedirect("/auth/login");
    }
}