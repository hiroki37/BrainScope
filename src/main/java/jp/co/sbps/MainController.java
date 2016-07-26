package jp.co.sbps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sbps.dao.ConfigDao;
import jp.co.sbps.dao.NeuronDao;
import jp.co.sbps.dao.TreeDiagramDao;
import jp.co.sbps.entity.Neuron;
import jp.co.sbps.form.FlagForm;

@Controller
public class MainController {

	@Autowired
	NeuronDao neuronDao;
	
	@Autowired
	TreeDiagramDao treeDiagramDao;
	
	@Autowired
	ConfigDao configDao;
	
	@Value("${log.isMinus}")
	String logIsMinus;
	
	@Value("${log.hasNeuron}")
	String logHasNeuron;
	
	@Value("${log.brainScope}")
	String logBrainScope;
	
	@Value("${log.returnNeuron}")
	String logReturnNeuron;
	
	@Value("${log.synapse}")
	String logSynapse;
	
	@Value("${log.returnSynapse}")
	String logReturnSynapse;
	
	@Value("${log.moveUp}")
	String logMoveUp;
	
	@Value("${log.moveDown}")
	String logMoveDown;
	
	@Value("${log.generateNeuron}")
	String logGenerateNeuron;
	
	@Value("${log.generateTree}")
	String logGenerateTree;
	
	@Value("${log.extinctNeuron}")
	String logExtinctNeuron;
	
	@Value("${log.extinctTree}")
	String logExtinctTree;
	
	@Value("${log.update}")
	String logUpdate;
	
	@Value("${log.insertNeuron}")
	String logInsertNeuron;
	
	@Value("${log.insertTree}")
	String logInsertTree;
	
	@Value("${log.insertNeuronLevel}")
	String logInsertNeuronLevel;
	
	@Value("${log.activate}")
	String logActivate;
	
	/** logger **/
	private final static Logger log = LoggerFactory.getLogger(MainController.class);
	
	@RequestMapping("brainscope")
	public String brainScope(Neuron neuron, FlagForm flagForm, Model model) {
		
		// エラーチェック （指定のidが負）
		if (neuron.getId() != null && isMinus(neuron)) {
			log.error(logIsMinus);
			model.addAttribute("isMinus", true);
		}
		
		// エラーチェック (指定のニューロンが存在しない)
		else if(neuron.getId() != null && !neuronDao.hasNeuron(neuron)) {
			log.error(logHasNeuron);
			model.addAttribute("hasNeuron", true);
		}
		
		// スコープアドレスの移動（上り）
		else if (isMoveUp(neuron, flagForm)) {
			log.info(logMoveUp, neuron.getId(), neuron.getNeuronLevel(), flagForm.getMoveUpFlag());	
			configDao.moveUp(neuron);
		}
		
		// スコープアドレスの移動（下り）
		else if (isMoveDown(neuron, flagForm)) {
			log.info(logMoveDown, neuron.getId(), neuron.getNeuronLevel(), flagForm.getMoveDownFlag());
			configDao.moveDown(neuron);
		}
		
		// ニューロンの生成＆木構造の生成
		else if (isGenerate(neuron, flagForm)) {
			log.info(logGenerateNeuron, neuron.getId(), neuron.getNeuronLevel(), flagForm.getGenerateFlag());
			neuronDao.generateNeuron(neuron);
			
			log.info(logGenerateTree, neuron.getId(), neuron.getNeuronLevel(), flagForm.getGenerateFlag());
			treeDiagramDao.generateTreeDiagram(neuron, neuronDao.youngestNeuron());
		}
		
		// ニューロンの削除＆木構造の削除
		else if (isExtinct(neuron, flagForm)) {
			log.info(logExtinctNeuron, neuron.getId(), neuron.getNeuronLevel(), flagForm.getExtinctFlag());
			neuronDao.extinctNeuron(neuron);
			
			log.info(logExtinctTree, neuron.getId(), neuron.getNeuronLevel(), flagForm.getExtinctFlag());
			treeDiagramDao.extinctTreeDiagram(neuron);
		}
		
		// ニューロンの更新
		else if (isUpdate(neuron, flagForm)) {
			log.info(logUpdate, neuron.getId(), neuron.getNeuronLevel(), flagForm.getUpdateFlag());
			neuronDao.updateNeuron(neuron);
		}
		
		// ニューロンの挿入＆木構造の挿入＆ニューロンレベルの調整
		else if (isInsert(neuron, flagForm)) {
			log.info(logInsertNeuron, neuron.getId(), neuron.getNeuronLevel(), flagForm.getInsertFlag());
			neuronDao.insertNeuron(neuron);
			
			log.info(logInsertTree, neuron.getId(), neuron.getNeuronLevel(), flagForm.getInsertFlag());
			treeDiagramDao.insertTreeDiagram(neuron, neuronDao.youngestNeuron());
			
			log.info(logInsertNeuronLevel, neuron.getId(), neuron.getNeuronLevel(), flagForm.getInsertFlag());
			neuronDao.insertNeuronLevel(neuron);
		}
		
		// ニューロンの活性化
		else if (isActivate(neuron, flagForm)) {
			log.info(logActivate, neuron.getId(), neuron.getNeuronLevel(), flagForm.getActivateFlag());
			neuronDao.activateNeuron(neuron);
		}
		
		// ニューロンのリストをモデルに代入
		if (isFirst(neuron)) {
			log.info(logBrainScope);
		}
		
		log.info(logReturnNeuron);
		
		model.addAttribute("parentNeuron", neuronDao.returnNeuron(configDao.returnConfig().getScopeAddress()));
		model.addAttribute("neuron", neuronDao.returnNeuronList());
		
		return "brainscope";
	}
	
	@RequestMapping("synapse")
	public String synapse(Neuron neuron, FlagForm flagForm, Model model) {
		
		// ニューロンの活性化
		if (neuron.getId() != null && flagForm.getActivateFlag() != null) {
			log.info(logActivate, neuron.getId(), neuron.getNeuronLevel(), flagForm.getActivateFlag());
			neuronDao.activateNeuron(neuron);
		}
		
		// ニューロンのリストをモデルに代入
		if (isFirst(neuron)) {
			log.info(logSynapse);
		}
		
		log.info(logReturnSynapse);
		
		model.addAttribute("neuron", neuronDao.returnNeuronList());
		
		return "synapse";
	}
	
	Boolean isFirst(Neuron neuron) {
		if(neuron.getId() == null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isMoveUp(Neuron neuron, FlagForm flagForm) {
		if(neuron.getId() != null && neuron.getNeuronLevel() - 1 > 0 && flagForm.getMoveUpFlag() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isMoveDown(Neuron neuron, FlagForm flagForm) {
		if(neuron.getId() != null && flagForm.getMoveDownFlag() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isGenerate(Neuron neuron, FlagForm flagForm) {
		if(neuron.getId() != null && flagForm.getGenerateFlag() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isExtinct(Neuron neuron, FlagForm flagForm) {
		if(neuron.getId() != null && flagForm.getExtinctFlag() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isUpdate(Neuron neuron, FlagForm flagForm) {
		if(neuron.getId() != null && flagForm.getUpdateFlag() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isInsert(Neuron neuron, FlagForm flagForm) {
		if(neuron.getId() != null && flagForm.getInsertFlag() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isActivate(Neuron neuron, FlagForm flagForm) {
		if(neuron.getId() != null && flagForm.getActivateFlag() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	Boolean isMinus(Neuron neuron) {
		if(neuron.getId() <= 0) {
			return true;
		} else {
			return false;
		}
	}
}