package system.web.wicket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

import system.utils.ClassUtils;

public class WicketUtils {

    public static Component findComponent(Iterator<Component> iterator, String id) {
        while (iterator.hasNext()) {
            Component component = iterator.next();

            if (component.getId().equals(id)) {
                return component;
            }
            if (component instanceof MarkupContainer) {
                component = findComponent((MarkupContainer) component, id);
                if (component != null) {
                    return component;
                }
            }

        }

        return null;
    }

    public static Component findComponent(MarkupContainer parent, String id) {
        return findComponent(parent.iterator(), id);
    }

//    public static <T> List<T> getChilds(String parentId, Class<T> clazz) {
//    	WebMarkupContainer parent = findComponent(parentId);
//        return getChilds(parent, clazz);
//    }

    public static <T> List<T> getChilds(MarkupContainer parent, Class<T> clazz) {
        List<T> lstComponent = new ArrayList<T>();
        Iterator<Component> iterator = parent.iterator();

        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (ClassUtils.isExtend(clazz, component.getClass())) {
                lstComponent.add(clazz.cast(component));
            } else if (component instanceof WebMarkupContainer) {
                List<T> subComponents = getChilds((WebMarkupContainer) component, clazz);
                if (!subComponents.isEmpty()) {
                    lstComponent.addAll(subComponents);
                }
            }
        }

        return lstComponent;
    }

}
