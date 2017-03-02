package com.retail.dao;

import com.retail.entity.Account;

public interface AccountDAO {
	public Account findAccount(String userName);
}
