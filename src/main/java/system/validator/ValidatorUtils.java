package system.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import system.cache.CacheManager;
import system.exception.ApplicationException;

public class ValidatorUtils {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    private static final Validator validator = factory.getValidator();

    private static final String cacheKey = "ValidatorUtils.Cache";

    // 検証
    public static <T> ConstraintViolation<T>[] validate(T bean) {
        Set<ConstraintViolation<T>> setViolation = validator.validate(bean);

        @SuppressWarnings("unchecked")
        ConstraintViolation<T>[] violation = setViolation.toArray(new ConstraintViolation[0]);

        if (violation.length > 0) {
            sort(bean.getClass().getName(), violation);
        }

        return violation;
    }

    private static <T> void sort(String className, ConstraintViolation<T>[] violation) {
        String key = cacheKey + ":" + className;
        if (!CacheManager.containsKey(key)) {
            CacheManager.put(key, new ConcurrentHashMap<String, List<Long>>());
        }

        @SuppressWarnings("unchecked")
        Map<String, List<Long>> mapCache = (Map<String, List<Long>>) CacheManager.get(key);

        for (ConstraintViolation<T> v : violation) {
            String path = v.getPropertyPath().toString();
            if (!mapCache.containsKey(path)) {
                mapCache.put(path, getIndex(v));
            }
        }

        Arrays.sort(violation, new Comparator<ConstraintViolation<T>>() {

            @Override
            public int compare(ConstraintViolation<T> o1, ConstraintViolation<T> o2) {
                List<Long> lstIndex1 = mapCache.get(o1.getPropertyPath().toString());
                List<Long> lstIndex2 = mapCache.get(o2.getPropertyPath().toString());
                int size1 = lstIndex1.size();
                int size2 = lstIndex2.size();

                for (int i = 0; i < size1; i++) {
                    long index1 = lstIndex1.get(i);
                    if (i < size2) {
                        long index2 = lstIndex2.get(i);
                        if (index1 < index2) {
                            return -1;
                        } else if (index1 > index2) {
                            return 1;
                        }
                    } else {
                        return 1;
                    }
                }

                if (size1 < size2) {
                    return -1;
                } else {
                    return 0;
                }
            }

        });
    }

    private static <T> List<Long> getIndex(ConstraintViolation<T> violation) {
        Object rootBean = violation.getRootBean();
        Object parentBean = rootBean;
        String[] paths = violation.getPropertyPath().toString().split("\\.", -1);
        List<Long> lstIndex = new ArrayList<>();

        for (int i = 0, len1 = paths.length; i < len1; i++) {

            Class<?> clazz = parentBean.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (int j = 0, len2 = fields.length; j < len2; j++) {

                Field field = fields[j];
                if (field.getName().equals(paths[i])) {
                    Order order = field.getAnnotation(Order.class);
                    if (order == null) {
                        lstIndex.add(0x80000000L + j);
                    } else {
                        lstIndex.add((long) order.order());
                    }
                    if (i < len1 - 1) {
                        field.setAccessible(true);
                        try {
                            parentBean = field.get(parentBean);
                        } catch (IllegalAccessException e) {
                            throw new ApplicationException(e);
                        }

                    }
                    break;
                }

            }

        }

        return lstIndex;
    }

}
