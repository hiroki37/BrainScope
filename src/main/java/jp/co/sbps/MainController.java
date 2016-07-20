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
import jp.co.sbps.form.BrainScopeForm;

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
	public String brainScope(BrainScopeForm brainScopeForm , Model model) {
		
		// スコープアドレスの移動（上り）
		if (brainScopeForm.getId() != null && neuronDao.neuronLevel(brainScopeForm.getId()) - 1 > 0 && brainScopeForm.getMoveUpFlag() != null) {
			configDao.moveUp(brainScopeForm.getId());
		}
		
		// スコープアドレスの移動（下り）
		if (brainScopeForm.getId() != null && brainScopeForm.getMoveDownFlag() != null) {
			configDao.moveDown(brainScopeForm.getId());
		}
		
		// ニューロンの生成＆木構造の生成
		if (brainScopeForm.getGenerateFlag() != null) {
			neuronDao.generateNeuron(brainScopeForm.getId());
			
			treeDiagramDao.generateTreeDiagram(brainScopeForm.getId(), neuronDao.youngestId());
		}
		
		// ニューロンの削除＆木構造の削除
		if (brainScopeForm.getId() != null && brainScopeForm.getExtinctFlag() != null) {
			neuronDao.extinctNeuron(brainScopeForm.getId());
			
			treeDiagramDao.extinctTreeDiagram(brainScopeForm.getId());
		}
		
		// ニューロンの更新
		if (brainScopeForm.getId() != null && brainScopeForm.getUpdateFlag() != null) {
			neuronDao.updateNeuron(brainScopeForm.getId(), brainScopeForm.getTitle(), brainScopeForm.getContent());
		}
		
		// ニューロンの挿入＆木構造の挿入＆ニューロンレベルの調整
		if (brainScopeForm.getId() != null && brainScopeForm.getInsertFlag() != null) {
			neuronDao.insertNeuron(brainScopeForm.getId());
			
			treeDiagramDao.insertTreeDiagram(brainScopeForm.getId(), neuronDao.parentId(brainScopeForm.getId()), neuronDao.youngestId());
			
			neuronDao.insertNeuronLevel(brainScopeForm.getId());
		}
		
		// ニューロンの活性化
		if (brainScopeForm.getId() != null && brainScopeForm.getActivateFlag() != null) {
			neuronDao.activateNeuron(brainScopeForm.getId());
		}
		
		// ニューロンのリストをモデルに代入
		model.addAttribute("neuron", neuronDao.returnNeuron());
		
		// log.info();
		
		return "brainscope";
	}
	
	@RequestMapping("synapse")
	public String synapse(Integer id, String activateFlag, Model model) {
		
		// ニューロンの活性化
		if (id != null && activateFlag != null) {
			neuronDao.activateNeuron(id);
		}
		
		// すべてのニューロンのリストをモデルに代入		
		model.addAttribute("neuron", neuronDao.returnAllNeuron());
		
		// log.info();
		
		return "synapse";
	}
}