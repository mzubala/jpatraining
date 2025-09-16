package pl.com.bottega.jpatraining.read;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;

public class AdsFinder {

    private final EntityManager entityManager;

    public AdsFinder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // zwraca Brand o zadanej nazwie lub null jezeli nie ma takiego
    public Brand brand(String name) {

        return null;
    }

    // zwraca Model o zadanej nazwie i nazwie marki lub null jezeli nie ma takiego
    public Model model(String brandName, String modelName) {
        return null;
    }

    // zwraca liczbę ogłoszeń sprzedaży danej marki
    public int countByBrand(Brand brand) {
        return 0;
    }

    // zwraca ogłoszenia sprzedaży danego modelu i w podanym przedziale cenowym
    public List<CarAd> findByModelAndPrice(Model model, BigDecimal from, BigDecimal to) {
        return null;
    }

    public CarAdSearchResults search(CarAdQuery query) {
        return null;
    }

}
