package pt.isel.pdm.li51n.g4.tmdbisel.data.models.schema;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.ReflectionHelper;

/**
 * Class to be used on creation of the DB Schema
 *
 * @param <T>
 */
public class TableSchema<T> {

    /**
     * TAG to be used in logs
     */
    private static final String TAG = TableSchema.class.getSimpleName();

    private Class<T> type;
    private String table;
    private String[] mColumns;
    private String mSortOrderDefault;

    /**
     * Default constructor
     * @param type of the class to be used
     */
    public TableSchema(Class<T> type) {
        this.type = type;
        table = type.getSimpleName();
    }

    /**
     * Private method to get columns name
     * @param type to be used
     * @param prefix prefix to be used on each column name
     * @return Array of Strings with all columns name
     */
    private static String[] getColumns(Class<?> type, String prefix) {
        Log.d(TAG, "getColumns");
        Iterable<Field> fields = ReflectionHelper.getFieldsUpTo(type, Object.class);

        List<String> columns = new ArrayList<>();
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(Attributes.class);
            Attributes attr = (Attributes) annotation;

            if (attr != null && attr.notMapped()) {
                continue;
            }

            String columnName = prefix + (attr != null && attr.primaryKey() ? "_" : "") + field.getName();
            if (columnName.equals("id")) {
                columnName = "_id";
            }

            if (!String.class.isAssignableFrom(field.getType()) &&
                    !Integer.class.isAssignableFrom(field.getType()) &&
                    !Double.class.isAssignableFrom(field.getType())) {
                Collections.addAll(columns, getColumns(field.getType(), field.getName()));
            } else {
                columns.add(columnName);
            }
        }
        return columns.toArray(new String[columns.size()]);
    }

    /**
     * Data Description Language (DDL) to be used on drop DB table
     * @return statement to drop DB table
     */
    public String getDropTableDDL() {
        Log.d(TAG, "getDropTableDDL => " + table);
        return "DROP TABLE IF EXISTS " + table;
    }

    /**
     * Data Description Language (DDL) to be used on create DB table
     * @return statement to create DB table
     */
    public String getCreateTableDDL() {
        Log.d(TAG, "getCreateTableDDL => " + table);
        StringBuilder queryBuilder = new StringBuilder();

        try {
            Iterable<Field> fields = ReflectionHelper.getFieldsUpTo(type, Object.class);
            queryBuilder.append("CREATE TABLE ").append(table).append(" (");

            queryBuilder.append(getColumnsDDL(fields));

            queryBuilder.append(");");
        } catch (Exception e) {
            Logger.e(e);
        }

        return queryBuilder.toString();
    }

    /**
     * Get columns name
     * @return Array of Strings with all columns name
     */
    public String[] getColumns() {
        Log.d(TAG, "getColumns => " + table);

        if (mColumns != null) {
            return mColumns;
        }

        mColumns = getColumns(type, "");
        return mColumns;
    }

    /**
     * Data Description Language (DDL) to be used on create DB
     * @param fields fields
     * @return statement of DB columns
     */
    public String getColumnsDDL(Iterable<Field> fields) {
        Log.d(TAG, "getColumnsDDL => " + table);
        return getColumnsDDL(fields, "");
    }

    /**
     * Data Description Language (DDL) to be used on create DB
     * @param fields fields
     * @param prefix to be used on fields
     * @return string with columns information on DDL
     */
    private String getColumnsDDL(Iterable<Field> fields, String prefix) {
        Log.d(TAG, "getColumnsDDL => " + table);
        boolean firstField = true;
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder lastLines = new StringBuilder();

        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(Attributes.class);
            Attributes attr = (Attributes) annotation;

            if (attr != null && attr.notMapped()) {
                if (!attr.compositeKey().equals("")) {
                    lastLines.append(String.format(
                            ", PRIMARY KEY(%s)",
                            attr.compositeKey()
                    ));
                }
                continue;
            }

            if (!firstField) {
                queryBuilder.append(", ");
            }

            String columnName = (attr != null && attr.primaryKey() ? "_" : "") + field.getName();
            if (columnName.equals("id")) {
                columnName = "_id";
            }

            if (String.class.isAssignableFrom(field.getType())) {
                queryBuilder.append(prefix);
                queryBuilder.append(columnName).append(" ");
                queryBuilder.append("TEXT");
            } else if (Integer.class.isAssignableFrom(field.getType())) {
                queryBuilder.append(prefix);
                queryBuilder.append(columnName).append(" ");
                queryBuilder.append("INTEGER");
            } else if (Double.class.isAssignableFrom(field.getType())) {
                queryBuilder.append(prefix);
                queryBuilder.append(columnName).append(" ");
                queryBuilder.append("REAL");
            } else {
                Iterable<Field> fieldFields = ReflectionHelper.getFieldsUpTo(field.getType(), Object.class);
                queryBuilder.append(getColumnsDDL(fieldFields, field.getName()));
                continue;
            }

            if (annotation != null && attr != null) {
                if (attr.primaryKey()) {
                    queryBuilder.append(" PRIMARY KEY");
                    if (attr.autoIncrement()) {
                        queryBuilder.append(" AUTOINCREMENT");
                    }
                } else if (attr.unique()) {
                    queryBuilder.append(" UNIQUE");
                } else if (!attr.foreignTable().equals("")) {
                    lastLines.append(String.format(
                            ", FOREIGN KEY(%s) REFERENCES %s(%s)",
                            columnName,
                            attr.foreignTable(),
                            "_id"
                    ));
                }
                if (attr.sortOrderDefault()) {
                    mSortOrderDefault = columnName;
                }
            }

            firstField = false;
        }
        queryBuilder.append(lastLines.toString());
        return queryBuilder.toString();
    }

    /**
     * Get table name
     * @return table name
     */
    public String getTableName() {
        Log.d(TAG, "getTableName => " + table);
        return table;
    }

    /**
     * Get default sort order
     * @return sort order
     */
    public String getSortOrderDefault() {
        Log.d(TAG, "getSortOrderDefault => " + table);
        return mSortOrderDefault;
    }

    /**
     * Get columns name separated with comma
     * @return columns name separated with comma
     */
    public String getColumnsString() {
        Log.d(TAG, "getColumnsString => " + table);
        StringBuilder sb = new StringBuilder();
        for(String column : getColumns()){
            String aux = table+"."+column+", ";
            sb.append(aux);
        }
        String res = sb.toString();
        res = res.substring(0, res.lastIndexOf(", "));
        return res;
    }
}
