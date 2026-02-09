package org.app.processmanagement;

import org.app.encryptDecrypt.Cryption;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessManager {

    private static final int MAX_QUEUE_SIZE = 1000;

    // Thread-safe bounded queue for tasks
    private final BlockingQueue<Task> taskQueue;

    // Controls how many tasks are available for processing
    private final Semaphore itemsSemaphore;

    // Controls available capacity in the queue
    private final Semaphore emptySlotsSemaphore;

    // Pool of worker threads executing tasks in parallel
    private final ExecutorService workerPool;

    // Tracks the current number of tasks in the queue
    private final AtomicInteger size;

    public ProcessManager(int workerCount) {

        this.taskQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
        this.itemsSemaphore = new Semaphore(0);
        this.emptySlotsSemaphore = new Semaphore(MAX_QUEUE_SIZE);
        this.workerPool = Executors.newFixedThreadPool(workerCount);
        this.size = new AtomicInteger(0);

        // Start worker threads that continuously process tasks
        for (int i = 0; i < workerCount; i++) {
            workerPool.submit(this::processTasks);
        }
    }

    /**
     * Submits a task for processing.
     * Blocks if the queue is full.
     */
    public boolean submitToQueue(Task task) {
        try {
            emptySlotsSemaphore.acquire();

            taskQueue.put(task);
            size.incrementAndGet();

            itemsSemaphore.release();
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Continuously takes tasks from the queue and executes them.
     */
    private void processTasks() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                itemsSemaphore.acquire();

                Task task = taskQueue.take();
                size.decrementAndGet();

                emptySlotsSemaphore.release();

                executeCryption(task);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Executes encryption or decryption logic for a task.
     */
    private void executeCryption(Task task) {
        try {
            Cryption.executeCryption(task.toString());
        } finally {
            task.close(); // VERY IMPORTANT
        }
    }


    /**
     * Shuts down all worker threads immediately.
     */
    public void shutdownAndAwait() {

        workerPool.shutdownNow();

        try {
            workerPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
