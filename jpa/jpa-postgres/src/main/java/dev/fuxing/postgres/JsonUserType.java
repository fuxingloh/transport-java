package dev.fuxing.postgres;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created By: Fuxing Loh
 * Date: 5/1/2017
 * Time: 4:06 PM
 * Project: v22-transport
 */
public class JsonUserType extends PojoUserType<JsonNode> {

    public JsonUserType() {
        super(JsonNode.class);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        final String cellContent = rs.getString(names[0]);
        if (cellContent == null) {
            return null;
        }
        try {
            // Map from bytes[] to JsonNode
            return Mapper.readTree(cellContent.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new HibernateException(ex);
        }
    }

    /**
     * Deep copy JsonNode by serializing to bytes with jackson then back to JsonNode
     *
     * @param value object
     * @return Deep Copy
     * @throws HibernateException IOException from mapper read tree
     */
    @Override
    public JsonNode deepCopy(Object value) throws HibernateException {
        try {
            byte[] bytes = Mapper.writeValueAsBytes(value);
            return Mapper.readTree(bytes);
        } catch (IOException ex) {
            throw new HibernateException(ex);
        }
    }
}
