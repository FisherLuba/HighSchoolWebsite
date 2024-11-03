package com.fisherl.schoolwebsite.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<PostUser, String> {

    Optional<PostUser> findByEmail(String email);

}
