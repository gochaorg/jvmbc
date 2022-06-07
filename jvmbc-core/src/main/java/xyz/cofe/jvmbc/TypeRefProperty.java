package xyz.cofe.jvmbc;

import java.util.Optional;

public interface TypeRefProperty {
    int getTypeRef();
    void setTypeRef(int typeRef);

    Optional<String> getTypePath();
    void setTypePath(Optional<String> typePath);
}
