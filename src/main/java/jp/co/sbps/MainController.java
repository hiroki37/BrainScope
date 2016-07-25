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
	
	// @Valueが冗長なので美しくする手法を考える
	
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
		
		// スコープアドレスの移動（上り）
		if (neuron.getId() != null && neuron.getNeuronLevel() - 1 > 0 && flagForm.getMoveUpFlag() != null) {
			long start = System.currentTimeMillis();
			configDao.moveUp(neuron);
			long end = System.currentTimeMillis();
			log.info(logMoveUp,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getMoveUpFlag());
		}
		
		// スコープアドレスの移動（下り）
		else if (neuron.getId() != null && flagForm.getMoveDownFlag() != null) {
			long start = System.currentTimeMillis();
			configDao.moveDown(neuron);
			long end = System.currentTimeMillis();
			log.info(logMoveDown,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getMoveDownFlag());
		}
		
		// ニューロンの生成＆木構造の生成
		else if (flagForm.getGenerateFlag() != null) {
			long start = System.currentTimeMillis();
			neuronDao.generateNeuron(neuron);
			long end = System.currentTimeMillis();
			log.info(logGenerateNeuron,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getGenerateFlag());
			
			start = System.currentTimeMillis();
			treeDiagramDao.generateTreeDiagram(neuron, neuronDao.youngestNeuron());
			end = System.currentTimeMillis();
			log.info(logGenerateTree,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getGenerateFlag());
		}
		
		// ニューロンの削除＆木構造の削除
		else if (neuron.getId() != null && flagForm.getExtinctFlag() != null) {
			long start = System.currentTimeMillis();
			neuronDao.extinctNeuron(neuron);
			long end = System.currentTimeMillis();
			log.info(logExtinctNeuron,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getExtinctFlag());
			
			start = System.currentTimeMillis();
			treeDiagramDao.extinctTreeDiagram(neuron);
			end = System.currentTimeMillis();
			log.info(logExtinctTree,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getExtinctFlag());
		}
		
		// ニューロンの更新
		else if (neuron.getId() != null && flagForm.getUpdateFlag() != null) {
			long start = System.currentTimeMillis();
			neuronDao.updateNeuron(neuron);
			long end = System.currentTimeMillis();
			log.info(logUpdate,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getUpdateFlag());
		}
		
		// ニューロンの挿入＆木構造の挿入＆ニューロンレベルの調整
		else if (neuron.getId() != null && flagForm.getInsertFlag() != null) {
			long start = System.currentTimeMillis();
			neuronDao.insertNeuron(neuron);
			long end = System.currentTimeMillis();
			log.info(logInsertNeuron,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getInsertFlag());
			
			start = System.currentTimeMillis();
			treeDiagramDao.insertTreeDiagram(neuron, neuronDao.youngestNeuron());
			end = System.currentTimeMillis();
			log.info(logInsertTree,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getInsertFlag());
			
			start = System.currentTimeMillis();
			neuronDao.insertNeuronLevel(neuron);
			end = System.currentTimeMillis();
			log.info(logInsertNeuronLevel,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getInsertFlag());
		}
		
		// ニューロンの活性化
		else if (neuron.getId() != null && flagForm.getActivateFlag() != null) {
			long start = System.currentTimeMillis();
			neuronDao.activateNeuron(neuron);
			long end = System.currentTimeMillis();
			log.info(logActivate,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getActivateFlag());
		}
		
		// ニューロンのリストをモデルに代入
		long start = System.currentTimeMillis();
		model.addAttribute("parentNeuron", neuronDao.returnNeuron(configDao.returnConfig().getScopeAddress()));
		model.addAttribute("neuron", neuronDao.returnNeuronList());
		long end = System.currentTimeMillis();
		
		if (neuron.getId() == null) {
			log.info(logBrainScope);
			log.info(logReturnNeuron, end-start);
		}
		
		return "brainscope";
	}
	
	@RequestMapping("synapse")
	public String synapse(Neuron neuron, FlagForm flagForm, Model model) {
		
		// ニューロンの活性化
		if (neuron.getId() != null && flagForm.getActivateFlag() != null) {
			long start = System.currentTimeMillis();
			neuronDao.activateNeuron(neuron);
			long end = System.currentTimeMillis();
			log.info(logActivate,
					end-start, neuron.getId(), neuron.getNeuronLevel(), flagForm.getActivateFlag());
		}
		
		// すべてのニューロンのリストをモデルに代入
		long start = System.currentTimeMillis();
		model.addAttribute("neuron", neuronDao.returnNeuronList());
		long end = System.currentTimeMillis();
		
		if (neuron.getId() == null) {
			log.info(logSynapse);
			log.info(logReturnSynapse, end-start);
		}
		
		return "synapse";
	}
}