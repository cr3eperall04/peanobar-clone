/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.davidemichelotti.peanobar.config;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 *
 * @author david
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer{
    
    @Autowired
    CustomAuthenticationManager authManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        AuthenticationFilter filter=new AuthenticationFilter();
        filter.setAuthenticationManager(authManager);
        
        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.POST,"/api/auth/login","/api/auth/newpw").permitAll()
                .antMatchers(HttpMethod.GET,"/api/auth/forgot").permitAll()
                .antMatchers("/*","/en/*", "/en/", "/en/assets/**").permitAll()
                .antMatchers("/it/*", "/it/", "/it/assets/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/img","/api/order/complete","/api/product","/api/user/balance").hasAnyRole("ADMIN","BAR")
                .antMatchers(HttpMethod.PUT, "/api/img").hasAnyRole("ADMIN","BAR")
                .antMatchers(HttpMethod.DELETE, "/api/img", "/api/product" ).hasAnyRole("ADMIN","BAR")
                .antMatchers(HttpMethod.PATCH, "/api/product").hasAnyRole("ADMIN","BAR")
                .antMatchers(HttpMethod.GET, "/it/bar/**","/en/bar/**", "/api/img/all", "/api/order/byuuid","/api/order/contents","/api/order","/api/order/toprocess","/api/user/classroom","/api/user/classrooms","/api/user/all","/api/user/byemail","/api/user/byusername","/api/user/byuuid","/api/user/count","/api/user/search","/api/order/count","/api/order/all").hasAnyRole("ADMIN","BAR")
                .antMatchers(HttpMethod.POST, "/api/order/send","/api/order/cart").hasAnyRole("ADMIN","USER")
                .antMatchers(HttpMethod.GET, "api/order/ownorders","api/order/countown").hasAnyRole("ADMIN","USER")
                .antMatchers(HttpMethod.POST,"/api/auth/revoke","/api/order","/api/user").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/it/admin/**","/en/admin/**","/api/user/cart","/api/user").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH,"/api/user","/api/user/classroom").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/user","/api/user/classroom").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().csrf().disable()
                .logout().disable()
                //.and().logout().logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))).and()
                .httpBasic().disable()
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(filter);
        return http.build();
    }

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/"
    };
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }
    
    public ITemplateResolver thymeleafTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("mail-templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }
    
    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("mail-templates/mailMessages");
        return messageSource;
    }
    
    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
        return templateEngine;
    }
    
}
