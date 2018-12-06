package pl.com.bottega.jpatraining.read;

import java.math.BigDecimal;

public class CarAdQuery {

    String brand, model;
    Integer productionYearFrom, productionYearTo;
    Integer firstRegistrationYearFrom, firstRegistrationYearTo;
    boolean damaged;
    boolean firstOwner;
    Fuel fuel;
    BigDecimal priceFrom, priceTo;

    Integer perPage = 20, page = 1;

}
