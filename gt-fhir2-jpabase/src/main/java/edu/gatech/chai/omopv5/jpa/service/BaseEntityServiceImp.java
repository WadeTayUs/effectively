package edu.gatech.chai.omopv5.jpa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.gatech.chai.omopv5.jpa.dao.BaseEntityDao;
import edu.gatech.chai.omopv5.jpa.entity.BaseEntity;

public abstract class BaseEntityServiceImp<T extends BaseEntity, V extends BaseEntityDao<T>>  implements IService<T> {

	@Autowired
	private V vDao;
	private Class<T> entityClass;
	
	public BaseEntityServiceImp(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public V getEntityDao() {
		return vDao;
	}
	
	public Class<T> getEntityClass() {
		return this.entityClass;
	}
	
	@Transactional(readOnly = true)
	public T findById(Long id) {
		return vDao.findById(entityClass, id);
	}

	@Transactional(readOnly = true)
	public List<T> searchByColumnString(String column, String value) {
		EntityManager em = vDao.getEntityManager();
		List<T> retvals = new ArrayList<T>();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityClass);
		Root<T> root = query.from(entityClass);
		
		Path<String> path = root.get(column);
		Predicate where = builder.like(builder.lower(path), value.toLowerCase());

		query.select(root);
		query.where(where);
		
		retvals = em.createQuery(query).getResultList();
		return retvals;	
	}

	@Transactional
	public T create(T entity) {
		vDao.add(entity);
		return entity;
	}

	@Transactional
	public T update(T entity) {
		vDao.merge(entity);
		return entity;
	}
	
	@Transactional(readOnly = true)
	public Long getSize() {
		EntityManager em = vDao.getEntityManager();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(entityClass);
		
		query.select(builder.count(root));
		
		return em.createQuery(query).getSingleResult();		
		
	}

	@Transactional(readOnly = true)
	public Long getSize(Map<String, List<ParameterWrapper>> paramMap) {
		// Construct predicate from this map.
		EntityManager em = vDao.getEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(entityClass);

		List<Predicate> predicates = ParameterWrapper.constructPredicate(builder, paramMap, root);		
		if (predicates == null || predicates.isEmpty()) return 0L;

		query.select(builder.count(root));
		query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
		
		return em.createQuery(query).getSingleResult();
	}
	
	@Transactional(readOnly = true)
	public List<T> searchWithoutParams(int fromIndex, int toIndex) {
		int length = toIndex - fromIndex;
		EntityManager em = vDao.getEntityManager();		
		List<T> retvals = new ArrayList<T>();
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityClass);
		Root<T> root = query.from(entityClass);
		
		query.select(root);
		query.orderBy(builder.asc(root.get("id")));
		
		if (length <= 0) {
			retvals = em.createQuery(query)
					.getResultList();
		} else {
			retvals = em.createQuery(query)
					.setFirstResult(fromIndex)
					.setMaxResults(length)
					.getResultList();
		}
		return retvals;	
	}

	@Transactional(readOnly = true)
	public List<T> searchWithParams(int fromIndex, int toIndex, Map<String, List<ParameterWrapper>> paramMap) {
		int length = toIndex - fromIndex;
		EntityManager em = vDao.getEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityClass);
		Root<T> root = query.from(entityClass);
		
		List<T> retvals = new ArrayList<T>();
		
		List<Predicate> predicates = ParameterWrapper.constructPredicate(builder, paramMap, root);		
		if (predicates == null || predicates.isEmpty()) return retvals; // Nothing. return empty list
	
		query.select(root);
		query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));

		if (length <= 0) {
			retvals = em.createQuery(query)
					.getResultList();			
		} else {
			retvals = em.createQuery(query)
					.setFirstResult(fromIndex)
					.setMaxResults(length)
					.getResultList();
		}
		return retvals;
	}

}
