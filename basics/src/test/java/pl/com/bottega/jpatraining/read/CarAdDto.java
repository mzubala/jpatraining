package pl.com.bottega.jpatraining.read;

import java.util.Objects;

public class CarAdDto {

    Long id;
    String brand, model;

    public CarAdDto(Long id, String brand, String model) {
        this.id = id;
        this.brand = brand;
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarAdDto carAdDto = (CarAdDto) o;
        return Objects.equals(id, carAdDto.id) &&
            Objects.equals(brand, carAdDto.brand) &&
            Objects.equals(model, carAdDto.model);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, brand, model);
    }
}
