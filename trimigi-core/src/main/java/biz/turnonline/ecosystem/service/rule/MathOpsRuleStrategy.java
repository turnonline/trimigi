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

package biz.turnonline.ecosystem.service.rule;

import biz.turnonline.ecosystem.model.api.MigrationSetRule;
import com.google.common.base.Charsets;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Rule set algebraic strategy for mathematical operations - {@code '=','<','>','<=','>='}
 *
 * @author <a href="mailto:pohorelec@turnonlie.biz">Jozef Pohorelec</a>
 */
public class MathOpsRuleStrategy
        implements RuleStrategy
{
    public static final RuleStrategy INSTANCE_EQ = new MathOpsRuleStrategy( Operation.EQ );
    public static final RuleStrategy INSTANCE_LT = new MathOpsRuleStrategy( Operation.LT );
    public static final RuleStrategy INSTANCE_LTE = new MathOpsRuleStrategy( Operation.LTE );
    public static final RuleStrategy INSTANCE_GT = new MathOpsRuleStrategy( Operation.GT );
    public static final RuleStrategy INSTANCE_GTE = new MathOpsRuleStrategy( Operation.GTE );

    private Operation operation;

    public MathOpsRuleStrategy( Operation operation )
    {
        this.operation = operation;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public boolean apply( MigrationSetRule rule, Map<String, Object> ctx )
    {
        Object property = ctx.get( rule.getProperty() );
        if ( property != null )
        {
            Object convertedRuleValue = convertValue( rule.getValue(), property, rule );

            switch ( operation )
            {
                case EQ:
                {
                    return property.equals( convertedRuleValue );
                }
                case LT:
                {
                    return compareTo( property, convertedRuleValue ) < 0;
                }
                case LTE:
                {
                    return compareTo( property, convertedRuleValue ) <= 0;
                }
                case GT:
                {
                    return compareTo( property, convertedRuleValue ) > 0;
                }
                case GTE:
                {
                    return compareTo( property, convertedRuleValue ) >= 0;
                }
            }
        }

        // return true if property was not found - it means that we do not want to filter row if property is not found
        return true;
    }

    private Object convertValue( Object convertedValue,
                                 Object property,
                                 MigrationSetRule rule )
    {
        if ( property instanceof Integer )
        {
            convertedValue = Integer.valueOf( rule.getValue() );
        }
        else if ( property instanceof Long )
        {
            convertedValue = Long.valueOf( rule.getValue() );
        }
        else if ( property instanceof Float )
        {
            convertedValue = Float.valueOf( rule.getValue() );
        }
        else if ( property instanceof Double )
        {
            convertedValue = Double.valueOf( rule.getValue() );
        }
        else if ( property instanceof BigDecimal )
        {
            convertedValue = new BigDecimal( rule.getValue() );
        }
        else if ( property instanceof Boolean )
        {
            convertedValue = Boolean.valueOf( rule.getValue() );
        }
        else if ( property instanceof byte[] )
        {
            convertedValue = rule.getValue().getBytes( Charsets.UTF_8 );
        }
        else if ( property instanceof Date )
        {
            convertedValue = new Date( Long.valueOf( rule.getValue() ) );
        }

        return convertedValue;
    }

    @SuppressWarnings( "unchecked" )
    private int compareTo( Object property, Object convertedValue )
    {
        return ( ( Comparable ) property ).compareTo( convertedValue );
    }
}
