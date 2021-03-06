package dev.fuxing.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Created By: Fuxing Loh
 * Date: 10/3/2017
 * Time: 2:33 PM
 * Project: v22-transport
 */
public abstract class PojoUserType<T> implements UserType {

    protected static final ObjectMapper Mapper = new ObjectMapper();

    private final Class<T> returnedClass;

    public PojoUserType(Class<T> returnedClass) {
        this.returnedClass = returnedClass;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class<T> returnedClass() {
        return returnedClass;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if ((x == null) || (y == null)) {
            return false;
        }
        return x.equals(y);
    }

    /**
     * @throws HibernateException NPE if null
     */
    @Override
    public int hashCode(Object object) throws HibernateException {
        Objects.requireNonNull(object);
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        final String cellContent = rs.getString(names[0]);
        if (cellContent == null) {
            return null;
        }
        try {
            return Mapper.readValue(cellContent.getBytes(StandardCharsets.UTF_8), returnedClass);
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
            Mapper.writeValue(w, value);
            w.flush();
            ps.setObject(index, w.toString(), Types.OTHER);
        } catch (Exception ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public T deepCopy(Object value) throws HibernateException {
        try {
            byte[] bytes = Mapper.writeValueAsBytes(value);
            return Mapper.readValue(bytes, returnedClass);
        } catch (IOException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        Object deepCopy = deepCopy(value);

        if (!(deepCopy instanceof Serializable)) {
            throw new SerializationException(
                    String.format("deepCopy of %s is not serializable", value), null);
        }

        return (Serializable) deepCopy;
    }

    @Override
    public T assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public T replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
