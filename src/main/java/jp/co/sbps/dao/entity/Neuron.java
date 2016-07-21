package jp.co.sbps.dao.entity;

import java.util.Calendar;

import lombok.Data;

/*
 * ニューロンのエンティティクラス
 * ニューロンの情報を保持
 */

@Data
public class Neuron {
	
	// ニューロンID
	private Integer id;
	
	// ニューロンタイトル
	private String title;
	
	// ニューロンコンテンツ
	private String content;
	
	// ニューロンレベル
	private Integer neuronLevel;

	// ニューロンの活性/非活性
	private Boolean active;
	
	// ニューロンの生成日時
	private Calendar createDate;
	
	// ニューロンの更新日時
	private Calendar updateDate;
}
