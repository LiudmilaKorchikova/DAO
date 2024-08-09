package hexlet.code;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public void saveUser(User user) throws SQLException {
        if (user.getId() == null) {
            var sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getPhone());
                ps.executeUpdate();
                var generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
            }
        } else {
            var sql = "UPDATE users SET username=?, phone=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getPhone());
                ps.setLong(3, user.getId());
                ps.executeUpdate();
            }
        }
    }

    public Optional<User> find(Long id) throws SQLException {
        var sql = "SELECT * FROM users WHERE id=?";
           try (PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setLong(1, id);
           var result = ps.executeQuery();
           if (result.next()) {
                var username = result.getString("username");
                var phone = result.getString("phone");
                var user1 = new User(username, phone);
                user1.setId(id);
                return Optional.of(user1);
           }
           return Optional.empty();

        }
    }
    public void deleteUser(Long id) throws SQLException {
        var sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
