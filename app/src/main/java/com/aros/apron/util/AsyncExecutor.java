package com.aros.apron.util;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncExecutor {
    private static final AtomicBoolean shouldStop = new AtomicBoolean(false);
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 定义一个回调接口，用于处理异步结果
    public interface AsyncCallback<T> {
        void onResult(T result);

        void onError(Exception e);
    }

    // 异步方法，接受一个需要被执行的方法（Callable）和一个回调接口
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> void executeAsync(Callable<T> task, AsyncCallback<T> callback) {

        Runnable taskWrapper = () -> {
            if (shouldStop.get()) {
                // 如果已经应该停止，则直接返回
                return;
            }
            CompletableFuture.supplyAsync(() -> {
                try {
                    // 执行传入的任务
                    return task.call();
                } catch (Exception e) {
                    // 如果任务执行过程中出现异常，则抛出运行时异常
                    throw new RuntimeException(e);
                }
            }).thenAccept(result -> {
                    // 异步任务成功完成，调用回调接口的onResult方法
                    callback.onResult(result);

            }).exceptionally(ex -> {
                // 异步任务执行过程中出现异常，调用回调接口的onError方法
                callback.onError((Exception) ex);
                return null; // 这里返回null，因为exceptionally的返回值不会被使用
            });
        };

        // 使用scheduleAtFixedRate或scheduleWithFixedDelay来调度任务
        // 这里为了示例使用scheduleAtFixedRate，但注意它可能导致任务重叠
        // 安排任务以固定频率执行（初始延迟为0，之后每隔30毫秒执行一次）
        scheduler.scheduleAtFixedRate(taskWrapper, 0,30 , TimeUnit.MILLISECONDS);

        // 添加一个钩子来在程序退出时关闭调度器（如果尚未关闭）
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!scheduler.isShutdown()) {
                scheduler.shutdownNow();
            }
            try {
//                判断是否已关闭线程池，没有则延时5秒后再判断，返回true或false，这个方法只有判断的作用
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args) {
        // 定义一个需要被执行的方法，这里使用Callable接口
        Callable<Integer> myTask = () -> {
            int i = CameraControllerUtil.getInstance().sendVideoWithARInfoXFun(0);
            return i;
        };

        // 使用异步方法，并传入回调接口的实现
        executeAsync(myTask, new AsyncCallback<Integer>() {
            @Override
            public void onResult(Integer result) {
                // 处理异步任务的结果
                System.out.println("Async task result: " + result);
            }

            @Override
            public void onError(Exception e) {
                // 处理异步任务中的异常
                e.printStackTrace();
            }
        });
//        某个时候需要停止调度器
        if (true) {
            // 如果结果满足停止条件，则设置停止标志
            shouldStop.set(true);
            // 可以选择性地取消调度器中的所有任务
            scheduler.shutdownNow(); // 这将尝试停止正在执行的任务并取消等待的任务
        }
        // 注意：main方法会立即返回，不会等待异步任务完成。
        // 异步任务的结果将通过回调接口返回。

    }
}
