package com.Destinex.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

}
/*

1️⃣ Hibernate / JPA proxy (for entities)
When you do @ManyToOne(fetch = LAZY) or EntityManager.getReference(), Hibernate doesn’t load the real object immediately.
Instead, it creates a proxy object, which is basically a placeholder that has the ID of the entity.
Only when you access a field other than ID, Hibernate fires a SQL query to load the full entity.

Example:

User userProxy = entityManager.getReference(User.class, 42);
System.out.println(userProxy.getId());    // no DB query
System.out.println(userProxy.getName());  // triggers DB query

So this proxy lets you attach relationships without loading the full entity, which is perfect for setting review.setUser(userProxy).

2️⃣ Spring AOP proxy
This is different. Spring creates a proxy around your bean to intercept method calls.
It’s used for transactions, security, caching, etc.
Example: if you have @Transactional, Spring wraps your service in a proxy. When you call a method, the proxy adds transaction logic before and after the real method.
*
* */