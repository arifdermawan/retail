package com.retail.dao;

import com.retail.entity.Product;
import com.retail.model.PaginationResult;
import com.retail.model.ProductInfo;
public interface ProductDAO {
	public Product findProduct(String code);
	public ProductInfo findProductInfo(String code);
	public PaginationResult<ProductInfo> queryProducts(int page, int maxResult,
			int maxNavigationPage);
	public PaginationResult<ProductInfo> queryProducts(int page, int maxResult,
			int maxNavigationPage, String likeName);
	public void save(ProductInfo productInfo);
}
