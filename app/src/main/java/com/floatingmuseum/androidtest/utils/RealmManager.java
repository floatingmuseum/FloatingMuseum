package com.floatingmuseum.androidtest.utils;

import com.floatingmuseum.androidtest.functions.catchtime.AppTimeUsingInfo;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by Floatingmuseum on 2017/3/20.
 */

public class RealmManager {

    public static void insertOrUpdate(final RealmModel model) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(model);
            }
        });
    }

    public static void updateEndTimeAndUsingTime(long startTime, long endTime) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

            }
        });
    }

    public static RealmResults<? extends RealmModel> query(final Class<? extends RealmModel> clazz) {
        RealmResults<?> results = Realm.getDefaultInstance()
                .where(clazz)
                .findAll();
        return results;
    }
}
