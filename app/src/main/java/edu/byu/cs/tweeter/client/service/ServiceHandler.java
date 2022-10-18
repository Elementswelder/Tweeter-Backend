package edu.byu.cs.tweeter.client.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServiceHandler<T extends Runnable> {

    public void startTask(T task){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
}
