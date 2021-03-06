package dev.fuxing.postgres;

import com.fasterxml.jackson.databind.type.CollectionType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;

/**
 * Created by: Fuxing
 * Date: 2019-08-08
 * Time: 17:15
 */
public class PojoCollectionUserType<F extends Collection, B> extends PojoUserType<F> {

    protected final CollectionType type;

    public PojoCollectionUserType(Class<F> collectionClass, Class<B> baseClass) {
        super(collectionClass);
        this.type = Mapper.getTypeFactory().constructCollectionType(collectionClass, baseClass);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        final String cellContent = rs.getString(names[0]);
        if (cellContent == null) {
            return null;
        }
        try {
            byte[] bytes = cellContent.getBytes(StandardCharsets.UTF_8);
            return Mapper.readValue(bytes, type);
        } catch (Exception ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(index, Types.OTHER);
            return;
        }
        try {
            final StringWriter w = new StringWriter();
            Mapper.writerFor(type).writeValue(w, value);
            w.flush();
            ps.setObject(index, w.toString(), Types.OTHER);
        } catch (Exception ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public F deepCopy(Object value) throws HibernateException {
        try {
            byte[] bytes = Mapper.writerFor(type).writeValueAsBytes(value);
            return Mapper.readValue(bytes, type);
        } catch (IOException ex) {
            throw new HibernateException(ex);
        }
    }
}
