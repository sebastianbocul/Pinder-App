package com.pinder.app.util;

public abstract class MultiTaskHandler {
    private int mTasksLeft;
    private boolean mIsCanceled = false;

    public MultiTaskHandler(int numOfTasks) {
        mTasksLeft = numOfTasks;
    }

    protected abstract void onAllTasksCompleted();

    public void taskComplete() {
        mTasksLeft--;
        if (mTasksLeft == 0 && !mIsCanceled) {
            onAllTasksCompleted();
        }
    }

    public void reset(int numOfTasks) {
        mTasksLeft = numOfTasks;
        mIsCanceled = false;
    }

    public void cancel() {
        mIsCanceled = true;
    }
}