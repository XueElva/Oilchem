package net.oilchem.communication.sms.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.oilchem.communication.sms.data.model.*;
import net.oilchem.communication.sms.util.IApi.API;

import java.lang.reflect.Type;

public class JsonUtil {
    public static Gson gson = null;

    private JsonUtil() {
    }

    private static Gson getInstance() {
        if (null == gson) {
            try {
                gson = new GsonBuilder()
                        .enableComplexMapKeySerialization()//.setPrettyPrinting()
                        .create();
            } catch (Exception ex) {
                gson = new GsonBuilder()
                        .create();
            } catch (Error e) {
                gson = new GsonBuilder()
                        .create();
            }
        }
        return gson;
    }

    public static OilResponseData getResponse(String json, API api) {
        OilResponseData data = null;
        switch (api) {
            case API_CONFIG:
                data = getInstance().fromJson(json, DataConfig.class);
                break;
            case API_WELCOME:
            case API_WELCOME_GET_BY_DAY:
                data = getInstance().fromJson(json, DataSmsList.class);
                break;
            case API_LOGIN:
                data = getInstance().fromJson(json, DataLogin.class);
                break;
            case API_LOGOUT:
                data = getInstance().fromJson(json, DataLogout.class);
                break;
            case API_NOTIFICATION:
                data = getInstance().fromJson(json, DataSmsList.class);
                break;
            case API_NOTIFICATION_CHANGE:
                data = getInstance().fromJson(json, DataChangeNotification.class);
                break;
            case API_REGISTER:
                data = getInstance().fromJson(json, DataRegister.class);
                break;
            case API_SMS_SEARCH:
                data = getInstance().fromJson(json, DataSmsList.class);
                break;
            case API_UPGRADE:
                data = getInstance().fromJson(json, DataUpgrade.class);
                break;
            case API_GET_CATEGORIES:
                data = getInstance().fromJson(json, DataCategories.class);
                break;
            case API_CERTIFICATION_CODE:
                data = getInstance().fromJson(json, DataAuthCode.class);
                break;
            case API_PHONECODE:
                data = getInstance().fromJson(json, DataPhoneCode.class);
                break;
            case API_PUSHREPLY:
                data = getInstance().fromJson(json, DataReply.class);
                break;
            case API_GET_REPLIES:
                data = getInstance().fromJson(json, DataReply.class);
                break;
            default:
                break;
        }
        return data;
    }

    public static <T> T fromJson(String objStr, Type type) {
        return getInstance().fromJson(objStr, type);
    }

    public static String toJson(Object obj, Type type) {
        return getInstance().toJson(obj, type);
    }
}
