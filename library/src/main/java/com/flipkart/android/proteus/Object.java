package com.flipkart.android.proteus;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Object
 *
 * @author aditya.sharat
 */

public class Object extends Value {

    private final HashMap<String, Value> members = new HashMap<>();

    @Override
    Object copy() {
        Object result = new Object();
        for (Map.Entry<String, Value> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().copy());
        }
        return result;
    }

    /**
     * Adds a member, which is a name-value pair, to self. The name must be a String, but the value
     * can be an arbitrary Value, thereby allowing you to build a full tree of Value
     * rooted at this node.
     *
     * @param property name of the member.
     * @param value    the member object.
     */
    public void add(String property, Value value) {
        if (value == null) {
            value = Null.INSTANCE;
        }
        members.put(property, value);
    }

    /**
     * Removes the {@code property} from this {@link Object}.
     *
     * @param property name of the member that should be removed.
     * @return the {@link Value} object that is being removed.
     * @since 1.3
     */
    public Value remove(String property) {
        return members.remove(property);
    }

    /**
     * Convenience method to add a primitive member. The specified value is converted to a
     * Primitive of String.
     *
     * @param property name of the member.
     * @param value    the string value associated with the member.
     */
    public void addProperty(String property, String value) {
        add(property, createValue(value));
    }

    /**
     * Convenience method to add a primitive member. The specified value is converted to a
     * Primitive of Number.
     *
     * @param property name of the member.
     * @param value    the number value associated with the member.
     */
    public void addProperty(String property, Number value) {
        add(property, createValue(value));
    }

    /**
     * Convenience method to add a boolean member. The specified value is converted to a
     * Primitive of Boolean.
     *
     * @param property name of the member.
     * @param value    the number value associated with the member.
     */
    public void addProperty(String property, Boolean value) {
        add(property, createValue(value));
    }

    /**
     * Convenience method to add a char member. The specified value is converted to a
     * Primitive of Character.
     *
     * @param property name of the member.
     * @param value    the number value associated with the member.
     */
    public void addProperty(String property, Character value) {
        add(property, createValue(value));
    }

    /**
     * Creates the proper {@link Value} object from the given {@code value} object.
     *
     * @param value the object to generate the {@link Value} for
     * @return a {@link Value} if the {@code value} is not null, otherwise a {@link Null}
     */
    private Value createValue(java.lang.Object value) {
        return value == null ? Null.INSTANCE : new Primitive(value);
    }

    /**
     * Returns a set of members of this object. The set is ordered, and the order is in which the
     * values were added.
     *
     * @return a set of members of this object.
     */
    public Set<Map.Entry<String, Value>> entrySet() {
        return members.entrySet();
    }

    /**
     * Returns the number of key/value pairs in the object.
     *
     * @return the number of key/value pairs in the object.
     */
    public int size() {
        return members.size();
    }

    /**
     * Convenience method to check if a member with the specified name is present in this object.
     *
     * @param memberName name of the member that is being checked for presence.
     * @return true if there is a member with the specified name, false otherwise.
     */
    public boolean has(String memberName) {
        return members.containsKey(memberName);
    }

    public boolean isPrimitive(String memberName) {
        return has(memberName) && get(memberName).isPrimitive();
    }

    public boolean isBoolean(String memberName) {
        if (has(memberName) && get(memberName).isPrimitive()) {
            return getAsPrimitive(memberName).isBoolean();
        }
        return false;
    }

    public boolean isNumber(String memberName) {
        if (has(memberName) && get(memberName).isPrimitive()) {
            return getAsPrimitive(memberName).isNumber();
        }
        return false;
    }

    public boolean isObject(String memberName) {
        return has(memberName) && get(memberName).isObject();
    }

    public boolean isArray(String memberName) {
        return has(memberName) && get(memberName).isArray();
    }

    public boolean isNull(String memberName) {
        return has(memberName) && get(memberName).isNull();
    }

    public boolean isLayout(String memberName) {
        return has(memberName) && get(memberName).isLayout();
    }

    /**
     * Returns the member with the specified name.
     *
     * @param memberName name of the member that is being requested.
     * @return the member matching the name. Null if no such member exists.
     */
    public Value get(String memberName) {
        return members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a Value.
     *
     * @param memberName name of the member being requested.
     * @return the Primitive corresponding to the specified member.
     */
    public Primitive getAsPrimitive(String memberName) {
        return (Primitive) members.get(memberName);
    }

    @Nullable
    public Boolean getAsBoolean(String memberName) {
        if (isBoolean(memberName)) {
            return getAsPrimitive(memberName).getAsBoolean();
        }
        return null;
    }

    @Nullable
    public Integer getAsInteger(String memberName) {
        if (isNumber(memberName)) {
            return getAsPrimitive(memberName).getAsInt();
        }
        return null;
    }

    @Nullable
    public Float getAsFloat(String memberName) {
        if (isNumber(memberName)) {
            return getAsPrimitive(memberName).getAsFloat();
        }
        return null;
    }

    @Nullable
    public Double getAsDouble(String memberName) {
        if (isNumber(memberName)) {
            return getAsPrimitive(memberName).getAsDouble();
        }
        return null;
    }

    @Nullable
    public Long getAsLong(String memberName) {
        if (isNumber(memberName)) {
            return getAsPrimitive(memberName).getAsLong();
        }
        return null;
    }

    @Nullable
    public String getAsString(String memberName) {
        if (isPrimitive(memberName)) {
            return getAsPrimitive(memberName).getAsString();
        }
        return null;
    }

    /**
     * Convenience method to get the specified member as a Array.
     *
     * @param memberName name of the member being requested.
     * @return the Array corresponding to the specified member.
     */
    public Array getAsArray(String memberName) {
        return (Array) members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a Object.
     *
     * @param memberName name of the member being requested.
     * @return the Object corresponding to the specified member.
     */
    public Object getAsObject(String memberName) {
        return (Object) members.get(memberName);
    }

    @Nullable
    public Layout getAsLayout(String memberName) {
        if (isLayout(memberName)) {
            return (Layout) members.get(memberName);
        }
        return null;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        return (o == this) || (o instanceof Object && ((Object) o).members.equals(members));
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }

}