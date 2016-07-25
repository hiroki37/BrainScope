package jp.co.sbps;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import jp.co.sbps.dao.NeuronDao;
import jp.co.sbps.entity.Neuron;

/*
 * NeuronDaoが適切に動作しているかを確認するプログラム
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrainScopeApplication.class)
@Transactional
public class NeuronDaoTest {
	
	@Autowired
	private NeuronDao neuronDao;
	
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
	public void presentNeuron_ニューロンを返すことを確認する() {
		// Setup
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer id = 1;
		
		// Exercise
		Neuron neuron = neuronDao.returnNeuron(id);
		
		// Verify
		assertThat(neuron.getId(), is(1));
		assertThat(neuron.getTitle(), is("ニューロン１"));
		assertThat(neuron.getContent(), is("コンテンツ１"));
		assertThat(neuron.getNeuronLevel(), is(1));
		assertThat(neuron.getActive(), is(false));
		assertThat(format.format(neuron.getCreateDate().getTime()), is("2016-04-01 00:00:00"));
		assertThat(format.format(neuron.getUpdateDate().getTime()), is("2016-04-01 00:00:00"));
	}
	
	@Test
	public void returnAllNeuronList_すべてのニューロンを返すことを確認する() {
		// Setup
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// Exercise
		List<Neuron> neuron = neuronDao.returnNeuronList();
		
		// Verify
		assertThat(neuron.get(0).getId(), is(1));
		assertThat(neuron.get(0).getTitle(), is("ニューロン１"));
		assertThat(neuron.get(0).getContent(), is("コンテンツ１"));
		assertThat(neuron.get(0).getNeuronLevel(), is(1));
		assertThat(neuron.get(0).getActive(), is(false));
		assertThat(format.format(neuron.get(0).getCreateDate().getTime()), is("2016-04-01 00:00:00"));
		assertThat(format.format(neuron.get(0).getUpdateDate().getTime()), is("2016-04-01 00:00:00"));
		
		assertThat(neuron.get(1).getId(), is(2));
		assertThat(neuron.get(1).getTitle(), is("ニューロン２"));
		assertThat(neuron.get(1).getContent(), is("コンテンツ２"));
		assertThat(neuron.get(1).getNeuronLevel(), is(2));
		assertThat(neuron.get(1).getActive(), is(false));
		assertThat(format.format(neuron.get(1).getCreateDate().getTime()), is("2016-04-01 00:00:00"));
		assertThat(format.format(neuron.get(1).getUpdateDate().getTime()), is("2016-04-01 00:00:00"));
		
		assertThat(neuron.get(2).getId(), is(3));
		assertThat(neuron.get(2).getTitle(), is("ニューロン３"));
		assertThat(neuron.get(2).getContent(), is("コンテンツ３"));
		assertThat(neuron.get(2).getNeuronLevel(), is(3));
		assertThat(neuron.get(2).getActive(), is(false));
		assertThat(format.format(neuron.get(2).getCreateDate().getTime()), is("2016-04-01 00:00:00"));
		assertThat(format.format(neuron.get(2).getUpdateDate().getTime()), is("2016-04-01 00:00:00"));
		
		assertThat(neuron.get(3).getId(), is(4));
		assertThat(neuron.get(3).getTitle(), is("ニューロン４"));
		assertThat(neuron.get(3).getContent(), is("コンテンツ４"));
		assertThat(neuron.get(3).getNeuronLevel(), is(3));
		assertThat(neuron.get(3).getActive(), is(false));
		assertThat(format.format(neuron.get(3).getCreateDate().getTime()), is("2016-04-01 00:00:00"));
		assertThat(format.format(neuron.get(3).getUpdateDate().getTime()), is("2016-04-01 00:00:00"));
	}
	
	@Test
	public void updateNeuron_ニューロンが更新されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(1);
		setup.setTitle("更新されたタイトル");
		setup.setContent("更新されたコンテンツ");
		
		// Exercise
		neuronDao.updateNeuron(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE id = ?",
				new BeanPropertyRowMapper<>(Neuron.class), 1);
		
		// Verify
		assertThat(neuron.get(0).getTitle(), is("更新されたタイトル"));
		assertThat(neuron.get(0).getContent(), is("更新されたコンテンツ"));
	}
	
	@Test
	public void generateNeuron_ニューロンが生成されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(1);
		setup.setNeuronLevel(1);
		
		// Exercise
		neuronDao.generateNeuron(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE title = '新しいニューロン'",
				new BeanPropertyRowMapper<>(Neuron.class));
		
		// Verify
		assertThat(neuron.get(0).getTitle(), is("新しいニューロン"));
		assertThat(neuron.get(0).getContent(), is(""));
		assertThat(neuron.get(0).getNeuronLevel(), is(2));
	}
	
	@Test
	public void extinctNeuron_ニューロンが削除されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(1);
		
		// Exercise
		neuronDao.extinctNeuron(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE id = ?",
				new BeanPropertyRowMapper<>(Neuron.class), 1);
		
		// Verify
		assertThat(neuron, is(empty()));
	}
	
	@Test
	public void insertNeuron_ニューロンが挿入されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(2);
		setup.setNeuronLevel(2);
		
		// Exercise
		neuronDao.insertNeuron(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE title = '挿入されたニューロン'",
				new BeanPropertyRowMapper<>(Neuron.class));
		
		// Verify
		assertThat(neuron.get(0).getTitle(), is("挿入されたニューロン"));
		assertThat(neuron.get(0).getContent(), is(""));
		assertThat(neuron.get(0).getNeuronLevel(), is(2));
	}
	
	@Test
	public void insertNeuronLevel_ニューロンレベルが調整されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(2);
		
		// Exercise
		neuronDao.insertNeuronLevel(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE id >= 2",
				new BeanPropertyRowMapper<>(Neuron.class));
		
		// Verify
		assertThat(neuron.get(0).getNeuronLevel(), is(3));
		assertThat(neuron.get(1).getNeuronLevel(), is(4));
		assertThat(neuron.get(2).getNeuronLevel(), is(4));
	}
	
	@Test
	public void activateNeuron_ニューロンが活性化されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(1);
		
		// Exercise
		neuronDao.activateNeuron(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE id = 1",
				new BeanPropertyRowMapper<>(Neuron.class));
		
		// Verify
		assertThat(neuron.get(0).getActive(), is(true));
	}
	
	@Test
	public void activateNeuron_ニューロンが非活性化されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(1);
		jdbc.update("UPDATE neuron SET active = true");
		
		// Exercise
		neuronDao.activateNeuron(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE id = 1",
				new BeanPropertyRowMapper<>(Neuron.class));
		
		// Verify
		assertThat(neuron.get(0).getActive(), is(false));
	}
	
	@Test
	public void parentNeuron_親ニューロンが出力されることを確認する() {
		// Setup
		Neuron setup = new Neuron();
		setup.setId(2);
		setup.setNeuronLevel(2);
		
		// Exercise
		neuronDao.parentNeuron(setup);
		List<Neuron> neuron = jdbc.query("SELECT * FROM neuron WHERE id = 1",
				new BeanPropertyRowMapper<>(Neuron.class));
		
		// Verify
		assertThat(neuron.get(0).getId(), is(1));
	}
	
	@Test
	public void yougestNeuron_最も新しいニューロンが出力されることを確認する() {
		// Setup
		Neuron neuron = new Neuron();
		
		// Exercise
		neuron = neuronDao.youngestNeuron();
		
		// Verify
		assertThat(neuron.getId(), is(4));
	}
	
	@Test
	public void hasNeuron_指定のニューロンがあるかどうかを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(1);
		
		// Exercise
		Boolean actual = neuronDao.hasNeuron(neuron);
		
		// Verify
		assertThat(actual, is(true));
	}
}