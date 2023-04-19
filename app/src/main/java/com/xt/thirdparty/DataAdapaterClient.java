package com.xt.thirdparty;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataAdapaterClient {

    Map<String, DataChangedListener> listenerMap = new HashMap<>();

    public static DataAdapaterClient getInstance() {
        return DataAdapaterClient.SingletonHolder.SINGLETON;
    }

    private static class SingletonHolder {
        private static final DataAdapaterClient SINGLETON = new DataAdapaterClient();

        private SingletonHolder() {
        }
    }


    public void registerDataNotifyListener(String key, DataChangedListener listener) {
        listenerMap.put(key, listener);
    }

    public void unRegisterDataNotifyListener(DataChangedListener listener) {
        Iterator<DataChangedListener> iterator = listenerMap.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == listener) {
                iterator.remove();
                break;
            }
        }
    }


    public interface DataChangedListener {
        void onDataChanged(String var1, String var2);
    }
}
