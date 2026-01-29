package com.example.adoptions.adoptions;

import org.springframework.modulith.events.Externalized;

@Externalized
public record DogAdoptedEvent(int dogId)  {
}
