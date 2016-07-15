package jp.co.sbps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/*
 * configテーブルへのアクセスをするDaoクラス
*/

@Component
public class ConfigDao {
	
	@Autowired
	NeuronDao neuronDao;

	@Autowired
	private JdbcTemplate jdbc;

	// スコープアドレスの移動（上り）
	public void moveUp(Integer id) {
		jdbc.update("UPDATE config SET scope_address = (SELECT id FROM neuron WHERE left_edge = ?)",
				neuronDao.parentLeftEdge(id));
	}

	// スコープアドレスの移動（下り）
	public void moveDown(Integer id) {
		jdbc.update("UPDATE config SET scope_address = ?", id);
	}

	// ＝＝＝＝＝SQLパーツ＝＝＝＝＝

	//現在のスコープアドレス
	public Integer scopeAddress() { 
		return jdbc.queryForObject("SELECT scope_address FROM config", Integer.class);
	}
}