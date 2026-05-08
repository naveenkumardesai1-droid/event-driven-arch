package order.management.reference.service;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import com.order.management.common.model.User;

public class UserCache extends PolledCache<String, User> {
    public UserCache(Supplier<Collection<User>> valueSupplier, Function<User, String> keyExtractor) {
        super(valueSupplier, keyExtractor);
    }
}
