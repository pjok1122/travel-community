package project.board.configuration;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import project.board.filter.RequestAttributeUserSettingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean userSettingFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        RequestAttributeUserSettingFilter filter = new RequestAttributeUserSettingFilter();
        registration.setFilter(filter);
        registration.setOrder(1);

        return registration;
    }
}
