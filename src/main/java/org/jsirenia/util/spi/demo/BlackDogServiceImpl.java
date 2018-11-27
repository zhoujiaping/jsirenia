package org.jsirenia.util.spi.demo;

public class BlackDogServiceImpl implements DogService{

    @Override
    public void sleep() {
        System.out.println("黑色dog。。。汪汪叫，不睡觉...");
        
    }
    
}