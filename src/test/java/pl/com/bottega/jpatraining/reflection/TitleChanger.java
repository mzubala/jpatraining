package pl.com.bottega.jpatraining.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TitleChanger {

    public void changeTitle(Object object, String newTitle) {

    }

    public String getTitle(Object object) {
        // TODO
        return null;
    }

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        Class<Auction> auctionClass = Auction.class;
        Class<Auction> auctionClass2 = (Class<Auction>) Class.forName("pl.com.bottega.jpatraining.reflection.Auction");

        Method[] auctionMethods = auctionClass.getMethods();
        Auction auction = new Auction("test");
        for (Method method : auctionMethods) {
            if (method.getParameterTypes().length == 0 && method.getName().contains("get")) {
                method.invoke(auction);
            }
        }

        Field[] auctionFields = auctionClass.getDeclaredFields();
        Field titleField = auctionClass.getDeclaredField("title");
        for (Field field : auctionFields) {
            field.setAccessible(true);
            field.set(auction, "new value");
            System.out.println(field.get(auction));
        }

        Constructor<Auction> auctionConstructor = auctionClass2.getDeclaredConstructor(String.class);
        Auction auctionCreatedWithReflection = auctionConstructor.newInstance("vaule");
        System.out.println(auctionCreatedWithReflection.getTitle());
    }

}
