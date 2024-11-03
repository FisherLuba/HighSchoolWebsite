package com.fisherl.schoolwebsite;

import com.fisherl.schoolwebsite.permission.Permission;
import com.fisherl.schoolwebsite.permission.TemporaryPermissions;
import com.fisherl.schoolwebsite.permission.TemporaryPermissionsRepository;
import com.fisherl.schoolwebsite.post.question.LikeActionStringConverter;
import com.fisherl.schoolwebsite.util.SearchRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
@Configuration
@EnableWebSecurity
@EnableJpaRepositories(repositoryBaseClass = SearchRepositoryImpl.class)
public class SchoolWebsiteApplication implements WebMvcConfigurer {


    //    https://spring.io/guides/tutorials/spring-boot-oauth2/
    //    https://www.atlantic.net/dedicated-server-hosting/how-to-install-and-secure-postgresql-server-on-oracle-linux-8/
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SchoolWebsiteApplication.class, args);
        final String str = context.getEnvironment().getProperty("administrator.emails");
        if (str == null) {
            System.out.println("No default administrators found");
            return;
        }
        final TemporaryPermissionsRepository repository = context.getBean(TemporaryPermissionsRepository.class);
        for (String email : str.replace(",", " ").split("\\s+")) {
            repository.findById(email).ifPresentOrElse(perms -> System.out.println("Found administrator: " + email),
                    () -> {
                        System.out.println("Adding administrator: " + email);
                        final TemporaryPermissions permissions = TemporaryPermissions.of(
                                email,
                                Arrays.stream(Permission.values()).
                                        map(perm -> Map.entry(perm.toString(), true)).
                                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                        );
                        repository.save(permissions);
                    });

        }
    }

    @PostMapping("/user")
    public OAuth2User user(@AuthenticationPrincipal OAuth2User principal) {
        return principal;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(l -> l.logoutSuccessUrl("/").permitAll()
                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .oauth2Login();
        return http.build();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LikeActionStringConverter());
    }

    @Bean
    FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        final FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

//    @Autowired
//    private QuestionManager questionManager;

}
