package com.boge;


import com.boge.model.User;
import com.boge.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringDataJpaTest {

	@Autowired
	private IUserService service;

	@Test
	void contextLoads() {
		List<User> list = service.query();
		for (User user : list) {
			System.out.println(user);
		}
	}
}
