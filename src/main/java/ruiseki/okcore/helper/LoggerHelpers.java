package ruiseki.okcore.helper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;

/**
 * Logger that will be used in this mod.
 *
 * @author rubensworks
 *
 */
public class LoggerHelpers {

    @Getter
    private final Logger logger;

    /**
     * Initialize the logger.
     *
     * @param modName The mod name.
     */
    public LoggerHelpers(String modName) {
        logger = LogManager.getLogger(modName);
    }

    /**
     * Log a new message with support for placeholders {}.
     *
     * @param logLevel The level to log at.
     * @param message  The message to log.
     */
    public void log(Level logLevel, String message) {
        logger.log(logLevel, message);
    }

    /**
     * Log a new message with support for placeholders {}.
     *
     * @param logLevel The level to log at.
     * @param message  The message to log (can contain {}).
     * @param params   Parameters to replace in the message.
     */
    public void log(Level logLevel, String message, Object... params) {
        logger.log(logLevel, message, params);
    }
}
