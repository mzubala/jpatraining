package pl.com.bottega.jpatraining.inheritance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserCore implements User {

    @OneToMany(mappedBy = "userCore", cascade = CascadeType.ALL)
    private Set<UserRole> roles = new HashSet<>();

    @Id
    @GeneratedValue
    private Long id;

    private String login, password;

    private LocalDateTime lastLogin;

    UserCore() {
    }

    UserCore(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public <T extends UserRole> T getRole(Class<T> roleClass) {
        return (T) roles.stream().
                filter(role -> role.getClass().equals(roleClass)).
                findFirst().
                orElseThrow(() -> new RuntimeException("User has no such role"));
    }

    @Override
    public void addRole(UserRole userRole) {
        userRole.setUserCore(this);
        roles.add(userRole);
    }

    @Override
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public void saveLastLoginDate(Clock clock) {
        this.lastLogin = LocalDateTime.now(clock);
    }
}
