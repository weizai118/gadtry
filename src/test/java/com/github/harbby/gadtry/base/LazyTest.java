/*
 * Copyright (C) 2018 The Harbby Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.harbby.gadtry.base;

import com.github.harbby.gadtry.function.Creator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LazyTest
{
    @Test
    public void goLazy()
            throws IOException
    {
        final Creator<Connection> connection = Lazys.goLazy(() -> {
            try {
                return DriverManager.getConnection("jdbc:url");
            }
            catch (SQLException e) {
                throw new RuntimeException("Connection create fail", e);
            }
        });

        Assert.assertNotNull(Serializables.serialize(connection));
    }
}
