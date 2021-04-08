package com.pinder.app.util;

import android.os.Build;

import com.pinder.app.models.Card;
import com.pinder.app.models.TagsObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingFunctions {
    public static List<TagsObject> sortTagsCollectionByDistance(List<TagsObject> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(TagsObject::getmDistance));
        } else {
            Collections.sort(list, new Comparator<TagsObject>() {
                public int compare(TagsObject o1, TagsObject o2) {
                    if (Double.parseDouble(o1.getmDistance()) == Double.parseDouble(o2.getmDistance()))
                        return 0;
                    return Double.parseDouble(o1.getmDistance()) < Double.parseDouble(o2.getmDistance()) ? -1 : 1;
                }
            });
        }
        return list;
    }

    public static ArrayList<Card> sortCollectionByLikesMeThenDistance(ArrayList<Card> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Card::isLikesMe).reversed().thenComparing(Card::getDistance));
        } else {
            Collections.sort(list, new Comparator<Card>() {
                public int compare(Card o1, Card o2) {
                    Boolean x1 = o1.isLikesMe();
                    Boolean x2 = o2.isLikesMe();
                    int sComp = x2.compareTo(x1);
                    if (sComp != 0) {
                        return sComp;
                    }
                    return o1.getDistance() < o2.getDistance() ? -1 : 1;
                }
            });
        }
        return list;
    }

    public static ArrayList<Card> sortCollectionByLikesMe(ArrayList<Card> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(list, Comparator.comparing(Card::isLikesMe).reversed());
        } else {
            Collections.sort(list, new Comparator<Card>() {
                public int compare(Card o1, Card o2) {
                    Boolean x1 = o1.isLikesMe();
                    Boolean x2 = o2.isLikesMe();
                    int sComp = x2.compareTo(x1);
                    return sComp;
                }
            });
        }
        return list;
    }
}
