package pt.isel.pdm.li51n.g4.tmdbisel.data.models.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Attributes used to create DB schema
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attributes {

    /**
     * Primary Key attribute
     *
     * @return true if field is primary key, false otherwise
     */
    boolean primaryKey() default false;

    /**
     * Unique attribute
     * @return true if field is unique, false otherwise
     */
    boolean unique() default false;

    /**
     * Field don't be mapped to DB Schema
     * @return true if field should not to be used in DB Schema, false otherwise
     */
    boolean notMapped() default false;

    /**
     * Foreign table referenced by this field
     * @return name of the foreign table
     */
    String foreignTable() default "";

    /**
     * Used in DB Schema to identify autoincrement field
     * @return true if field should to be used has autoincrement, false otherwise
     */
    boolean autoIncrement() default false;

    /**
     * Used to identify a Composite Key on DB Schema
     * @return the Composite Key to be used in DB Schema
     */
    String compositeKey() default "";

    /**
     * Used to identify if this field will be used as Default in query sort
     * @return true fi field will be used as sort, false otherwise
     */
    boolean sortOrderDefault() default false;
}