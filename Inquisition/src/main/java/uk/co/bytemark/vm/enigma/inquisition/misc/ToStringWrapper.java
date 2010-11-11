package uk.co.bytemark.vm.enigma.inquisition.misc;

public class ToStringWrapper<T> {

    private final T wrappedObject;

    private final String toString;

    public ToStringWrapper(T wrappedObject, String toString) {
        this.wrappedObject = wrappedObject;
        this.toString = toString;
    }

    public T getWrappedObject() {
        return wrappedObject;
    }

    public String getToString() {
        return toString;
    }

    @Override
    public String toString() {
        return toString;
    }
}
