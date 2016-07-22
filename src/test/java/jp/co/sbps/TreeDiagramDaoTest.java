package jp.co.sbps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;

/*
 * TreeDiagramDaoが適切に動作しているかを確認するプログラム
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sbps.dao.TreeDiagramDao;
import jp.co.sbps.entity.Neuron;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrainScopeApplication.class)
@Transactional
public class TreeDiagramDaoTest {
	
	@Autowired
	private TreeDiagramDao treeDiagramDao;
	
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
	public void generateTreeDiagram_木構造が適切に生成されていることを確認する() {
		// SetUp
		Neuron setup = new Neuron();
		setup.setId(1);
		
		Neuron youngestSetup = new Neuron();
		youngestSetup.setId(5);
		
		// Exercise
		treeDiagramDao.generateTreeDiagram(setup, youngestSetup);
		String treeDiagram = String.valueOf(jdbc.queryForList("SELECT * FROM tree_diagram WHERE descendant = 5"));
		
		// Verify
		assertThat(treeDiagram, is("[{ancestor=1, descendant=5}, {ancestor=5, descendant=5}]"));
	}
	
	@Test
	public void extinctTreeDiagram_木構造が適切に削除されていることを確認する() {
		// SetUp
		Neuron setup = new Neuron();
		setup.setId(2);
		
		// Exercise
		treeDiagramDao.extinctTreeDiagram(setup);
		String treeDiagram = String.valueOf(jdbc.queryForList("SELECT * FROM tree_diagram WHERE ancestor = 2"));
		
		// Verify
		assertThat(treeDiagram, is("[]"));
	}
	
	@Test
	public void insertTreeDiagram_木構造が適切に挿入されていることを確認する() {
		// SetUp
		Neuron setup = new Neuron();
		setup.setId(4);
		setup.setNeuronLevel(3);
		
		Neuron youngestSetup = new Neuron();
		youngestSetup.setId(5);
		
		// Exercise
		treeDiagramDao.insertTreeDiagram(setup, youngestSetup);
		String treeDiagram = String.valueOf(jdbc.queryForList("SELECT * FROM tree_diagram WHERE ancestor = 5 OR descendant = 5"));
		
		// Verify
		assertThat(treeDiagram, is("[{ancestor=1, descendant=5}, {ancestor=2, descendant=5}, {ancestor=5, descendant=5}, {ancestor=5, descendant=4}]"));
	}
}
