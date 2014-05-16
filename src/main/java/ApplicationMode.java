import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get application mode from os environment or system property.
 */
public enum ApplicationMode {
    PROD, TEST, DEV;

    public static Logger logger = LoggerFactory.getLogger(ApplicationMode.class);

    public final static String ENV_MODE_KEY = "ENV_CONFIG";

    public static ApplicationMode getApplicationMode() {
        String appMode = System.getenv(ENV_MODE_KEY);
        if (appMode == null) {
            appMode = System.getProperty(ENV_MODE_KEY);
        }
        if (appMode == null) {
            logger.warn(ENV_MODE_KEY + " not defined in environment, defaulting to " + DEV);
            return DEV;
        }
        for (ApplicationMode m : values()) {
            if (m.toString().equals(appMode)) {
                logger.info(String.format("Running in %s mode", appMode));
                return m;
            }
        }

        logger.error("Unknown " + ENV_MODE_KEY + ": " + appMode);
        return null;
    }
}
