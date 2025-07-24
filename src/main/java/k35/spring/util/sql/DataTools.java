package k35.spring.util.sql;

import k35.sql.util.convertors.Transform;
import k35.sql.util.convertors.Transform.ExtractFunction;
import k35.sql.util.convertors.Transform.TransformFunction;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DataTools {

    /**
     * Защитим от создания экземпляров, будем использовать только статические методы.
     */
    private DataTools() {

    }

    @FunctionalInterface
    public interface PrepeareFunction<T> {
        SqlParameterSource apply(T o);
    }

    /**
     * Извлечение данных в коллекцию из ResultSet
     *
     * @param <T>
     * @param transform
     * @return
     */
    public static <T> ResultSetExtractor<List<T>> createResultSetExtractorFor(TransformFunction<T> transform) {
        return rs -> {

            final List<T> list = new ArrayList<>();

            while (rs.next()) {
                list.add(transform.apply(rs));
            }

            return list;
        };
    }

    /**
     * Преобразователь результата по функции
     */
    public static class RowMapperFor<T> implements RowMapper<T> {

        private final TransformFunction<T> transform;

        public RowMapperFor(TransformFunction<T> transform) {
            this.transform = transform;
        }

        @Override
        @Nullable
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            return this.transform.apply(rs);
        }

    }

    /**
     * Создать преобразователь записи
     *
     * @param <T>
     * @param transform
     * @return
     */
    public static <T> RowMapper<T> createRowMapperFor(final TransformFunction<T> transform) {
        return new RowMapperFor<>(transform);
    }

    /**
     * Создать преобразователь записи
     *
     * @param <T>
     * @param extractFunction
     * @return
     */
    public static <T> RowMapper<T> createRowMapperFor(final ExtractFunction<T> extractFunction) {
        return new RowMapperFor<>(Transform.of(extractFunction));
    }

}
