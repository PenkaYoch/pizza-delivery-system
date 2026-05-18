package com.pizza.delivery.task.repository;


import com.pizza.delivery.task.model.PizzaTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<PizzaTask, Long> {
}
