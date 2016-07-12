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
	public void move_up(String id) {
		
		jdbc.update("UPDATE config SET scope_address = (SELECT id FROM neuron WHERE left_edge = ?)",
				neuronDao.parent_left_edge(id));
	}

	// スコープアドレスの移動（下り）
	public void move_down(String id) {
		jdbc.update("UPDATE config SET scope_address = ?", id);
	}

	// ＝＝＝＝＝SQLパーツ＝＝＝＝＝

	//現在のスコープアドレス
	public String scope_address() {
		String scope_address = jdbc.queryForObject("SELECT scope_address FROM config", String.class);
		return scope_address;
	}
}