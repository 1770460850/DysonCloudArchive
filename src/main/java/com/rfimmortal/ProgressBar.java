package com.rfimmortal;

public class ProgressBar {
    public ProgressBar(long transferred, long total){
        int progress = (int) ((transferred * 100) / total);
        int length = 50;
        int doneLength = (progress * length) / 100;
        int remainingLength = length - doneLength;

        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < doneLength; i++) {
            progressBar.append("=");
        }
        for (int i = 0; i < remainingLength; i++) {
            progressBar.append(" ");
        }
        progressBar.append("] " + progress + "%");

        System.out.print("\r" + progressBar.toString());
    }
}
