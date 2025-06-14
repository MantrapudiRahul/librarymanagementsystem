package dao;

import org.hibernate.Session;

import models.Admin;
import util.HibernateUtil;
public class AdminDAO {
	    public Admin findByUsernameAndPassword(String username, String password) {
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            return session.createQuery("FROM Admin WHERE username = :username AND password = :password", Admin.class)
	                          .setParameter("username", username)
	                          .setParameter("password", password)
	                          .uniqueResult();
	        }
	    }
	}
