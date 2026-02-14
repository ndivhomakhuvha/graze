package com.graze.graze.animal.generators;

import com.graze.graze.animal.domain.Animal;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.math.BigInteger;
import java.util.EnumSet;

public class AnimalTagGenerator implements BeforeExecutionGenerator {
  @Override
  public Object generate(
    SharedSessionContractImplementor session,
    Object entity,
    Object currentValue,
    EventType eventType) {

    Animal animal = (Animal) entity;

    String genderKey = animal.getGender().name();
    String prefix = animal.getGender().name().substring(0, 1);

    BigInteger nextValue = (BigInteger) session.createNativeQuery(
        "select next_value from animal_sequence where gender = :gender for update")
      .setParameter("gender", genderKey)
      .uniqueResult();

    if (nextValue == null) {
      throw new RuntimeException("No sequence found for gender: " + genderKey);
    }

    session.createNativeQuery(
        "update animal_sequence set next_value = next_value + 1 where gender = :gender")
      .setParameter("gender", genderKey)
      .executeUpdate();

    return prefix + String.format("%03d", nextValue.longValue());
  }

  @Override
  public EnumSet<EventType> getEventTypes() {
    return EnumSet.of(EventType.INSERT);
  }
}
