//package com.danrmzn.financiallyfit;
//
//import android.annotation.SuppressLint;
//import android.os.CountDownTimer;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//
//public class GetCountdownTimer {
//
//    /**
//     * Static method to create and return a CountDownTimer instance.
//     *
//     * @param startTime  The starting time of the countdown
//     * @param minutes    Minutes to add to the countdown
//     * @param hours      Hours to add to the countdown
//     * @param interval   Tick interval in milliseconds (e.g., 1000 for 1 second)
//     * @param onTick     Callback for each tick
//     * @param onFinish   Callback for when the timer finishes
//     * @return CountDownTimer instance
//     */
//
//    public static CountDownTimer createTimer(
//            LocalDateTime startTime,
//            long minutes,
//            long hours,
////            OnTickCallback onTick,
////            OnFinishCallback onFinish
//    ) {
//        // Calculate the final countdown duration in milliseconds
//        LocalDateTime endTime = startTime.plusHours(hours).plusMinutes(minutes);
//        long durationMillis = Duration.between(LocalDateTime.now(), endTime).toMillis();
//
//        // Ensure the duration is positive
//        if (durationMillis <= 0) {
//            throw new IllegalArgumentException("The countdown duration must be positive.");
//        }
//
//        // Return the CountDownTimer
//        return new CountDownTimer(durationMillis, 1000) {
//            @Override
//            public void onTick(
////                    long millisUntilFinished
//            ) {
////                if (onTick != null) {
////                    onTick.onTick(millisUntilFinished);
////                }
//            }
//
//            @Override
//            public void onFinish() {
////                if (onFinish != null) {
////                    onFinish.onFinish();
////                }
//            }
//        };
//    }
//
//    // Functional interface for tick callback
//    public interface OnTickCallback {
//        void onTick(long millisUntilFinished);
//    }
//
//    // Functional interface for finish callback
//    public interface OnFinishCallback {
//        void onFinish();
//    }
//}
