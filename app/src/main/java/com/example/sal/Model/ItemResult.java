package com.example.sal.Model;

public class ItemResult {
    private String userId;
    private String userfam;
    private String username;
    private int questionIndex;
    private int selectedAnswerIndex;
    private boolean isCorrectAnswer;

    public ItemResult() {
        // Пустой конструктор для Firebase
    }

    public ItemResult(String userId, String userfam, String username, int questionIndex, int selectedAnswerIndex, boolean isCorrectAnswer) {
        this.userId = userId;
        this.userfam = userfam;
        this.username = username;
        this.questionIndex = questionIndex;
        this.selectedAnswerIndex = selectedAnswerIndex;
        this.isCorrectAnswer = isCorrectAnswer;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserfam() {
        return userfam;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String setUserfam() {
        return userfam;
    }

    public String setUsername() {
        return username;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getSelectedAnswerIndex() {
        return selectedAnswerIndex;
    }

    public void setSelectedAnswerIndex(int selectedAnswerIndex) {
        this.selectedAnswerIndex = selectedAnswerIndex;
    }

    public boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        isCorrectAnswer = correctAnswer;
    }
}

