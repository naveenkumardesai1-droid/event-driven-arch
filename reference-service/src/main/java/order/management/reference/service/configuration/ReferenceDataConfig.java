package order.management.reference.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.order.management.common.model.User;

import order.management.reference.service.PolledCache;
import order.management.reference.service.UserCache;
import order.management.reference.service.service.UserService;

@Configuration
public class ReferenceDataConfig {
    @Bean
    public PolledCache<String, User> userCache(UserService userService) {
        return new UserCache(userService::findAll, user -> user.id());
    }
}
