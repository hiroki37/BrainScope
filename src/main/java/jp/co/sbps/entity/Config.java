package jp.co.sbps.entity;

/*
 * コンフィグのエンティティクラス
 * コンフィグの情報を保持
 */

public class Config {
	// スコープアドレス
	private Integer scopeAddress;
	
	public Integer getScopeAddress() {
		return scopeAddress;
	}

	public void setScopeAddress(Integer scopeAddress) {
		this.scopeAddress = scopeAddress;
	}
	
	// スコープサイズ
	private Integer scopeSize;

	public Integer getScopeSize() {
		return scopeSize;
	}

	public void setScopeSize(Integer scopeSize) {
		this.scopeSize = scopeSize;
	}
}
