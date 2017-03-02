package com.retail.model;

import com.retail.entity.Product;

import org.springframework.web.multipart.commons.CommonsMultipartFile;
public class ProductInfo {
	private String code;
    private String name;
    private double price;
    private String isGrocery;
    
    private boolean newProduct=false;
 
    // Upload file.
    private CommonsMultipartFile fileData;
 
    public ProductInfo() {
    }
 
    public ProductInfo(Product product) {
        this.code = product.getCode();
        this.name = product.getName();
        this.price = product.getPrice();
        this.isGrocery = product.getIsGrocery();
    }
 
    
    public ProductInfo(String code, String name, double price, String isGrocery ) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.isGrocery = isGrocery;
    }
 
    public String getCode() {
        return code;
    }
 
    public void setCode(String code) {
        this.code = code;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public double getPrice() {
        return price;
    }
 
    public void setPrice(double price) {
        this.price = price;
    }
 
    public CommonsMultipartFile getFileData() {
        return fileData;
    }
 
    public void setFileData(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }
 
    public boolean isNewProduct() {
        return newProduct;
    }
 
    public void setNewProduct(boolean newProduct) {
        this.newProduct = newProduct;
    }

	public String getIsGrocery() {
		return isGrocery;
	}

	public void setIsGrocery(String isGrocery) {
		this.isGrocery = isGrocery;
	}


    
    
}
