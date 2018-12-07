package pl.com.bottega.jpatraining.inheritance;

public class UserFactory {

    public User standardUser(String login, String pwd) {
        UserCore userCore = new UserCore(login, pwd);
        return userCore;
    }

    public User supervisorUser(String login, String pwd) {
        User user = standardUser(login, pwd);
        user.addRole(new InvoiceCorrectorRole());
        return user;
    }

    public User adminUser(String login, String pwd) {
        User user = supervisorUser(login, pwd);
        user.addRole(new OrderCorrectorRole());
        return user;
    }

}
