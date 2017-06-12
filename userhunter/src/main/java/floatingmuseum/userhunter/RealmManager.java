package floatingmuseum.userhunter;

import floatingmuseum.userhunter.utils.ListUtil;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by Floatingmuseum on 2017/6/12.
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

    public static void delete(final long startTime) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<AppTimeUsingInfo> results = realm.where(AppTimeUsingInfo.class)
                        .equalTo("startTime", startTime)
                        .findAll();
                if (ListUtil.isEmpty(results)) {
                    results.deleteAllFromRealm();
                }
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
