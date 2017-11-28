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


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ExceptionMessageTest {
    @Test
    public void defaultConstructorTest(){
        ExceptionMessage exceptionMessage = new ExceptionMessage();
        Assert.assertNull(exceptionMessage.getExceptionName());
        Assert.assertNull(exceptionMessage.getMessage());
        Assert.assertNull(exceptionMessage.getCause());
    }

    @Test
    public void exceptionWithNoCauseAndMessageTest() {
        IOException exception = new IOException();
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertException(exception, exceptionMessage);
    }

    @Test
    public void exceptionWithNoCauseButWithMessageTest() {
        IOException exception = new IOException("Sample message");
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertException(exception, exceptionMessage);
    }

    @Test
    public void exceptionWithACauseButNoMessageTest() {
        IOException exception = new IOException(new ArrayIndexOutOfBoundsException());
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertException(exception, exceptionMessage);
    }

    @Test
    public void exceptionWithACauseAndMessageTest() {
        IOException exception = new IOException("Sample message", new ArrayIndexOutOfBoundsException("Cause"));
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertException(exception, exceptionMessage);
    }

    @Test
    public void exceptionWithCauseChainTest() {
        IOException exception = null;

        for (int i = 10; i > 0; i--) {
            exception = new IOException("Exception #" + i, exception);
        }

        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertException(exception, exceptionMessage);
    }

    @Test
    public void printExceptionWithNoCauseAndMessageTest() {
        IOException exception = new IOException();
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertExceptionMessageToString(exceptionMessage);
        System.out.println(exceptionMessage);
    }

    @Test
    public void printExceptionWithNoCauseButWithMessageTest() {
        IOException exception = new IOException("Sample message");
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertExceptionMessageToString(exceptionMessage);
        System.out.println(exceptionMessage);
    }

    @Test
    public void printExceptionWithACauseButNoMessageTest() {
        IOException exception = new IOException(new ArrayIndexOutOfBoundsException());
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertExceptionMessageToString(exceptionMessage);
        System.out.println(exceptionMessage);
    }

    @Test
    public void printExceptionWithACauseAndMessageTest() {
        IOException exception = new IOException("Sample message", new ArrayIndexOutOfBoundsException("Cause"));
        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertExceptionMessageToString(exceptionMessage);
        System.out.println(exceptionMessage);
    }

    @Test
    public void printExceptionWithCauseChainTest() {
        IOException exception = null;

        for (int i = 10; i > 0; i--) {
            exception = new IOException("Exception #" + i, exception);
        }

        ExceptionMessage exceptionMessage = ExceptionMessage.fromThrowable(exception);

        assertExceptionMessageToString(exceptionMessage);
        System.out.println(exceptionMessage);
    }

    private void assertException(Throwable throwable, ExceptionMessage exceptionMessage) {
        Assert.assertEquals(throwable.getClass().getName(), exceptionMessage.getExceptionName());
        Assert.assertEquals(throwable.getMessage(), exceptionMessage.getMessage());
        if (throwable.getCause() == null || exceptionMessage.getCause() == null) {
            // both should be null
            Assert.assertNull(throwable.getCause());
            Assert.assertNull(exceptionMessage.getCause());
        } else {
            // none of the causes is null
            assertException(throwable.getCause(), exceptionMessage.getCause());
        }
    }

    private void assertExceptionMessageToString(ExceptionMessage exceptionMessage){
        assertExceptionMessageToString(exceptionMessage, exceptionMessage.toString());
    }

    private void assertExceptionMessageToString(ExceptionMessage exceptionMessage, String output){
        Assert.assertTrue(output.contains(exceptionMessage.getExceptionName()));

        if (exceptionMessage.getMessage()!=null){
            Assert.assertTrue(output.contains(exceptionMessage.getMessage()));
        }

        if (exceptionMessage.getCause()!=null){
            assertExceptionMessageToString(exceptionMessage.getCause(), output);
        }
    }
}
