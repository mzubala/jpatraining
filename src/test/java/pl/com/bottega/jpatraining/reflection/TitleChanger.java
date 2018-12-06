package pl.com.bottega.jpatraining.reflection;

import java.lang.reflect.Field;

public class TitleChanger {

    public void changeTitle(Object object, String newTitle) {
        try {
            Field title = object.getClass().getDeclaredField("title");
            title.setAccessible(true);
            title.set(object, newTitle);
            title.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            //nothing to do here
        }
    }

    public String getTitle(Object object) {
        try {
            Field title = object.getClass().getDeclaredField("title");
            title.setAccessible(true);
            String val = (String) title.get(object);
            title.setAccessible(false);
            return val;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
