package com.github.vatbub.javametricscatcher.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

public abstract class KryoCommon {
    private KryoCommon(){
        // TODO: Check what exception shall be thrown here
        throw new IllegalStateException("Class may not be instantiated");
    }

    public static void registerClasses(Kryo kryo){
        kryo.register(MetricsUpdateRequest.class, new JavaSerializer());
    }
}
