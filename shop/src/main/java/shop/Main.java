package shop;

import shop.Domain.*;

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

            Category category = new Category();
            category.setName("food");

            Item cheese = new Item();
            cheese.setName("cheese");
            cheese.setPrice(1500);
            cheese.setStockQuantity(3);
            cheese.getCategories().add(category);

            Item snack = new Item();
            snack.setName("potato chip");
            snack.setPrice(2000);
            snack.setStockQuantity(5);
            snack.getCategories().add(category);

            Delivery delivery = new Delivery();
            delivery.setStatus("prepare");
            delivery.setCity("Seoul");
            delivery.setStreet("Jong-ro");
            delivery.setZipcode("XXXXXX");

            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setMember(memberA);
            order.setStatus("delivering");
            order.setDelivery(delivery);

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
            em.persist(category);
            em.persist(delivery);
            em.persist(order);
            em.persist(cheese);
            em.persist(snack);
            em.persist(orderCheese);
            em.persist(orderSnack);

            tx.commit();

            em.clear();

            em.find(Order.class, order.getId())
                    .getMember()
                    .getOrders()
                    .forEach(o -> System.out.println(o.getId()));

            em.find(OrderItem.class, orderCheese.getId())
                    .getItem()
                    .getOrderItems()
                    .forEach(o -> System.out.println(o.getId()));

            em.find(OrderItem.class, orderSnack.getId())
                    .getOrder()
                    .getOrderItems()
                    .forEach(o -> System.out.println(o.getId()));

            System.out.println(
                    em.find(Delivery.class, delivery.getId()).getOrder().getId());

            em.find(Category.class, category.getId())
                    .getItems()
                    .forEach(o -> System.out.println(o.getName()));

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            em.close();
            emf.close();
        }
    }
}
