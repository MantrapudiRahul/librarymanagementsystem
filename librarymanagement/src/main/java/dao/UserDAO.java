package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import models.User;
import util.HibernateUtil;

import java.util.List;
public class UserDAO {
	


	    public void save(User user) {
	        Transaction transaction = null;
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            transaction = session.beginTransaction();
	            session.persist(user);
	            transaction.commit();
	        } catch (Exception e) {
	            if (transaction != null) transaction.rollback();
	            e.printStackTrace();
	        }
	    }

	    public User findById(Long id) {
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            return session.get(User.class, id);
	        }
	    }

	    public List<User> findAll() {
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            return session.createQuery("FROM User", User.class).list();
	        }
	    }
	}