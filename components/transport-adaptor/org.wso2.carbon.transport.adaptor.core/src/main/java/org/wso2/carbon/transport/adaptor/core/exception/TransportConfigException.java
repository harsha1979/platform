/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
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
 */

package org.wso2.carbon.transport.adaptor.core.exception;

/**
 * this class represents the exceptions related to deploy time configuration
 */
public class TransportConfigException extends Exception {

    public TransportConfigException() {
    }

    public TransportConfigException(String message) {
        super(message);
    }

    public TransportConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransportConfigException(Throwable cause) {
        super(cause);
    }
}