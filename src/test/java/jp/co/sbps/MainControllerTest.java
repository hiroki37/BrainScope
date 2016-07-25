package jp.co.sbps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sbps.entity.Neuron;
import jp.co.sbps.form.FlagForm;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrainScopeApplication.class)
@Transactional
public class MainControllerTest {
	
	@Autowired
	private MainController mainController;
	
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
