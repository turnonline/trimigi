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

package biz.turnonline.ecosystem.service;

import biz.turnonline.ecosystem.model.api.ImportSet;
import biz.turnonline.ecosystem.model.api.ImportSetProperty;
import biz.turnonline.ecosystem.service.mapper.Mapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of {@link ImportService}
 *
 * @author <a href="mailto:pohorelec@turnonlie.biz">Jozef Pohorelec</a>
 */
@Singleton
public class ImportServiceBean
        implements ImportService
{
    private static final Logger log = LoggerFactory.getLogger( ImportServiceBean.class );

    @Inject
    private MongoClient mongoClient;

    @Inject
    private Mapper<ImportSetProperty, Object> mapper;

    @Override
    public void importData( ImportSet importSet )
    {
        // drop collection if requested
        if ( "DROP".equals( importSet.getClean() ) )
        {
            deleteCollection( importSet );
        }

        // import if namespace, kind and id is specified
        if ( importSet.getNamespace() != null && importSet.getKind() != null )
        {
            insertRecord( importSet );
        }
    }

    // -- private helpers

    private void insertRecord( ImportSet importSet )
    {
        String kind = importSet.getKind();
        String namespace = importSet.getNamespace();

        try
        {
            Document document = new Document();

            // set id if provided
            String id = importSet.getId();
            if ( id != null )
            {
                document.append( "_id", id );
            }

            for ( ImportSetProperty property : importSet.getProperties() )
            {
                mapper.map( property, document );
            }

            // get database
            MongoDatabase database = mongoClient.getDatabase( namespace );

            // get collection
            MongoCollection<Document> collection = database.getCollection( kind );

            // insert document
            collection.insertOne( document );
        }
        catch ( MongoException e )
        {
            log.error( "Unable to write document: " + namespace + ":" + kind, e );
        }
    }

    private void deleteCollection( ImportSet importSet )
    {
        mongoClient.getDatabase( importSet.getNamespace() ).getCollection( importSet.getKind() ).drop();
    }
}
