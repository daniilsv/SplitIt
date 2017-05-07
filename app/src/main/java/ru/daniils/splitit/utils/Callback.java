package ru.daniils.splitit.utils;

public class Callback {
    public interface ISimple {
        void run();
    }

    public interface ISuccessError {
        void onSuccess();

        void onError();
    }
}
