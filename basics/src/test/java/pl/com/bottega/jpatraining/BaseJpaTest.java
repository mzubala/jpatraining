package pl.com.bottega.jpatraining;

import org.junit.jupiter.api.BeforeEach;

public class BaseJpaTest {

    protected final EntityManagerTemplate template = new EntityManagerTemplate();

    @BeforeEach
    public void cleanDb() {
        template.cleanDb();
    }

}
