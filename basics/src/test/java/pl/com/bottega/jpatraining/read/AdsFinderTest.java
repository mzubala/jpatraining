package pl.com.bottega.jpatraining.read;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.com.bottega.jpatraining.BaseJpaTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AdsFinderTest extends BaseJpaTest {

    private AdsFinder sut = new AdsFinder(template.getEntityManager());

    @BeforeEach
    public void setup() {
        // given
        testData();
    }

    @Test
    public void findsBrands() {
        assertThat(sut.brand("BMW").name).isEqualTo("BMW");
        assertThat(sut.brand("Volvo").name).isEqualTo("Volvo");
        assertThat(sut.brand("XXXX")).isNull();
    }

    @Test
    public void findsModels() {
        assertThat(sut.model("BMW", "X3").name).isEqualTo("X3");
        assertThat(sut.model("Volvo", "XC60").name).isEqualTo("XC60");
        assertThat(sut.model("X", "YZ")).isNull();
    }

    @Test
    public void countsAdsByBrand() {
        // when
        int bmwCount = sut.countByBrand(sut.brand("BMW"));
        int vwCount = sut.countByBrand(sut.brand("VW"));
        int volvoCount = sut.countByBrand(sut.brand("Volvo"));

        // then
        assertThat(bmwCount).isEqualTo(5);
        assertThat(vwCount).isEqualTo(4);
        assertThat(volvoCount).isEqualTo(2);
    }

    @Test
    public void findsByModelAndPrice() {
        // when
        List<CarAd> res1 = sut.findByModelAndPrice(sut.model("BMW", "X3"), new BigDecimal(100000), new BigDecimal(200000));
        List<CarAd> res2 = sut.findByModelAndPrice(sut.model("VW", "Polo"), new BigDecimal(16000), new BigDecimal(17000));

        // then
        assertThat(res1.stream().map(c -> c.id)).containsExactly(5L);
        assertThat(res2.stream().map(c -> c.id)).containsExactly(9L);
    }

    @Test
    public void searchesAdsByFirstOwnerFuelAndDamaged() {
        // given
        CarAdQuery query = new CarAdQuery();
        query.firstOwner = true;
        query.fuel = Fuel.PETROL;
        query.damaged = false;

        // when
        CarAdSearchResults results = sut.search(query);

        // then
        assertThat(results.ads).containsExactly(new CarAdDto(5L, "BMW", "X3"));
    }

    @Test
    public void searchesAdsByBrandAndFuel() {
        // given
        CarAdQuery query = new CarAdQuery();
        query.fuel = Fuel.PETROL;
        query.brand = "VW";

        // when
        CarAdSearchResults results = sut.search(query);

        // then
        assertThat(results.ads).containsExactly(
            new CarAdDto(6L, "VW", "Passat"),
            new CarAdDto(8L, "VW", "Polo")
        );
    }

    @Test
    public void paginatesSearchResults() {
        // given
        CarAdQuery query = new CarAdQuery();
        query.page = 3;
        query.perPage = 2;

        // when
        CarAdSearchResults results = sut.search(query);

        // then
        assertThat(results.ads).containsExactly(
            new CarAdDto(5L, "BMW", "X3"),
            new CarAdDto(6L, "VW", "Passat")
        );
        assertThat(results.pageNumber).isEqualTo(3);
        assertThat(results.pagesCount).isEqualTo(6);
        assertThat(results.perPage).isEqualTo(2);
    }

    private void testData() {
        Brand bmw = new Brand("BMW");
        bmw.models.add(new Model("X1", bmw));
        bmw.models.add(new Model("X3", bmw));
        bmw.models.add(new Model("X5", bmw));
        Brand vw = new Brand("VW");
        vw.models.add(new Model("Passat", vw));
        vw.models.add(new Model("Golf", vw));
        vw.models.add(new Model("Polo", vw));
        Brand volvo = new Brand("Volvo");
        volvo.models.add(new Model("XC60", volvo));
        volvo.models.add(new Model("S60", volvo));
        volvo.models.add(new Model("S90", volvo));
        volvo.models.add(new Model("XC90", volvo));

        List<CarAd> ads = Arrays.asList(
            new CarAd(1, bmw.model("X1"), 2014, 2014, false, false, Fuel.DIESEL, new BigDecimal(120000)),
            new CarAd(2, bmw.model("X1"), 2016, 2016, true, false, Fuel.PETROL, new BigDecimal(50000)),
            new CarAd(3, bmw.model("X1"), 2018, null, false, true, Fuel.DIESEL, new BigDecimal(250000)),
            new CarAd(4, bmw.model("X5"), 2010, 2010, false, false, Fuel.DIESEL, new BigDecimal(75000)),
            new CarAd(5, bmw.model("X3"), 2015, 2015, false, true, Fuel.PETROL, new BigDecimal(135000)),
            new CarAd(6, vw.model("Passat"), 2000, 2001, false, false, Fuel.PETROL, new BigDecimal(10000)),
            new CarAd(7, vw.model("Golf"), 2005, 2005, false, false, Fuel.DIESEL, new BigDecimal(12000)),
            new CarAd(8, vw.model("Polo"), 2007, 2007, false, false, Fuel.PETROL, new BigDecimal(15000)),
            new CarAd(9, vw.model("Polo"), 2008, 2008, false, false, Fuel.DIESEL, new BigDecimal(17000)),
            new CarAd(10, volvo.model("S60"), 2016, 20016, false, false, Fuel.PETROL, new BigDecimal(110000)),
            new CarAd(11, volvo.model("XC60"), 20018, 2018, false, true, Fuel.DIESEL, new BigDecimal(220000))
        );

        template.executeInTx((em) -> {
            em.persist(bmw);
            em.persist(vw);
            em.persist(volvo);
            ads.forEach((ad) -> {
                em.persist(ad);
            });
        });
    }

}
