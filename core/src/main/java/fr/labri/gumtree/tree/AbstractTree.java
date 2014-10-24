package fr.labri.gumtree.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractTree implements ITree {

	private static final String OPEN_SYMBOL = "[(";
	private static final String CLOSE_SYMBOL = ")]";
	private static final String SEPARATE_SYMBOL = "@@";
	protected int id;
	protected ITree parent;
	protected List<ITree> children;
	protected int height;
	protected int size;
	protected int depth;
	protected int digest;
	protected boolean matched;

	@Override
	public boolean areDescendantsMatched() {
		for (ITree c: getDescendants()) if (!c.isMatched()) return false;
		return true;
	}

	@Override
	public int getChildPosition(ITree child) {
		return getChildren().indexOf(child);
	}
	
	@Override
	public ITree getChild(int position) {
		return getChildren().get(position);
	}
	
	@Override
	public String getChildrenLabels() {
		StringBuffer b = new StringBuffer();
		for (ITree child: getChildren()) if (!"".equals(child.getLabel())) b.append(child.getLabel() + " ");
		return b.toString().trim();
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public List<ITree> getDescendants() {
		List<ITree> trees = TreeUtils.preOrder(this); 
		trees.remove(0);
		return trees;
	}

	@Override
	public int getDigest() {
		return digest;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean hasLabel() {
		return !ITree.NO_LABEL.equals(getLabel());
	}
	
	@Override
	public List<ITree> getLeaves() {
		List<ITree> leafs = new ArrayList<>();
		for (ITree t: getTrees()) if (t.isLeaf()) leafs.add(t);
		return leafs;
	}

	@Override
	public ITree getParent() {
		return parent;
	}
	
	@Override
	public void setParent(ITree parent) {
		this.parent = parent;
	}

	@Override
	public List<ITree> getParents() {
		List<ITree> parents = new ArrayList<>();
		if (getParent() == null) return parents;
		else {
			parents.add(getParent());
			parents.addAll(getParent().getParents());
		}
		return parents;
	}

	@Override
	public String getShortLabel() {
		String lbl = getLabel();
		return lbl.substring(0, Math.min(50, lbl.length()));
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public List<ITree> getTrees() {
		return TreeUtils.preOrder(this);
	}

	private String indent(ITree t) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < t.getDepth(); i++) b.append("\t");
		return b.toString();
	}

	@Override
	public boolean isClone(ITree tree) {
		if (this.getDigest() != tree.getDigest()) return false;
		else {
			boolean res = (this.toDigestTreeString().equals(tree.toDigestTreeString())); 
			return res;
		}
	}

	@Override
	public boolean isCompatible(ITree t) {
		return getType() == t.getType();
	}

	@Override
	public boolean isLeaf() {
		return getChildren().size() == 0;
	}

	@Override
	public boolean isMatchable(ITree t) {
		return isCompatible(t) && !(isMatched()  || t.isMatched());
	}

	@Override
	public boolean isMatched() {
		return matched;
	}

	@Override
	public boolean isRoot() {
		return getParent() == null;
	}

	@Override
	public boolean isSimilar(ITree t) {
		if (!isCompatible(t)) return false;
		else if (!getLabel().equals(t.getLabel())) return false;
		return true;
	}

	@Override
	public Iterable<ITree> postOrder() {
		return new Iterable<ITree>() {
			@Override
			public Iterator<ITree> iterator() {
				return TreeUtils.postOrderIterator(AbstractTree.this);
			}
		};
	}

	@Override
	public Iterable<ITree> breadthFirst() {
		return new Iterable<ITree>() {
			@Override
			public Iterator<ITree> iterator() {
				return TreeUtils.breadthFirstIterator(AbstractTree.this);
			}
		};
	}

	@Override
	public int positionInParent() {
		ITree p = getParent();
		if (p == null)
			return -1;
		else
			return p.getChildren().indexOf(this);
	}

	@Override
	public void refresh() {
		TreeUtils.computeSize(this);
		TreeUtils.computeDepth(this);
		TreeUtils.computeHeight(this);
		TreeUtils.computeDigest(this);
	}

	@Override
	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public void setDigest(int digest) {
		this.digest = digest;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	@Override
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toDigestString() {
		return getLabel() + SEPARATE_SYMBOL + getType();
	}

	@Override
	public String toDigestTreeString() {
		StringBuffer b = new StringBuffer();
		b.append(OPEN_SYMBOL);
		b.append(this.toDigestString());
		for (ITree c: this.getChildren()) b.append(c.toDigestTreeString());
		b.append(CLOSE_SYMBOL);
		return b.toString();
	}

	@Override
	public String toString() {
		throw new RuntimeException("This method should currently not be used");
	}
	
	@Override
	public String toShortString() {
		return String.format("%d:%s", getType(), getLabel());
	}

	@Override
	public String toTreeString() {
		StringBuffer b = new StringBuffer();
		for (ITree t : TreeUtils.preOrder(this)) b.append(indent(t) + t.toShortString() + "\n");
		return b.toString();
	}

//	static int getType(Class<?> generator, int type) {
//		
//	}

	@Override
	public String toPrettyString(TreeContext ctx) {
		if (hasLabel()) {
			return ctx.getTypeLabel(this) + ": " + getLabel();
		} else {
			return ctx.getTypeLabel(this);
		}
	}
	
	public static class FakeTree extends AbstractTree {
		public FakeTree(ITree... trees) {
			children = new ArrayList<ITree>(trees.length);
			children.addAll(Arrays.asList(trees));
		}

		private RuntimeException unsupportedOperation() {
			return new UnsupportedOperationException("This method should not be called on a fake tree");
		}
		
		@Override
		public void addChild(ITree t) {
			throw unsupportedOperation();
		}

		@Override
		public ITree deepCopy() {
			throw unsupportedOperation();
		}

		@Override
		public List<ITree> getChildren() {
			return children;
		}

		@Override
		public int getEndPos() {
			throw unsupportedOperation();
		}

		@Override
		public String getLabel() {
			return Tree.NO_LABEL;
		}

		@Override
		public int[] getLcPosEnd() {
			throw unsupportedOperation();
		}

		@Override
		public int[] getLcPosStart() {
			throw unsupportedOperation();
		}

		@Override
		public int getLength() {
			throw unsupportedOperation();
		}

		@Override
		public int getPos() {
			throw unsupportedOperation();
		}

		@Override
		public Object getTmpData() {
			throw unsupportedOperation();
		}

		@Override
		public int getType() {
			return -1;
		}

		@Override
		public void setChildren(List<ITree> children) {
			throw unsupportedOperation();
		}

		@Override
		public void setLabel(String label) {
			throw unsupportedOperation();
		}

		@Override
		public void setLcPosEnd(int[] lcPosEnd) {
			throw unsupportedOperation();
		}

		@Override
		public void setLcPosStart(int[] lcPosStart) {
			throw unsupportedOperation();
		}

		@Override
		public void setLength(int length) {
			throw unsupportedOperation();
		}

		@Override
		public void setParentAndUpdateChildren(ITree parent) {
			throw unsupportedOperation();
		}

		@Override
		public void setPos(int pos) {
			throw unsupportedOperation();
		}

		@Override
		public void setTmpData(Object tmpData) {
			throw unsupportedOperation();
		}

		@Override
		public void setType(int type) {
			throw unsupportedOperation();
		}

		@Override
		public String toPrettyString(TreeContext ctx) {
			return "FakeTree";
		}
	}
}