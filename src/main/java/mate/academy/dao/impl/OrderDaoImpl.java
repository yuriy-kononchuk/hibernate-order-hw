package mate.academy.dao.impl;

import java.util.List;
import mate.academy.dao.OrderDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Order;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class OrderDaoImpl implements OrderDao {
    @Override
    public Order add(Order order) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(order);
            transaction.commit();
            return order;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add an order: " + order, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Order> getByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> getOrderByUserQuery = session.createQuery(
                    "FROM Order o "
                            + "LEFT JOIN FETCH o.user "
                            + "LEFT JOIN FETCH o.tickets tk "
                            + "LEFT JOIN FETCH tk.movieSession ms "
                            + "LEFT JOIN FETCH ms.movie "
                            + "LEFT JOIN FETCH ms.cinemaHall "
                            + "WHERE o.id = :user_id", Order.class);
            getOrderByUserQuery.setParameter("user_id", user.getId());
            return getOrderByUserQuery.getResultList();
            //return getOrderByUserQuery.uniqueResult();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find an order by user: " + user, e);
        }
    }
}