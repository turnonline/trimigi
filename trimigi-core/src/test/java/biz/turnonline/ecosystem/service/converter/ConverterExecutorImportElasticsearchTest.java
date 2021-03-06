/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package biz.turnonline.ecosystem.service.converter;

import biz.turnonline.ecosystem.model.LatLng;
import biz.turnonline.ecosystem.model.api.ImportSetProperty;
import biz.turnonline.ecosystem.service.enricher.EnricherExecutor;
import biz.turnonline.ecosystem.service.transformer.TransformerExecutor;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static biz.turnonline.ecosystem.Mocks.mockImportSetProperty;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_BINARY;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_BOOLEAN;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_DATE;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_DOUBLE;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_GEO_POINT;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_KEYWORD;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_LONG;
import static biz.turnonline.ecosystem.service.converter.ElasticsearchConverterRegistrat.TYPE_TEXT;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link ConverterExecutor} - elasticsearch registrat - convert for import
 *
 * @author <a href="mailto:pohorelec@turnonlie.biz">Jozef Pohorelec</a>
 */
public class ConverterExecutorImportElasticsearchTest
{
    private ConverterExecutor executor = new ConverterExecutor( new EnricherExecutor(), new TransformerExecutor(), new ElasticsearchConverterRegistrat() );

    @Test
    public void test_Text()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_TEXT, "Boston" );
        String convertedValue = ( String ) executor.convertProperty( property );

        assertEquals( "Boston", convertedValue );
    }

    @Test
    public void test_Keyword()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_KEYWORD, "Boston" );
        String convertedValue = ( String ) executor.convertProperty( property );

        assertEquals( "Boston", convertedValue );
    }

    @Test
    public void test_Long()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_LONG, "1" );
        Long convertedValue = ( Long ) executor.convertProperty( property );

        assertEquals( Long.valueOf( 1 ), convertedValue );
    }

    @Test
    public void test_Double()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_DOUBLE, "1.2" );
        Double convertedValue = ( Double ) executor.convertProperty( property );

        assertEquals( Double.valueOf( 1.2 ), convertedValue );
    }

    @Test
    public void test_Date()
    {
        Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
        calendar.set( 2018, Calendar.JANUARY, 2, 0, 0, 0 );

        ImportSetProperty property = mockImportSetProperty( TYPE_DATE, Long.valueOf( calendar.getTimeInMillis() ).toString() );
        Date convertedValue = ( Date ) executor.convertProperty( property );

        assertEquals( calendar.getTime(), convertedValue );
    }

    @Test
    public void test_Boolean_False()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_BOOLEAN, "false" );
        Boolean convertedValue = ( Boolean ) executor.convertProperty( property );

        assertFalse( convertedValue );
    }

    @Test
    public void test_Boolean_True()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_BOOLEAN, "true" );
        Boolean convertedValue = ( Boolean ) executor.convertProperty( property );

        assertTrue( convertedValue );
    }

    @Test
    public void test_Binary()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_BINARY, "Sm9obg==" );
        byte[] convertedValue = ( byte[] ) executor.convertProperty( property );

        assertArrayEquals( new byte[]{'J', 'o', 'h', 'n'}, convertedValue );
    }

    @Test
    public void test_GeoPoint()
    {
        ImportSetProperty property = mockImportSetProperty( TYPE_GEO_POINT, "13.4:14.5" );
        LatLng convertedValue = ( LatLng ) executor.convertProperty( property );

        assertEquals(  13.4, convertedValue.getLatitude(), 0);
        assertEquals(  14.5, convertedValue.getLongitude(), 0);
    }
}