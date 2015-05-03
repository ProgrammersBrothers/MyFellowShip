package com.mygame.myfellowship;

import java.util.List;

public class Question {
	private String question;
	private String quesionId; 
	private List<String> answers;
	private List<String> answerstype;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getQuesionId() {
		return quesionId;
	}
	public void setQuesionId(String quesionId) {
		this.quesionId = quesionId;
	}
	public List<String> getAnswers() {
		return answers;
	}
	public void setAnswers(List<String> questions) {
		this.answers = questions;
	}
	public List<String> getAnswerstype() {
		return answerstype;
	}
	public void setAnswerstype(List<String> answerstype) {
		this.answerstype = answerstype;
	}
}
