package jp.co.sbps.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jp.co.sbps.dao.entity.Config;
import jp.co.sbps.dao.entity.Neuron;

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
	public void moveUp(Neuron neuron) {
		jdbc.update("UPDATE config SET scope_address = ?", neuronDao.parentNeuron(neuron).getId());
	}
	
	// スコープアドレスの移動（下り）
	public void moveDown(Neuron neuron) {
		jdbc.update("UPDATE config SET scope_address = ?", neuron.getId());
	}
	
	// コンフィグを返す
	public Config returnConfig() { 
		return jdbc.query("SELECT scope_address FROM config", new BeanPropertyRowMapper<>(Config.class)).get(0);
	}
}