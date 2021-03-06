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

package biz.turnonline.ecosystem.service.beam.pipeline;

import biz.turnonline.ecosystem.model.api.ImportBatch;
import biz.turnonline.ecosystem.model.api.ImportSet;
import biz.turnonline.ecosystem.service.beam.function.ImportDoFn;
import biz.turnonline.ecosystem.service.beam.options.ImportPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;

import javax.inject.Singleton;
import java.util.List;

/**
 * Import beam pipeline
 *
 * @author <a href="mailto:pohorelec@turnonlie.biz">Jozef Pohorelec</a>
 */
@Singleton
public class ImportBeamPipeline
        extends BeamPipeline<ImportBatch, ImportPipelineOptions>
{
    @Override
    public Pipeline create( ImportBatch batch, ImportPipelineOptions options )
    {
        Pipeline pipeline = Pipeline.create( options );
        pipeline
                .apply( "Split import sets", new PTransform<PBegin, PCollection<ImportSet>>()
                {
                    @Override
                    public PCollection<ImportSet> expand( PBegin input )
                    {
                        List<ImportSet> importSets = batch.getImportSets();
                        Create.Values<ImportSet> values;
                        if ( importSets.isEmpty() )
                        {
                            values = Create.empty( new TypeDescriptor<ImportSet>() {} );
                        }
                        else
                        {
                            values = Create.of( importSets );
                        }

                        return input.apply( values );
                    }
                } )
                .apply( "Import sets", ParDo.of( new ImportDoFn() ) );

        return pipeline;
    }
}
