package jp.co.sbps.dao.entity;

import java.util.Calendar;

/*
 * ニューロンのエンティティクラス
 * ニューロンの情報を保持
 */

public class Neuron {
	
	// ニューロンID
	private Integer id;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	// ニューロンタイトル
	private String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	// ニューロンコンテンツ
	private String content;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	// ニューロンレベル
	private Integer neuronLevel;
	
	public Integer getNeuronLevel() {
		return neuronLevel;
	}

	public void setNeuronLevel(Integer neuronLevel) {
		this.neuronLevel = neuronLevel;
	}

	// ニューロンの活性/非活性
	private Boolean active;
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;	}
	
	// ニューロンの生成日時
	private Calendar createDate;
	
	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}
	
	// ニューロンの更新日時
	private Calendar updateDate;

	public Calendar getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Calendar updateDate) {
		this.updateDate = updateDate;
	}
}
