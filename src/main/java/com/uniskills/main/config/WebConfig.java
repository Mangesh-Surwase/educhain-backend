package com.uniskills.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 1. फोटो दाखवण्यासाठी जुने सेटिंग (जसेच्या तसे)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");
    }

    // 2. 🔥 नवीन CORS सेटिंग (Netlify साठी) 🔥
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // सगळ्या API ला लागू होईल
                .allowedOrigins(
                        "http://localhost:5173", // Localhost Frontend (Vite)
                        "http://localhost:3000", // Localhost Frontend (React)
                        "https://educhain-platform.netlify.app" // 🔥 तुझी Netlify लिंक (महत्त्वाची)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // सगळ्या प्रकारच्या रिक्वेस्ट
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}