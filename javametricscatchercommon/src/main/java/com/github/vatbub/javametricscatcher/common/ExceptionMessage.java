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


import java.io.Serializable;

public class ExceptionMessage implements Serializable{
    private String exceptionName;
    private String message;
    private ExceptionMessage cause;

    public static ExceptionMessage fromThrowable(Throwable throwable) {
        ExceptionMessage res = new ExceptionMessage(throwable.getClass().getName(), throwable.getMessage());
        if (throwable.getCause() != null) {
            res.setCause(ExceptionMessage.fromThrowable(throwable.getCause()));
        }

        return res;
    }

    /**
     * For KryoNet
     */
    @SuppressWarnings("unused")
    public ExceptionMessage() {
        this(null, null);
    }

    public ExceptionMessage(String exceptionName, String message) {
        this(exceptionName, message, null);
    }

    public ExceptionMessage(String exceptionName, String message, ExceptionMessage cause) {
        setExceptionName(exceptionName);
        setMessage(message);
        setCause(cause);
    }

    public ExceptionMessage getCause() {
        return cause;
    }

    public void setCause(ExceptionMessage cause) {
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String prefix) {
        String res = prefix + getExceptionName();

        if (getMessage()!=null){
            res = res + ": " + getMessage();
        }

        if (getCause()!=null){
            res = res + "\n" + prefix + "Cause:\n"  + getCause().toString(prefix + "\t");
        }

        return res;
    }
}
