package jp.co.sbps.form;

/*
 * BrainScope機能のフォーム
 */

public class BrainScopeForm {
	
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
	
	// スコープアドレスの移動（上り）フラグ
	private String moveUpFlag;
	
	public String getMoveUpFlag() {
		return moveUpFlag;
	}

	public void setMoveUpFlag(String moveUpFlag) {
		this.moveUpFlag = moveUpFlag;
	}
	
	// スコープアドレスの移動（下り）フラグ
	private String moveDownFlag;
	
	public String getMoveDownFlag() {
		return moveDownFlag;
	}

	public void setMoveDownFlag(String moveDownFlag) {
		this.moveDownFlag = moveDownFlag;
	}
	
	// ニューロンの生成＆木構造の生成フラグ
	private String generateFlag;
	
	public String getGenerateFlag() {
		return generateFlag;
	}

	public void setGenerateFlag(String generateFlag) {
		this.generateFlag = generateFlag;
	}
	
	// ニューロンの削除＆木構造の削除フラグ
	private String extinctFlag;
	
	public String getExtinctFlag() {
		return extinctFlag;
	}

	public void setExtinctFlag(String extinctFlag) {
		this.extinctFlag = extinctFlag;
	}
	
	// ニューロンの更新フラグ
	private String updateFlag;
	
	public String getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}
	
	// ニューロンの挿入＆木構造の挿入＆ニューロンレベルの調整フラグ
	private String insertFlag;
	
	public String getInsertFlag() {
		return insertFlag;
	}

	public void setInsertFlag(String insertFlag) {
		this.insertFlag = insertFlag;
	}
	
	// ニューロンの活性化フラグ
	private String activateFlag;

	public String getActivateFlag() {
		return activateFlag;
	}

	public void setActivateFlag(String activateFlag) {
		this.activateFlag = activateFlag;
	}
}
