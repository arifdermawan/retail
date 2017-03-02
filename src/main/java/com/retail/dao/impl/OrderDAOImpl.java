package com.retail.dao.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
 








import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.retail.dao.AccountDAO;
import com.retail.dao.OrderDAO;
import com.retail.dao.ProductDAO;
import com.retail.entity.Account;
import com.retail.entity.Order;
import com.retail.entity.OrderDetail;
import com.retail.entity.Product;
import com.retail.model.CartInfo;
import com.retail.model.CartLineInfo;
import com.retail.model.CustomerInfo;
import com.retail.model.OrderDetailInfo;
import com.retail.model.OrderInfo;
import com.retail.model.PaginationResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.joda.LocalDateParser;
import org.springframework.transaction.annotation.Transactional;
 
//Transactional for Hibernate
@Transactional
public class OrderDAOImpl implements OrderDAO {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private AccountDAO accountDAO;
 
    private int getMaxOrderNum() {
        String sql = "Select max(o.orderNum) from " + Order.class.getName() + " o ";
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(sql);
        Integer value = (Integer) query.uniqueResult();
        if (value == null) {
            return 0;
        }
        return value;
    }
 
    @Override
    public void saveOrder(CartInfo cartInfo) {
        Session session = sessionFactory.getCurrentSession();
        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
        BigDecimal amountTotalAfterDisc = new BigDecimal(0);
        BigDecimal amountBeforeDiscount = new BigDecimal(cartInfo.getAmountTotal());
        int orderNum = this.getMaxOrderNum() + 1;
        Order order = new Order();
        Account acc = this.accountDAO.findAccount(customerInfo.getName());
        System.out.println("acc"+acc);
 
        order.setId(UUID.randomUUID().toString());
        order.setOrderNum(orderNum);
        order.setOrderDate(new Date());
        order.setAmount(cartInfo.getAmountTotal());
 
       
        order.setCustomerName(customerInfo.getName());
        order.setCustomerEmail(customerInfo.getEmail());
        order.setCustomerPhone(customerInfo.getPhone());
        order.setCustomerAddress(customerInfo.getAddress());
 
        session.persist(order);
 
        List<CartLineInfo> lines = cartInfo.getCartLines();
 
        for (CartLineInfo line : lines) {
        	System.out.println("Line"+line);
        	BigDecimal tempAmount = new BigDecimal(line.getAmount());
            OrderDetail detail = new OrderDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setOrder(order);
            detail.setPrice(line.getProductInfo().getPrice());
            System.out.println("tempAmount"+line.getProductInfo().getIsGrocery());
            if(line.getProductInfo().getIsGrocery().equals("false")){
            	
            if(acc.getUserRole().equals("EMPLOYEE") || acc.getUserRole().equals("MANAGER")){
            	tempAmount = tempAmount.multiply(new BigDecimal(30)).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            	tempAmount = amountBeforeDiscount.subtract(tempAmount);
            	System.out.println("EMPLOYEE"+acc.getUserRole());
            }
            else if((acc.getUserRole().equals("CUSTOMER"))){
            	int joinYear = countYear(acc.getDate_join());
            	if(joinYear > 2){
            		tempAmount = tempAmount.multiply(new BigDecimal(5)).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            		tempAmount = amountBeforeDiscount.subtract(tempAmount);
            	}
            	else if(tempAmount.compareTo(new BigDecimal(100)) > 1){
            		tempAmount = tempAmount.multiply(new BigDecimal(10)).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            		tempAmount = amountBeforeDiscount.subtract(tempAmount);
            	}
            	
            	}
            amountTotalAfterDisc = amountTotalAfterDisc.add(tempAmount);
            }
            
            if(tempAmount.compareTo(new BigDecimal(0)) == 0){
            	detail.setAmount(line.getAmount());
            }
            else{
            	detail.setAmount(tempAmount.doubleValue());
            }
            detail.setQuanity(line.getQuantity());
 
            String code = line.getProductInfo().getCode();
            Product product = this.productDAO.findProduct(code);
            detail.setProduct(product);
            System.out.println("detail"+detail);
            session.persist(detail);
        }
        System.out.println("amountTotalAfterDisc"+amountTotalAfterDisc);
        if(amountTotalAfterDisc.compareTo(new BigDecimal(0)) == 0){
        	order.setAmount(cartInfo.getAmountTotal());
        	
        }
        else{
        	order.setAmount(amountTotalAfterDisc.doubleValue());
        }
       this.updateAmountTable(order);
       
        // Set OrderNum for report.
 
        cartInfo.setOrderNum(orderNum);
    }
 
    // @page = 1, 2, ...
    @Override
    public PaginationResult<OrderInfo> listOrderInfo(int page, int maxResult, int maxNavigationPage) {
        String sql = "Select new " + OrderInfo.class.getName()//
                + "(ord.id, ord.orderDate, ord.orderNum, ord.amount, "
                + " ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) " + " from "
                + Order.class.getName() + " ord "//
                + " order by ord.orderNum desc";
        Session session = this.sessionFactory.getCurrentSession();
 
        Query query = session.createQuery(sql);
 
        return new PaginationResult<OrderInfo>(query, page, maxResult, maxNavigationPage);
    }
    
    @Override
    public void updateAmountTable(Order order) {
        String sql = "UPDATE "+Order.class.getName()+" SET amount= :amount" + " WHERE id = :id";
        Session session = this.sessionFactory.getCurrentSession();
 
        Query query = session.createQuery(sql);
        query.setParameter("amount", order.getAmount());
        query.setParameter("id", order.getId());
  
   int result = query.executeUpdate();
   System.out.println("Rows affected: " + result);
 
    }
 
    public Order findOrder(String orderId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Order.class);
        crit.add(Restrictions.eq("id", orderId));
        return (Order) crit.uniqueResult();
    }
 
    @Override
    public OrderInfo getOrderInfo(String orderId) {
        Order order = this.findOrder(orderId);
        if (order == null) {
            return null;
        }
        return new OrderInfo(order.getId(), order.getOrderDate(), //
                order.getOrderNum(), order.getAmount(), order.getCustomerName(), //
                order.getCustomerAddress(), order.getCustomerEmail(), order.getCustomerPhone());
    }
 
    @Override
    public List<OrderDetailInfo> listOrderDetailInfos(String orderId) {
        String sql = "Select new " + OrderDetailInfo.class.getName() //
                + "(d.id, d.product.code, d.product.name , d.quanity,d.price,d.amount) "//
                + " from " + OrderDetail.class.getName() + " d "//
                + " where d.order.id = :orderId ";
 
        Session session = this.sessionFactory.getCurrentSession();
 
        Query query = session.createQuery(sql);
        query.setParameter("orderId", orderId);
 
        return query.list();
    }
 
    public int countYear(Date date){
    	Calendar join = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
    	join.setTime(date);
    	now.setTime(new Date());
    	
    	int joinYear = now.get(Calendar.YEAR)-join.get(Calendar.YEAR);
    	System.out.println(joinYear);
    	return joinYear;
    }
}
