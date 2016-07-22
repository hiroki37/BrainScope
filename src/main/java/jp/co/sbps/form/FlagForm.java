package jp.co.sbps.form;

import lombok.Data;

/*
 * フラグフォーム
 */

@Data
public class FlagForm {

	// スコープアドレスの移動（上り）フラグ
	private String moveUpFlag;
	
	// スコープアドレスの移動（下り）フラグ
	private String moveDownFlag;
	
	// ニューロンの生成＆木構造の生成フラグ
	private String generateFlag;
	
	// ニューロンの削除＆木構造の削除フラグ
	private String extinctFlag;
	
	// ニューロンの更新フラグ
	private String updateFlag;
	
	// ニューロンの挿入＆木構造の挿入＆ニューロンレベルの調整フラグ
	private String insertFlag;
	
	// ニューロンの活性化フラグ
	private String activateFlag;
}
