package jp.co.sbps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/*
 * configDaoが適切に動作しているかを確認するプログラム
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
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, 1, 1024, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, 200, 800, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 2, 900, 1000, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 3, 300, 400, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (5, 'ニューロン５', 'コンテンツ５', 3, 500, 600, '2016-04-01', '2016-04-01')");
		
		// configテーブルの初期化
		jdbc.update("DELETE FROM config");
		jdbc.update("INSERT INTO config VALUES (1, 2)");
	}
	
	@Test
	public void moveUp_スコープアドレスが適切に移動していることを確認する() {
		// SetUp
		Integer id = 2;
		jdbc.update("UPDATE config SET scope_address=?", id);
		
		Integer expected = 1;
		
		// Exercise
		configDao.moveUp(id);
		Integer actual = jdbc.queryForObject("SELECT scope_address FROM config", Integer.class);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void moveDown_スコープアドレスが適切に移動していることを確認する() {
		// SetUp
		Integer id = 2;
		
		Integer expected = 2;
		
		// Exercise
		configDao.moveDown(id);
		Integer actual = jdbc.queryForObject("SELECT scope_address FROM config", Integer.class);
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void scopeAddress_現在のスコープアドレスが正しく取得できることを確認する() {
		// SetUp
		Integer expected = 1;
		
		// Exercise
		Integer actual = configDao.scopeAddress();
		
		// Verify
		assertThat(actual, is(expected));
	}

}