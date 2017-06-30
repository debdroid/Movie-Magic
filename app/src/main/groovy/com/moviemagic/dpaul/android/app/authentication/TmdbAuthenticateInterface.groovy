/*
 * Copyright 2017 Debashis Paul

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moviemagic.dpaul.android.app.authentication

import android.os.Bundle
import groovy.transform.CompileStatic

@CompileStatic
/**
 * An interface which will be implemented by the ServerAuthenticate Class
 */
public interface TmdbAuthenticateInterface {
    public Bundle tmdbUserSignIn(final String userName, final String password, String authTokenType) throws Exception
}