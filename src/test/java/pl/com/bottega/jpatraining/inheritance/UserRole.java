package pl.com.bottega.jpatraining.inheritance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.time.Clock;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.InheritanceType.JOINED;

@Entity
@Inheritance(strategy = JOINED)
public abstract class UserRole implements User {

    @ManyToOne
    private UserCore userCore;

    @Id
    @GeneratedValue(strategy = IDENTITY)
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
