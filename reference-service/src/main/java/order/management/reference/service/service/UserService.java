package order.management.reference.service.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.order.management.common.model.User;

@Service
public class UserService {
    private static final String SELECT_ALL_ORDERS_SQL = "SELECT * FROM user WHERE last_updated_at > :lastTimeProcessed";

    private LocalDateTime lastProcessedTimestamp = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());;
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        try {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("lastTimeProcessed", lastProcessedTimestamp);
            lastProcessedTimestamp = LocalDateTime.now();

            List<User> users = jdbcTemplate.query(SELECT_ALL_ORDERS_SQL, params, (rs, rowNum) -> new User(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("last_updated_at").toLocalDateTime()));
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }
    }
}
