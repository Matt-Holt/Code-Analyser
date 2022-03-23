package analyser;

import javafx.scene.Node;

public class CodeSmellNode {
	private Node title;
	private Node desc;
	
	public CodeSmellNode(Node title, Node desc) {
		this.title = title;
		this.desc = desc;
	}

	//@return title node
	public Node getTitle() {
		return title;
	}
	
	//@return desc node
	public Node getDesc() {
		return desc;
	}
}
