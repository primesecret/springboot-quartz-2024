package com.finpong.quartz.repository;

import com.finpong.quartz.entity.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

}