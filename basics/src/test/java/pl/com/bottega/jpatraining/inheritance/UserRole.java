package pl.com.bottega.jpatraining.inheritance;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Clock;

public abstract class UserRole implements User {

    private UserCore userCore;

    private Long id;

    public UserRole(UserCore userCore) {
        this.userCore = userCore;
    }

    UserRole() {}

    @Override
    public <T extends UserRole> T getRole(Class<T> roleClass) {
        return userCore.getRole(roleClass);
    }

    @Override
    public void addRole(UserRole userRole) {
        userCore.addRole(userRole);
    }

    @Override
    public void changePassword(String newPassword) {
        userCore.changePassword(newPassword);
    }

    @Override
    public void saveLastLoginDate(Clock clock) {
        userCore.saveLastLoginDate(clock);
    }

    void setUserCore(UserCore userCore) {
        this.userCore = userCore;
    }
}
