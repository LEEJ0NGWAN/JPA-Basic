package shop;

import shop.Domain.Item;
import shop.Domain.Member;
import shop.Domain.Order;
import shop.Domain.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("shop");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            Member memberA = new Member();
            memberA.setCity("Seoul");
            memberA.setName("Lee Jong Wan");
            memberA.setStreet("Yeongdeungpo-ro");
            memberA.setZipcode("XXXXXX");

            Item cheese = new Item();
            cheese.setName("cheese");
            cheese.setPrice(1500);
            cheese.setStockQuantity(3);

            Item snack = new Item();
            snack.setName("potato chip");
            snack.setPrice(2000);
            snack.setStockQuantity(5);

            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setMember(memberA);
            order.setStatus("delivering");

            OrderItem orderCheese = new OrderItem();
            orderCheese.setOrder(order);
            orderCheese.setItem(cheese);
            orderCheese.setOrderPrice(4500);
            orderCheese.setCount(3);

            OrderItem orderSnack = new OrderItem();
            orderSnack.setOrder(order);
            orderSnack.setItem(snack);
            orderSnack.setOrderPrice(10000);
            orderSnack.setCount(10);

            em.persist(memberA);
            em.persist(order);
            em.persist(cheese);
            em.persist(snack);
            em.persist(orderCheese);
            em.persist(orderSnack);

            tx.commit();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            em.close();
            emf.close();
        }
    }
}
