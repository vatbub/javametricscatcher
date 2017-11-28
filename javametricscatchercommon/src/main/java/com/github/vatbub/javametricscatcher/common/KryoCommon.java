package com.github.vatbub.javametricscatcher.common;

/*-
 * #%L
 * javametricscatcher.common
 * %%
 * Copyright (C) 2017 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

public abstract class KryoCommon {
    private KryoCommon(){
        // TODO: Check what exception shall be thrown here
        throw new IllegalStateException("Class may not be instantiated");
    }

    public static void registerClasses(Kryo kryo){
        kryo.register(ExceptionMessage.class, new JavaSerializer());
        kryo.register(MetricHistory.class, new JavaSerializer());
        kryo.register(MetricsUpdateRequest.class, new JavaSerializer());
        kryo.register(MetricsUpdateResponse.class, new JavaSerializer());

        kryo.register(Integer.class, new JavaSerializer());
        kryo.register(Long.class, new JavaSerializer());
    }
}
