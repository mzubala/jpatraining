package pl.com.bottega.jpatraining;

import org.junit.Before;

public class BaseJpaTest {

    protected final EntityManagerTemplate template = new EntityManagerTemplate();

    @Before
    public void cleanDb() {
        template.cleanDb();
    }

}
