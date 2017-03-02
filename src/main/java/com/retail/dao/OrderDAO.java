package com.retail.dao;

import java.util.List;

import com.retail.entity.Order;
import com.retail.model.CartInfo;
import com.retail.model.OrderDetailInfo;
import com.retail.model.OrderInfo;
import com.retail.model.PaginationResult;

public interface OrderDAO {
	 public void saveOrder(CartInfo cartInfo);
	 
	 public PaginationResult<OrderInfo> listOrderInfo(int page,int maxResult, int maxNavigationPage);
	 public OrderInfo getOrderInfo(String orderId);
	 public List<OrderDetailInfo> listOrderDetailInfos(String orderId);
	 public void updateAmountTable(Order order);
}
