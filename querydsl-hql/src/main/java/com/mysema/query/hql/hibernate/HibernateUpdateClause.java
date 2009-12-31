/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.hql.hibernate;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.QueryMetadata;
import com.mysema.query.dml.UpdateClause;
import com.mysema.query.hql.HQLSerializer;
import com.mysema.query.hql.HQLTemplates;
import com.mysema.query.types.expr.EBoolean;
import com.mysema.query.types.expr.Expr;
import com.mysema.query.types.path.PEntity;
import com.mysema.query.types.path.Path;

/**
 * UpdateClause implementation for JPA
 * 
 * @author tiwe
 *
 */
public class HibernateUpdateClause implements UpdateClause<HibernateUpdateClause>{

    private final QueryMetadata md = new DefaultQueryMetadata();
    
    private final SessionHolder session;
    
    private final HQLTemplates templates;
    
    public HibernateUpdateClause(Session session, PEntity<?> entity){
        this(new DefaultSessionHolder(session), entity, HQLTemplates.DEFAULT);
    }
    
    public HibernateUpdateClause(StatelessSession session, PEntity<?> entity){
        this(new StatelessSessionHolder(session), entity, HQLTemplates.DEFAULT);
    }
    
    public HibernateUpdateClause(SessionHolder session, PEntity<?> entity, HQLTemplates templates){
        this.session = session;
        this.templates = templates;
        md.addFrom(entity);        
    }
    
    @Override
    public long execute() {
        HQLSerializer serializer = new HQLSerializer(templates);
        serializer.serializeForUpdate(md);
        Map<Object,String> constants = serializer.getConstantToLabel();

        Query query = session.createQuery(serializer.toString());
        HibernateUtil.setConstants(query, constants);
        return query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> HibernateUpdateClause set(Path<T> path, T value) {
        md.addProjection(((Expr<T>)path).eq(value));
        return this;
    }

    @Override
    public HibernateUpdateClause where(EBoolean... o) {
        md.addWhere(o);
        return this;
    }

}
