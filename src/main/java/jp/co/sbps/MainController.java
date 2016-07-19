package jp.co.sbps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	@Autowired
	NeuronDao neuronDao;
	
	@Autowired
	TreeDiagramDao treeDiagramDao;

	@Autowired
	ConfigDao configDao;
	
	@RequestMapping("brainscope")
	public String brainscope(Integer id, String title, String content, String moveUpFlag, String moveDownFlag,
			String updateFlag, String generateFlag, String extinctFlag, String insertFlag, Model model) {

		// スコープアドレスの移動（上り）
		if (id != null && neuronDao.neuronLevel(id) - 1 > 0 && moveUpFlag != null) {
			configDao.moveUp(id);
		}

		// スコープアドレスの移動（下り）
		if (id != null && moveDownFlag != null) {
			configDao.moveDown(id);
		}

		// ニューロンの生成＆木構造の生成
		if (generateFlag != null) {
			neuronDao.generate(id);
			
			treeDiagramDao.generateTreeDiagram(id, neuronDao.youngestId());
		}

		// ニューロンの削除＆木構造の削除
		if (id != null && extinctFlag != null) {
			neuronDao.extinct(id);
			
			treeDiagramDao.extinctTreeDiagram(id);
		}

		// ニューロンの更新
		if (id != null && updateFlag != null) {
			neuronDao.update(id, title, content);
		}

		// ニューロンの挿入＆木構造の挿入
		if (id != null && insertFlag != null) {
			neuronDao.insert(id);
			
			treeDiagramDao.insertTreeDiagram(id, neuronDao.youngestId());
		}

		// ニューロンのリストをモデルに代入		
		model.addAttribute("brainscope", neuronDao.display());

		return "brainscope";
	}
}