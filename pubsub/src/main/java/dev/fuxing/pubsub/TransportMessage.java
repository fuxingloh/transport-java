package dev.fuxing.pubsub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.annotation.Nullable;

/**
 * Added this interface to add: <br>
 * JsonIgnoreProperties: so that it won't break when new data is added.
 * <p>
 * Created by: Fuxing
 * Date: 2019-04-24
 * Time: 18:53
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface TransportMessage {

    /**
     * Best to implement so the subscriber can ignore messages that they don't understand.
     *
     * @return optional version of the message
     */
    @Nullable
    default String getVersion() {
        return null;
    }
}
