package com.server.constants;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public class Configurations {

    public static final String NETWORK_SERVER_TCP_PORT                    = "network-server-tcp-port";
    public static final String NETWORK_SERVER_WS_PORT                     = "network-server-ws-port";

    public static final String RABBITMQ_HOST                              = "rabbitmq-host";
    public static final String RABBITMQ_USERNAME                          = "rabbitmq-username";
    public static final String RABBITMQ_PASSWORD                          = "rabbitmq-password";

    public static final String APP_ID                                     = "app-id";
    public static final String GAME_SERVER_VERSION                        = "game-server-version";
    public static final String USER_TTL                                   = "user-ttl";

    public static final String USER_CONTROL_EXCHANGE_FORMAT               = "%s.v%s.user.control";

    public static Config getConfiguration() {
        return ConfigFactory.load("server.conf").getConfig(getEnvironment());
    }

    public static String getEnvironment() {
        return System.getProperty("env") == null ? "development" : System.getProperty("env");
    }

    public static Integer getNetworkServerTcpPort() {
        return getConfiguration().getInt(NETWORK_SERVER_TCP_PORT);
    }

    public static Integer getNetworkServerWsPort() {
        return getConfiguration().getInt(NETWORK_SERVER_WS_PORT);
    }

    public static String getRabbitMqHost() {
        return getConfiguration().getString(RABBITMQ_HOST);
    }

    public static String getRabbitMqUser() {
        return getConfiguration().getString(RABBITMQ_USERNAME);
    }

    public static String getRabbitMqPassword() {
        return getConfiguration().getString(RABBITMQ_PASSWORD);
    }

    public static String getAppId() {
        return getConfiguration().getString(APP_ID);
    }

    public static String getGameServerVersion() {
        return getConfiguration().getString(GAME_SERVER_VERSION);
    }

    public static String getUserControlExchange() {
        return String.format(USER_CONTROL_EXCHANGE_FORMAT, getAppId(), getGameServerVersion());
    }

    public static long getUserTTL() {
        return getConfiguration().getLong(USER_TTL);
    }
}
