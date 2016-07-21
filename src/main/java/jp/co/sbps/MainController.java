package jp.co.sbps;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sbps.dao.ConfigDao;
import jp.co.sbps.dao.NeuronDao;
import jp.co.sbps.dao.TreeDiagramDao;
import jp.co.sbps.dao.entity.Neuron;
import jp.co.sbps.form.FlagForm;

@Controller
public class MainController {

	@Autowired
	NeuronDao neuronDao;
	
	@Autowired
	TreeDiagramDao treeDiagramDao;
	
	@Autowired
	ConfigDao configDao;
	
	/** logger **/
	// private final static Logger log = LoggerFactory.getLogger(MainController.class);
	
	@RequestMapping("brainscope")
	public String brainScope(Neuron neuron, FlagForm flagForm, Model model) {
		
		/*
		// スコープアドレスの移動（上り）
		if (neuron.getId() != null && neuronDao.neuronLevel(neuron.getId()) - 1 > 0 && flagForm.getMoveUpFlag() != null) {
			configDao.moveUp(neuron);
		}
		*/
		
		// スコープアドレスの移動（上り）
		if (neuron.getId() != null && neuron.getNeuronLevel() - 1 > 0 && flagForm.getMoveUpFlag() != null) {
			configDao.moveUp(neuron);
		}
		
		// スコープアドレスの移動（下り）
		if (neuron.getId() != null && flagForm.getMoveDownFlag() != null) {
			configDao.moveDown(neuron);
		}
		
		// ニューロンの生成＆木構造の生成
		if (flagForm.getGenerateFlag() != null) {
			neuronDao.generateNeuron(neuron);
			
			treeDiagramDao.generateTreeDiagram(neuron, neuronDao.youngestNeuron());
		}
		
		// ニューロンの削除＆木構造の削除
		if (neuron.getId() != null && flagForm.getExtinctFlag() != null) {
			neuronDao.extinctNeuron(neuron);
			
			treeDiagramDao.extinctTreeDiagram(neuron);
		}
		
		// ニューロンの更新
		if (neuron.getId() != null && flagForm.getUpdateFlag() != null) {
			neuronDao.updateNeuron(neuron);
		}
		
		// ニューロンの挿入＆木構造の挿入＆ニューロンレベルの調整
		if (neuron.getId() != null && flagForm.getInsertFlag() != null) {
			neuronDao.insertNeuron(neuron);
			
			treeDiagramDao.insertTreeDiagram(neuron, neuronDao.youngestNeuron());
			
			neuronDao.insertNeuronLevel(neuron);
		}
		
		// ニューロンの活性化
		if (neuron.getId() != null && flagForm.getActivateFlag() != null) {
			neuronDao.activateNeuron(neuron);
		}
		
		// ニューロンのリストをモデルに代入
		model.addAttribute("neuron", neuronDao.returnNeuronList());
		
		// log.info();
		
		return "brainscope";
	}
	
	@RequestMapping("synapse")
	public String synapse(Neuron neuron, FlagForm flagForm, Model model) {
		
		// ニューロンの活性化
		if (neuron.getId() != null && flagForm.getActivateFlag() != null) {
			neuronDao.activateNeuron(neuron);
		}
		
		// すべてのニューロンのリストをモデルに代入		
		model.addAttribute("neuron", neuronDao.returnAllNeuronList());
		
		// log.info();
		
		return "synapse";
	}
}