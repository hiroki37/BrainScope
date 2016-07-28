package jp.co.sbps.entity;

import lombok.Data;

/**
 * コンフィグのエンティティクラス
 * コンフィグの情報を保持
 */

@Data
public class Config {
	// スコープアドレス
	private Integer scopeAddress;
	
	// スコープサイズ
	private Integer scopeSize;
}
