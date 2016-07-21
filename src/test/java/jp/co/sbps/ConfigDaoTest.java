
package jp.co.sbps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sbps.dao.ConfigDao;
import jp.co.sbps.dao.entity.Config;
import jp.co.sbps.dao.entity.Neuron;

/*
 * ConfigDaoが適切に動作しているかを確認するプログラム
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrainScopeApplication.class)
@Transactional
public class ConfigDaoTest {
	
	@Autowired
	private ConfigDao configDao;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Before
	public void setup() {
		// neuronテーブルの初期化
		jdbc.update("DELETE FROM neuron");
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, false, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, false, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 3, false, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 3, false, '2016-04-01', '2016-04-01')");
		
		// tree_diagramテーブルの初期化
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 1)");
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 2)");
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 3)");
		jdbc.update("INSERT INTO tree_diagram VALUES (1, 4)");
		jdbc.update("INSERT INTO tree_diagram VALUES (2, 2)");
		jdbc.update("INSERT INTO tree_diagram VALUES (2, 3)");
		jdbc.update("INSERT INTO tree_diagram VALUES (2, 4)");
		jdbc.update("INSERT INTO tree_diagram VALUES (3, 3)");
		jdbc.update("INSERT INTO tree_diagram VALUES (4, 4)");
		
		// configテーブルの初期化
		jdbc.update("DELETE FROM config");
		jdbc.update("INSERT INTO config VALUES (1, 2)");
	}
	
	@Test
	public void moveUp_スコープアドレスが移動していることを確認する() {
		// SetUp
		Neuron setup = new Neuron();
		setup.setId(2);
		setup.setNeuronLevel(2);
		
		// Exercise
		configDao.moveUp(setup);
		List<Config> config = jdbc.query("SELECT * FROM config",
				new BeanPropertyRowMapper<>(Config.class));
		
		// Verify
		assertThat(config.get(0).getScopeAddress(), is(1));
	}
	
	@Test
	public void moveDown_スコープアドレスが移動していることを確認する() {
		// SetUp
		Neuron setup = new Neuron();
		setup.setId(2);
		
		// Exercise
		configDao.moveDown(setup);
		List<Config> config = jdbc.query("SELECT * FROM config",
				new BeanPropertyRowMapper<>(Config.class));
		
		// Verify
		assertThat(config.get(0).getScopeAddress(), is(2));
	}
	
	@Test
	public void returnConfig_コンフィグを返すことを確認する() {
		// SetUp
		
		// Exercise
		Config config = configDao.returnConfig();
		
		// Verify
		assertThat(config.getScopeAddress(), is(1));
		assertThat(config.getScopeSize(), is(2));
	}
}