package org.gooru.nuclues.search.indexers.app.index.model;

public class QuesionEo {

	private AnswerEo answer;
	private String explanation;
	private HintEo hint;
	private String question;
	private String type;
	public AnswerEo getAnswer() {
		return answer;
	}
	public void setAnswer(AnswerEo answer) {
		this.answer = answer;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public HintEo getHint() {
		return hint;
	}
	public void setHint(HintEo hint) {
		this.hint = hint;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
