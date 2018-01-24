
import javax.swing.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Stack;

public class Homework1 extends JPanel
		implements TreeSelectionListener {

	private JEditorPane htmlPane;
	private JTree tree;

	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";

	public static Node root;

	public Homework1() {
		super(new GridLayout(1,0));

		DefaultMutableTreeNode top =
				new DefaultMutableTreeNode(root);
		createNodes(top,root);

		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode
				(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		JScrollPane treeView = new JScrollPane(tree);

		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(500, 300));

		ImageIcon leafIcon = createImageIcon("middle.gif");
		if (leafIcon != null) {
			DefaultTreeCellRenderer renderer =
					new DefaultTreeCellRenderer();
			renderer.setClosedIcon(leafIcon);
			renderer.setOpenIcon(leafIcon);
			tree.setCellRenderer(renderer);
		}

		add(splitPane);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		StringBuffer syntex ;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				tree.getLastSelectedPathComponent();
		Object nodeInfo = node.getUserObject();
		if (node == null) return;
		if(node.isLeaf()){
			htmlPane.setText(nodeInfo.toString());
		}else{
			syntex = getvalue((Node) nodeInfo);
			htmlPane.setText(String.valueOf(syntex));
		}
	}


	static public StringBuffer getvalue(Node n){
		StringBuffer x = new StringBuffer(infix(n));
		x.deleteCharAt(0);
		x.deleteCharAt(x.length() - 1);
		x.append("=");
		x.append(calculate(n));
	return x;
	}

	private void createNodes(DefaultMutableTreeNode top ,Node n) {
		if(n.left!=null)
		{
			DefaultMutableTreeNode Right=new DefaultMutableTreeNode(n.left);
			top.add(Right);
			createNodes(Right,n.left);
		}
		if(n.right!=null)
		{
			DefaultMutableTreeNode left=new DefaultMutableTreeNode(n.right);
			top.add(left);
			createNodes(left,n.right);
		}
	}

	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Homework1.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.out.println("Couldn't find file!!!");
			return null;
		}
	}

	private static void createAndShowGUI() {

		JFrame frame = new JFrame("Binary Tree Calculator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Homework1 newContentPane = new Homework1();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
	}

	static public void main(String[] args) {
		try {
			root = new Node(args[0]);
			root = Tostack(root);
			StringBuffer x = new StringBuffer(infix(root));
			x.deleteCharAt(0);
			x.deleteCharAt(x.length() - 1);
			System.out.print(x);
			System.out.println("=" + calculate(root));
			//System.out.print(inorder(current));
		//	System.out.println(inorder(current));
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
			});
		}catch (Exception e){
			System.out.print("Invalid Input Try Again ..");
		}
	}

	static public Node Tostack(Node n){
		Stack<Character> stack = new Stack<>();
		for(int i=0 ; i<n.sr.length();i++){
			stack.push(n.sr.charAt(i));
		}
		Node root = new Node(' ');
		n = Maketree(stack,n,root);

		return n;
	}
	static public String infix(Node n){
		if(isOperator(n.ar)){
			return ("("+infix(n.left) + n.ar + infix(n.right)+")");
		}else{
			return (n.ar+"");
		}
	}

	static public String inorder(Node n){
		if( n.left != null && n.right != null){
			return (n.ar +" " + inorder(n.right) + " " + inorder(n.left)) ;
		}else return (n.ar+"");
	}

	static public int calculate(Node n){
		if(isOperator(n.ar)){
			switch(n.ar){
				case '+': return calculate(n.left)+calculate(n.right);
				case '-': return calculate(n.left)-calculate(n.right);
				case '*': return calculate(n.left)*calculate(n.right);
				case '/': return calculate(n.left)/calculate(n.right);
			}
		}else{
			return n.ar-'0';
		}
		return  n.fl;
	}

	static public Node Maketree(Stack<Character> stack,Node n,Node root){

		Node current = n;
		if(stack.empty()){
			return n;
		}
		if(isOperator(stack.peek())){
			if(root.ar == ' ' ){
				current.ar = stack.pop();
				root.ar = current.ar;
				current.parent = null;
				Maketree(stack,current,root);
			}else{
				if(current.right == null){
					current.right = new Node(stack.pop());
					current.right.parent = current;
					Maketree(stack,current.right,root);
				}else if(current.left == null){
					current.left = new Node(stack.pop());
					current.left.parent = current;
					Maketree(stack,current.left,root);
				}
			}
		}else{
			if(current.right == null){
				current.right = new Node(stack.pop());
				current.right.parent = current;
				Maketree(stack,current,root);
			}else if(current.left == null){
				current.left = new Node(stack.pop());
				current.left.parent = current;
				Maketree(stack,current.parent,root);
			}else{
				current.parent.left = new Node(stack.pop());
				current.parent.left.parent = current.parent;
				Maketree(stack,current.parent.parent,root);
			}
		}
		return  n;
	}

	static public class Node{
		int fl;
		char ar;
		String sr;
		Node right,left,parent;

		public Node(char e) {
			this.ar = e;
		}
		public Node(String k){
			this.sr = k;
		}
		public String toString(){
			return ar+"";
		}
	}

	static public boolean isOperator(Character ch){
		switch(ch){
			case '+': return true;
			case '-': return true;
			case '*': return true;
			case '/': return true;
			default: return false;
		}
	}
}

