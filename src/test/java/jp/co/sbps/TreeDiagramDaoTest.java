package jp.co.sbps;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

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
		jdbc.update("INSERT INTO neuron VALUES (1, 'ニューロン１', 'コンテンツ１', 1, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (2, 'ニューロン２', 'コンテンツ２', 2, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (3, 'ニューロン３', 'コンテンツ３', 3, '2016-04-01', '2016-04-01')");
		jdbc.update("INSERT INTO neuron VALUES (4, 'ニューロン４', 'コンテンツ４', 3, '2016-04-01', '2016-04-01')");
		
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
		Integer id = 1;
		Integer youngestId = 5;
		
		String expected = "[{ancestor=1, descendant=5}, {ancestor=5, descendant=5}]";
		
		// Exercise
		treeDiagramDao.generateTreeDiagram(id, youngestId);
		
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM tree_diagram WHERE descendant = 5"));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void extinctTreeDiagram_木構造が適切に削除されていることを確認する() {
		// SetUp
		Integer id = 2;
		
		String expected = "[]";
		
		// Exercise
		treeDiagramDao.extinctTreeDiagram(id);
		
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM tree_diagram "
				+ "WHERE descendant IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", id));
		
		// Verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void insertTreeDiagram_木構造が適切に挿入されていることを確認する() {
		// SetUp
		Integer id = 4;
		Integer parentId = 2;
		Integer youngestId = 5;
		
		String expected = "[{ancestor=4, descendant=4}, {ancestor=5, descendant=5}, {ancestor=5, descendant=4}]";
		
		// Exercise
		treeDiagramDao.insertTreeDiagram(id, parentId, youngestId);
		
		String actual = String.valueOf(jdbc.queryForList("SELECT * FROM tree_diagram "
				+ "WHERE ancestor IN (SELECT descendant FROM tree_diagram WHERE ancestor = ?)", youngestId));
		
		// Verify
		assertThat(actual, is(expected));
	}
}
