package com.aiproxy.repository;

import com.aiproxy.model.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserAccountRepository {

    private final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();

    public UserAccount findOrCreate(String userId) {
        return accounts.computeIfAbsent(userId, UserAccount::new);
    }

    public Collection<UserAccount> findAll() {
        return accounts.values();
    }
}
