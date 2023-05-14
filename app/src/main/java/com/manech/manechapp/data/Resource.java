package com.manech.manechapp.data;

import com.manech.manechapp.data.images.StoreImage;

import java.util.ArrayList;
import java.util.List;

public class Resource extends ManechEntity {
    private String uri;

    public Resource(String uri) {
        this.uri = uri;
    }



    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public static List<Resource> castList(List<? extends Resource> list) {
        List<Resource> castList = new ArrayList<>();
        if (list == null || list.isEmpty()) return castList;
        for (Resource element : list) {
            castList.add(element);
        }
        return castList;
    }

    public static <T extends Resource> List<T> castList(List<Resource> list, Class<T> clazz) {
        List<T> castList = new ArrayList<>();
        if (list == null || list.isEmpty()) return castList;
        for (Resource element : list) {
            if (clazz.isInstance(element)) {
                castList.add(clazz.cast(element));
            }
        }
        return castList;
    }
}
