package jp.co.sbps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sbps.entity.Neuron;
import jp.co.sbps.form.FlagForm;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrainScopeApplication.class)
@Transactional
public class MainControllerTest {
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MainController mainController;
	
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
	
	// 条件節のテストのみ実装
	
	@Test
	public void isFirst_引数のないアクセスであることを判定する() {
		// SetUp
		Neuron neuron = new Neuron();
		
		// Exercise
		Boolean actual = mainController.isFirst(neuron);
		
		// Verify
		assertThat(actual, is(true));
	}
	
	@Test
	public void isMoveUp_スコープアドレスの移動上りのアクセスであることを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(2);
		neuron.setNeuronLevel(2);
		
		FlagForm flagForm = new FlagForm();
		flagForm.setMoveUpFlag("1");
		
		// Exercise
		Boolean actual = mainController.isMoveUp(neuron, flagForm);
		
		// Verify
		assertThat(actual, is(true));
	}
	
	@Test
	public void isMoveDown_スコープアドレスの移動下りのアクセスであることを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(1);
		
		FlagForm flagForm = new FlagForm();
		flagForm.setMoveDownFlag("1");
		
		// Exercise
		Boolean actual = mainController.isMoveDown(neuron, flagForm);
		
		// Verify
		assertThat(actual, is(true));
	}
	
	@Test
	public void isGenerate_ニューロン生成のアクセスであることを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(1);
		
		FlagForm flagForm = new FlagForm();
		flagForm.setGenerateFlag("1");
		
		// Exercise
		Boolean actual = mainController.isGenerate(neuron, flagForm);
		
		// Verify
		assertThat(actual, is(true));
	}
	
	@Test
	public void isExtinct_ニューロン削除のアクセスであることを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(1);
		
		FlagForm flagForm = new FlagForm();
		flagForm.setExtinctFlag("1");
		
		// Exercise
		Boolean actual = mainController.isExtinct(neuron, flagForm);
		
		// Verify
		assertThat(actual, is(true));
	}
	
	@Test
	public void isUpdate_ニューロン更新のアクセスであることを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(1);
		
		FlagForm flagForm = new FlagForm();
		flagForm.setUpdateFlag("1");
		
		// Exercise
		Boolean actual = mainController.isUpdate(neuron, flagForm);
		
		// Verify
		assertThat(actual, is(true));
	}
	
	@Test
	public void isInsert_ニューロン挿入のアクセスであることを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(1);
		
		FlagForm flagForm = new FlagForm();
		flagForm.setInsertFlag("1");
		
		// Exercise
		Boolean actual = mainController.isInsert(neuron, flagForm);
		
		// Verify
		assertThat(actual, is(true));
	}
	
	@Test
	public void isActivate_ニューロン活性化のアクセスであることを判定する() {
		// Setup
		Neuron neuron = new Neuron();
		neuron.setId(1);
		
		FlagForm flagForm = new FlagForm();
		flagForm.setActivateFlag("1");
		
		// Exercise
		Boolean actual = mainController.isActivate(neuron, flagForm);
		
		// Verify
		assertThat(actual, is(true));
	}
}
